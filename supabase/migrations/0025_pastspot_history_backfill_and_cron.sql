create extension if not exists pg_cron with schema extensions;
create extension if not exists pg_net with schema extensions;

alter table public.market_artist_cache
    add column if not exists history_backfilled_at timestamptz,
    add column if not exists history_earliest_date date,
    add column if not exists history_latest_date date,
    add column if not exists history_sample_count integer not null default 0,
    add column if not exists history_status text,
    add column if not exists history_error text;

create index if not exists market_artist_cache_history_status_idx
    on public.market_artist_cache (history_status, history_backfilled_at);

create index if not exists market_artist_cache_history_sample_count_idx
    on public.market_artist_cache (history_sample_count desc);

comment on column public.market_artist_cache.history_status is
    'Pastspot historical backfill state: complete, partial, no_periods, missing_spotify_id, rate_limited, or failed.';

drop function if exists public.market_metric_coverage_diagnostics();

create function public.market_metric_coverage_diagnostics()
returns table (
    total_artists bigint,
    with_current_listeners bigint,
    with_current_observed_at bigint,
    with_snapshot bigint,
    with_previous_snapshot bigint,
    with_weekly_gain bigint,
    with_weekly_growth bigint,
    with_since_snapshot_change bigint,
    with_growth_spike bigint,
    insufficient_growth_history bigint,
    history_complete bigint,
    history_partial bigint,
    history_failed bigint,
    average_history_samples numeric
)
language sql
security definer
set search_path = public
as $$
    select
        count(*)::bigint,
        count(*) filter (where current_listeners is not null)::bigint,
        count(*) filter (where current_listeners_observed_at is not null)::bigint,
        count(*) filter (where snapshot_listeners is not null)::bigint,
        count(*) filter (where snapshot_previous_listeners is not null)::bigint,
        count(*) filter (where weekly_listener_gain is not null)::bigint,
        count(*) filter (where weekly_listener_growth_percent is not null)::bigint,
        count(*) filter (where listener_change_since_snapshot is not null)::bigint,
        count(*) filter (where growth_spike_score is not null)::bigint,
        count(*) filter (where growth_spike_status = 'insufficient_history')::bigint,
        count(*) filter (where history_status = 'complete')::bigint,
        count(*) filter (where history_status = 'partial')::bigint,
        count(*) filter (where history_status in ('failed', 'rate_limited'))::bigint,
        round(avg(history_sample_count), 2)
    from public.market_artist_cache;
$$;

grant execute on function public.market_metric_coverage_diagnostics() to authenticated;

-- Begin conservatively. Increase only after checking Edge Function logs for
-- 403, 429, timeout, and Cloudflare challenge responses.
select cron.unschedule(jobid)
from cron.job
where jobname like 'backfill-pastspot-history-%';

select cron.schedule(
    'backfill-pastspot-history-0000',
    '25 1 * * *',
    $cron$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object(
            'Content-Type', 'application/json',
            'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')
        ),
        body := jsonb_build_object(
            'mode', 'backfill-pastspot-history',
            'offset', 0,
            'limit', 25,
            'concurrency', 4,
            'force', false
        ),
        timeout_milliseconds := 120000
    ) as request_id;
    $cron$
);

select cron.schedule(
    'backfill-pastspot-history-0025',
    '35 1 * * *',
    $cron$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object(
            'Content-Type', 'application/json',
            'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')
        ),
        body := jsonb_build_object(
            'mode', 'backfill-pastspot-history',
            'offset', 25,
            'limit', 25,
            'concurrency', 4,
            'force', false
        ),
        timeout_milliseconds := 120000
    ) as request_id;
    $cron$
);

select cron.schedule(
    'backfill-pastspot-history-0050',
    '45 1 * * *',
    $cron$
    select net.http_post(
        url := (select decrypted_secret from vault.decrypted_secrets where name = 'project_url') || '/functions/v1/refresh-market-cache',
        headers := jsonb_build_object(
            'Content-Type', 'application/json',
            'x-refresh-secret', (select decrypted_secret from vault.decrypted_secrets where name = 'market_refresh_secret')
        ),
        body := jsonb_build_object(
            'mode', 'backfill-pastspot-history',
            'offset', 50,
            'limit', 25,
            'concurrency', 4,
            'force', false
        ),
        timeout_milliseconds := 120000
    ) as request_id;
    $cron$
);
