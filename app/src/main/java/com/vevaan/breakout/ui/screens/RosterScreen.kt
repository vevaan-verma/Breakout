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
internal fun RosterScreen(
    roster: Map<RosterSlot, ArtistUi>,
    waiverClaims: List<WaiverClaimUi>,
    leagueSettings: LeagueSettingsUi,
    memberCount: Int,
    draftStatus: DraftStatus,
    draftRoomContext: Boolean,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit,
    onRemoveArtist: (RosterSlot) -> Unit,
    onMoveArtist: (RosterSlot, RosterSlot) -> Unit,
    onRunWaivers: () -> Unit,
    onCancelWaiver: (WaiverClaimUi) -> Unit,
    onMoveWaiver: (WaiverClaimUi, Int) -> Unit,
    onOpenMarket: (MarketFilter) -> Unit
) {
    val context = LocalContext.current
    val rosterArtists = roster.values.toList()
    var pendingDrop by remember { mutableStateOf<Pair<RosterSlot, ArtistUi>?>(null) }
    var pendingMove by remember { mutableStateOf<Pair<RosterSlot, ArtistUi>?>(null) }
    var pendingFillSlot by remember { mutableStateOf<RosterSlot?>(null) }
    var pendingCancelWaiver by remember { mutableStateOf<WaiverClaimUi?>(null) }
    val rosterListState = rememberLazyListState()
    LaunchedEffect(rosterArtists.joinToString { it.name }) {
        if (rosterArtists.isNotEmpty()) {
            prefetchArtistImages(context, rosterArtists)
        }
    }
    ScreenColumn(
        refreshing = refreshing,
        onRefresh = onRefresh,
        stickyTopBar = {
            val slots = activeRosterSlots(leagueSettings)
            TopTitle(
                title = "Roster",
                subtitle = "${roster.size} of ${slots.size} Filled",
                onMenuClick = onOpenMenu
            )
        }
    ) {
        val slots = activeRosterSlots(leagueSettings)
        if (draftStatus == DraftStatus.Scheduled || draftStatus == DraftStatus.Lobby) {
            StatusCard(
                title = "Roster Locked",
                detail = "Rosters are filled during the live draft."
            )
        }
        LazyColumn(
            state = rosterListState,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 760.dp),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)
        ) {
            items(slots, key = { slot -> slot.name }) { slot ->
                RosterSlotCard(
                    modifier = Modifier.animateItem(),
                    slot = slot,
                    artist = roster[slot],
                    draftStatus = draftStatus,
                    draftRoomContext = draftRoomContext,
                    onArtistSelected = onArtistSelected,
                    onOpenMarket = { onOpenMarket(slot.filter) },
                    canRemove = draftStatus != DraftStatus.Live,
                    canMove = draftStatus == DraftStatus.Complete,
                    onMoveArtist = {
                        roster[slot]?.let { pendingMove = slot to it } ?: run { pendingFillSlot = slot }
                    },
                    onRemoveArtist = { roster[slot]?.let { pendingDrop = slot to it } }
                )
            }
        }
        if (draftStatus == DraftStatus.Complete) {
            BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                    StatTile("Waiver Priority", "#1", "This week", Modifier.weight(1f))
                    StatTile("Open Slots", slots.count { roster[it] == null }.toString(), "Roster space", Modifier.weight(1f))
                }
                if (waiverClaims.isEmpty()) {
                    Text("Queue claims from artist pages when you have a matching open slot.", color = BreakoutTextSecondary)
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((waiverClaims.size * 180).dp),
                        userScrollEnabled = false,
                        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
                    ) {
                        items(waiverClaims, key = { claim -> "${claim.artist.stableListKey()}:${claim.slot.name}" }) { claim ->
                            val index = waiverClaims.indexOf(claim)
                            WaiverClaimRow(
                                modifier = Modifier.animateItem(),
                                priority = index + 1,
                                claim = claim,
                                canMoveUp = index > 0,
                                canMoveDown = index < waiverClaims.lastIndex,
                                onArtistSelected = { onArtistSelected(claim.artist) },
                                onMoveUp = { onMoveWaiver(claim, -1) },
                                onMoveDown = { onMoveWaiver(claim, 1) },
                                onCancel = { pendingCancelWaiver = claim }
                            )
                        }
                    }
                }
            }
            BreakoutCard {
                Text("Waiver Order", style = MaterialTheme.typography.titleLarge)
                waiverOrderPreview(memberCount).forEachIndexed { index, team ->
                    WaiverOrderRow(rank = index + 1, team = team)
                }
            }
        }
        pendingDrop?.let { (slot, artist) ->
            ConfirmActionCard(
                title = "Drop ${artist.name}?",
                detail = "This removes the artist from your roster.",
                confirmText = "Drop",
                onCancel = { pendingDrop = null },
                onConfirm = {
                    pendingDrop = null
                    onRemoveArtist(slot)
                }
            )
        }
        pendingMove?.let { (fromSlot, artist) ->
            val moveTargets = slots.filter { targetSlot ->
                targetSlot != fromSlot &&
                    targetSlot.canHold(artist) &&
                    roster[targetSlot]?.let { fromSlot.canHold(it) } != false
            }
            MoveRosterSlotDialog(
                artist = artist,
                targets = moveTargets,
                roster = roster,
                onDismiss = { pendingMove = null },
                onMove = { target ->
                    pendingMove = null
                    onMoveArtist(fromSlot, target)
                }
            )
        }
        pendingFillSlot?.let { targetSlot ->
            val sourceOptions = roster.entries
                .filter { (sourceSlot, artist) -> sourceSlot != targetSlot && targetSlot.canHold(artist) }
            FillRosterSlotDialog(
                slot = targetSlot,
                options = sourceOptions,
                onDismiss = { pendingFillSlot = null },
                onMove = { source ->
                    pendingFillSlot = null
                    onMoveArtist(source, targetSlot)
                }
            )
        }
        pendingCancelWaiver?.let { claim ->
            ConfirmActionCard(
                title = "Cancel Claim?",
                detail = "This removes ${claim.artist.name} from your waiver queue.",
                confirmText = "Cancel Claim",
                onCancel = { pendingCancelWaiver = null },
                onConfirm = {
                    pendingCancelWaiver = null
                    onCancelWaiver(claim)
                }
            )
        }
    }
}

@Composable
internal fun MoveRosterSlotDialog(
    artist: ArtistUi,
    targets: List<RosterSlot>,
    roster: Map<RosterSlot, ArtistUi>,
    onDismiss: () -> Unit,
    onMove: (RosterSlot) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = BreakoutSurface.copy(alpha = 0.98f),
            shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
            border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.85f)),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BreakoutDimensions.xl),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Text("Move ${artist.name}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Choose a slot this artist can occupy.", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
                if (targets.isEmpty()) {
                    StatusCard("No Valid Slots", "This artist cannot move into any open or swappable slot right now.")
                } else {
                    targets.forEach { slot ->
                        val occupant = roster[slot]
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMove(slot) },
                            color = BreakoutSurfaceVariant.copy(alpha = 0.72f),
                            shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                            border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(BreakoutDimensions.md),
                                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (occupant != null) {
                                    ArtistArtwork(artist = occupant, size = BreakoutDimensions.ArtworkList)
                                } else {
                                    EmptySlotArtwork()
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(slot.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(
                                        occupant?.let { "Swap with ${it.name}" } ?: "Open slot",
                                        color = BreakoutTextSecondary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text("Swap", color = BreakoutPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                SecondaryButton(text = "Close", modifier = Modifier.fillMaxWidth(), onClick = onDismiss)
            }
        }
    }
}

@Composable
internal fun FillRosterSlotDialog(
    slot: RosterSlot,
    options: List<Map.Entry<RosterSlot, ArtistUi>>,
    onDismiss: () -> Unit,
    onMove: (RosterSlot) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = BreakoutSurface.copy(alpha = 0.98f),
            shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
            border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.85f)),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BreakoutDimensions.xl),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Text("Fill ${slot.label}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Move a compatible artist into this open slot.", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
                if (options.isEmpty()) {
                    StatusCard("No Compatible Artists", "No current roster artist can move into this slot.")
                } else {
                    options.forEach { (sourceSlot, artist) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMove(sourceSlot) },
                            color = BreakoutSurfaceVariant.copy(alpha = 0.72f),
                            shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                            border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(BreakoutDimensions.md),
                                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ArtistArtwork(artist = artist, size = BreakoutDimensions.ArtworkList)
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(artist.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("From ${sourceSlot.label}", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Text("Move", color = BreakoutPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                SecondaryButton(text = "Close", modifier = Modifier.fillMaxWidth(), onClick = onDismiss)
            }
        }
    }
}

@Composable
internal fun WaiverClaimRow(
    modifier: Modifier = Modifier,
    priority: Int,
    claim: WaiverClaimUi,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onArtistSelected: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        color = Color(0xFF19171A),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
        border = BorderStroke(
            width = 1.dp,
            color = WaiverAccent.copy(alpha = 0.42f)
        )
    ) {
        Column(
            // Waiver card inner margin and spacing between priority, artist text, reorder controls, and cancel.
            modifier = Modifier.padding(BreakoutDimensions.md),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(52.dp)
                        .height(72.dp)
                        .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                        .background(WaiverAccent.copy(alpha = 0.14f))
                        .border(
                            width = 1.dp,
                            color = WaiverAccent.copy(alpha = 0.36f),
                            shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("#", color = WaiverAccent.copy(alpha = 0.82f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                        Text(priority.toString(), color = WaiverAccent, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                        .clickable(onClick = onArtistSelected)
                        .padding(vertical = BreakoutDimensions.xs),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = claim.artist.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${claim.slot.label} claim",
                        color = BreakoutTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Pending waiver",
                        color = WaiverAccent,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                RosterActionIcon(
                    text = "X",
                    accent = BreakoutCoral,
                    size = 46.dp,
                    onClick = onCancel
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
            ) {
                WaiverReorderButton(
                    text = "↑ Move Up",
                    enabled = canMoveUp,
                    modifier = Modifier.weight(1f),
                    onClick = onMoveUp
                )
                WaiverReorderButton(
                    text = "Move Down ↓",
                    enabled = canMoveDown,
                    modifier = Modifier.weight(1f),
                    onClick = onMoveDown
                )
            }
        }
    }
}

@Composable
private fun WaiverReorderButton(
    text: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(999.dp))
            .clickable(enabled = enabled, onClick = onClick),
        color = if (enabled) BreakoutSurfaceVariant else BreakoutSurfaceVariant.copy(alpha = 0.42f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, if (enabled) BreakoutPrimary.copy(alpha = 0.42f) else BreakoutOutline.copy(alpha = 0.25f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (enabled) BreakoutPrimary else BreakoutTextSecondary.copy(alpha = 0.55f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
internal fun WaiverOrderRow(rank: Int, team: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(if (rank == 1) WaiverAccent.copy(alpha = 0.12f) else BreakoutSurfaceVariant.copy(alpha = 0.52f))
            .border(
                1.dp,
                if (rank == 1) WaiverAccent.copy(alpha = 0.34f) else BreakoutOutline.copy(alpha = 0.28f),
                RoundedCornerShape(BreakoutDimensions.SmallCornerRadius)
            )
            .padding(horizontal = BreakoutDimensions.md, vertical = BreakoutDimensions.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#$rank", color = if (rank == 1) WaiverAccent else BreakoutSecondary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(team, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

