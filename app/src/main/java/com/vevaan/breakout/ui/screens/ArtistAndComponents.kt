package com.vevaan.breakout

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.vevaan.breakout.core.designsystem.BreakoutDimensions
import com.vevaan.breakout.ui.theme.BreakoutCoral
import com.vevaan.breakout.ui.theme.BreakoutOutline
import com.vevaan.breakout.ui.theme.BreakoutPrimary
import com.vevaan.breakout.ui.theme.BreakoutSecondary
import com.vevaan.breakout.ui.theme.BreakoutSurface
import com.vevaan.breakout.ui.theme.BreakoutSurfaceVariant
import com.vevaan.breakout.ui.theme.BreakoutTextPrimary
import com.vevaan.breakout.ui.theme.BreakoutTextSecondary
import com.vevaan.breakout.ui.theme.BreakoutTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.DayOfWeek
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.ChronoUnit
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.random.Random

@Composable
internal fun ArtistDetailScreen(
    artist: ArtistUi,
    isInRoster: Boolean,
    draftStatus: DraftStatus,
    weeklyPoints: List<ArtistWeekPointsUi> = emptyList(),
    refreshing: Boolean = false,
    draftedStatusLabel: String?,
    draftedHistoryLabel: String?,
    droppedAtMillis: Long?,
    waiveredAtMillis: Long? = null,
    waiveredHistoryLabel: String? = null,
    isWaiverQueued: Boolean,
    canAddToRoster: Boolean,
    canQueueWaiver: Boolean,
    resolvedAction: ResolvedArtistAction? = null,
    waiverUnavailableDetail: String? = null,
    waiverReplacementOptions: List<Pair<RosterSlot, ArtistUi>> = emptyList(),
    onAddToRoster: () -> Unit,
    onRemoveFromRoster: () -> Unit,
    onQueueWaiver: (RosterSlot?) -> Unit,
    onCancelWaiver: () -> Unit,
    onDraftPick: () -> Unit,
    onRefresh: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var detailArtist by remember(artist.name) { mutableStateOf(artist) }
    var detailReady by remember(artist.name) { mutableStateOf(false) }
    var detailContentVisible by remember(artist.name) { mutableStateOf(false) }
    var confirmRemove by remember { mutableStateOf(false) }
    var chooseWaiverDrop by remember { mutableStateOf(false) }
    LaunchedEffect(artist.name) {
        detailReady = false
        val enrichedArtist = MusicArtistService.enrichSignals(artist)
        prefetchArtistImages(context, listOf(enrichedArtist))
        detailArtist = enrichedArtist
        detailReady = true
    }
    LaunchedEffect(detailReady) {
        if (detailReady) {
            detailContentVisible = false
            delay(40)
            detailContentVisible = true
        } else {
            detailContentVisible = false
        }
    }
    val shownArtist = detailArtist
    val marketScoreArtist = artist
    val action = resolvedAction
    val effectiveWaiverQueued = action?.waiverQueued ?: isWaiverQueued
    val effectiveCanQueueWaiver = action?.kind == ArtistActionKind.QueueWaiver || (action == null && canQueueWaiver)
    val effectiveWaiverDetail = action?.unavailableDetail ?: waiverUnavailableDetail
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = !detailReady,
            enter = fadeIn(animationSpec = tween(220)) + slideInVertically(animationSpec = tween(220)) { it / 16 },
            exit = slideOutVertically(animationSpec = tween(420)) { -it }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
            ArtistLoadingScreen()
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(horizontal = BreakoutDimensions.ScreenHorizontalPadding, vertical = BreakoutDimensions.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OverlayBackButton(onClick = onBack)
            }
        }
        }
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = detailReady && detailContentVisible,
            enter = fadeIn(animationSpec = tween(260)) + slideInHorizontally(animationSpec = tween(260)) { it / 8 },
            exit = fadeOut(animationSpec = tween(190)) + slideOutHorizontally(animationSpec = tween(190)) { it / 10 }
        ) {
            ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
                Box(modifier = Modifier.height(44.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ArtistArtwork(artist = shownArtist, size = 232.dp)
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
                ) {
                    Text(
                        shownArtist.displayName(),
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        shownArtist.artistDetailSubtitle(),
                        color = BreakoutTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                ArtistHeroMetricGrid(shownArtist = shownArtist, marketScoreArtist = marketScoreArtist)
            AnimatedVisibility(
                visible = isInRoster || effectiveWaiverQueued,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ArtistOwnershipBanner(
                    isInRoster = isInRoster,
                    isWaiverQueued = effectiveWaiverQueued
                )
            }
            AnimatedVisibility(
                visible = !isInRoster && !effectiveWaiverQueued && draftStatus == DraftStatus.Complete && !effectiveCanQueueWaiver,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ArtistAvailabilityBanner(
                    detail = effectiveWaiverDetail ?: if (draftedStatusLabel != null) {
                        "This artist is already on a roster."
                    } else {
                        "This artist cannot fit an open roster slot right now."
                    }
                )
            }
            ArtistMarketIntelCard(shownArtist = shownArtist, marketScoreArtist = marketScoreArtist)
            BreakoutCard {
                Text("Draft Profile", style = MaterialTheme.typography.titleLarge)
                ScoreLine("Role", shownArtist.tag)
                ScoreLine("Market Value", marketScoreArtist.price)
                shownArtist.albumCount?.let { ScoreLine("Catalog", "$it releases") }
                ScoreLine("Risk", shownArtist.riskLabel)
            }
            if (weeklyPoints.isNotEmpty()) {
                ArtistWeeklyPointsCard(weeklyPoints)
            }
            ChartSignalsCard(shownArtist)
            StreamSplitCard(shownArtist)
            shownArtist.latestReleaseTitle?.let { releaseTitle ->
                LatestReleaseCard(
                    title = releaseTitle,
                    date = shownArtist.latestReleaseDate,
                    type = shownArtist.latestReleaseType,
                    imageUrl = shownArtist.latestReleaseImageUrl
                )
            }
            if (draftedHistoryLabel != null || droppedAtMillis != null || waiveredAtMillis != null) {
                ArtistHistoryTimelineCard(
                    draftedDetail = draftedHistoryLabel,
                    droppedAtMillis = droppedAtMillis,
                    waiveredAtMillis = waiveredAtMillis,
                    waiveredDetail = waiveredHistoryLabel
                )
            }
            if (!canAddToRoster && draftStatus == DraftStatus.Live) {
                StatusCard(
                    title = "Roster Move Locked",
                    detail = "This artist is not available for the current pick."
                )
            }
        }
        }
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = BreakoutDimensions.ScreenHorizontalPadding, vertical = BreakoutDimensions.xs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OverlayBackButton(onClick = onBack)
            TagLabel(shownArtist.tag)
        }
        val showWaiverAction = draftStatus == DraftStatus.Complete && draftedStatusLabel == null && !isInRoster
        if ((canAddToRoster || effectiveCanQueueWaiver || effectiveWaiverQueued || isInRoster || showWaiverAction) && (draftedStatusLabel == null || isInRoster || effectiveCanQueueWaiver || showWaiverAction)) {
            RosterToggleButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = BreakoutDimensions.lg, bottom = BreakoutDimensions.lg),
                added = isInRoster,
                waiver = effectiveCanQueueWaiver || effectiveWaiverQueued || action?.kind == ArtistActionKind.WaiverQueueFull,
                waiverCancel = effectiveWaiverQueued,
                size = 64.dp,
                solid = false,
                onClick = when {
                    isInRoster -> ({ confirmRemove = true })
                    effectiveWaiverQueued -> onCancelWaiver
                    effectiveCanQueueWaiver -> ({
                        if (waiverReplacementOptions.isNotEmpty()) {
                            chooseWaiverDrop = true
                        } else {
                            onQueueWaiver(null)
                        }
                    })
                    showWaiverAction -> ({
                        Toast.makeText(context, effectiveWaiverDetail ?: "Waiver is unavailable right now.", Toast.LENGTH_SHORT).show()
                    })
                    else -> onDraftPick
                }
            )
        }
        if (chooseWaiverDrop) {
            WaiverDropSlotDialog(
                artist = shownArtist,
                options = waiverReplacementOptions,
                onDismiss = { chooseWaiverDrop = false },
                onChoose = { dropSlot ->
                    chooseWaiverDrop = false
                    onQueueWaiver(dropSlot)
                }
            )
        }
        if (confirmRemove) {
            val losesLegacyEligibility = shownArtist.isGrandfatheredFor()
            ConfirmActionCard(
                title = if (losesLegacyEligibility) "Lose Legacy Eligibility?" else "Drop ${shownArtist.displayName()}?",
                detail = if (losesLegacyEligibility) {
                    "${shownArtist.displayName()} is now a ${shownArtist.currentRoleLabel()} but remains ${shownArtist.acquiredRole} Eligible because they were acquired in that role.\n\nIf you drop them, this eligibility will be permanently lost. If you add them again later, they will use their current role."
                } else {
                    "This removes the artist from your roster."
                },
                confirmText = if (losesLegacyEligibility) "Drop Anyway" else "Drop",
                onCancel = { confirmRemove = false },
                onConfirm = {
                    confirmRemove = false
                    onRemoveFromRoster()
                }
            )
        }
    }
}

@Composable
private fun ArtistHeroMetricGrid(shownArtist: ArtistUi, marketScoreArtist: ArtistUi) {
    val snapshotLabel = shownArtist.snapshotDate?.displaySnapshotDate()?.let { "Week ending $it" } ?: "Completed snapshot"
    val gainLabel = shownArtist.snapshotDate?.displaySnapshotDate()?.let { "Since $it" } ?: "Since snapshot"
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
        ) {
            StatTile(
                "Listeners",
                shownArtist.listeners?.formatCompact() ?: "Sizing Up",
                shownArtist.currentAudienceDetail(),
                Modifier.weight(1f)
            )
            StatTile(
                "Breakout",
                marketScoreArtist.breakoutScore(null).formatScore(),
                "Market rating",
                Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
        ) {
            StatTile(
                "Listener Growth",
                shownArtist.weeklyListenerGrowthPercent?.formatSignedPercent() ?: "Pending",
                "Last full week",
                Modifier.weight(1f)
            )
            StatTile(
                "Weekly Gain",
                shownArtist.displayWeeklyListenerGain()?.takeIf { kotlin.math.abs(it) > 100L }?.formatSignedCompact() ?: "Pending",
                snapshotLabel,
                Modifier.weight(1f)
            )
        }
        if (shownArtist.listenerChangeSinceSnapshot != null || shownArtist.listenerChangeSinceSnapshotPercent != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
            ) {
                StatTile(
                    "Since Snapshot",
                    shownArtist.listenerChangeSinceSnapshotPercent?.formatSignedPercent()
                        ?: shownArtist.listenerChangeSinceSnapshot?.formatSignedCompact()
                        ?: "Pending",
                    gainLabel,
                    Modifier.weight(1f)
                )
                StatTile(
                    "Snapshot Age",
                    shownArtist.daysSinceSnapshot?.let { "$it days" } ?: "Unknown",
                    shownArtist.snapshotDate?.displaySnapshotDate() ?: "No snapshot date",
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ArtistMarketIntelCard(shownArtist: ArtistUi, marketScoreArtist: ArtistUi) {
    val breakout = marketScoreArtist.breakoutScore(null)
    val fantasyPreview = marketScoreArtist.leagueWeekScore(1)
    val audienceSignal = shownArtist.listeners
        ?.let { normalizedAudienceFloor(it) * 100.0 }
        ?: 0.0
    val growthSignal = shownArtist.weeklyListenerGrowthPercent
        ?.let { (45.0 + it.coerceIn(-25.0, 175.0) * 0.26).coerceIn(0.0, 100.0) }
        ?: 0.0
    BreakoutCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Fantasy Read", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                TagLabel(shownArtist.riskLabel)
            }
        }
        ArtistSignalBar("Breakout", breakout, BreakoutPrimary)
        ArtistSignalBar("Weekly Points", fantasyPreview, BreakoutSecondary)
        ArtistSignalBar("Audience Floor", audienceSignal, BreakoutCoral)
        if (shownArtist.weeklyListenerGrowthPercent != null || shownArtist.weeklyListenerGain != null) {
            ArtistSignalBar("Growth Spike", growthSignal, BreakoutPrimary)
        }
        shownArtist.releaseRecencyScore?.let { ArtistSignalBar("Release Recency", it, BreakoutSecondary) }
        shownArtist.topCityName?.let { city ->
            ScoreLine(
                "Top City",
                listOfNotNull(city, shownArtist.topCityListenersLabel).joinToString(" - ")
            )
        }
        if (shownArtist.isGrandfatheredFor()) {
            ScoreLine("Current Role", shownArtist.currentRoleLabel())
            ScoreLine("Roster Eligibility", "${shownArtist.acquiredRole} Eligible")
            Text(
                "Legacy Eligibility",
                color = WaiverAccent,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "This artist was acquired as a ${shownArtist.acquiredRole} and remains eligible for that role while continuously rostered.",
                color = BreakoutTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        ScoreLine("Fantasy Read", shownArtist.marketNote)
    }
}

@Composable
private fun ArtistSignalBar(label: String, value: Double, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
            Text(value.formatScore(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(BreakoutSurfaceVariant.copy(alpha = 0.72f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((value.coerceIn(0.0, 100.0) / 100.0).toFloat())
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

internal fun Double.formatSignedPercent(): String =
    if (this >= 0.0) "+${"%.1f".format(this)}%" else "${"%.1f".format(this)}%"

@Composable
internal fun ArtistWeeklyPointsCard(weeklyPoints: List<ArtistWeekPointsUi>) {
    BreakoutCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("League Points", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text("Weekly scorecard", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
            val scoredWeeks = weeklyPoints.count { it.points != null }
            Text(
                "$scoredWeeks/${weeklyPoints.size}",
                color = BreakoutPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
            weeklyPoints.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
                ) {
                    row.forEach { week ->
                        WeeklyPointTile(week = week, modifier = Modifier.weight(1f))
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun WeeklyPointTile(week: ArtistWeekPointsUi, modifier: Modifier = Modifier) {
    val borderColor = if (week.isCurrent) BreakoutPrimary.copy(alpha = 0.58f) else BreakoutOutline.copy(alpha = 0.32f)
    val backgroundColor = if (week.isCurrent) BreakoutPrimary.copy(alpha = 0.12f) else BreakoutSurfaceVariant.copy(alpha = 0.64f)
    Surface(
        modifier = modifier.height(72.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = BreakoutDimensions.md, vertical = BreakoutDimensions.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Week ${week.week}", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelMedium)
                if (week.isCurrent) {
                    Text("Current", color = BreakoutPrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                week.points?.formatPoints() ?: "--",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
internal fun WaiverDropSlotDialog(
    artist: ArtistUi,
    options: List<Pair<RosterSlot, ArtistUi>>,
    onDismiss: () -> Unit,
    onChoose: (RosterSlot) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.94f),
            color = BreakoutSurface.copy(alpha = 0.98f),
            shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
            border = BorderStroke(1.dp, WaiverAccent.copy(alpha = 0.42f)),
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BreakoutDimensions.xl),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(WaiverAccent.copy(alpha = 0.16f))
                            .border(1.dp, WaiverAccent.copy(alpha = 0.48f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("W", color = WaiverAccent, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Replacement",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("Claim ${artist.displayName()}", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Surface(
                    color = WaiverAccent.copy(alpha = 0.10f),
                    shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                    border = BorderStroke(1.dp, WaiverAccent.copy(alpha = 0.30f))
                ) {
                    Text(
                        text = "The replacement is only dropped if your waiver claim is processed. If a higher-priority claim wins that artist first, your claim is rejected and your roster stays unchanged.",
                        modifier = Modifier.padding(BreakoutDimensions.md),
                        color = BreakoutTextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                options.forEach { (slot, currentArtist) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                            .clickable { onChoose(slot) },
                        color = BreakoutSurfaceVariant.copy(alpha = 0.82f),
                        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                        border = BorderStroke(1.dp, WaiverAccent.copy(alpha = 0.22f))
                    ) {
                        Row(
                            modifier = Modifier.padding(BreakoutDimensions.md),
                            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ArtistArtwork(artist = currentArtist, size = BreakoutDimensions.ArtworkList)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(currentArtist.displayName(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(currentArtist.tag, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Text(
                                "Replace",
                                color = WaiverAccent,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                SecondaryButton(text = "Close", modifier = Modifier.fillMaxWidth(), onClick = onDismiss)
            }
        }
    }
}

@Composable
internal fun LeagueDrawer(
    leagues: List<LeagueUi>,
    activeLeague: LeagueUi?,
    drawerOpen: Boolean,
    draftLeagueName: String,
    draftInviteCode: String,
    onLeagueNameChange: (String) -> Unit,
    onInviteCodeChange: (String) -> Unit,
    joinError: String?,
    pendingTradeCount: Int = 0,
    onSwitchLeague: (LeagueUi) -> Unit,
    onCreateLeague: () -> Unit,
    onJoinLeague: () -> Unit,
    onLeaveLeague: () -> Unit,
    onNavigate: (BreakoutTab) -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val drawerScrollState = rememberScrollState()
    var confirmLeave by rememberSaveable(activeLeague?.inviteCode) { mutableStateOf(false) }
    var leagueActionMode by rememberSaveable { mutableStateOf<String?>(null) }
    val leaveActionText = when {
        activeLeague == null -> "Leave Current League"
        activeLeague.isManager && activeLeague.memberCount <= 1 -> "Delete League"
        else -> "Leave Current League"
    }
    val leaveConfirmTitle = if (leaveActionText == "Delete League") "Delete League?" else "Leave League?"
    val leaveConfirmDetail = if (leaveActionText == "Delete League") {
        "This league has no other members, so leaving will permanently delete it."
    } else if (activeLeague?.isManager == true) {
        "You will leave this league and manager access will move to another member."
    } else {
        "You will lose access to this league."
    }

    LaunchedEffect(drawerOpen) {
        drawerScrollState.scrollTo(0)
    }

    ModalDrawerSheet(
        drawerContainerColor = Color(0xFF10131B),
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 304.dp, max = 360.dp)
                .statusBarsPadding()
                .imePadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .verticalScroll(drawerScrollState)
                .padding(BreakoutDimensions.CardPadding),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)
        ) {
            BreakoutCard(
                contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding),
                border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.42f))
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md), verticalAlignment = Alignment.CenterVertically) {
                    BreakoutMark(size = 58.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Breakout", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Text(
                            activeLeague?.name ?: "Create or join a league",
                            color = BreakoutTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (activeLeague != null) {
                InviteCodeCard(
                    inviteCode = activeLeague.inviteCode,
                    onCopy = { clipboard.setText(AnnotatedString(activeLeague.inviteCode)) }
                )
                BreakoutCard {
                    Text("Navigate", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    DrawerNavRow("League Settings", BreakoutTab.League, onNavigate)
                    DrawerNavRow("Trades", BreakoutTab.Trades, onNavigate, badgeCount = pendingTradeCount)
                    DrawerNavRow("All Matchups", BreakoutTab.AllMatchups, onNavigate)
                    if (activeLeague.draftStatus == DraftStatus.Complete) {
                        DrawerNavRow("Draft Summary", BreakoutTab.DraftSummary, onNavigate)
                    }
                    DrawerNavRow("Standings", BreakoutTab.Standings, onNavigate)
                    DrawerNavRow("Account", BreakoutTab.Account, onNavigate)
                }
            }
            if (activeLeague == null) {
                BreakoutCard {
                    Text("Navigate", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    DrawerNavRow("Account", BreakoutTab.Account, onNavigate)
                }
            }
            BreakoutCard {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Leagues", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${leagues.size}/$MaxJoinedLeagues joined", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(BreakoutPrimary.copy(alpha = 0.16f))
                            .border(1.dp, BreakoutPrimary.copy(alpha = 0.42f), CircleShape)
                            .clickable { leagueActionMode = "choice" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = BreakoutPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    }
                }
                if (leagues.isEmpty()) {
                    Text("Create a league to start drafting.", color = BreakoutTextSecondary)
                } else {
                    leagues.forEach { league ->
                        LeagueSwitcherRow(
                            league = league,
                            selected = league.inviteCode == activeLeague?.inviteCode,
                            onClick = { onSwitchLeague(league) }
                        )
                    }
                }
            }
        }
    }
    if (leagueActionMode == "choice") {
        LeagueChoiceDialog(
            onDismiss = { leagueActionMode = null },
            onCreate = { leagueActionMode = "create" },
            onJoin = { leagueActionMode = "join" }
        )
    }
    if (leagueActionMode == "create") {
        LeagueActionDialog(
            title = "Create League",
            inputLabel = "League Name",
            value = draftLeagueName,
            onValueChange = onLeagueNameChange,
            actionText = "Create League",
            enabled = draftLeagueName.isNotBlank() && leagues.size < MaxJoinedLeagues,
            error = joinError,
            onDismiss = { leagueActionMode = null },
            onAction = onCreateLeague
        )
    } else if (leagueActionMode == "join") {
        LeagueActionDialog(
            title = "Join League",
            inputLabel = "Invite Code",
            value = draftInviteCode,
            onValueChange = { value -> onInviteCodeChange(value.uppercase().filter { it.isLetterOrDigit() }.take(6)) },
            actionText = "Join League",
            enabled = isValidInviteCode(draftInviteCode) && leagues.size < MaxJoinedLeagues,
            error = joinError,
            onDismiss = { leagueActionMode = null },
            onAction = onJoinLeague
        )
    }
}

@Composable
internal fun LeagueChoiceDialog(
    onDismiss: () -> Unit,
    onCreate: () -> Unit,
    onJoin: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = BreakoutDimensions.lg)
                .fillMaxWidth()
                .widthIn(max = 430.dp),
            color = Color(0xFF141824),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.46f)),
            tonalElevation = 14.dp
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                BreakoutPrimary.copy(alpha = 0.12f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(BreakoutDimensions.xl),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md), verticalAlignment = Alignment.CenterVertically) {
                    BreakoutMark(size = 54.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Add League", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        Text("Start fresh or enter an invite code.", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                DrawerActionChoice(
                    label = "Create new league",
                    detail = "Name it, invite friends, then set draft rules.",
                    accent = BreakoutPrimary,
                    onClick = onCreate
                )
                DrawerActionChoice(
                    label = "Join existing league",
                    detail = "Use a 6-character invite code.",
                    accent = WaiverAccent,
                    onClick = onJoin
                )
                SecondaryButton("Cancel", modifier = Modifier.fillMaxWidth(), onClick = onDismiss)
            }
        }
    }
}

@Composable
internal fun DrawerActionChoice(label: String, detail: String, accent: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.CardCornerRadius))
            .clickable(onClick = onClick),
        color = BreakoutSurfaceVariant.copy(alpha = 0.66f),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.36f))
    ) {
        Row(
            modifier = Modifier.padding(BreakoutDimensions.md),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f))
                    .border(1.dp, accent.copy(alpha = 0.52f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("+", color = accent, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(label, color = BreakoutTextPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
                Text(detail, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Text(">", color = accent, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
internal fun LeagueActionDialog(
    title: String,
    inputLabel: String,
    value: String,
    onValueChange: (String) -> Unit,
    actionText: String,
    enabled: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onAction: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = BreakoutDimensions.lg)
                .fillMaxWidth()
                .widthIn(max = 430.dp)
                .imePadding(),
            color = Color(0xFF141824),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.46f)),
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                BreakoutPrimary.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
                    .padding(BreakoutDimensions.xl),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(BreakoutPrimary.copy(alpha = 0.16f))
                            .border(1.dp, BreakoutPrimary.copy(alpha = 0.48f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (inputLabel == "League Name") "+" else "#", color = BreakoutPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                        Text(
                            if (inputLabel == "League Name") "Create a new draft room." else "Enter your league invite code.",
                            color = BreakoutTextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                StyledTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = inputLabel,
                    maxLength = if (inputLabel == "League Name") MaxLeagueNameLength else 6,
                    keyboardActions = KeyboardActions(onDone = { if (enabled) onAction() })
                )
                AnimatedFeedbackText(message = cleanVisibleError(error), color = BreakoutCoral)
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
                    SecondaryButton("Cancel", modifier = Modifier.weight(1f), onClick = onDismiss)
                    PrimaryButton(
                        text = actionText,
                        modifier = Modifier.weight(1f),
                        enabled = enabled,
                        onClick = onAction
                    )
                }
            }
        }
    }
}

@Composable
internal fun DrawerNavRow(label: String, tab: BreakoutTab, onNavigate: (BreakoutTab) -> Unit, badgeCount: Int = 0) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(BreakoutSurfaceVariant.copy(alpha = 0.42f))
            .border(1.dp, BreakoutOutline.copy(alpha = 0.24f), RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .clickable { onNavigate(tab) }
            .padding(BreakoutDimensions.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs), verticalAlignment = Alignment.CenterVertically) {
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(BreakoutCoral),
                    contentAlignment = Alignment.Center
                ) {
                    Text(badgeCount.coerceAtMost(99).toString(), color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                }
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(BreakoutPrimary.copy(alpha = 0.14f))
                    .padding(horizontal = BreakoutDimensions.sm, vertical = BreakoutDimensions.xs),
                contentAlignment = Alignment.Center
            ) {
                Text(tab.mark, color = BreakoutPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun TopTitle(title: String, subtitle: String, onMenuClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onMenuClick != null) {
            MenuButton(onClick = onMenuClick)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xxs)
        ) {
            Text(title, style = MaterialTheme.typography.headlineLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
internal fun MenuButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(BreakoutDimensions.MinimumTouchTarget)
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(BreakoutSurfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(BreakoutPrimary)
                )
            }
        }
    }
}

@Composable
internal fun BreakoutMark(size: Dp) {
    Image(
        painter = painterResource(id = R.drawable.breakout_app_icon_small),
        contentDescription = "Breakout",
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(22.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
internal fun InviteCodeCard(inviteCode: String, onCopy: () -> Unit) {
    BreakoutCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Invite Friends", style = MaterialTheme.typography.titleLarge)
                Text(inviteCode, color = BreakoutSecondary, style = MaterialTheme.typography.headlineLarge)
            }
            SecondaryButton(text = "Copy", onClick = onCopy)
        }
    }
}

@Composable
internal fun LeagueSwitcherRow(
    league: LeagueUi,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(if (selected) BreakoutPrimary.copy(alpha = 0.18f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(BreakoutDimensions.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(league.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${league.inviteCode} - ${league.memberCount} members", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        if (selected) {
            Pill("Active")
        }
    }
}

@Composable
internal fun BreakoutCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(BreakoutDimensions.CardPadding),
    border: BorderStroke = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.55f)),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = BreakoutSurface),
        border = border
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CompactContentSpacing),
            content = content
        )
    }
}

@Composable
internal fun RecommendedPickButton(
    artist: ArtistUi,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = BreakoutPrimary.copy(alpha = 0.14f)),
        border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = BreakoutDimensions.CardPadding, vertical = BreakoutDimensions.md),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Recommended Pick", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "${artist.displayName()} - ${artist.tag} - Best fit right now",
                        color = BreakoutTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(">", color = BreakoutPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun MarketHeroHeader(
    title: String,
    subtitle: String,
    rosterCount: Int,
    draftPickMode: Boolean,
    onMenuClick: () -> Unit
) {
    BreakoutCard(
        contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding),
        border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.38f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.Top
        ) {
            MenuButton(onClick = onMenuClick)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)) {
                Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text(subtitle, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
            MiniSignalPill(if (draftPickMode) "Live Board" else "Market Board", BreakoutPrimary)
            MiniSignalPill("$rosterCount Rostered", BreakoutSecondary)
        }
    }
}

@Composable
internal fun MiniSignalPill(text: String, accent: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(accent.copy(alpha = 0.14f))
            .border(1.dp, accent.copy(alpha = 0.34f), RoundedCornerShape(999.dp))
            .padding(horizontal = BreakoutDimensions.md, vertical = BreakoutDimensions.xs),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = accent, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
internal fun ArtistRow(
    modifier: Modifier = Modifier,
    artist: ArtistUi,
    isInRoster: Boolean = false,
    isDrafted: Boolean = false,
    isDraftedByOther: Boolean = false,
    isWaiverQueued: Boolean = false,
    waiverQueuePosition: Int? = null,
    showRoleTag: Boolean = false,
    canToggleRoster: Boolean = true,
    waiverAction: Boolean = false,
    waiverCancelAction: Boolean = false,
    statusLabel: String? = null,
    canRevealActions: Boolean = false,
    actionsOpen: Boolean = false,
    onActionsOpenChange: (Boolean) -> Unit = {},
    onClick: () -> Unit,
    onToggleRoster: (() -> Unit)? = null
) {
    var isDraggingActions by remember { mutableStateOf(false) }
    var dragOffsetPx by remember { mutableStateOf(0f) }
    val actionButtonSize = 64.dp
    // Artist card swipe action spacing: button size, side gap, reveal distance, and elastic threshold.
    val actionGutter = BreakoutDimensions.xxs
    val hasPrimaryAction = onToggleRoster != null
    val revealWidth = actionButtonSize + actionGutter * 2
    val density = LocalDensity.current
    val revealPx = with(density) { revealWidth.toPx() }
    val maxSwipePx = revealPx * 1.58f
    val openThresholdPx = revealPx * 1.14f
    val closeThresholdPx = revealPx * 0.34f
    val targetOffset = when {
        !canRevealActions -> 0f
        isDraggingActions -> dragOffsetPx
        actionsOpen && hasPrimaryAction -> -openThresholdPx
        else -> 0f
    }
    val cardOffset by animateFloatAsState(targetValue = targetOffset, label = "artistActionReveal")
    val stateAccentTarget = when {
        isWaiverQueued -> WaiverAccent
        isInRoster -> BreakoutPrimary
        isDraftedByOther -> DraftedOtherAccent
        else -> BreakoutOutline.copy(alpha = 0.62f)
    }
    val stateAccent by animateColorAsState(
        targetValue = stateAccentTarget,
        animationSpec = tween(durationMillis = 260),
        label = "artistRowStateAccent"
    )
    val stateBorder by animateColorAsState(
        targetValue = when {
            isWaiverQueued -> WaiverAccent.copy(alpha = 0.9f)
            isInRoster -> BreakoutPrimary.copy(alpha = 0.78f)
            isDraftedByOther -> DraftedOtherAccent.copy(alpha = 0.78f)
            else -> BreakoutOutline.copy(alpha = 0.55f)
        },
        animationSpec = tween(durationMillis = 260),
        label = "artistRowStateBorder"
    )
    val statusTextColor by animateColorAsState(
        targetValue = when {
            isInRoster -> BreakoutPrimary
            isDraftedByOther -> DraftedOtherAccent
            isWaiverQueued -> WaiverAccent
            else -> BreakoutTextSecondary
        },
        animationSpec = tween(durationMillis = 260),
        label = "artistRowStatusColor"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 108.dp)
    ) {
        if (canRevealActions) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(revealWidth)
                    .heightIn(min = 98.dp)
                    .padding(horizontal = actionGutter)
                    .graphicsLayer { translationX = cardOffset * 0.06f },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasPrimaryAction) {
                    RosterToggleButton(
                        added = isInRoster,
                        drafted = false,
                        waiver = waiverAction || waiverCancelAction,
                        waiverCancel = waiverCancelAction,
                        enabled = canToggleRoster,
                        size = actionButtonSize,
                        onClick = {
                            onActionsOpenChange(false)
                            onToggleRoster?.invoke()
                        }
                    )
                }
            }
        }
        BreakoutCard(
            modifier = Modifier
                .offset { IntOffset(cardOffset.roundToInt(), 0) }
                .clickable(onClick = onClick)
                .pointerInput(canRevealActions, revealPx, actionsOpen, hasPrimaryAction) {
                    if (!canRevealActions) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDraggingActions = true
                            dragOffsetPx = if (actionsOpen) -revealPx else 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragOffsetPx = (dragOffsetPx + dragAmount).coerceIn(-maxSwipePx, 0f)
                        },
                        onDragEnd = {
                            val shouldStayOpen = if (!hasPrimaryAction) {
                                false
                            } else if (actionsOpen) {
                                dragOffsetPx <= -closeThresholdPx
                            } else {
                                dragOffsetPx <= -openThresholdPx
                            }
                            onActionsOpenChange(shouldStayOpen)
                            isDraggingActions = false
                            dragOffsetPx = 0f
                        },
                        onDragCancel = {
                            isDraggingActions = false
                            dragOffsetPx = 0f
                        }
                    )
                },
        border = BorderStroke(1.dp, stateBorder)
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(72.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(stateAccent)
            )
            ArtistArtwork(artist = artist, size = BreakoutDimensions.ArtworkCard)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = BreakoutDimensions.ArtworkCard),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    artist.displayName(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    artist.cardAudienceLabel,
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    statusLabel ?: if (isWaiverQueued) "Waiver #${waiverQueuePosition ?: ""}".trim() else artist.marketMomentumLabel(),
                    color = statusTextColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (statusLabel != null || isWaiverQueued) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier.widthIn(min = 84.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
            ) {
                Text(
                    text = artist.price,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${artist.breakoutScore(null).formatScore()} score",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (showRoleTag) {
                    TagLabel(artist.tag)
                }
            }
        }
        }
    }
}

@Composable
internal fun RosterSlotCard(
    modifier: Modifier = Modifier,
    slot: RosterSlot,
    artist: ArtistUi?,
    draftStatus: DraftStatus,
    draftRoomContext: Boolean,
    onArtistSelected: (ArtistUi) -> Unit,
    onOpenMarket: () -> Unit,
    canRemove: Boolean = true,
    canMove: Boolean = false,
    locked: Boolean = false,
    onMoveArtist: () -> Unit = {},
    onRemoveArtist: () -> Unit
) {
    val inLiveDraftRoom = draftRoomContext && draftStatus == DraftStatus.Live
    val emptyTitle = if (inLiveDraftRoom) "Draft artist" else "Scout artist"
    val emptyPill = if (inLiveDraftRoom) "Draft" else "Scout"
    BreakoutCard(
        modifier = modifier.clickable {
            if (artist == null) onOpenMarket() else onArtistSelected(artist)
        },
        contentPadding = PaddingValues(BreakoutDimensions.md),
        border = BorderStroke(
            1.dp,
            when {
                locked -> BreakoutOutline.copy(alpha = 0.42f)
                artist != null -> BreakoutPrimary.copy(alpha = 0.35f)
                else -> BreakoutOutline.copy(alpha = 0.55f)
            }
        )
    ) {
        Box {
            AnimatedContent(
                targetState = artist,
                transitionSpec = {
                    (fadeIn() + slideInHorizontally { -it / 4 }) togetherWith
                        (fadeOut() + slideOutHorizontally { it / 4 })
                },
                label = "rosterSlotSwap"
            ) { shownArtist ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (shownArtist != null) BreakoutPrimary else BreakoutOutline.copy(alpha = 0.55f))
                )
                if (shownArtist != null) {
                    ArtistArtwork(artist = shownArtist, size = 72.dp)
                } else {
                    EmptySlotArtwork()
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 72.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        if (shownArtist?.isGrandfatheredFor(slot) == true) "Now ${shownArtist.currentRoleLabel()}" else slot.label,
                        color = BreakoutSecondary,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1
                    )
                    Text(
                        text = shownArtist?.displayName() ?: emptyTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        if (shownArtist?.isGrandfatheredFor(slot) == true) {
                            "${shownArtist.acquiredRole} Eligible"
                        } else {
                            shownArtist?.audienceLabel ?: slot.hint
                        },
                        color = if (shownArtist?.isGrandfatheredFor(slot) == true) WaiverAccent else BreakoutTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (shownArtist == null) {
                    if (canMove) {
                        RosterActionIcon(text = "Swap", accent = BreakoutPrimary, width = 58.dp, onClick = onMoveArtist)
                    } else if (locked) {
                        Pill("Locked")
                    } else {
                        Pill(emptyPill)
                    }
                } else if (canRemove) {
                    Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs), verticalAlignment = Alignment.CenterVertically) {
                        if (canMove) {
                            RosterActionIcon(text = "Swap", accent = BreakoutPrimary, width = 58.dp, onClick = onMoveArtist)
                        }
                        RosterActionIcon(text = "-", accent = BreakoutCoral, onClick = onRemoveArtist)
                    }
                } else if (locked) {
                    Pill("Locked")
                }
            }
            }
            if (locked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(BreakoutDimensions.CardCornerRadius))
                        .background(Color.Black.copy(alpha = 0.10f))
                )
            }
        }
    }
}

@Composable
internal fun RosterActionIcon(
    text: String,
    accent: Color,
    enabled: Boolean = true,
    size: androidx.compose.ui.unit.Dp = 42.dp,
    width: androidx.compose.ui.unit.Dp = size,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(size)
            .clip(RoundedCornerShape(999.dp))
            .background(
                if (enabled) {
                    accent.copy(alpha = 0.16f)
                } else {
                    BreakoutSurfaceVariant.copy(alpha = 0.4f)
                }
            )
            .border(
                width = 1.dp,
                color = if (enabled) {
                    accent.copy(alpha = 0.42f)
                } else {
                    BreakoutOutline.copy(alpha = 0.35f)
                },
                shape = RoundedCornerShape(999.dp)
            )
            .clickable(
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) {
                accent
            } else {
                BreakoutTextSecondary
            },
            style = when {
                text == "Up" || text == "Dn" -> {
                    MaterialTheme.typography.bodySmall
                }

                text.length > 1 || size < 40.dp -> {
                    MaterialTheme.typography.labelSmall
                }

                else -> {
                    MaterialTheme.typography.titleMedium
                }
            },
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun EmptySlotArtwork() {
    Box(
        modifier = Modifier
            .size(BreakoutDimensions.ArtworkList)
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .border(1.dp, BreakoutOutline, RoundedCornerShape(BreakoutDimensions.SmallCornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        Text("+", color = BreakoutTextSecondary, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
internal fun ArtistArtwork(artist: ArtistUi, size: androidx.compose.ui.unit.Dp) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val imageSizePx = with(density) { size.roundToPx().coerceAtLeast(72) }
    val artworkUrl = if (size <= 96.dp) artist.cardImageUrl else artist.bestImageUrl
    Box(
        modifier = Modifier
            .size(size)
            .clip(if (size > 100.dp) RoundedCornerShape(BreakoutDimensions.HeroCornerRadius) else RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(Brush.linearGradient(listOf(BreakoutPrimary, BreakoutCoral, BreakoutSecondary))),
        contentAlignment = Alignment.Center
    ) {
        if (!artworkUrl.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(artworkUrl)
                    .size(imageSizePx, imageSizePx)
                    .allowRgb565(size <= 96.dp)
                    .crossfade(false)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = "${artist.displayName()} artist image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(artist.initials, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
internal fun FilterChipRow(
    filters: List<MarketFilter>,
    selected: MarketFilter?,
    onSelected: (MarketFilter?) -> Unit,
    searchLabel: String? = null,
    onSearchSelected: (() -> Unit)? = null
) {
    val chipScrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    LaunchedEffect(searchLabel, selected, filters, configuration.screenWidthDp) {
        if (searchLabel != null) {
            chipScrollState.animateScrollTo(0)
        } else if (selected != null) {
            delay(60)
            val selectedIndex = filters.indexOf(selected).coerceAtLeast(0)
            val chipWidthPx = with(density) { 106.dp.roundToPx() }
            val gapPx = with(density) { BreakoutDimensions.sm.roundToPx() }
            val viewportPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }
            val target = (selectedIndex * (chipWidthPx + gapPx)) - ((viewportPx - chipWidthPx) / 2)
            chipScrollState.animateScrollTo(target.coerceIn(0, chipScrollState.maxValue))
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(chipScrollState),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
    ) {
        AnimatedVisibility(
            visible = searchLabel != null,
            enter = fadeIn(animationSpec = tween(180)) + expandHorizontally(animationSpec = tween(220)),
            exit = fadeOut(animationSpec = tween(140)) + shrinkHorizontally(animationSpec = tween(180))
        ) {
            val background by animateColorAsState(
                targetValue = BreakoutPrimary.copy(alpha = 0.30f),
                label = "marketSearchFilterBackground"
            )
            val border by animateColorAsState(
                targetValue = BreakoutPrimary.copy(alpha = 0.72f),
                label = "marketSearchFilterBorder"
            )
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .widthIn(min = 106.dp, max = 180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(background)
                    .border(1.dp, border, RoundedCornerShape(16.dp))
                    .clickable { onSearchSelected?.invoke() }
                    .padding(horizontal = BreakoutDimensions.md),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    searchLabel.orEmpty(),
                    color = BreakoutPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        filters.forEach { filter ->
            val isSelected = filter == selected
            val background by animateColorAsState(
                targetValue = if (isSelected) BreakoutPrimary.copy(alpha = 0.30f) else BreakoutSurface.copy(alpha = 0.78f),
                label = "marketFilterBackground"
            )
            val border by animateColorAsState(
                targetValue = if (isSelected) BreakoutPrimary.copy(alpha = 0.72f) else BreakoutOutline.copy(alpha = 0.65f),
                label = "marketFilterBorder"
            )
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .widthIn(min = 106.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(background)
                    .border(1.dp, border, RoundedCornerShape(16.dp))
                    .clickable { onSelected(filter) }
                    .padding(horizontal = BreakoutDimensions.md),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    filter.label,
                    color = if (isSelected) BreakoutPrimary else BreakoutTextSecondary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
internal fun StatusCard(title: String, detail: String) {
    BreakoutCard {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(detail, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DraftDatePickerDialog(
    selectedDate: String,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val initialMillis = parseDraftDateTime("$selectedDate 12:00")
        ?.toLocalDate()
        ?.atStartOfDay(ZoneOffset.UTC)
        ?.toInstant()
        ?.toEpochMilli()
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val selected = Instant.ofEpochMilli(selectedMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .toString()
                        onDateSelected(selected)
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DraftTimePickerDialog(
    selectedTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val parsed = runCatching {
        LocalDateTime.parse("2026-01-01 ${selectedTime.ifBlank { "19:00" }}", DraftDateInputFormatter).toLocalTime()
    }.getOrNull()
    val timePickerState = rememberTimePickerState(
        initialHour = parsed?.hour ?: 19,
        initialMinute = parsed?.minute ?: 0,
        is24Hour = false
    )
    Dialog(onDismissRequest = onDismiss) {
        BreakoutCard {
            Text("Draft Time", style = MaterialTheme.typography.titleLarge)
            TimePicker(state = timePickerState)
            Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                SecondaryButton(text = "Cancel", modifier = Modifier.weight(1f), onClick = onDismiss)
                PrimaryButton(
                    text = "Done",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onTimeSelected("%02d:%02d".format(timePickerState.hour, timePickerState.minute))
                    }
                )
            }
        }
    }
}

@Composable
internal fun AnimatedFeedbackText(message: String?, color: Color) {
    AnimatedVisibility(
        visible = !message.isNullOrBlank(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Text(message.orEmpty(), color = color, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
internal fun WarningCard(
    title: String,
    messages: List<String>,
    onMessageClick: ((String) -> Unit)? = null
) {
    AlertNoticeCard(
        title = title,
        messages = messages,
        accent = BreakoutCoral,
        symbol = "!",
        onMessageClick = onMessageClick
    )
}

@Composable
internal fun AlertNoticeCard(
    title: String,
    messages: List<String>,
    accent: Color,
    symbol: String,
    onMessageClick: ((String) -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
        colors = CardDefaults.cardColors(containerColor = BreakoutSurfaceVariant.copy(alpha = 0.66f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.42f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(accent.copy(alpha = 0.18f), Color.Transparent)
                    )
                )
                .padding(horizontal = BreakoutDimensions.md, vertical = BreakoutDimensions.sm),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.18f))
                        .border(1.dp, accent.copy(alpha = 0.45f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(symbol, color = accent, fontWeight = FontWeight.Black)
                }
                Text(
                    title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            messages.forEach { message ->
                Text(
                    message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (onMessageClick != null) Modifier.clickable { onMessageClick(message) } else Modifier),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
internal fun ScoreLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = BreakoutTextSecondary, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.End)
    }
}

@Composable
internal fun InfoScoreLine(label: String, value: String, info: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = BreakoutTextSecondary, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(value, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun SignalMetricRow(label: String, value: String, detail: String?, info: List<String>) {
    val numeric = value.toDoubleOrNull()?.coerceIn(0.0, 100.0)
    Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)) {
        InfoScoreLine(label = label, value = value, info = info)
        detail?.takeIf { it.isNotBlank() }?.let {
            Text(
                it,
                color = BreakoutTextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 8.dp)
                .clip(CircleShape)
                .background(BreakoutSurfaceVariant.copy(alpha = 0.72f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((numeric?.toFloat() ?: 0f) / 100f)
                    .heightIn(min = 8.dp)
                    .clip(CircleShape)
                    .background(Brush.horizontalGradient(listOf(BreakoutPrimary, BreakoutSecondary)))
            )
        }
    }
}

@Composable
internal fun SignalInfoDialog(
    title: String,
    details: List<String>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(horizontal = BreakoutDimensions.md)
                .widthIn(max = 640.dp),
            color = BreakoutSurface.copy(alpha = 0.995f),
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.38f)),
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(BreakoutPrimary.copy(alpha = 0.10f), Color.Transparent)
                        )
                    )
                    .padding(BreakoutDimensions.xl),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(BreakoutPrimary.copy(alpha = 0.18f))
                            .border(1.dp, BreakoutPrimary.copy(alpha = 0.45f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("?", color = BreakoutPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("Signal Guide", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelLarge)
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BreakoutSurfaceVariant.copy(alpha = 0.72f))
                            .border(1.dp, BreakoutOutline.copy(alpha = 0.34f), CircleShape)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("x", color = BreakoutTextSecondary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
                ) {
                    details.forEachIndexed { index, detail ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = BreakoutSurfaceVariant.copy(alpha = 0.64f),
                            shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                            border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.28f))
                        ) {
                            Row(
                                modifier = Modifier.padding(BreakoutDimensions.md),
                                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(BreakoutSecondary.copy(alpha = 0.16f))
                                        .border(1.dp, BreakoutSecondary.copy(alpha = 0.42f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text((index + 1).toString(), color = BreakoutSecondary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                                }
                                Text(
                                    detail,
                                    modifier = Modifier.weight(1f),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = BreakoutDimensions.xxs),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .height(46.dp)
                            .widthIn(min = 132.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .clickable(onClick = onDismiss),
                        color = BreakoutSecondary.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(999.dp),
                        border = BorderStroke(1.dp, BreakoutSecondary.copy(alpha = 0.44f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Done", color = BreakoutSecondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

internal fun scoringSignalInfo(label: String): List<String>? = when (label) {
    "Breakout Score" -> listOf(
        "Combines audience movement, track momentum, listener growth, play growth, and release freshness",
        "Runs from 0 to 100",
        "Higher means a stronger breakout profile"
    )
    "Daily Streams" -> listOf(
        "Uses current daily stream pace from chart data when available",
        "Rewards meaningful listening activity without making huge artists unbeatable",
        "Pairs best with weekly listener growth"
    )
    "Track Signal" -> listOf(
        "Measures current song momentum from top-track popularity",
        "Runs from 0 to 100",
        "Higher means stronger recent track activity"
    )
    "Artist Signal" -> listOf(
        "Measures broader artist demand and platform momentum",
        "Runs from 0 to 100",
        "Higher means stronger overall demand"
    )
    "Release Recency" -> listOf(
        "Measures how much a newer release is helping the artist's current profile",
        "Runs from 0 to 100",
        "Higher means fresher release momentum"
    )
    else -> null
}

@Composable
internal fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.take(maxLength)) },
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    scope.launch {
                        delay(260)
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            },
        enabled = enabled,
        singleLine = true,
        label = { Text(label) },
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledTextColor = BreakoutTextSecondary,
            focusedContainerColor = BreakoutSurface.copy(alpha = 0.86f),
            unfocusedContainerColor = BreakoutSurface.copy(alpha = 0.72f),
            disabledContainerColor = BreakoutSurfaceVariant.copy(alpha = 0.46f),
            focusedBorderColor = BreakoutPrimary,
            unfocusedBorderColor = BreakoutOutline.copy(alpha = 0.72f),
            disabledBorderColor = BreakoutOutline.copy(alpha = 0.32f),
            cursorColor = BreakoutPrimary,
            focusedLabelColor = BreakoutPrimary,
            unfocusedLabelColor = BreakoutTextSecondary,
            disabledLabelColor = BreakoutTextSecondary.copy(alpha = 0.55f)
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation
    )
}

@Composable
internal fun StepperRow(
    label: String,
    value: String,
    enabled: Boolean,
    minusEnabled: Boolean,
    plusEnabled: Boolean,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Text(value, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        CompactButton("-", enabled = enabled && minusEnabled, onClick = onMinus)
        CompactButton("+", enabled = enabled && plusEnabled, onClick = onPlus)
    }
}

@Composable
internal fun OptionCycleRow(
    label: String,
    value: String,
    enabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Text(value, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        CompactButton("<", enabled = enabled, onClick = onPrevious)
        CompactButton(">", enabled = enabled, onClick = onNext)
    }
}

@Composable
internal fun ToggleRow(
    label: String,
    value: String,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium)
            Text(value, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        SecondaryButton(
            text = if (value == "Open" || value == "On" || value == "Subscribed") "On" else "Off",
            modifier = Modifier.widthIn(min = 84.dp),
            enabled = enabled,
            onClick = onToggle
        )
    }
}

@Composable
internal fun CompactButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(BreakoutDimensions.MinimumTouchTarget)
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(if (enabled) BreakoutSurfaceVariant else BreakoutSurfaceVariant.copy(alpha = 0.35f))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (enabled) BreakoutPrimary else BreakoutTextSecondary, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
internal fun LoadingState(label: String) {
    BreakoutCard {
        Row(
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            Text(label, color = BreakoutTextSecondary)
        }
    }
}

@Composable
internal fun InlineMarketWarmupCard() {
    BreakoutCard(border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.28f))) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(44.dp),
                    color = BreakoutPrimary,
                    trackColor = BreakoutSurfaceVariant,
                    strokeWidth = 4.dp
                )
                Text("M", color = BreakoutPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Preparing Artists", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Loading artwork before showing this list.", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
internal fun ArtistLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        BreakoutSecondary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .statusBarsPadding()
            .padding(BreakoutDimensions.ScreenHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        BreakoutCard(
            contentPadding = PaddingValues(BreakoutDimensions.xl),
            border = BorderStroke(1.dp, BreakoutSecondary.copy(alpha = 0.42f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(92.dp),
                        color = BreakoutSecondary,
                        trackColor = BreakoutSurfaceVariant,
                        strokeWidth = 8.dp
                    )
                    BreakoutMark(size = 54.dp)
                }
                Text("Loading Artist", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text(
                    "Preparing profile, signals, and artwork",
                    modifier = Modifier.fillMaxWidth(),
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
internal fun MarketInitializingScreen() {
    val loadingLines = remember {
        listOf(
            "Opening the artist pool",
            "Reading audience movement",
            "Checking daily stream leaders",
            "Matching chart signals",
            "Sorting each market tier",
            "Preloading artist photos",
            "Balancing breakout scores",
            "Preparing search results",
            "Finalizing market"
        )
    }
    var lineIndex by remember { mutableStateOf(MarketLoadingLineMemory.index.coerceIn(0, loadingLines.lastIndex)) }
    LaunchedEffect(Unit) {
        while (lineIndex < loadingLines.lastIndex) {
            delay(820)
            lineIndex += 1
            MarketLoadingLineMemory.index = lineIndex
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        BreakoutPrimary.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .statusBarsPadding()
            .padding(BreakoutDimensions.ScreenHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        BreakoutCard(
            contentPadding = PaddingValues(BreakoutDimensions.xl),
            border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.42f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(92.dp),
                        color = BreakoutPrimary,
                        trackColor = BreakoutSurfaceVariant,
                        strokeWidth = 8.dp
                    )
                    BreakoutMark(size = 54.dp)
                }
                Text("Loading Market", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(targetState = loadingLines[lineIndex], label = "marketLoadingLine") { line ->
                        Text(
                            line,
                            modifier = Modifier.fillMaxWidth(),
                            color = BreakoutTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun BreakoutActionLoadingScreen(
    title: String,
    lines: List<String>,
    accent: Color
) {
    val safeLines = lines.ifEmpty { listOf("Working") }
    var lineIndex by remember(title) { mutableStateOf(0) }
    LaunchedEffect(title, safeLines.size) {
        while (lineIndex < safeLines.lastIndex) {
            delay(720)
            lineIndex += 1
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        accent.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .statusBarsPadding()
            .padding(BreakoutDimensions.ScreenHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        BreakoutCard(
            contentPadding = PaddingValues(BreakoutDimensions.xl),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.42f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(92.dp),
                        color = accent,
                        trackColor = BreakoutSurfaceVariant,
                        strokeWidth = 8.dp
                    )
                    BreakoutMark(size = 54.dp)
                }
                Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(targetState = safeLines[lineIndex], label = "$title-loading-line") { line ->
                        Text(
                            line,
                            modifier = Modifier.fillMaxWidth(),
                            color = BreakoutTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

private object MarketLoadingLineMemory {
    var index: Int = 0
}

@Composable
internal fun StatTile(label: String, value: String, caption: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(BreakoutSurfaceVariant)
            .padding(BreakoutDimensions.CardPadding),
        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
    ) {
        Text(label, color = BreakoutTextSecondary, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(caption, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun Pill(text: String) {
    Box(
        modifier = Modifier
            .heightIn(min = BreakoutDimensions.MinimumTouchTarget)
            .clip(RoundedCornerShape(BreakoutDimensions.CardCornerRadius))
            .border(1.dp, BreakoutOutline, RoundedCornerShape(BreakoutDimensions.CardCornerRadius))
            .padding(horizontal = BreakoutDimensions.md, vertical = BreakoutDimensions.sm),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = BreakoutTextSecondary,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
internal fun TagLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(BreakoutPrimary.copy(alpha = 0.16f))
            .padding(horizontal = BreakoutDimensions.sm, vertical = BreakoutDimensions.xs),
        color = BreakoutPrimary,
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}

@Composable
internal fun RosterToggleButton(
    modifier: Modifier = Modifier,
    added: Boolean,
    drafted: Boolean = false,
    waiver: Boolean = false,
    waiverCancel: Boolean = false,
    enabled: Boolean = true,
    size: androidx.compose.ui.unit.Dp = BreakoutDimensions.MinimumTouchTarget,
    solid: Boolean = false,
    onClick: () -> Unit
) {
    val fillColor = when {
        drafted -> BreakoutSurfaceVariant
        added -> if (solid) BreakoutCoral.copy(alpha = 0.9f) else BreakoutCoral.copy(alpha = 0.16f)
        waiverCancel -> if (solid) BreakoutCoral.copy(alpha = 0.9f) else BreakoutCoral.copy(alpha = 0.24f)
        waiver -> if (solid) WaiverAccent.copy(alpha = 0.92f) else WaiverAccent.copy(alpha = 0.18f)
        enabled -> if (solid) BreakoutPrimary.copy(alpha = 0.94f) else BreakoutPrimary.copy(alpha = 0.18f)
        else -> BreakoutSurfaceVariant
    }
    val contentColor = when {
        drafted -> BreakoutTextSecondary
        solid && (added || waiverCancel || enabled) -> Color.White
        solid && waiver -> Color(0xFF201406)
        added -> BreakoutCoral
        waiverCancel -> BreakoutCoral
        waiver -> WaiverAccent
        enabled -> BreakoutPrimary
        else -> BreakoutTextSecondary
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(fillColor)
            .border(
                if (solid) 2.dp else 1.dp,
                when {
                    drafted -> BreakoutOutline.copy(alpha = 0.35f)
                    added || waiverCancel -> BreakoutCoral.copy(alpha = if (solid) 0.85f else 0.44f)
                    waiver -> WaiverAccent.copy(alpha = if (solid) 0.82f else 0.44f)
                    enabled -> BreakoutPrimary.copy(alpha = if (solid) 0.88f else 0.48f)
                    else -> BreakoutOutline.copy(alpha = 0.32f)
                },
                CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when {
                added && !drafted -> "-"
                waiverCancel -> "-"
                waiver -> "W"
                else -> "+"
            },
            color = contentColor,
            style = if (size > BreakoutDimensions.MinimumTouchTarget) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = BreakoutDimensions.MinimumTouchTarget),
        colors = ButtonDefaults.buttonColors(containerColor = BreakoutPrimary),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius)
    ) {
        Text(
            text,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = BreakoutDimensions.MinimumTouchTarget),
        colors = ButtonDefaults.buttonColors(containerColor = BreakoutSurfaceVariant),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
        border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.65f))
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun AccentButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.heightIn(min = BreakoutDimensions.MinimumTouchTarget),
        colors = ButtonDefaults.buttonColors(
            containerColor = BreakoutSecondary.copy(alpha = 0.22f),
            contentColor = BreakoutSecondary
        ),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
        border = BorderStroke(1.dp, BreakoutSecondary.copy(alpha = 0.58f))
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun DangerButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = BreakoutDimensions.MinimumTouchTarget),
        colors = ButtonDefaults.buttonColors(containerColor = BreakoutCoral.copy(alpha = 0.28f)),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius)
    ) {
        Text(text, color = BreakoutCoral, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun DangerMiniButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = BreakoutDimensions.MinimumTouchTarget),
        colors = ButtonDefaults.buttonColors(containerColor = BreakoutCoral.copy(alpha = 0.22f)),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius)
    ) {
        Text(text, color = BreakoutCoral, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
internal fun AccentConfirmButton(
    text: String,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = BreakoutDimensions.MinimumTouchTarget),
        colors = ButtonDefaults.buttonColors(
            containerColor = accent.copy(alpha = 0.24f),
            contentColor = accent
        ),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.38f))
    ) {
        Text(text, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun DangerIconButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(BreakoutDimensions.MinimumTouchTarget)
            .clip(CircleShape)
            .background(BreakoutCoral.copy(alpha = 0.22f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("x", color = BreakoutCoral, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
internal fun SecondaryMiniButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.heightIn(min = 34.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BreakoutSurfaceVariant,
            disabledContainerColor = BreakoutSurfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
        contentPadding = PaddingValues(horizontal = BreakoutDimensions.sm, vertical = 0.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold, maxLines = 1, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
internal fun BreakoutBottomNavigation(
    selectedTab: BreakoutTab,
    onTabSelected: (BreakoutTab) -> Unit
) {
    val primaryTabs = listOf(BreakoutTab.Home, BreakoutTab.Draft, BreakoutTab.Matchup, BreakoutTab.Market, BreakoutTab.Roster)
    Surface(
        color = BreakoutSurface,
        border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = BreakoutDimensions.md, vertical = BreakoutDimensions.xs),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            primaryTabs.forEach { tab ->
                val selected = tab == selectedTab
                val tabColor by animateColorAsState(targetValue = if (selected) BreakoutPrimary else BreakoutTextSecondary, label = "tabColor")
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = BreakoutDimensions.MinimumTouchTarget)
                        .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                        .background(if (selected) BreakoutPrimary.copy(alpha = 0.18f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (selected) BreakoutPrimary.copy(alpha = 0.34f) else Color.Transparent,
                            RoundedCornerShape(BreakoutDimensions.SmallCornerRadius)
                        )
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = BreakoutDimensions.xxs, vertical = BreakoutDimensions.xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(tab.mark, color = tabColor, style = MaterialTheme.typography.labelLarge)
                    Text(
                        tab.title,
                        color = tabColor,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

internal fun isOnlinePlayConfigured(): Boolean =
    BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

internal fun String.cleanUsernameInput(): String =
    filter { it.isLetterOrDigit() || it == '_' }.take(MaxUsernameLength)

internal fun isValidUsername(username: String): Boolean =
    username.matches(Regex("^[A-Za-z0-9_]{$MinUsernameLength,$MaxUsernameLength}$")) &&
        !username.equals("you", ignoreCase = true) &&
        !username.isReservedBotUsername()

internal fun usernameValidationMessage(username: String): String = when {
    username.length < MinUsernameLength -> "Username must be at least $MinUsernameLength characters."
    username.length > MaxUsernameLength -> "Username must be $MaxUsernameLength characters or fewer."
    username.equals("you", ignoreCase = true) -> "Username cannot be You because Breakout uses You to label your own picks."
    username.isReservedBotUsername() -> "Usernames like Bot1 are reserved for test bots."
    !username.matches(Regex("^[A-Za-z0-9_]+$")) -> "Username can only use letters, numbers, and underscores."
    else -> "Username must be $MinUsernameLength-$MaxUsernameLength letters, numbers, or underscores."
}

internal fun String.isReservedBotUsername(): Boolean =
    matches(Regex("^bot\\d+$", RegexOption.IGNORE_CASE))

internal fun isValidEmail(email: String): Boolean =
    email.trim().let { value ->
        value.length in 5..MaxEmailLength &&
            value.count { it == '@' } == 1 &&
            value.substringBefore("@").isNotBlank() &&
            value.substringAfter("@").contains(".") &&
            value.none { it.isWhitespace() }
    }

internal fun passwordPolicyError(password: String): String? = when {
    password.length < MinPasswordLength -> "Password must be at least $MinPasswordLength characters."
    password.none { it.isLowerCase() } -> "Password must include a lowercase letter."
    password.none { it.isUpperCase() } -> "Password must include an uppercase letter."
    password.none { it.isDigit() } -> "Password must include a number."
    password.none { !it.isLetterOrDigit() } -> "Password must include a symbol."
    else -> null
}

internal fun emailQualityError(email: String): String? {
    val value = email.trim().lowercase()
    if (!isValidEmail(value)) return "Enter a valid email address."
    val localPart = value.substringBefore("@")
    val domain = value.substringAfter("@")
    if (localPart.startsWith(".") || localPart.endsWith(".") || localPart.contains("..")) {
        return "Enter a valid email address."
    }
    if (domain.startsWith(".") || domain.endsWith(".") || domain.contains("..")) {
        return "Enter a valid email address."
    }
    CommonEmailDomainTypos[domain]?.let { suggestion ->
        return "Did you mean $suggestion?"
    }
    if (domain in BlockedSignupEmailDomains) {
        return "Use a real inbox so you can verify your account."
    }
    if (!domain.substringAfterLast(".").all { it.isLetter() }) {
        return "Enter a valid email address."
    }
    return null
}

internal fun String.cleanDraftDateLabel(): String =
    trim()
        .takeUnless { it.isBlank() || it.equals("null", ignoreCase = true) }
        ?.normalizeDraftDateLabel()
        ?: "Set date"

internal fun String.displayDraftDateLabel(): String =
    cleanDraftDateLabel().let { label ->
        if (label == "Set date") {
            "Date Not Set"
        } else {
            parseDraftDateTime(label)?.format(DraftDateDisplayFormatter) ?: label
        }
    }

internal fun formatDraftLength(totalSeconds: Int): String {
    val minutes = (totalSeconds + 59) / 60
    if (minutes <= 0) return "Under 1 min"
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return when {
        hours == 0 -> "$minutes min"
        remainingMinutes == 0 -> "$hours hr"
        else -> "$hours hr $remainingMinutes min"
    }
}

internal fun draftCountdownLabel(draftDateLabel: String): String? {
    val scheduledAt = parseDraftDateTime(draftDateLabel) ?: return null
    val totalMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), scheduledAt)
    if (totalMinutes <= 0) return "Starting now"
    val days = totalMinutes / (24 * 60)
    val hours = (totalMinutes % (24 * 60)) / 60
    val minutes = totalMinutes % 60
    return when {
        days > 0 && hours > 0 -> "$days ${if (days == 1L) "day" else "days"} $hours hr"
        days > 0 -> "$days ${if (days == 1L) "day" else "days"}"
        hours > 0 && minutes > 0 -> "$hours hr $minutes min"
        hours > 0 -> "$hours hr"
        else -> "$minutes min"
    }
}

internal fun formatPickDuration(seconds: Int?): String {
    val safeSeconds = seconds ?: return "--"
    return if (safeSeconds < 60) {
        "${safeSeconds}s"
    } else {
        "${safeSeconds / 60}m ${safeSeconds % 60}s"
    }
}

internal fun String.normalizeDraftDateLabel(): String {
    val value = trim()
    return parseDraftDateTime(value)?.format(DraftDateInputFormatter) ?: value
}

internal fun parseDraftDateTime(value: String): LocalDateTime? {
    val cleaned = value.trim()
    if (cleaned.isBlank() || cleaned.equals("Set date", ignoreCase = true)) return null
    return runCatching {
        OffsetDateTime.parse(cleaned).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    }.getOrElse {
        runCatching {
            Instant.parse(cleaned).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }.getOrElse {
            runCatching {
                LocalDateTime.parse(cleaned.replace("T", " ").take(16), DraftDateInputFormatter)
            }.getOrNull()
        }
    }
}

internal fun String.parseServerInstantMillis(): Long? {
    val cleaned = trim()
    if (cleaned.isBlank() || cleaned.equals("null", ignoreCase = true)) return null
    return runCatching {
        Instant.parse(cleaned).toEpochMilli()
    }.getOrElse {
        runCatching {
            OffsetDateTime.parse(cleaned).toInstant().toEpochMilli()
        }.getOrNull()
    }
}

internal fun draftDateInputPart(label: String): String =
    parseDraftDateTime(label)?.toLocalDate()?.toString() ?: ""

internal fun draftTimeInputPart(label: String): String =
    parseDraftDateTime(label)?.toLocalTime()?.let { "%02d:%02d".format(it.hour, it.minute) } ?: ""

internal fun draftDateDisplayPart(date: String): String =
    runCatching {
        LocalDate.parse(date.trim()).format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }.getOrDefault("Choose Date")

internal fun draftTimeDisplayPart(time: String): String =
    runCatching {
        LocalDateTime.parse("2026-01-01 ${time.trim()}", DraftDateInputFormatter)
            .toLocalTime()
            .format(DateTimeFormatter.ofPattern("h:mm a"))
    }.getOrDefault("Choose Time")

internal fun draftDateValidationError(date: String, time: String): String? {
    if (date.isBlank() || time.isBlank()) return "Enter a draft date and time."
    val dateTime = runCatching {
        LocalDateTime.parse("${date.trim()} ${time.trim()}", DraftDateInputFormatter)
    }.getOrNull() ?: return "Use date YYYY-MM-DD and time HH:MM."
    val leadMinutes = if (BuildConfig.DEBUG) DebugDraftLeadMinutes else ReleaseDraftLeadMinutes
    val earliest = LocalDateTime.now().plusMinutes(leadMinutes)
    val latest = LocalDateTime.now().plusYears(1)
    return when {
        dateTime.isBefore(earliest) -> "Draft time must be at least ${leadMinutes.minuteLabel()} from now."
        dateTime.isAfter(latest) -> "Draft time must be within the next year."
        else -> null
    }
}

internal fun Long.minuteLabel(): String = if (this == 1L) "1 minute" else "$this minutes"

internal fun draftDateLabelFromInputs(date: String, time: String): String =
    LocalDateTime.parse("${date.trim()} ${time.trim()}", DraftDateInputFormatter).format(DraftDateInputFormatter)

internal fun String.toDraftServerTimestamp(): String? =
    parseDraftDateTime(this)
        ?.atZone(ZoneId.systemDefault())
        ?.toOffsetDateTime()
        ?.toString()

internal fun profileUsernameFor(account: AccountUi): String {
    val preferred = account.username.cleanUsernameInput()
    if (isValidUsername(preferred)) return preferred
    val emailName = account.email.substringBefore("@").cleanUsernameInput()
    if (isValidUsername(emailName)) return emailName
    return profileFallbackUsername(account)
}

internal fun profileFallbackUsername(account: AccountUi): String {
    val userSuffix = account.userId.filter { it.isLetterOrDigit() }.take(8).ifBlank { "000000" }
    return "user_$userSuffix".take(MaxUsernameLength)
}

internal fun showDraftSystemNotification(context: Context, title: String, message: String, notificationId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val notification = NotificationCompat.Builder(context, DraftNotificationChannelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(notificationId, notification)
}

internal fun scheduleDraftReminderNotifications(context: Context, league: LeagueUi) {
    val scheduledAt = parseDraftDateTime(league.settings.draftDateLabel) ?: return
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val reminders = DraftReminderOffsets + (1L to "1 minute") + (0L to "started")
    reminders.forEach { (offsetMinutes, label) ->
        val triggerAtMillis = scheduledAt
            .minusMinutes(offsetMinutes)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val requestCode = league.id.hashCode() + offsetMinutes.toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, DraftReminderReceiver::class.java).apply {
                putExtra(
                    DraftReminderTitleExtra,
                    if (offsetMinutes == 0L) "${league.name}: Draft lobby open" else "${league.name}: Draft starts in $label"
                )
                putExtra(
                    DraftReminderMessageExtra,
                    if (offsetMinutes == 0L) {
                        "${league.name} is waiting for members before the first pick."
                    } else {
                        "${league.name} starts at ${league.settings.draftDateLabel.displayDraftDateLabel()}."
                    }
                )
                putExtra(DraftReminderIdExtra, requestCode)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        if (triggerAtMillis <= System.currentTimeMillis()) {
            alarmManager.cancel(pendingIntent)
        } else {
            scheduleReminderAlarm(alarmManager, triggerAtMillis, pendingIntent)
        }
    }
}

internal fun scheduleReminderAlarm(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
    runCatching {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms() -> {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
            else -> {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        }
    }.getOrElse {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }
}

internal fun isSignInAgainMessage(message: String): Boolean =
    message.contains("Sign in again", ignoreCase = true) ||
        message.contains("session expired", ignoreCase = true)

internal fun isAuthFailure(rawMessage: String?): Boolean {
    val message = rawMessage.orEmpty()
    return message.contains("JWT expired", ignoreCase = true) ||
        message.contains("invalid JWT", ignoreCase = true) ||
        message.contains("invalid claim", ignoreCase = true) ||
        message.contains("not authenticated", ignoreCase = true) ||
        message.contains("Authentication required", ignoreCase = true) ||
        message.contains("PGRST301", ignoreCase = true) ||
        message.contains("PGRST303", ignoreCase = true)
}

internal fun friendlyLeagueLoadError(rawMessage: String?): String {
    val message = rawMessage.orEmpty()
    return when {
        isAuthFailure(message) -> "Your session expired. Please log in again."
        message.contains("42P17", ignoreCase = true) ||
            message.contains("infinite recursion", ignoreCase = true) ->
            "League sync needs a refresh. The league was created, but the list could not update."
        message.contains("my_leagues", ignoreCase = true) ||
            message.contains("function", ignoreCase = true) && message.contains("does not exist", ignoreCase = true) ->
            "League sync needs a refresh. Please try again."
        else -> ""
    }
}

internal fun cleanVisibleError(rawMessage: String?): String {
    val message = rawMessage.orEmpty()
    return when {
        message.isBlank() -> ""
        isAuthFailure(message) -> "Your session expired. Please log in again."
        message.contains("PGRST102", ignoreCase = true) ||
            message.contains("empty or invalid json", ignoreCase = true) ->
            "The waiver request could not be sent cleanly. Please try again."
        message.trim().startsWith("{") -> normalizedErrorText(message).ifBlank { "Something went wrong. Please try again." }
        else -> message
    }
}

internal fun normalizedErrorText(rawMessage: String?): String {
    val raw = rawMessage.orEmpty()
    val trimmed = raw.trim()
    if (!trimmed.startsWith("{")) return raw
    return runCatching {
        val json = JSONObject(trimmed)
        listOf(
            json.optString("code"),
            json.optString("error_code"),
            json.optString("error"),
            json.optString("msg"),
            json.optString("message"),
            json.optString("details"),
            json.optString("hint")
        )
            .filter { it.isNotBlank() && it != "null" }
            .joinToString(" ")
            .ifBlank { raw }
    }.getOrDefault(raw)
}

internal fun authRetryDelaySeconds(message: String): Long? =
    Regex("after\\s+(\\d+)\\s+seconds?", RegexOption.IGNORE_CASE)
        .find(message)
        ?.groupValues
        ?.getOrNull(1)
        ?.toLongOrNull()

internal fun isRateLimitError(rawMessage: String?): Boolean {
    val message = normalizedErrorText(rawMessage)
    return message.contains("over_email_send_rate_limit", ignoreCase = true) ||
        message.contains("email rate limit", ignoreCase = true) ||
        message.contains("rate limit", ignoreCase = true) ||
        message.contains("wait a few seconds", ignoreCase = true) ||
        message.contains("too many", ignoreCase = true) && message.contains("attempt", ignoreCase = true) ||
        message.contains("only request this after", ignoreCase = true)
}

internal fun isVerificationEmailNotice(rawMessage: String?): Boolean {
    val message = normalizedErrorText(rawMessage)
    return message.contains("Check your email", ignoreCase = true) ||
        message.contains("waiting for email verification", ignoreCase = true) ||
        message.contains("Email not confirmed", ignoreCase = true) ||
        message.contains("email_not_confirmed", ignoreCase = true)
}

internal fun rateLimitMessage(message: String, mode: AuthMode): String {
    val seconds = authRetryDelaySeconds(message)
    return if (mode == AuthMode.CreateAccount) {
        if (seconds != null) {
            "Account creation is cooling down because verification emails were requested too quickly. Try again in ${seconds.coerceAtLeast(1)} seconds."
        } else {
            "Account creation is cooling down because verification emails were requested too quickly. Try again soon."
        }
    } else if (seconds != null) {
        "Too many login attempts. Try again in ${seconds.coerceAtLeast(1)} seconds."
    } else {
        "Too many login attempts. Please wait before trying again."
    }
}

internal fun friendlyAuthError(rawMessage: String?, mode: AuthMode): String {
    val message = normalizedErrorText(rawMessage)
    return when {
        isRateLimitError(message) ->
            rateLimitMessage(message, mode)
        message.contains("User already registered", ignoreCase = true) ||
            message.contains("already been registered", ignoreCase = true) ||
            message.contains("already registered", ignoreCase = true) ||
            message.contains("user_already_exists", ignoreCase = true) ||
            message.contains("email_exists", ignoreCase = true) ||
            message.contains("email already", ignoreCase = true) ||
            message.contains("duplicate", ignoreCase = true) && message.contains("email", ignoreCase = true) ||
            message.contains("already exists", ignoreCase = true) && message.contains("email", ignoreCase = true) ->
            "That email already has an account. Use Log In instead."
        message.contains("Invalid login credentials", ignoreCase = true) ||
            message.contains("invalid_credentials", ignoreCase = true) ->
            "That email/username and password do not match."
        message.contains("No account found", ignoreCase = true) ->
            "No account was found for that username."
        message.contains("Email not confirmed", ignoreCase = true) ||
            message.contains("email_not_confirmed", ignoreCase = true) ->
            "Check your email to verify this account, then log in."
        message.contains("waiting for email verification", ignoreCase = true) ->
            "Account created. This account is waiting for email verification before it can log in."
        message.contains("Check your email", ignoreCase = true) ->
            "Account created. Check your email to verify it, then log in."
        message.contains("Enter a valid email", ignoreCase = true) ||
            message.contains("invalid email", ignoreCase = true) ||
            message.contains("email address is invalid", ignoreCase = true) ||
            message.contains("invalid email address", ignoreCase = true) ||
            message.contains("email_address_invalid", ignoreCase = true) ->
            "Enter a valid email address."
        message.contains("Username must be", ignoreCase = true) ||
            message.contains("profiles_username_format", ignoreCase = true) ->
            "Username must be $MinUsernameLength-$MaxUsernameLength letters, numbers, or underscores, and cannot be You."
        message.contains("username is already taken", ignoreCase = true) ||
            message.contains("That username is already taken", ignoreCase = true) ||
            message.contains("duplicate", ignoreCase = true) && message.contains("username", ignoreCase = true) ->
            "That username is already taken."
        message.contains("leaked", ignoreCase = true) ||
            message.contains("pwned", ignoreCase = true) ||
            message.contains("compromised", ignoreCase = true) ->
            passwordBackendMessage(message)
        message.contains("Password should be", ignoreCase = true) ||
            message.contains("weak password", ignoreCase = true) ||
            message.contains("Password is too weak", ignoreCase = true) ||
            message.contains("password", ignoreCase = true) && message.contains("8", ignoreCase = true) ->
            passwordBackendMessage(message)
        message.contains("Online accounts are not configured", ignoreCase = true) ->
            "Accounts are unavailable right now."
        message.contains("Network is unreachable", ignoreCase = true) ||
            message.contains("Unable to resolve host", ignoreCase = true) ||
            message.contains("timeout", ignoreCase = true) ->
            "Could not connect. Check your internet connection and try again."
        mode == AuthMode.CreateAccount -> "Could not create the account. Check the email and username, then try again."
        else -> "Could not log in. Please check your details and try again."
    }
}

internal fun passwordBackendMessage(message: String): String = when {
    message.contains("leaked", ignoreCase = true) ||
        message.contains("pwned", ignoreCase = true) ||
        message.contains("compromised", ignoreCase = true) ->
        "This password has appeared in a data breach. Choose a different password."
    message.contains("lower", ignoreCase = true) ->
        "Password must include a lowercase letter."
    message.contains("upper", ignoreCase = true) ->
        "Password must include an uppercase letter."
    message.contains("digit", ignoreCase = true) ||
        message.contains("number", ignoreCase = true) ->
        "Password must include a number."
    message.contains("symbol", ignoreCase = true) ||
        message.contains("special", ignoreCase = true) ->
        "Password must include a symbol."
    message.contains("8", ignoreCase = true) ||
        message.contains("minimum", ignoreCase = true) ||
        message.contains("short", ignoreCase = true) ->
        "Password must be at least $MinPasswordLength characters."
    else -> "Password does not meet the requirements."
}

internal fun friendlyPasswordResetError(rawMessage: String?): String {
    val message = normalizedErrorText(rawMessage)
    return when {
        isRateLimitError(message) -> "Password reset is cooling down. Please wait before requesting another link."
        isAuthFailure(message) -> "This reset link expired. Request a new password reset link."
        message.contains("leaked", ignoreCase = true) ||
            message.contains("pwned", ignoreCase = true) ||
            message.contains("compromised", ignoreCase = true) ->
            passwordBackendMessage(message)
        message.contains("weak password", ignoreCase = true) ||
            message.contains("Password is too weak", ignoreCase = true) ||
            message.contains("password", ignoreCase = true) && message.contains("8", ignoreCase = true) ->
            passwordBackendMessage(message)
        message.contains("Enter a valid email", ignoreCase = true) ||
            message.contains("invalid email", ignoreCase = true) ||
            message.contains("email address is invalid", ignoreCase = true) ||
            message.contains("invalid email address", ignoreCase = true) ->
            "Enter a valid email address."
        message.contains("Use a real inbox", ignoreCase = true) ->
            "Use a real inbox so you can receive the reset link."
        message.contains("Network is unreachable", ignoreCase = true) ||
            message.contains("Unable to resolve host", ignoreCase = true) ||
            message.contains("timeout", ignoreCase = true) ->
            "Could not connect. Check your internet connection and try again."
        else -> "Could not send the reset link. Please try again."
    }
}

internal fun friendlyAccountError(rawMessage: String?): String {
    val message = rawMessage.orEmpty()
    return when {
        message.contains("JWT expired", ignoreCase = true) ||
            message.contains("invalid JWT", ignoreCase = true) ||
            message.contains("Sign in again", ignoreCase = true) ->
            "Your session expired. Please log in again."
        message.contains("Username must be", ignoreCase = true) ||
            message.contains("profiles_username_format", ignoreCase = true) ->
            "Username must be $MinUsernameLength-$MaxUsernameLength letters, numbers, or underscores, and cannot be You."
        message.contains("username is already taken", ignoreCase = true) ||
            message.contains("That username is already taken", ignoreCase = true) ||
            message.contains("duplicate key", ignoreCase = true) && message.contains("username", ignoreCase = true) ->
            "That username is already taken."
        message.contains("duplicate key", ignoreCase = true) && message.contains("email", ignoreCase = true) ->
            "That email is already connected to another account."
        message.contains("delete_my_account", ignoreCase = true) ||
            message.contains("function", ignoreCase = true) && message.contains("does not exist", ignoreCase = true) ->
            "Account deletion is unavailable right now. Please try again later."
        else -> "Could not update the account. Please try again."
    }
}

internal fun friendlyJoinError(rawMessage: String?): String {
    val message = rawMessage.orEmpty()
    return when {
        isAuthFailure(message) -> "Your session expired. Please log in again."
        message.contains("League is full", ignoreCase = true) -> "That league is full."
        message.contains("already in that league", ignoreCase = true) -> "You are already in that league."
        message.contains("Invite codes must", ignoreCase = true) -> "Invite codes must be exactly 6 characters."
        message.contains("Invalid invite code", ignoreCase = true) -> "No open league with that exact code is available."
        else -> "No open league with that exact code is available."
    }
}

internal fun friendlyDraftPickError(rawMessage: String?): String {
    val message = normalizedErrorText(rawMessage)
    return when {
        isAuthFailure(message) -> "Your session expired. Please log in again."
        message.contains("not your pick", ignoreCase = true) ||
            message.contains("not on the clock", ignoreCase = true) ->
            "It is not your pick right now."
        message.contains("pick has already advanced", ignoreCase = true) ||
            message.contains("expected", ignoreCase = true) && message.contains("pick", ignoreCase = true) ->
            "That pick already advanced. The draft board has been refreshed."
        message.contains("already drafted", ignoreCase = true) ||
            message.contains("duplicate", ignoreCase = true) && message.contains("artist", ignoreCase = true) ->
            "That artist has already been drafted."
        message.contains("roster", ignoreCase = true) ||
            message.contains("slot", ignoreCase = true) ->
            "That artist does not fit an open roster slot."
        message.contains("league", ignoreCase = true) && message.contains("not live", ignoreCase = true) ->
            "The draft is not live right now."
        else -> "Could not make that pick. Refresh the draft room and try again."
    }
}

internal fun friendlyDraftStartError(rawMessage: String?): String {
    val message = rawMessage.orEmpty()
    return when {
        isAuthFailure(message) -> "Your session expired. Please log in again."
        message.contains("at least one more member", ignoreCase = true) -> "Invite at least one more member before the draft can start."
        message.contains("manager", ignoreCase = true) -> "Only the manager can start the draft."
        else -> "Could not start the draft. Please refresh and try again."
    }
}

internal fun friendlyLeagueError(rawMessage: String?): String {
    val message = rawMessage.orEmpty()
    return when {
        isAuthFailure(message) -> "Your session expired. Please log in again."
        message.contains("leagues_owner_id_fkey", ignoreCase = true) ||
            message.contains("not present in table", ignoreCase = true) -> "Sign in again to finish account setup."
        message.contains("42P17", ignoreCase = true) ||
            message.contains("infinite recursion", ignoreCase = true) ->
            "League sync needs a refresh. The league was created, but the list could not update."
        message.contains("Invite codes must", ignoreCase = true) -> "Invite codes must be exactly 6 characters."
        message.contains("duplicate key", ignoreCase = true) ||
            message.contains("invite_code", ignoreCase = true) -> "That invite code was already used. Try creating the league again."
        else -> "Could not create league. Please try again."
    }
}

internal fun friendlyTradeError(rawMessage: String?): String {
    val message = normalizedErrorText(rawMessage)
    return when {
        isAuthFailure(message) -> "Your session expired. Please log in again."
        message.contains("not a member", ignoreCase = true) -> "That member is not available for trades."
        message.contains("not active", ignoreCase = true) -> "That trade is no longer active."
        message.contains("expired", ignoreCase = true) -> "That trade offer has expired."
        message.contains("active outgoing", ignoreCase = true) -> "You already have 5 active outgoing trades. Cancel one before sending another."
        message.contains("no longer has", ignoreCase = true) ||
            message.contains("already moved", ignoreCase = true) -> "One of those artists moved rosters. Refresh trades and try again."
        message.contains("does not fit", ignoreCase = true) ||
            message.contains("slot", ignoreCase = true) -> "That trade does not fit both roster slots."
        message.contains("draft", ignoreCase = true) && message.contains("complete", ignoreCase = true) -> "Trades open after the draft is complete."
        else -> "Could not complete that trade. Refresh and try again."
    }
}

internal fun firstOpenSlotFor(
    artist: ArtistUi,
    roster: Map<RosterSlot, ArtistUi>,
    settings: LeagueSettingsUi
): RosterSlot? = preferredSlotsFor(artist).firstOrNull { slot ->
    slot in activeRosterSlots(settings) &&
    roster[slot] == null && slot.canHold(artist)
}

internal fun waiverReplacementOptionsFor(
    artist: ArtistUi,
    roster: Map<RosterSlot, ArtistUi>,
    settings: LeagueSettingsUi
): List<Pair<RosterSlot, ArtistUi>> = activeRosterSlots(settings)
    .filter { slot -> slot.canHold(artist) }
    .mapNotNull { slot -> roster[slot]?.let { currentArtist -> slot to currentArtist } }

internal fun claimableSlotFor(
    artist: ArtistUi,
    roster: Map<RosterSlot, ArtistUi>,
    settings: LeagueSettingsUi
): RosterSlot? = firstOpenSlotFor(artist, roster, settings)
    ?: waiverReplacementOptionsFor(artist, roster, settings).firstOrNull()?.first

internal enum class ArtistActionKind {
    None,
    Draft,
    QueueWaiver,
    WaiverQueueFull,
    CancelWaiver,
    Drop,
    WaiverUnavailable
}

internal data class ResolvedArtistAction(
    val kind: ArtistActionKind,
    val draftedByYou: Boolean = false,
    val draftedByOther: Boolean = false,
    val waiverQueued: Boolean = false,
    val waiverQueuePosition: Int? = null,
    val statusLabel: String? = null,
    val unavailableDetail: String? = null
) {
    val canReveal: Boolean
        get() = kind != ArtistActionKind.None
}

internal fun resolveArtistAction(
    artist: ArtistUi,
    roster: Map<RosterSlot, ArtistUi>,
    draftPicks: List<DraftPickUi>,
    waiverQueuedNames: Set<String>,
    leagueSettings: LeagueSettingsUi,
    draftStatus: DraftStatus,
    draftPickMode: Boolean,
    canMakeDraftPick: Boolean,
    readOnly: Boolean = false
): ResolvedArtistAction {
    val artistKey = artist.name.lowercase()
    val draftedPick = draftPicks.firstOrNull { it.artist.name.equals(artist.name, ignoreCase = true) }
    val draftedByYou = roster.values.any { it.name.equals(artist.name, ignoreCase = true) }
    val drafted = draftedPick != null
    val draftedByOther = drafted && !draftedByYou
    val waiverQueued = artistKey in waiverQueuedNames
    val waiverQueuePosition = waiverQueuedNames.toList().indexOf(artistKey).takeIf { it >= 0 }?.plus(1)
    val claimableSlot = claimableSlotFor(artist, roster, leagueSettings)
    val openDraftSlot = firstOpenSlotFor(artist, roster, leagueSettings)
    val base = ResolvedArtistAction(
        kind = ArtistActionKind.None,
        draftedByYou = draftedByYou,
        draftedByOther = draftedByOther,
        waiverQueued = waiverQueued,
        waiverQueuePosition = waiverQueuePosition,
        statusLabel = when {
            draftedByYou -> "On Roster"
            draftedByOther -> "Taken"
            else -> null
        }
    )
    if (readOnly) return base
    return when {
        draftedByYou -> base.copy(kind = ArtistActionKind.Drop)
        waiverQueued -> base.copy(kind = ArtistActionKind.CancelWaiver)
        draftPickMode && draftStatus == DraftStatus.Live && canMakeDraftPick && !drafted && openDraftSlot != null ->
            base.copy(kind = ArtistActionKind.Draft)
        draftStatus == DraftStatus.Complete && !drafted && claimableSlot != null && waiverQueuedNames.size < leagueSettings.maxWaiverClaims ->
            base.copy(kind = ArtistActionKind.QueueWaiver)
        draftStatus == DraftStatus.Complete && !drafted && claimableSlot != null ->
            base.copy(
                kind = ArtistActionKind.WaiverQueueFull,
                unavailableDetail = "Your waiver queue is full. Cancel a claim before adding another."
            )
        draftStatus == DraftStatus.Complete && !drafted ->
            base.copy(
                kind = ArtistActionKind.WaiverUnavailable,
                unavailableDetail = "This artist cannot fit an open roster slot right now."
            )
        else -> base
    }
}

internal fun preferredSlotsFor(artist: ArtistUi): List<RosterSlot> = when {
    artist.isHeadlinerEligible() -> headlinerSlots() + benchSlots()
    artist.isMainstayEligible() -> wildcardSlots() + benchSlots()
    artist.isRisingEligible() -> risingSlots() + benchSlots()
    artist.isDeepCutEligible() -> deepCutSlots() + benchSlots()
    else -> benchSlots()
}

internal fun ArtistUi.currentRoleLabel(): String = tag

internal fun ArtistUi.withAcquiredRole(): ArtistUi =
    if (acquiredRole.isNullOrBlank()) copy(acquiredRole = currentRoleLabel()) else this

internal fun ArtistUi.isGrandfatheredFor(slot: RosterSlot? = null): Boolean {
    val retainedRole = acquiredRole ?: return false
    val changedRole = !retainedRole.equals(currentRoleLabel(), ignoreCase = true)
    return changedRole && (slot == null || slot.roleLabel()?.equals(retainedRole, ignoreCase = true) == true)
}

internal fun RosterSlot.roleLabel(): String? = when (this) {
    RosterSlot.HeadlinerOne,
    RosterSlot.HeadlinerTwo,
    RosterSlot.HeadlinerThree,
    RosterSlot.HeadlinerFour -> "Headliner"
    RosterSlot.WildcardOne,
    RosterSlot.WildcardTwo,
    RosterSlot.WildcardThree,
    RosterSlot.WildcardFour -> "Mainstay"
    RosterSlot.RisingOne,
    RosterSlot.RisingTwo,
    RosterSlot.RisingThree,
    RosterSlot.RisingFour -> "Rising"
    RosterSlot.DeepCutOne,
    RosterSlot.DeepCutTwo,
    RosterSlot.DeepCutThree -> "Deep Cut"
    else -> null
}

internal fun RosterSlot.canHold(artist: ArtistUi): Boolean = when (this) {
    RosterSlot.HeadlinerOne,
    RosterSlot.HeadlinerTwo,
    RosterSlot.HeadlinerThree,
    RosterSlot.HeadlinerFour -> artist.isHeadlinerEligible() || artist.acquiredRole.equals("Headliner", ignoreCase = true)
    RosterSlot.RisingOne,
    RosterSlot.RisingTwo,
    RosterSlot.RisingThree,
    RosterSlot.RisingFour -> artist.isRisingEligible() || artist.acquiredRole.equals("Rising", ignoreCase = true)
    RosterSlot.WildcardOne,
    RosterSlot.WildcardTwo,
    RosterSlot.WildcardThree,
    RosterSlot.WildcardFour -> artist.isMainstayEligible() || artist.acquiredRole.equals("Mainstay", ignoreCase = true)
    RosterSlot.BenchOne,
    RosterSlot.BenchTwo,
    RosterSlot.BenchThree,
    RosterSlot.BenchFour,
    RosterSlot.BenchFive,
    RosterSlot.BenchSix -> true
    RosterSlot.DeepCutOne,
    RosterSlot.DeepCutTwo,
    RosterSlot.DeepCutThree -> artist.isDeepCutEligible() || artist.acquiredRole.equals("Deep Cut", ignoreCase = true)
}

internal fun activeRosterSlots(settings: LeagueSettingsUi): List<RosterSlot> =
    headlinerSlots().take(settings.headlinerSlots) +
        wildcardSlots().take(settings.wildcardSlots) +
        risingSlots().take(settings.risingSlots) +
        deepCutSlots().take(settings.deepCutSlots) +
        benchSlots().take(settings.benchSlots)

internal fun rebalanceRosterForRoles(
    settings: LeagueSettingsUi,
    artists: List<ArtistUi>
): Map<RosterSlot, ArtistUi> {
    val remaining = artists.marketDistinct().toMutableList()
    val balanced = linkedMapOf<RosterSlot, ArtistUi>()

    fun fillSlots(slots: List<RosterSlot>, eligible: (ArtistUi) -> Boolean) {
        slots.filter { it in activeRosterSlots(settings) }.forEach { slot ->
            val selected = remaining
                .filter(eligible)
                .maxByOrNull { it.breakoutScore(null) }
                ?: return@forEach
            balanced[slot] = selected
            remaining.removeAll { it.name.equals(selected.name, ignoreCase = true) }
        }
    }

    fillSlots(headlinerSlots().take(settings.headlinerSlots)) { it.isHeadlinerEligible() }
    fillSlots(wildcardSlots().take(settings.wildcardSlots)) { it.isMainstayEligible() }
    fillSlots(risingSlots().take(settings.risingSlots)) { it.isRisingEligible() }
    fillSlots(deepCutSlots().take(settings.deepCutSlots)) { it.isDeepCutEligible() }

    benchSlots().take(settings.benchSlots).forEach { slot ->
        val selected = remaining.maxByOrNull { it.breakoutScore(null) } ?: return@forEach
        balanced[slot] = selected
        remaining.removeAll { it.name.equals(selected.name, ignoreCase = true) }
    }

    return balanced
}

internal fun headlinerSlots(): List<RosterSlot> = listOf(
    RosterSlot.HeadlinerOne,
    RosterSlot.HeadlinerTwo,
    RosterSlot.HeadlinerThree,
    RosterSlot.HeadlinerFour
)

internal fun risingSlots(): List<RosterSlot> = listOf(
    RosterSlot.RisingOne,
    RosterSlot.RisingTwo,
    RosterSlot.RisingThree,
    RosterSlot.RisingFour
)

internal fun wildcardSlots(): List<RosterSlot> = listOf(
    RosterSlot.WildcardOne,
    RosterSlot.WildcardTwo,
    RosterSlot.WildcardThree,
    RosterSlot.WildcardFour
)

internal fun deepCutSlots(): List<RosterSlot> = listOf(
    RosterSlot.DeepCutOne,
    RosterSlot.DeepCutTwo,
    RosterSlot.DeepCutThree
)

internal fun benchSlots(): List<RosterSlot> = listOf(
    RosterSlot.BenchOne,
    RosterSlot.BenchTwo,
    RosterSlot.BenchThree,
    RosterSlot.BenchFour,
    RosterSlot.BenchFive,
    RosterSlot.BenchSix
)

internal fun ArtistUi.isHeadlinerEligible(): Boolean =
    (listeners ?: 0L) >= 40_000_000

internal fun ArtistUi.isMainstayEligible(): Boolean =
    !isHeadlinerEligible() && (listeners ?: 0L) >= 15_000_000L

internal fun ArtistUi.isRisingEligible(): Boolean =
    !isHeadlinerEligible() && (listeners ?: 0L) in 1_000_000L until 15_000_000L

internal fun ArtistUi.isDeepCutEligible(): Boolean =
    !isHeadlinerEligible() && (listeners ?: Long.MAX_VALUE) < 1_000_000

internal fun ArtistUi.marketBucket(): MarketFilter = when {
    isHeadlinerEligible() -> MarketFilter.Headliners
    isMainstayEligible() -> MarketFilter.Wildcards
    isDeepCutEligible() -> MarketFilter.DeepCuts
    isRisingEligible() -> MarketFilter.Rising
    else -> MarketFilter.Wildcards
}

internal fun ArtistUi.discoverySortValue(): Double {
    val scale = listeners ?: return 0.0
    val distance = kotlin.math.abs(scale - 250_000) / 250_000.0
    return (1.0 - distance).coerceIn(0.0, 1.0) * 100
}

internal fun ArtistUi.marketMomentumLabel(): String =
    when {
        weeklyListenerGrowthPercent != null && displayWeeklyListenerGain() != null -> {
            val gain = displayWeeklyListenerGain()
            if (gain != null && kotlin.math.abs(gain) > 100L) {
                "${weeklyListenerGrowthPercent.formatSignedPercent()} last week • ${gain.formatSignedCompact()}"
            } else {
                "${weeklyListenerGrowthPercent.formatSignedPercent()} last week"
            }
        }
        weeklyListenerGrowthPercent != null ->
            "${weeklyListenerGrowthPercent.formatSignedPercent()} last week"
        displayWeeklyListenerGain() != null && kotlin.math.abs(displayWeeklyListenerGain() ?: 0L) > 100L ->
            "${displayWeeklyListenerGain()?.formatSignedCompact()} last week"
        else -> compactRead
    }

@Composable
internal fun ArtistUi.displayName(): String = name.displayArtistName()

@Composable
internal fun String.displayArtistName(): String {
    return displayArtistNameForMode(this, LocalEncoreMode.current)
}

internal fun displayArtistNameForMode(name: String, encoreMode: Boolean): String {
    if (!encoreMode) return name
    return when (name.artistKey()) {
        "theweeknd" -> "Father"
        "arianagrande" -> "Mother"
        else -> name
    }
}

internal fun ArtistUi.displayWeeklyListenerGain(): Long? {
    weeklyListenerGain?.let { return it }
    val snapshot = snapshotListeners ?: listeners ?: return null
    val growth = weeklyListenerGrowthPercent ?: return null
    if (growth <= -99.0) return null
    val previous = snapshot / (1.0 + (growth / 100.0))
    return (snapshot - previous).roundToLong()
}

internal fun ArtistUi.artistDetailSubtitle(): String =
    when {
        snapshotDate != null ->
            "Weekly snapshot ${snapshotDate.displaySnapshotDate()} • ${marketNote}"
        source.isNotBlank() ->
            marketNote
        else -> "Market data pending"
    }

internal fun ArtistUi.currentAudienceDetail(): String =
    when {
        currentListenersSource?.contains("Pastspot search", ignoreCase = true) == true -> "Current Spotify listeners"
        currentListenersObservedAt != null -> "Current listeners"
        currentListenersSource != null -> tag
        else -> tag
    }

internal fun String.displaySnapshotDate(): String =
    runCatching {
        val date = java.time.LocalDate.parse(take(10))
        date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d"))
    }.getOrDefault(take(10))

internal fun List<ArtistUi>.marketDistinct(): List<ArtistUi> =
    groupBy { it.name.artistKey() }
        .values
        .mapNotNull { group ->
            group.maxWithOrNull(
                compareBy<ArtistUi> { it.listeners ?: 0L }
                    .thenBy { it.spotifyPopularity ?: 0 }
                    .thenBy { it.trackPopularity ?: 0 }
            )
        }

internal fun ArtistUi.stableListKey(): String {
    val cleanSpotifyId = spotifyId
        ?.trim()
        ?.takeUnless { it.isBlank() || it.equals("null", ignoreCase = true) }
    return cleanSpotifyId?.let { "spotify:$it" }
        ?: id?.let { "id:$it" }
        ?: "name:${name.artistKey()}:${listeners ?: 0L}:${imageUrl.orEmpty().hashCode()}"
}

internal fun ArtistUi.breakoutScore(snapshot: SnapshotUi?): Double {
    val trackSignal = (trackPopularity ?: spotifyPopularity ?: 50).coerceIn(0, 100).toDouble()
    val artistSignal = (spotifyPopularity ?: trackPopularity ?: 50).coerceIn(0, 100).toDouble()
    val recency = (snapshot?.releaseRecencyScore ?: releaseRecencyScore ?: 45.0).coerceIn(0.0, 100.0)
    val audience = normalizedAudienceFloor(listeners) * 100.0
    val kworbDailySignal = kworbDailyListenerChange?.let { signedLogSignal(it) }
    val weeklyGrowthSignal = weeklyListenerGrowthPercent?.let { (45.0 + it.coerceIn(-25.0, 175.0) * 0.26).coerceIn(20.0, 95.0) }
    val streamSignal = kworbDailyStreams?.let { dailyStreamSignal(it, listeners) }
    val peakSignal = kworbPeakListeners?.let { normalizedAudienceFloor(it) * 100.0 }
    val discovery = when {
        listeners == null -> 42.0
        listeners < 1_000_000 -> 82.0
        listeners < 12_000_000 -> 74.0
        listeners < 50_000_000 -> 58.0
        else -> 38.0
    }
    val audienceTrend = when {
        kworbDailySignal != null && peakSignal != null -> (kworbDailySignal * 0.70) + ((audience - peakSignal + 50.0).coerceIn(0.0, 100.0) * 0.30)
        kworbDailySignal != null -> kworbDailySignal
        else -> audience
    }
    val fallbackScore = (trackSignal * 0.17) +
        (artistSignal * 0.13) +
        (audience * 0.15) +
        (audienceTrend * 0.20) +
        ((streamSignal ?: trackSignal) * 0.13) +
        (discovery * 0.08) +
        (recency * 0.07) +
        ((weeklyGrowthSignal ?: audienceTrend) * 0.07)
    val availableSignalCount = listOfNotNull(
        trackPopularity,
        spotifyPopularity,
        kworbDailyListenerChange,
        weeklyListenerGrowthPercent,
        kworbDailyStreams,
        kworbPeakListeners,
        releaseRecencyScore
    ).size
    val confidence = (0.48 + availableSignalCount * 0.08).coerceIn(0.48, 1.0)
    val confidenceAdjustedScore = (fallbackScore * confidence) + (42.0 * (1.0 - confidence))

    if (snapshot == null) return confidenceAdjustedScore.coerceIn(0.0, 100.0)

    val followerGrowth = snapshot.percentGrowth(
        previous = snapshot?.previousListeners,
        current = snapshot?.currentListeners ?: listeners
    )
    val trackGrowth = snapshot.pointGrowth(
        previous = snapshot?.previousTrackPopularity,
        current = snapshot?.currentTrackPopularity ?: trackPopularity
    )
    val lastFmListenerGrowth = snapshot.percentGrowth(
        previous = snapshot?.previousLastFmListeners,
        current = snapshot?.currentLastFmListeners ?: lastFmListeners
    )
    val lastFmPlayGrowth = snapshot.percentGrowth(
        previous = snapshot?.previousLastFmPlaycount,
        current = snapshot?.currentLastFmPlaycount ?: lastFmPlaycount
    )
    val spotifyFollowerGrowth = kworbDailySignal?.let { (it * 0.70) + (followerGrowth * 0.30) } ?: followerGrowth
    val streamGrowth = streamSignal ?: lastFmPlayGrowth
    val weeklyGrowth = weeklyGrowthSignal ?: spotifyFollowerGrowth
    val growthScore = spotifyFollowerGrowth * 0.30 +
        trackGrowth * 0.20 +
        lastFmListenerGrowth * 0.16 +
        streamGrowth * 0.15 +
        recency * 0.09 +
        weeklyGrowth * 0.10

    return (growthScore * 0.72 + confidenceAdjustedScore * 0.28).coerceIn(0.0, 100.0)
}

internal fun SnapshotUi?.percentGrowth(previous: Long?, current: Long?): Double {
    if (previous == null || current == null || previous <= 0L) return 0.0
    val percent = ((current - previous).toDouble() / previous.toDouble()) * 100.0
    return (percent * 5.0).coerceIn(0.0, 100.0)
}

internal fun SnapshotUi?.pointGrowth(previous: Int?, current: Int?): Double {
    if (previous == null || current == null) return 0.0
    return ((current - previous).toDouble() * 5.0).coerceIn(0.0, 100.0)
}

internal fun signedLogSignal(value: Long): Double {
    if (value == 0L) return 50.0
    val magnitude = (log10(kotlin.math.abs(value).toDouble() + 1.0) / 6.0).coerceIn(0.0, 1.0)
    return if (value > 0) {
        50.0 + magnitude * 50.0
    } else {
        50.0 - magnitude * 50.0
    }.coerceIn(0.0, 100.0)
}

internal fun dailyStreamSignal(value: Long, listeners: Long? = null): Double {
    if (value <= 0L) return 0.0
    val logValue = log10(value.toDouble() + 1.0)
    val absolute = ((logValue - 4.0) / 4.8 * 100.0).coerceIn(0.0, 92.0)
    val relative = listeners
        ?.takeIf { it > 0L }
        ?.let { ((value.toDouble() / it.toDouble()) * 1_800.0).coerceIn(0.0, 100.0) }
    return if (relative == null) absolute else (absolute * 0.58 + relative * 0.42).coerceIn(0.0, 98.0)
}

internal fun JSONObject.nullableInt(key: String): Int? = if (isNull(key) || !has(key)) null else optInt(key)

internal fun JSONObject.nullableLong(key: String): Long? = if (isNull(key) || !has(key)) null else optLong(key)

internal fun JSONObject.putNullable(key: String, value: Any?) {
    if (value == null) put(key, JSONObject.NULL) else put(key, value)
}

internal fun ArtistUi.projectionRows(): List<SignalRowUi> {
    return listOf(
        SignalRowUi("Breakout Score", breakoutScore(null).formatScore()),
        SignalRowUi(
            "Weekly Growth",
            weeklyListenerGrowthPercent?.formatSignedPercent() ?: "Not Available"
        ),
        SignalRowUi(
            "Weekly Gain",
            weeklyListenerGain?.formatSignedCompact() ?: "Not Available"
        ),
        SignalRowUi(
            "Daily Streams",
            kworbDailyStreams?.let { dailyStreamSignal(it, listeners).formatScore() } ?: "Not Available"
        ),
        SignalRowUi(
            "Track Signal",
            (trackPopularity ?: spotifyPopularity)?.toString() ?: "Not Available"
        ),
        SignalRowUi(
            "Artist Signal",
            (spotifyPopularity ?: trackPopularity)?.toString() ?: "Not Available"
        ),
        SignalRowUi(
            "Release Recency",
            ((releaseRecencyScore ?: 45.0).formatScore())
        )
    )
}

internal fun Double.formatScore(): String = "%.1f".format(coerceIn(0.0, 100.0))

internal fun Double.formatPoints(): String = "%.1f".format(coerceAtLeast(0.0))

internal fun DraftFormat.previous(): DraftFormat {
    val entries = DraftFormat.entries
    return entries[(ordinal - 1 + entries.size) % entries.size]
}

internal fun DraftFormat.next(): DraftFormat {
    val entries = DraftFormat.entries
    return entries[(ordinal + 1) % entries.size]
}

internal fun isValidInviteCode(code: String): Boolean =
    code.matches(Regex("[A-Z2-9]{6}"))

internal fun sampleDraftOrder(league: LeagueUi): List<String> =
    List(league.memberCount.coerceAtLeast(1)) { index ->
        if (index == 0) "You" else "Team ${index + 1}"
    }.let { order ->
        if (league.settings.draftFormat == DraftFormat.Snake && league.currentPickIndex / order.size % 2 == 1) {
            order.reversed()
        } else {
            order
        }
    }

internal fun currentDraftPicker(
    league: LeagueUi,
    members: List<LeagueMemberUi>? = null,
    account: AccountUi? = null
): String {
    val username = currentDraftPickerUsername(league, members)
    return if (username != null && username.equals(account?.username.orEmpty(), ignoreCase = true)) {
        "You"
    } else {
        username ?: "Team ${league.currentPickIndex + 1}"
    }
}

internal fun isAccountOnClock(league: LeagueUi, members: List<LeagueMemberUi>?, account: AccountUi?): Boolean =
    currentDraftPickerUsername(league, members)?.equals(account?.username.orEmpty(), ignoreCase = true) == true

internal fun isMemberAutoPickEnabled(members: List<LeagueMemberUi>?, account: AccountUi?, league: LeagueUi): Boolean =
    members
        ?.firstOrNull { it.username.equals(account?.username.orEmpty(), ignoreCase = true) }
        ?.autoPickEnabled
        ?: league.autoPickEnabled

internal fun currentDraftPickerUsername(league: LeagueUi, members: List<LeagueMemberUi>?): String? {
    return draftPickerUsernameAt(league, members, league.currentPickIndex)
}

internal fun scheduleForUser(members: List<String>, username: String, seasonWeeks: Int): List<MatchupWeekUi> =
    (1..seasonWeeks.coerceAtLeast(1)).map { week -> matchupForWeek(members, username, week) ?: MatchupWeekUi(week, null, false) }

internal fun rosterForMemberName(username: String, draftPicks: List<DraftPickUi>): Map<RosterSlot, ArtistUi> =
    draftPicks
        .filter { it.pickedBy.equals(username, ignoreCase = true) }
        .associate { it.slot to it.artist }

internal fun matchupPairsForWeek(members: List<String>, week: Int): List<Pair<String, String?>> {
    val cleanMembers = members
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() }
    if (cleanMembers.isEmpty()) return emptyList()
    val byeMarker = "__BYE__"
    val rotationMembers = if (cleanMembers.size % 2 == 1) cleanMembers + byeMarker else cleanMembers
    if (rotationMembers.size < 2) return emptyList()
    val roundsPerCycle = rotationMembers.size - 1
    val roundIndex = (week - 1).floorMod(roundsPerCycle.coerceAtLeast(1))
    val rotated = buildList {
        add(rotationMembers.first())
        val rotating = rotationMembers.drop(1)
        rotating.indices.forEach { index ->
            add(rotating[(index - roundIndex).floorMod(rotating.size)])
        }
    }
    return rotated
        .take(rotationMembers.size / 2)
        .zip(rotated.takeLast(rotationMembers.size / 2).reversed())
        .mapNotNull { (left, right) ->
            when {
                left == byeMarker && right == byeMarker -> null
                left == byeMarker -> right to null
                right == byeMarker -> left to null
                else -> left to right
            }
        }
}

internal fun matchupScheduleWarnings(memberCount: Int, seasonWeeks: Int): List<String> {
    if (memberCount < 2) return emptyList()
    val weeksNeededForEveryoneOnce = if (memberCount % 2 == 0) memberCount - 1 else memberCount
    return buildList {
        if (seasonWeeks < weeksNeededForEveryoneOnce) {
            add("With $seasonWeeks weeks, some members will not face every opponent. Use at least $weeksNeededForEveryoneOnce weeks for everyone to meet once.")
        }
        if (memberCount % 2 == 1) {
            add("Odd member counts create rotating bye weeks, so one member sits out each scoring week.")
        }
    }
}

internal fun matchupForWeek(members: List<String>, username: String, week: Int): MatchupWeekUi? {
    val cleanMembers = members
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinctBy { it.lowercase() }
    if (cleanMembers.size < 2 || username.isBlank()) return null
    val byeMarker = "__BYE__"
    val rotationMembers = if (cleanMembers.size % 2 == 1) cleanMembers + byeMarker else cleanMembers
    val roundsPerCycle = rotationMembers.size - 1
    val roundIndex = (week - 1).floorMod(roundsPerCycle.coerceAtLeast(1))
    val rotated = buildList {
        add(rotationMembers.first())
        val rotating = rotationMembers.drop(1)
        rotating.indices.forEach { index ->
            add(rotating[(index - roundIndex).floorMod(rotating.size)])
        }
    }
    val pairs = rotated.take(rotationMembers.size / 2).zip(rotated.takeLast(rotationMembers.size / 2).reversed())
    val matchup = pairs.firstOrNull { (left, right) ->
        left.equals(username, ignoreCase = true) || right.equals(username, ignoreCase = true)
    } ?: return MatchupWeekUi(week, null, false)
    val opponent = if (matchup.first.equals(username, ignoreCase = true)) matchup.second else matchup.first
    return if (opponent == byeMarker) {
        MatchupWeekUi(week, null, true)
    } else {
        MatchupWeekUi(week, opponent, false)
    }
}

internal fun Int.floorMod(modulus: Int): Int =
    ((this % modulus) + modulus) % modulus

internal fun draftPickerLabelAt(
    league: LeagueUi,
    members: List<LeagueMemberUi>?,
    account: AccountUi?,
    pickIndex: Int
): String {
    val username = draftPickerUsernameAt(league, members, pickIndex)
    return if (username != null && username.equals(account?.username.orEmpty(), ignoreCase = true)) {
        "You"
    } else {
        username ?: "Member ${pickIndex + 1}"
    }
}

internal fun draftPickerUsernameAt(league: LeagueUi, members: List<LeagueMemberUi>?, pickIndex: Int): String? {
    val memberCount = league.memberCount.coerceAtLeast(1)
    val baseOrder = members
        ?.takeIf { it.isNotEmpty() }
        ?.map { it.username }
        ?.filter { it.isNotBlank() }
        ?.take(memberCount)
        ?.let { names -> names + List((memberCount - names.size).coerceAtLeast(0)) { index -> "Member ${names.size + index + 1}" } }
        ?: List(memberCount) { index -> "Member ${index + 1}" }
    val roundIndex = pickIndex / memberCount
    val order = if (league.settings.draftFormat == DraftFormat.Snake && roundIndex % 2 == 1) {
        baseOrder.reversed()
    } else {
        baseOrder
    }
    return order.getOrNull(pickIndex % memberCount)
}

internal suspend fun bestEligibleDraftPick(
    roster: Map<RosterSlot, ArtistUi>,
    league: LeagueUi,
    unavailableArtistNames: Set<String> = emptySet()
): ArtistUi? = coroutineScope {
    val headliners = async { MusicArtistService.headlinerCandidates() }
    val rising = async { MusicArtistService.risingCandidates() }
    val deepCuts = async { MusicArtistService.deepCutCandidates() }
    val wildcards = async { MusicArtistService.wildcardCandidates() }
    (headliners.await() + rising.await() + deepCuts.await() + wildcards.await())
        .distinctBy { it.name.lowercase() }
        .strategicDraftRecommendation(
            roster = roster,
            settings = league.settings,
            currentPickIndex = league.currentPickIndex,
            memberCount = league.memberCount,
            snapshots = emptyMap(),
            unavailableArtistNames = unavailableArtistNames
        )
}

internal fun List<ArtistUi>.strategicDraftRecommendation(
    roster: Map<RosterSlot, ArtistUi>,
    settings: LeagueSettingsUi,
    currentPickIndex: Int,
    memberCount: Int,
    snapshots: Map<String, SnapshotUi>,
    unavailableArtistNames: Set<String> = emptySet()
): ArtistUi? {
    val openSlots = activeRosterSlots(settings).filter { roster[it] == null }
    if (openSlots.isEmpty()) return null
    val totalSlots = activeRosterSlots(settings).size.coerceAtLeast(1)
    val totalRounds = kotlin.math.ceil(totalSlots.toDouble() / memberCount.coerceAtLeast(1).toDouble()).toInt().coerceAtLeast(1)
    val currentRound = (currentPickIndex / memberCount.coerceAtLeast(1)) + 1
    val draftProgress = (currentRound - 1).toDouble() / (totalRounds - 1).coerceAtLeast(1).toDouble()
    val rosterProgress = roster.size.toDouble() / totalSlots.toDouble()
    val riskTolerance = ((draftProgress * 0.65) + (rosterProgress * 0.35)).coerceIn(0.0, 1.0)
    return distinctBy { it.name.lowercase() }
        .filter { artist ->
            artist.name.lowercase() !in unavailableArtistNames &&
                roster.values.none { it.name.equals(artist.name, ignoreCase = true) } &&
                firstOpenSlotFor(artist, roster, settings) != null
        }
        .maxByOrNull { artist ->
            val compatibleOpenSlots = openSlots.filter { slot -> slot.canHold(artist) }
            val fillsStartingSlot = compatibleOpenSlots.any { !it.isBenchSlot() }
            val slotNeedScore = when {
                compatibleOpenSlots.isEmpty() -> 0.0
                fillsStartingSlot -> 26.0 / compatibleOpenSlots.size.coerceAtLeast(1)
                else -> 7.0 / compatibleOpenSlots.size.coerceAtLeast(1)
            }
            val audienceFloor = normalizedAudienceFloor(artist.listeners)
            val breakout = artist.breakoutScore(snapshots[artist.name.lowercase()])
            val trackSignal = (artist.trackPopularity ?: artist.spotifyPopularity ?: 45).coerceIn(0, 100).toDouble()
            val artistSignal = (artist.spotifyPopularity ?: artist.trackPopularity ?: 45).coerceIn(0, 100).toDouble()
            val recency = (artist.releaseRecencyScore ?: 40.0).coerceIn(0.0, 100.0)
            val risk = artist.riskScore()
            val safetyWeight = 1.0 - riskTolerance
            val upsideWeight = 0.65 + riskTolerance
            val riskPenalty = risk * (0.45 + safetyWeight * 0.45)

            (artist.projectedScore * 0.60) +
                (breakout * 0.28 * upsideWeight) +
                (trackSignal * 0.10) +
                (artistSignal * 0.08) +
                (recency * 0.05 * upsideWeight) +
                (audienceFloor * 18.0 * safetyWeight) +
                slotNeedScore -
                riskPenalty
        }
}

internal fun normalizedAudienceFloor(listeners: Long?): Double {
    val scale = listeners ?: return 0.0
    val normalized = ((log10(max(scale.toDouble(), 100_000.0)) - 5.0) / 3.2).coerceIn(0.0, 1.0)
    return normalized.pow(1.25)
}

internal fun ArtistUi.riskScore(): Double {
    val scale = listeners ?: return 45.0
    val scaleRisk = when {
        scale >= 8_000_000 -> 12.0
        scale >= 2_500_000 -> 24.0
        scale >= 750_000 -> 42.0
        else -> 58.0
    }
    val signal = (trackPopularity ?: spotifyPopularity ?: 50).coerceIn(0, 100)
    val signalRelief = (signal - 50).coerceAtLeast(0) * 0.20
    return (scaleRisk - signalRelief).coerceIn(8.0, 70.0)
}

internal fun RosterSlot.isBenchSlot(): Boolean = name.startsWith("Bench")

internal fun projectedDraftNeeds(settings: LeagueSettingsUi, roster: Map<RosterSlot, ArtistUi>): List<String> {
    val slots = activeRosterSlots(settings)
    return slots
        .filter { roster[it] == null }
        .take(5)
        .map { slot -> "${slot.label} fit" }
        .ifEmpty { listOf("Roster filled") }
}

internal fun weeklyPointsForArtist(artist: ArtistUi, league: LeagueUi, weekOffset: Int = 0): List<ArtistWeekPointsUi> {
    val weekCount = league.settings.seasonWeeks.coerceAtLeast(1)
    val currentWeek = currentLeagueWeek(league, weekOffset)
    return (1..weekCount).map { week ->
        ArtistWeekPointsUi(
            week = week,
            points = if (league.draftStatus == DraftStatus.Complete && week <= currentWeek) artist.actualWeekScore(week) else null,
            isCurrent = league.draftStatus == DraftStatus.Complete && week == currentWeek
        )
    }
}

internal fun currentLeagueWeek(league: LeagueUi, weekOffset: Int = 0): Int {
    val weekOneStart = leagueWeekOneStartDate(league) ?: return 1
    val daysSinceWeekOne = ChronoUnit.DAYS.between(weekOneStart, LocalDate.now()).coerceAtLeast(0)
    return (((daysSinceWeekOne / 7) + 1).toInt() + weekOffset).coerceIn(1, league.settings.seasonWeeks.coerceAtLeast(1))
}

internal fun completedLeagueWeeks(league: LeagueUi, weekOffset: Int = 0): Int {
    if (league.draftStatus != DraftStatus.Complete) return 0
    val currentWeek = currentLeagueWeek(league, weekOffset)
    return (currentWeek - 1).coerceIn(0, league.settings.seasonWeeks.coerceAtLeast(1))
}

internal fun isRosterNaturallyLockedForScoring(league: LeagueUi, today: LocalDate = LocalDate.now()): Boolean {
    if (league.draftStatus != DraftStatus.Complete) return false
    val weekOneStart = leagueWeekOneStartDate(league) ?: return false
    if (today.isBefore(weekOneStart)) return false
    return today.dayOfWeek in DayOfWeek.MONDAY..DayOfWeek.FRIDAY
}

internal fun leagueWeekOneStartDate(league: LeagueUi): LocalDate? {
    val scheduledAt = parseDraftDateTime(league.settings.draftDateLabel) ?: return null
    return scheduledAt.toLocalDate()
        .plusDays(1)
        .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
}

internal fun ArtistUi.leagueWeekScore(week: Int): Double {
    return actualWeekScore(week)
}

internal fun ArtistUi.projectedWeekScore(week: Int): Double {
    val breakout = breakoutScore(null)
    val audienceMove = kworbDailyListenerChange?.let { signedLogSignal(it) } ?: normalizedAudienceFloor(listeners) * 100.0
    val weeklyGrowth = weeklyListenerGrowthPercent?.let { (40.0 + it.coerceIn(-30.0, 220.0) * 0.24).coerceIn(10.0, 104.0) } ?: audienceMove
    val weeklyGain = weeklyListenerGain?.let { signedLogSignal(it) } ?: audienceMove
    val streams = kworbDailyStreams?.let { dailyStreamSignal(it, listeners) } ?: ((trackPopularity ?: spotifyPopularity ?: 50).toDouble())
    val roleStability = when (tag) {
        "Headliner" -> 8.0
        "Mainstay" -> 5.0
        "Rising" -> 2.5
        "Deep Cut" -> -1.0
        else -> 0.0
    }
    val raw = (breakout * 0.34) +
        (weeklyGrowth * 0.20) +
        (weeklyGain * 0.12) +
        (audienceMove * 0.18) +
        (streams * 0.10) +
        (projectedScore * 0.06) +
        roleStability
    val audienceFloor = normalizedAudienceFloor(listeners) * 100.0
    val projectionFloor = when (tag) {
        "Headliner" -> 38.0 + audienceFloor * 0.16
        "Mainstay" -> 24.0 + audienceFloor * 0.14
        "Rising" -> 13.0 + audienceFloor * 0.12
        "Deep Cut" -> 5.0 + audienceFloor * 0.10
        else -> 8.0
    }
    return max(raw, projectionFloor).coerceIn(0.0, 100.0)
}

internal fun ArtistUi.actualWeekScore(week: Int): Double {
    val identitySeed = name.artistKey().fold(0) { acc, char -> (acc * 31) + char.code }
    val wave = (((identitySeed + week * 37) % 29) - 14) / 100.0
    val secondWave = (((identitySeed / 7 + week * 53) % 23) - 11) / 100.0
    val breakout = breakoutScore(null)
    val audienceMove = kworbDailyListenerChange?.let { signedLogSignal(it) } ?: normalizedAudienceFloor(listeners) * 100.0
    val weeklyGrowth = weeklyListenerGrowthPercent?.let { (42.0 + it.coerceIn(-30.0, 220.0) * 0.30).coerceIn(12.0, 108.0) } ?: audienceMove
    val weeklyGain = weeklyListenerGain?.let { signedLogSignal(it) } ?: audienceMove
    val streams = kworbDailyStreams?.let { dailyStreamSignal(it, listeners) } ?: ((trackPopularity ?: spotifyPopularity ?: 50).toDouble())
    val riskSwing = when (riskLabel) {
        "High variance" -> wave * 38.0 + secondWave * 12.0
        "Medium variance" -> wave * 27.0 + secondWave * 8.0
        else -> wave * 18.0 + secondWave * 5.0
    }
    val raw = ((breakout * 0.30) + (weeklyGrowth * 0.25) + (weeklyGain * 0.16) + (audienceMove * 0.12) + (streams * 0.11) + (projectedScore * 0.06) + riskSwing)
    val audienceFloor = normalizedAudienceFloor(listeners) * 100.0
    val consistencyFloor = when (tag) {
        "Headliner" -> 35.0 + audienceFloor * 0.14
        "Mainstay" -> 23.0 + audienceFloor * 0.12
        "Rising" -> 12.0 + audienceFloor * 0.10
        "Deep Cut" -> 4.0 + audienceFloor * 0.08
        else -> 12.0
    }
    return max(raw, consistencyFloor).coerceIn(0.0, 100.0)
}

internal fun waiverOrderPreview(memberCount: Int): List<String> =
    List(memberCount.coerceAtLeast(1)) { index ->
        if (index == 0) "You" else "Team ${index + 1}"
    }

internal fun String.nextDraftDateLabel(): String = when (this) {
    "Set date" -> "Tonight"
    "Tonight" -> "Tomorrow"
    "Tomorrow" -> "This weekend"
    "This weekend" -> "Next week"
    else -> "Set date"
}

internal fun generateInviteCode(): String =
    "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".let { characters ->
        List(6) { characters[Random.nextInt(characters.length)] }.joinToString("")
    }

internal fun String.artistKey(): String =
    lowercase()
        .replace("&amp;", "&")
        .replace(Regex("[^a-z0-9]+"), "")

internal fun String.wordsForSearch(): List<String> =
    lowercase()
        .replace(Regex("[^a-z0-9\\s]+"), " ")
        .split(Regex("\\s+"))
        .filter { it.length >= 2 }

internal fun String.searchMatchScore(query: String): Double {
    val nameKey = artistKey()
    val queryKey = query.artistKey()
    val normalizedNameKey = removeLeadingArticle().artistKey()
    val normalizedQueryKey = query.removeLeadingArticle().artistKey()
    if (queryKey.isBlank()) return 0.0
    if (nameKey == queryKey || normalizedNameKey == normalizedQueryKey || normalizedNameKey == queryKey) return 1_000.0
    val nameWords = wordsForSearch()
    val queryWords = query.wordsForSearch()
    val startsBonus = if (nameKey.startsWith(queryKey) || normalizedNameKey.startsWith(queryKey)) 320.0 else 0.0
    val containsBonus = if (nameKey.contains(queryKey) || normalizedNameKey.contains(queryKey)) 220.0 else 0.0
    val wordBonus = queryWords.sumOf { queryWord ->
        when {
            nameWords.any { it == queryWord } -> 80.0
            nameWords.any { it.startsWith(queryWord) || queryWord.startsWith(it) } -> 45.0
            nameWords.any { it.levenshteinDistance(queryWord) <= 1 } -> 34.0
            else -> 0.0
        }
    }
    val distance = nameKey.levenshteinDistance(queryKey)
    val maxLength = max(nameKey.length, queryKey.length).coerceAtLeast(1)
    val fuzzy = (1.0 - distance.toDouble() / maxLength.toDouble()).coerceIn(0.0, 1.0) * 180.0
    val lengthPenalty = kotlin.math.abs(nameKey.length - queryKey.length) * 1.2
    return startsBonus + containsBonus + wordBonus + fuzzy - lengthPenalty
}

internal fun String.removeLeadingArticle(): String =
    trim().replace(Regex("^(the|a|an)\\s+", RegexOption.IGNORE_CASE), "")

internal fun String.levenshteinDistance(other: String): Int {
    if (this == other) return 0
    if (isEmpty()) return other.length
    if (other.isEmpty()) return length
    val previous = IntArray(other.length + 1) { it }
    val current = IntArray(other.length + 1)
    for (i in indices) {
        current[0] = i + 1
        for (j in other.indices) {
            val cost = if (this[i] == other[j]) 0 else 1
            current[j + 1] = minOf(
                current[j] + 1,
                previous[j + 1] + 1,
                previous[j] + cost
            )
        }
        for (j in previous.indices) previous[j] = current[j]
    }
    return previous[other.length]
}

internal fun String.cleanHtml(separator: String = ""): String =
    replace(Regex("<[^>]+>"), separator)
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&#039;", "'")
        .replace("&apos;", "'")
        .replace("&nbsp;", " ")
        .replace(Regex("\\s+"), " ")
        .trim()

internal fun Long.formatCompact(): String = when {
    this >= 1_000_000_000 -> "${"%.1f".format(this / 1_000_000_000.0)}B"
    this >= 1_000_000 -> "${"%.1f".format(this / 1_000_000.0)}M"
    this >= 1_000 -> "${"%.1f".format(this / 1_000.0)}K"
    else -> toString()
}

internal fun Long.formatSignedCompact(): String =
    if (this >= 0) "+${formatCompact()}" else "-${kotlin.math.abs(this).formatCompact()}"

internal fun String.displayReleaseDate(): String =
    runCatching {
        val date = when (length) {
            4 -> LocalDate.of(toInt(), 1, 1)
            7 -> LocalDate.parse("$this-01")
            else -> LocalDate.parse(take(10))
        }
        DateTimeFormatter.ofPattern("MMM d, yyyy").format(date)
    }.getOrDefault(this)

internal fun String.displayKworbDate(): String =
    runCatching {
        DateTimeFormatter.ofPattern("MMM d, yyyy")
            .format(LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy/MM/dd")))
    }.getOrElse {
        runCatching {
            OffsetDateTime.parse(this)
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a"))
        }.getOrDefault(take(10))
    }

internal fun Long.formatLocalDateTime(): String =
    runCatching {
        DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
            .format(Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()))
    }.getOrDefault("Recently")

internal fun String.parseCompactLong(): Long? {
    val clean = trim().replace(",", "")
    val match = Regex("""([+-]?\d+(?:\.\d+)?)\s*([KMB])?""", RegexOption.IGNORE_CASE).find(clean) ?: return null
    val number = match.groupValues.getOrNull(1)?.toDoubleOrNull() ?: return null
    val multiplier = when (match.groupValues.getOrNull(2)?.uppercase()) {
        "K" -> 1_000.0
        "M" -> 1_000_000.0
        "B" -> 1_000_000_000.0
        else -> 1.0
    }
    return (number * multiplier).toLong()
}

@Preview(showBackground = true, widthDp = 360)
@Composable
internal fun LeagueSetupPreview() {
    BreakoutTheme(darkTheme = true) {
        LeagueSetupScreen(joinError = null, onClearError = {}, onCreateLeague = {}, onJoinLeague = {})
    }
}

@Preview(showBackground = true, widthDp = 412, fontScale = 1.3f)
@Composable
internal fun MarketPreview() {
    BreakoutTheme(darkTheme = true) {
        val previewListState = rememberLazyListState()
        var previewQuery by rememberSaveable { mutableStateOf("") }
        var previewFilter by rememberSaveable { mutableStateOf<MarketFilter?>(MarketFilter.Headliners) }
        var previewSubmittedSearch by rememberSaveable { mutableStateOf("") }
        var previewPreviousFilter by rememberSaveable { mutableStateOf(MarketFilter.Headliners) }
        var previewMarketState by remember { mutableStateOf<MarketState>(MarketState.Loading) }
        var previewSnapshots by remember { mutableStateOf<Map<String, SnapshotUi>>(emptyMap()) }
        var previewVisibleCount by rememberSaveable { mutableStateOf(20) }
        var previewLastKey by rememberSaveable { mutableStateOf<String?>(null) }
        var previewLoadedKey by rememberSaveable { mutableStateOf<String?>(null) }
        MarketScreen(
            roster = emptyMap(),
            draftPicks = emptyList(),
            droppedArtistNames = emptySet(),
            waiverQueuedNames = emptySet(),
            leagueSettings = LeagueSettingsUi(),
            draftStatus = DraftStatus.Scheduled,
            draftPickMode = false,
            canMakeDraftPick = false,
            showExpiredFeedback = false,
            currentPickIndex = 0,
            memberCount = 2,
            startFilter = MarketFilter.Headliners,
            query = previewQuery,
            onQueryChange = { previewQuery = it },
            submittedSearch = previewSubmittedSearch,
            onSubmittedSearchChange = { previewSubmittedSearch = it },
            activeFilter = previewFilter,
            onActiveFilterChange = { previewFilter = it },
            previousFilter = previewPreviousFilter,
            onPreviousFilterChange = { previewPreviousFilter = it },
            marketState = previewMarketState,
            onMarketStateChange = { previewMarketState = it },
            searchResultCache = emptyMap(),
            onCacheSearchResults = { _, _ -> },
            onClearSearchResults = {},
            snapshots = previewSnapshots,
            onSnapshotsChange = { previewSnapshots = it },
            visibleCount = previewVisibleCount,
            onVisibleCountChange = { previewVisibleCount = it },
            resetTick = 0,
            lastMarketKey = previewLastKey,
            onLastMarketKeyChange = { previewLastKey = it },
            loadedMarketKey = previewLoadedKey,
            onLoadedMarketKeyChange = { previewLoadedKey = it },
            openActionArtistKey = null,
            onOpenActionArtistKeyChange = {},
            listState = previewListState,
            preloadedArtists = null,
            refreshing = false,
            onRefresh = {},
            onOpenMenu = {},
            onArtistSelected = {},
            onToggleArtist = {},
            onRemoveRosterArtist = {},
            onQueueWaiverArtist = { _, _ -> },
            onCancelWaiverArtist = {},
            onPickExpired = {}
        )
    }
}
