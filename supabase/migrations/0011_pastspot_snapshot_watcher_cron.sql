create extension if not exists pg_cron with schema extensions;
create extension if not exists pg_net with schema extensions;

select cron.unschedule(jobid)
from cron.job
where jobname = 'watch-pastspot-weekly-snapshot';

select cron.schedule(
    'watch-pastspot-weekly-snapshot',
    '0 * * * 5,6',
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
            'mode', 'watch-pastspot'
        )
    ) as request_id;
    $$
);
