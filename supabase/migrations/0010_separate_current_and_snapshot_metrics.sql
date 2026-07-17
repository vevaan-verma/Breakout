alter table public.market_artist_cache
add column if not exists current_listeners bigint;

alter table public.market_artist_cache
add column if not exists current_listeners_observed_at timestamptz;

alter table public.market_artist_cache
add column if not exists current_listeners_source text;

alter table public.market_artist_cache
add column if not exists snapshot_listeners bigint;

alter table public.market_artist_cache
add column if not exists snapshot_previous_listeners bigint;

alter table public.market_artist_cache
add column if not exists snapshot_date date;

alter table public.market_artist_cache
add column if not exists listener_change_since_snapshot bigint;

alter table public.market_artist_cache
add column if not exists listener_change_since_snapshot_percent numeric;

alter table public.market_artist_cache
add column if not exists days_since_snapshot integer;

update public.market_artist_cache
set
    current_listeners = coalesce(current_listeners, listeners),
    current_listeners_observed_at = coalesce(current_listeners_observed_at, updated_at),
    current_listeners_source = coalesce(current_listeners_source, source),
    snapshot_listeners = coalesce(snapshot_listeners, listeners),
    snapshot_date = coalesce(snapshot_date, data_date),
    listener_change_since_snapshot = case
        when current_listeners is not null and snapshot_listeners is not null then current_listeners - snapshot_listeners
        when listeners is not null and snapshot_listeners is not null then listeners - snapshot_listeners
        else listener_change_since_snapshot
    end,
    listener_change_since_snapshot_percent = case
        when coalesce(snapshot_listeners, 0) > 0 then
            ((coalesce(current_listeners, listeners) - snapshot_listeners)::numeric / snapshot_listeners::numeric) * 100
        else listener_change_since_snapshot_percent
    end,
    days_since_snapshot = case
        when coalesce(snapshot_date, data_date) is not null then greatest(0, current_date - coalesce(snapshot_date, data_date))
        else days_since_snapshot
    end;

alter table public.artist_weekly_metrics
add column if not exists snapshot_date date;

alter table public.artist_weekly_metrics
add column if not exists snapshot_listeners bigint;

alter table public.artist_weekly_metrics
add column if not exists previous_snapshot_listeners bigint;

alter table public.artist_weekly_metrics
add column if not exists top_city jsonb;

alter table public.artist_weekly_metrics
add column if not exists fetched_at timestamptz not null default now();

update public.artist_weekly_metrics
set
    snapshot_date = coalesce(snapshot_date, week_start + 6),
    snapshot_listeners = coalesce(snapshot_listeners, listeners),
    previous_snapshot_listeners = coalesce(previous_snapshot_listeners, listeners - weekly_listener_gain)
where snapshot_date is null
or snapshot_listeners is null
or previous_snapshot_listeners is null;

create table if not exists public.pastspot_snapshot_state (
    id boolean primary key default true,
    latest_seen_date date,
    first_seen_at timestamptz,
    processed_date date,
    processed_at timestamptz,
    last_checked_at timestamptz,
    check_payload jsonb not null default '{}'::jsonb,
    constraint pastspot_snapshot_state_singleton check (id)
);

insert into public.pastspot_snapshot_state (id)
values (true)
on conflict (id) do nothing;

create table if not exists public.weekly_track_gainers (
    id uuid primary key default gen_random_uuid(),
    snapshot_date date not null,
    rank integer not null,
    spotify_id text,
    artist_name text,
    normalized_artist_name text,
    track_id text,
    track_name text not null,
    raw_gain text,
    gain_value numeric,
    growth_percent numeric,
    source text not null default 'Pastspot',
    raw_data jsonb not null default '{}'::jsonb,
    fetched_at timestamptz not null default now(),
    unique (snapshot_date, rank)
);

alter table public.weekly_track_gainers enable row level security;

drop policy if exists "Anyone can read weekly track gainers" on public.weekly_track_gainers;
create policy "Anyone can read weekly track gainers"
on public.weekly_track_gainers for select
to anon, authenticated
using (true);

drop function if exists public.market_artists_cached(text);
create or replace function public.market_artists_cached(search_query text default '')
returns table (
    spotify_id text,
    name text,
    image_url text,
    image_url_card text,
    image_url_full text,
    current_listeners bigint,
    current_listeners_observed_at timestamptz,
    current_listeners_source text,
    snapshot_listeners bigint,
    snapshot_previous_listeners bigint,
    snapshot_date date,
    listener_change_since_snapshot bigint,
    listener_change_since_snapshot_percent numeric,
    days_since_snapshot integer,
    listeners bigint,
    weekly_listener_gain bigint,
    weekly_listener_growth_percent numeric,
    monthly_listener_change_percent numeric,
    daily_listener_change bigint,
    kworb_rank integer,
    source text,
    score_status text,
    provider_url text,
    data_date date,
    listener_history jsonb,
    top_tracks jsonb,
    discography jsonb,
    top_cities jsonb,
    updated_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
    select
        mac.spotify_id,
        mac.name,
        mac.image_url,
        mac.image_url_card,
        mac.image_url_full,
        coalesce(mac.current_listeners, mac.listeners) as current_listeners,
        mac.current_listeners_observed_at,
        mac.current_listeners_source,
        coalesce(mac.snapshot_listeners, mac.listeners) as snapshot_listeners,
        mac.snapshot_previous_listeners,
        coalesce(mac.snapshot_date, mac.data_date) as snapshot_date,
        mac.listener_change_since_snapshot,
        mac.listener_change_since_snapshot_percent,
        mac.days_since_snapshot,
        coalesce(mac.current_listeners, mac.listeners) as listeners,
        mac.weekly_listener_gain,
        mac.weekly_listener_growth_percent,
        mac.monthly_listener_change_percent,
        mac.daily_listener_change,
        mac.kworb_rank,
        mac.source,
        mac.score_status,
        mac.provider_url,
        mac.data_date,
        mac.listener_history,
        mac.top_tracks,
        mac.discography,
        mac.top_cities,
        mac.updated_at
    from public.market_artist_cache mac
    where mac.updated_at >= now() - interval '14 days'
    and (
        trim(coalesce(search_query, '')) = ''
        or mac.normalized_name like '%' || lower(regexp_replace(trim(search_query), '\s+', ' ', 'g')) || '%'
        or lower(mac.name) like '%' || lower(trim(search_query)) || '%'
    )
    order by
        case
            when trim(coalesce(search_query, '')) = '' then 0
            when mac.normalized_name = lower(regexp_replace(trim(search_query), '\s+', ' ', 'g')) then 0
            when mac.normalized_name like lower(regexp_replace(trim(search_query), '\s+', ' ', 'g')) || '%' then 1
            else 2
        end,
        coalesce(mac.weekly_listener_growth_percent, -999999) desc,
        coalesce(mac.weekly_listener_gain, -999999999999) desc,
        coalesce(mac.current_listeners, mac.listeners, 0) desc,
        mac.name asc
    limit 10000;
$$;

grant execute on function public.market_artists_cached(text) to anon, authenticated;
