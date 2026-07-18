create or replace function public.run_score_catchup_until(
    cutoff_time timestamptz
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
    catchup_job_id bigint;
begin
    -- Once the cutoff has passed, remove the temporary job.
    if now() >= cutoff_time then
        select jobid
        into catchup_job_id
        from cron.job
        where jobname = 'score-artists-catchup-tonight'
        limit 1;

        if catchup_job_id is not null then
            perform cron.unschedule(catchup_job_id);
        end if;

        return;
    end if;

    -- Before the cutoff, run another scoring pass.
    perform net.http_post(
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
    );
end;
$$;

-- Replace the existing endlessly recurring temporary job.
select cron.unschedule(jobid)
from cron.job
where jobname = 'score-artists-catchup-tonight';

select cron.schedule(
    'score-artists-catchup-tonight',
    '*/20 * * * *',
    $$
    select public.run_score_catchup_until(
        '2026-07-18 15:00:00+00'::timestamptz
    );
    $$
);
