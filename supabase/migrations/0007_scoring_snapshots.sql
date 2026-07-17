create extension if not exists pg_cron with schema extensions;
create extension if not exists pg_net with schema extensions;

create table if not exists public.artist_weekly_metrics (
    id uuid primary key default gen_random_uuid(),
    spotify_id text,
    normalized_name text not null,
    name text not null,
    week_start date not null,
    role text not null,
    listeners bigint,
    weekly_listener_gain bigint,
    weekly_listener_growth_percent numeric,
    daily_listener_change bigint,
    kworb_rank integer,
    source text,
    raw_data jsonb not null default '{}'::jsonb,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (normalized_name, week_start)
);

create index if not exists artist_weekly_metrics_week_idx
on public.artist_weekly_metrics (week_start desc);

create index if not exists artist_weekly_metrics_role_week_idx
on public.artist_weekly_metrics (role, week_start desc);

create index if not exists artist_weekly_metrics_growth_idx
on public.artist_weekly_metrics (weekly_listener_growth_percent desc nulls last);

alter table public.artist_weekly_metrics enable row level security;

drop policy if exists "Anyone can read artist weekly metrics" on public.artist_weekly_metrics;
create policy "Anyone can read artist weekly metrics"
on public.artist_weekly_metrics for select
to anon, authenticated
using (true);

create table if not exists public.scoring_formula_trials (
    id uuid primary key default gen_random_uuid(),
    formula_version text not null,
    sample_size integer not null default 0,
    result jsonb not null default '{}'::jsonb,
    created_at timestamptz not null default now()
);

create index if not exists scoring_formula_trials_created_idx
on public.scoring_formula_trials (created_at desc);

alter table public.scoring_formula_trials enable row level security;

drop policy if exists "Anyone can read scoring formula trials" on public.scoring_formula_trials;
create policy "Anyone can read scoring formula trials"
on public.scoring_formula_trials for select
to anon, authenticated
using (true);

select cron.unschedule(jobid)
from cron.job
where jobname = 'snapshot-market-weekly-metrics';

select cron.schedule(
    'snapshot-market-weekly-metrics',
    '45 2 * * *',
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
            'mode', 'snapshot-week',
            'limit', 10000
        )
    ) as request_id;
    $$
);
