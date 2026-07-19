create extension if not exists pg_cron with schema extensions;
create extension if not exists pg_net with schema extensions;

update public.trade_offers
set processing_at = public.next_trade_processing_at(coalesce(responded_at, created_at, now()))
where status = 'accepted'
and processed_at is null
and processing_at is null;

update public.leagues
set invites_open = false
where draft_status <> 'scheduled'
and invites_open = true;

alter table public.rosters
add column if not exists acquired_role text,
add column if not exists acquired_role_source text,
add column if not exists acquired_listeners bigint;

create or replace function public.role_for_listener_count(listener_count bigint)
returns text
language sql
immutable
as $$
    select case
        when listener_count is null then null
        when listener_count >= 40000000 then 'Headliner'
        when listener_count >= 15000000 then 'Mainstay'
        when listener_count >= 1000000 then 'Rising'
        else 'Deep Cut'
    end;
$$;

create or replace function public.set_roster_acquisition_role()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
declare
    observed_listeners bigint;
    observed_source text;
    should_set_role boolean := false;
begin
    if tg_op = 'INSERT' then
        should_set_role := true;
    elsif tg_op = 'UPDATE' then
        should_set_role := old.released_at is not null and new.released_at is null;
    end if;

    if should_set_role then
        select
            coalesce(mac.current_listeners, mac.snapshot_listeners, mac.listeners, artists.listeners),
            case
                when mac.current_listeners is not null then coalesce(mac.current_listeners_source, 'market current listeners')
                when mac.snapshot_listeners is not null then 'market snapshot listeners'
                when mac.listeners is not null then 'market cached listeners'
                else 'artists.listeners'
            end
        into observed_listeners, observed_source
        from public.artists
        left join public.market_artist_cache mac
            on mac.normalized_name = artists.normalized_name
        where artists.id = new.artist_id
        order by
            (mac.current_listeners is not null) desc,
            mac.current_listeners_observed_at desc nulls last,
            mac.updated_at desc nulls last
        limit 1;

        new.acquired_listeners := observed_listeners;
        new.acquired_role := public.role_for_listener_count(observed_listeners);
        new.acquired_role_source := observed_source;
    end if;
    return new;
end;
$$;

drop trigger if exists rosters_set_acquisition_role on public.rosters;
create trigger rosters_set_acquisition_role
before insert or update of released_at, artist_id on public.rosters
for each row
execute function public.set_roster_acquisition_role();

update public.rosters
set acquired_listeners = coalesce(acquired_listeners, artists.listeners),
    acquired_role = coalesce(acquired_role, public.role_for_listener_count(artists.listeners)),
    acquired_role_source = coalesce(acquired_role_source, 'legacy backfill from artists.listeners')
from public.artists
where rosters.artist_id = artists.id
and rosters.released_at is null
and rosters.acquired_role is null;

do $$
begin
    if exists (
        select 1
        from public.league_members
        where status = 'active'
        and role = 'manager'
        group by league_id
        having count(*) > 1
    ) then
        raise notice 'Skipped one-active-manager unique index because duplicate active managers exist. Repair managers explicitly first.';
    else
        create unique index if not exists league_members_one_active_manager_idx
        on public.league_members (league_id)
        where status = 'active' and role = 'manager';
    end if;
end;
$$;

create or replace function public.transfer_league_manager(target_league_id uuid, target_username text)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    target_user_id uuid;
    current_manager_id uuid := auth.uid();
begin
    if current_manager_id is null then
        raise exception 'Authentication required';
    end if;

    if not exists (
        select 1 from public.league_members
        where league_id = target_league_id
        and user_id = current_manager_id
        and role = 'manager'
        and status = 'active'
        for update
    ) then
        raise exception 'Only the current manager can transfer manager role';
    end if;

    select profiles.id into target_user_id
    from public.profiles
    join public.league_members on league_members.user_id = profiles.id
    where league_members.league_id = target_league_id
    and league_members.status = 'active'
    and league_members.role <> 'bot-managed'
    and lower(profiles.username) = lower(trim(target_username))
    limit 1
    for update of league_members;

    if target_user_id is null then
        raise exception 'No active human member with that username';
    end if;

    update public.league_members
    set role = 'member'
    where league_id = target_league_id
    and status = 'active'
    and role = 'manager';

    update public.league_members
    set role = 'manager'
    where league_id = target_league_id
    and user_id = target_user_id
    and status = 'active';

    update public.leagues
    set owner_id = target_user_id
    where id = target_league_id;

    insert into public.league_moderation_actions (league_id, manager_id, target_user_id, action, reason)
    values (target_league_id, current_manager_id, target_user_id, 'manager_transfer', 'Manager role transferred.');
end;
$$;

grant execute on function public.transfer_league_manager(uuid, text) to authenticated;

create or replace function public.process_trade_offer(target_trade_id uuid, force_now boolean default false)
returns text
language plpgsql
security definer
set search_path = public
as $$
declare
    actor uuid := auth.uid();
    actor_role text;
    trade public.trade_offers%rowtype;
    trade_item jsonb;
    target_slot text;
    available_proposer_slots text[];
    available_recipient_slots text[];
    used_proposer_slots text[] := '{}';
    used_recipient_slots text[] := '{}';
    target_artist_id uuid;
begin
    select * into trade
    from public.trade_offers
    where id = target_trade_id
    for update;

    if trade.id is null then
        raise exception 'Trade offer not found';
    end if;
    if trade.status = 'processed' then
        return 'already_processed';
    end if;
    if trade.status <> 'accepted' or trade.processed_at is not null then
        raise exception 'Trade is not pending processing';
    end if;
    if trade.processing_at is null then
        update public.trade_offers
        set processing_at = public.next_trade_processing_at(coalesce(responded_at, created_at, now()))
        where id = trade.id
        returning * into trade;
    end if;

    if force_now then
        select role into actor_role
        from public.league_members
        where league_id = trade.league_id
        and user_id = actor
        and status = 'active'
        limit 1;

        if coalesce(current_setting('request.jwt.claim.role', true), '') <> 'service_role'
           and actor_role <> 'manager' then
            raise exception 'Only the league manager can process this trade early';
        end if;
    elsif trade.processing_at > now() then
        raise exception 'Trade is not ready to process';
    end if;

    for trade_item in select jsonb_array_elements(trade.offered_items || trade.requested_items)
    loop
        target_artist_id := (trade_item->>'artist_id')::uuid;
        if exists (
            select 1
            from public.trade_offers active_offer,
                 jsonb_array_elements(active_offer.offered_items || active_offer.requested_items) active_item
            where active_offer.league_id = trade.league_id
            and active_offer.id <> trade.id
            and active_offer.status = 'accepted'
            and active_offer.processed_at is null
            and active_item->>'artist_id' = target_artist_id::text
        ) then
            raise exception 'An artist in this trade is reserved by another accepted trade';
        end if;
    end loop;

    select coalesce(array_agg(slot), '{}') into available_proposer_slots
    from unnest(public.trade_active_slots(trade.league_id)) as slot
    where slot not in (
        select roster_slot from public.rosters
        where league_id = trade.league_id
        and user_id = trade.proposer_id
        and released_at is null
    );

    select coalesce(array_agg(slot), '{}') into available_recipient_slots
    from unnest(public.trade_active_slots(trade.league_id)) as slot
    where slot not in (
        select roster_slot from public.rosters
        where league_id = trade.league_id
        and user_id = trade.recipient_id
        and released_at is null
    );

    for trade_item in select jsonb_array_elements(trade.offered_items)
    loop
        target_artist_id := (trade_item->>'artist_id')::uuid;
        select roster_slot into target_slot
        from public.rosters
        where league_id = trade.league_id
        and user_id = trade.proposer_id
        and artist_id = target_artist_id
        and released_at is null
        for update;
        if target_slot is null then
            raise exception 'An offered artist is no longer on the original roster';
        end if;
        available_proposer_slots := array_append(available_proposer_slots, target_slot);
    end loop;

    for trade_item in select jsonb_array_elements(trade.requested_items)
    loop
        target_artist_id := (trade_item->>'artist_id')::uuid;
        select roster_slot into target_slot
        from public.rosters
        where league_id = trade.league_id
        and user_id = trade.recipient_id
        and artist_id = target_artist_id
        and released_at is null
        for update;
        if target_slot is null then
            raise exception 'A requested artist is no longer on the original roster';
        end if;
        available_recipient_slots := array_append(available_recipient_slots, target_slot);
    end loop;

    for trade_item in select jsonb_array_elements(trade.requested_items)
    loop
        target_artist_id := (trade_item->>'artist_id')::uuid;
        select slot into target_slot
        from unnest(available_proposer_slots) as slot
        where slot <> all(used_proposer_slots)
        and public.artist_fits_roster_slot(target_artist_id, slot)
        limit 1;
        if target_slot is null then
            raise exception 'The trade no longer fits the proposer roster';
        end if;
        used_proposer_slots := array_append(used_proposer_slots, target_slot);
    end loop;

    for trade_item in select jsonb_array_elements(trade.offered_items)
    loop
        target_artist_id := (trade_item->>'artist_id')::uuid;
        select slot into target_slot
        from unnest(available_recipient_slots) as slot
        where slot <> all(used_recipient_slots)
        and public.artist_fits_roster_slot(target_artist_id, slot)
        limit 1;
        if target_slot is null then
            raise exception 'The trade no longer fits the recipient roster';
        end if;
        used_recipient_slots := array_append(used_recipient_slots, target_slot);
    end loop;

    used_proposer_slots := '{}';
    for trade_item in select jsonb_array_elements(trade.requested_items)
    loop
        target_artist_id := (trade_item->>'artist_id')::uuid;
        select slot into target_slot
        from unnest(available_proposer_slots) as slot
        where slot <> all(used_proposer_slots)
        and public.artist_fits_roster_slot(target_artist_id, slot)
        limit 1;
        used_proposer_slots := array_append(used_proposer_slots, target_slot);
        update public.rosters
        set user_id = trade.proposer_id,
            roster_slot = target_slot
        where league_id = trade.league_id
        and user_id = trade.recipient_id
        and artist_id = target_artist_id
        and released_at is null;
        if not found then
            raise exception 'Requested artist ownership changed during processing';
        end if;
    end loop;

    used_recipient_slots := '{}';
    for trade_item in select jsonb_array_elements(trade.offered_items)
    loop
        target_artist_id := (trade_item->>'artist_id')::uuid;
        select slot into target_slot
        from unnest(available_recipient_slots) as slot
        where slot <> all(used_recipient_slots)
        and public.artist_fits_roster_slot(target_artist_id, slot)
        limit 1;
        used_recipient_slots := array_append(used_recipient_slots, target_slot);
        update public.rosters
        set user_id = trade.recipient_id,
            roster_slot = target_slot
        where league_id = trade.league_id
        and user_id = trade.proposer_id
        and artist_id = target_artist_id
        and released_at is null;
        if not found then
            raise exception 'Offered artist ownership changed during processing';
        end if;
    end loop;

    update public.trade_offers
    set status = 'processed',
        processed_at = now(),
        failure_reason = null
    where id = trade.id
    and status = 'accepted';

    insert into public.notifications (user_id, league_id, kind, title, body)
    select
        league_members.user_id,
        trade.league_id,
        'trade_processed',
        'Trade processed',
        case when force_now then 'A league manager processed an accepted trade.' else 'A scheduled trade processed in your league.' end
    from public.league_members
    where league_members.league_id = trade.league_id
    and league_members.status = 'active';

    insert into public.league_moderation_actions (league_id, manager_id, target_user_id, action, reason)
    values (
        trade.league_id,
        actor,
        trade.proposer_id,
        case when force_now then 'trade_force_processed' else 'trade_processed' end,
        'Trade ' || trade.id::text || ' processed.'
    );

    return 'processed';
end;
$$;

grant execute on function public.process_trade_offer(uuid, boolean) to authenticated;

create or replace function public.process_due_trades()
returns table (
    trade_id uuid,
    scheduled_processing_at timestamptz,
    started_at timestamptz,
    completed_at timestamptz,
    result text,
    duration_ms integer
)
language plpgsql
security definer
set search_path = public
as $$
declare
    trade_record record;
    start_at timestamptz;
    end_at timestamptz;
    process_result text;
begin
    for trade_record in
        select id, processing_at
        from public.trade_offers
        where status = 'accepted'
        and processed_at is null
        and processing_at <= now()
        order by processing_at, created_at
        limit 50
    loop
        start_at := clock_timestamp();
        begin
            process_result := public.process_trade_offer(trade_record.id, false);
        exception when others then
            process_result := 'failed';
            update public.trade_offers
            set status = 'failed',
                failed_at = now(),
                failure_reason = sqlerrm
            where id = trade_record.id
            and status = 'accepted';
        end;
        end_at := clock_timestamp();
        trade_id := trade_record.id;
        scheduled_processing_at := trade_record.processing_at;
        started_at := start_at;
        completed_at := end_at;
        result := process_result;
        duration_ms := extract(milliseconds from (end_at - start_at))::integer;
        return next;
    end loop;
end;
$$;

grant execute on function public.process_due_trades() to authenticated;

create or replace function public.manager_role_conflicts()
returns table (
    league_id uuid,
    manager_count bigint,
    managers jsonb
)
language sql
security definer
set search_path = public
as $$
    select
        league_members.league_id,
        count(*) as manager_count,
        jsonb_agg(jsonb_build_object('user_id', league_members.user_id, 'team_name', league_members.team_name)) as managers
    from public.league_members
    where league_members.status = 'active'
    and league_members.role = 'manager'
    group by league_members.league_id
    having count(*) > 1;
$$;

grant execute on function public.manager_role_conflicts() to authenticated;

create or replace function public.trade_duplicate_active_ownerships()
returns table (
    league_id uuid,
    artist_id uuid,
    active_owner_count bigint,
    owners jsonb
)
language sql
security definer
set search_path = public
as $$
    select
        rosters.league_id,
        rosters.artist_id,
        count(*) as active_owner_count,
        jsonb_agg(jsonb_build_object(
            'user_id', rosters.user_id,
            'roster_slot', rosters.roster_slot,
            'acquired_at', rosters.acquired_at
        ) order by rosters.acquired_at) as owners
    from public.rosters
    where rosters.released_at is null
    group by rosters.league_id, rosters.artist_id
    having count(*) > 1;
$$;

grant execute on function public.trade_duplicate_active_ownerships() to authenticated;

create or replace function public.market_listener_diagnostics()
returns table (
    spotify_id text,
    normalized_name text,
    name text,
    issue text,
    current_listeners bigint,
    current_listeners_source text,
    current_listeners_observed_at timestamptz,
    snapshot_listeners bigint,
    generic_listeners bigint,
    updated_at timestamptz
)
language sql
security definer
set search_path = public
as $$
    select
        mac.spotify_id,
        mac.normalized_name,
        mac.name,
        issue.issue,
        mac.current_listeners,
        mac.current_listeners_source,
        mac.current_listeners_observed_at,
        mac.snapshot_listeners,
        mac.listeners,
        mac.updated_at
    from public.market_artist_cache mac
    cross join lateral (
        values
            ('missing_current_listeners', mac.current_listeners is null),
            ('missing_current_source', mac.current_listeners is not null and nullif(trim(coalesce(mac.current_listeners_source, '')), '') is null),
            ('stale_current_listeners', mac.current_listeners_observed_at is not null and mac.current_listeners_observed_at < now() - interval '14 days'),
            ('generic_current_disagreement', mac.current_listeners is not null and mac.listeners is not null and greatest(mac.current_listeners, mac.listeners) > 0 and abs(mac.current_listeners - mac.listeners)::numeric / greatest(mac.current_listeners, mac.listeners)::numeric > 0.15),
            ('snapshot_mislabeled_as_current', mac.current_listeners is not null and mac.snapshot_listeners is not null and mac.current_listeners = mac.snapshot_listeners and coalesce(mac.current_listeners_source, '') not ilike '%search%')
    ) as issue(issue, active)
    where issue.active;
$$;

grant execute on function public.market_listener_diagnostics() to authenticated;

create or replace function public.roster_legacy_eligibility_diagnostics()
returns table (
    league_id uuid,
    user_id uuid,
    artist_id uuid,
    roster_slot text,
    acquired_role text,
    acquired_listeners bigint,
    acquired_role_source text,
    current_role_label text,
    current_listeners bigint,
    issue text
)
language sql
security definer
set search_path = public
as $$
    with roster_rows as (
        select
            r.league_id,
            r.user_id,
            r.artist_id,
            r.roster_slot,
            r.acquired_role,
            r.acquired_listeners,
            r.acquired_role_source,
            coalesce(
                mac.current_listeners,
                mac.snapshot_listeners,
                mac.listeners,
                a.listeners
            ) as current_listeners,
            public.role_for_listener_count(
                coalesce(
                    mac.current_listeners,
                    mac.snapshot_listeners,
                    mac.listeners,
                    a.listeners
                )
            ) as current_role_label,
            r.released_at
        from public.rosters r
        join public.artists a
            on a.id = r.artist_id
        left join public.market_artist_cache mac
            on mac.normalized_name = a.normalized_name
        where r.released_at is null
    )
    select
        roster_rows.league_id,
        roster_rows.user_id,
        roster_rows.artist_id,
        roster_rows.roster_slot,
        roster_rows.acquired_role,
        roster_rows.acquired_listeners,
        roster_rows.acquired_role_source,
        roster_rows.current_role_label,
        roster_rows.current_listeners,
        diagnostic.issue
    from roster_rows
    cross join lateral (
        values
            (
                'missing_acquisition_metadata',
                roster_rows.acquired_role is null
                or roster_rows.acquired_listeners is null
            ),
            (
                'acquired_role_copied_from_slot_without_source',
                roster_rows.acquired_role is not null
                and roster_rows.roster_slot ilike
                    '%' || replace(lower(roster_rows.acquired_role), ' ', '') || '%'
                and coalesce(roster_rows.acquired_role_source, '') ilike '%slot%'
            ),
            (
                'acquired_role_listener_mismatch',
                roster_rows.acquired_listeners is not null
                and roster_rows.acquired_role is distinct from
                    public.role_for_listener_count(roster_rows.acquired_listeners)
            ),
            (
                'current_role_listener_mismatch',
                roster_rows.current_listeners is not null
                and roster_rows.current_role_label is null
            )
    ) as diagnostic(issue, active)
    where diagnostic.active;
$$;

grant execute
on function public.roster_legacy_eligibility_diagnostics()
to authenticated;

drop function if exists public.league_roster_entries(uuid);
create or replace function public.league_roster_entries(target_league_id uuid)
returns table(
    username text,
    roster_slot text,
    artist_name text,
    image_url text,
    listeners bigint,
    playcount bigint,
    acquired_at timestamptz,
    released_at timestamptz,
    acquired_role text,
    acquired_listeners bigint,
    acquired_role_source text
)
language sql
security definer
set search_path = public
as $$
    select
        profiles.username,
        rosters.roster_slot,
        coalesce(artists.display_name, artists.normalized_name) as artist_name,
        artists.image_url,
        coalesce(mac.current_listeners, mac.snapshot_listeners, mac.listeners, artists.listeners) as listeners,
        artists.playcount,
        rosters.acquired_at,
        rosters.released_at,
        rosters.acquired_role,
        rosters.acquired_listeners,
        rosters.acquired_role_source
    from public.rosters
    join public.profiles on profiles.id = rosters.user_id
    join public.artists on artists.id = rosters.artist_id
    left join public.market_artist_cache mac
        on mac.normalized_name = artists.normalized_name
    where rosters.league_id = target_league_id
    and rosters.released_at is null
    and exists (
        select 1 from public.league_members
        where league_members.league_id = target_league_id
        and league_members.user_id = auth.uid()
        and league_members.status = 'active'
    )
    order by profiles.username, rosters.roster_slot, rosters.acquired_at;
$$;

grant execute on function public.league_roster_entries(uuid) to authenticated;

create or replace function public.trade_health_diagnostics()
returns table (
    issue text,
    count bigint
)
language sql
security definer
set search_path = public
as $$
    select 'accepted_missing_processing_at', count(*)
    from public.trade_offers
    where status = 'accepted' and processed_at is null and processing_at is null
    union all
    select 'duplicate_active_ownership', count(*)
    from public.trade_duplicate_active_ownerships()
    union all
    select 'manager_role_conflicts', count(*)
    from public.manager_role_conflicts();
$$;

grant execute on function public.trade_health_diagnostics() to authenticated;

select cron.unschedule(jobid)
from cron.job
where jobname in (
    'refresh-market-cache-daily',
    'refresh-deep-cuts-01',
    'refresh-deep-cuts-02',
    'refresh-deep-cuts-03',
    'refresh-deep-cuts-04',
    'refresh-market-artwork-0250',
    'refresh-market-artwork-0300-0650',
    'backfill-market-artwork-early',
    'backfill-market-artwork-late',
    'snapshot-artist-weekly-metrics',
    'watch-pastspot-snapshots',
    'freeze-artist-weekly-projections',
    'score-artists-daily',
    'score-artists-catchup-tonight',
    'process-due-trades-every-5'
);

select cron.schedule(
    'refresh-market-cache-daily',
    '0 2 * * *',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'core'),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'refresh-deep-cuts-01',
    '10 2 * * *',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'deep-cuts', 'countryOffset', 0, 'countryLimit', 25, 'offset', 0, 'limit', 100),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'refresh-deep-cuts-02',
    '20 2 * * *',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'deep-cuts', 'countryOffset', 0, 'countryLimit', 25, 'offset', 100, 'limit', 100),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'refresh-deep-cuts-03',
    '30 2 * * *',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'deep-cuts', 'countryOffset', 25, 'countryLimit', 25, 'offset', 0, 'limit', 100),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'refresh-deep-cuts-04',
    '40 2 * * *',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'deep-cuts', 'countryOffset', 25, 'countryLimit', 25, 'offset', 100, 'limit', 100),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'backfill-market-artwork-early',
    '50 2-23 * * *',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'artwork', 'limit', 100),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'backfill-market-artwork-late',
    '0 3-23 * * *',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'artwork', 'limit', 100),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'snapshot-artist-weekly-metrics',
    '45 7 * * 6',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'snapshot-week', 'limit', 10000),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'watch-pastspot-snapshots',
    '*/30 7-14 * * *',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'watch-pastspot'),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'freeze-artist-weekly-projections',
    '15 8 * * 1',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'freeze-projections', 'limit', 10000),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'score-artists-daily',
    '30 8 * * *',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'score-daily', 'limit', 10000),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);

select cron.schedule(
    'process-due-trades-every-5',
    '*/5 * * * *',
    $$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object('Content-Type', 'application/json', 'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')),
        body := jsonb_build_object('mode', 'process-due-trades'),
        timeout_milliseconds := 120000
    ) as request_id;
    $$
);
