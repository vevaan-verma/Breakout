package com.vevaan.breakout

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Bundle
import android.os.Build
import android.os.SystemClock
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import kotlin.math.roundToInt
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.vevaan.breakout.core.designsystem.BreakoutDimensions
import com.vevaan.breakout.ui.theme.BreakoutCoral
import com.vevaan.breakout.ui.theme.BreakoutOutline
import com.vevaan.breakout.ui.theme.BreakoutPrimary
import com.vevaan.breakout.ui.theme.BreakoutSecondary
import com.vevaan.breakout.ui.theme.BreakoutSurface
import com.vevaan.breakout.ui.theme.BreakoutSurfaceVariant
import com.vevaan.breakout.ui.theme.BreakoutTextSecondary
import com.vevaan.breakout.ui.theme.BreakoutTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Base64
import kotlin.math.log10
import kotlin.math.max
import kotlin.random.Random

private const val MinCompleteMarketArtists = 100
private const val MarketInitialPageSize = 20

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MarketScreen(
    roster: Map<RosterSlot, ArtistUi>,
    draftPicks: List<DraftPickUi>,
    droppedArtistNames: Set<String>,
    waiverQueuedNames: Set<String>,
    leagueSettings: LeagueSettingsUi,
    draftStatus: DraftStatus,
    draftPickMode: Boolean,
    canMakeDraftPick: Boolean,
    showExpiredFeedback: Boolean,
    currentPickIndex: Int,
    memberCount: Int,
    startFilter: MarketFilter,
    query: String,
    onQueryChange: (String) -> Unit,
    submittedSearch: String,
    onSubmittedSearchChange: (String) -> Unit,
    activeFilter: MarketFilter?,
    onActiveFilterChange: (MarketFilter?) -> Unit,
    previousFilter: MarketFilter,
    onPreviousFilterChange: (MarketFilter) -> Unit,
    marketState: MarketState,
    onMarketStateChange: (MarketState) -> Unit,
    searchResultCache: Map<String, List<ArtistUi>> = emptyMap(),
    onCacheSearchResults: (String, List<ArtistUi>) -> Unit = { _, _ -> },
    onClearSearchResults: (String) -> Unit = {},
    snapshots: Map<String, SnapshotUi>,
    onSnapshotsChange: (Map<String, SnapshotUi>) -> Unit,
    visibleCount: Int,
    onVisibleCountChange: (Int) -> Unit,
    resetTick: Int,
    lastMarketKey: String?,
    onLastMarketKeyChange: (String?) -> Unit,
    loadedMarketKey: String?,
    onLoadedMarketKeyChange: (String?) -> Unit,
    openActionArtistKey: String?,
    onOpenActionArtistKeyChange: (String?) -> Unit,
    listState: LazyListState,
    preloadedArtists: List<ArtistUi>?,
    onLoadMarketArtists: suspend (String) -> List<ArtistUi> = { emptyList() },
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit,
    onToggleArtist: (ArtistUi) -> Unit,
    onRemoveRosterArtist: (ArtistUi) -> Unit,
    onQueueWaiverArtist: (ArtistUi, RosterSlot?) -> Unit,
    onCancelWaiverArtist: (ArtistUi) -> Unit,
    onPickExpired: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var hideDrafted by rememberSaveable { mutableStateOf(true) }
    var hadPickControl by rememberSaveable { mutableStateOf(false) }
    var pickSubmitted by rememberSaveable { mutableStateOf(false) }
    var pendingActionArtist by remember { mutableStateOf<ArtistUi?>(null) }
    var pendingActionLabel by remember { mutableStateOf<String?>(null) }
    var pendingWaiverDropArtist by remember { mutableStateOf<ArtistUi?>(null) }
    var marketContentVisible by remember { mutableStateOf(false) }
    var loadingMoreArtists by remember { mutableStateOf(false) }
    var artistListVisible by remember { mutableStateOf(true) }
    var visibleArtistListKey by remember { mutableStateOf<String?>(null) }
    var visibleArtistRows by remember { mutableStateOf<List<ArtistUi>>(emptyList()) }
    var animatingArtistKeys by remember { mutableStateOf<Set<String>>(emptySet()) }
    var computedSearchRows by remember { mutableStateOf<List<ArtistUi>?>(null) }
    var searchLoadingVisible by remember { mutableStateOf(false) }
    var searchLoaderMounted by remember { mutableStateOf(false) }
    var searchLoaderExiting by remember { mutableStateOf(false) }
    var searchPreparedKey by remember { mutableStateOf<String?>(null) }
    var emptyStateAllowedKey by remember { mutableStateOf<String?>(null) }
    var emptyStateContent by remember { mutableStateOf("No Artists Found" to "No artists match this filter right now.") }
    val effectiveQuery = submittedSearch.trim()
    val dataKey = effectiveQuery.lowercase()
    val filterKey = "${dataKey}|${activeFilter?.name ?: "search"}"
    val displayedState = when {
        effectiveQuery.isNotBlank() && marketState is MarketState.Ready -> marketState
        loadedMarketKey == dataKey -> marketState
        else -> MarketState.Loading
    }
    val draftedByArtist = remember(draftPicks, droppedArtistNames) {
        draftPicks
            .filter { it.artist.name.lowercase() !in droppedArtistNames }
            .associateBy { it.artist.name.lowercase() }
    }
    val waiverQueuePositions = remember(waiverQueuedNames) {
        waiverQueuedNames.toList().withIndex().associate { it.value to it.index + 1 }
    }
    val recommendationPool = preloadedArtists?.takeIf { it.isNotEmpty() }
        ?: (marketState as? MarketState.Ready)?.artists.orEmpty()
    val recommendedPick = if (draftPickMode && recommendationPool.isNotEmpty()) {
        recommendationPool.strategicDraftRecommendation(
            roster = roster,
            settings = leagueSettings,
            currentPickIndex = currentPickIndex,
            memberCount = memberCount,
            snapshots = snapshots,
            unavailableArtistNames = draftedByArtist.keys
        )
    } else {
        null
    }

    LaunchedEffect(startFilter) {
        if (effectiveQuery.isBlank() && activeFilter == null) onActiveFilterChange(startFilter)
    }

    LaunchedEffect(query, effectiveQuery) {
        if (query.isBlank() && effectiveQuery.isBlank()) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(searchLoadingVisible) {
        if (searchLoadingVisible) {
            searchLoaderMounted = true
        } else if (searchLoaderMounted) {
            delay(520)
            if (!searchLoaderExiting) searchLoaderMounted = false
        }
    }

    fun submitSearch() {
        val cleaned = query.trim()
        if (cleaned.isBlank()) return
        if (marketState !is MarketState.Ready) return
        if (submittedSearch.equals(cleaned, ignoreCase = true) && computedSearchRows != null) {
            focusManager.clearFocus()
            return
        }
        if (submittedSearch.isNotBlank() && !submittedSearch.equals(cleaned, ignoreCase = true)) {
            onClearSearchResults(submittedSearch.trim().lowercase())
        }
        onSubmittedSearchChange(cleaned)
        searchLoadingVisible = true
        searchLoaderExiting = false
        searchPreparedKey = null
        activeFilter?.let { onPreviousFilterChange(it) }
        onActiveFilterChange(null)
        onOpenActionArtistKeyChange(null)
        focusManager.clearFocus()
        scope.launch { listState.scrollToItem(0) }
    }

    fun clearSearchTextOnly() {
        onQueryChange("")
        focusManager.clearFocus()
    }

    fun clearSearch(targetFilter: MarketFilter = previousFilter) {
        if (effectiveQuery.isNotBlank()) onClearSearchResults(dataKey)
        searchPreparedKey = null
        preloadedArtists?.takeIf { it.isNotEmpty() }?.let {
            onMarketStateChange(MarketState.Ready(it))
            onLoadedMarketKeyChange("")
        }
        onSubmittedSearchChange("")
        onQueryChange("")
        onActiveFilterChange(targetFilter)
        onOpenActionArtistKeyChange(null)
        focusManager.clearFocus()
        scope.launch {
            listState.smoothMarketScrollToTop()
        }
    }

    LaunchedEffect(resetTick) {
        if (resetTick <= 0) return@LaunchedEffect
        focusManager.clearFocus()
        searchLoadingVisible = false
        searchLoaderExiting = false
        searchPreparedKey = null
        computedSearchRows = null
        if (effectiveQuery.isNotBlank()) onClearSearchResults(dataKey)
        artistListVisible = false
        delay(80)
        listState.smoothMarketScrollToTop()
        artistListVisible = true
    }

    LaunchedEffect(draftPickMode, canMakeDraftPick, currentPickIndex) {
        if (!draftPickMode) {
            hadPickControl = false
            pickSubmitted = false
        } else {
            if (hadPickControl && !canMakeDraftPick && !pickSubmitted && showExpiredFeedback) {
                onPickExpired()
            }
            hadPickControl = canMakeDraftPick
            if (!canMakeDraftPick) pickSubmitted = false
        }
    }

    fun List<ArtistUi>.filteredForFilter(filter: MarketFilter?): List<ArtistUi> =
        if (effectiveQuery.isNotBlank()) {
            val cleanQuery = effectiveQuery
            filter { it.name.searchMatchScore(cleanQuery) > 0.0 }
                .sortedWith(
                compareByDescending<ArtistUi> { it.name.searchMatchScore(cleanQuery) }
                    .thenByDescending { it.listeners ?: 0L }
                    .thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
            )
        } else when (filter) {
            MarketFilter.Trending -> filter { it.weeklyListenerGain != null || it.weeklyListenerGrowthPercent != null }
                .sortedWith(compareByDescending<ArtistUi> { it.weeklyListenerGrowthPercent ?: 0.0 }
                    .thenByDescending { it.weeklyListenerGain ?: 0L }
                    .thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) })
            MarketFilter.Headliners -> filter { it.isHeadlinerEligible() }.ifEmpty { sortedByDescending { it.listeners ?: 0L }.take(30) }
                .sortedWith(compareByDescending<ArtistUi> { it.listeners ?: 0L }.thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) })
            MarketFilter.Rising -> (filter { it.marketBucket() == MarketFilter.Rising } + filter { (it.listeners ?: 0L) in 1_000_000L until 15_000_000L })
                .marketDistinct()
                .sortedWith(compareByDescending<ArtistUi> { it.listeners ?: 0L }.thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) })
            MarketFilter.Wildcards -> (filter { it.marketBucket() == MarketFilter.Wildcards } + filter { (it.listeners ?: 0L) in 15_000_000L until 40_000_000L })
                .marketDistinct()
                .sortedWith(compareByDescending<ArtistUi> { it.listeners ?: 0L }.thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) })
            MarketFilter.DeepCuts -> (filter { it.marketBucket() == MarketFilter.DeepCuts } + filter { (it.listeners ?: Long.MAX_VALUE) < 1_000_000L })
                .marketDistinct()
                .sortedWith(compareByDescending<ArtistUi> { it.listeners ?: 0L }.thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) + it.discoverySortValue() })
            MarketFilter.Waivered -> filter { it.name.lowercase() in waiverQueuedNames }
                .sortedWith(compareBy<ArtistUi> { waiverQueuePositions[it.name.lowercase()] ?: Int.MAX_VALUE }
                    .thenByDescending { it.listeners ?: 0L })
            null -> sortedWith(
                compareByDescending<ArtistUi> { it.isHeadlinerEligible() }
                    .thenByDescending { it.listeners ?: 0L }
                    .thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
            )
        }

    fun List<ArtistUi>.filtered(): List<ArtistUi> = filteredForFilter(activeFilter)

    val readyArtists = (displayedState as? MarketState.Ready)?.artists
    LaunchedEffect(effectiveQuery, readyArtists, snapshots, loadedMarketKey) {
        if (effectiveQuery.isNotBlank()) {
            val cached = searchResultCache[dataKey]
            if (!cached.isNullOrEmpty()) {
                computedSearchRows = cached
                return@LaunchedEffect
            }
            searchLoadingVisible = true
            if (loadedMarketKey != dataKey) {
                computedSearchRows = null
                return@LaunchedEffect
            }
        }
        val source = readyArtists.orEmpty()
        if (effectiveQuery.isNotBlank() && loadedMarketKey == dataKey && source.isNotEmpty()) {
            computedSearchRows = computedSearchRows ?: source.marketDistinct()
            return@LaunchedEffect
        }
        computedSearchRows = null
        if (effectiveQuery.isBlank() || source.isEmpty()) {
            if (effectiveQuery.isBlank()) searchLoadingVisible = false
            return@LaunchedEffect
        }
        val cleanQuery = effectiveQuery
        delay(16)
            computedSearchRows = withContext(Dispatchers.Default) {
                source
                .filter { it.name.searchMatchScore(cleanQuery) > 0.0 }
                .sortedWith(
                    compareByDescending<ArtistUi> { it.name.searchMatchScore(cleanQuery) }
                        .thenByDescending { it.listeners ?: 0L }
                        .thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
                )
                .marketDistinct()
        }.also { rows ->
            if (effectiveQuery.isNotBlank() && rows.isNotEmpty()) {
                onCacheSearchResults(dataKey, rows.take(MarketInitialPageSize))
            }
        }
    }
    val allFilteredArtists = remember(readyArtists, filterKey, draftedByArtist, hideDrafted, roster, effectiveQuery, activeFilter, snapshots, computedSearchRows) {
        val base = if (effectiveQuery.isNotBlank()) {
            computedSearchRows.orEmpty()
        } else {
            readyArtists?.filtered().orEmpty()
        }
        base
            .filter { artist -> !draftPickMode || !hideDrafted || draftedByArtist[artist.name.lowercase()] == null }
            .sortedBy { artist -> draftPickMode && roster.values.any { it.name == artist.name } }
            .marketDistinct()
    }
    val visibleRowsKey = remember(filterKey, effectiveQuery, allFilteredArtists) {
        "$filterKey:${allFilteredArtists.size}:${allFilteredArtists.take(MarketInitialPageSize).joinToString("|") { it.stableListKey() }}"
    }
    val restoredVisibleCount = MarketInitialPageSize
    val cachedVisibleRows = remember(allFilteredArtists, visibleCount, restoredVisibleCount) {
        allFilteredArtists.take(maxOf(visibleCount, restoredVisibleCount, MarketInitialPageSize))
    }
    val rowsReadyForCurrentKey = displayedState is MarketState.Ready &&
        visibleArtistListKey == visibleRowsKey &&
        artistListVisible
    val rowsStagedForCurrentKey = displayedState is MarketState.Ready &&
        visibleArtistListKey == visibleRowsKey
    val transitioningArtistRows = displayedState is MarketState.Ready && !rowsReadyForCurrentKey
    val initialMarketLoading = effectiveQuery.isBlank() && displayedState is MarketState.Loading
    val canShowEmptyForCurrentKey = effectiveQuery.isNotBlank() ||
        activeFilter == MarketFilter.Waivered ||
        readyArtists.isNullOrEmpty()

    LaunchedEffect(visibleRowsKey, allFilteredArtists.size, displayedState) {
        emptyStateAllowedKey = null
        if (effectiveQuery.isNotBlank() && computedSearchRows == null) return@LaunchedEffect
        if (displayedState is MarketState.Ready && allFilteredArtists.isEmpty() && canShowEmptyForCurrentKey) {
            delay(360)
            if (visibleArtistListKey == visibleRowsKey || visibleArtistRows.isEmpty()) {
                emptyStateContent = if (effectiveQuery.isNotBlank()) {
                    "No Results Found" to "Try a different artist name or check the spelling."
                } else {
                    "${activeFilter?.label ?: "Search"} Empty" to "No artists match this filter right now."
                }
                emptyStateAllowedKey = visibleRowsKey
            }
        }
    }

    LaunchedEffect(visibleRowsKey, displayedState) {
        if (displayedState !is MarketState.Ready) {
            visibleArtistRows = emptyList()
            visibleArtistListKey = null
            return@LaunchedEffect
        }
        if (effectiveQuery.isNotBlank() && computedSearchRows == null) {
            return@LaunchedEffect
        }
        if (visibleArtistListKey != visibleRowsKey) {
            artistListVisible = false
            onOpenActionArtistKeyChange(null)
            delay(120)
            val neededCount = restoredVisibleCount
            onVisibleCountChange(neededCount.coerceAtLeast(MarketInitialPageSize))
            val firstRows = allFilteredArtists.take(neededCount.coerceAtLeast(MarketInitialPageSize))
            visibleArtistRows = firstRows
            visibleArtistListKey = visibleRowsKey
            animatingArtistKeys = firstRows.map { it.stableListKey() }.toSet()
            if (lastMarketKey != visibleRowsKey) {
                listState.smoothMarketScrollToTop()
            }
            if (effectiveQuery.isNotBlank() && firstRows.isNotEmpty()) {
                prefetchArtistImages(context, firstRows, limit = firstRows.size.coerceAtMost(MarketInitialPageSize))
            }
            delay(180)
            if (effectiveQuery.isNotBlank()) {
                searchLoaderExiting = true
                searchLoadingVisible = false
                delay(540)
                searchLoaderExiting = false
                searchLoaderMounted = false
                searchPreparedKey = visibleRowsKey
                delay(70)
            }
            artistListVisible = true
            delay(720)
            animatingArtistKeys = emptySet()
        }
        onLastMarketKeyChange(visibleRowsKey)
    }

    LaunchedEffect(visibleRowsKey, visibleCount, allFilteredArtists, artistListVisible) {
        if (displayedState is MarketState.Ready && visibleArtistListKey == visibleRowsKey && artistListVisible) {
            val nextRows = if (visibleCount <= visibleArtistRows.size) {
                visibleArtistRows
            } else {
                (visibleArtistRows + allFilteredArtists.drop(visibleArtistRows.size).take(visibleCount - visibleArtistRows.size)).marketDistinct()
            }
            visibleArtistRows = nextRows
        }
    }

    LaunchedEffect(listState, displayedState, visibleCount, artistListVisible, visibleArtistRows.size, allFilteredArtists.size) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible to total
        }
            .map { (lastVisible, total) -> total > 0 && lastVisible >= total - 4 }
            .distinctUntilChanged()
            .collect { nearBottom ->
                val ready = displayedState as? MarketState.Ready ?: return@collect
                if (!artistListVisible || visibleArtistRows.isEmpty()) return@collect
                val available = allFilteredArtists.size
                if (nearBottom && visibleCount < available) {
                    val nextCount = (visibleCount + MarketInitialPageSize).coerceAtMost(available)
                    loadingMoreArtists = true
                    val preparedRows = allFilteredArtists.drop(visibleCount).take(nextCount - visibleCount)
                    visibleArtistRows = (visibleArtistRows + preparedRows).marketDistinct()
                    animatingArtistKeys = preparedRows.map { it.stableListKey() }.toSet()
                    delay(120)
                    onVisibleCountChange(nextCount)
                    delay(560)
                    animatingArtistKeys = emptySet()
                    loadingMoreArtists = false
                }
            }
    }

    LaunchedEffect(dataKey) {
        val requestKey = dataKey
        val warmedArtists = preloadedArtists.orEmpty()
        if (effectiveQuery.isNotBlank()) {
            val cached = searchResultCache[requestKey]
            if (!cached.isNullOrEmpty()) {
                computedSearchRows = cached
                onMarketStateChange(MarketState.Ready(cached))
                onLoadedMarketKeyChange(requestKey)
                return@LaunchedEffect
            }
        }
        if (effectiveQuery.isBlank() && loadedMarketKey == requestKey && marketState is MarketState.Ready) {
            return@LaunchedEffect
        }
        if (effectiveQuery.isNotBlank() && loadedMarketKey == requestKey && marketState is MarketState.Ready) {
            return@LaunchedEffect
        }
        if (effectiveQuery.isBlank()) {
            onMarketStateChange(MarketState.Loading)
            onLoadedMarketKeyChange(null)
            val nextState = runCatching {
                val artists = onLoadMarketArtists("")
                onLoadedMarketKeyChange(requestKey)
                if (artists.isEmpty()) {
                    MarketState.Error("The server market cache is empty. Run the refresh job, then try again.")
                } else {
                    MarketState.Ready(artists)
                }
            }.getOrElse {
                if (it is CancellationException) throw it
                onLoadedMarketKeyChange(requestKey)
                MarketState.Error("Could not load artists: ${it.message ?: "unknown error"}")
            }
            onMarketStateChange(nextState)
            return@LaunchedEffect
        }
        if (warmedArtists.size >= MinCompleteMarketArtists) {
            delay(16)
            val localResults = withContext(Dispatchers.Default) {
                warmedArtists
                    .filter { it.name.searchMatchScore(effectiveQuery) > 0.0 }
                    .sortedWith(
                        compareByDescending<ArtistUi> { it.name.searchMatchScore(effectiveQuery) }
                            .thenByDescending { it.listeners ?: 0L }
                            .thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
                    )
                    .marketDistinct()
            }
            val remoteResults = if (localResults.firstOrNull()?.name?.searchMatchScore(effectiveQuery) == 1_000.0) {
                emptyList()
            } else {
                runCatching { onLoadMarketArtists(effectiveQuery) }.getOrDefault(emptyList())
            }
            val results = withContext(Dispatchers.Default) {
                (localResults + remoteResults)
                    .marketDistinct()
                    .sortedWith(
                        compareByDescending<ArtistUi> { it.name.searchMatchScore(effectiveQuery) }
                            .thenByDescending { it.listeners ?: 0L }
                            .thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
                    )
            }
            if (results.isNotEmpty()) {
                onCacheSearchResults(requestKey, results.take(MarketInitialPageSize))
            }
            onMarketStateChange(if (results.isEmpty()) MarketState.Empty("No artists found.") else MarketState.Ready(results))
            onLoadedMarketKeyChange(requestKey)
            return@LaunchedEffect
        }
        onMarketStateChange(MarketState.Loading)
        onLoadedMarketKeyChange(null)
        val nextState = runCatching {
            val artists = onLoadMarketArtists(effectiveQuery)
            onLoadedMarketKeyChange(requestKey)
            if (artists.isNotEmpty()) {
                onCacheSearchResults(requestKey, artists.take(MarketInitialPageSize))
            }
            if (artists.isEmpty()) MarketState.Empty("No artists found.") else MarketState.Ready(artists)
        }.getOrElse {
            if (it is CancellationException) throw it
            onLoadedMarketKeyChange(requestKey)
            MarketState.Error("Could not load artists: ${it.message ?: "unknown error"}")
        }
        onMarketStateChange(nextState)
    }

    LaunchedEffect(initialMarketLoading) {
        if (initialMarketLoading) {
            marketContentVisible = false
        } else {
            marketContentVisible = false
            delay(180)
            marketContentVisible = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = !initialMarketLoading && marketContentVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    },
                contentPadding = PaddingValues(
                    start = BreakoutDimensions.ScreenHorizontalPadding,
                    top = BreakoutDimensions.xs,
                    end = BreakoutDimensions.ScreenHorizontalPadding,
                    bottom = BreakoutDimensions.SectionSpacing
                ),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)
            ) {
            if (draftStatus == DraftStatus.Scheduled) {
                item {
                    StatusCard(
                        title = "Draft Not Started",
                        detail = "Use the market to scout artists. Rosters are filled inside the draft room."
                    )
                }
            }
            if (recommendedPick != null) {
                item {
                    RecommendedPickButton(
                        artist = recommendedPick,
                        onClick = { onArtistSelected(recommendedPick) }
                    )
                }
            }
            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.98f),
                    shadowElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = BreakoutDimensions.xxs),
                        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
                    ) {
                        TopTitle(
                            title = if (draftPickMode) "Draft Market" else "Market",
                            subtitle = when {
                                draftPickMode -> "Pick one artist for this turn"
                                draftStatus == DraftStatus.Scheduled -> "Scout artists before the draft"
                                else -> "Find artists for your roster"
                            },
                            onMenuClick = onOpenMenu
                        )
                        BreakoutCard(
                            contentPadding = PaddingValues(BreakoutDimensions.md),
                            border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.52f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StyledTextField(
                                    value = query,
                                    onValueChange = onQueryChange,
                                    label = "Search Artists",
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(64.dp),
                                    maxLength = 48,
                                    imeAction = ImeAction.Search,
                                    keyboardActions = KeyboardActions(onSearch = { submitSearch() })
                                )
                                Row(
                                    modifier = Modifier
                                        .width(112.dp)
                                        .height(64.dp)
                                        .align(Alignment.CenterVertically),
                                    horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MarketSearchActionButton(
                                        label = "x",
                                        enabled = query.isNotBlank() || effectiveQuery.isNotBlank(),
                                        accent = BreakoutTextSecondary,
                                        onClick = {
                                            if (effectiveQuery.isNotBlank()) {
                                                clearSearch()
                                            } else {
                                                clearSearchTextOnly()
                                            }
                                        }
                                    )
                                    MarketSearchActionButton(
                                        label = ">",
                                        enabled = query.trim().isNotBlank(),
                                        accent = BreakoutPrimary,
                                        onClick = { submitSearch() }
                                    )
                                }
                            }
                            if (draftPickMode) {
                                ToggleRow(
                                    label = "Hide Drafted",
                                    value = if (hideDrafted) "On" else "Off",
                                    enabled = true,
                                    onToggle = { hideDrafted = !hideDrafted }
                                )
                            }
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                FilterChipRow(
                                    filters = if (draftStatus == DraftStatus.Complete) {
                                        MarketFilter.entries
                                    } else {
                                        MarketFilter.entries.filterNot { it == MarketFilter.Waivered }
                                    },
                                    selected = activeFilter,
                                    searchLabel = effectiveQuery.takeIf { it.isNotBlank() }?.let { "\"${it.take(18)}${if (it.length > 18) "..." else ""}\"" },
                                    onSearchSelected = { focusManager.clearFocus() },
                                    onSelected = { selected ->
                                        selected?.let {
                                            if (effectiveQuery.isNotBlank()) {
                                                clearSearch(it)
                                            } else if (it == activeFilter) {
                                                focusManager.clearFocus()
                                                onOpenActionArtistKeyChange(null)
                                                onVisibleCountChange(MarketInitialPageSize)
                                                scope.launch { listState.smoothMarketScrollToTop() }
                                            } else {
                                                onPreviousFilterChange(it)
                                                onActiveFilterChange(it)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
            when (val current = displayedState) {
                MarketState.Loading -> item { LoadingState(if (effectiveQuery.isBlank()) "Loading Market" else "Searching Artists") }
                is MarketState.Empty -> item { MarketEmptyState(title = "No Artists Found", detail = current.message) }
                is MarketState.Error -> item { StatusCard("Market Error", current.message) }
                is MarketState.Ready -> {
                    val searchReadyToReveal = effectiveQuery.isNotBlank() && searchPreparedKey == visibleRowsKey
                    val searchStillSettling = effectiveQuery.isNotBlank() &&
                        !searchReadyToReveal &&
                        (searchLoadingVisible ||
                            searchLoaderExiting ||
                            computedSearchRows == null ||
                            (allFilteredArtists.isNotEmpty() && visibleArtistListKey != visibleRowsKey))
                    val showSearchLoader = effectiveQuery.isNotBlank() &&
                        !searchReadyToReveal &&
                        (searchLoaderMounted || searchStillSettling || searchLoaderExiting)
                    if (showSearchLoader) {
                        item(key = "search-loader:$dataKey") {
                            AnimatedVisibility(
                                visible = !searchLoaderExiting,
                                enter = fadeIn(animationSpec = tween(260)) + expandVertically(animationSpec = tween(300)),
                                exit = fadeOut(animationSpec = tween(360)) + shrinkVertically(animationSpec = tween(420))
                            ) {
                                MarketSearchingState()
                            }
                        }
                    }
                    val filteredArtists = if (showSearchLoader) {
                        emptyList()
                    } else if (rowsReadyForCurrentKey && visibleArtistRows.isEmpty() && cachedVisibleRows.isNotEmpty()) {
                        cachedVisibleRows
                    } else if (rowsStagedForCurrentKey) {
                        visibleArtistRows
                    } else {
                        emptyList()
                    }
                    if (filteredArtists.isEmpty()) {
                        item {
                            val showEmpty = !searchStillSettling &&
                                !showSearchLoader &&
                                !transitioningArtistRows &&
                                rowsReadyForCurrentKey &&
                                allFilteredArtists.isEmpty() &&
                                canShowEmptyForCurrentKey &&
                                emptyStateAllowedKey == visibleRowsKey
                            AnimatedVisibility(
                                visible = showEmpty,
                                enter = fadeIn(animationSpec = tween(260)) + expandVertically(animationSpec = tween(300)),
                                exit = fadeOut(animationSpec = tween(260)) + shrinkVertically(animationSpec = tween(300))
                            ) {
                                MarketEmptyState(
                                    title = emptyStateContent.first,
                                    detail = emptyStateContent.second
                                )
                            }
                            if (!searchStillSettling && !showEmpty) {
                                Spacer(modifier = Modifier.height(1.dp))
                            }
                        }
                    } else {
                        itemsIndexed(filteredArtists, key = { index, artist -> "${visibleArtistListKey ?: filterKey}:$index:${artist.stableListKey()}" }) { index, artist ->
                            val rowDelay = (index.coerceAtMost(9) * 42)
                            val shouldAnimateRow = artist.stableListKey() in animatingArtistKeys
                            AnimatedVisibility(
                                visible = artistListVisible,
                                enter = if (shouldAnimateRow) {
                                    fadeIn(animationSpec = tween(durationMillis = 320, delayMillis = rowDelay)) +
                                    if (shouldAnimateRow) {
                                        slideInHorizontally(animationSpec = tween(durationMillis = 340, delayMillis = rowDelay)) { -it / 3 }
                                    } else {
                                        slideInHorizontally(animationSpec = tween(durationMillis = 1)) { 0 }
                                    }
                                } else {
                                    fadeIn(animationSpec = tween(durationMillis = 0))
                                },
                                exit = fadeOut(animationSpec = tween(durationMillis = 150)) +
                                    slideOutHorizontally(animationSpec = tween(durationMillis = 170)) { it / 8 }
                            ) {
                                val draftedPick = draftedByArtist[artist.name.lowercase()]
                                val draftedByYou = roster.values.any { it.name == artist.name }
                                val drafted = draftedPick != null
                                val draftedByOther = drafted && !draftedByYou
                                val waiverQueued = artist.name.lowercase() in waiverQueuedNames
                                val canQueueFromMarket = !draftPickMode &&
                                    draftStatus == DraftStatus.Complete &&
                                    draftedPick == null &&
                                    waiverQueuedNames.size < leagueSettings.maxWaiverClaims &&
                                    !waiverQueued &&
                                    !draftedByYou &&
                                    claimableSlotFor(artist, roster, leagueSettings) != null
                                val canShowFullQueueWaiver = !draftPickMode &&
                                    draftStatus == DraftStatus.Complete &&
                                    draftedPick == null &&
                                    waiverQueuedNames.size >= leagueSettings.maxWaiverClaims &&
                                    !waiverQueued &&
                                    !draftedByYou &&
                                    claimableSlotFor(artist, roster, leagueSettings) != null
                                val canDraftFromMarket = draftPickMode &&
                                    canMakeDraftPick &&
                                    !drafted &&
                                    !draftedByYou &&
                                    firstOpenSlotFor(artist, roster, leagueSettings) != null
                                val hasSwipeAction = draftedByYou || waiverQueued || canDraftFromMarket || canQueueFromMarket || canShowFullQueueWaiver
                                val artistKey = artist.stableListKey()
                                ArtistRow(
                                    modifier = Modifier,
                                    artist = artist,
                                    isInRoster = draftedByYou,
                                    isDrafted = drafted,
                                    isDraftedByOther = draftedByOther,
                                    isWaiverQueued = waiverQueued,
                                    waiverQueuePosition = waiverQueuePositions[artist.name.lowercase()],
                                    showRoleTag = effectiveQuery.isNotBlank(),
                                    canRevealActions = true,
                                    actionsOpen = openActionArtistKey == artistKey,
                                    onActionsOpenChange = { open -> onOpenActionArtistKeyChange(if (open) artistKey else null) },
                                    canToggleRoster = hasSwipeAction,
                                    waiverAction = canQueueFromMarket || canShowFullQueueWaiver,
                                    waiverCancelAction = waiverQueued,
                                    statusLabel = when {
                                        draftedByYou -> "On Roster"
                                        draftedByOther -> "Taken"
                                        else -> null
                                    },
                                    onClick = { onArtistSelected(artist) },
                                    onToggleRoster = if (draftedByYou) ({
                                        pendingActionArtist = artist
                                        pendingActionLabel = "drop"
                                    }) else if (canDraftFromMarket) ({
                                        pendingActionArtist = artist
                                        pendingActionLabel = "draft"
                                    }) else if (canQueueFromMarket) ({
                                        pendingActionArtist = artist
                                        pendingActionLabel = "waiver"
                                    }) else if (canShowFullQueueWaiver) ({
                                        pendingActionArtist = artist
                                        pendingActionLabel = "waiver_full"
                                    }) else if (waiverQueued) ({
                                        pendingActionArtist = artist
                                        pendingActionLabel = "cancel"
                                    }) else null
                                )
                            }
                        }
                        val totalAvailable = allFilteredArtists.size
                        if (artistListVisible && filteredArtists.isNotEmpty() && (visibleCount < totalAvailable || (loadingMoreArtists && visibleCount < totalAvailable))) {
                            item {
                                LoadingState(if (loadingMoreArtists) "Loading More Artists" else "Scroll for More Artists")
                            }
                        }
                    }
                }
                }
            }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = initialMarketLoading,
            enter = fadeIn(animationSpec = tween(220)) + slideInVertically(animationSpec = tween(220)) { it / 16 },
            exit = slideOutVertically(animationSpec = tween(420)) { -it }
        ) {
            MarketInitializingScreen()
        }
        pendingActionArtist?.let { artist ->
            val action = pendingActionLabel.orEmpty()
            ConfirmActionCard(
                title = when (action) {
                    "draft" -> "Draft ${artist.displayName()}?"
                    "waiver" -> "Queue Waiver?"
                    "waiver_full" -> "Waiver Queue Full"
                    "cancel" -> "Cancel Waiver?"
                    "drop" -> "Drop ${artist.displayName()}?"
                    else -> "Confirm Action?"
                },
                detail = when (action) {
                    "draft" -> "This uses your current draft pick."
                    "waiver" -> "This adds ${artist.displayName()} to your waiver queue."
                    "waiver_full" -> "You have used all ${leagueSettings.maxWaiverClaims} waiver claims. Cancel claims from your roster before adding another artist."
                    "cancel" -> "This removes ${artist.displayName()} from your waiver queue."
                    "drop" -> "This removes ${artist.displayName()} from your roster."
                    else -> "Confirm this roster action."
                },
                confirmText = when (action) {
                    "draft" -> "Draft"
                    "waiver" -> "Queue"
                    "waiver_full" -> "Got It"
                    "cancel" -> "Cancel Claim"
                    "drop" -> "Drop"
                    else -> "Confirm"
                },
                accent = if (action == "waiver" || action == "waiver_full") WaiverAccent else BreakoutCoral,
                onCancel = {
                    pendingActionArtist = null
                    pendingActionLabel = null
                },
                onConfirm = {
                    when (action) {
                        "draft" -> {
                            pickSubmitted = true
                            onToggleArtist(artist)
                        }
                        "waiver" -> {
                            if (firstOpenSlotFor(artist, roster, leagueSettings) == null) {
                                pendingWaiverDropArtist = artist
                            } else {
                                onQueueWaiverArtist(artist, null)
                            }
                        }
                        "cancel" -> onCancelWaiverArtist(artist)
                        "drop" -> onRemoveRosterArtist(artist)
                    }
                    pendingActionArtist = null
                    pendingActionLabel = null
                }
            )
        }
        pendingWaiverDropArtist?.let { artist ->
            WaiverDropSlotDialog(
                artist = artist,
                options = waiverReplacementOptionsFor(artist, roster, leagueSettings),
                onDismiss = { pendingWaiverDropArtist = null },
                onChoose = { dropSlot ->
                    pendingWaiverDropArtist = null
                    onQueueWaiverArtist(artist, dropSlot)
                }
            )
        }
    }
}


@Composable
private fun MarketSearchActionButton(
    label: String,
    enabled: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    val background by animateColorAsState(
        targetValue = if (enabled) accent.copy(alpha = 0.18f) else BreakoutSurfaceVariant.copy(alpha = 0.46f),
        animationSpec = tween(180),
        label = "marketSearchActionBackground"
    )
    val border by animateColorAsState(
        targetValue = if (enabled) accent.copy(alpha = 0.62f) else BreakoutOutline.copy(alpha = 0.24f),
        animationSpec = tween(180),
        label = "marketSearchActionBorder"
    )
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, border, CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (enabled) accent else BreakoutTextSecondary.copy(alpha = 0.45f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black
        )
    }
}

private suspend fun LazyListState.smoothMarketScrollToTop() {
    val viewportStep = (layoutInfo.viewportSize.height * 0.42f).coerceAtLeast(240f)
    repeat(80) {
        if (firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0) return
        scrollBy(-viewportStep)
        delay(16)
    }
    if (firstVisibleItemIndex != 0 || firstVisibleItemScrollOffset != 0) {
        animateScrollToItem(0)
    }
}

@Composable
private fun MarketSearchingState() {
    BreakoutCard(
        contentPadding = PaddingValues(BreakoutDimensions.lg),
        border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.42f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(BreakoutPrimary.copy(alpha = 0.12f))
                    .border(1.dp, BreakoutPrimary.copy(alpha = 0.38f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 3.dp,
                    color = BreakoutPrimary
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
            ) {
                Text(
                    "Searching Artists",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Preparing matching artists and artwork.",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun String?.parseScrollPosition(): Pair<Int, Int>? {
    if (isNullOrBlank()) return null
    val parts = split(":")
    val index = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val offset = parts.getOrNull(1)?.toIntOrNull() ?: 0
    return index.coerceAtLeast(0) to offset.coerceAtLeast(0)
}

@Composable
private fun MarketEmptyState(title: String, detail: String) {
    BreakoutCard(
        contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding),
        border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.34f))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(BreakoutSurfaceVariant.copy(alpha = 0.82f))
                    .border(1.dp, BreakoutOutline.copy(alpha = 0.42f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("?", color = BreakoutTextSecondary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text(detail, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
