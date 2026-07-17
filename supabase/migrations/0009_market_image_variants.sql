alter table public.market_artist_cache
add column if not exists image_url_card text;

alter table public.market_artist_cache
add column if not exists image_url_full text;

update public.market_artist_cache
set
    image_url_full = coalesce(image_url_full, image_url),
    image_url_card = coalesce(
        image_url_card,
        nullif(
            replace(
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
            ),
            ''
        )
    )
where image_url is not null
and (image_url_card is null or image_url_full is null);

drop function if exists public.market_artists_cached(text);
create or replace function public.market_artists_cached(search_query text default '')
returns table (
    spotify_id text,
    name text,
    image_url text,
    image_url_card text,
    image_url_full text,
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
        mac.image_url_card,
        mac.image_url_full,
        mac.listeners,
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
        coalesce(mac.listeners, 0) desc,
        mac.name asc
    limit 10000;
$$;

grant execute on function public.market_artists_cached(text) to anon, authenticated;
