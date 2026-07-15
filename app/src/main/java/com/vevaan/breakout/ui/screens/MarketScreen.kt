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
import androidx.compose.animation.Crossfade
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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
    activeFilter: MarketFilter?,
    onActiveFilterChange: (MarketFilter?) -> Unit,
    previousFilter: MarketFilter,
    onPreviousFilterChange: (MarketFilter) -> Unit,
    marketState: MarketState,
    onMarketStateChange: (MarketState) -> Unit,
    snapshots: Map<String, SnapshotUi>,
    onSnapshotsChange: (Map<String, SnapshotUi>) -> Unit,
    visibleCount: Int,
    onVisibleCountChange: (Int) -> Unit,
    lastMarketKey: String?,
    onLastMarketKeyChange: (String?) -> Unit,
    loadedMarketKey: String?,
    onLoadedMarketKeyChange: (String?) -> Unit,
    openActionArtistKey: String?,
    onOpenActionArtistKeyChange: (String?) -> Unit,
    listState: LazyListState,
    preloadedArtists: List<ArtistUi>?,
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
    val dataKey = query.trim().lowercase()
    val filterKey = "${dataKey}|${activeFilter?.name ?: "search"}"
    val displayedState = if (loadedMarketKey == dataKey) marketState else MarketState.Loading
    val initialMarketLoading = query.isBlank() && displayedState is MarketState.Loading
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
        if (query.isBlank() && activeFilter == null) onActiveFilterChange(startFilter)
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
        if (query.isNotBlank()) {
            val cleanQuery = query.trim()
            sortedWith(
                compareByDescending<ArtistUi> { it.name.searchMatchScore(cleanQuery) }
                    .thenByDescending { it.listeners ?: 0L }
                    .thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
            )
        } else when (filter) {
            MarketFilter.Headliners -> filter { it.isHeadlinerEligible() }
                .sortedWith(compareByDescending<ArtistUi> { it.listeners ?: 0L }.thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) })
            MarketFilter.Rising -> filter { it.marketBucket() == MarketFilter.Rising }
                .sortedByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
            MarketFilter.Wildcards -> filter { it.marketBucket() == MarketFilter.Wildcards }
                .sortedByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
            MarketFilter.DeepCuts -> filter { it.marketBucket() == MarketFilter.DeepCuts }
                .sortedByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) + it.discoverySortValue() }
            null -> sortedWith(
                compareByDescending<ArtistUi> { it.isHeadlinerEligible() }
                    .thenByDescending { it.listeners ?: 0L }
                    .thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
            )
        }

    fun List<ArtistUi>.filtered(): List<ArtistUi> = filteredForFilter(activeFilter)

    val readyArtists = (displayedState as? MarketState.Ready)?.artists
    val allFilteredArtists = remember(readyArtists, filterKey, draftedByArtist, hideDrafted, roster, query, activeFilter, snapshots) {
        readyArtists
            ?.filtered()
            ?.filter { artist -> !draftPickMode || !hideDrafted || draftedByArtist[artist.name.lowercase()] == null }
            ?.sortedBy { artist -> draftPickMode && roster.values.any { it.name == artist.name } }
            ?.marketDistinct()
            .orEmpty()
    }
    val filteredPreview = remember(allFilteredArtists, visibleCount) {
        allFilteredArtists.take(visibleCount)
    }

    LaunchedEffect(readyArtists, query, snapshots, draftedByArtist, hideDrafted, roster) {
        val artists = readyArtists ?: return@LaunchedEffect
        if (query.isNotBlank()) return@LaunchedEffect
        val warmedRows = MarketFilter.entries
            .flatMap { filter ->
                artists
                    .filteredForFilter(filter)
                    .filter { artist -> !draftPickMode || !hideDrafted || draftedByArtist[artist.name.lowercase()] == null }
                    .sortedBy { artist -> draftPickMode && roster.values.any { it.name == artist.name } }
                    .marketDistinct()
                    .take(20)
            }
            .marketDistinct()
        prefetchArtistImages(context, warmedRows)
    }

    LaunchedEffect(preloadedArtists, query) {
        val artists = preloadedArtists
        if (
            artists != null &&
            artists.size >= MinCompleteMarketArtists &&
            query.isBlank() &&
            loadedMarketKey == null &&
            marketState !is MarketState.Ready
        ) {
            prefetchArtistImages(context, artists.filtered().marketDistinct().take(visibleCount))
            onSnapshotsChange(LocalBreakoutStore.updateSnapshots(context, artists))
            onMarketStateChange(MarketState.Ready(artists))
            onLoadedMarketKeyChange(dataKey)
        }
    }

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            activeFilter?.let { onPreviousFilterChange(it) }
            onActiveFilterChange(null)
        }
    }

    LaunchedEffect(filterKey, displayedState) {
        if (displayedState !is MarketState.Ready) {
            visibleArtistRows = emptyList()
            visibleArtistListKey = null
            return@LaunchedEffect
        }
        if (visibleArtistListKey != filterKey) {
            artistListVisible = false
            onVisibleCountChange(20)
            onOpenActionArtistKeyChange(null)
            delay(120)
            listState.scrollToItem(0)
            val firstRows = allFilteredArtists.take(20)
            prefetchArtistImages(context, firstRows)
            visibleArtistRows = firstRows
            visibleArtistListKey = filterKey
            delay(24)
            artistListVisible = true
        }
        onLastMarketKeyChange(filterKey)
    }

    LaunchedEffect(filterKey, visibleCount, allFilteredArtists, artistListVisible) {
        if (displayedState is MarketState.Ready && visibleArtistListKey == filterKey && artistListVisible) {
            visibleArtistRows = allFilteredArtists.take(visibleCount)
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
                    val nextCount = (visibleCount + 20).coerceAtMost(available)
                    loadingMoreArtists = true
                    prefetchArtistImages(context, allFilteredArtists.drop(visibleCount).take(nextCount - visibleCount))
                    onVisibleCountChange(nextCount)
                    loadingMoreArtists = false
                }
            }
    }

    LaunchedEffect(dataKey) {
        val requestKey = dataKey
        val warmedArtists = preloadedArtists.orEmpty()
        if (query.isBlank() && loadedMarketKey == requestKey && marketState is MarketState.Ready) {
            return@LaunchedEffect
        }
        if (query.isBlank() && warmedArtists.size >= MinCompleteMarketArtists) {
            onSnapshotsChange(LocalBreakoutStore.updateSnapshots(context, warmedArtists))
            prefetchArtistImages(context, warmedArtists.filtered().take(visibleCount))
            onMarketStateChange(MarketState.Ready(warmedArtists))
            onLoadedMarketKeyChange(requestKey)
            return@LaunchedEffect
        }
        if (query.isBlank()) {
            onMarketStateChange(MarketState.Loading)
            onLoadedMarketKeyChange(null)
            val nextState = runCatching {
                val artists = MusicArtistService.topArtists()
                onSnapshotsChange(LocalBreakoutStore.updateSnapshots(context, artists))
                prefetchArtistImages(context, artists.filtered().take(visibleCount))
                onLoadedMarketKeyChange(requestKey)
                if (artists.size < MinCompleteMarketArtists) {
                    MarketState.Error("The market did not finish loading. Refresh and try again.")
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
        if (query.isNotBlank() && query.trim().length < 2) {
            onMarketStateChange(MarketState.Empty("Keep typing to search."))
            onLoadedMarketKeyChange(requestKey)
            return@LaunchedEffect
        }
        if (query.isNotBlank()) {
            delay(350)
        }
        onMarketStateChange(MarketState.Loading)
        onLoadedMarketKeyChange(null)
        val nextState = runCatching {
            val artists = MusicArtistService.search(query)
            onSnapshotsChange(LocalBreakoutStore.updateSnapshots(context, artists))
            prefetchArtistImages(context, artists.filtered().take(visibleCount))
            onLoadedMarketKeyChange(requestKey)
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
            delay(40)
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
                    .statusBarsPadding(),
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
                            // Market header spacing: controls the gap under the title and above search/filter.
                            .padding(vertical = BreakoutDimensions.sm),
                        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
                    ) {
                        TopTitle(
                            title = if (draftPickMode) "Draft Market" else "Market",
                            subtitle = when {
                                draftPickMode -> "Pick one artist for this turn"
                                draftStatus == DraftStatus.Scheduled -> "Scout artists before the draft"
                                else -> "Audience Scale and Discovery"
                            },
                            onMenuClick = onOpenMenu
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StyledTextField(
                                value = query,
                                onValueChange = onQueryChange,
                                label = "Search Artists",
                                modifier = Modifier.weight(1f)
                            )
                            AnimatedVisibility(
                                visible = query.isNotBlank(),
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(BreakoutSurfaceVariant)
                                        .border(1.dp, BreakoutOutline.copy(alpha = 0.55f), CircleShape)
                                        .clickable {
                                            onQueryChange("")
                                            onActiveFilterChange(previousFilter)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("x", color = BreakoutTextSecondary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
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
                            visible = query.isBlank(),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            FilterChipRow(
                                filters = MarketFilter.entries,
                                selected = activeFilter,
                                onSelected = { selected ->
                                    selected?.let {
                                        onPreviousFilterChange(it)
                                        onActiveFilterChange(it)
                                    }
                                }
                            )
                        }
                    }
                }
            }
            when (val current = displayedState) {
                MarketState.Loading -> item { LoadingState(if (query.isBlank()) "Loading Market" else "Searching Artists") }
                is MarketState.Empty -> item { StatusCard("No Live Data", current.message) }
                is MarketState.Error -> item { StatusCard("Market Error", current.message) }
                is MarketState.Ready -> {
                    val filteredArtists = visibleArtistRows
                    if (filteredArtists.isEmpty()) {
                        item {
                            if (artistListVisible && visibleArtistListKey == filterKey) {
                                StatusCard(
                                    title = "${activeFilter?.label ?: "Search"} Unavailable",
                                    detail = "No artists in this result match the selected filter."
                                )
                            } else {
                                Spacer(modifier = Modifier.height(1.dp))
                            }
                        }
                    } else {
                        itemsIndexed(filteredArtists, key = { _, artist -> "${visibleArtistListKey ?: filterKey}:${artist.stableListKey()}" }) { index, artist ->
                            val rowDelay = (index.coerceAtMost(9) * 42)
                            AnimatedVisibility(
                                visible = artistListVisible,
                                enter = fadeIn(animationSpec = tween(durationMillis = 320, delayMillis = rowDelay)) +
                                    slideInHorizontally(animationSpec = tween(durationMillis = 340, delayMillis = rowDelay)) { -it / 3 },
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
                                val canDraftFromMarket = draftPickMode &&
                                    canMakeDraftPick &&
                                    !drafted &&
                                    !draftedByYou &&
                                    firstOpenSlotFor(artist, roster, leagueSettings) != null
                                val hasSwipeAction = draftedByYou || waiverQueued || canDraftFromMarket || canQueueFromMarket
                                val artistKey = artist.stableListKey()
                                ArtistRow(
                                    modifier = Modifier.animateContentSize(),
                                    artist = artist,
                                    isInRoster = draftedByYou,
                                    isDrafted = drafted,
                                    isDraftedByOther = draftedByOther,
                                    isWaiverQueued = waiverQueued,
                                    waiverQueuePosition = waiverQueuePositions[artist.name.lowercase()],
                                    showRoleTag = query.isNotBlank(),
                                    canRevealActions = true,
                                    actionsOpen = openActionArtistKey == artistKey,
                                    onActionsOpenChange = { open -> onOpenActionArtistKeyChange(if (open) artistKey else null) },
                                    canToggleRoster = hasSwipeAction,
                                    waiverAction = canQueueFromMarket,
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
                                    }) else if (waiverQueued) ({
                                        pendingActionArtist = artist
                                        pendingActionLabel = "cancel"
                                    }) else null
                                )
                            }
                        }
                        val totalAvailable = current.artists.filtered()
                            .filter { artist -> !draftPickMode || !hideDrafted || draftedByArtist[artist.name.lowercase()] == null }
                            .marketDistinct()
                            .size
                        if (artistListVisible && filteredArtists.isNotEmpty() && (visibleCount < totalAvailable || loadingMoreArtists)) {
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
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            MarketInitializingScreen(onOpenMenu = onOpenMenu)
        }
        pendingActionArtist?.let { artist ->
            val action = pendingActionLabel.orEmpty()
            ConfirmActionCard(
                title = when (action) {
                    "draft" -> "Draft ${artist.name}?"
                    "waiver" -> "Queue Waiver?"
                    "cancel" -> "Cancel Waiver?"
                    "drop" -> "Drop ${artist.name}?"
                    else -> "Confirm Action?"
                },
                detail = when (action) {
                    "draft" -> "This uses your current draft pick."
                    "waiver" -> "This adds ${artist.name} to your waiver queue."
                    "cancel" -> "This removes ${artist.name} from your waiver queue."
                    "drop" -> "This removes ${artist.name} from your roster."
                    else -> "Confirm this roster action."
                },
                confirmText = when (action) {
                    "draft" -> "Draft"
                    "waiver" -> "Queue"
                    "cancel" -> "Cancel Claim"
                    "drop" -> "Drop"
                    else -> "Confirm"
                },
                accent = if (action == "waiver") WaiverAccent else BreakoutCoral,
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