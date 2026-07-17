create extension if not exists pg_cron with schema extensions;
create extension if not exists pg_net with schema extensions;

-- Remove older versions of these jobs before recreating them.
select cron.unschedule(jobid)
from cron.job
where jobname in (
    'refresh-deep-cuts-01',
    'refresh-deep-cuts-02',
    'refresh-deep-cuts-03',
    'refresh-deep-cuts-04'
);

select cron.schedule(
    'refresh-deep-cuts-01',
    '10 2 * * *',
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
            'mode', 'deep-cuts',
            'countryOffset', 0,
            'countryLimit', 25,
            'offset', 0,
            'limit', 100
        )
    ) as request_id;
    $$
);

select cron.schedule(
    'refresh-deep-cuts-02',
    '20 2 * * *',
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
            'mode', 'deep-cuts',
            'countryOffset', 0,
            'countryLimit', 25,
            'offset', 100,
            'limit', 100
        )
    ) as request_id;
    $$
);

select cron.schedule(
    'refresh-deep-cuts-03',
    '30 2 * * *',
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
            'mode', 'deep-cuts',
            'countryOffset', 25,
            'countryLimit', 25,
            'offset', 0,
            'limit', 100
        )
    ) as request_id;
    $$
);

select cron.schedule(
    'refresh-deep-cuts-04',
    '40 2 * * *',
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
            'mode', 'deep-cuts',
            'countryOffset', 25,
            'countryLimit', 25,
            'offset', 100,
            'limit', 100
        )
    ) as request_id;
    $$
);
