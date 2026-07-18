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
internal fun StandingsScreen(
    league: LeagueUi,
    currentUsername: String = "",
    members: List<LeagueMemberUi> = emptyList(),
    draftPicks: List<DraftPickUi> = emptyList(),
    weekOffset: Int = 0,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit
) {
    val memberNames = members
        .map { it.username }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() }
    val currentWeek = currentLeagueWeek(league, weekOffset)
    val completedWeeks = if (league.draftStatus == DraftStatus.Complete) (currentWeek - 1).coerceAtLeast(0) else 0
    val startingSlots = activeRosterSlots(league.settings).filterNot { it.isBenchSlot() }
    val rows = remember(memberNames, draftPicks, completedWeeks, league.settings) {
        val records = memberNames.associateWith {
            MutableStandingRow(name = it)
        }.toMutableMap()
        (1..completedWeeks).forEach { week ->
            matchupPairsForWeek(memberNames, week).forEach { pair ->
                val left = records[pair.first] ?: return@forEach
                val rightName = pair.second
                if (rightName == null) {
                    left.byes += 1
                    return@forEach
                }
                val right = records[rightName] ?: return@forEach
                val leftScore = draftPicks
                    .filter { it.pickedBy.equals(pair.first, ignoreCase = true) && it.slot in startingSlots }
                    .sumOf { it.artist.actualWeekScore(week) }
                val rightScore = draftPicks
                    .filter { it.pickedBy.equals(rightName, ignoreCase = true) && it.slot in startingSlots }
                    .sumOf { it.artist.actualWeekScore(week) }
                left.pointsFor += leftScore
                left.pointsAgainst += rightScore
                right.pointsFor += rightScore
                right.pointsAgainst += leftScore
                when {
                    leftScore > rightScore -> {
                        left.wins += 1
                        right.losses += 1
                    }
                    rightScore > leftScore -> {
                        right.wins += 1
                        left.losses += 1
                    }
                    else -> {
                        left.ties += 1
                        right.ties += 1
                    }
                }
            }
        }
        records.values.sortedWith(
            compareByDescending<MutableStandingRow> { it.wins }
                .thenByDescending { it.pointsFor }
                .thenBy { it.losses }
                .thenBy { it.name.lowercase() }
        )
    }
    ScreenColumn(
        refreshing = refreshing,
        onRefresh = onRefresh,
        stickyTopBar = { TopTitle(title = "Standings", subtitle = league.name, onMenuClick = onOpenMenu) }
    ) {
        ScreenHero(
            eyebrow = "Season Table",
            title = "Standings",
            subtitle = "League rank, records, and scoring totals will settle here as matchups finish.",
            stats = listOf(
                Triple("Members", league.memberCount.toString(), "Managers"),
                Triple("Weeks", league.settings.seasonWeeks.toString(), "Season"),
                Triple("Roster", league.settings.rosterSize.toString(), "Slots"),
                Triple("Format", league.settings.draftFormat.label, "Draft")
            ),
            accent = BreakoutSecondary
        )
        BreakoutCard {
            Text(
                if (completedWeeks == 0) "Awaiting Week 1" else "Through Week $completedWeeks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                if (completedWeeks == 0) "Results appear after scoring closes." else "Records include simulated and completed matchups.",
                color = BreakoutTextSecondary
            )
            rows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                        .background(BreakoutSurfaceVariant.copy(alpha = 0.45f))
                        .padding(BreakoutDimensions.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NumberBadge((index + 1).toString(), if (row.name.equals(currentUsername, ignoreCase = true)) WaiverAccent else BreakoutSecondary)
                        Column {
                            Text(
                                row.name.displayMemberName(currentUsername),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                color = if (row.name.equals(currentUsername, ignoreCase = true)) WaiverAccent else Color.White
                            )
                            Text(
                                "${row.wins}-${row.losses}${if (row.ties > 0) "-${row.ties}" else ""}${if (row.byes > 0) " · ${row.byes} bye" else ""}",
                                color = BreakoutTextSecondary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(row.pointsFor.formatPoints(), color = BreakoutPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("PF", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

private data class MutableStandingRow(
    val name: String,
    var wins: Int = 0,
    var losses: Int = 0,
    var ties: Int = 0,
    var byes: Int = 0,
    var pointsFor: Double = 0.0,
    var pointsAgainst: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LeagueScreen(
    league: LeagueUi,
    account: AccountUi?,
    draftPicks: List<DraftPickUi>,
    localMembers: List<LeagueMemberUi> = emptyList(),
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    onUpdateLeague: ((LeagueUi) -> LeagueUi) -> Unit,
    devModeEnabled: Boolean = false,
    simulatedWeekOffset: Int = 0,
    maxSimulatedWeekOffset: Int = 0,
    rostersLocked: Boolean = false,
    forceRosterUnlocked: Boolean = false,
    forceRosterLocked: Boolean = false,
    onSimulateWeeks: (Int) -> Unit = {},
    onForceRosterUnlockedChange: (Boolean) -> Unit = {},
    onForceRosterLockedChange: (Boolean) -> Unit = {},
    onAddBotMembers: (Int) -> Unit = {},
    onForceMakeManager: () -> Unit = {},
    onTransferManager: (String) -> Unit,
    onKickMember: (String) -> Unit,
    onOpenRoster: () -> Unit,
    memberRosterToOpen: String?,
    onMemberRosterOpened: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit,
    onMemberRosterArtistSelected: (LeagueMemberUi, ArtistUi) -> Unit,
    onTradeWithMember: (LeagueMemberUi) -> Unit,
    onRunWaivers: () -> Unit,
    onLeaveLeague: () -> Unit,
    onDeleteLeague: () -> Unit
) {
    val settings = league.settings
    val draftSettingsEditable = league.isManager && league.draftStatus == DraftStatus.Scheduled
    val draftTimeEditable = draftSettingsEditable && league.memberCount >= MinLeagueMembers
    var editedLeagueName by rememberSaveable(league.inviteCode) { mutableStateOf(league.name) }
    var members by remember { mutableStateOf<List<LeagueMemberUi>>(emptyList()) }
    var selectedMember by remember { mutableStateOf<LeagueMemberUi?>(null) }
    var rosterMember by remember { mutableStateOf<LeagueMemberUi?>(null) }
    var pendingTransfer by remember { mutableStateOf<LeagueMemberUi?>(null) }
    var pendingKick by remember { mutableStateOf<LeagueMemberUi?>(null) }
    var confirmRunWaivers by rememberSaveable(league.inviteCode) { mutableStateOf(false) }
    var simulationWeeks by rememberSaveable(league.id, maxSimulatedWeekOffset, simulatedWeekOffset) { mutableStateOf(0) }
    var botCountToAdd by rememberSaveable(league.id, league.memberCount, league.maxMembers) { mutableStateOf(0) }
    var confirmSimulation by rememberSaveable(league.id) { mutableStateOf(false) }
    var confirmRosterLockChange by rememberSaveable(league.id) { mutableStateOf(false) }
    var confirmAddBots by rememberSaveable(league.id) { mutableStateOf(false) }
    var confirmForceManager by rememberSaveable(league.id) { mutableStateOf(false) }
    var confirmLeave by rememberSaveable(league.inviteCode) { mutableStateOf(false) }
    var confirmDeleteLeague by rememberSaveable(league.inviteCode) { mutableStateOf(false) }
    var draftDateInput by rememberSaveable(league.id, settings.draftDateLabel) {
        mutableStateOf(draftDateInputPart(settings.draftDateLabel))
    }
    var draftTimeInput by rememberSaveable(league.id, settings.draftDateLabel) {
        mutableStateOf(draftTimeInputPart(settings.draftDateLabel))
    }
    var draftTimeError by remember { mutableStateOf<String?>(null) }
    var draftTimeSaved by remember { mutableStateOf(false) }
    var showDraftDatePicker by remember { mutableStateOf(false) }
    var showDraftTimePicker by remember { mutableStateOf(false) }
    val accountUsername = account?.username.orEmpty()
    val accountMemberName = accountUsername.ifBlank { account?.email?.substringBefore("@").orEmpty() }
    val leaveActionText = if (league.isManager && league.memberCount <= 1) "Delete League" else "Leave League"
    val leaveConfirmTitle = if (leaveActionText == "Delete League") "Delete League?" else "Leave League?"
    val leaveConfirmDetail = if (leaveActionText == "Delete League") {
        "This league has no other members, so leaving will permanently delete it."
    } else if (league.isManager) {
        "You will leave this league and manager access will move to another member."
    } else {
        "You will lose access to this league."
    }
    val scheduleWarnings = matchupScheduleWarnings(league.memberCount, settings.seasonWeeks)
    val currentWeek = currentLeagueWeek(league, simulatedWeekOffset)
    val invitesLockedByDraft = league.draftStatus != DraftStatus.Scheduled
    val minSimulationDelta = -simulatedWeekOffset
    val targetSimulationWeek = (currentWeek + simulationWeeks).coerceIn(1, settings.seasonWeeks.coerceAtLeast(1))
    val mergedMembers = (localMembers + members)
        .distinctBy { it.username.lowercase() }
    val availableBotSlots = (league.maxMembers - mergedMembers.size).coerceAtLeast(0)
    val visibleMembers = mergedMembers.ifEmpty {
        listOf(
            LeagueMemberUi(
                username = accountMemberName,
                teamName = accountMemberName,
                role = if (league.isManager) "manager" else "member"
            )
        )
    }

    LaunchedEffect(league.id, account?.accessToken) {
        val token = account?.accessToken.orEmpty()
        if (token.isNotBlank()) {
            members = SupabaseLeagueService.loadMembers(token, league.id).getOrDefault(emptyList())
        }
    }

    LaunchedEffect(memberRosterToOpen, members, account?.accessToken) {
        val username = memberRosterToOpen ?: return@LaunchedEffect
        val loadedMembers = if (members.isEmpty() && account?.accessToken?.isNotBlank() == true) {
            SupabaseLeagueService.loadMembers(account.accessToken, league.id).getOrDefault(emptyList()).also { members = it }
        } else {
            members
        }
        (loadedMembers + localMembers).distinctBy { it.username.lowercase() }
            .firstOrNull { it.username.equals(username, ignoreCase = true) }?.let {
            rosterMember = it
            onMemberRosterOpened()
        }
    }

    LaunchedEffect(draftTimeSaved) {
        if (draftTimeSaved) {
            delay(3000)
            draftTimeSaved = false
        }
    }

    LaunchedEffect(maxSimulatedWeekOffset) {
        simulationWeeks = simulationWeeks.coerceIn(minSimulationDelta, maxSimulatedWeekOffset)
    }

    LaunchedEffect(availableBotSlots) {
        botCountToAdd = botCountToAdd.coerceIn(0, availableBotSlots.coerceAtLeast(0))
    }

    ScreenColumn(
        refreshing = refreshing,
        onRefresh = onRefresh,
        stickyTopBar = {
            TopTitle(
                title = "League",
                subtitle = if (league.isManager) "Manager tools" else "League rules",
                onMenuClick = onOpenMenu
            )
        }
    ) {
        ScreenHero(
            eyebrow = if (league.isManager) "Manager Console" else "League Settings",
            title = league.name,
            subtitle = if (league.isManager) {
                "Manage draft setup, roster rules, invites, waivers, and members."
            } else {
                "Review league rules, draft timing, members, and season structure."
            },
            stats = listOf(
                Triple("Members", "${league.memberCount}/${league.maxMembers}", league.inviteState),
                Triple("Roster", settings.rosterSize.toString(), "Active slots"),
                Triple("Weeks", settings.seasonWeeks.toString(), "Season"),
                Triple("Waivers", settings.maxWaiverClaims.toString(), "Max claims")
            )
        )
        BreakoutCard {
            Text("League Details", style = MaterialTheme.typography.titleLarge)
            StyledTextField(
                value = editedLeagueName,
                onValueChange = { editedLeagueName = it },
                enabled = league.isManager,
                label = "League Name",
                maxLength = MaxLeagueNameLength
            )
            if (league.isManager) {
                PrimaryButton(
                    text = "Save Name",
                    enabled = editedLeagueName.isNotBlank() && editedLeagueName.trim() != league.name,
                    onClick = {
                        val cleanedName = editedLeagueName.trim()
                        onUpdateLeague { it.copy(name = cleanedName) }
                        editedLeagueName = cleanedName
                    }
                )
            }
            ScoreLine("Invite Code", league.inviteCode)
            ScoreLine("Invites", if (invitesLockedByDraft) "Closed for draft" else league.inviteState)
            StepperRow(
                label = "Max Members",
                value = league.maxMembers.toString(),
                enabled = draftSettingsEditable,
                minusEnabled = league.maxMembers > MinLeagueMembers,
                plusEnabled = league.maxMembers < MaxLeagueMembers,
                onMinus = { onUpdateLeague { it.copy(maxMembers = (it.maxMembers - 1).coerceAtLeast(MinLeagueMembers)) } },
                onPlus = { onUpdateLeague { it.copy(maxMembers = (it.maxMembers + 1).coerceAtMost(MaxLeagueMembers)) } }
            )
        }
        BreakoutCard {
            Text("Draft Setup", style = MaterialTheme.typography.titleLarge)
            if (league.isManager && league.draftStatus != DraftStatus.Scheduled) {
                Text(
                    "Draft setup is locked once the draft starts.",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            OptionCycleRow(
                label = "Draft Type",
                value = settings.draftFormat.label,
                enabled = draftSettingsEditable,
                onPrevious = {
                    onUpdateLeague { it.copy(settings = settings.copy(draftFormat = settings.draftFormat.previous())) }
                },
                onNext = {
                    onUpdateLeague { it.copy(settings = settings.copy(draftFormat = settings.draftFormat.next())) }
                }
            )
            Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
                Text("Draft Time", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
                if (league.memberCount < MinLeagueMembers) {
                    Surface(
                        color = WaiverAccent.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                        border = BorderStroke(1.dp, WaiverAccent.copy(alpha = 0.36f))
                    ) {
                        Text(
                            "Invite at least ${MinLeagueMembers - league.memberCount} more ${if (MinLeagueMembers - league.memberCount == 1) "member" else "members"} before setting a draft time.",
                            modifier = Modifier.padding(BreakoutDimensions.md),
                            color = WaiverAccent,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                    SecondaryButton(
                        text = draftDateDisplayPart(draftDateInput),
                        modifier = Modifier.weight(1f),
                        enabled = draftTimeEditable,
                        onClick = {
                            draftTimeError = null
                            showDraftDatePicker = true
                        }
                    )
                    SecondaryButton(
                        text = draftTimeDisplayPart(draftTimeInput),
                        modifier = Modifier.weight(1f),
                        enabled = draftTimeEditable,
                        onClick = {
                            draftTimeError = null
                            showDraftTimePicker = true
                        }
                    )
                }
                AnimatedFeedbackText(message = draftTimeError, color = BreakoutCoral)
                PrimaryButton(
                    text = if (draftTimeSaved) "Draft Time Saved" else "Save Draft Time",
                    enabled = draftTimeEditable &&
                        !draftTimeSaved &&
                        (draftDateInput.isNotBlank() || draftTimeInput.isNotBlank()) &&
                        settings.draftDateLabel != runCatching { draftDateLabelFromInputs(draftDateInput, draftTimeInput) }.getOrNull(),
                    onClick = {
                        val error = draftDateValidationError(draftDateInput, draftTimeInput)
                        if (error != null) {
                            draftTimeError = error
                            draftTimeSaved = false
                        } else {
                            val savedLabel = draftDateLabelFromInputs(draftDateInput, draftTimeInput)
                            draftTimeError = null
                            draftTimeSaved = true
                            onUpdateLeague { it.copy(settings = settings.copy(draftDateLabel = savedLabel)) }
                        }
                    }
                )
            }
            if (showDraftDatePicker) {
                DraftDatePickerDialog(
                    selectedDate = draftDateInput,
                    onDismiss = { showDraftDatePicker = false },
                    onDateSelected = {
                        draftDateInput = it
                        draftTimeError = null
                        showDraftDatePicker = false
                    }
                )
            }
            if (showDraftTimePicker) {
                DraftTimePickerDialog(
                    selectedTime = draftTimeInput,
                    onDismiss = { showDraftTimePicker = false },
                    onTimeSelected = {
                        draftTimeInput = it
                        draftTimeError = null
                        showDraftTimePicker = false
                    }
                )
            }
            StepperRow(
                label = "Pick Timer",
                value = "${settings.pickSeconds}s per pick",
                enabled = draftSettingsEditable,
                minusEnabled = settings.pickSeconds > 30,
                plusEnabled = settings.pickSeconds < 300,
                onMinus = { onUpdateLeague { it.copy(settings = settings.copy(pickSeconds = (settings.pickSeconds - 15).coerceAtLeast(30))) } },
                onPlus = { onUpdateLeague { it.copy(settings = settings.copy(pickSeconds = (settings.pickSeconds + 15).coerceAtMost(300))) } }
            )
            StepperRow(
                label = "Season Length",
                value = "${settings.seasonWeeks} weeks",
                enabled = draftSettingsEditable,
                minusEnabled = settings.seasonWeeks > MinSeasonWeeks,
                plusEnabled = settings.seasonWeeks < MaxSeasonWeeks,
                onMinus = { onUpdateLeague { it.copy(settings = settings.copy(seasonWeeks = (settings.seasonWeeks - 1).coerceAtLeast(MinSeasonWeeks))) } },
                onPlus = { onUpdateLeague { it.copy(settings = settings.copy(seasonWeeks = (settings.seasonWeeks + 1).coerceAtMost(MaxSeasonWeeks))) } }
            )
            if (league.isManager && scheduleWarnings.isNotEmpty()) {
                ScheduleCheckCard(messages = scheduleWarnings)
            }
        }
        BreakoutCard {
            Text("Roster Rules", style = MaterialTheme.typography.titleLarge)
            if (league.isManager && league.draftStatus != DraftStatus.Scheduled) {
                Text(
                    "Roster rules are locked after the draft starts so completed rosters stay valid.",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            StepperRow("Headliners", settings.headlinerSlots.toString(), draftSettingsEditable,
                minusEnabled = settings.headlinerSlots > 1,
                plusEnabled = settings.headlinerSlots < 4,
                onMinus = { onUpdateLeague { it.copy(settings = settings.copy(headlinerSlots = (settings.headlinerSlots - 1).coerceAtLeast(1))) } },
                onPlus = { onUpdateLeague { it.copy(settings = settings.copy(headlinerSlots = (settings.headlinerSlots + 1).coerceAtMost(4))) } }
            )
            StepperRow("Mainstays", settings.wildcardSlots.toString(), draftSettingsEditable,
                minusEnabled = settings.wildcardSlots > 0,
                plusEnabled = settings.wildcardSlots < 4,
                onMinus = { onUpdateLeague { it.copy(settings = settings.copy(wildcardSlots = (settings.wildcardSlots - 1).coerceAtLeast(0))) } },
                onPlus = { onUpdateLeague { it.copy(settings = settings.copy(wildcardSlots = (settings.wildcardSlots + 1).coerceAtMost(4))) } }
            )
            StepperRow("Rising", settings.risingSlots.toString(), draftSettingsEditable,
                minusEnabled = settings.risingSlots > 0,
                plusEnabled = settings.risingSlots < 4,
                onMinus = { onUpdateLeague { it.copy(settings = settings.copy(risingSlots = (settings.risingSlots - 1).coerceAtLeast(0))) } },
                onPlus = { onUpdateLeague { it.copy(settings = settings.copy(risingSlots = (settings.risingSlots + 1).coerceAtMost(4))) } }
            )
            StepperRow("Deep Cuts", settings.deepCutSlots.toString(), draftSettingsEditable,
                minusEnabled = settings.deepCutSlots > 0,
                plusEnabled = settings.deepCutSlots < 3,
                onMinus = { onUpdateLeague { it.copy(settings = settings.copy(deepCutSlots = (settings.deepCutSlots - 1).coerceAtLeast(0))) } },
                onPlus = { onUpdateLeague { it.copy(settings = settings.copy(deepCutSlots = (settings.deepCutSlots + 1).coerceAtMost(3))) } }
            )
            StepperRow("Bench", settings.benchSlots.toString(), draftSettingsEditable,
                minusEnabled = settings.benchSlots > 0,
                plusEnabled = settings.benchSlots < 6,
                onMinus = { onUpdateLeague { it.copy(settings = settings.copy(benchSlots = (settings.benchSlots - 1).coerceAtLeast(0))) } },
                onPlus = { onUpdateLeague { it.copy(settings = settings.copy(benchSlots = (settings.benchSlots + 1).coerceAtMost(6))) } }
            )
            StepperRow("Waiver Claims", "${settings.maxWaiverClaims} max", draftSettingsEditable,
                minusEnabled = settings.maxWaiverClaims > MinWaiverClaims,
                plusEnabled = settings.maxWaiverClaims < MaxWaiverClaims,
                onMinus = { onUpdateLeague { it.copy(settings = settings.copy(maxWaiverClaims = (settings.maxWaiverClaims - 1).coerceAtLeast(MinWaiverClaims))) } },
                onPlus = { onUpdateLeague { it.copy(settings = settings.copy(maxWaiverClaims = (settings.maxWaiverClaims + 1).coerceAtMost(MaxWaiverClaims))) } }
            )
        }
        if (league.isManager) {
            BreakoutCard {
                Text("Waiver Controls", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Process every pending claim in priority order.",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                SecondaryButton(
                    text = "Force Run Waivers",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = league.draftStatus == DraftStatus.Complete,
                    onClick = { confirmRunWaivers = true }
                )
            }
        }
        AnimatedVisibility(
            visible = devModeEnabled,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BreakoutCard {
                Text("Developer League Tools", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text(
                    "Test scoring weeks and roster lock behavior for this league.",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                ScoreLine("Live View", if (simulatedWeekOffset == 0) "Live week $currentWeek" else "Simulated week $currentWeek")
                ScoreLine(
                    "Simulation Target",
                    if (simulationWeeks == 0) "No week jump" else "Move to Week $targetSimulationWeek"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm), verticalAlignment = Alignment.CenterVertically) {
                    SecondaryButton(
                        text = "-",
                        modifier = Modifier.weight(1f),
                        enabled = simulationWeeks > minSimulationDelta,
                        onClick = { simulationWeeks = (simulationWeeks - 1).coerceAtLeast(minSimulationDelta) }
                    )
                    Surface(
                        modifier = Modifier.weight(1.2f),
                        color = BreakoutSurfaceVariant.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                        border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(BreakoutDimensions.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(simulationWeeks.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                            Text(
                                if (kotlin.math.abs(simulationWeeks) == 1) "week" else "weeks",
                                color = BreakoutTextSecondary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    SecondaryButton(
                        text = "+",
                        modifier = Modifier.weight(1f),
                        enabled = simulationWeeks < maxSimulatedWeekOffset,
                        onClick = { simulationWeeks = (simulationWeeks + 1).coerceAtMost(maxSimulatedWeekOffset) }
                    )
                }
                PrimaryButton(
                    text = when {
                        simulationWeeks > 0 -> "Simulate Forward"
                        simulationWeeks < 0 -> "Simulate Back"
                        else -> "Choose Week Move"
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = simulationWeeks != 0,
                    onClick = { confirmSimulation = true }
                )
                AnimatedContent(
                    targetState = rostersLocked,
                    transitionSpec = {
                        (fadeIn() + expandVertically()).togetherWith(fadeOut() + shrinkVertically())
                    },
                    label = "rosterLockDevButton"
                ) { currentlyLocked ->
                    SecondaryButton(
                        text = if (currentlyLocked) "Force Unlock Rosters" else "Force Lock Rosters",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true,
                        onClick = { confirmRosterLockChange = true }
                    )
                }
                AlertNoticeCard(
                    title = "Bot Schedule Warning",
                    messages = listOf("Adding bots after a league already exists is for testing. It can rebalance members without rebuilding every past schedule perfectly, so some test schedules may feel uneven."),
                    accent = BreakoutSecondary,
                    symbol = "!"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm), verticalAlignment = Alignment.CenterVertically) {
                    SecondaryButton(
                        text = "-",
                        modifier = Modifier.weight(1f),
                        enabled = botCountToAdd > 0,
                        onClick = { botCountToAdd = (botCountToAdd - 1).coerceAtLeast(0) }
                    )
                    Surface(
                        modifier = Modifier.weight(1.2f),
                        color = BreakoutSurfaceVariant.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                        border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(BreakoutDimensions.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(botCountToAdd.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                            Text(
                                if (botCountToAdd == 1) "bot" else "bots",
                                color = BreakoutTextSecondary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                    SecondaryButton(
                        text = "+",
                        modifier = Modifier.weight(1f),
                        enabled = botCountToAdd < availableBotSlots,
                        onClick = { botCountToAdd = (botCountToAdd + 1).coerceAtMost(availableBotSlots) }
                    )
                }
                SecondaryButton(
                    text = if (botCountToAdd == 1) "Add Bot" else "Add Bots",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = league.draftStatus != DraftStatus.Live && availableBotSlots > 0 && botCountToAdd > 0,
                    onClick = { confirmAddBots = true }
                )
                SecondaryButton(
                    text = if (league.isManager) "You Are Manager" else "Force Make Me Manager",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !league.isManager && accountUsername.isNotBlank(),
                    onClick = { confirmForceManager = true }
                )
            }
        }
        if (league.isManager) {
            BreakoutCard {
                Text("Mailing List", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Use this for draft reminders, league announcements, and product updates. Only members who opt in are included.",
                    color = BreakoutTextSecondary
                )
                ScoreLine("Local Subscriber", if (account?.mailingList == true) account.email else "None")
                ScoreLine("Access", "Manager only")
            }
        }
        BreakoutCard {
            Text("Members", style = MaterialTheme.typography.titleLarge)
            Text(
                if (league.isManager) {
                    "Manage access before the draft starts. Full leagues reject new joins automatically."
                } else {
                    "Ask the manager for invite, member, or rule changes."
                },
                color = BreakoutTextSecondary
            )
            ScoreLine("Manager", if (league.isManager) "You" else "League manager")
            ScoreLine("Members", "${league.memberCount}/${league.maxMembers}")
            if (league.isManager) {
                SecondaryButton(
                    if (invitesLockedByDraft) "Invites Closed For Draft" else if (league.invitesOpen) "Close Invites" else "Open Invites",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = league.isManager && !invitesLockedByDraft,
                    onClick = { onUpdateLeague { it.copy(invitesOpen = !it.invitesOpen) } }
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
                visibleMembers.forEach { member ->
                    MemberReviewRow(
                        member = member,
                        currentUsername = accountUsername,
                        canManage = league.isManager && mergedMembers.isNotEmpty() && !member.username.isReservedBotUsername(),
                        onOpen = { selectedMember = member },
                        onTransfer = { pendingTransfer = member },
                        onKick = { pendingKick = member }
                    )
                }
            }
            Text(
                if (league.isManager) "Only the current manager can change invites and draft settings." else "Member details are read-only unless you manage the league.",
                color = BreakoutTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (confirmRunWaivers) {
            ConfirmActionCard(
                title = "Run Waivers Now?",
                detail = "Pending claims will process immediately in waiver order.",
                confirmText = "Run Waivers",
                onCancel = { confirmRunWaivers = false },
                onConfirm = {
                    confirmRunWaivers = false
                    onRunWaivers()
                }
            )
        }
        pendingTransfer?.let { member ->
            ConfirmActionCard(
                title = "Transfer Manager?",
                detail = "${member.username} will become manager and you will become a member.",
                confirmText = "Transfer",
                onCancel = { pendingTransfer = null },
                onConfirm = {
                    onTransferManager(member.username)
                    pendingTransfer = null
                }
            )
        }
        if (confirmSimulation) {
            ConfirmActionCard(
                title = when {
                    simulationWeeks < 0 -> "Move Back ${kotlin.math.abs(simulationWeeks)} Week${if (kotlin.math.abs(simulationWeeks) == 1) "" else "s"}?"
                    simulationWeeks > 0 -> "Simulate $simulationWeeks Week${if (simulationWeeks == 1) "" else "s"}?"
                    else -> "No Week Move?"
                },
                detail = if (simulationWeeks < 0) {
                    "This moves the simulated league view back to Week $targetSimulationWeek and clears later simulated view progress."
                } else if (simulationWeeks == 0) {
                    "No simulated week movement is selected."
                } else {
                    "This moves the simulated league view to Week $targetSimulationWeek."
                },
                confirmText = if (simulationWeeks < 0) "Move Back" else "Simulate",
                onCancel = { confirmSimulation = false },
                onConfirm = {
                    confirmSimulation = false
                    onSimulateWeeks(simulationWeeks)
                }
            )
        }
        if (confirmRosterLockChange) {
            ConfirmActionCard(
                title = if (rostersLocked) "Force Unlock Rosters?" else "Force Lock Rosters?",
                detail = if (!rostersLocked) {
                    "Roster active-slot moves will be blocked immediately for testing."
                } else {
                    "This temporarily allows active roster moves for testing in this league."
                },
                confirmText = if (rostersLocked) "Unlock" else "Lock",
                onCancel = { confirmRosterLockChange = false },
                onConfirm = {
                    confirmRosterLockChange = false
                    if (rostersLocked) {
                        onForceRosterUnlockedChange(true)
                        onForceRosterLockedChange(false)
                    } else {
                        onForceRosterLockedChange(true)
                        onForceRosterUnlockedChange(false)
                    }
                }
            )
        }
        if (confirmAddBots) {
            ConfirmActionCard(
                title = "Add $botCountToAdd Bot${if (botCountToAdd == 1) "" else "s"}?",
                detail = if (league.draftStatus == DraftStatus.Complete) {
                    "Each bot will join as a member and receive a smart-filled roster from available artists."
                } else {
                    "Each bot will join as an autopick member for the draft."
                },
                confirmText = "Add Bots",
                onCancel = { confirmAddBots = false },
                onConfirm = {
                    confirmAddBots = false
                    onAddBotMembers(botCountToAdd.coerceIn(1, availableBotSlots))
                    botCountToAdd = 0
                }
            )
        }
        if (confirmForceManager) {
            ConfirmActionCard(
                title = "Force Manager Access?",
                detail = "Developer mode will make your local account the manager and demote the previous local manager without notifying them.",
                confirmText = "Make Me Manager",
                onCancel = { confirmForceManager = false },
                onConfirm = {
                    confirmForceManager = false
                    onForceMakeManager()
                }
            )
        }
        pendingKick?.let { member ->
            ConfirmActionCard(
                title = "Remove Member?",
                detail = "${member.username} will lose access to this league.",
                confirmText = "Remove",
                onCancel = { pendingKick = null },
                onConfirm = {
                    onKickMember(member.username)
                    pendingKick = null
                }
            )
        }
        selectedMember?.let { member ->
            MemberDetailDialog(
                member = member,
                currentUsername = accountUsername,
                canManage = league.isManager && mergedMembers.isNotEmpty() && !member.isManager &&
                    !member.username.equals(accountUsername, ignoreCase = true) &&
                    !member.username.isReservedBotUsername(),
                canViewRoster = !member.username.equals(accountUsername, ignoreCase = true),
                onDismiss = { selectedMember = null },
                onViewRoster = {
                    selectedMember = null
                    rosterMember = member
                },
                onTrade = {
                    selectedMember = null
                    onTradeWithMember(member)
                },
                onTransfer = {
                    selectedMember = null
                    pendingTransfer = member
                },
                onKick = {
                    selectedMember = null
                    pendingKick = member
                }
            )
        }
        rosterMember?.let { member ->
            MemberRosterDialog(
                member = member,
                picks = draftPicks.filter { it.pickedBy.equals(member.username, ignoreCase = true) },
                onDismiss = { rosterMember = null },
                onArtistSelected = {
                    rosterMember = null
                    onMemberRosterArtistSelected(member, it)
                }
            )
        }
        if (confirmLeave) {
            ConfirmActionCard(
                title = leaveConfirmTitle,
                detail = leaveConfirmDetail,
                confirmText = if (leaveActionText == "Delete League") "Delete" else "Leave",
                onCancel = { confirmLeave = false },
                onConfirm = {
                    confirmLeave = false
                    onLeaveLeague()
                }
            )
        }
        if (league.isManager) {
            BreakoutCard {
                Text("Danger Zone", style = MaterialTheme.typography.titleLarge)
                Text(
                    "Deleting a league removes it for every member and clears its draft, roster, and matchup data.",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                DangerButton(text = "Delete League", onClick = { confirmDeleteLeague = true })
            }
        }
        if (confirmDeleteLeague) {
            ConfirmActionCard(
                title = "Delete League?",
                detail = "This permanently deletes the league for every member.",
                confirmText = "Delete",
                onCancel = { confirmDeleteLeague = false },
                onConfirm = {
                    confirmDeleteLeague = false
                    onDeleteLeague()
                }
            )
        }
        DangerButton(text = leaveActionText, onClick = { confirmLeave = true })
    }
}