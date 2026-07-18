const SUPABASE_URL = Deno.env.get("SUPABASE_URL")?.replace(/\/$/, "") ?? "";
const SERVICE_ROLE_KEY = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? "";
const MARKET_REFRESH_SECRET = Deno.env.get("MARKET_REFRESH_SECRET") ?? "";
const BatchSize = 500;
const DefaultDeepCutLimit = 100;
const MaxDeepCutLimit = 100;
const DefaultArtworkLimit = 100;
const MaxArtworkLimit = 100;
const ArtworkRetryDelayHours = 24;
const MaxArtworkAttempts = 3;
const FetchTimeoutMs = 12_000;
const MarketArtistSelect = "spotify_id,name,normalized_name,image_url,image_url_card,image_url_full,current_listeners,current_listeners_observed_at,current_listeners_source,snapshot_listeners,snapshot_previous_listeners,snapshot_date,listener_change_since_snapshot,listener_change_since_snapshot_percent,days_since_snapshot,listeners,weekly_listener_gain,weekly_listener_growth_percent,monthly_listener_change_percent,daily_listener_change,kworb_rank,source,score_status,provider_url,data_date,listener_history,top_tracks,discography,top_cities,updated_at";
const MarketArtistSelectWithAttempts = `${MarketArtistSelect},artwork_last_attempted_at,artwork_attempt_count`;

type MarketArtist = {
  spotify_id?: string | null;
  name: string;
  normalized_name: string;
  image_url?: string | null;
  image_url_card?: string | null;
  image_url_full?: string | null;
  current_listeners?: number | null;
  current_listeners_observed_at?: string | null;
  current_listeners_source?: string | null;
  snapshot_listeners?: number | null;
  snapshot_previous_listeners?: number | null;
  snapshot_date?: string | null;
  listener_change_since_snapshot?: number | null;
  listener_change_since_snapshot_percent?: number | null;
  days_since_snapshot?: number | null;
  listeners?: number | null;
  weekly_listener_gain?: number | null;
  weekly_listener_growth_percent?: number | null;
  monthly_listener_change_percent?: number | null;
  daily_listener_change?: number | null;
  kworb_rank?: number | null;
  source: string;
  score_status: string;
  provider_url?: string | null;
  data_date?: string | null;
  listener_history?: unknown[];
  top_tracks?: unknown[];
  discography?: unknown[];
  top_cities?: unknown[];
  artwork_last_attempted_at?: string | null;
  artwork_attempt_count?: number | null;
  updated_at: string;
};

type PastspotDetail = {
  listeners?: number | null;
  weekly_listener_growth_percent?: number | null;
  monthly_listener_change_percent?: number | null;
  image_url?: string | null;
  data_date?: string | null;
  listener_history?: unknown[];
  top_tracks?: unknown[];
  discography?: unknown[];
  top_cities?: unknown[];
};

type MusicMetricsVaultListItem = {
  item?: {
    name?: unknown;
    url?: unknown;
  };
};

type WeeklyMetric = {
  spotify_id: string | null;
  normalized_name: string;
  name: string;
  week_start: string;
  snapshot_date?: string | null;
  role: string;
  listeners: number | null;
  snapshot_listeners?: number | null;
  previous_snapshot_listeners?: number | null;
  weekly_listener_gain: number | null;
  weekly_listener_growth_percent: number | null;
  daily_listener_change: number | null;
  kworb_rank: number | null;
  source: string | null;
  top_city?: unknown;
  fetched_at?: string;
  raw_data: Record<string, unknown>;
};

type ArtistDailySnapshot = {
  normalized_name: string;
  name: string;
  snapshot_date: string;
  listeners: number | null;
  source: string | null;
  raw_data: Record<string, unknown>;
  updated_at: string;
};

type ArtistProjectionFreeze = {
  normalized_name: string;
  name: string;
  week_start: string;
  projected_points: number;
  formula_version: string;
  listeners: number | null;
  role: string;
  source: string | null;
  raw_data: Record<string, unknown>;
};

type ArtistDailyScore = {
  normalized_name: string;
  name: string;
  week_start: string;
  score_date: string;
  actual_points: number;
  projected_points: number | null;
  formula_version: string;
  listeners: number | null;
  listener_delta: number | null;
  listener_growth_percent: number | null;
  role: string;
  source: string | null;
  raw_data: Record<string, unknown>;
  updated_at: string;
};

type ScoringFormula = {
  audienceWeight: number;
  growthWeight: number;
  gainWeight: number;
  deepCutCeilingBonus: number;
  risingCeilingBonus: number;
  mainstayCeilingBonus: number;
  headlinerCeilingBonus: number;
  headlinerFloorBonus: number;
  mainstayFloorBonus: number;
  risingFloorBonus: number;
  deepCutFloorBonus: number;
  deepCutVolatility: number;
  risingVolatility: number;
  stableVolatility: number;
};

Deno.serve(async (request) => {
  const startedAt = Date.now();
  try {
    if (request.method !== "POST") {
      return json({ error: "Method not allowed." }, 405);
    }

    if (!SUPABASE_URL || !SERVICE_ROLE_KEY || !MARKET_REFRESH_SECRET) {
      return json({ error: "Missing Supabase environment." }, 500);
    }

    if (request.headers.get("x-refresh-secret") !== MARKET_REFRESH_SECRET) {
      return json({ error: "Unauthorized." }, 401);
    }

    const body = await request.json().catch(() => ({}));
    const mode = typeof body?.mode === "string" ? body.mode : "core";

    if (mode === "deep-cuts") {
      const offset = clampInteger(body?.offset, 0, 100_000, 0);
      const limit = clampInteger(body?.limit, 1, MaxDeepCutLimit, DefaultDeepCutLimit);
      const countryOffset = clampInteger(body?.countryOffset, 0, 1_000, 0);
      const countryLimit = clampInteger(body?.countryLimit, 1, 40, 25);
      const artists = await loadMusicMetricsVaultArtists({ offset, limit, countryOffset, countryLimit });
      const normalized = artists.map(normalizeMarketArtist);
      if (normalized.length > 0) {
        await upsertMarketArtists(normalized);
      }
      return json({
        mode,
        offset,
        limit,
        countryOffset,
        countryLimit,
        updated: normalized.length,
        musicMetricsVault: artists.length,
        withImages: normalized.filter((artist) => artist.image_url).length,
        durationMs: Date.now() - startedAt,
      });
    }

    if (mode === "watch-pastspot") {
      const latestDate = await loadPastspotLatestSnapshotDate();
      const state = await loadPastspotSnapshotState();
      const nowIso = new Date().toISOString();
      const isNewDate = latestDate != null && latestDate !== state.processed_date;
      const firstSeenAt = isNewDate && latestDate === state.latest_seen_date
        ? state.first_seen_at
        : nowIso;
      const readyForRefresh = isNewDate &&
        firstSeenAt != null &&
        Date.now() - Date.parse(firstSeenAt) >= 90 * 60 * 1000;
      await savePastspotSnapshotState({
        latest_seen_date: latestDate,
        first_seen_at: firstSeenAt,
        last_checked_at: nowIso,
        check_payload: { mode, latestDate, readyForRefresh },
      });
      if (!readyForRefresh || latestDate == null) {
        return json({
          mode,
          latestDate,
          processedDate: state.processed_date,
          readyForRefresh,
          durationMs: Date.now() - startedAt,
        });
      }
      const [pastspot, kworb] = await Promise.all([
        loadPastspotGainers(),
        loadKworbListeners(),
      ]);
      const normalized = mergeArtists([...pastspot, ...kworb])
        .slice(0, 10000)
        .map(normalizeMarketArtist);
      if (normalized.length > 0) {
        await upsertMarketArtists(normalized);
        await upsertWeeklyMetrics(buildWeeklyMetrics(normalized));
      }
      await savePastspotSnapshotState({
        latest_seen_date: latestDate,
        first_seen_at: firstSeenAt,
        processed_date: latestDate,
        processed_at: new Date().toISOString(),
        last_checked_at: new Date().toISOString(),
        check_payload: { mode, latestDate, refreshed: normalized.length },
      });
      return json({
        mode,
        latestDate,
        refreshed: normalized.length,
        pastspot: pastspot.length,
        kworb: kworb.length,
        durationMs: Date.now() - startedAt,
      });
    }

    if (mode === "artwork") {
      const limit = clampInteger(body?.limit, 1, MaxArtworkLimit, DefaultArtworkLimit);
      const force = body?.force === true;
      const beforeStats = await countArtworkBackfillStats();
      const artists = await loadArtistsMissingArtwork(limit, force);
      if (artists.length > 0) {
        await markArtworkAttempts(artists);
      }
      const attemptedIconNames = new Set(
        artists
          .filter((artist) => !artist.image_url)
          .map((artist) => artist.normalized_name),
      );
      const filled = (await parallelMap(artists, 4, fillMissingPastspotArtwork))
        .filter((artist) => artist.image_url || artist.current_listeners != null);
      if (filled.length > 0) {
        await upsertMarketArtists(filled.map(normalizeMarketArtist));
      }
      const afterStats = await countArtworkBackfillStats();
      return json({
        mode,
        limit,
        force,
        attempted: artists.length,
        attemptedMissingIcons: attemptedIconNames.size,
        found: filled.filter((artist) => artist.image_url && attemptedIconNames.has(artist.normalized_name)).length,
        updated: filled.length,
        before: beforeStats,
        after: afterStats,
        remainingMissing: afterStats.missingIconsOrCurrentListeners,
        remainingMissingIcons: afterStats.missingIcons,
        remainingMissingCurrentListeners: afterStats.missingCurrentListeners,
        durationMs: Date.now() - startedAt,
      });
    }

    if (mode === "snapshot-week") {
      const limit = clampInteger(body?.limit, 100, 20_000, 10_000);
      const artists = await loadCachedMarketArtistsForMetrics(limit);
      const metrics = buildWeeklyMetrics(artists);
      if (metrics.length > 0) {
        await upsertWeeklyMetrics(metrics);
      }
      return json({
        mode,
        artists: artists.length,
        metrics: metrics.length,
        weeks: [...new Set(metrics.map((metric) => metric.week_start))].length,
        durationMs: Date.now() - startedAt,
      });
    }

    if (mode === "freeze-projections" || mode === "score-daily") {
      const limit = clampInteger(body?.limit, 100, 20_000, 10_000);
      const formulaVersion = typeof body?.formulaVersion === "string" && body.formulaVersion.trim()
        ? body.formulaVersion.trim()
        : "balanced-v1";
      const formula = scoringFormulaFromBody(body?.formula);
      const scoreDate = typeof body?.scoreDate === "string" && /^\d{4}-\d{2}-\d{2}$/.test(body.scoreDate)
        ? body.scoreDate
        : new Date().toISOString().slice(0, 10);
      const weekStart = typeof body?.weekStart === "string" && /^\d{4}-\d{2}-\d{2}$/.test(body.weekStart)
        ? body.weekStart
        : weekStartForDate(scoreDate);
      const artists = await loadCachedMarketArtistsForMetrics(limit);
      const metrics = buildWeeklyMetrics(artists);
      const snapshots = buildDailySnapshots(artists, scoreDate);
      if (snapshots.length > 0) {
        await upsertDailySnapshots(snapshots);
      }
      const projections = buildProjectionFreezes(metrics, weekStart, formulaVersion, formula);
      const shouldFreeze = mode === "freeze-projections" || body?.freezeMissingProjections === true;
      if (shouldFreeze && projections.length > 0) {
        await upsertProjectionFreezes(projections);
      }
      const projectionMap = shouldFreeze
        ? new Map(projections.map((projection) => [projection.normalized_name, projection.projected_points]))
        : await loadProjectionMap(weekStart, formulaVersion);
      const scores = buildDailyScores(metrics, weekStart, scoreDate, formulaVersion, formula, projectionMap);
      if (mode === "score-daily" && scores.length > 0) {
        await upsertDailyScores(scores);
      }
      return json({
        mode,
        formulaVersion,
        scoreDate,
        weekStart,
        artists: artists.length,
        snapshots: snapshots.length,
        projections: shouldFreeze ? projections.length : projectionMap.size,
        scores: mode === "score-daily" ? scores.length : 0,
        durationMs: Date.now() - startedAt,
      });
    }

    if (mode === "backtest-scoring") {
      const formulaVersion = typeof body?.formulaVersion === "string" && body.formulaVersion.trim()
        ? body.formulaVersion.trim()
        : "balanced-v1";
      const weeks = clampInteger(body?.weeks, 4, 260, 104);
      const formula = scoringFormulaFromBody(body?.formula);
      const metrics = await loadWeeklyMetricsForBacktest(weeks);
      const result = await runScoringBacktest(formulaVersion, formula, metrics);
      return json({
        mode,
        formulaVersion,
        formula,
        weeks,
        metrics: metrics.length,
        ...result,
        durationMs: Date.now() - startedAt,
      });
    }

    if (mode !== "core") {
      return json({ error: `Unknown mode: ${mode}` }, 400);
    }

    const [pastspot, kworb] = await Promise.all([
      loadPastspotGainers(),
      loadKworbListeners(),
    ]);
    const normalized = mergeArtists([...pastspot, ...kworb])
      .slice(0, 10000)
      .map(normalizeMarketArtist);

    if (normalized.length === 0) {
      return json({ error: "No market artists found." }, 502);
    }
    await upsertMarketArtists(normalized);

    return json({
      mode,
      updated: normalized.length,
      pastspot: pastspot.length,
      kworb: kworb.length,
      musicMetricsVault: 0,
      withImages: normalized.filter((artist) => artist.image_url).length,
      durationMs: Date.now() - startedAt,
    });
  } catch (error) {
    return json({ error: error instanceof Error ? error.message : String(error) }, 500);
  }
});

async function upsertMarketArtists(artists: MarketArtist[]): Promise<void> {
  for (const batch of chunkArray(artists, BatchSize)) {
    const safeBatch = await preserveExistingArtwork(batch);
    const response = await fetch(`${SUPABASE_URL}/rest/v1/market_artist_cache?on_conflict=normalized_name`, {
      method: "POST",
      signal: AbortSignal.timeout(FetchTimeoutMs),
      headers: {
        "apikey": SERVICE_ROLE_KEY,
        "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
        "Content-Type": "application/json",
        "Prefer": "resolution=merge-duplicates",
      },
      body: JSON.stringify(safeBatch),
    });

    if (!response.ok) {
      throw new Error(await response.text());
    }
  }
}

async function preserveExistingArtwork(artists: MarketArtist[]): Promise<MarketArtist[]> {
  const missing = artists
    .filter((artist) => !artist.image_url)
    .map((artist) => artist.normalized_name)
    .filter(Boolean);
  if (missing.length === 0) return artists;
  const names = missing
    .map((name) => `"${String(name).replaceAll("\"", "\\\"")}"`)
    .join(",");
  const response = await fetch(`${SUPABASE_URL}/rest/v1/market_artist_cache?select=normalized_name,image_url,image_url_card,image_url_full&image_url=not.is.null&normalized_name=in.(${encodeURIComponent(names)})`, {
    signal: AbortSignal.timeout(FetchTimeoutMs),
    headers: {
      "apikey": SERVICE_ROLE_KEY,
      "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
    },
  }).catch(() => null);
  if (!response?.ok) return artists;
  const rows = await response.json().catch(() => []);
  const byName = new Map<string, Pick<MarketArtist, "image_url" | "image_url_card" | "image_url_full">>();
  for (const row of rows) {
    if (row?.normalized_name && row?.image_url) {
      byName.set(row.normalized_name, {
        image_url: row.image_url,
        image_url_card: row.image_url_card ?? null,
        image_url_full: row.image_url_full ?? row.image_url,
      });
    }
  }
  const withExisting = artists.map((artist) => artist.image_url ? artist : {
    ...artist,
    image_url: byName.get(artist.normalized_name)?.image_url ?? artist.image_url ?? null,
    image_url_card: byName.get(artist.normalized_name)?.image_url_card ?? artist.image_url_card ?? null,
    image_url_full: byName.get(artist.normalized_name)?.image_url_full ?? artist.image_url_full ?? null,
  });
  return withExisting.map(normalizeArtworkUrls);
}

async function markArtworkAttempts(artists: MarketArtist[]): Promise<void> {
  const names = artists
    .map((artist) => artist.normalized_name)
    .filter(Boolean);
  if (names.length === 0) return;
  const response = await fetch(`${SUPABASE_URL}/rest/v1/rpc/mark_market_artwork_attempts`, {
    method: "POST",
    signal: AbortSignal.timeout(FetchTimeoutMs),
    headers: {
      "apikey": SERVICE_ROLE_KEY,
      "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ artist_names: names }),
  });
  if (!response.ok) throw new Error(await response.text());
}

type ArtworkBackfillStats = {
  missingIcons: number;
  missingCurrentListeners: number;
  missingIconsOrCurrentListeners: number;
  eligibleNow: number;
  waitingForRetryWindow: number;
  exhaustedAttempts: number;
};

async function countArtworkBackfillStats(): Promise<ArtworkBackfillStats> {
  const retryBefore = new Date(Date.now() - ArtworkRetryDelayHours * 60 * 60 * 1000).toISOString();
  const [
    missingIcons,
    missingCurrentListeners,
    missingIconsOrCurrentListeners,
    eligibleNow,
    waitingForRetryWindow,
    exhaustedAttempts,
  ] = await Promise.all([
    countMarketRows({ image_url: "is.null" }),
    countMarketRows({ current_listeners: "is.null" }),
    countMarketRows({ or: "(image_url.is.null,current_listeners.is.null)" }),
    countMarketRows({
      and: `(or(image_url.is.null,current_listeners.is.null),or(artwork_last_attempted_at.is.null,artwork_last_attempted_at.lt.${retryBefore}))`,
      artwork_attempt_count: `lt.${MaxArtworkAttempts}`,
    }),
    countMarketRows({
      and: `(or(image_url.is.null,current_listeners.is.null),artwork_last_attempted_at.gte.${retryBefore})`,
      artwork_attempt_count: `lt.${MaxArtworkAttempts}`,
    }),
    countMarketRows({
      or: "(image_url.is.null,current_listeners.is.null)",
      artwork_attempt_count: `gte.${MaxArtworkAttempts}`,
    }),
  ]);
  return {
    missingIcons,
    missingCurrentListeners,
    missingIconsOrCurrentListeners,
    eligibleNow,
    waitingForRetryWindow,
    exhaustedAttempts,
  };
}

async function countMarketRows(filters: Record<string, string>): Promise<number> {
  const query = new URLSearchParams({
    select: "normalized_name",
    ...filters,
  });
  const response = await fetch(
    `${SUPABASE_URL}/rest/v1/market_artist_cache?${query}`,
    {
      signal: AbortSignal.timeout(FetchTimeoutMs),
      headers: {
        "apikey": SERVICE_ROLE_KEY,
        "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
        "Prefer": "count=exact",
        "Range-Unit": "items",
        "Range": "0-0",
      },
    },
  );
  if (!response.ok) throw new Error(await response.text());
  const contentRange = response.headers.get("content-range") ?? "";
  const totalText = contentRange.split("/").at(-1);
  const total = totalText ? Number.parseInt(totalText, 10) : NaN;
  return Number.isFinite(total) ? total : 0;
}

async function loadArtistsMissingArtwork(limit: number, force = false): Promise<MarketArtist[]> {
  const iconRows = await loadMissingArtworkRows(limit, force, true);
  if (iconRows.length >= limit) return iconRows;
  const currentRows = await loadMissingArtworkRows(limit - iconRows.length, force, false);
  const seen = new Set(iconRows.map((artist) => artist.normalized_name));
  return [
    ...iconRows,
    ...currentRows.filter((artist) => !seen.has(artist.normalized_name)),
  ].slice(0, limit);
}

async function loadMissingArtworkRows(limit: number, force: boolean, iconsOnly: boolean): Promise<MarketArtist[]> {
  const retryBefore = new Date(Date.now() - ArtworkRetryDelayHours * 60 * 60 * 1000).toISOString();
  const missingFilter = iconsOnly
    ? { image_url: "is.null" }
    : { image_url: "not.is.null", current_listeners: "is.null" };
  const query = new URLSearchParams(
    force
      ? {
          select: MarketArtistSelectWithAttempts,
          ...missingFilter,
          order: "artwork_attempt_count.asc,artwork_last_attempted_at.asc.nullsfirst,listeners.desc.nullslast",
        }
      : {
          select: MarketArtistSelectWithAttempts,
          ...missingFilter,
          or: `(artwork_last_attempted_at.is.null,artwork_last_attempted_at.lt.${retryBefore})`,
          artwork_attempt_count: `lt.${MaxArtworkAttempts}`,
          order: "artwork_last_attempted_at.asc.nullsfirst,artwork_attempt_count.asc,listeners.desc.nullslast",
        },
  );
  const response = await fetch(
    `${SUPABASE_URL}/rest/v1/market_artist_cache?${query}`,
    {
      signal: AbortSignal.timeout(FetchTimeoutMs),
      headers: {
        "apikey": SERVICE_ROLE_KEY,
        "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
        "Range-Unit": "items",
        "Range": `0-${limit - 1}`,
      },
    },
  );
  if (!response.ok) throw new Error(await response.text());
  return await response.json();
}

async function loadCachedMarketArtistsForMetrics(limit: number): Promise<MarketArtist[]> {
  const pageSize = 1000;
  const rows: MarketArtist[] = [];
  let offset = 0;
  while (offset < limit) {
    const pageLimit = Math.min(pageSize, limit - offset);
    const response = await fetch(
      `${SUPABASE_URL}/rest/v1/market_artist_cache?select=${MarketArtistSelect}&updated_at=gte.${encodeURIComponent(new Date(Date.now() - 14 * 24 * 60 * 60 * 1000).toISOString())}&order=listeners.desc.nullslast`,
      {
        signal: AbortSignal.timeout(FetchTimeoutMs),
        headers: {
          "apikey": SERVICE_ROLE_KEY,
          "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
          "Range-Unit": "items",
          "Range": `${offset}-${offset + pageLimit - 1}`,
        },
      },
    );
    if (!response.ok) throw new Error(await response.text());
    const page: MarketArtist[] = await response.json();
    rows.push(...page);
    if (page.length < pageLimit) break;
    offset += page.length;
  }
  return rows;
}

function buildWeeklyMetrics(artists: MarketArtist[]): WeeklyMetric[] {
  const byKey = new Map<string, WeeklyMetric>();
  for (const artist of artists) {
    const history = Array.isArray(artist.listener_history) ? artist.listener_history : [];
    const historyRows = history
      .map((entry) => metricFromHistoryEntry(artist, entry))
      .filter((entry): entry is WeeklyMetric => entry != null);
    const currentMetric = metricFromArtist(artist);
    for (const metric of [...historyRows, currentMetric]) {
      const key = `${metric.normalized_name}:${metric.week_start}`;
      const current = byKey.get(key);
      if (!current || (metric.listeners ?? 0) >= (current.listeners ?? 0)) {
        byKey.set(key, metric);
      }
    }
  }
  return [...byKey.values()];
}

function metricFromArtist(artist: MarketArtist): WeeklyMetric {
  const date = artist.snapshot_date ?? artist.data_date ?? artist.updated_at ?? new Date().toISOString();
  const snapshotListeners = artist.snapshot_listeners ?? artist.listeners ?? null;
  return {
    spotify_id: artist.spotify_id ?? null,
    normalized_name: artist.normalized_name,
    name: artist.name,
    week_start: weekStartIso(date),
    snapshot_date: date.slice(0, 10),
    role: roleForListeners(snapshotListeners),
    listeners: snapshotListeners,
    snapshot_listeners: snapshotListeners,
    previous_snapshot_listeners: artist.snapshot_previous_listeners ?? (
      snapshotListeners != null && artist.weekly_listener_gain != null ? snapshotListeners - artist.weekly_listener_gain : null
    ),
    weekly_listener_gain: artist.weekly_listener_gain ?? null,
    weekly_listener_growth_percent: artist.weekly_listener_growth_percent ?? artist.monthly_listener_change_percent ?? null,
    daily_listener_change: artist.daily_listener_change ?? null,
    kworb_rank: artist.kworb_rank ?? null,
    source: artist.source ?? null,
    top_city: Array.isArray(artist.top_cities) ? artist.top_cities[0] ?? null : null,
    fetched_at: new Date().toISOString(),
    raw_data: {
      data_date: artist.data_date ?? null,
      snapshot_date: artist.snapshot_date ?? null,
      current_listeners: artist.current_listeners ?? null,
      top_tracks: artist.top_tracks ?? [],
      discography: artist.discography ?? [],
      top_cities: artist.top_cities ?? [],
      provider_url: artist.provider_url ?? null,
    },
  };
}

function metricFromHistoryEntry(artist: MarketArtist, entry: unknown): WeeklyMetric | null {
  if (!entry || typeof entry !== "object") return null;
  const row = entry as Record<string, unknown>;
  const date = typeof row.date === "string" ? row.date : null;
  if (!date) return null;
  const listeners = numberFromUnknown(row.listeners);
  const growth = numberFromUnknown(row.change_percent);
  const gain = listeners != null && growth != null && growth !== -100
    ? Math.round(listeners - listeners / (1 + growth / 100))
    : null;
  const previous = listeners != null && gain != null ? listeners - gain : null;
  return {
    spotify_id: artist.spotify_id ?? null,
    normalized_name: artist.normalized_name,
    name: artist.name,
    week_start: weekStartIso(date),
    snapshot_date: date.slice(0, 10),
    role: roleForListeners(listeners),
    listeners,
    snapshot_listeners: listeners,
    previous_snapshot_listeners: previous,
    weekly_listener_gain: gain,
    weekly_listener_growth_percent: growth,
    daily_listener_change: gain == null ? null : Math.round(gain / 7),
    kworb_rank: null,
    source: "Pastspot listener history",
    top_city: Array.isArray(artist.top_cities) ? artist.top_cities[0] ?? null : null,
    fetched_at: new Date().toISOString(),
    raw_data: { history: row },
  };
}

async function upsertWeeklyMetrics(metrics: WeeklyMetric[]): Promise<void> {
  for (const batch of chunkArray(metrics, BatchSize)) {
    const response = await fetch(`${SUPABASE_URL}/rest/v1/artist_weekly_metrics?on_conflict=normalized_name,week_start`, {
      method: "POST",
      signal: AbortSignal.timeout(FetchTimeoutMs),
      headers: {
        "apikey": SERVICE_ROLE_KEY,
        "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
        "Content-Type": "application/json",
        "Prefer": "resolution=merge-duplicates",
      },
      body: JSON.stringify(batch),
    });
    if (!response.ok) throw new Error(await response.text());
  }
}

function buildDailySnapshots(artists: MarketArtist[], scoreDate: string): ArtistDailySnapshot[] {
  return artists
    .filter((artist) => artist.normalized_name && artist.name)
    .map((artist) => ({
      normalized_name: artist.normalized_name,
      name: artist.name,
      snapshot_date: scoreDate,
      listeners: artist.current_listeners ?? artist.snapshot_listeners ?? artist.listeners ?? null,
      source: artist.current_listeners_source ?? artist.source ?? null,
      raw_data: {
        current_listeners_observed_at: artist.current_listeners_observed_at ?? null,
        snapshot_date: artist.snapshot_date ?? null,
        data_date: artist.data_date ?? null,
        weekly_listener_gain: artist.weekly_listener_gain ?? null,
        weekly_listener_growth_percent: artist.weekly_listener_growth_percent ?? null,
      },
      updated_at: new Date().toISOString(),
    }));
}

function buildProjectionFreezes(
  metrics: WeeklyMetric[],
  weekStart: string,
  formulaVersion: string,
  formula: ScoringFormula,
): ArtistProjectionFreeze[] {
  return latestMetricByArtist(metrics).map((metric) => ({
    normalized_name: metric.normalized_name,
    name: metric.name,
    week_start: weekStart,
    projected_points: roundOne(scoreWeeklyMetric(metric, formula)),
    formula_version: formulaVersion,
    listeners: metric.listeners,
    role: metric.role,
    source: metric.source ?? null,
    raw_data: {
      projected_from_week_start: metric.week_start,
      weekly_listener_gain: metric.weekly_listener_gain,
      weekly_listener_growth_percent: metric.weekly_listener_growth_percent,
      snapshot_date: metric.snapshot_date ?? null,
    },
  }));
}

function buildDailyScores(
  metrics: WeeklyMetric[],
  weekStart: string,
  scoreDate: string,
  formulaVersion: string,
  formula: ScoringFormula,
  projectionMap: Map<string, number>,
): ArtistDailyScore[] {
  return latestMetricByArtist(metrics).map((metric) => {
    const listeners = metric.listeners ?? metric.snapshot_listeners ?? null;
    const baseline = metric.previous_snapshot_listeners ?? (
      listeners != null && metric.weekly_listener_gain != null ? listeners - metric.weekly_listener_gain : null
    );
    const delta = listeners != null && baseline != null ? listeners - baseline : metric.weekly_listener_gain ?? null;
    const growth = baseline != null && baseline > 0 && delta != null ? (delta / baseline) * 100 : metric.weekly_listener_growth_percent ?? null;
    const liveMetric: WeeklyMetric = {
      ...metric,
      week_start: weekStart,
      listeners,
      weekly_listener_gain: delta,
      weekly_listener_growth_percent: growth,
      daily_listener_change: delta == null ? metric.daily_listener_change : Math.round(delta / 7),
    };
    return {
      normalized_name: metric.normalized_name,
      name: metric.name,
      week_start: weekStart,
      score_date: scoreDate,
      actual_points: roundOne(scoreWeeklyMetric(liveMetric, formula)),
      projected_points: projectionMap.get(metric.normalized_name) ?? null,
      formula_version: formulaVersion,
      listeners,
      listener_delta: delta,
      listener_growth_percent: growth == null ? null : roundOne(growth),
      role: metric.role,
      source: metric.source ?? null,
      raw_data: {
        source_week_start: metric.week_start,
        source_snapshot_date: metric.snapshot_date ?? null,
        projected_points: projectionMap.get(metric.normalized_name) ?? null,
      },
      updated_at: new Date().toISOString(),
    };
  });
}

function latestMetricByArtist(metrics: WeeklyMetric[]): WeeklyMetric[] {
  const byName = new Map<string, WeeklyMetric>();
  for (const metric of metrics) {
    const current = byName.get(metric.normalized_name);
    if (!current || metric.week_start >= current.week_start) {
      byName.set(metric.normalized_name, metric);
    }
  }
  return [...byName.values()].filter((metric) => metric.listeners != null);
}

async function upsertDailySnapshots(rows: ArtistDailySnapshot[]): Promise<void> {
  for (const batch of chunkArray(rows, BatchSize)) {
    const response = await fetch(`${SUPABASE_URL}/rest/v1/artist_daily_listener_snapshots?on_conflict=normalized_name,snapshot_date`, {
      method: "POST",
      signal: AbortSignal.timeout(FetchTimeoutMs),
      headers: {
        "apikey": SERVICE_ROLE_KEY,
        "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
        "Content-Type": "application/json",
        "Prefer": "resolution=merge-duplicates",
      },
      body: JSON.stringify(batch),
    });
    if (!response.ok) throw new Error(await response.text());
  }
}

async function upsertProjectionFreezes(rows: ArtistProjectionFreeze[]): Promise<void> {
  for (const batch of chunkArray(rows, BatchSize)) {
    const response = await fetch(`${SUPABASE_URL}/rest/v1/artist_weekly_projection_freezes?on_conflict=normalized_name,week_start,formula_version`, {
      method: "POST",
      signal: AbortSignal.timeout(FetchTimeoutMs),
      headers: {
        "apikey": SERVICE_ROLE_KEY,
        "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
        "Content-Type": "application/json",
        "Prefer": "resolution=ignore-duplicates",
      },
      body: JSON.stringify(batch),
    });
    if (!response.ok) throw new Error(await response.text());
  }
}

async function upsertDailyScores(rows: ArtistDailyScore[]): Promise<void> {
  for (const batch of chunkArray(rows, BatchSize)) {
    const response = await fetch(`${SUPABASE_URL}/rest/v1/artist_daily_scores?on_conflict=normalized_name,week_start,score_date,formula_version`, {
      method: "POST",
      signal: AbortSignal.timeout(FetchTimeoutMs),
      headers: {
        "apikey": SERVICE_ROLE_KEY,
        "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
        "Content-Type": "application/json",
        "Prefer": "resolution=merge-duplicates",
      },
      body: JSON.stringify(batch),
    });
    if (!response.ok) throw new Error(await response.text());
  }
}

async function loadProjectionMap(weekStart: string, formulaVersion: string): Promise<Map<string, number>> {
  const response = await fetch(
    `${SUPABASE_URL}/rest/v1/artist_weekly_projection_freezes?select=normalized_name,projected_points&week_start=eq.${weekStart}&formula_version=eq.${encodeURIComponent(formulaVersion)}&limit=20000`,
    {
      signal: AbortSignal.timeout(FetchTimeoutMs),
      headers: {
        "apikey": SERVICE_ROLE_KEY,
        "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
      },
    },
  );
  if (!response.ok) return new Map();
  const rows: Array<{ normalized_name: string; projected_points: number }> = await response.json().catch(() => []);
  return new Map(rows.map((row) => [row.normalized_name, Number(row.projected_points)]));
}

async function loadWeeklyMetricsForBacktest(weeks: number): Promise<WeeklyMetric[]> {
  const since = new Date(Date.now() - weeks * 7 * 24 * 60 * 60 * 1000).toISOString().slice(0, 10);
  const pageSize = 1000;
  const rows: WeeklyMetric[] = [];
  let offset = 0;
  while (offset < 50_000) {
    const response = await fetch(
      `${SUPABASE_URL}/rest/v1/artist_weekly_metrics?select=spotify_id,normalized_name,name,week_start,role,listeners,weekly_listener_gain,weekly_listener_growth_percent,daily_listener_change,kworb_rank,source,raw_data&week_start=gte.${since}&order=week_start.asc`,
      {
        signal: AbortSignal.timeout(FetchTimeoutMs),
        headers: {
          "apikey": SERVICE_ROLE_KEY,
          "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
          "Range-Unit": "items",
          "Range": `${offset}-${offset + pageSize - 1}`,
        },
      },
    );
    if (!response.ok) throw new Error(await response.text());
    const page: WeeklyMetric[] = await response.json();
    rows.push(...page);
    if (page.length < pageSize) break;
    offset += page.length;
  }
  return rows;
}

async function runScoringBacktest(formulaVersion: string, formula: ScoringFormula, metrics: WeeklyMetric[]): Promise<Record<string, unknown>> {
  const scored = metrics
    .filter((metric) => metric.listeners != null)
    .map((metric) => ({ ...metric, points: scoreWeeklyMetric(metric, formula) }));
  const roles = ["Headliner", "Mainstay", "Rising", "Deep Cut"];
  const roleSummary = roles.map((role) => {
    const rows = scored.filter((metric) => metric.role === role);
    const points = rows.map((row) => row.points);
    const average = averageOf(points);
    const variance = averageOf(points.map((point) => Math.pow(point - average, 2)));
    const topFinishRate = rows.length === 0 ? 0 : rows.filter((row) => row.points >= 75).length / rows.length;
    const spikeRate = rows.length === 0 ? 0 : rows.filter((row) => row.points >= 85).length / rows.length;
    const floorRate = rows.length === 0 ? 0 : rows.filter((row) => row.points >= 45).length / rows.length;
    return {
      role,
      count: rows.length,
      averagePoints: roundOne(average),
      variance: roundOne(variance),
      topFinishRate: roundOne(topFinishRate * 100),
      spikeRate: roundOne(spikeRate * 100),
      floorRate: roundOne(floorRate * 100),
    };
  });
  const payload = {
    sampleSize: scored.length,
    formula,
    roleSummary,
    topWeeks: scored
      .sort((a, b) => b.points - a.points)
      .slice(0, 50)
      .map((metric) => ({
        name: metric.name,
        role: metric.role,
        week_start: metric.week_start,
        points: roundOne(metric.points),
        listeners: metric.listeners,
        weekly_listener_gain: metric.weekly_listener_gain,
        weekly_listener_growth_percent: metric.weekly_listener_growth_percent,
      })),
  };
  await saveScoringTrial(formulaVersion, payload);
  return payload;
}

async function saveScoringTrial(formulaVersion: string, result: Record<string, unknown>): Promise<void> {
  const sampleSize = typeof result.sampleSize === "number" ? result.sampleSize : 0;
  const response = await fetch(`${SUPABASE_URL}/rest/v1/scoring_formula_trials`, {
    method: "POST",
    signal: AbortSignal.timeout(FetchTimeoutMs),
    headers: {
      "apikey": SERVICE_ROLE_KEY,
      "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      formula_version: formulaVersion,
      result,
      sample_size: sampleSize,
    }),
  });
  if (!response.ok) throw new Error(await response.text());
}

function scoreWeeklyMetric(metric: WeeklyMetric, formula: ScoringFormula): number {
  const listeners = metric.listeners ?? 0;
  const audienceFloor = normalizedAudienceFloor(listeners) * 100;
  const growth = metric.weekly_listener_growth_percent ?? 0;
  const growthScore = (45 + Math.max(-30, Math.min(240, growth)) * 0.28);
  const gainScore = signedLogSignal(metric.weekly_listener_gain ?? metric.daily_listener_change ?? 0);
  const roleCeiling = metric.role === "Deep Cut"
    ? formula.deepCutCeilingBonus
    : metric.role === "Rising"
      ? formula.risingCeilingBonus
      : metric.role === "Mainstay"
        ? formula.mainstayCeilingBonus
        : formula.headlinerCeilingBonus;
  const roleFloor = metric.role === "Headliner"
    ? formula.headlinerFloorBonus
    : metric.role === "Mainstay"
      ? formula.mainstayFloorBonus
      : metric.role === "Rising"
        ? formula.risingFloorBonus
        : formula.deepCutFloorBonus;
  const volatility = metric.role === "Deep Cut"
    ? Math.max(-10, Math.min(24, growth * formula.deepCutVolatility))
    : metric.role === "Rising"
      ? Math.max(-7, Math.min(16, growth * formula.risingVolatility))
      : Math.max(-5, Math.min(9, growth * formula.stableVolatility));
  return Math.max(0, Math.min(100,
    audienceFloor * formula.audienceWeight +
    growthScore * formula.growthWeight +
    gainScore * formula.gainWeight +
    roleCeiling +
    roleFloor +
    volatility
  ));
}

function scoringFormulaFromBody(value: unknown): ScoringFormula {
  const row = value && typeof value === "object" ? value as Record<string, unknown> : {};
  const fallback: ScoringFormula = {
    audienceWeight: 0.24,
    growthWeight: 0.30,
    gainWeight: 0.24,
    deepCutCeilingBonus: 14,
    risingCeilingBonus: 9,
    mainstayCeilingBonus: 4,
    headlinerCeilingBonus: -4,
    headlinerFloorBonus: 8,
    mainstayFloorBonus: 4,
    risingFloorBonus: 1,
    deepCutFloorBonus: -3,
    deepCutVolatility: 0.10,
    risingVolatility: 0.07,
    stableVolatility: 0.035,
  };
  return Object.fromEntries(
    Object.entries(fallback).map(([key, defaultValue]) => {
      const parsed = numberFromUnknown(row[key]);
      return [key, parsed == null ? defaultValue : parsed];
    }),
  ) as ScoringFormula;
}

async function loadPastspotGainers(): Promise<MarketArtist[]> {
  const html = await fetchText("https://pastspot.com/gainers");
  const now = new Date().toISOString();
  const matches = html.matchAll(/href="\/artists\/([^"?]+)(?:\?[^"]*)?".{0,2200}?<img src="([^"]*)" alt="([^"]*)".{0,2200}?<p[^>]*>([^<]+)<\/p>.{0,1800}?lucide-trending-up.{0,900}?([+-]?[0-9.,]+[KMB]?).{0,700}?([+-]?[0-9.,]+)\s*(?:<!-- -->)?%/gis);

  const cards = [...matches].map((match) => {
    const name = cleanText(match[4] || match[3]);
    const weeklyGain = parseCompactNumber(match[5]);
    const growth = parseFloat((match[6] ?? "").replace(/,/g, ""));
    const listeners = estimateListeners(weeklyGain, growth);
    return {
      spotify_id: match[1] || null,
      name,
      normalized_name: normalizeName(name),
      image_url: htmlDecode(match[2] ?? "") || null,
      listeners,
      weekly_listener_gain: weeklyGain,
      weekly_listener_growth_percent: Number.isFinite(growth) ? growth : null,
      monthly_listener_change_percent: Number.isFinite(growth) ? growth : null,
      daily_listener_change: weeklyGain == null ? null : Math.round(weeklyGain / 7),
      source: "Pastspot",
      score_status: Number.isFinite(growth) ? `Weekly growth +${growth.toFixed(1)}%` : "Weekly growth",
      provider_url: match[1] ? `https://pastspot.com/artists/${match[1]}` : null,
      updated_at: now,
    };
  }).filter((artist) => artist.name.length > 0);

  return await parallelMap(cards, 8, async (artist) => {
    if (!artist.spotify_id) return artist;
    const detail = await loadPastspotArtistDetail(artist.spotify_id).catch(() => null);
    if (!detail) return artist;
    return {
      ...artist,
      image_url: detail.image_url ?? artist.image_url ?? null,
      listeners: detail.listeners ?? artist.listeners ?? null,
      weekly_listener_growth_percent: detail.weekly_listener_growth_percent ?? artist.weekly_listener_growth_percent ?? null,
      monthly_listener_change_percent: detail.monthly_listener_change_percent ?? artist.monthly_listener_change_percent ?? null,
      data_date: detail.data_date ? detail.data_date.slice(0, 10) : artist.data_date ?? null,
      listener_history: detail.listener_history ?? artist.listener_history ?? [],
      top_tracks: detail.top_tracks ?? artist.top_tracks ?? [],
      discography: detail.discography ?? artist.discography ?? [],
      top_cities: detail.top_cities ?? artist.top_cities ?? [],
      updated_at: detail.data_date ?? artist.updated_at,
    };
  });
}

type PastspotSnapshotState = {
  latest_seen_date?: string | null;
  first_seen_at?: string | null;
  processed_date?: string | null;
  processed_at?: string | null;
  last_checked_at?: string | null;
  check_payload?: Record<string, unknown>;
};

async function loadPastspotLatestSnapshotDate(): Promise<string | null> {
  const detail = await loadPastspotArtistDetail("0du5cEVh5yTK9QJze8zA0C");
  return detail.data_date ? detail.data_date.slice(0, 10) : null;
}

async function loadPastspotSnapshotState(): Promise<PastspotSnapshotState> {
  const response = await fetch(`${SUPABASE_URL}/rest/v1/pastspot_snapshot_state?select=latest_seen_date,first_seen_at,processed_date,processed_at,last_checked_at,check_payload&id=eq.true&limit=1`, {
    signal: AbortSignal.timeout(FetchTimeoutMs),
    headers: {
      "apikey": SERVICE_ROLE_KEY,
      "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
    },
  });
  if (!response.ok) return {};
  const rows = await response.json().catch(() => []);
  return rows[0] ?? {};
}

async function savePastspotSnapshotState(state: PastspotSnapshotState): Promise<void> {
  const response = await fetch(`${SUPABASE_URL}/rest/v1/pastspot_snapshot_state?on_conflict=id`, {
    method: "POST",
    signal: AbortSignal.timeout(FetchTimeoutMs),
    headers: {
      "apikey": SERVICE_ROLE_KEY,
      "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
      "Content-Type": "application/json",
      "Prefer": "resolution=merge-duplicates",
    },
    body: JSON.stringify([{ id: true, ...state }]),
  });
  if (!response.ok) throw new Error(await response.text());
}

async function fillMissingPastspotArtwork(artist: MarketArtist): Promise<MarketArtist> {
  if ((artist.image_url && artist.current_listeners != null) || !artist.name) return artist;
  const detailById = artist.spotify_id
    ? await loadPastspotArtistDetail(artist.spotify_id).catch(() => null)
    : null;
  let enriched = artist;
  if (detailById?.image_url || detailById?.listeners != null) {
    enriched = {
      ...artist,
      image_url: detailById.image_url ?? artist.image_url ?? null,
      listeners: detailById.listeners ?? artist.listeners ?? null,
      monthly_listener_change_percent: detailById.monthly_listener_change_percent ?? artist.monthly_listener_change_percent ?? null,
      data_date: detailById.data_date ? detailById.data_date.slice(0, 10) : artist.data_date ?? null,
      listener_history: firstNonEmptyArray(detailById.listener_history, artist.listener_history),
      top_tracks: firstNonEmptyArray(detailById.top_tracks, artist.top_tracks),
      discography: firstNonEmptyArray(detailById.discography, artist.discography),
      top_cities: firstNonEmptyArray(detailById.top_cities, artist.top_cities),
    };
    if (enriched.image_url && enriched.current_listeners != null) return enriched;
  }
  const search = await loadPastspotSearchBasics(artist.name).catch(() => null);
  if (!search) return enriched;
  const now = new Date().toISOString();
  return {
    ...enriched,
    spotify_id: enriched.spotify_id ?? search.spotify_id ?? null,
    image_url: search.image_url ?? enriched.image_url ?? null,
    current_listeners: search.current_listeners ?? enriched.current_listeners ?? null,
    current_listeners_observed_at: search.current_listeners != null ? now : enriched.current_listeners_observed_at ?? null,
    current_listeners_source: search.current_listeners != null ? "Pastspot search" : enriched.current_listeners_source ?? null,
    provider_url: enriched.provider_url ?? search.provider_url ?? null,
  };
}

async function loadPastspotSearchBasics(name: string): Promise<Pick<MarketArtist, "spotify_id" | "image_url" | "provider_url" | "current_listeners"> | null> {
  const response = await fetch(`https://pastspot.com/api/search?q=${encodeURIComponent(name)}`, {
    signal: AbortSignal.timeout(FetchTimeoutMs),
    headers: { "User-Agent": "BreakoutFantasy/1.0" },
  });
  if (!response.ok) return null;
  const payload = await response.json().catch(() => null) as { artists?: unknown[] } | null;
  const candidates = (payload?.artists ?? [])
    .map((entry) => entry && typeof entry === "object" ? entry as Record<string, unknown> : null)
    .filter((entry): entry is Record<string, unknown> => entry != null)
    .map((entry) => {
      const spotifyId = stringFromUnknown(entry.spotifyId) ?? stringFromUnknown(entry.spotify_id);
      const displayName = stringFromUnknown(entry.name) ?? "";
      return {
        spotify_id: spotifyId,
        image_url: stringFromUnknown(entry.imageUrl) ?? stringFromUnknown(entry.image_url),
        display_name: cleanText(displayName),
        current_listeners: numberFromUnknown(entry.monthlyListeners ?? entry.monthly_listeners),
        provider_url: spotifyId ? `https://pastspot.com/artists/${spotifyId}` : null,
      };
    })
    .filter((candidate) => candidate.spotify_id && (candidate.image_url || candidate.current_listeners != null));
  const normalized = normalizeName(name);
  const exact = candidates.find((candidate) => normalizeName(candidate.display_name) === normalized);
  const prefix = candidates.find((candidate) => normalizeName(candidate.display_name).startsWith(normalized) || normalized.startsWith(normalizeName(candidate.display_name)));
  const best = exact ?? prefix ?? candidates[0];
  return best ? {
    spotify_id: best.spotify_id,
    image_url: best.image_url,
    current_listeners: best.current_listeners ?? null,
    provider_url: best.provider_url,
  } : null;
}

async function loadPastspotArtistDetail(spotifyId: string): Promise<PastspotDetail> {
  const html = await fetchText(`https://pastspot.com/artists/${spotifyId}`);
  const image = firstMatch(html, /<img src="([^"]*)" alt="[^"]*" class="object-cover w-full h-full bg-muted rounded-full/);
  const listenersText = firstMatch(html, /Monthly Listeners[\s\S]{0,900}?<div class="text-2xl font-bold text-white">([^<]+)<\/div>/);
  const changeText = firstMatch(html, /Monthly Listeners[\s\S]{0,1100}?<p class="text-xs text-(?:green|red)-400">([+-]?[0-9.,]+)% change<\/p>/);
  const dateText = firstMatch(html, /Showing data from:\s*([^<]+)</);
  const parsedChange = changeText ? parseFloat(changeText.replace(/,/g, "")) : null;
  return {
    image_url: image ? htmlDecode(image) : null,
    listeners: parseCompactNumber(listenersText ?? undefined),
    weekly_listener_growth_percent: parsedChange,
    monthly_listener_change_percent: parsedChange,
    data_date: dateText ? pastspotDateToIso(dateText) : null,
    listener_history: extractPastspotListenerHistory(html),
    top_tracks: extractPastspotNamedRows(html, "Top Tracks"),
    discography: extractPastspotNamedRows(html, "Albums"),
    top_cities: extractPastspotNamedRows(html, "Top Cities"),
  };
}

async function loadKworbListeners(): Promise<MarketArtist[]> {
  const html = await fetchText("https://kworb.net/spotify/listeners.html");
  const now = new Date().toISOString();
  const rows = [...html.matchAll(/<tr[^>]*>([\s\S]*?)<\/tr>/gi)];
  return rows.map((row) => {
    const cells = tableCells(row[1] ?? "");
    if (cells.length < 4) return null;
    const rank = parseInteger(cells[0]);
    const name = cleanText(cells[1]);
    const listeners = parseInteger(cells[2]);
    const dailyChange = parseInteger(cells[3]);
    const spotifyId = firstMatch(row[1] ?? "", /open\.spotify\.com\/artist\/([A-Za-z0-9]+)/i)
      ?? firstMatch(row[1] ?? "", /\/spotify\/artist\/([A-Za-z0-9]+)(?:_songs)?\.html/i)
      ?? firstMatch(row[1] ?? "", /artist\/([A-Za-z0-9]+)(?:_songs)?\.html/i);
    if (!rank || !name || !listeners) return null;
    return {
      spotify_id: spotifyId,
      name,
      normalized_name: normalizeName(name),
      listeners,
      daily_listener_change: dailyChange,
      kworb_rank: rank,
      source: "Spotify listener data",
      score_status: dailyChange == null ? "Audience signal" : `Daily move ${formatSigned(dailyChange)}`,
      provider_url: "https://kworb.net/spotify/listeners.html",
      updated_at: now,
    };
  }).filter((artist): artist is MarketArtist => artist != null);
}

type MusicMetricsVaultLoadOptions = {
  offset: number;
  limit: number;
  countryOffset: number;
  countryLimit: number;
};

async function loadMusicMetricsVaultArtists(options: MusicMetricsVaultLoadOptions): Promise<MarketArtist[]> {
  const countriesHtml = await fetchText("https://www.musicmetricsvault.com/countries");
  const countryUrls = [...countriesHtml.matchAll(/href="https:\/\/www\.musicmetricsvault\.com(\/countries\/[^"]+)"/gi)]
    .map((match) => `https://www.musicmetricsvault.com${match[1]}`)
    .filter((value, index, values) => values.indexOf(value) === index)
    .slice(options.countryOffset, options.countryOffset + options.countryLimit);
  const countryPages = await parallelMap(countryUrls, 4, async (url) => fetchText(url).catch(() => ""));
  const artistUrls = countryPages
    .flatMap(extractMusicMetricsVaultArtistUrls)
    .filter((value, index, values) => values.findIndex((other) => other.spotifyId === value.spotifyId) === index)
    .slice(options.offset, options.offset + options.limit);
  return (await parallelMap(artistUrls, 4, async (artist) => {
    const detail = await fetchText(artist.url).catch(() => "");
    if (!detail) return null;
    const listeners = parseMusicMetricsVaultListeners(detail);
    if (listeners == null) return null;
    const imageUrl = firstMatch(detail, /<meta property="og:image" content="([^"]+)"/);
    return {
      spotify_id: artist.spotifyId,
      name: artist.name,
      normalized_name: normalizeName(artist.name),
      image_url: imageUrl ? htmlDecode(imageUrl) : null,
      listeners,
      source: "Global artist index",
      score_status: "Audience indexed",
      provider_url: artist.url,
      updated_at: new Date().toISOString(),
    };
  })).filter((artist): artist is MarketArtist => artist != null);
}

function extractMusicMetricsVaultArtistUrls(html: string): Array<{ name: string; url: string; spotifyId: string }> {
  const jsonBlocks = [...html.matchAll(/<script type="application\/ld\+json">([\s\S]*?)<\/script>/gi)];
  const fromJson = jsonBlocks.flatMap((block) => {
    try {
      const parsed = JSON.parse(block[1]);
      const items: MusicMetricsVaultListItem[] = Array.isArray(parsed?.itemListElement) ? parsed.itemListElement : [];
      return items.map((item) => {
        const url = String(item?.item?.url ?? "");
        const spotifyId = url.split("/").filter(Boolean).at(-1) ?? "";
        return {
          name: cleanText(String(item?.item?.name ?? "")),
          url,
          spotifyId,
        };
      });
    } catch {
      return [];
    }
  });
  const fromLinks = [...html.matchAll(/href="(https:\/\/www\.musicmetricsvault\.com\/artists\/[^"]+\/([A-Za-z0-9]+))"[^>]*>([^<]+)</gi)]
    .map((match) => ({
      url: match[1],
      spotifyId: match[2],
      name: cleanText(match[3]),
    }))
    .filter((artist) => artist.name && artist.spotifyId);
  const bySpotifyId = new Map<string, { name: string; url: string; spotifyId: string }>();
  for (const artist of [...fromJson, ...fromLinks]) {
    if (artist.name && artist.url && artist.spotifyId && !bySpotifyId.has(artist.spotifyId)) {
      bySpotifyId.set(artist.spotifyId, artist);
    }
  }
  return [...bySpotifyId.values()];
}

function parseMusicMetricsVaultListeners(html: string): number | null {
  const description = firstMatch(html, /<meta name="description" content="([^"]+)"/);
  const listenerText = description?.match(/([0-9,.]+)\s+monthly listeners/i)?.[1];
  return parseInteger(listenerText);
}

function mergeArtists(artists: MarketArtist[]): MarketArtist[] {
  const byName = new Map<string, MarketArtist>();
  for (const artist of artists) {
    const current = byName.get(artist.normalized_name);
    if (!current) {
      byName.set(artist.normalized_name, artist);
      continue;
    }
    byName.set(artist.normalized_name, {
      ...current,
      ...artist,
      spotify_id: current.spotify_id ?? artist.spotify_id ?? null,
      image_url: current.image_url ?? artist.image_url ?? null,
      image_url_card: current.image_url_card ?? artist.image_url_card ?? null,
      image_url_full: current.image_url_full ?? artist.image_url_full ?? null,
      current_listeners: maxOfNumbers(current.current_listeners, artist.current_listeners, current.listeners, artist.listeners),
      current_listeners_observed_at: current.current_listeners_observed_at ?? artist.current_listeners_observed_at ?? current.updated_at ?? artist.updated_at ?? null,
      current_listeners_source: current.current_listeners_source ?? artist.current_listeners_source ?? current.source ?? artist.source ?? null,
      snapshot_listeners: current.snapshot_listeners ?? artist.snapshot_listeners ?? current.listeners ?? artist.listeners ?? null,
      snapshot_previous_listeners: current.snapshot_previous_listeners ?? artist.snapshot_previous_listeners ?? null,
      snapshot_date: current.snapshot_date ?? artist.snapshot_date ?? current.data_date ?? artist.data_date ?? null,
      listeners: maxNumber(current.listeners, artist.listeners),
      weekly_listener_gain: current.weekly_listener_gain ?? artist.weekly_listener_gain ?? null,
      weekly_listener_growth_percent: current.weekly_listener_growth_percent ?? artist.weekly_listener_growth_percent ?? null,
      monthly_listener_change_percent: current.monthly_listener_change_percent ?? artist.monthly_listener_change_percent ?? null,
      daily_listener_change: current.daily_listener_change ?? artist.daily_listener_change ?? null,
      kworb_rank: current.kworb_rank ?? artist.kworb_rank ?? null,
      data_date: current.data_date ?? artist.data_date ?? null,
      listener_history: firstNonEmptyArray(current.listener_history, artist.listener_history),
      top_tracks: firstNonEmptyArray(current.top_tracks, artist.top_tracks),
      discography: firstNonEmptyArray(current.discography, artist.discography),
      top_cities: firstNonEmptyArray(current.top_cities, artist.top_cities),
      source: [current.source, artist.source].some((source) => source.includes("Pastspot"))
        ? "Pastspot + Spotify listener data"
        : artist.source,
      score_status: current.weekly_listener_growth_percent != null ? current.score_status : artist.score_status,
    });
  }
  return [...byName.values()].sort((a, b) =>
    (b.weekly_listener_growth_percent ?? -999) - (a.weekly_listener_growth_percent ?? -999) ||
    (b.weekly_listener_gain ?? -1) - (a.weekly_listener_gain ?? -1) ||
    (b.listeners ?? 0) - (a.listeners ?? 0)
  );
}

function normalizeMarketArtist(artist: MarketArtist): MarketArtist {
  const artwork = normalizeArtworkUrls(artist);
  const currentListeners = artwork.current_listeners ?? null;
  const snapshotListeners = artwork.snapshot_listeners ?? snapshotListenersFromHistory(artwork) ?? artwork.listeners ?? null;
  const snapshotDate = artwork.snapshot_date ?? artwork.data_date ?? latestHistoryDate(artwork) ?? null;
  const weeklyGain = artwork.weekly_listener_gain ?? gainFromSnapshot(snapshotListeners, artwork.weekly_listener_growth_percent ?? artwork.monthly_listener_change_percent ?? null);
  const previousSnapshotListeners = artwork.snapshot_previous_listeners ?? (
    snapshotListeners != null && weeklyGain != null ? snapshotListeners - weeklyGain : null
  );
  const sinceSnapshot = currentListeners != null && snapshotListeners != null ? currentListeners - snapshotListeners : null;
  const sinceSnapshotPercent = sinceSnapshot != null && snapshotListeners != null && snapshotListeners > 0
    ? (sinceSnapshot / snapshotListeners) * 100
    : null;
  return {
    spotify_id: artwork.spotify_id ?? null,
    name: artwork.name,
    normalized_name: artwork.normalized_name,
    image_url: artwork.image_url ?? null,
    image_url_card: artwork.image_url_card ?? null,
    image_url_full: artwork.image_url_full ?? artwork.image_url ?? null,
    current_listeners: currentListeners,
    current_listeners_observed_at: currentListeners != null ? artwork.current_listeners_observed_at ?? artwork.updated_at ?? null : null,
    current_listeners_source: currentListeners != null ? artwork.current_listeners_source ?? artwork.source ?? null : null,
    snapshot_listeners: snapshotListeners,
    snapshot_previous_listeners: previousSnapshotListeners,
    snapshot_date: snapshotDate ? snapshotDate.slice(0, 10) : null,
    listener_change_since_snapshot: sinceSnapshot,
    listener_change_since_snapshot_percent: sinceSnapshotPercent,
    days_since_snapshot: snapshotDate ? daysSince(snapshotDate) : null,
    listeners: currentListeners ?? snapshotListeners,
    weekly_listener_gain: weeklyGain,
    weekly_listener_growth_percent: artwork.weekly_listener_growth_percent ?? null,
    monthly_listener_change_percent: artwork.monthly_listener_change_percent ?? null,
    daily_listener_change: artwork.daily_listener_change ?? null,
    kworb_rank: artwork.kworb_rank ?? null,
    source: artwork.source,
    score_status: artwork.score_status,
    provider_url: artwork.provider_url ?? null,
    data_date: artwork.data_date ?? null,
    listener_history: artwork.listener_history ?? [],
    top_tracks: artwork.top_tracks ?? [],
    discography: artwork.discography ?? [],
    top_cities: artwork.top_cities ?? [],
    updated_at: artwork.updated_at,
  };
}

function snapshotListenersFromHistory(artist: MarketArtist): number | null {
  const history = Array.isArray(artist.listener_history) ? artist.listener_history : [];
  const latest = history
    .map((entry) => entry && typeof entry === "object" ? entry as Record<string, unknown> : null)
    .filter((entry): entry is Record<string, unknown> => entry != null && typeof entry.date === "string")
    .sort((a, b) => String(b.date).localeCompare(String(a.date)))[0];
  return latest ? numberFromUnknown(latest.listeners) : null;
}

function latestHistoryDate(artist: MarketArtist): string | null {
  const history = Array.isArray(artist.listener_history) ? artist.listener_history : [];
  return history
    .map((entry) => entry && typeof entry === "object" ? String((entry as Record<string, unknown>).date ?? "") : "")
    .filter(Boolean)
    .sort((a, b) => b.localeCompare(a))[0] ?? null;
}

function gainFromSnapshot(listeners: number | null, growth: number | null): number | null {
  if (listeners == null || growth == null || growth === -100) return null;
  return Math.round(listeners - listeners / (1 + growth / 100));
}

function daysSince(date: string): number | null {
  const parsed = Date.parse(date);
  if (!Number.isFinite(parsed)) return null;
  return Math.max(0, Math.floor((Date.now() - parsed) / (24 * 60 * 60 * 1000)));
}

function normalizeArtworkUrls(artist: MarketArtist): MarketArtist {
  const raw = cleanUrl(artist.image_url_full) ?? cleanUrl(artist.image_url) ?? null;
  const full = raw ? fullArtworkUrl(raw) : null;
  const card = cleanUrl(artist.image_url_card) ?? (raw ? cardArtworkUrl(raw) : null);
  return {
    ...artist,
    image_url: full ?? raw,
    image_url_full: full ?? raw,
    image_url_card: card,
  };
}

function cleanUrl(value?: string | null): string | null {
  const clean = value?.trim();
  if (!clean || clean.toLowerCase() === "null") return null;
  return clean;
}

function cardArtworkUrl(url: string): string {
  return url
    .replaceAll("0000e5eb", "00005174")
    .replaceAll("0000f178", "00005174")
    .replaceAll("1000x1000", "250x250")
    .replaceAll("640x640", "250x250")
    .replaceAll("500x500", "250x250")
    .replaceAll("300x300", "250x250");
}

function fullArtworkUrl(url: string): string {
  return url
    .replaceAll("00005174", "0000e5eb")
    .replaceAll("56x56", "1000x1000")
    .replaceAll("250x250", "1000x1000")
    .replaceAll("500x500", "1000x1000");
}

function extractPastspotListenerHistory(html: string): unknown[] {
  const dataDate = pastspotDateToIso(firstMatch(html, /Showing data from:\s*([^<]+)</) ?? "");
  const listenersText = firstMatch(html, /Monthly Listeners[\s\S]{0,900}?<div class="text-2xl font-bold text-white">([^<]+)<\/div>/);
  const changeText = firstMatch(html, /Monthly Listeners[\s\S]{0,1100}?<p class="text-xs text-(?:green|red)-400">([+-]?[0-9.,]+)% change<\/p>/);
  const listeners = parseCompactNumber(listenersText ?? undefined);
  const changePercent = changeText ? parseFloat(changeText.replace(/,/g, "")) : null;
  if (!dataDate && listeners == null && changePercent == null) return [];
  return [{
    date: dataDate ? dataDate.slice(0, 10) : null,
    listeners,
    change_percent: Number.isFinite(changePercent) ? changePercent : null,
  }];
}

function extractPastspotNamedRows(html: string, heading: string): unknown[] {
  const headingIndex = html.toLowerCase().indexOf(heading.toLowerCase());
  if (headingIndex < 0) return [];
  const section = html.slice(headingIndex, headingIndex + 45_000);
  const tableRows = [...section.matchAll(/<tr[^>]*>([\s\S]*?)<\/tr>/gi)]
    .map((row) => tableCells(row[1] ?? ""))
    .filter((cells) => cells.length >= 2)
    .slice(0, 25);
  if (tableRows.length > 0) {
    return tableRows.map((cells) => ({
      rank: parseInteger(cells[0]),
      name: cells[1] ?? null,
      secondary: cells[2] ?? null,
      tertiary: cells[3] ?? null,
      value: cells.at(-1) ?? null,
    }));
  }
  return [...section.matchAll(/<div[^>]*class="[^"]*(?:grid|flex)[^"]*"[^>]*>([\s\S]{0,1800}?)<\/div>/gi)]
    .map((row) => cleanText(row[1] ?? ""))
    .filter((value) => value.length >= 3 && value.length <= 240 && !value.includes("Showing data from"))
    .slice(0, 25)
    .map((text, index) => ({ rank: index + 1, text }));
}

async function fetchText(url: string): Promise<string> {
  const response = await fetch(url, {
    signal: AbortSignal.timeout(FetchTimeoutMs),
    headers: { "User-Agent": "BreakoutMarketCache/1.0" },
  });
  if (!response.ok) throw new Error(`${url} returned ${response.status}`);
  return await response.text();
}

function clampInteger(value: unknown, min: number, max: number, fallback: number): number {
  const parsed = typeof value === "number" ? value : parseInt(String(value ?? ""), 10);
  if (!Number.isFinite(parsed)) return fallback;
  return Math.max(min, Math.min(max, Math.floor(parsed)));
}

function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "Content-Type": "application/json" },
  });
}

function normalizeName(value: string): string {
  return cleanText(value).toLowerCase().replace(/\s+/g, " ");
}

function cleanText(value: string): string {
  return htmlDecode(value).replace(/<[^>]+>/g, " ").replace(/\s+/g, " ").trim();
}

function htmlDecode(value: string): string {
  return value
    .replaceAll("&amp;", "&")
    .replaceAll("&quot;", "\"")
    .replaceAll("&#039;", "'")
    .replaceAll("&apos;", "'")
    .replaceAll("&nbsp;", " ");
}

function parseCompactNumber(value: string | undefined): number | null {
  if (!value) return null;
  const clean = value.replace(/,/g, "").trim();
  const numeric = parseFloat(clean);
  if (!Number.isFinite(numeric)) return null;
  const suffix = clean.at(-1)?.toUpperCase();
  const multiplier = suffix === "B" ? 1_000_000_000 : suffix === "M" ? 1_000_000 : suffix === "K" ? 1_000 : 1;
  return Math.round(numeric * multiplier);
}

function firstMatch(value: string, pattern: RegExp): string | null {
  return value.match(pattern)?.[1] ?? null;
}

function pastspotDateToIso(value: string): string | null {
  const parsed = Date.parse(`${cleanText(value)} 12:00:00 UTC`);
  return Number.isFinite(parsed) ? new Date(parsed).toISOString() : null;
}

function tableCells(rowHtml: string): string[] {
  return [...rowHtml.matchAll(/<td[^>]*>([\s\S]*?)<\/td>/gi)]
    .map((match) => cleanText(match[1] ?? ""));
}

function parseInteger(value: string | undefined): number | null {
  if (!value) return null;
  const parsed = parseInt(value.replace(/,/g, "").replace(/\s+/g, ""), 10);
  return Number.isFinite(parsed) ? parsed : null;
}

function estimateListeners(gain: number | null, growth: number): number | null {
  if (gain == null || !Number.isFinite(growth) || growth <= 0) return gain;
  return Math.max(gain, Math.round(gain + gain / (growth / 100)));
}

function maxNumber(a?: number | null, b?: number | null): number | null {
  if (a == null) return b ?? null;
  if (b == null) return a;
  return Math.max(a, b);
}

function maxOfNumbers(...values: Array<number | null | undefined>): number | null {
  const clean = values.filter((value): value is number => typeof value === "number" && Number.isFinite(value));
  return clean.length === 0 ? null : Math.max(...clean);
}

function numberFromUnknown(value: unknown): number | null {
  if (typeof value === "number" && Number.isFinite(value)) return value;
  if (typeof value !== "string") return null;
  const parsed = Number(value.replace(/,/g, ""));
  return Number.isFinite(parsed) ? parsed : null;
}

function stringFromUnknown(value: unknown): string | null {
  if (typeof value !== "string") return null;
  const clean = value.trim();
  return clean.length > 0 ? clean : null;
}

function weekStartIso(value: string): string {
  const parsed = new Date(value);
  const date = Number.isFinite(parsed.getTime()) ? parsed : new Date();
  const utcDay = date.getUTCDay();
  const daysSinceMonday = (utcDay + 6) % 7;
  date.setUTCHours(0, 0, 0, 0);
  date.setUTCDate(date.getUTCDate() - daysSinceMonday);
  return date.toISOString().slice(0, 10);
}

function weekStartForDate(value: string): string {
  return weekStartIso(`${value}T00:00:00.000Z`);
}

function roleForListeners(listeners: number | null): string {
  if (listeners == null) return "Unknown";
  if (listeners >= 40_000_000) return "Headliner";
  if (listeners >= 15_000_000) return "Mainstay";
  if (listeners >= 1_000_000) return "Rising";
  return "Deep Cut";
}

function normalizedAudienceFloor(listeners: number | null): number {
  if (listeners == null || listeners <= 0) return 0;
  return Math.max(0, Math.min(1, (Math.log10(Math.max(listeners, 10_000)) - 4) / 4.25));
}

function signedLogSignal(value: number): number {
  if (!Number.isFinite(value) || value === 0) return 50;
  const magnitude = Math.max(0, Math.min(1, Math.log10(Math.abs(value) + 1) / 6));
  return Math.max(0, Math.min(100, value > 0 ? 50 + magnitude * 50 : 50 - magnitude * 50));
}

function averageOf(values: number[]): number {
  return values.length === 0 ? 0 : values.reduce((sum, value) => sum + value, 0) / values.length;
}

function roundOne(value: number): number {
  return Math.round(value * 10) / 10;
}

function firstNonEmptyArray<T>(a?: T[] | null, b?: T[] | null): T[] {
  return a && a.length > 0 ? a : b ?? [];
}

function formatSigned(value: number): string {
  return value >= 0 ? `+${value.toLocaleString()}` : value.toLocaleString();
}

function chunkArray<T>(values: T[], size: number): T[][] {
  const chunks: T[][] = [];
  for (let index = 0; index < values.length; index += size) {
    chunks.push(values.slice(index, index + size));
  }
  return chunks;
}

async function parallelMap<T, R>(
  values: T[],
  concurrency: number,
  mapper: (value: T) => Promise<R>,
): Promise<R[]> {
  const results: R[] = new Array(values.length);
  let nextIndex = 0;
  const workers = Array.from({ length: Math.min(concurrency, values.length) }, async () => {
    while (nextIndex < values.length) {
      const currentIndex = nextIndex++;
      results[currentIndex] = await mapper(values[currentIndex]);
    }
  });
  await Promise.all(workers);
  return results;
}
