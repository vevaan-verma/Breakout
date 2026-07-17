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

@Composable
internal fun MatchupScreen(
    league: LeagueUi,
    account: AccountUi?,
    roster: Map<RosterSlot, ArtistUi>,
    draftPicks: List<DraftPickUi>,
    members: List<LeagueMemberUi>?,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    onOpenWeek: (Int) -> Unit,
    onOpenMarket: (MarketFilter) -> Unit,
    onArtistSelected: (ArtistUi) -> Unit
) {
    val context = LocalContext.current
    val slots = activeRosterSlots(league.settings)
    val startingSlots = slots.filterNot { it.isBenchSlot() }
    val benchSlots = slots.filter { it.isBenchSlot() }
    val rosterEntries = slots.mapNotNull { slot -> roster[slot]?.let { slot to it } }
    val startingRosterEntries = startingSlots.mapNotNull { slot -> roster[slot]?.let { slot to it } }
    val rosterArtists = rosterEntries.map { it.second } + draftPicks.map { it.artist }
    var matchupReady by remember(rosterArtists.joinToString { it.name }) { mutableStateOf(rosterArtists.isEmpty()) }
    val hasByeWeek = league.memberCount > 1 && league.memberCount % 2 == 1
    val selfName = account?.username.orEmpty()
    val loadedMembers = members.orEmpty()
    val scheduleMembers = loadedMembers.mapNotNull { it.username.takeIf { name -> name.isNotBlank() } }
    val currentWeek = currentLeagueWeek(league)
    val currentMatchup = matchupForWeek(scheduleMembers, selfName, currentWeek)
    val opponentName = currentMatchup?.opponent ?: "Opponent"
    val opponentRoster = if (opponentName == "Opponent") {
        emptyMap()
    } else {
        draftPicks
            .filter { it.pickedBy.equals(opponentName, ignoreCase = true) }
            .associate { it.slot to it.artist }
    }
    val currentHasBye = currentMatchup?.isBye == true || hasByeWeek && currentMatchup == null
    val schedulePreview = scheduleForUser(scheduleMembers, selfName, league.settings.seasonWeeks)
    val waitingForMembers = league.memberCount > 1 && members == null
    LaunchedEffect(rosterArtists.joinToString { it.name }) {
        matchupReady = rosterArtists.isEmpty()
        if (rosterArtists.isNotEmpty()) {
            prefetchArtistImages(context, rosterArtists)
            matchupReady = true
        }
    }
    val userScore = startingRosterEntries.sumOf { (_, artist) -> artist.breakoutScore(null) }
    val opponentScore = if (league.memberCount > 1) {
        startingSlots.mapNotNull { opponentRoster[it] }.sumOf { it.breakoutScore(null) }
    } else null
    val userCurrentScore = 0.0
    val opponentCurrentScore = if (league.memberCount > 1) 0.0 else null
    if (!matchupReady || waitingForMembers) ScreenColumn(
        refreshing = refreshing,
        onRefresh = onRefresh,
        stickyTopBar = { TopTitle(title = "Matchup", subtitle = league.name, onMenuClick = onOpenMenu) }
    ) {
        LoadingState("Loading Matchup")
    } else ScreenColumn(
        refreshing = refreshing,
        onRefresh = onRefresh,
        stickyTopBar = { TopTitle(title = "Matchup", subtitle = league.name, onMenuClick = onOpenMenu) }
    ) {
        MatchupScoreboardCard(
            opponentName = opponentName,
            currentHasBye = currentHasBye,
            currentWeek = currentWeek,
            userProjected = userScore,
            opponentProjected = opponentScore,
            userCurrent = userCurrentScore,
            opponentCurrent = opponentCurrentScore
        )
        if (rosterEntries.isEmpty()) {
            StatusCard(
                title = "No Artists Yet",
                detail = "Your matchup fills in after your roster has drafted artists."
            )
        }
        BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
            Text("Head To Head", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                Text("You", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = BreakoutSecondary)
                Text(if (currentHasBye) "Bye" else opponentName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = BreakoutTextSecondary, textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            startingSlots.forEach { slot ->
                MatchupSlotComparisonRow(
                    slot = slot,
                    userArtist = roster[slot],
                    opponentArtist = opponentRoster[slot],
                    onOpenUserSlot = { targetSlot ->
                        onOpenMarket(if (targetSlot.isBenchSlot()) MarketFilter.Trending else targetSlot.filter)
                    },
                    onArtistSelected = onArtistSelected
                )
            }
            if (benchSlots.isNotEmpty()) {
                BenchDivider()
                benchSlots.forEach { slot ->
                    MatchupSlotComparisonRow(
                        slot = slot,
                        userArtist = roster[slot],
                        opponentArtist = opponentRoster[slot],
                        onOpenUserSlot = { targetSlot ->
                            onOpenMarket(if (targetSlot.isBenchSlot()) MarketFilter.Trending else targetSlot.filter)
                        },
                        onArtistSelected = onArtistSelected
                    )
                }
            }
        }
        if (league.memberCount < 2) {
            StatusCard(
                title = "Waiting for an Opponent",
                detail = "Matchups begin once another member joins this league."
            )
        } else if (currentHasBye) {
            StatusCard(
                title = "Bye Week",
                detail = "Odd-member leagues rotate one bye each scoring week so every active matchup stays head-to-head."
            )
        }
        if (schedulePreview.isNotEmpty()) {
            BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
                Text("Season Schedule", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                schedulePreview.take(league.settings.seasonWeeks).forEach { week ->
                    MatchupScheduleRow(
                        week = week,
                        isCurrent = week.week == currentWeek,
                        onClick = { onOpenWeek(week.week) }
                    )
                }
            }
        }
    }
}

@Composable
internal fun AllMatchupsScreen(
    league: LeagueUi,
    members: List<LeagueMemberUi>?,
    draftPicks: List<DraftPickUi>,
    initialWeek: Int,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit
) {
    val memberNames = members.orEmpty()
        .map { it.username }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() }
    val weekCount = league.settings.seasonWeeks.coerceAtLeast(1)
    var selectedWeek by rememberSaveable(league.id) { mutableStateOf(initialWeek.coerceIn(1, weekCount)) }
    var weekDirection by remember { mutableStateOf(1) }
    LaunchedEffect(initialWeek, weekCount) {
        selectedWeek = initialWeek.coerceIn(1, weekCount)
    }
    val pairs = matchupPairsForWeek(memberNames, selectedWeek)
    ScreenColumn(
        refreshing = refreshing,
        onRefresh = onRefresh,
        stickyTopBar = { TopTitle(title = "All Matchups", subtitle = league.name, onMenuClick = onOpenMenu) }
    ) {
        BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RosterActionIcon(
                    text = "<",
                    accent = BreakoutPrimary,
                    onClick = {
                        weekDirection = -1
                        selectedWeek = if (selectedWeek == 1) weekCount else selectedWeek - 1
                    }
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Week $selectedWeek", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                    Text(
                        if (selectedWeek == 1) "Current Matchups" else "Season Matchups",
                        color = BreakoutTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                RosterActionIcon(
                    text = ">",
                    accent = BreakoutPrimary,
                    onClick = {
                        weekDirection = 1
                        selectedWeek = if (selectedWeek == weekCount) 1 else selectedWeek + 1
                    }
                )
            }
        }
        if (memberNames.size < 2) {
            StatusCard("Waiting for Members", "Matchups appear once at least two members are in the league.")
        } else {
            AnimatedContent(
                targetState = selectedWeek,
                transitionSpec = {
                    if (weekDirection >= 0) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                    }
                },
                label = "allMatchupsWeek"
            ) { week ->
                Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                    matchupPairsForWeek(memberNames, week).forEach { pair ->
                        AllMatchupCard(
                            leftName = pair.first,
                            rightName = pair.second,
                            league = league,
                            draftPicks = draftPicks,
                            onArtistSelected = onArtistSelected
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun AllMatchupCard(
    leftName: String,
    rightName: String?,
    league: LeagueUi,
    draftPicks: List<DraftPickUi>,
    onArtistSelected: (ArtistUi) -> Unit
) {
    val slots = activeRosterSlots(league.settings)
    val startingSlots = slots.filterNot { it.isBenchSlot() }
    val benchSlots = slots.filter { it.isBenchSlot() }
    val leftRoster = rosterForMemberName(leftName, draftPicks)
    val rightRoster = rightName?.let { rosterForMemberName(it, draftPicks) }.orEmpty()
    val leftScore = startingSlots.mapNotNull { leftRoster[it] }.sumOf { it.breakoutScore(null) }
    val rightScore = rightName?.let { startingSlots.mapNotNull { rightRoster[it] }.sumOf { artist -> artist.breakoutScore(null) } }
    BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
        Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing), verticalAlignment = Alignment.CenterVertically) {
            MatchupScoreSide(leftName, "--", leftScore.formatPoints(), Modifier.weight(1f), alignEnd = false)
            Text("VS", color = BreakoutPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            MatchupScoreSide(rightName ?: "Bye", "--", rightScore?.formatPoints() ?: "--", Modifier.weight(1f), alignEnd = true)
        }
        startingSlots.forEach { slot ->
            MatchupSlotComparisonRow(
                slot = slot,
                userArtist = leftRoster[slot],
                opponentArtist = rightRoster[slot],
                onArtistSelected = onArtistSelected
            )
        }
        if (benchSlots.isNotEmpty()) {
            BenchDivider()
            benchSlots.forEach { slot ->
                MatchupSlotComparisonRow(
                    slot = slot,
                    userArtist = leftRoster[slot],
                    opponentArtist = rightRoster[slot],
                    onArtistSelected = onArtistSelected
                )
            }
        }
    }
}

@Composable
internal fun MatchupScheduleRow(week: MatchupWeekUi, isCurrent: Boolean, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(
                when {
                    isCurrent -> WaiverAccent.copy(alpha = 0.12f)
                    week.isBye -> BreakoutCoral.copy(alpha = 0.12f)
                    else -> BreakoutSurfaceVariant.copy(alpha = 0.62f)
                }
            )
            .border(
                1.dp,
                when {
                    isCurrent -> WaiverAccent.copy(alpha = 0.46f)
                    week.isBye -> BreakoutCoral.copy(alpha = 0.28f)
                    else -> BreakoutOutline.copy(alpha = 0.35f)
                },
                RoundedCornerShape(BreakoutDimensions.SmallCornerRadius)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = BreakoutDimensions.md, vertical = BreakoutDimensions.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(if (isCurrent) "Week ${week.week} - Current" else "Week ${week.week}", style = MaterialTheme.typography.titleSmall)
        Text(
            if (week.isBye) "Bye" else "vs ${week.opponent ?: "TBD"}",
            color = if (week.isBye) BreakoutCoral else BreakoutSecondary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun MatchupScoreboardCard(
    opponentName: String,
    currentHasBye: Boolean,
    currentWeek: Int,
    userProjected: Double,
    opponentProjected: Double?,
    userCurrent: Double,
    opponentCurrent: Double?
) {
    BreakoutCard(
        contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding),
        border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.42f))
    ) {
        Text(
            if (currentHasBye) "Current Matchup - Week $currentWeek Bye" else "Current Matchup - Week $currentWeek",
            color = BreakoutTextSecondary,
            style = MaterialTheme.typography.labelLarge
        )
        Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing), verticalAlignment = Alignment.CenterVertically) {
            MatchupScoreSide("You", userCurrent.formatPoints(), userProjected.formatPoints(), Modifier.weight(1f), alignEnd = false)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(BreakoutPrimary.copy(alpha = 0.16f))
                    .border(1.dp, BreakoutPrimary.copy(alpha = 0.38f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("VS", color = BreakoutPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
            }
            MatchupScoreSide(
                if (currentHasBye) "Bye" else opponentName,
                if (currentHasBye) "--" else opponentCurrent?.formatPoints() ?: "--",
                if (currentHasBye) "--" else opponentProjected?.formatPoints() ?: "--",
                Modifier.weight(1f),
                alignEnd = true
            )
        }
    }
}

@Composable
internal fun MatchupScoreSide(
    name: String,
    current: String,
    projected: String,
    modifier: Modifier,
    alignEnd: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
    ) {
        Text(
            name,
            style = MaterialTheme.typography.titleMedium,
            color = if (name == "You") WaiverAccent else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (name == "You") FontWeight.Black else FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(current, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Text("Proj. $projected", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
internal fun ScheduleCheckCard(messages: List<String>) {
    val scheduleYellow = Color(0xFFFFC857)
    AlertNoticeCard(
        title = "Schedule Check",
        messages = messages,
        accent = scheduleYellow,
        symbol = "!"
    )
}

@Composable
internal fun BenchDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = BreakoutDimensions.sm, bottom = BreakoutDimensions.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
    ) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(BreakoutOutline.copy(alpha = 0.36f))
        )
        Text("Bench", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(BreakoutOutline.copy(alpha = 0.36f))
        )
    }
}

@Composable
internal fun MatchupSlotComparisonRow(
    slot: RosterSlot,
    userArtist: ArtistUi?,
    opponentArtist: ArtistUi?,
    onOpenUserSlot: (RosterSlot) -> Unit = {},
    onArtistSelected: (ArtistUi) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(BreakoutPrimary.copy(alpha = 0.72f))
            )
            Text(slot.label, color = BreakoutTextSecondary, style = MaterialTheme.typography.labelMedium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm), verticalAlignment = Alignment.CenterVertically) {
            MatchupSideCell(
                slot = slot,
                artist = userArtist,
                alignEnd = false,
                onOpenSlot = { onOpenUserSlot(slot) },
                onArtistSelected = onArtistSelected,
                modifier = Modifier.weight(1f)
            )
            Text(":", color = BreakoutOutline, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            MatchupSideCell(
                slot = slot,
                artist = opponentArtist,
                alignEnd = true,
                onOpenSlot = null,
                onArtistSelected = onArtistSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
internal fun MatchupSideCell(
    slot: RosterSlot,
    artist: ArtistUi?,
    alignEnd: Boolean,
    onOpenSlot: (() -> Unit)?,
    onArtistSelected: (ArtistUi) -> Unit,
    modifier: Modifier = Modifier
) {
    val isOpen = artist == null
    val borderColor = if (isOpen) BreakoutOutline.copy(alpha = 0.34f) else BreakoutPrimary.copy(alpha = 0.38f)
    val backgroundColor = if (isOpen) BreakoutSurfaceVariant.copy(alpha = 0.34f) else BreakoutPrimary.copy(alpha = 0.10f)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .then(
                when {
                    artist != null -> Modifier.clickable { onArtistSelected(artist) }
                    onOpenSlot != null -> Modifier.clickable(onClick = onOpenSlot)
                    else -> Modifier
                }
            )
            .heightIn(min = 82.dp)
            .padding(horizontal = BreakoutDimensions.md, vertical = BreakoutDimensions.sm),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
    ) {
        Text(
            artist?.name ?: "Open",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (isOpen) BreakoutTextSecondary else Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (alignEnd) TextAlign.End else TextAlign.Start
        )
        Text(
            if (isOpen) "Unfilled slot" else "Proj. ${artist?.breakoutScore(null)?.formatScore() ?: "--"}",
            color = if (isOpen) BreakoutTextSecondary.copy(alpha = 0.75f) else BreakoutTextSecondary,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
        Text(
            if (isOpen) "Needs artist" else "Current --",
            color = if (isOpen) BreakoutTextSecondary.copy(alpha = 0.75f) else BreakoutTextSecondary,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}

@Composable
internal fun MatchupArtistRow(slot: RosterSlot, artist: ArtistUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .clickable(onClick = onClick)
            .padding(vertical = BreakoutDimensions.sm),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtistArtwork(artist = artist, size = BreakoutDimensions.ArtworkList)
        Column(modifier = Modifier.weight(1f)) {
            Text(artist.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(slot.label, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(artist.breakoutScore(null).formatScore(), style = MaterialTheme.typography.titleMedium)
            Text("Proj.", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelSmall)
        }
    }
}

