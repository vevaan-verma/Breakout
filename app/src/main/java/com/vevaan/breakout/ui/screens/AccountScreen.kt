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
internal fun AccountScreen(
    account: AccountUi?,
    devModeAllowed: Boolean = false,
    spotlightModeAllowed: Boolean = false,
    devModeEnabled: Boolean = false,
    spotlightModeEnabled: Boolean = false,
    onDevModeChange: (Boolean) -> Unit = {},
    onSpotlightModeChange: (Boolean) -> Unit = {},
    onOpenMenu: () -> Unit,
    onSaveAccount: (AccountUi, (String?) -> Unit) -> Unit,
    onDeleteAccount: ((String?) -> Unit) -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    var email by rememberSaveable(account?.email.orEmpty()) { mutableStateOf(account?.email.orEmpty()) }
    var username by rememberSaveable(account?.username.orEmpty()) { mutableStateOf(account?.username.orEmpty()) }
    var mailingList by rememberSaveable(account?.mailingList ?: true) { mutableStateOf(account?.mailingList ?: true) }
    var accountError by rememberSaveable { mutableStateOf<String?>(null) }
    var accountStatus by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by rememberSaveable { mutableStateOf(false) }
    var deleting by rememberSaveable { mutableStateOf(false) }
    var confirmSignOut by rememberSaveable { mutableStateOf(false) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    var emailUnlockTaps by rememberSaveable { mutableStateOf(0) }
    var usernameUnlockTaps by rememberSaveable { mutableStateOf(0) }
    var confirmDisableDevMode by rememberSaveable { mutableStateOf(false) }
    var confirmDisableSpotlightMode by rememberSaveable { mutableStateOf(false) }
    val canSave = email.trim().contains("@") && isValidUsername(username.trim()) && !saving

    ScreenColumn(
        stickyTopBar = { TopTitle(title = "Account", subtitle = account?.label ?: "Signed out", onMenuClick = onOpenMenu) }
    ) {
        ScreenHero(
            eyebrow = "Manager Profile",
            title = username.ifBlank { "Account" },
            subtitle = "Keep your sign-in, display name, and notification preferences current.",
            stats = listOf(
                Triple("Email", if (email.isBlank()) "--" else "Set", "Sign-in"),
                Triple("Mailing", if (mailingList) "On" else "Off", "Updates"),
                Triple("Username", if (isValidUsername(username.trim())) "Valid" else "Check", "Display"),
                Triple("Session", if (account?.accessToken?.isNotBlank() == true) "Active" else "Local", "Login")
            ),
            accent = BreakoutSecondary,
            onStatTap = { label ->
                when (label) {
                    "Email" -> {
                        if (devModeAllowed && !devModeEnabled) {
                            emailUnlockTaps += 1
                            if (emailUnlockTaps >= 10) {
                                emailUnlockTaps = 0
                                onDevModeChange(true)
                                Toast.makeText(context, "Developer mode toggled on.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    "Username" -> {
                        if (spotlightModeAllowed && !spotlightModeEnabled) {
                            usernameUnlockTaps += 1
                            if (usernameUnlockTaps >= 10) {
                                usernameUnlockTaps = 0
                                onSpotlightModeChange(true)
                                Toast.makeText(context, "Spotlight mode toggled on.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        )
        BreakoutCard {
            Text("Profile", style = MaterialTheme.typography.titleLarge)
            StyledTextField(
                value = email,
                onValueChange = {
                    email = it
                    accountError = null
                    accountStatus = null
                },
                label = "Email",
                maxLength = MaxEmailLength,
                keyboardType = KeyboardType.Email
            )
            StyledTextField(
                value = username,
                onValueChange = {
                    username = it.cleanUsernameInput()
                    accountError = null
                    accountStatus = null
                },
                label = "Username",
                maxLength = MaxUsernameLength
            )
            if (username.isNotBlank() && !isValidUsername(username.trim())) {
                AnimatedFeedbackText(message = usernameValidationMessage(username.trim()), color = BreakoutCoral)
            }
            ToggleRow(
                label = "Mailing List",
                value = if (mailingList) "Subscribed" else "Off",
                enabled = true,
                onToggle = {
                    mailingList = !mailingList
                    accountError = null
                    accountStatus = null
                }
            )
            PrimaryButton(
                text = if (saving) "Saving" else "Save Account",
                enabled = canSave,
                onClick = {
                    saving = true
                    accountError = null
                    accountStatus = null
                    val updatedAccount = AccountUi(
                        email = email.trim(),
                        username = username.trim(),
                        displayName = username.trim(),
                        mailingList = mailingList,
                        accessToken = account?.accessToken.orEmpty(),
                        refreshToken = account?.refreshToken.orEmpty(),
                        userId = account?.userId.orEmpty()
                    )
                    onSaveAccount(updatedAccount) { error ->
                        saving = false
                        if (error == null) {
                            accountStatus = "Account updated."
                        } else {
                            accountError = error
                        }
                    }
                }
            )
            AnimatedFeedbackText(message = accountStatus, color = BreakoutPrimary)
            AnimatedFeedbackText(message = accountError, color = BreakoutCoral)
        }
        BreakoutCard {
            Text("Mailing List", style = MaterialTheme.typography.titleLarge)
            Text(
                "Subscribed accounts can receive league announcements, draft reminders, and product updates.",
                color = BreakoutTextSecondary
            )
            ScoreLine("Your Email", email.trim().ifBlank { "Not set" })
            ScoreLine("Status", if (mailingList) "Subscribed" else "Unsubscribed")
        }
        AnimatedVisibility(
            visible = devModeEnabled || spotlightModeEnabled,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BreakoutCard {
                Text("Breakout Lab", style = MaterialTheme.typography.titleLarge)
                AnimatedVisibility(
                    visible = devModeEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ToggleRow(
                        label = "Developer Mode",
                        value = "On",
                        enabled = devModeAllowed,
                        onToggle = { confirmDisableDevMode = true }
                    )
                }
                AnimatedVisibility(
                    visible = spotlightModeEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ToggleRow(
                        label = "Spotlight Mode",
                        value = "On",
                        enabled = spotlightModeAllowed,
                        onToggle = { confirmDisableSpotlightMode = true }
                    )
                }
            }
        }
        BreakoutCard {
            Text("Account Actions", style = MaterialTheme.typography.titleLarge)
            DangerButton(text = "Log Out", onClick = { confirmSignOut = true })
            DangerButton(text = if (deleting) "Deleting Account" else "Delete Account", onClick = { confirmDelete = true })
        }
        if (confirmDisableDevMode) {
            ConfirmActionCard(
                title = "Turn Off Developer Mode?",
                detail = "Testing utilities will disappear immediately.",
                confirmText = "Turn Off",
                onCancel = { confirmDisableDevMode = false },
                onConfirm = {
                    confirmDisableDevMode = false
                    onDevModeChange(false)
                }
            )
        }
        if (confirmDisableSpotlightMode) {
            ConfirmActionCard(
                title = "Turn Off Spotlight Mode?",
                detail = "Spotlight-only changes will turn off immediately.",
                confirmText = "Turn Off",
                onCancel = { confirmDisableSpotlightMode = false },
                onConfirm = {
                    confirmDisableSpotlightMode = false
                    onSpotlightModeChange(false)
                }
            )
        }
        if (confirmSignOut) {
            ConfirmActionCard(
                title = "Log Out?",
                detail = "You will need to log in again before managing leagues or drafting artists.",
                confirmText = "Log Out",
                onCancel = { confirmSignOut = false },
                onConfirm = {
                    confirmSignOut = false
                    onSignOut()
                }
            )
        }
        if (confirmDelete) {
            ConfirmActionCard(
                title = "Delete Account?",
                detail = "This permanently removes your account, leagues, rosters, and profile data.",
                confirmText = "Delete",
                onCancel = { confirmDelete = false },
                onConfirm = {
                    deleting = true
                    accountError = null
                    accountStatus = null
                    onDeleteAccount { error ->
                        deleting = false
                        confirmDelete = false
                        if (error != null) accountError = error
                    }
                }
            )
        }
    }
}

@Composable
internal fun MemberReviewRow(
    member: LeagueMemberUi,
    currentUsername: String,
    canManage: Boolean,
    onOpen: () -> Unit,
    onTransfer: () -> Unit,
    onKick: () -> Unit
) {
    val isSelf = member.username.equals(currentUsername, ignoreCase = true)
    val accent = if (member.isManager) BreakoutSecondary else BreakoutPrimary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(BreakoutSurfaceVariant.copy(alpha = 0.82f))
            .border(1.dp, accent.copy(alpha = if (member.isManager || isSelf) 0.34f else 0.14f), RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .clickable(onClick = onOpen)
            .padding(BreakoutDimensions.md),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.18f))
                .border(1.dp, accent.copy(alpha = 0.42f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                member.username.take(1).uppercase().ifBlank { "?" },
                color = accent,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(member.username, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                if (member.isManager) "League manager" else "Member",
                color = BreakoutTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (isSelf) {
            Pill("You")
        } else if (member.isManager) {
            Pill("Manager")
        } else if (canManage) {
            Pill("Manage")
        }
    }
}

@Composable
internal fun MemberDetailDialog(
    member: LeagueMemberUi,
    currentUsername: String,
    canManage: Boolean,
    canViewRoster: Boolean,
    onDismiss: () -> Unit,
    onViewRoster: () -> Unit,
    onTrade: () -> Unit,
    onTransfer: () -> Unit,
    onKick: () -> Unit
) {
    val isSelf = member.username.equals(currentUsername, ignoreCase = true)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = BreakoutSurface.copy(alpha = 0.98f),
            shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
            border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.85f)),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 560.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = BreakoutDimensions.xl, vertical = BreakoutDimensions.lg),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)) {
                    Text(member.username, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        listOfNotNull(
                            if (member.isManager) "Manager" else "Member",
                            if (isSelf) "You" else null
                        ).joinToString(" - "),
                        color = BreakoutTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Surface(
                    color = BreakoutSurfaceVariant.copy(alpha = 0.72f),
                    shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                    border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.45f))
                ) {
                    Column(
                        modifier = Modifier.padding(BreakoutDimensions.md),
                        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
                    ) {
                        ScoreLine("Role", if (member.isManager) "Manager" else "Member")
                        ScoreLine("Account", if (isSelf) "You" else "League Member")
                    }
                }
                if (canViewRoster) {
                    Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
                        SecondaryButton(text = "View Roster", modifier = Modifier.weight(1f), onClick = onViewRoster)
                        AccentButton(text = "Trade", modifier = Modifier.weight(1f), onClick = onTrade)
                    }
                }
                if (canManage) {
                    AccentButton(text = "Make Manager", modifier = Modifier.fillMaxWidth(), onClick = onTransfer)
                    DangerButton(text = "Kick Member", onClick = onKick)
                }
                SecondaryButton(text = "Close", modifier = Modifier.fillMaxWidth(), onClick = onDismiss)
            }
        }
    }
}

@Composable
internal fun MemberRosterDialog(
    member: LeagueMemberUi,
    picks: List<DraftPickUi>,
    onDismiss: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit
) {
    val sortedPicks = picks.sortedBy { it.pickNumber }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = BreakoutSurface.copy(alpha = 0.98f),
            shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
            border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.85f)),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 560.dp)
                .heightIn(max = 720.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = BreakoutDimensions.xl, vertical = BreakoutDimensions.lg),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)) {
                        Text("${member.username}'s Roster", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            if (sortedPicks.isEmpty()) "No drafted artists" else "${sortedPicks.size} drafted artists",
                            color = BreakoutTextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    CompactButton("x", onClick = onDismiss)
                }
                if (sortedPicks.isEmpty()) {
                    Surface(
                        color = BreakoutSurfaceVariant.copy(alpha = 0.72f),
                        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                        border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.45f))
                    ) {
                        Text(
                            "This member does not have any drafted artists.",
                            modifier = Modifier.padding(BreakoutDimensions.md),
                            color = BreakoutTextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
                        sortedPicks.forEach { pick ->
                            MemberRosterPickRow(
                                pick = pick,
                                onClick = { onArtistSelected(pick.artist) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun MemberRosterPickRow(pick: DraftPickUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(BreakoutSurfaceVariant.copy(alpha = 0.72f))
            .clickable(onClick = onClick)
            .padding(BreakoutDimensions.md),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArtistArtwork(artist = pick.artist, size = BreakoutDimensions.ArtworkList)
        Column(modifier = Modifier.weight(1f)) {
            Text(pick.artist.displayName(), style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Overall #${pick.pickNumber}", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        TagLabel(pick.artist.tag)
    }
}

@Composable
internal fun DraftedStatusCard(detail: String) {
    BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BreakoutSecondary.copy(alpha = 0.18f))
                    .border(1.dp, BreakoutSecondary.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("D", color = BreakoutSecondary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
            ) {
                Text("Already Drafted", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(detail.removePrefix("Drafted by "), color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
internal fun ArtistOwnershipBanner(
    isInRoster: Boolean,
    isWaiverQueued: Boolean
) {
    val accent = if (isInRoster) BreakoutPrimary else WaiverAccent
    BreakoutCard(
        contentPadding = PaddingValues(BreakoutDimensions.md),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.18f))
                    .border(1.dp, accent.copy(alpha = 0.42f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isInRoster) "+" else "W", color = accent, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (isInRoster) "On Your Roster" else "Waiver Claim Queued",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (isInRoster) "This artist is currently active on your roster." else "This claim is waiting for the next waiver run.",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
internal fun ArtistAvailabilityBanner(detail: String) {
    BreakoutCard(
        contentPadding = PaddingValues(BreakoutDimensions.md),
        border = BorderStroke(1.dp, BreakoutCoral.copy(alpha = 0.48f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BreakoutCoral.copy(alpha = 0.16f))
                    .border(1.dp, BreakoutCoral.copy(alpha = 0.42f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("!", color = BreakoutCoral, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
            ) {
                Text("Waiver Unavailable", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(detail, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
internal fun OverlayBackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(BreakoutSurface.copy(alpha = 0.92f))
            .border(1.dp, BreakoutOutline.copy(alpha = 0.65f), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("<", color = BreakoutPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
    }
}

@Composable
internal fun LatestReleaseCard(
    title: String,
    date: String?,
    type: String?,
    imageUrl: String?
) {
    BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                    .background(Brush.linearGradient(listOf(BreakoutPrimary, BreakoutCoral, BreakoutSecondary))),
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "$title release artwork",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("LP", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)) {
                Text("Latest Release", color = BreakoutSecondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    listOfNotNull(
                        type?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        date?.displayReleaseDate()
                    ).joinToString(" - ").ifBlank { "Release activity" },
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
internal fun TopTrackCard(
    title: String,
    streams: Long?,
    dailyStreams: Long?,
    imageUrl: String?,
    releaseDate: String?
) {
    BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                    .background(Brush.linearGradient(listOf(Color(0xFF3B82F6), BreakoutPrimary))),
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "$title track artwork",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("?", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)) {
                Text("Top Daily Track", color = Color(0xFF7DB7FF), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    listOfNotNull(
                        (dailyStreams ?: streams)?.let { "${it.formatCompact()} daily streams" },
                        releaseDate?.displayReleaseDate()
                    ).joinToString(" - ").ifBlank { "Daily streaming leader" },
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
internal fun ChartSignalsCard(artist: ArtistUi) {
    val hasChartData = artist.kworbRank != null ||
        artist.kworbTotalStreams != null ||
        artist.kworbDailyStreams != null ||
        artist.kworbPeakListeners != null
    if (!hasChartData) return
    BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
        Text("Chart Signals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
            StatTile(
                label = "Listener Rank",
                value = artist.kworbRank?.let { "#$it" } ?: "--",
                caption = artist.kworbPeakListeners?.let { "Peak ${it.formatCompact()}" } ?: "Audience chart",
                modifier = Modifier.weight(1f)
            )
            StatTile(
                label = "Daily Streams",
                value = artist.kworbDailyStreams?.formatCompact() ?: "--",
                caption = artist.kworbLeadDailyStreams?.let { "${it.formatCompact()} as lead artist" } ?: "Artist stream pace",
                modifier = Modifier.weight(1f)
            )
        }
        artist.kworbPeakListeners?.let { peak ->
            ScoreLine("Peak Listeners", peak.formatCompact())
        }
        artist.kworbDataUpdatedAt?.let { updated ->
            ScoreLine("Chart Updated", updated.displayKworbDate())
        }
    }
}

@Composable
internal fun StreamSplitCard(artist: ArtistUi) {
    val total = artist.kworbTotalStreams ?: return
    BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Stream Split", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            artist.kworbDataUpdatedAt?.let {
                Text("Updated ${it.displayKworbDate()}", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelMedium)
            }
        }
        ScoreLine("Total", total.formatCompact())
        ScoreLine("As Lead", artist.kworbLeadStreams?.formatCompact() ?: "--")
        ScoreLine("Solo", artist.kworbSoloStreams?.formatCompact() ?: "--")
        ScoreLine("As Feature", artist.kworbFeatureStreams?.formatCompact() ?: "--")
        Text(
            "Feature totals include credited featured appearances.",
            color = BreakoutTextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
internal fun ArtistHistoryTimelineCard(
    draftedDetail: String?,
    droppedAtMillis: Long?,
    waiveredAtMillis: Long? = null,
    waiveredDetail: String? = null
) {
    val events = buildList {
        draftedDetail?.let { add(Triple("Drafted", it.removePrefix("Drafted by "), BreakoutPrimary)) }
        if (waiveredAtMillis != null || waiveredDetail != null) {
            add(Triple("Waiver Claimed", waiveredDetail ?: "by You via waivers • ${waiveredAtMillis?.formatLocalDateTime().orEmpty()}", WaiverAccent))
        }
        droppedAtMillis?.let { add(Triple("Dropped", it.formatLocalDateTime(), BreakoutCoral)) }
    }
    if (events.isEmpty()) return
    BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
        Text("Roster History", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        events.forEachIndexed { index, event ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(event.third.copy(alpha = 0.18f))
                            .border(1.dp, event.third.copy(alpha = 0.65f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(event.third)
                        )
                    }
                    if (index != events.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .heightIn(min = 34.dp)
                                .background(Color.White.copy(alpha = 0.28f))
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)) {
                    Text(event.first, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        event.second,
                        color = if (event.second.startsWith("You")) WaiverAccent else BreakoutTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (event.second.startsWith("You")) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
internal fun ConfirmActionCard(
    title: String,
    detail: String,
    confirmText: String,
    accent: Color = BreakoutCoral,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = BreakoutDimensions.lg)
                .fillMaxWidth()
                .widthIn(max = 430.dp),
            color = Color(0xFF171B25),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.72f)),
            tonalElevation = 14.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.16f))
                            .border(1.dp, accent.copy(alpha = 0.42f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "!",
                            color = accent,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
                    ) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            detail,
                            color = BreakoutTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)
                ) {
                    SecondaryButton(text = "Cancel", modifier = Modifier.weight(1f), onClick = onCancel)
                    AccentConfirmButton(text = confirmText, accent = accent, modifier = Modifier.weight(1f), onClick = onConfirm)
                }
            }
        }
    }
}

