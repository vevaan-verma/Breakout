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
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
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

@Composable
internal fun DraftScreen(
    league: LeagueUi,
    account: AccountUi?,
    members: List<LeagueMemberUi>?,
    draftPresence: DraftPresenceUi?,
    draftPicks: List<DraftPickUi>,
    roster: Map<RosterSlot, ArtistUi>,
    draftRoomOpen: Boolean,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    onUpdateLeague: ((LeagueUi) -> LeagueUi) -> Unit,
    onOpenDraftRoom: () -> Unit,
    onCloseDraftRoom: () -> Unit,
    onOpenDraftMarket: () -> Unit,
    onOpenDraftRoster: () -> Unit,
    onOpenRoster: () -> Unit,
    onOpenDraftSummary: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenLeagueSettings: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit,
    onDraftPick: (ArtistUi, Boolean) -> Unit,
    onAutoPickChange: (Boolean) -> Unit,
    onStartLiveDraft: () -> Unit,
    onSetupWarningSelected: (String) -> Unit
) {
    val slots = activeRosterSlots(league.settings)
    val filledSlots = roster.keys.count { it in slots }
    val totalPicks = (slots.size * league.memberCount).coerceAtLeast(slots.size)
    val currentPicker = currentDraftPicker(league, members, account)
    val draftDateLabel = league.settings.draftDateLabel.displayDraftDateLabel()
    val estimatedDraftLength = formatDraftLength(totalPicks * league.settings.pickSeconds)
    val setupWarnings = buildList {
        if (league.memberCount < MinLeagueMembers) {
            add("Invite at least ${MinLeagueMembers - league.memberCount} more ${if (MinLeagueMembers - league.memberCount == 1) "member" else "members"}")
        } else if (league.settings.draftDateLabel == "Set date") {
            add("Set a draft time")
        }
    }

    if (draftRoomOpen && league.draftStatus != DraftStatus.Complete) {
        DraftRoomScreen(
            league = league,
            account = account,
            members = members,
            draftPresence = draftPresence,
            draftPicks = draftPicks,
            roster = roster,
            refreshing = refreshing,
            onRefresh = onRefresh,
            onClose = onCloseDraftRoom,
            onUpdateLeague = onUpdateLeague,
            onOpenDraftMarket = onOpenDraftMarket,
            onOpenDraftRoster = onOpenDraftRoster,
            onOpenRoster = onOpenRoster,
            onOpenDraftSummary = onOpenDraftSummary,
            onOpenHome = onOpenHome,
            onAutoPickChange = onAutoPickChange,
            onStartLiveDraft = onStartLiveDraft,
            onArtistSelected = onArtistSelected,
            onDraftPick = onDraftPick
        )
        return
    }

    ScreenColumn(
        refreshing = refreshing,
        onRefresh = onRefresh,
        stickyTopBar = { TopTitle(title = "Draft", subtitle = league.draftStatus.label, onMenuClick = onOpenMenu) }
    ) {
        ScreenHero(
            eyebrow = "Draft Center",
            title = when (league.draftStatus) {
                DraftStatus.Scheduled -> "Draft Room"
                DraftStatus.Lobby -> "Draft Lobby"
                DraftStatus.Practice -> "Draft Room"
                DraftStatus.Live -> "Live Draft"
                DraftStatus.Complete -> "Draft Complete"
            },
            subtitle = when (league.draftStatus) {
                DraftStatus.Scheduled -> "The room opens when the scheduled draft begins."
                DraftStatus.Lobby -> "The draft starts when enough members are present."
                DraftStatus.Practice -> "The room opens when the scheduled draft begins."
                DraftStatus.Live -> "Make one pick when you are on the clock. The timer advances automatically."
                DraftStatus.Complete -> "The draft is finished."
            },
            stats = listOf(
                Triple("Format", league.settings.draftFormat.label, draftDateLabel),
                Triple("Pick Clock", "${league.settings.pickSeconds}s", "Pick timeout")
            ),
            accent = if (league.draftStatus == DraftStatus.Live) BreakoutSecondary else BreakoutPrimary
        )
        BreakoutCard {
            if (league.draftStatus == DraftStatus.Scheduled && league.settings.draftDateLabel != "Set date") {
                draftCountdownLabel(league.settings.draftDateLabel)?.let { countdown ->
                    ScoreLine("Starts In", countdown)
                }
            }
            if (league.draftStatus == DraftStatus.Scheduled && league.memberCount >= 2) {
                ScoreLine("Estimated Length", estimatedDraftLength)
            }
            if (league.draftStatus == DraftStatus.Lobby) {
                val presence = draftPresence ?: DraftPresenceUi(memberCount = league.memberCount)
                ScoreLine("Members Here", "${presence.presentCount}/${presence.requiredCount}")
            }
            PrimaryButton(
                text = when (league.draftStatus) {
                    DraftStatus.Lobby -> "Join Lobby"
                    DraftStatus.Live -> "Join Draft Room"
                    DraftStatus.Complete -> "View Draft Summary"
                    else -> "Draft Room Locked"
                },
                enabled = when (league.draftStatus) {
                    DraftStatus.Lobby -> true
                    DraftStatus.Live -> league.memberCount >= 2 && filledSlots < slots.size
                    DraftStatus.Complete -> true
                    else -> false
                },
                onClick = {
                    if (league.draftStatus == DraftStatus.Complete) onOpenDraftSummary() else onOpenDraftRoom()
                }
            )
        }
        if (league.isManager && league.draftStatus == DraftStatus.Scheduled && setupWarnings.isNotEmpty()) {
            WarningCard(
                title = "Draft Setup Needed",
                messages = setupWarnings,
                onMessageClick = onSetupWarningSelected
            )
        } else if (!league.isManager && league.draftStatus == DraftStatus.Scheduled && league.settings.draftDateLabel == "Set date") {
            StatusCard(
                title = "Draft Time Pending",
                detail = "The manager will set the draft time."
            )
        }
        if (league.draftStatus != DraftStatus.Complete) {
            BreakoutCard {
                Text(if (league.draftStatus == DraftStatus.Live) "Live Draft" else "Draft Setup", style = MaterialTheme.typography.titleLarge)
                ScoreLine("Members", "${league.memberCount}/${league.maxMembers}")
                if (league.draftStatus == DraftStatus.Live && league.memberCount >= 2) {
                    ScoreLine("Current Pick", if (members == null) "Loading" else currentPicker)
                    ScoreLine("Round", ((league.currentPickIndex / league.memberCount.coerceAtLeast(1)) + 1).toString())
                    ScoreLine("Pick", "${(league.currentPickIndex + 1).coerceAtMost(totalPicks)} of $totalPicks")
                } else if (league.draftStatus == DraftStatus.Lobby) {
                    val presence = draftPresence ?: DraftPresenceUi(memberCount = league.memberCount)
                    ScoreLine("Lobby", "${presence.presentCount}/${presence.requiredCount} present")
                    ScoreLine("Grace Period", formatDraftLength(DraftLobbyGraceSeconds))
                } else {
                    ScoreLine("Draft Time", draftDateLabel)
                }
            }
        }
        BreakoutCard {
            Text("Rules Enforced", style = MaterialTheme.typography.titleLarge)
            ScoreLine("Headliners", league.settings.headlinerSlots.toString())
            ScoreLine("Mainstays", league.settings.wildcardSlots.toString())
            ScoreLine("Rising", league.settings.risingSlots.toString())
            ScoreLine("Deep Cuts", league.settings.deepCutSlots.toString())
            ScoreLine("Bench", league.settings.benchSlots.toString())
            if (league.isManager && league.draftStatus == DraftStatus.Scheduled) {
                SecondaryButton(
                    text = "Change Rules",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onOpenLeagueSettings
                )
            }
        }
    }
}

@Composable
internal fun DraftLobbyScreen(
    league: LeagueUi,
    presence: DraftPresenceUi,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onClose: () -> Unit,
    onStartLiveDraft: () -> Unit
) {
    var nowMs by remember { mutableStateOf(SupabaseLeagueService.syncedNowMillis()) }
    val lobbyStartedMs = league.currentPickStartedAt.parseServerInstantMillis() ?: nowMs
    val readyAtValue = presence.readyAt.ifBlank { league.lobbyReadyAt }
    val readyAtMs = readyAtValue.parseServerInstantMillis()
    val graceLeft = (((lobbyStartedMs + DraftLobbyGraceSeconds * 1000L) - nowMs).coerceAtLeast(0L) / 1000L)
        .coerceIn(0L, DraftLobbyGraceSeconds.toLong())
        .toInt()
    val readyCountdownLeft = readyAtMs?.let {
        (((it + DraftLobbyReadyCountdownSeconds * 1000L) - nowMs).coerceAtLeast(0L) / 1000L)
            .coerceIn(0L, DraftLobbyReadyCountdownSeconds.toLong())
            .toInt()
    }
    val progressTarget = if (presence.requiredCount <= 0) {
        0f
    } else {
        (presence.presentCount.toFloat() / presence.requiredCount.toFloat()).coerceIn(0f, 1f)
    }
    val animatedProgress by animateFloatAsState(progressTarget, label = "draftLobbyProgress")
    val countdownProgressTarget = readyCountdownLeft?.let { remaining ->
        1f - (remaining.toFloat() / DraftLobbyReadyCountdownSeconds.toFloat()).coerceIn(0f, 1f)
    } ?: 0f
    val animatedCountdownProgress by animateFloatAsState(countdownProgressTarget, label = "draftLobbyCountdownProgress")

    LaunchedEffect(Unit) {
        val baseSyncedMs = SupabaseLeagueService.syncedNowMillis()
        val baseElapsedMs = SystemClock.elapsedRealtime()
        while (true) {
            delay(100)
            nowMs = baseSyncedMs + (SystemClock.elapsedRealtime() - baseElapsedMs)
        }
    }

    LaunchedEffect(presence.presentCount, presence.requiredCount, readyAtValue, readyCountdownLeft) {
        if (presence.presentCount >= presence.requiredCount && readyAtMs != null && readyCountdownLeft == 0) {
            delay(250)
            onStartLiveDraft()
        }
    }

    ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
            ) {
                Text("Draft Lobby", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("Waiting for members", color = BreakoutTextSecondary)
            }
            SecondaryButton(text = "Back", onClick = onClose)
        }
        BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = BreakoutDimensions.md),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.size(156.dp),
                    color = BreakoutPrimary,
                    trackColor = BreakoutOutline.copy(alpha = 0.45f),
                    strokeWidth = 10.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${presence.presentCount}/${presence.requiredCount}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Here", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelLarge)
                }
            }
            Text(
                text = when {
                    readyCountdownLeft != null -> "Members ready. Starting soon."
                    else -> "The pick clock starts once enough members join."
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                StatTile("Members", "${presence.memberCount}", "In league", Modifier.weight(1f))
                if (readyCountdownLeft != null) {
                    StatTile("Countdown", "${readyCountdownLeft}s", "Starting soon", Modifier.weight(1f))
                } else {
                    StatTile("Grace", formatPickDuration(graceLeft), "Before delay", Modifier.weight(1f))
                }
            }
        }
        if (readyCountdownLeft != null) {
            BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = animatedCountdownProgress,
                            modifier = Modifier.size(72.dp),
                            color = BreakoutSecondary,
                            trackColor = BreakoutOutline.copy(alpha = 0.45f),
                            strokeWidth = 7.dp
                        )
                        Text("${readyCountdownLeft}s", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Starting Soon", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            "More members can still join before the first pick begins.",
                            color = BreakoutTextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            BreakoutCard {
                ScoreLine("Needed", "${(presence.requiredCount - presence.presentCount).coerceAtLeast(0)} more")
                ScoreLine("If Short", "Moves back ${DraftLobbyDelayMinutes} min")
            }
        }
    }
}

@Composable
internal fun DraftRoomScreen(
    league: LeagueUi,
    account: AccountUi?,
    members: List<LeagueMemberUi>?,
    draftPresence: DraftPresenceUi?,
    draftPicks: List<DraftPickUi>,
    roster: Map<RosterSlot, ArtistUi>,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onClose: () -> Unit,
    onUpdateLeague: ((LeagueUi) -> LeagueUi) -> Unit,
    onOpenDraftMarket: () -> Unit,
    onOpenDraftRoster: () -> Unit,
    onOpenRoster: () -> Unit,
    onOpenDraftSummary: () -> Unit,
    onOpenHome: () -> Unit,
    onAutoPickChange: (Boolean) -> Unit,
    onStartLiveDraft: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit,
    onDraftPick: (ArtistUi, Boolean) -> Unit
) {
    val context = LocalContext.current
    val slots = activeRosterSlots(league.settings)
    val totalPicks = (slots.size * league.memberCount).coerceAtLeast(slots.size)
    val currentPicker = currentDraftPicker(league, members, account)
    val userOnClock = isAccountOnClock(league, members, account)
    val userAutoPickEnabled = isMemberAutoPickEnabled(members, account, league)
    val round = (league.currentPickIndex / league.memberCount.coerceAtLeast(1)) + 1
    val pickInRound = (league.currentPickIndex % league.memberCount.coerceAtLeast(1)) + 1
    var query by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf<MarketFilter?>(null) }
    var hideDrafted by rememberSaveable { mutableStateOf(true) }
    var roomView by rememberSaveable { mutableStateOf(DraftRoomView.Home) }
    var missedManualTimeouts by rememberSaveable { mutableStateOf(0) }
    var nowMs by remember { mutableStateOf(SupabaseLeagueService.syncedNowMillis()) }
    var state by remember { mutableStateOf<MarketState>(MarketState.Loading) }
    val listState = rememberLazyListState()
    var pullDistance by remember { mutableStateOf(0f) }
    var refreshTriggered by remember { mutableStateOf(false) }
    val pullOffset by animateFloatAsState(
        targetValue = when {
            refreshing -> 28f
            pullDistance > 0f -> pullDistance * 0.28f
            else -> 0f
        },
        label = "draftRoomPullOffset"
    )
    val nestedScrollConnection = remember(refreshing, listState) {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val atTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                if (!refreshing && !refreshTriggered && atTop && available.y > 0f) {
                    pullDistance = (pullDistance + available.y).coerceAtMost(154f)
                } else if (available.y < 0f) {
                    pullDistance = (pullDistance + available.y).coerceAtLeast(0f)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (!refreshing && !refreshTriggered && pullDistance >= 96f) {
                    refreshTriggered = true
                    onRefresh()
                }
                pullDistance = 0f
                return Velocity.Zero
            }
        }
    }
    val pickStartedMs = league.currentPickStartedAt.parseServerInstantMillis() ?: nowMs
    val secondsLeft = (((pickStartedMs + league.settings.pickSeconds * 1000L) - nowMs + 999L) / 1000L)
        .coerceIn(0L, league.settings.pickSeconds.toLong())
        .toInt()

    if (league.draftStatus == DraftStatus.Lobby) {
        DraftLobbyScreen(
            league = league,
            presence = draftPresence ?: DraftPresenceUi(memberCount = league.memberCount),
            refreshing = refreshing,
            onRefresh = onRefresh,
            onClose = onClose,
            onStartLiveDraft = onStartLiveDraft
        )
        return
    }

    if (league.draftStatus == DraftStatus.Complete) {
        DraftEndedScreen(
            draftPicks = draftPicks,
            memberCount = league.memberCount,
            onHome = onOpenHome,
            onRoster = onOpenRoster,
            onSummary = onOpenDraftSummary
        )
        return
    }

    val draftedByArtist = remember(draftPicks) { draftPicks.associateBy { it.artist.name.lowercase() } }

    fun List<ArtistUi>.available(): List<ArtistUi> =
        filter { artist -> draftedByArtist[artist.name.lowercase()] == null && roster.values.none { it.name == artist.name } && firstOpenSlotFor(artist, roster, league.settings) != null }
            .filter { query.isBlank() || it.name.contains(query.trim(), ignoreCase = true) }
            .filter { selectedFilter == null || it.marketBucket() == selectedFilter }
            .sortedByDescending { it.projectedScore }

    fun List<ArtistUi>.boardResults(): List<ArtistUi> =
        filter { query.isBlank() || it.name.contains(query.trim(), ignoreCase = true) }
            .filter { selectedFilter == null || it.marketBucket() == selectedFilter }
            .filter { !hideDrafted || draftedByArtist[it.name.lowercase()] == null }
            .sortedWith(
                compareBy<ArtistUi> { draftedByArtist[it.name.lowercase()] != null }
                    .thenByDescending { it.projectedScore }
            )

    fun recommendedDraftPick(): ArtistUi? =
        (state as? MarketState.Ready)?.artists?.strategicDraftRecommendation(
            roster = roster,
            settings = league.settings,
            currentPickIndex = league.currentPickIndex,
            memberCount = league.memberCount,
            snapshots = emptyMap(),
            unavailableArtistNames = draftedByArtist.keys
        )

    LaunchedEffect(Unit) {
        onRefresh()
        state = runCatching {
            val artists = coroutineScope {
                val headliners = async { MusicArtistService.headlinerCandidates() }
                val rising = async { MusicArtistService.risingCandidates() }
                val deepCuts = async { MusicArtistService.deepCutCandidates() }
                val wildcards = async { MusicArtistService.wildcardCandidates() }
                headliners.await() + rising.await() + deepCuts.await() + wildcards.await()
            }.distinctBy { it.name.lowercase() }
            prefetchArtistImages(context, artists.available().take(60))
            if (artists.isEmpty()) MarketState.Empty("No artists found.") else MarketState.Ready(artists)
        }.getOrElse {
            if (it is CancellationException) throw it
            MarketState.Error("Could not load draft board.")
        }
    }

    LaunchedEffect(league.currentPickIndex, league.settings.pickSeconds, league.draftStatus, league.currentPickStartedAt) {
        val baseSyncedMs = SupabaseLeagueService.syncedNowMillis()
        val baseElapsedMs = SystemClock.elapsedRealtime()
        nowMs = baseSyncedMs
        while (league.draftStatus == DraftStatus.Live) {
            delay(100)
            nowMs = baseSyncedMs + (SystemClock.elapsedRealtime() - baseElapsedMs)
        }
    }

    LaunchedEffect(userAutoPickEnabled, userOnClock, league.currentPickIndex, state) {
        if (league.draftStatus == DraftStatus.Live && userAutoPickEnabled && userOnClock) {
            delay(5_000)
            val autoPick = recommendedDraftPick()
            if (autoPick != null) {
                onDraftPick(autoPick, true)
            }
        }
    }

    LaunchedEffect(secondsLeft, userOnClock, league.currentPickIndex, state) {
        val timeoutPick = recommendedDraftPick()
        if (league.draftStatus == DraftStatus.Live && secondsLeft == 0 && userOnClock && timeoutPick != null) {
            if (!userAutoPickEnabled) {
                missedManualTimeouts += 1
                if (missedManualTimeouts >= 2) {
                    onAutoPickChange(true)
                    missedManualTimeouts = 0
                }
            }
            onDraftPick(timeoutPick, true)
        }
    }

    LaunchedEffect(league.currentPickIndex, userOnClock) {
        if (userOnClock && userAutoPickEnabled) {
            missedManualTimeouts = 0
        }
    }

    LaunchedEffect(refreshing) {
        if (!refreshing) refreshTriggered = false
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .nestedScroll(nestedScrollConnection)
            .graphicsLayer { translationY = pullOffset },
        contentPadding = PaddingValues(
            start = BreakoutDimensions.ScreenHorizontalPadding,
            top = BreakoutDimensions.xs,
            end = BreakoutDimensions.ScreenHorizontalPadding,
            bottom = BreakoutDimensions.SectionSpacing
        ),
        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Draft Room", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text("Round $round, Pick $pickInRound", color = BreakoutTextSecondary)
                }
                SecondaryButton(text = if (roomView == DraftRoomView.Home) "Back" else "Room", onClick = {
                    if (roomView == DraftRoomView.Home) onClose() else roomView = DraftRoomView.Home
                })
            }
        }
        item {
            DraftPickStrip(
                league = league,
                account = account,
                members = members,
                draftPicks = draftPicks,
                totalPicks = totalPicks,
                onPickSelected = onArtistSelected
            )
        }
        item {
            BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
                Text(
                    if (userOnClock) "You Are On The Clock" else "$currentPicker Is Picking",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                    StatTile("Time Left", "${secondsLeft}s", "Auto-pick at zero", Modifier.weight(1f))
                    StatTile("Pick", "${league.currentPickIndex + 1}/$totalPicks", league.settings.draftFormat.label, Modifier.weight(1f))
                }
                ToggleRow(
                    label = "Auto-Pick",
                    value = if (userAutoPickEnabled) "On" else "Off",
                    enabled = true,
                    onToggle = { onAutoPickChange(!userAutoPickEnabled) }
                )
            }
        }
        when (roomView) {
            DraftRoomView.Home -> {
                item {
                    PrimaryButton(
                        text = "Make Your Pick",
                        enabled = userOnClock && !userAutoPickEnabled,
                        onClick = onOpenDraftMarket
                    )
                }
                item {
                    BreakoutCard {
                        Text("Draft Tools", style = MaterialTheme.typography.titleLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                            SecondaryButton(text = "Market", modifier = Modifier.weight(1f), onClick = onOpenDraftMarket)
                            SecondaryButton(text = "Roster", modifier = Modifier.weight(1f), onClick = onOpenDraftRoster)
                        }
                    }
                }
                item {
                    BreakoutCard {
                        Text("Recent Picks", style = MaterialTheme.typography.titleLarge)
                        if (draftPicks.isEmpty() && roster.isEmpty()) {
                            Text("No picks yet.", color = BreakoutTextSecondary)
                        } else {
                            draftPicks.take(8).forEach { pick ->
                                DraftPickSummaryRow(
                                    pickNumber = pick.pickNumber,
                                    memberCount = league.memberCount,
                                    pickedBy = if (pick.pickedBy.equals(account?.username.orEmpty(), ignoreCase = true)) "You" else pick.pickedBy,
                                    slot = pick.slot,
                                    artist = pick.artist,
                                    onClick = { onArtistSelected(pick.artist) }
                                )
                            }
                            if (draftPicks.isEmpty()) {
                                val recent = roster.entries.toList().asReversed().take(3)
                                recent.forEachIndexed { index, entry ->
                                    DraftPickSummaryRow(
                                        pickNumber = roster.size - index,
                                        memberCount = league.memberCount,
                                        pickedBy = "You",
                                        slot = entry.key,
                                        artist = entry.value,
                                        onClick = { onArtistSelected(entry.value) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            DraftRoomView.Roster -> item {
                BreakoutCard {
                    Text("Your Roster", style = MaterialTheme.typography.titleLarge)
                    ScoreLine("Filled", "${roster.size}/${slots.size}")
                    slots.forEach { slot ->
                        val artist = roster[slot]
                        ScoreLine(slot.label, artist?.name ?: "Open")
                    }
                }
            }
            DraftRoomView.Picks -> item {
                BreakoutCard {
                    Text("Draft Picks", style = MaterialTheme.typography.titleLarge)
                    if (draftPicks.isEmpty() && roster.isEmpty()) {
                        Text("No picks yet.", color = BreakoutTextSecondary)
                    } else {
                        if (draftPicks.isNotEmpty()) {
                            draftPicks.forEach { pick ->
                                DraftPickSummaryRow(
                                    pickNumber = pick.pickNumber,
                                    memberCount = league.memberCount,
                                    pickedBy = if (pick.pickedBy.equals(account?.username.orEmpty(), ignoreCase = true)) "You" else pick.pickedBy,
                                    slot = pick.slot,
                                    artist = pick.artist,
                                    onClick = { onArtistSelected(pick.artist) }
                                )
                            }
                        } else {
                            roster.entries.toList().forEachIndexed { index, entry ->
                                DraftPickSummaryRow(pickNumber = index + 1, memberCount = league.memberCount, pickedBy = "You", slot = entry.key, artist = entry.value, onClick = { onArtistSelected(entry.value) })
                            }
                        }
                    }
                }
            }
            DraftRoomView.Board -> {
                item {
                    StyledTextField(value = query, onValueChange = { query = it }, label = "Search Draft Board")
                }
                item {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
                    ) {
                        FilterChip(selected = selectedFilter == null, onClick = { selectedFilter = null }, label = { Text("All") })
                        MarketFilter.entries.forEach { filter ->
                            FilterChip(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, label = { Text(filter.label) })
                        }
                    }
                }
                item {
                    ToggleRow(
                        label = "Hide Drafted",
                        value = if (hideDrafted) "On" else "Off",
                        enabled = true,
                        onToggle = { hideDrafted = !hideDrafted }
                    )
                }
                when (val current = state) {
                    MarketState.Loading -> item { LoadingState("Loading Draft Board") }
                    is MarketState.Empty -> item { StatusCard("No Artists", current.message) }
                    is MarketState.Error -> item { StatusCard("Draft Board Error", current.message) }
                    is MarketState.Ready -> {
                        val boardArtists = current.artists.boardResults()
                        if (boardArtists.isEmpty()) {
                            item { StatusCard("No Eligible Artists", "Your remaining slots do not match the visible artists.") }
                        } else {
                            items(boardArtists.take(80).marketDistinct(), key = { it.stableListKey() }) { artist ->
                                val draftedPick = draftedByArtist[artist.name.lowercase()]
                                ArtistRow(
                                    modifier = Modifier.animateItem(),
                                    artist = artist,
                                    isInRoster = false,
                                    isDrafted = draftedPick != null,
                                    canToggleRoster = userOnClock && draftedPick == null && firstOpenSlotFor(artist, roster, league.settings) != null,
                                    statusLabel = draftedPick?.let { "Drafted" },
                                    onClick = { onArtistSelected(artist) },
                                    onToggleRoster = if (userOnClock && draftedPick == null) ({ onDraftPick(artist, false) }) else if (userOnClock) ({}) else null
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DraftPickStrip(
    league: LeagueUi,
    account: AccountUi?,
    members: List<LeagueMemberUi>?,
    draftPicks: List<DraftPickUi>,
    totalPicks: Int,
    onPickSelected: (ArtistUi) -> Unit
) {
    val memberCount = league.memberCount.coerceAtLeast(1)
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val chipWidth = 176.dp
    val chipSpacing = BreakoutDimensions.sm
    val picksByNumber = remember(draftPicks) { draftPicks.associateBy { it.pickNumber } }
    LaunchedEffect(league.currentPickIndex, totalPicks, screenWidth) {
        if (!listState.isScrollInProgress) {
            val viewportCenterOffset = with(density) { ((screenWidth - chipWidth) / 2).roundToPx().coerceAtLeast(0) }
            listState.animateScrollToItem(league.currentPickIndex.coerceIn(0, totalPicks.coerceAtLeast(1)), -viewportCenterOffset)
        }
    }
    BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.md)) {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = BreakoutDimensions.sm),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
        ) {
            items(totalPicks) { pickIndex ->
                val round = (pickIndex / memberCount) + 1
                val pick = (pickIndex % memberCount) + 1
                val name = draftPickerLabelAt(league, members, account, pickIndex)
                val draftedPick = picksByNumber[pickIndex + 1]
                val pickerUsername = draftPickerUsernameAt(league, members, pickIndex)
                val userAutoPick = members
                    ?.firstOrNull { it.username.equals(pickerUsername.orEmpty(), ignoreCase = true) }
                    ?.autoPickEnabled
                    ?: (league.autoPickEnabled && pickerUsername.equals(account?.username.orEmpty(), ignoreCase = true))
                DraftPickChip(
                    modifier = Modifier.width(chipWidth),
                    round = round,
                    pick = pick,
                    manager = name,
                    artistName = draftedPick?.artist?.name,
                    autoPick = draftedPick?.autoPicked ?: userAutoPick,
                    selected = pickIndex == league.currentPickIndex,
                    onClick = draftedPick?.let { { onPickSelected(it.artist) } }
                )
            }
            item {
                DraftPickChip(modifier = Modifier.width(chipWidth), round = null, pick = null, manager = "End", artistName = null, autoPick = false, selected = false, onClick = null)
            }
        }
    }
}

@Composable
internal fun DraftPickChip(
    modifier: Modifier = Modifier,
    round: Int?,
    pick: Int?,
    manager: String,
    artistName: String?,
    autoPick: Boolean,
    selected: Boolean,
    onClick: (() -> Unit)?
) {
    val background by animateColorAsState(
        targetValue = if (selected) BreakoutPrimary.copy(alpha = 0.35f) else BreakoutSurfaceVariant,
        label = "draftPickChip"
    )
    Surface(
        color = background,
        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
        border = BorderStroke(1.dp, if (selected) BreakoutPrimary else BreakoutOutline.copy(alpha = 0.55f)),
        modifier = modifier
            .heightIn(min = 112.dp, max = 112.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 84.dp)
                .padding(BreakoutDimensions.md)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    .padding(end = if (autoPick) 42.dp else 0.dp),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
            ) {
                Text(
                    if (round == null || pick == null) "End of Draft" else "Round $round, Pick $pick",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) BreakoutPrimary else BreakoutTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    artistName ?: manager,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (artistName != null) {
                    Text(
                        manager,
                        color = BreakoutTextSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            AnimatedVisibility(
                visible = autoPick,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(999.dp))
                    .background(BreakoutSecondary.copy(alpha = 0.16f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    "Auto",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
internal fun DraftPickSummaryRow(
    pickNumber: Int,
    memberCount: Int,
    pickedBy: String,
    slot: RosterSlot,
    artist: ArtistUi,
    onClick: () -> Unit
) {
    val safeMemberCount = memberCount.coerceAtLeast(1)
    val round = ((pickNumber - 1) / safeMemberCount) + 1
    val pick = ((pickNumber - 1) % safeMemberCount) + 1
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtistArtwork(artist = artist, size = BreakoutDimensions.ArtworkList)
        Column(modifier = Modifier.weight(1f)) {
            Text(artist.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Round $round, Pick $pick - $pickedBy", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(artist.tag, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
internal fun DraftEndedScreen(
    draftPicks: List<DraftPickUi>,
    memberCount: Int,
    onHome: () -> Unit,
    onRoster: () -> Unit,
    onSummary: () -> Unit
) {
    val roundCount = draftPicks.maxOfOrNull { ((it.pickNumber - 1) / memberCount.coerceAtLeast(1)) + 1 }
    ScreenColumn(refreshing = false, onRefresh = {}) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
                Text("Draft Complete", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Text("Every roster is locked in. Review the full board or jump straight into your team.", color = BreakoutTextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                    StatTile("Picks", draftPicks.size.toString(), "Completed", Modifier.weight(1f))
                    StatTile("Rounds", roundCount?.toString() ?: "--", "Final board", Modifier.weight(1f))
                }
                PrimaryButton(text = "View Draft Summary", onClick = onSummary)
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                    SecondaryButton(text = "Home", modifier = Modifier.weight(1f), onClick = onHome)
                    SecondaryButton(text = "Roster", modifier = Modifier.weight(1f), onClick = onRoster)
                }
            }
        }
    }
}

@Composable
internal fun DraftSummaryScreen(
    league: LeagueUi,
    account: AccountUi?,
    draftPicks: List<DraftPickUi>,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit
) {
    val memberCount = league.memberCount.coerceAtLeast(1)
    val sortedPicks = draftPicks.sortedBy { it.pickNumber }
    val rounds = sortedPicks.groupBy { ((it.pickNumber - 1) / memberCount) + 1 }
    ScreenColumn(
        refreshing = refreshing,
        onRefresh = onRefresh,
        stickyTopBar = { TopTitle(title = "Draft Summary", subtitle = league.name, onMenuClick = onOpenMenu) }
    ) {
        ScreenHero(
            eyebrow = "Draft Archive",
            title = "Final Draft Board",
            subtitle = "Every pick, round, manager, and pick time in one clean draft record.",
            stats = listOf(
                Triple("Picks", sortedPicks.size.toString(), "Completed"),
                Triple("Rounds", rounds.keys.maxOrNull()?.toString() ?: "--", league.settings.draftFormat.label),
                Triple("Members", memberCount.toString(), "Draft order"),
                Triple("Clock", "${league.settings.pickSeconds}s", "Pick limit")
            ),
            accent = BreakoutSecondary
        )
        if (sortedPicks.isEmpty()) {
            StatusCard("No Draft Picks", "The completed draft history will appear here once picks are synced.")
        } else {
            rounds.forEach { (round, picks) ->
                DraftSummaryRoundCard(
                    round = round,
                    picks = picks,
                    memberCount = memberCount,
                    account = account,
                    onArtistSelected = onArtistSelected
                )
            }
        }
    }
}

@Composable
internal fun DraftSummaryRoundCard(
    round: Int,
    picks: List<DraftPickUi>,
    memberCount: Int,
    account: AccountUi?,
    onArtistSelected: (ArtistUi) -> Unit
) {
    val roundDuration = picks.sumOf { it.secondsToPick ?: 0 }
    BreakoutCard(
        border = BorderStroke(1.dp, BreakoutSecondary.copy(alpha = 0.28f)),
        contentPadding = PaddingValues(BreakoutDimensions.lg)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                        .background(BreakoutSecondary.copy(alpha = 0.18f))
                        .border(1.dp, BreakoutSecondary.copy(alpha = 0.48f), RoundedCornerShape(BreakoutDimensions.SmallCornerRadius)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(round.toString(), color = BreakoutSecondary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Round $round", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("${picks.size} picks completed", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        if (roundDuration > 0) formatPickDuration(roundDuration) else "--",
                        color = BreakoutSecondary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black
                    )
                    Text("Total", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelSmall)
                }
            }
            picks.forEach { pick ->
                DraftSummaryPickTicket(
                    pick = pick,
                    roundPickNumber = ((pick.pickNumber - 1) % memberCount.coerceAtLeast(1)) + 1,
                    isYou = pick.pickedBy.equals(account?.username.orEmpty(), ignoreCase = true),
                    onArtistSelected = { onArtistSelected(pick.artist) }
                )
            }
        }
    }
}

@Composable
internal fun DraftSummaryPickTicket(
    pick: DraftPickUi,
    roundPickNumber: Int,
    isYou: Boolean,
    onArtistSelected: () -> Unit
) {
    val accent = if (isYou) WaiverAccent else BreakoutOutline
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.CardCornerRadius))
            .background(if (isYou) WaiverAccent.copy(alpha = 0.08f) else BreakoutSurfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, accent.copy(alpha = if (isYou) 0.58f else 0.22f), RoundedCornerShape(BreakoutDimensions.CardCornerRadius))
            .clickable(onClick = onArtistSelected)
            .padding(horizontal = BreakoutDimensions.sm, vertical = BreakoutDimensions.sm),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(38.dp)
                .height(58.dp)
                .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                .background(BreakoutSecondary.copy(alpha = 0.16f))
                .border(1.dp, BreakoutSecondary.copy(alpha = 0.42f), RoundedCornerShape(BreakoutDimensions.SmallCornerRadius)),
            contentAlignment = Alignment.Center
        ) {
            Text("#$roundPickNumber", color = BreakoutSecondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
        }
        ArtistArtwork(artist = pick.artist, size = 58.dp)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(pick.artist.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                if (isYou) "You" else pick.pickedBy,
                color = if (isYou) WaiverAccent else BreakoutTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isYou) FontWeight.Black else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    pick.artist.tag,
                    color = BreakoutPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatPickDuration(pick.secondsToPick), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
            Text("Clock", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
internal fun DraftSummaryPickRow(pick: DraftPickUi, onArtistSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(BreakoutSurfaceVariant.copy(alpha = 0.38f))
            .clickable(onClick = onArtistSelected)
            .padding(horizontal = BreakoutDimensions.xs, vertical = BreakoutDimensions.sm),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(34.dp)
                .height(54.dp)
                .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                .background(BreakoutSecondary.copy(alpha = 0.14f))
                .border(1.dp, BreakoutSecondary.copy(alpha = 0.34f), RoundedCornerShape(BreakoutDimensions.SmallCornerRadius)),
            contentAlignment = Alignment.Center
        ) {
            Text("#${pick.pickNumber}", color = BreakoutSecondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
        ArtistArtwork(artist = pick.artist, size = 54.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(pick.artist.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${pick.pickedBy} - ${pick.slot.label}", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatPickDuration(pick.secondsToPick), style = MaterialTheme.typography.labelLarge)
            Text("Pick Time", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelSmall)
        }
    }
}

