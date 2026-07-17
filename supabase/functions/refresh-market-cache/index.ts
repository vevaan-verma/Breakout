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

type MarketArtist = {
  spotify_id?: string | null;
  name: string;
  normalized_name: string;
  image_url?: string | null;
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
  role: string;
  listeners: number | null;
  weekly_listener_gain: number | null;
  weekly_listener_growth_percent: number | null;
  daily_listener_change: number | null;
  kworb_rank: number | null;
  source: string | null;
  raw_data: Record<string, unknown>;
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

    if (mode === "artwork") {
      const limit = clampInteger(body?.limit, 1, MaxArtworkLimit, DefaultArtworkLimit);
      const artists = await loadArtistsMissingArtwork(limit);
      if (artists.length > 0) {
        await markArtworkAttempts(artists);
      }
      const filled = (await parallelMap(artists, 4, fillMissingPastspotArtwork))
        .filter((artist) => artist.image_url);
      if (filled.length > 0) {
        await upsertMarketArtists(filled.map(normalizeMarketArtist));
      }
      const remainingMissing = await countMissingArtwork();
      return json({
        mode,
        limit,
        attempted: artists.length,
        found: filled.length,
        remainingMissing,
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
  const response = await fetch(`${SUPABASE_URL}/rest/v1/market_artist_cache?select=normalized_name,image_url&image_url=not.is.null&normalized_name=in.(${encodeURIComponent(names)})`, {
    signal: AbortSignal.timeout(FetchTimeoutMs),
    headers: {
      "apikey": SERVICE_ROLE_KEY,
      "Authorization": `Bearer ${SERVICE_ROLE_KEY}`,
    },
  }).catch(() => null);
  if (!response?.ok) return artists;
  const rows = await response.json().catch(() => []);
  const byName = new Map<string, string>();
  for (const row of rows) {
    if (row?.normalized_name && row?.image_url) byName.set(row.normalized_name, row.image_url);
  }
  return artists.map((artist) => artist.image_url ? artist : {
    ...artist,
    image_url: byName.get(artist.normalized_name) ?? artist.image_url ?? null,
  });
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

async function countMissingArtwork(): Promise<number> {
  const response = await fetch(
    `${SUPABASE_URL}/rest/v1/market_artist_cache?select=normalized_name&image_url=is.null`,
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

async function loadArtistsMissingArtwork(limit: number): Promise<MarketArtist[]> {
  const retryBefore = new Date(Date.now() - ArtworkRetryDelayHours * 60 * 60 * 1000).toISOString();
  const query = new URLSearchParams({
    select: "spotify_id,name,normalized_name,image_url,listeners,weekly_listener_gain,weekly_listener_growth_percent,monthly_listener_change_percent,daily_listener_change,kworb_rank,source,score_status,provider_url,data_date,listener_history,top_tracks,discography,top_cities,artwork_last_attempted_at,artwork_attempt_count,updated_at",
    image_url: "is.null",
    artwork_attempt_count: `lt.${MaxArtworkAttempts}`,
    or: `(artwork_last_attempted_at.is.null,artwork_last_attempted_at.lt.${retryBefore})`,
    order: "artwork_last_attempted_at.asc.nullsfirst,artwork_attempt_count.asc,listeners.desc.nullslast",
  });
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
      `${SUPABASE_URL}/rest/v1/market_artist_cache?select=spotify_id,name,normalized_name,image_url,listeners,weekly_listener_gain,weekly_listener_growth_percent,monthly_listener_change_percent,daily_listener_change,kworb_rank,source,score_status,provider_url,data_date,listener_history,top_tracks,discography,top_cities,updated_at&updated_at=gte.${encodeURIComponent(new Date(Date.now() - 14 * 24 * 60 * 60 * 1000).toISOString())}&order=listeners.desc.nullslast`,
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
  const date = artist.data_date ?? artist.updated_at ?? new Date().toISOString();
  return {
    spotify_id: artist.spotify_id ?? null,
    normalized_name: artist.normalized_name,
    name: artist.name,
    week_start: weekStartIso(date),
    role: roleForListeners(artist.listeners ?? null),
    listeners: artist.listeners ?? null,
    weekly_listener_gain: artist.weekly_listener_gain ?? null,
    weekly_listener_growth_percent: artist.weekly_listener_growth_percent ?? artist.monthly_listener_change_percent ?? null,
    daily_listener_change: artist.daily_listener_change ?? null,
    kworb_rank: artist.kworb_rank ?? null,
    source: artist.source ?? null,
    raw_data: {
      data_date: artist.data_date ?? null,
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
  return {
    spotify_id: artist.spotify_id ?? null,
    normalized_name: artist.normalized_name,
    name: artist.name,
    week_start: weekStartIso(date),
    role: roleForListeners(listeners),
    listeners,
    weekly_listener_gain: gain,
    weekly_listener_growth_percent: growth,
    daily_listener_change: gain == null ? null : Math.round(gain / 7),
    kworb_rank: null,
    source: "Pastspot listener history",
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

async function fillMissingPastspotArtwork(artist: MarketArtist): Promise<MarketArtist> {
  if (artist.image_url || !artist.name) return artist;
  const detailById = artist.spotify_id
    ? await loadPastspotArtistDetail(artist.spotify_id).catch(() => null)
    : null;
  if (detailById?.image_url) {
    return {
      ...artist,
      image_url: detailById.image_url,
      listeners: detailById.listeners ?? artist.listeners ?? null,
      monthly_listener_change_percent: detailById.monthly_listener_change_percent ?? artist.monthly_listener_change_percent ?? null,
      data_date: detailById.data_date ? detailById.data_date.slice(0, 10) : artist.data_date ?? null,
      listener_history: firstNonEmptyArray(detailById.listener_history, artist.listener_history),
      top_tracks: firstNonEmptyArray(detailById.top_tracks, artist.top_tracks),
      discography: firstNonEmptyArray(detailById.discography, artist.discography),
      top_cities: firstNonEmptyArray(detailById.top_cities, artist.top_cities),
    };
  }
  const search = await loadPastspotSearchArtwork(artist.name).catch(() => null);
  if (!search) return artist;
  return {
    ...artist,
    spotify_id: artist.spotify_id ?? search.spotify_id ?? null,
    image_url: search.image_url ?? artist.image_url ?? null,
    provider_url: artist.provider_url ?? search.provider_url ?? null,
  };
}

async function loadPastspotSearchArtwork(name: string): Promise<Pick<MarketArtist, "spotify_id" | "image_url" | "provider_url"> | null> {
  const html = await fetchText(`https://pastspot.com/search?q=${encodeURIComponent(name)}`);
  const candidates = [...html.matchAll(/href="\/artists\/([^"?]+)(?:\?[^"]*)?".{0,1800}?<img src="([^"]*)" alt="([^"]*)"/gis)]
    .map((match) => ({
      spotify_id: match[1] || null,
      image_url: htmlDecode(match[2] ?? "") || null,
      display_name: cleanText(match[3] ?? ""),
      provider_url: match[1] ? `https://pastspot.com/artists/${match[1]}` : null,
    }))
    .filter((candidate) => candidate.spotify_id && candidate.image_url);
  const normalized = normalizeName(name);
  const exact = candidates.find((candidate) => normalizeName(candidate.display_name) === normalized);
  const prefix = candidates.find((candidate) => normalizeName(candidate.display_name).startsWith(normalized) || normalized.startsWith(normalizeName(candidate.display_name)));
  const best = exact ?? prefix ?? candidates[0];
  return best ? {
    spotify_id: best.spotify_id,
    image_url: best.image_url,
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
  return {
    spotify_id: artist.spotify_id ?? null,
    name: artist.name,
    normalized_name: artist.normalized_name,
    image_url: artist.image_url ?? null,
    listeners: artist.listeners ?? null,
    weekly_listener_gain: artist.weekly_listener_gain ?? null,
    weekly_listener_growth_percent: artist.weekly_listener_growth_percent ?? null,
    monthly_listener_change_percent: artist.monthly_listener_change_percent ?? null,
    daily_listener_change: artist.daily_listener_change ?? null,
    kworb_rank: artist.kworb_rank ?? null,
    source: artist.source,
    score_status: artist.score_status,
    provider_url: artist.provider_url ?? null,
    data_date: artist.data_date ?? null,
    listener_history: artist.listener_history ?? [],
    top_tracks: artist.top_tracks ?? [],
    discography: artist.discography ?? [],
    top_cities: artist.top_cities ?? [],
    updated_at: artist.updated_at,
  };
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

function numberFromUnknown(value: unknown): number | null {
  if (typeof value === "number" && Number.isFinite(value)) return value;
  if (typeof value !== "string") return null;
  const parsed = Number(value.replace(/,/g, ""));
  return Number.isFinite(parsed) ? parsed : null;
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
