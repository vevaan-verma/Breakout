create extension if not exists pg_cron with schema extensions;
create extension if not exists pg_net with schema extensions;

-- Recreate existing refresh-market-cache jobs that already specify a timeout.
-- Direct UPDATE access to cron.job is not allowed on hosted Supabase.
do $$
declare
    existing_job record;
    updated_command text;
begin
    for existing_job in
        select
            jobid,
            jobname,
            schedule,
            command
        from cron.job
        where command like '%/functions/v1/refresh-market-cache%'
          and command ~ 'timeout_milliseconds\s*:=\s*[0-9]+'
          and jobname <> 'process-due-trades-every-5'
    loop
        updated_command := regexp_replace(
            existing_job.command,
            'timeout_milliseconds\s*:=\s*[0-9]+',
            'timeout_milliseconds := 120000',
            'g'
        );

        perform cron.unschedule(existing_job.jobid);

        perform cron.schedule(
            existing_job.jobname,
            existing_job.schedule,
            updated_command
        );
    end loop;
end
$$;

-- Remove an existing copy so retrying this migration does not create duplicates.
select cron.unschedule(jobid)
from cron.job
where jobname = 'process-due-trades-every-5';

-- Process accepted trades that have reached their authoritative processing time.
select cron.schedule(
    'process-due-trades-every-5',
    '*/5 * * * *',
    $cron$
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
            'mode', 'process-due-trades'
        ),
        timeout_milliseconds := 120000
    ) as request_id;
    $cron$
);
