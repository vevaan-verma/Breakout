create extension if not exists pg_cron with schema extensions;
create extension if not exists pg_net with schema extensions;

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
        ),
        timeout_milliseconds := 60000
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
        ),
        timeout_milliseconds := 60000
    ) as request_id;
    $$
);
