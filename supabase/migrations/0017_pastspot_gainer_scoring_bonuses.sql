create table if not exists public.weekly_artist_gainers (
    id uuid primary key default gen_random_uuid(),
    snapshot_date date not null,
    week_start date not null,
    rank integer not null,
    spotify_artist_id text,
    artist_name text not null,
    normalized_artist_name text not null,
    raw_gain text,
    gain_value numeric,
    growth_percent numeric,
    source_url text not null default 'https://pastspot.com/gainers',
    raw_data jsonb not null default '{}'::jsonb,
    fetched_at timestamptz not null default now()
);

create unique index if not exists weekly_artist_gainers_snapshot_artist_uidx
on public.weekly_artist_gainers (snapshot_date, normalized_artist_name);

create unique index if not exists weekly_artist_gainers_snapshot_rank_uidx
on public.weekly_artist_gainers (snapshot_date, rank);

alter table public.weekly_artist_gainers enable row level security;

drop policy if exists "Anyone can read weekly artist gainers" on public.weekly_artist_gainers;
create policy "Anyone can read weekly artist gainers"
on public.weekly_artist_gainers for select
to anon, authenticated
using (true);

alter table public.weekly_track_gainers
add column if not exists week_start date,
add column if not exists spotify_track_id text,
add column if not exists spotify_artist_id text,
add column if not exists source_url text not null default 'https://pastspot.com/gainers';

update public.weekly_track_gainers
set week_start = date_trunc('week', snapshot_date)::date
where week_start is null;

alter table public.weekly_track_gainers
alter column week_start set not null;

create unique index if not exists weekly_track_gainers_snapshot_track_uidx
on public.weekly_track_gainers (
    snapshot_date,
    coalesce(spotify_track_id, track_id, lower(regexp_replace(track_name || ':' || coalesce(normalized_artist_name, artist_name, ''), '\s+', '', 'g')))
);

create unique index if not exists weekly_track_gainers_snapshot_spotify_track_uidx
on public.weekly_track_gainers (snapshot_date, spotify_track_id)
where spotify_track_id is not null;

alter table public.artist_weekly_projection_freezes
add column if not exists artist_gainer_bonus numeric not null default 0,
add column if not exists track_gainer_bonus numeric not null default 0,
add column if not exists pastspot_bonus_total numeric not null default 0,
add column if not exists qualifying_track_count integer not null default 0,
add column if not exists best_artist_rank integer,
add column if not exists best_track_rank integer;

alter table public.artist_daily_scores
add column if not exists artist_gainer_bonus numeric not null default 0,
add column if not exists track_gainer_bonus numeric not null default 0,
add column if not exists pastspot_bonus_total numeric not null default 0,
add column if not exists qualifying_track_count integer not null default 0,
add column if not exists best_artist_rank integer,
add column if not exists best_track_rank integer;

drop function if exists public.artist_scores_cached(text);

create or replace function public.artist_scores_cached(search_query text default '')
returns table (
    normalized_name text,
    name text,
    week_start date,
    score_date date,
    actual_points numeric,
    projected_points numeric,
    listeners bigint,
    listener_delta bigint,
    listener_growth_percent numeric,
    role text,
    formula_version text,
    artist_gainer_bonus numeric,
    track_gainer_bonus numeric,
    pastspot_bonus_total numeric,
    qualifying_track_count integer,
    best_artist_rank integer,
    best_track_rank integer
)
language sql
stable
security definer
set search_path = public
as $$
    select distinct on (scores.normalized_name)
        scores.normalized_name,
        scores.name,
        scores.week_start,
        scores.score_date,
        scores.actual_points,
        scores.projected_points,
        scores.listeners,
        scores.listener_delta,
        scores.listener_growth_percent,
        scores.role,
        scores.formula_version,
        scores.artist_gainer_bonus,
        scores.track_gainer_bonus,
        scores.pastspot_bonus_total,
        scores.qualifying_track_count,
        scores.best_artist_rank,
        scores.best_track_rank
    from public.artist_daily_scores scores
    where trim(coalesce(search_query, '')) = ''
       or scores.normalized_name like '%' || lower(regexp_replace(trim(search_query), '\s+', '', 'g')) || '%'
       or lower(scores.name) like '%' || lower(trim(search_query)) || '%'
    order by scores.normalized_name, scores.week_start desc, scores.score_date desc;
$$;

grant execute on function public.artist_scores_cached(text) to anon, authenticated;
