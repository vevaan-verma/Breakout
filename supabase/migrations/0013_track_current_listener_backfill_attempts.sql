create index if not exists market_artist_cache_artwork_or_current_backfill_idx
on public.market_artist_cache (
    artwork_last_attempted_at asc nulls first,
    artwork_attempt_count asc,
    listeners desc nulls last
)
where image_url is null or current_listeners is null;

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
    and (image_url is null or current_listeners is null);
$$;

grant execute on function public.mark_market_artwork_attempts(text[]) to service_role;
