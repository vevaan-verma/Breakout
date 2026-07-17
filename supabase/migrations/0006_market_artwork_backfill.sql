create extension if not exists pg_cron with schema extensions;
create extension if not exists pg_net with schema extensions;

alter table public.market_artist_cache
add column if not exists artwork_last_attempted_at timestamptz;

alter table public.market_artist_cache
add column if not exists artwork_attempt_count integer not null default 0;

create index if not exists market_artist_cache_artwork_backfill_idx
on public.market_artist_cache (
    artwork_last_attempted_at asc nulls first,
    artwork_attempt_count asc,
    listeners desc nulls last
)
where image_url is null;

create or replace function public.mark_market_artwork_attempts(artist_names text[])
returns void
language sql
security definer
set search_path = public
as $$
    update public.market_artist_cache
    set
        artwork_last_attempted_at = now(),
        artwork_attempt_count = coalesce(artwork_attempt_count, 0) + 1
    where normalized_name = any(artist_names)
    and image_url is null;
$$;

grant execute on function public.mark_market_artwork_attempts(text[]) to service_role;

select cron.unschedule(jobid)
from cron.job
where jobname in (
    'refresh-market-artwork-0250',
    'refresh-market-artwork-0300-0650'
);

select cron.schedule(
    'refresh-market-artwork-0250',
    '50 2 * * *',
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
            'mode', 'artwork',
            'limit', 100
        )
    ) as request_id;
    $$
);

select cron.schedule(
    'refresh-market-artwork-0300-0650',
    '*/10 3-6 * * *',
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
            'mode', 'artwork',
            'limit', 100
        )
    ) as request_id;
    $$
);
