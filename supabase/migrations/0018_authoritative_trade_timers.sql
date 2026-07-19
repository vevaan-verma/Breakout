create extension if not exists pg_cron with schema extensions;
create extension if not exists pg_net with schema extensions;

alter table public.leagues
add column if not exists timezone_name text not null default 'America/Los_Angeles',
add column if not exists waiver_run_weekday integer not null default 3,
add column if not exists waiver_run_time time not null default time '12:00';

do $$
begin
    if not exists (select 1 from pg_constraint where conname = 'leagues_waiver_run_weekday_range') then
        alter table public.leagues
        add constraint leagues_waiver_run_weekday_range check (waiver_run_weekday between 0 and 6);
    end if;
end;
$$;

alter table public.trade_offers
add column if not exists processing_at timestamptz,
add column if not exists processed_at timestamptz,
add column if not exists failed_at timestamptz,
add column if not exists failure_reason text,
add column if not exists processing_started_at timestamptz;

do $$
begin
    alter table public.trade_offers drop constraint if exists trade_offers_status_check;
    alter table public.trade_offers
    add constraint trade_offers_status_check
    check (status in ('pending', 'accepted', 'processed', 'failed', 'declined', 'canceled', 'expired'));
end;
$$;

create index if not exists trade_offers_pending_processing_idx
on public.trade_offers (processing_at)
where status = 'accepted' and processed_at is null;

create index if not exists trade_offers_league_feed_idx
on public.trade_offers (league_id, status, coalesce(processed_at, processing_at, created_at) desc);

do $$
begin
    if exists (
        select 1
        from public.rosters
        where released_at is null
        group by league_id, artist_id
        having count(*) > 1
    ) then
        raise notice 'Skipped active owner unique index because duplicate active roster ownership exists. Run public.trade_duplicate_active_ownerships() and repair explicitly.';
    else
        create unique index if not exists rosters_one_active_artist_owner_per_league_idx
        on public.rosters (league_id, artist_id)
        where released_at is null;
    end if;
end;
$$;

create or replace function public.next_weekday_time_at_timezone(
    target_weekday integer,
    target_time time,
    timezone_name text default 'America/Los_Angeles',
    reference_at timestamptz default now()
)
returns timestamptz
language plpgsql
stable
set search_path = public
as $$
declare
    local_now timestamp;
    local_day date;
    current_weekday integer;
    days_until integer;
    candidate timestamp;
begin
    local_now := reference_at at time zone timezone_name;
    local_day := local_now::date;
    current_weekday := extract(dow from local_now)::integer;
    days_until := mod(target_weekday - current_weekday + 7, 7);
    candidate := local_day + days_until + target_time;
    if candidate <= local_now then
        candidate := candidate + interval '7 days';
    end if;
    return candidate at time zone timezone_name;
end;
$$;

create or replace function public.next_trade_processing_at(reference_at timestamptz default now())
returns timestamptz
language sql
stable
set search_path = public
as $$
    select public.next_weekday_time_at_timezone(0, time '15:00', 'America/Los_Angeles', reference_at);
$$;

create or replace function public.roster_lock_state(reference_at timestamptz default now())
returns table (
    is_locked boolean,
    next_transition_at timestamptz,
    next_transition_kind text
)
language plpgsql
stable
set search_path = public
as $$
declare
    timezone_name text := 'America/Los_Angeles';
    local_now timestamp;
    current_weekday integer;
begin
    local_now := reference_at at time zone timezone_name;
    current_weekday := extract(dow from local_now)::integer;

    if current_weekday in (0, 6) then
        is_locked := false;
        next_transition_at := public.next_weekday_time_at_timezone(1, time '00:00', timezone_name, reference_at);
        next_transition_kind := 'lock';
    else
        is_locked := true;
        next_transition_at := public.next_weekday_time_at_timezone(6, time '00:00', timezone_name, reference_at);
        next_transition_kind := 'unlock';
    end if;
    return next;
end;
$$;

create or replace function public.next_waiver_processing_at(target_league_id uuid, reference_at timestamptz default now())
returns timestamptz
language sql
stable
security definer
set search_path = public
as $$
    select public.next_weekday_time_at_timezone(
        coalesce(leagues.waiver_run_weekday, 3),
        coalesce(leagues.waiver_run_time, time '12:00'),
        coalesce(nullif(leagues.timezone_name, ''), 'America/Los_Angeles'),
        reference_at
    )
    from public.leagues
    where leagues.id = target_league_id;
$$;

create or replace function public.league_timing(target_league_id uuid)
returns table (
    roster_locked boolean,
    roster_next_transition_at timestamptz,
    roster_next_transition_kind text,
    next_trade_processing_at timestamptz,
    next_waiver_processing_at timestamptz,
    server_now timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
    select
        lock_state.is_locked,
        lock_state.next_transition_at,
        lock_state.next_transition_kind,
        public.next_trade_processing_at(now()),
        public.next_waiver_processing_at(target_league_id, now()),
        now()
    from public.roster_lock_state(now()) lock_state
    where exists (
        select 1 from public.league_members
        where league_members.league_id = target_league_id
        and league_members.user_id = auth.uid()
        and league_members.status = 'active'
    );
$$;

grant execute on function public.league_timing(uuid) to authenticated;

create or replace function public.respond_trade_offer(target_trade_id uuid, response text)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    actor uuid := auth.uid();
    trade public.trade_offers%rowtype;
    trade_item jsonb;
    target_artist_id uuid;
begin
    if actor is null then
        raise exception 'Authentication required';
    end if;

    select * into trade
    from public.trade_offers
    where id = target_trade_id
    for update;

    if trade.id is null then
        raise exception 'Trade offer not found';
    end if;
    if trade.status <> 'pending' then
        raise exception 'This trade is no longer pending';
    end if;
    if trade.expires_at <= now() then
        update public.trade_offers set status = 'expired', responded_at = now() where id = trade.id;
        raise exception 'This trade offer has expired';
    end if;

    if response = 'cancel' then
        if actor <> trade.proposer_id then
            raise exception 'Only the sender can cancel this trade';
        end if;
        update public.trade_offers set status = 'canceled', responded_at = now() where id = trade.id;
        return;
    end if;

    if actor <> trade.recipient_id then
        raise exception 'Only the recipient can respond to this trade';
    end if;

    if response = 'decline' then
        update public.trade_offers set status = 'declined', responded_at = now() where id = trade.id;
        insert into public.notifications (user_id, league_id, kind, title, body)
        values (trade.proposer_id, trade.league_id, 'trade_declined', 'Trade declined', 'Your trade offer was declined.');
        return;
    end if;

    if response <> 'accept' then
        raise exception 'Unsupported trade response';
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
            raise exception 'One of these artists is already reserved by an accepted trade';
        end if;
    end loop;

    update public.trade_offers
    set status = 'accepted',
        responded_at = now(),
        processing_at = public.next_trade_processing_at(now()),
        failure_reason = null,
        failed_at = null
    where id = trade.id;

    insert into public.notifications (user_id, league_id, kind, title, body)
    select
        league_members.user_id,
        trade.league_id,
        'trade_accepted',
        'Trade accepted',
        'A trade was accepted and is pending weekly processing.'
    from public.league_members
    where league_members.league_id = trade.league_id
    and league_members.status = 'active';
end;
$$;

grant execute on function public.respond_trade_offer(uuid, text) to authenticated;

create or replace function public.process_trade_offer(target_trade_id uuid, force_now boolean default false)
returns text
language plpgsql
security definer
set search_path = public
as $$
declare
    actor uuid := auth.uid();
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
    if not force_now and trade.processing_at > now() then
        raise exception 'Trade is not ready to process';
    end if;
    if force_now and coalesce(current_setting('request.jwt.claim.role', true), '') <> 'service_role' then
        raise exception 'Only dev/server processing can force this trade';
    end if;

    update public.trade_offers
    set processing_started_at = now()
    where id = trade.id;

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
        if not exists (
            select 1 from public.rosters
            where league_id = trade.league_id
            and user_id = trade.proposer_id
            and artist_id = target_artist_id
            and released_at is null
            for update
        ) then
            update public.trade_offers set status = 'failed', failed_at = now(), failure_reason = 'An offered artist is no longer on the original roster.' where id = trade.id;
            return 'failed';
        end if;
        select roster_slot into target_slot from public.rosters where league_id = trade.league_id and user_id = trade.proposer_id and artist_id = target_artist_id and released_at is null limit 1;
        available_proposer_slots := array_append(available_proposer_slots, target_slot);
    end loop;

    for trade_item in select jsonb_array_elements(trade.requested_items)
    loop
        target_artist_id := (trade_item->>'artist_id')::uuid;
        if not exists (
            select 1 from public.rosters
            where league_id = trade.league_id
            and user_id = trade.recipient_id
            and artist_id = target_artist_id
            and released_at is null
            for update
        ) then
            update public.trade_offers set status = 'failed', failed_at = now(), failure_reason = 'A requested artist is no longer on the original roster.' where id = trade.id;
            return 'failed';
        end if;
        select roster_slot into target_slot from public.rosters where league_id = trade.league_id and user_id = trade.recipient_id and artist_id = target_artist_id and released_at is null limit 1;
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
            update public.trade_offers set status = 'failed', failed_at = now(), failure_reason = 'The trade no longer fits the proposer roster.' where id = trade.id;
            return 'failed';
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
            update public.trade_offers set status = 'failed', failed_at = now(), failure_reason = 'The trade no longer fits the recipient roster.' where id = trade.id;
            return 'failed';
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
    end loop;

    update public.trade_offers
    set status = 'processed',
        processed_at = now(),
        failure_reason = null
    where id = trade.id;

    insert into public.notifications (user_id, league_id, kind, title, body)
    select
        league_members.user_id,
        trade.league_id,
        'trade_processed',
        'Trade processed',
        'A scheduled trade processed in your league.'
    from public.league_members
    where league_members.league_id = trade.league_id
    and league_members.status = 'active';

    return 'processed';
exception when others then
    update public.trade_offers
    set status = 'failed',
        failed_at = now(),
        failure_reason = sqlerrm
    where id = target_trade_id
    and status = 'accepted';
    return 'failed';
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
        process_result := public.process_trade_offer(trade_record.id, false);
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

drop function if exists public.league_trade_offers(uuid);
create or replace function public.league_trade_offers(target_league_id uuid)
returns table (
    id uuid,
    proposer_username text,
    recipient_username text,
    offered_artist_name text,
    requested_artist_name text,
    offered_image_url text,
    requested_image_url text,
    offered_roster_slot text,
    requested_roster_slot text,
    offered_items jsonb,
    requested_items jsonb,
    status text,
    created_at timestamptz,
    expires_at timestamptz,
    processing_at timestamptz,
    processed_at timestamptz,
    failed_at timestamptz,
    failure_reason text,
    is_incoming boolean,
    is_outgoing boolean,
    is_public_feed boolean
)
language sql
security definer
set search_path = public
as $$
    select
        trade_offers.id,
        proposer.username as proposer_username,
        recipient.username as recipient_username,
        coalesce(offered_artist.display_name, offered_artist.normalized_name) as offered_artist_name,
        coalesce(requested_artist.display_name, requested_artist.normalized_name) as requested_artist_name,
        offered_artist.image_url as offered_image_url,
        requested_artist.image_url as requested_image_url,
        trade_offers.offered_roster_slot,
        trade_offers.requested_roster_slot,
        case
            when jsonb_array_length(trade_offers.offered_items) > 0 then trade_offers.offered_items
            else jsonb_build_array(jsonb_build_object(
                'artist_id', offered_artist.id,
                'name', coalesce(offered_artist.display_name, offered_artist.normalized_name),
                'image_url', offered_artist.image_url,
                'slot', trade_offers.offered_roster_slot
            ))
        end as offered_items,
        case
            when jsonb_array_length(trade_offers.requested_items) > 0 then trade_offers.requested_items
            else jsonb_build_array(jsonb_build_object(
                'artist_id', requested_artist.id,
                'name', coalesce(requested_artist.display_name, requested_artist.normalized_name),
                'image_url', requested_artist.image_url,
                'slot', trade_offers.requested_roster_slot
            ))
        end as requested_items,
        case
            when trade_offers.status = 'pending' and trade_offers.expires_at <= now() then 'expired'
            else trade_offers.status
        end as status,
        trade_offers.created_at,
        trade_offers.expires_at,
        trade_offers.processing_at,
        trade_offers.processed_at,
        trade_offers.failed_at,
        trade_offers.failure_reason,
        trade_offers.recipient_id = auth.uid() as is_incoming,
        trade_offers.proposer_id = auth.uid() as is_outgoing,
        trade_offers.status in ('accepted', 'processed', 'failed') as is_public_feed
    from public.trade_offers
    join public.profiles proposer on proposer.id = trade_offers.proposer_id
    join public.profiles recipient on recipient.id = trade_offers.recipient_id
    join public.artists offered_artist on offered_artist.id = trade_offers.offered_artist_id
    join public.artists requested_artist on requested_artist.id = trade_offers.requested_artist_id
    where trade_offers.league_id = target_league_id
    and exists (
        select 1 from public.league_members viewer
        where viewer.league_id = target_league_id
        and viewer.user_id = auth.uid()
        and viewer.status = 'active'
    )
    and (
        trade_offers.proposer_id = auth.uid()
        or trade_offers.recipient_id = auth.uid()
        or trade_offers.status in ('accepted', 'processed', 'failed')
    )
    order by coalesce(trade_offers.processed_at, trade_offers.processing_at, trade_offers.created_at) desc;
$$;

grant execute on function public.league_trade_offers(uuid) to authenticated;

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
    limit 1;

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
end;
$$;

grant execute on function public.transfer_league_manager(uuid, text) to authenticated;

create or replace function public.kick_league_member(target_league_id uuid, target_username text)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    target_user_id uuid;
    target_role text;
begin
    if auth.uid() is null then
        raise exception 'Authentication required';
    end if;

    if not exists (
        select 1 from public.league_members
        where league_id = target_league_id
        and user_id = auth.uid()
        and role = 'manager'
        and status = 'active'
    ) then
        raise exception 'Only the manager can remove members';
    end if;

    select profiles.id, league_members.role into target_user_id, target_role
    from public.profiles
    join public.league_members on league_members.user_id = profiles.id
    where league_members.league_id = target_league_id
    and league_members.status = 'active'
    and lower(profiles.username) = lower(trim(target_username))
    limit 1;

    if target_user_id is null then
        raise exception 'No active member with that username';
    end if;
    if target_user_id = auth.uid() then
        raise exception 'Managers must transfer manager role before leaving';
    end if;
    if target_role = 'manager' then
        raise exception 'Transfer manager role before removing this member';
    end if;
    if target_role = 'bot-managed' then
        raise exception 'Bot-managed teams cannot be kicked again';
    end if;

    update public.league_members
    set role = 'bot-managed',
        team_name = coalesce(nullif(team_name, ''), trim(target_username)) || ' Team'
    where league_id = target_league_id
    and user_id = target_user_id;
end;
$$;

grant execute on function public.kick_league_member(uuid, text) to authenticated;

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
        jsonb_agg(jsonb_build_object('user_id', rosters.user_id, 'roster_slot', rosters.roster_slot, 'acquired_at', rosters.acquired_at)) as owners
    from public.rosters
    where rosters.released_at is null
    group by rosters.league_id, rosters.artist_id
    having count(*) > 1;
$$;

grant execute on function public.trade_duplicate_active_ownerships() to authenticated;
