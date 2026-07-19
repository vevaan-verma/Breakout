-- Fix current-listener diagnostics and standardize artwork variants.
-- Safe to run after 0020. This migration is generic and contains no artist-specific overrides.

-- Fill any remaining missing current-listener fields from the latest stored snapshot.
-- Existing non-null current values are never overwritten.
update public.market_artist_cache
set
    current_listeners = snapshot_listeners,
    current_listeners_source = coalesce(
        nullif(trim(current_listeners_source), ''),
        case
            when nullif(trim(coalesce(source, '')), '') is not null
                then source || ' latest snapshot backfill'
            else 'Latest snapshot backfill'
        end
    ),
    current_listeners_observed_at = coalesce(
        current_listeners_observed_at,
        snapshot_date::timestamptz,
        data_date::timestamptz,
        updated_at,
        now()
    )
where current_listeners is null
  and snapshot_listeners is not null;

-- Keep the compatibility listener field aligned only when it is missing.
update public.market_artist_cache
set listeners = current_listeners
where listeners is null
  and current_listeners is not null;

-- Generate lightweight card artwork and moderately sized full-page artwork.
-- Card images remain approximately 250x250; full images are capped around 640x640.
update public.market_artist_cache
set
    image_url_card = case
        when image_url is null then image_url_card
        else replace(
            replace(
                replace(
                    replace(
                        replace(
                            replace(image_url, '0000e5eb', '00005174'),
                            '0000f178',
                            '00005174'
                        ),
                        '1000x1000',
                        '250x250'
                    ),
                    '640x640',
                    '250x250'
                ),
                '500x500',
                '250x250'
            ),
            '300x300',
            '250x250'
        )
    end,
    image_url_full = case
        when image_url is null then image_url_full
        else replace(
            replace(
                replace(
                    replace(
                        replace(image_url, '56x56', '640x640'),
                        '250x250',
                        '640x640'
                    ),
                    '300x300',
                    '640x640'
                ),
                '500x500',
                '640x640'
            ),
            '1000x1000',
            '640x640'
        )
    end
where image_url is not null;

-- Redefine the listener diagnostic.
-- Equality between current_listeners and snapshot_listeners is normal when the
-- newest snapshot is also the newest current observation, so that alone is not an issue.
drop function if exists public.market_listener_diagnostics();

create or replace function public.market_listener_diagnostics()
returns table (
    spotify_id text,
    normalized_name text,
    name text,
    issue text,
    current_listeners bigint,
    current_listeners_source text,
    current_listeners_observed_at timestamptz,
    snapshot_listeners bigint,
    generic_listeners bigint,
    updated_at timestamptz
)
language sql
security definer
set search_path = public
as $$
    select
        mac.spotify_id,
        mac.normalized_name,
        mac.name,
        diagnostic.issue,
        mac.current_listeners,
        mac.current_listeners_source,
        mac.current_listeners_observed_at,
        mac.snapshot_listeners,
        mac.listeners,
        mac.updated_at
    from public.market_artist_cache mac
    cross join lateral (
        values
            (
                'missing_current_listeners',
                mac.current_listeners is null
            ),
            (
                'missing_current_source',
                mac.current_listeners is not null
                and nullif(trim(coalesce(mac.current_listeners_source, '')), '') is null
            ),
            (
                'missing_current_observed_at',
                mac.current_listeners is not null
                and mac.current_listeners_observed_at is null
            ),
            (
                'stale_current_listeners',
                mac.current_listeners_observed_at is not null
                and mac.current_listeners_observed_at < now() - interval '14 days'
            ),
            (
                'generic_current_disagreement',
                mac.current_listeners is not null
                and mac.listeners is not null
                and greatest(mac.current_listeners, mac.listeners) > 0
                and abs(mac.current_listeners - mac.listeners)::numeric
                    / greatest(mac.current_listeners, mac.listeners)::numeric > 0.15
            ),
            (
                'current_older_than_snapshot',
                mac.current_listeners_observed_at is not null
                and mac.snapshot_date is not null
                and mac.current_listeners_observed_at::date < mac.snapshot_date
            )
    ) as diagnostic(issue, active)
    where diagnostic.active;
$$;

grant execute on function public.market_listener_diagnostics()
to authenticated;

-- Recreate the cache RPC so cards/pages receive both optimized artwork variants
-- and the designated current listener fields.
drop function if exists public.market_artists_cached(text);

create or replace function public.market_artists_cached(search_query text default '')
returns table (
    spotify_id text,
    name text,
    image_url text,
    image_url_card text,
    image_url_full text,
    current_listeners bigint,
    current_listeners_observed_at timestamptz,
    current_listeners_source text,
    snapshot_listeners bigint,
    snapshot_previous_listeners bigint,
    snapshot_date date,
    listener_change_since_snapshot bigint,
    listener_change_since_snapshot_percent numeric,
    days_since_snapshot integer,
    listeners bigint,
    weekly_listener_gain bigint,
    weekly_listener_growth_percent numeric,
    monthly_listener_change_percent numeric,
    daily_listener_change bigint,
    kworb_rank integer,
    source text,
    score_status text,
    provider_url text,
    data_date date,
    listener_history jsonb,
    top_tracks jsonb,
    discography jsonb,
    top_cities jsonb,
    updated_at timestamptz
)
language sql
stable
security definer
set search_path = public
as $$
    select
        mac.spotify_id,
        mac.name,
        mac.image_url,
        coalesce(mac.image_url_card, mac.image_url) as image_url_card,
        coalesce(mac.image_url_full, mac.image_url) as image_url_full,
        mac.current_listeners,
        mac.current_listeners_observed_at,
        mac.current_listeners_source,
        mac.snapshot_listeners,
        mac.snapshot_previous_listeners,
        mac.snapshot_date,
        mac.listener_change_since_snapshot,
        mac.listener_change_since_snapshot_percent,
        mac.days_since_snapshot,
        coalesce(mac.current_listeners, mac.listeners, mac.snapshot_listeners) as listeners,
        mac.weekly_listener_gain,
        mac.weekly_listener_growth_percent,
        mac.monthly_listener_change_percent,
        mac.daily_listener_change,
        mac.kworb_rank,
        mac.source,
        mac.score_status,
        mac.provider_url,
        mac.data_date,
        mac.listener_history,
        mac.top_tracks,
        mac.discography,
        mac.top_cities,
        mac.updated_at
    from public.market_artist_cache mac
    where mac.updated_at >= now() - interval '14 days'
    and (
        trim(coalesce(search_query, '')) = ''
        or mac.normalized_name like '%' || lower(regexp_replace(trim(search_query), '\s+', ' ', 'g')) || '%'
        or lower(mac.name) like '%' || lower(trim(search_query)) || '%'
    )
    order by
        case
            when trim(coalesce(search_query, '')) = '' then 0
            when mac.normalized_name = lower(regexp_replace(trim(search_query), '\s+', ' ', 'g')) then 0
            when mac.normalized_name like lower(regexp_replace(trim(search_query), '\s+', ' ', 'g')) || '%' then 1
            else 2
        end,
        coalesce(mac.weekly_listener_growth_percent, -999999) desc,
        coalesce(mac.weekly_listener_gain, -999999999999) desc,
        coalesce(mac.current_listeners, mac.listeners, mac.snapshot_listeners, 0) desc,
        mac.name asc
    limit 10000;
$$;

grant execute on function public.market_artists_cached(text)
to anon, authenticated;
