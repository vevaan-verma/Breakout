
create extension if not exists pgcrypto;

create table if not exists public.profiles (
    id uuid primary key references auth.users(id) on delete cascade,
    email text unique,
    username text unique,
    display_name text,
    mailing_list_opt_in boolean not null default true,
    created_at timestamptz not null default now()
);

alter table public.profiles add column if not exists email text unique;
alter table public.profiles add column if not exists mailing_list_opt_in boolean not null default true;
create unique index if not exists profiles_username_lower_unique
on public.profiles (lower(username))
where username is not null;

do $$
begin
    if not exists (
        select 1 from pg_constraint
        where conname = 'profiles_username_format'
    ) then
        alter table public.profiles
        add constraint profiles_username_format check (username ~ '^[A-Za-z0-9_]{3,24}$');
    end if;

    if not exists (
        select 1 from pg_constraint
        where conname = 'profiles_email_length'
    ) then
        alter table public.profiles
        add constraint profiles_email_length check (char_length(email) <= 254);
    end if;
end;
$$;

create table if not exists public.leagues (
    id uuid primary key default gen_random_uuid(),
    owner_id uuid not null references public.profiles(id) on delete cascade,
    name text not null,
    invite_code text not null unique,
    salary_cap integer not null default 100000000,
    draft_format text not null default 'snake',
    draft_starts_at timestamptz,
    pick_seconds integer not null default 90,
    season_weeks integer not null default 10,
    headliner_slots integer not null default 2,
    rising_slots integer not null default 1,
    wildcard_slots integer not null default 1,
    deep_cut_slots integer not null default 1,
    bench_slots integer not null default 2,
    settings_lock_hours integer not null default 24,
    manager_approval_required boolean not null default true,
    invites_open boolean not null default true,
    max_members integer not null default 10,
    draft_status text not null default 'scheduled',
    current_pick_index integer not null default 0,
    current_pick_started_at timestamptz not null default now(),
    lobby_ready_at timestamptz,
    auto_pick_enabled boolean not null default false,
    created_at timestamptz not null default now()
);

alter table public.leagues add column if not exists draft_format text not null default 'snake';
alter table public.leagues add column if not exists draft_starts_at timestamptz;
alter table public.leagues add column if not exists pick_seconds integer not null default 90;
alter table public.leagues add column if not exists season_weeks integer not null default 10;
alter table public.leagues add column if not exists headliner_slots integer not null default 2;
alter table public.leagues add column if not exists rising_slots integer not null default 1;
alter table public.leagues add column if not exists wildcard_slots integer not null default 1;
alter table public.leagues add column if not exists deep_cut_slots integer not null default 1;
alter table public.leagues add column if not exists bench_slots integer not null default 2;
alter table public.leagues add column if not exists max_waiver_claims integer not null default 5;
alter table public.leagues add column if not exists settings_lock_hours integer not null default 24;
alter table public.leagues add column if not exists manager_approval_required boolean not null default true;
alter table public.leagues add column if not exists invites_open boolean not null default true;
alter table public.leagues add column if not exists max_members integer not null default 10;
alter table public.leagues add column if not exists draft_status text not null default 'scheduled';
alter table public.leagues add column if not exists current_pick_index integer not null default 0;
alter table public.leagues add column if not exists current_pick_started_at timestamptz not null default now();
alter table public.leagues add column if not exists lobby_ready_at timestamptz;
alter table public.leagues add column if not exists auto_pick_enabled boolean not null default false;

do $$
begin
    if not exists (
        select 1 from pg_constraint
        where conname = 'leagues_name_length'
    ) then
        alter table public.leagues
        add constraint leagues_name_length check (char_length(name) between 1 and 40);
    end if;
end;
$$;

do $$
begin
    if not exists (
        select 1 from pg_constraint
        where conname = 'leagues_pick_seconds_range'
    ) then
        alter table public.leagues
        add constraint leagues_pick_seconds_range check (pick_seconds between 30 and 300);
    end if;
end;
$$;

do $$
begin
    if not exists (
        select 1 from pg_constraint
        where conname = 'leagues_max_members_range'
    ) then
        alter table public.leagues
        add constraint leagues_max_members_range check (max_members between 2 and 20);
    end if;
end;
$$;

do $$
begin
    if not exists (
        select 1 from pg_constraint
        where conname = 'leagues_season_weeks_range'
    ) then
        alter table public.leagues
        add constraint leagues_season_weeks_range check (season_weeks between 4 and 24);
    end if;
end;
$$;

do $$
begin
    if not exists (
        select 1 from pg_constraint
        where conname = 'leagues_max_waiver_claims_range'
    ) then
        alter table public.leagues
        add constraint leagues_max_waiver_claims_range check (max_waiver_claims between 1 and 12);
    end if;
end;
$$;

do $$
begin
    if not exists (
        select 1 from pg_constraint
        where conname = 'leagues_invite_code_format'
    ) then
        alter table public.leagues
        add constraint leagues_invite_code_format check (invite_code ~ '^[A-Z2-9]{6}$');
    end if;
end;
$$;

create table if not exists public.league_members (
    league_id uuid not null references public.leagues(id) on delete cascade,
    user_id uuid not null references public.profiles(id) on delete cascade,
    team_name text not null,
    role text not null default 'member',
    status text not null default 'active',
    joined_at timestamptz not null default now(),
    primary key (league_id, user_id)
);

alter table public.league_members add column if not exists role text not null default 'member';
alter table public.league_members add column if not exists status text not null default 'active';
alter table public.league_members add column if not exists auto_pick_enabled boolean not null default false;
alter table public.league_members add column if not exists draft_order_position integer;

create table if not exists public.draft_room_presence (
    league_id uuid not null references public.leagues(id) on delete cascade,
    user_id uuid not null references public.profiles(id) on delete cascade,
    last_seen_at timestamptz not null default now(),
    primary key (league_id, user_id)
);

create table if not exists public.league_setting_votes (
    id uuid primary key default gen_random_uuid(),
    league_id uuid not null references public.leagues(id) on delete cascade,
    proposed_by uuid not null references public.profiles(id) on delete cascade,
    settings jsonb not null,
    status text not null default 'pending',
    closes_at timestamptz not null,
    created_at timestamptz not null default now()
);

create table if not exists public.league_setting_vote_responses (
    vote_id uuid not null references public.league_setting_votes(id) on delete cascade,
    user_id uuid not null references public.profiles(id) on delete cascade,
    response text not null,
    responded_at timestamptz not null default now(),
    primary key (vote_id, user_id)
);

create table if not exists public.league_moderation_actions (
    id uuid primary key default gen_random_uuid(),
    league_id uuid not null references public.leagues(id) on delete cascade,
    manager_id uuid not null references public.profiles(id) on delete cascade,
    target_user_id uuid references public.profiles(id) on delete set null,
    action text not null,
    reason text,
    created_at timestamptz not null default now()
);

create table if not exists public.artists (
    id uuid primary key default gen_random_uuid(),
    display_name text,
    provider_name text not null,
    normalized_name text not null unique,
    provider_url text,
    image_url text,
    listeners bigint,
    playcount bigint,
    fetched_at timestamptz not null default now()
);

alter table public.artists add column if not exists provider_name text;
alter table public.artists add column if not exists provider_url text;
alter table public.artists add column if not exists display_name text;
alter table public.artists add column if not exists lastfm_name text;
update public.artists
set lastfm_name = coalesce(nullif(lastfm_name, ''), display_name, normalized_name)
where lastfm_name is null or lastfm_name = '';
alter table public.artists alter column lastfm_name set default '';

create table if not exists public.rosters (
    id uuid primary key default gen_random_uuid(),
    league_id uuid not null references public.leagues(id) on delete cascade,
    user_id uuid not null references public.profiles(id) on delete cascade,
    artist_id uuid not null references public.artists(id) on delete cascade,
    roster_slot text,
    acquisition_value integer not null,
    acquired_at timestamptz not null default now(),
    released_at timestamptz,
    unique (league_id, user_id, artist_id)
);

alter table public.rosters add column if not exists roster_slot text;

create table if not exists public.waiver_claims (
    id uuid primary key default gen_random_uuid(),
    league_id uuid not null references public.leagues(id) on delete cascade,
    user_id uuid not null references public.profiles(id) on delete cascade,
    artist_id uuid not null references public.artists(id) on delete cascade,
    roster_slot text not null,
    priority integer not null,
    status text not null default 'pending',
    created_at timestamptz not null default now(),
    processed_at timestamptz,
    unique (league_id, user_id, artist_id)
);

create table if not exists public.draft_picks (
    id uuid primary key default gen_random_uuid(),
    league_id uuid not null references public.leagues(id) on delete cascade,
    pick_number integer not null,
    user_id uuid not null references public.profiles(id) on delete cascade,
    artist_id uuid not null references public.artists(id) on delete cascade,
    roster_slot text not null,
    draft_mode text not null default 'live',
    auto_picked boolean not null default false,
    picked_at timestamptz not null default now(),
    unique (league_id, pick_number),
    unique (league_id, artist_id, draft_mode)
);

alter table public.draft_picks add column if not exists auto_picked boolean not null default false;

create table if not exists public.notifications (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles(id) on delete cascade,
    league_id uuid references public.leagues(id) on delete cascade,
    kind text not null,
    title text not null,
    body text not null,
    scheduled_for timestamptz,
    read_at timestamptz,
    created_at timestamptz not null default now()
);

alter table public.profiles enable row level security;
alter table public.leagues enable row level security;
alter table public.league_members enable row level security;
alter table public.draft_room_presence enable row level security;
alter table public.league_setting_votes enable row level security;
alter table public.league_setting_vote_responses enable row level security;
alter table public.league_moderation_actions enable row level security;
alter table public.artists enable row level security;
alter table public.rosters enable row level security;
alter table public.waiver_claims enable row level security;
alter table public.draft_picks enable row level security;
alter table public.notifications enable row level security;

drop policy if exists "Users can read their profile" on public.profiles;
create policy "Users can read their profile"
on public.profiles for select
using (auth.uid() = id);

drop policy if exists "Users can update their profile" on public.profiles;
create policy "Users can update their profile"
on public.profiles for update
using (auth.uid() = id);

drop policy if exists "Users can create their profile" on public.profiles;
create policy "Users can create their profile"
on public.profiles for insert
with check (auth.uid() = id);

drop policy if exists "Users can read leagues they belong to" on public.leagues;
create policy "Users can read leagues they belong to"
on public.leagues for select
using (
    exists (
        select 1 from public.league_members
        where league_members.league_id = leagues.id
        and league_members.user_id = auth.uid()
    )
);

drop policy if exists "Users can create owned leagues" on public.leagues;
create policy "Users can create owned leagues"
on public.leagues for insert
with check (auth.uid() = owner_id);

drop policy if exists "League managers can update league settings" on public.leagues;
create policy "League managers can update league settings"
on public.leagues for update
using (
    exists (
        select 1 from public.league_members
        where league_members.league_id = leagues.id
        and league_members.user_id = auth.uid()
        and league_members.role = 'manager'
        and league_members.status = 'active'
    )
)
with check (
    exists (
        select 1 from public.league_members
        where league_members.league_id = leagues.id
        and league_members.user_id = auth.uid()
        and league_members.role = 'manager'
        and league_members.status = 'active'
    )
);

drop policy if exists "Members can read league memberships" on public.league_members;
drop policy if exists "Users can read their own league memberships" on public.league_members;
create policy "Users can read their own league memberships"
on public.league_members for select
using (auth.uid() = user_id);

drop policy if exists "Users can join as themselves" on public.league_members;
create policy "Users can join as themselves"
on public.league_members for insert
with check (auth.uid() = user_id);

drop policy if exists "League managers can moderate members" on public.league_members;
drop policy if exists "Users can update their own league membership" on public.league_members;

create or replace function public.join_league_by_code(code text, requested_team_name text)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
    matched_league_id uuid;
    current_member_count bigint;
    member_limit integer;
begin
    if auth.uid() is null then
        raise exception 'Authentication required';
    end if;

    if upper(trim(code)) !~ '^[A-Z2-9]{6}$' then
        raise exception 'Invite codes must be exactly 6 characters';
    end if;

    select id into matched_league_id
    from public.leagues
    where invite_code = upper(trim(code))
    and invites_open = true;

    if matched_league_id is null then
        raise exception 'Invalid invite code';
    end if;

    if exists (
        select 1 from public.league_members
        where league_id = matched_league_id
        and user_id = auth.uid()
        and status = 'active'
    ) then
        raise exception 'You are already in that league';
    end if;

    select count(*), max(leagues.max_members)
    into current_member_count, member_limit
    from public.league_members
    join public.leagues on leagues.id = league_members.league_id
    where league_members.league_id = matched_league_id
    and league_members.status = 'active';

    if current_member_count >= member_limit then
        raise exception 'League is full';
    end if;

    insert into public.league_members (league_id, user_id, team_name)
    values (matched_league_id, auth.uid(), requested_team_name)
    on conflict (league_id, user_id) do update
    set team_name = excluded.team_name,
        role = 'member',
        status = 'active',
        joined_at = now();

    return matched_league_id;
end;
$$;

grant execute on function public.join_league_by_code(text, text) to authenticated;

create or replace function public.leave_league(target_league_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    remaining_members integer;
    next_manager_id uuid;
begin
    if auth.uid() is null then
        raise exception 'Authentication required';
    end if;

    delete from public.league_members
    where league_id = target_league_id
    and user_id = auth.uid();

    select count(*) into remaining_members
    from public.league_members
    where league_id = target_league_id
    and status = 'active';

    if remaining_members = 0 then
        delete from public.leagues where id = target_league_id;
    elsif not exists (
        select 1 from public.league_members
        where league_id = target_league_id
        and role = 'manager'
        and status = 'active'
    ) then
        select user_id into next_manager_id
        from public.league_members
        where league_id = target_league_id
        and status = 'active'
        order by joined_at asc
        limit 1;

        update public.league_members
        set role = 'manager'
        where league_id = target_league_id
        and user_id = next_manager_id;

        update public.leagues
        set owner_id = next_manager_id
        where id = target_league_id;
    end if;
end;
$$;

grant execute on function public.leave_league(uuid) to authenticated;

create or replace function public.delete_league(target_league_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
    if auth.uid() is null then
        raise exception 'Authentication required';
    end if;

    if not exists (
        select 1
        from public.league_members
        where league_id = target_league_id
        and user_id = auth.uid()
        and role = 'manager'
        and status = 'active'
    ) then
        raise exception 'Only the league manager can delete this league';
    end if;

    delete from public.leagues
    where id = target_league_id;
end;
$$;

grant execute on function public.delete_league(uuid) to authenticated;

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
    and lower(profiles.username) = lower(trim(target_username))
    limit 1;

    if target_user_id is null then
        raise exception 'No active member with that username';
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

    if not exists (
        select 1 from public.league_members
        where league_id = target_league_id
        and user_id = target_user_id
        and role = 'manager'
        and status = 'active'
    ) then
        raise exception 'Manager transfer did not complete';
    end if;
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

    select profiles.id into target_user_id
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
        raise exception 'Managers must leave the league instead of kicking themselves';
    end if;

    delete from public.league_members
    where league_id = target_league_id
    and user_id = target_user_id;
end;
$$;

grant execute on function public.kick_league_member(uuid, text) to authenticated;

create or replace function public.advance_draft_pick(target_league_id uuid, expected_pick_index integer)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
    if auth.uid() is null then
        raise exception 'Authentication required';
    end if;

    if not exists (
        select 1
        from public.league_members
        where league_id = target_league_id
        and user_id = auth.uid()
        and status = 'active'
    ) then
        raise exception 'You are not a member of this league';
    end if;

    update public.leagues
    set current_pick_index = current_pick_index + 1,
        current_pick_started_at = now()
    where id = target_league_id
    and draft_status = 'live'
    and current_pick_index = expected_pick_index;
end;
$$;

grant execute on function public.advance_draft_pick(uuid, integer) to authenticated;

drop function if exists public.make_draft_pick(
    uuid,
    integer,
    text,
    text,
    text,
    text,
    bigint,
    bigint,
    text,
    integer
);

create or replace function public.make_draft_pick(
    target_league_id uuid,
    expected_pick_index integer,
    requested_artist_name text,
    requested_provider_name text,
    requested_provider_url text,
    requested_image_url text,
    requested_listeners bigint,
    requested_playcount bigint,
    requested_roster_slot text,
    requested_acquisition_value integer,
    requested_auto_pick boolean default false
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    league_record public.leagues%rowtype;
    member_order uuid[];
    member_count integer;
    position_index integer;
    picker_user_id uuid;
    target_artist_id uuid;
    normalized_artist_name text;
begin
    if auth.uid() is null then
        raise exception 'Authentication required';
    end if;

    select *
    into league_record
    from public.leagues
    where id = target_league_id
    for update;

    if league_record.id is null then
        raise exception 'League not found';
    end if;

    if league_record.draft_status <> 'live' then
        raise exception 'The draft is not live';
    end if;

    if league_record.current_pick_index <> expected_pick_index then
        raise exception 'This pick has already moved. Refresh the draft room.';
    end if;

    select array_agg(user_id order by draft_order_position nulls last, joined_at asc)
    into member_order
    from public.league_members
    where league_id = target_league_id
    and status = 'active';

    member_count := coalesce(array_length(member_order, 1), 0);

    if member_count < 2 then
        raise exception 'A draft needs at least two members';
    end if;

    position_index := (expected_pick_index % member_count) + 1;

    if league_record.draft_format = 'snake' and ((expected_pick_index / member_count) % 2) = 1 then
        position_index := member_count - position_index + 1;
    end if;

    picker_user_id := member_order[position_index];

    if picker_user_id <> auth.uid() then
        if coalesce(requested_auto_pick, false) is not true then
            raise exception 'It is not your pick';
        end if;

        if not exists (
            select 1
            from public.league_members acting_member
            where acting_member.league_id = target_league_id
            and acting_member.user_id = auth.uid()
            and acting_member.status = 'active'
        ) then
            raise exception 'You are not a member of this league';
        end if;

        if not exists (
            select 1
            from public.league_members picker_member
            where picker_member.league_id = target_league_id
            and picker_member.user_id = picker_user_id
            and picker_member.status = 'active'
            and (
                picker_member.auto_pick_enabled = true
                or now() >= league_record.current_pick_started_at + make_interval(secs => league_record.pick_seconds)
            )
        ) then
            raise exception 'That pick is not ready for auto-pick';
        end if;
    end if;

    if trim(coalesce(requested_artist_name, '')) = '' then
        raise exception 'Choose an artist before making a pick';
    end if;

    if trim(coalesce(requested_roster_slot, '')) = '' then
        raise exception 'Choose a roster slot before making a pick';
    end if;

    normalized_artist_name := lower(regexp_replace(trim(requested_artist_name), '\s+', ' ', 'g'));

    insert into public.artists (
        display_name,
        lastfm_name,
        normalized_name,
        provider_name,
        provider_url,
        image_url,
        listeners,
        playcount,
        fetched_at
    )
    values (
        trim(requested_artist_name),
        trim(requested_artist_name),
        normalized_artist_name,
        coalesce(nullif(trim(requested_provider_name), ''), 'spotify'),
        nullif(trim(coalesce(requested_provider_url, '')), ''),
        nullif(trim(coalesce(requested_image_url, '')), ''),
        greatest(coalesce(requested_listeners, 0), 0),
        greatest(coalesce(requested_playcount, 0), 0),
        now()
    )
    on conflict (normalized_name) do update
    set display_name = coalesce(excluded.display_name, public.artists.display_name),
        lastfm_name = coalesce(nullif(excluded.lastfm_name, ''), public.artists.lastfm_name),
        provider_name = excluded.provider_name,
        provider_url = coalesce(excluded.provider_url, public.artists.provider_url),
        image_url = coalesce(excluded.image_url, public.artists.image_url),
        listeners = greatest(coalesce(excluded.listeners, 0), coalesce(public.artists.listeners, 0)),
        playcount = greatest(coalesce(excluded.playcount, 0), coalesce(public.artists.playcount, 0)),
        fetched_at = now()
    returning id into target_artist_id;

    if exists (
        select 1
        from public.draft_picks
        where league_id = target_league_id
        and artist_id = target_artist_id
        and draft_mode = 'live'
    ) then
        raise exception 'That artist has already been drafted';
    end if;

    if exists (
        select 1
        from public.rosters
        where league_id = target_league_id
        and user_id = picker_user_id
        and roster_slot = requested_roster_slot
        and released_at is null
    ) then
        raise exception 'That roster slot is already filled';
    end if;

    insert into public.draft_picks (
        league_id,
        pick_number,
        user_id,
        artist_id,
        roster_slot,
        draft_mode,
        auto_picked
    )
    values (
        target_league_id,
        expected_pick_index + 1,
        picker_user_id,
        target_artist_id,
        requested_roster_slot,
        'live',
        coalesce(requested_auto_pick, false)
    );

    insert into public.rosters (
        league_id,
        user_id,
        artist_id,
        roster_slot,
        acquisition_value
    )
    values (
        target_league_id,
        picker_user_id,
        target_artist_id,
        requested_roster_slot,
        greatest(coalesce(requested_acquisition_value, 0), 0)
    );

    update public.leagues
    set current_pick_index = current_pick_index + 1,
        current_pick_started_at = now(),
        draft_status = case
            when current_pick_index + 1 >= (
                select count(*) * (
                    league_record.headliner_slots +
                    league_record.rising_slots +
                    league_record.wildcard_slots +
                    league_record.deep_cut_slots +
                    league_record.bench_slots
                )
                from public.league_members
                where league_id = target_league_id
                and status = 'active'
            )
            then 'complete'
            else draft_status
        end
    where id = target_league_id;
end;
$$;

grant execute on function public.make_draft_pick(
    uuid,
    integer,
    text,
    text,
    text,
    text,
    bigint,
    bigint,
    text,
    integer,
    boolean
) to authenticated;

create or replace function public.draft_required_presence(active_count integer)
returns integer
language sql
immutable
as $$
    select least(
        greatest(coalesce(active_count, 0), 1),
        greatest(2, ceiling(coalesce(active_count, 0)::numeric * 0.5)::integer)
    );
$$;

create or replace function public.open_draft_lobby(target_league_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    active_count integer;
    league_record public.leagues%rowtype;
    caller_is_manager boolean;
begin
    select * into league_record
    from public.leagues
    where id = target_league_id
    for update;

    if league_record.id is null then
        raise exception 'League not found';
    end if;

    select exists (
        select 1
        from public.league_members
        where league_id = target_league_id
        and user_id = auth.uid()
        and role = 'manager'
        and status = 'active'
    ) into caller_is_manager;

    if not caller_is_manager and not exists (
        select 1
        from public.league_members
        where league_id = target_league_id
        and user_id = auth.uid()
        and status = 'active'
    ) then
        raise exception 'You are not a member of this league';
    end if;

    if not caller_is_manager and (league_record.draft_starts_at is null or now() < league_record.draft_starts_at) then
        raise exception 'Only the league manager can open the draft lobby';
    end if;

    select count(*) into active_count
    from public.league_members
    where league_id = target_league_id
    and status = 'active';

    if active_count < 2 then
        raise exception 'Invite at least one more member before opening the draft lobby';
    end if;

    delete from public.draft_room_presence
    where league_id = target_league_id;

    update public.leagues
    set draft_status = 'lobby',
        current_pick_index = 0,
        current_pick_started_at = now(),
        lobby_ready_at = null,
        auto_pick_enabled = false
    where id = target_league_id
    and draft_status = 'scheduled';
end;
$$;

grant execute on function public.open_draft_lobby(uuid) to authenticated;

create or replace function public.touch_draft_room_presence(target_league_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
    if not exists (
        select 1
        from public.league_members
        where league_id = target_league_id
        and user_id = auth.uid()
        and status = 'active'
    ) then
        raise exception 'You are not a member of this league';
    end if;

    insert into public.draft_room_presence (league_id, user_id, last_seen_at)
    values (target_league_id, auth.uid(), now())
    on conflict (league_id, user_id) do update
    set last_seen_at = excluded.last_seen_at;
end;
$$;

grant execute on function public.touch_draft_room_presence(uuid) to authenticated;

drop function if exists public.league_draft_presence(uuid);

create or replace function public.league_draft_presence(target_league_id uuid)
returns table(present_count integer, required_count integer, member_count integer, ready_at timestamptz)
language plpgsql
security definer
set search_path = public
as $$
declare
    active_count integer;
    here_count integer;
    needed_count integer;
    lobby_status text;
    current_ready_at timestamptz;
begin
    if not exists (
        select 1 from public.league_members viewer
        where viewer.league_id = target_league_id
        and viewer.user_id = auth.uid()
        and viewer.status = 'active'
    ) then
        return;
    end if;

    select draft_status, lobby_ready_at
    into lobby_status, current_ready_at
    from public.leagues
    where id = target_league_id
    for update;

    select count(*)::integer
    into active_count
    from public.league_members
    where league_id = target_league_id
    and status = 'active';

    select count(*)::integer
    into here_count
    from public.league_members active_members
    join public.draft_room_presence
        on draft_room_presence.league_id = target_league_id
        and draft_room_presence.user_id = active_members.user_id
        and draft_room_presence.last_seen_at > now() - interval '20 seconds'
    where active_members.league_id = target_league_id
    and active_members.status = 'active';

    needed_count := public.draft_required_presence(active_count);

    if lobby_status = 'lobby' then
        if here_count >= needed_count and current_ready_at is null then
            update public.leagues
            set lobby_ready_at = now()
            where id = target_league_id
            returning lobby_ready_at into current_ready_at;
        elsif here_count < needed_count and current_ready_at is not null then
            update public.leagues
            set lobby_ready_at = null
            where id = target_league_id;
            current_ready_at := null;
        end if;
    end if;

    return query select here_count, needed_count, active_count, current_ready_at;
end;
$$;

grant execute on function public.league_draft_presence(uuid) to authenticated;

create or replace function public.delay_draft_lobby(target_league_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    league_record public.leagues%rowtype;
    present_count integer;
    active_count integer;
    required_count integer;
begin
    if not exists (
        select 1
        from public.league_members
        where league_id = target_league_id
        and user_id = auth.uid()
        and status = 'active'
    ) then
        raise exception 'You are not a member of this league';
    end if;

    select * into league_record
    from public.leagues
    where id = target_league_id
    for update;

    if league_record.draft_status <> 'lobby' then
        return;
    end if;

    select count(*) into active_count
    from public.league_members
    where league_id = target_league_id
    and status = 'active';

    required_count := public.draft_required_presence(active_count);

    select count(*) into present_count
    from public.draft_room_presence
    where league_id = target_league_id
    and last_seen_at > now() - interval '20 seconds';

    if present_count >= required_count then
        return;
    end if;

    if now() < league_record.current_pick_started_at + interval '300 seconds' then
        return;
    end if;

    delete from public.draft_room_presence
    where league_id = target_league_id;

    update public.leagues
    set draft_status = 'scheduled',
        draft_starts_at = now() + interval '15 minutes',
        current_pick_started_at = now(),
        lobby_ready_at = null
    where id = target_league_id;
end;
$$;

grant execute on function public.delay_draft_lobby(uuid) to authenticated;

create or replace function public.start_live_draft(target_league_id uuid)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    active_count integer;
    present_count integer;
    required_count integer;
    league_record public.leagues%rowtype;
    caller_is_manager boolean;
begin
    select * into league_record
    from public.leagues
    where id = target_league_id
    for update;

    if league_record.id is null then
        raise exception 'League not found';
    end if;

    select exists (
        select 1
        from public.league_members
        where league_id = target_league_id
        and user_id = auth.uid()
        and role = 'manager'
        and status = 'active'
    ) into caller_is_manager;

    if not caller_is_manager and not exists (
        select 1
        from public.league_members
        where league_id = target_league_id
        and user_id = auth.uid()
        and status = 'active'
    ) then
        raise exception 'You are not a member of this league';
    end if;

    select count(*) into active_count
    from public.league_members
    where league_id = target_league_id
    and status = 'active';

    if active_count < 2 then
        raise exception 'Invite at least one more member before starting the draft';
    end if;

    if league_record.draft_status = 'lobby' then
        required_count := public.draft_required_presence(active_count);

        select count(*) into present_count
        from public.draft_room_presence
        where league_id = target_league_id
        and last_seen_at > now() - interval '20 seconds';

        if present_count < required_count then
            raise exception 'Waiting for more members before starting the draft';
        end if;

        if league_record.lobby_ready_at is null or now() < league_record.lobby_ready_at + interval '30 seconds' then
            raise exception 'The lobby countdown is still running';
        end if;
    elsif not caller_is_manager then
        raise exception 'Only the league manager can start the draft';
    end if;

    delete from public.draft_picks
    where league_id = target_league_id
    and draft_mode = 'live';

    delete from public.rosters
    where league_id = target_league_id;

    delete from public.draft_room_presence
    where league_id = target_league_id;

    update public.league_members
    set auto_pick_enabled = false,
        draft_order_position = shuffled.position_index
    from (
        select user_id, row_number() over (order by random()) as position_index
        from public.league_members
        where league_id = target_league_id
        and status = 'active'
    ) shuffled
    where league_members.league_id = target_league_id
    and league_members.user_id = shuffled.user_id;

    update public.leagues
    set draft_status = 'live',
        current_pick_index = 0,
        current_pick_started_at = now(),
        draft_starts_at = now(),
        lobby_ready_at = null,
        auto_pick_enabled = false
    where id = target_league_id;
end;
$$;

grant execute on function public.start_live_draft(uuid) to authenticated;

create or replace function public.set_my_auto_pick(target_league_id uuid, enabled boolean)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
    update public.league_members
    set auto_pick_enabled = coalesce(enabled, false)
    where league_id = target_league_id
    and user_id = auth.uid()
    and status = 'active';

    if not found then
        raise exception 'You are not a member of this league';
    end if;
end;
$$;

grant execute on function public.set_my_auto_pick(uuid, boolean) to authenticated;

drop function if exists public.league_draft_picks(uuid);

create or replace function public.league_draft_picks(target_league_id uuid)
returns table(
    pick_number integer,
    username text,
    roster_slot text,
    artist_name text,
    image_url text,
    listeners bigint,
    playcount bigint,
    seconds_to_pick integer,
    auto_picked boolean,
    picked_at timestamptz
)
language sql
security definer
set search_path = public
as $$
    with visible_picks as (
        select
            draft_picks.pick_number,
            profiles.username,
            draft_picks.roster_slot,
            coalesce(artists.display_name, artists.normalized_name) as artist_name,
            artists.image_url,
            artists.listeners,
            artists.playcount,
            draft_picks.auto_picked,
            draft_picks.picked_at,
            lag(draft_picks.picked_at) over (order by draft_picks.pick_number asc) as previous_pick_at,
            leagues.draft_starts_at,
            leagues.pick_seconds
        from public.draft_picks
        join public.profiles on profiles.id = draft_picks.user_id
        join public.artists on artists.id = draft_picks.artist_id
        join public.leagues on leagues.id = draft_picks.league_id
        where draft_picks.league_id = target_league_id
        and draft_picks.draft_mode = 'live'
        and exists (
            select 1
            from public.league_members viewer
            where viewer.league_id = target_league_id
            and viewer.user_id = auth.uid()
            and viewer.status = 'active'
        )
    )
    select
        visible_picks.pick_number,
        visible_picks.username,
        visible_picks.roster_slot,
        visible_picks.artist_name,
        visible_picks.image_url,
        visible_picks.listeners,
        visible_picks.playcount,
        least(
            case when visible_picks.auto_picked then 5 else visible_picks.pick_seconds end,
            greatest(
                0,
                extract(
                    epoch from visible_picks.picked_at - coalesce(visible_picks.previous_pick_at, visible_picks.draft_starts_at, visible_picks.picked_at)
                )::integer
            )
        ) as seconds_to_pick,
        visible_picks.auto_picked,
        visible_picks.picked_at
    from visible_picks
    order by visible_picks.pick_number desc;
$$;

grant execute on function public.league_draft_picks(uuid) to authenticated;

drop function if exists public.league_member_list(uuid);

create or replace function public.league_member_list(target_league_id uuid)
returns table(username text, team_name text, role text, joined_at timestamptz, auto_pick_enabled boolean, draft_order_position integer)
language sql
security definer
set search_path = public
as $$
    select profiles.username, league_members.team_name, league_members.role, league_members.joined_at, league_members.auto_pick_enabled, league_members.draft_order_position
    from public.league_members
    join public.profiles on profiles.id = league_members.user_id
    where league_members.league_id = target_league_id
    and league_members.status = 'active'
    and exists (
        select 1
        from public.league_members viewer
        where viewer.league_id = target_league_id
        and viewer.user_id = auth.uid()
        and viewer.status = 'active'
    )
    order by league_members.draft_order_position nulls last, league_members.joined_at asc;
$$;

grant execute on function public.league_member_list(uuid) to authenticated;

create or replace function public.create_league_with_manager(requested_name text, requested_invite_code text, requested_team_name text)
returns uuid
language plpgsql
security definer
set search_path = public
as $$
declare
    created_league_id uuid;
begin
    if auth.uid() is null then
        raise exception 'Authentication required';
    end if;

    if upper(trim(requested_invite_code)) !~ '^[A-Z2-9]{6}$' then
        raise exception 'Invite codes must be exactly 6 characters';
    end if;

    insert into public.leagues (owner_id, name, invite_code)
    values (auth.uid(), trim(requested_name), upper(trim(requested_invite_code)))
    returning id into created_league_id;

    insert into public.league_members (league_id, user_id, team_name, role, status)
    values (created_league_id, auth.uid(), requested_team_name, 'manager', 'active');

    return created_league_id;
end;
$$;

grant execute on function public.create_league_with_manager(text, text, text) to authenticated;

drop function if exists public.my_leagues();

create or replace function public.my_leagues()
returns table(
    id uuid,
    owner_id uuid,
    name text,
    invite_code text,
    salary_cap integer,
    draft_format text,
    draft_starts_at timestamptz,
    pick_seconds integer,
    season_weeks integer,
    headliner_slots integer,
    rising_slots integer,
    wildcard_slots integer,
    deep_cut_slots integer,
    bench_slots integer,
    max_waiver_claims integer,
    settings_lock_hours integer,
    manager_approval_required boolean,
    invites_open boolean,
    max_members integer,
    draft_status text,
    current_pick_index integer,
    current_pick_started_at timestamptz,
    lobby_ready_at timestamptz,
    auto_pick_enabled boolean,
    created_at timestamptz,
    is_manager boolean,
    member_count integer
)
language sql
security definer
set search_path = public
as $$
    select
        leagues.id,
        leagues.owner_id,
        leagues.name,
        leagues.invite_code,
        leagues.salary_cap,
        leagues.draft_format,
        leagues.draft_starts_at,
        leagues.pick_seconds,
        leagues.season_weeks,
        leagues.headliner_slots,
        leagues.rising_slots,
        leagues.wildcard_slots,
        leagues.deep_cut_slots,
        leagues.bench_slots,
        leagues.max_waiver_claims,
        leagues.settings_lock_hours,
        leagues.manager_approval_required,
        leagues.invites_open,
        leagues.max_members,
        leagues.draft_status,
        leagues.current_pick_index,
        leagues.current_pick_started_at,
        leagues.lobby_ready_at,
        leagues.auto_pick_enabled,
        leagues.created_at,
        viewer.role = 'manager' as is_manager,
        (
            select count(*)::integer
            from public.league_members active_members
            where active_members.league_id = leagues.id
            and active_members.status = 'active'
        ) as member_count
    from public.league_members viewer
    join public.leagues on leagues.id = viewer.league_id
    where viewer.user_id = auth.uid()
    and viewer.status = 'active'
    order by leagues.created_at desc;
$$;

grant execute on function public.my_leagues() to authenticated;

drop policy if exists "Members can read setting votes" on public.league_setting_votes;
create policy "Members can read setting votes"
on public.league_setting_votes for select
using (
    exists (
        select 1 from public.league_members
        where league_members.league_id = league_setting_votes.league_id
        and league_members.user_id = auth.uid()
        and league_members.status = 'active'
    )
);

drop policy if exists "League managers can create setting votes" on public.league_setting_votes;
create policy "League managers can create setting votes"
on public.league_setting_votes for insert
with check (
    exists (
        select 1 from public.league_members
        where league_members.league_id = league_setting_votes.league_id
        and league_members.user_id = auth.uid()
        and league_members.role = 'manager'
        and league_members.status = 'active'
    )
);

drop policy if exists "Members can respond to setting votes" on public.league_setting_vote_responses;
create policy "Members can respond to setting votes"
on public.league_setting_vote_responses for insert
with check (auth.uid() = user_id);

drop policy if exists "Members can read setting vote responses" on public.league_setting_vote_responses;
create policy "Members can read setting vote responses"
on public.league_setting_vote_responses for select
using (
    exists (
        select 1
        from public.league_setting_votes votes
        join public.league_members members on members.league_id = votes.league_id
        where votes.id = league_setting_vote_responses.vote_id
        and members.user_id = auth.uid()
        and members.status = 'active'
    )
);

drop policy if exists "Members can read moderation history" on public.league_moderation_actions;
create policy "Members can read moderation history"
on public.league_moderation_actions for select
using (
    exists (
        select 1 from public.league_members
        where league_members.league_id = league_moderation_actions.league_id
        and league_members.user_id = auth.uid()
        and league_members.status = 'active'
    )
);

drop policy if exists "League managers can create moderation actions" on public.league_moderation_actions;
create policy "League managers can create moderation actions"
on public.league_moderation_actions for insert
with check (
    exists (
        select 1 from public.league_members
        where league_members.league_id = league_moderation_actions.league_id
        and league_members.user_id = auth.uid()
        and league_members.role = 'manager'
        and league_members.status = 'active'
    )
);

drop policy if exists "Authenticated users can read artist market" on public.artists;
create policy "Authenticated users can read artist market"
on public.artists for select
to authenticated
using (true);

drop policy if exists "Users can read rosters in their leagues" on public.rosters;
create policy "Users can read rosters in their leagues"
on public.rosters for select
using (
    exists (
        select 1 from public.league_members
        where league_members.league_id = rosters.league_id
        and league_members.user_id = auth.uid()
    )
);

drop policy if exists "Users can manage their own roster" on public.rosters;
create policy "Users can manage their own roster"
on public.rosters for all
using (auth.uid() = user_id)
with check (auth.uid() = user_id);

drop policy if exists "Members can read waiver claims in their leagues" on public.waiver_claims;
create policy "Members can read waiver claims in their leagues"
on public.waiver_claims for select
using (
    exists (
        select 1 from public.league_members
        where league_members.league_id = waiver_claims.league_id
        and league_members.user_id = auth.uid()
        and league_members.status = 'active'
    )
);

drop policy if exists "Users can manage their waiver claims" on public.waiver_claims;
create policy "Users can manage their waiver claims"
on public.waiver_claims for all
using (auth.uid() = user_id)
with check (auth.uid() = user_id);

drop policy if exists "Members can read draft picks" on public.draft_picks;
create policy "Members can read draft picks"
on public.draft_picks for select
using (
    exists (
        select 1 from public.league_members
        where league_members.league_id = draft_picks.league_id
        and league_members.user_id = auth.uid()
        and league_members.status = 'active'
    )
);

drop policy if exists "Members can make their own draft picks" on public.draft_picks;
create policy "Members can make their own draft picks"
on public.draft_picks for insert
with check (auth.uid() = user_id);

drop policy if exists "Users can read their notifications" on public.notifications;
create policy "Users can read their notifications"
on public.notifications for select
using (auth.uid() = user_id);

drop policy if exists "Users can update their notifications" on public.notifications;
create policy "Users can update their notifications"
on public.notifications for update
using (auth.uid() = user_id);

create or replace function public.upsert_profile(requested_email text, requested_username text, requested_display_name text, requested_mailing_list boolean)
returns void
language plpgsql
security definer
set search_path = public
as $$
begin
    if auth.uid() is null then
        raise exception 'Authentication required';
    end if;

    if trim(requested_username) !~ '^[A-Za-z0-9_]{3,24}$' then
        raise exception 'Username must be 3-24 letters, numbers, or underscores.';
    end if;

    if exists (
        select 1
        from public.profiles
        where lower(username) = lower(trim(requested_username))
        and id <> auth.uid()
    ) then
        raise exception 'That username is already taken.';
    end if;

    insert into public.profiles (id, email, username, display_name, mailing_list_opt_in)
    values (auth.uid(), lower(trim(requested_email)), trim(requested_username), trim(requested_display_name), requested_mailing_list)
    on conflict (id) do update set
        email = excluded.email,
        username = excluded.username,
        display_name = excluded.display_name,
        mailing_list_opt_in = excluded.mailing_list_opt_in;
end;
$$;

grant execute on function public.upsert_profile(text, text, text, boolean) to authenticated;

create or replace function public.email_for_username(requested_username text)
returns text
language sql
security definer
set search_path = public
as $$
    select email
    from public.profiles
    where lower(username) = lower(trim(requested_username))
    limit 1;
$$;

grant execute on function public.email_for_username(text) to anon, authenticated;

create or replace function public.username_available(requested_username text)
returns boolean
language sql
security definer
set search_path = public
as $$
    select trim(requested_username) ~ '^[A-Za-z0-9_]{3,24}$'
    and not exists (
        select 1
        from public.profiles
        where lower(username) = lower(trim(requested_username))
    );
$$;

grant execute on function public.username_available(text) to anon, authenticated;

create or replace function public.username_available_for_account(requested_username text, current_user_id uuid)
returns boolean
language sql
security definer
set search_path = public
as $$
    select trim(requested_username) ~ '^[A-Za-z0-9_]{3,24}$'
    and not exists (
        select 1
        from public.profiles
        where lower(username) = lower(trim(requested_username))
        and id <> current_user_id
    );
$$;

grant execute on function public.username_available_for_account(text, uuid) to anon, authenticated;

create or replace function public.email_available(requested_email text)
returns boolean
language sql
security definer
set search_path = public
as $$
    select char_length(trim(requested_email)) between 5 and 254
    and trim(requested_email) !~ '[[:space:]]'
    and length(trim(requested_email)) - length(replace(trim(requested_email), '@', '')) = 1
    and position('@' in trim(requested_email)) > 1
    and split_part(trim(requested_email), '@', 2) like '%.%'
    and not exists (
        select 1
        from public.profiles
        where lower(email) = lower(trim(requested_email))
    )
    and not exists (
        select 1
        from auth.users
        where lower(email) = lower(trim(requested_email))
    );
$$;

grant execute on function public.email_available(text) to anon, authenticated;

create or replace function public.delete_my_account()
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    current_user_id uuid := auth.uid();
begin
    if current_user_id is null then
        raise exception 'Authentication required';
    end if;

    delete from auth.users
    where id = current_user_id;
end;
$$;

grant execute on function public.delete_my_account() to authenticated;

create or replace function public.league_mailing_list(target_league_id uuid)
returns table(email text, username text, display_name text)
language sql
security definer
set search_path = public
as $$
    select profiles.email, profiles.username, profiles.display_name
    from public.profiles
    join public.league_members on league_members.user_id = profiles.id
    where league_members.league_id = target_league_id
    and league_members.status = 'active'
    and profiles.mailing_list_opt_in = true
    and exists (
        select 1 from public.league_members manager
        where manager.league_id = target_league_id
        and manager.user_id = auth.uid()
        and manager.role = 'manager'
        and manager.status = 'active'
    );
$$;

grant execute on function public.league_mailing_list(uuid) to authenticated;

create index if not exists league_members_user_id_idx on public.league_members(user_id);
create index if not exists rosters_league_user_idx on public.rosters(league_id, user_id);
create index if not exists league_setting_votes_league_idx on public.league_setting_votes(league_id);
create index if not exists league_moderation_actions_league_idx on public.league_moderation_actions(league_id);
create index if not exists draft_picks_league_idx on public.draft_picks(league_id, pick_number);
create index if not exists notifications_user_idx on public.notifications(user_id, read_at);
