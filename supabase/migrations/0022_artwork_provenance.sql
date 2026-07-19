-- Track where each artwork URL was discovered.
alter table public.market_artist_cache
  add column if not exists artwork_source text,
  add column if not exists artwork_source_url text,
  add column if not exists artwork_observed_at timestamptz;

comment on column public.market_artist_cache.artwork_source is
  'Discovery path for artwork, e.g. pastspot_artist_page_og or pastspot_search_api.';

comment on column public.market_artist_cache.artwork_source_url is
  'Provider page or endpoint where the artwork URL was discovered.';

comment on column public.market_artist_cache.artwork_observed_at is
  'Timestamp when the artwork URL was most recently discovered.';

create index if not exists market_artist_cache_artwork_source_idx
  on public.market_artist_cache (artwork_source);
