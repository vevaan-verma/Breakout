create extension if not exists pg_cron with schema extensions;
create extension if not exists pg_net with schema extensions;

create table if not exists public.artist_daily_listener_snapshots (
    id uuid primary key default gen_random_uuid(),
    normalized_name text not null,
    name text not null,
    snapshot_date date not null,
    listeners bigint,
    source text,
    raw_data jsonb not null default '{}'::jsonb,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (normalized_name, snapshot_date)
);

create index if not exists artist_daily_listener_snapshots_date_idx
on public.artist_daily_listener_snapshots (snapshot_date desc);

create index if not exists artist_daily_listener_snapshots_name_date_idx
on public.artist_daily_listener_snapshots (normalized_name, snapshot_date desc);

alter table public.artist_daily_listener_snapshots enable row level security;

drop policy if exists "Anyone can read artist daily listener snapshots" on public.artist_daily_listener_snapshots;
create policy "Anyone can read artist daily listener snapshots"
on public.artist_daily_listener_snapshots for select
to anon, authenticated
using (true);

create table if not exists public.artist_weekly_projection_freezes (
    id uuid primary key default gen_random_uuid(),
    normalized_name text not null,
    name text not null,
    week_start date not null,
    projected_points numeric not null,
    formula_version text not null default 'balanced-v1',
    listeners bigint,
    role text,
    source text,
    raw_data jsonb not null default '{}'::jsonb,
    frozen_at timestamptz not null default now(),
    unique (normalized_name, week_start, formula_version)
);

create index if not exists artist_weekly_projection_freezes_week_idx
on public.artist_weekly_projection_freezes (week_start desc);

alter table public.artist_weekly_projection_freezes enable row level security;

drop policy if exists "Anyone can read artist weekly projection freezes" on public.artist_weekly_projection_freezes;
create policy "Anyone can read artist weekly projection freezes"
on public.artist_weekly_projection_freezes for select
to anon, authenticated
using (true);

create table if not exists public.artist_daily_scores (
    id uuid primary key default gen_random_uuid(),
    normalized_name text not null,
    name text not null,
    week_start date not null,
    score_date date not null,
    actual_points numeric not null,
    projected_points numeric,
    formula_version text not null default 'balanced-v1',
    listeners bigint,
    listener_delta bigint,
    listener_growth_percent numeric,
    role text,
    source text,
    raw_data jsonb not null default '{}'::jsonb,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (normalized_name, week_start, score_date, formula_version)
);

create index if not exists artist_daily_scores_week_date_idx
on public.artist_daily_scores (week_start desc, score_date desc);

create index if not exists artist_daily_scores_name_week_idx
on public.artist_daily_scores (normalized_name, week_start desc);

alter table public.artist_daily_scores enable row level security;

drop policy if exists "Anyone can read artist daily scores" on public.artist_daily_scores;
create policy "Anyone can read artist daily scores"
on public.artist_daily_scores for select
to anon, authenticated
using (true);

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
    formula_version text
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
        scores.formula_version
    from public.artist_daily_scores scores
    where trim(coalesce(search_query, '')) = ''
       or scores.normalized_name like '%' || lower(regexp_replace(trim(search_query), '\s+', '', 'g')) || '%'
       or lower(scores.name) like '%' || lower(trim(search_query)) || '%'
    order by scores.normalized_name, scores.week_start desc, scores.score_date desc;
$$;

grant execute on function public.artist_scores_cached(text) to anon, authenticated;

select cron.unschedule(jobid)
from cron.job
where jobname in (
    'freeze-artist-weekly-projections',
    'score-artists-daily',
    'score-artists-catchup-tonight'
);

select cron.schedule(
    'freeze-artist-weekly-projections',
    '15 8 * * 1',
    $$
    select net.http_post(
        url := (
            select decrypted_secret
            from vault.decrypted_secrets
            where name = 'project_url'
        ) || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object(
            'Content-Type', 'application/json',
            'x-refresh-secret', (
                select decrypted_secret
                from vault.decrypted_secrets
                where name = 'market_refresh_secret'
            )
        ),
        body := jsonb_build_object(
            'mode', 'freeze-projections',
            'limit', 10000
        )
    ) as request_id;
    $$
);

select cron.schedule(
    'score-artists-daily',
    '30 8 * * *',
    $$
    select net.http_post(
        url := (
            select decrypted_secret
            from vault.decrypted_secrets
            where name = 'project_url'
        ) || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object(
            'Content-Type', 'application/json',
            'x-refresh-secret', (
                select decrypted_secret
                from vault.decrypted_secrets
                where name = 'market_refresh_secret'
            )
        ),
        body := jsonb_build_object(
            'mode', 'score-daily',
            'limit', 10000
        )
    ) as request_id;
    $$
);

select cron.schedule(
    'score-artists-catchup-tonight',
    '*/20 * * * *',
    $$
    select net.http_post(
        url := (
            select decrypted_secret
            from vault.decrypted_secrets
            where name = 'project_url'
        ) || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object(
            'Content-Type', 'application/json',
            'x-refresh-secret', (
                select decrypted_secret
                from vault.decrypted_secrets
                where name = 'market_refresh_secret'
            )
        ),
        body := jsonb_build_object(
            'mode', 'score-daily',
            'limit', 10000,
            'freezeMissingProjections', true
        )
    ) as request_id;
    $$
);
