create table if not exists public.market_artist_cache (
    id uuid primary key default gen_random_uuid(),
    spotify_id text,
    name text not null,
    normalized_name text not null unique,
    image_url text,
    listeners bigint,
    weekly_listener_gain bigint,
    weekly_listener_growth_percent numeric,
    monthly_listener_change_percent numeric,
    daily_listener_change bigint,
    kworb_rank integer,
    source text not null default 'Market cache',
    score_status text not null default 'Server refreshed',
    provider_url text,
    data_date date,
    listener_history jsonb not null default '[]'::jsonb,
    top_tracks jsonb not null default '[]'::jsonb,
    discography jsonb not null default '[]'::jsonb,
    top_cities jsonb not null default '[]'::jsonb,
    updated_at timestamptz not null default now()
);

alter table public.market_artist_cache add column if not exists spotify_id text;
alter table public.market_artist_cache add column if not exists image_url text;
alter table public.market_artist_cache add column if not exists listeners bigint;
alter table public.market_artist_cache add column if not exists weekly_listener_gain bigint;
alter table public.market_artist_cache add column if not exists weekly_listener_growth_percent numeric;
alter table public.market_artist_cache add column if not exists monthly_listener_change_percent numeric;
alter table public.market_artist_cache add column if not exists daily_listener_change bigint;
alter table public.market_artist_cache add column if not exists kworb_rank integer;
alter table public.market_artist_cache add column if not exists source text not null default 'Market cache';
alter table public.market_artist_cache add column if not exists score_status text not null default 'Server refreshed';
alter table public.market_artist_cache add column if not exists provider_url text;
alter table public.market_artist_cache add column if not exists data_date date;
alter table public.market_artist_cache add column if not exists listener_history jsonb not null default '[]'::jsonb;
alter table public.market_artist_cache add column if not exists top_tracks jsonb not null default '[]'::jsonb;
alter table public.market_artist_cache add column if not exists discography jsonb not null default '[]'::jsonb;
alter table public.market_artist_cache add column if not exists top_cities jsonb not null default '[]'::jsonb;
alter table public.market_artist_cache add column if not exists updated_at timestamptz not null default now();

create index if not exists market_artist_cache_updated_idx on public.market_artist_cache (updated_at desc);
create index if not exists market_artist_cache_listeners_idx on public.market_artist_cache (listeners desc nulls last);
create index if not exists market_artist_cache_weekly_growth_idx on public.market_artist_cache (weekly_listener_growth_percent desc nulls last);

alter table public.market_artist_cache enable row level security;

drop policy if exists "Anyone can read cached market artists" on public.market_artist_cache;
create policy "Anyone can read cached market artists"
on public.market_artist_cache for select
to anon, authenticated
using (true);

drop function if exists public.market_artists_cached();
drop function if exists public.market_artists_cached(text);
create or replace function public.market_artists_cached(search_query text default '')
returns table (
    spotify_id text,
    name text,
    image_url text,
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
        mac.listeners,
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
        coalesce(mac.listeners, 0) desc,
        mac.name asc
    limit 10000;
$$;

grant execute on function public.market_artists_cached(text) to anon, authenticated;
