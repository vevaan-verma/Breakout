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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ScreenColumn(
    refreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
    stickyTopBar: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    var pullDistance by remember { mutableStateOf(0f) }
    var refreshTriggered by remember { mutableStateOf(false) }
    val refreshThreshold = 148f
    val canPullRefresh = onRefresh != null
    val pullOffset by animateFloatAsState(
        targetValue = when {
            pullDistance > 0f -> pullDistance * 0.32f
            else -> 0f
        },
        label = "pullOffset"
    )

    LaunchedEffect(refreshing) {
        if (!refreshing) refreshTriggered = false
    }

    val nestedScrollConnection = remember(canPullRefresh, refreshing, listState) {
        object : NestedScrollConnection {
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                val atTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
                if (canPullRefresh && !refreshing && !refreshTriggered && atTop && available.y > 0f && source == NestedScrollSource.UserInput) {
                    pullDistance = (pullDistance + available.y).coerceAtMost(refreshThreshold * 1.45f)
                } else if (available.y < 0f) {
                    pullDistance = (pullDistance + available.y).coerceAtLeast(0f)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (canPullRefresh && !refreshing && !refreshTriggered && pullDistance >= refreshThreshold) {
                    refreshTriggered = true
                    onRefresh?.invoke()
                }
                pullDistance = 0f
                return Velocity.Zero
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .nestedScroll(nestedScrollConnection)
                .graphicsLayer { translationY = pullOffset },
            state = listState,
            contentPadding = PaddingValues(
                start = BreakoutDimensions.ScreenHorizontalPadding,
                top = BreakoutDimensions.xs,
                end = BreakoutDimensions.ScreenHorizontalPadding,
                bottom = BreakoutDimensions.SectionSpacing + BreakoutDimensions.MinimumTouchTarget
            ),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)
        ) {
            if (stickyTopBar != null) {
                stickyHeader {
                    Surface(
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.98f),
                        shadowElevation = 6.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = BreakoutDimensions.sm)
                        ) {
                            stickyTopBar()
                        }
                    }
                }
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing),
                    content = content
                )
            }
        }
        AnimatedVisibility(
            visible = refreshing || pullDistance > 12f,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = BreakoutDimensions.sm)
                .graphicsLayer { translationY = if (pullDistance > 0f) pullOffset * 0.35f else 0f }
        ) {
            Surface(
                color = BreakoutSurface.copy(alpha = 0.96f),
                shape = CircleShape,
                border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.5f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(BreakoutDimensions.sm)
                        .size(22.dp),
                    color = BreakoutPrimary,
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
internal fun AuthCheckingScreen() {
    AppStartupLoadingScreen(progress = 0.32f, message = "Checking your account")
}

@Composable
internal fun AppStartupLoadingScreen(
    progress: Float,
    message: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "appStartupProgress"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(BreakoutDimensions.ScreenHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.lg)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(WaiverAccent.copy(alpha = 0.24f), BreakoutPrimary.copy(alpha = 0.28f), BreakoutSurfaceVariant)
                            )
                        )
                        .border(1.dp, BreakoutPrimary.copy(alpha = 0.42f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                            Box(Modifier.size(width = 7.dp, height = 18.dp).clip(RoundedCornerShape(999.dp)).background(BreakoutPrimary))
                            Box(Modifier.size(width = 7.dp, height = 30.dp).clip(RoundedCornerShape(999.dp)).background(WaiverAccent))
                            Box(Modifier.size(width = 7.dp, height = 23.dp).clip(RoundedCornerShape(999.dp)).background(BreakoutPrimary))
                        }
                        Text("B", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    }
                }
                Text(
                    "Breakout",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Fantasy music, synced live.",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            BreakoutCard(
                contentPadding = PaddingValues(BreakoutDimensions.lg),
                border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.38f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
                ) {
                    StartupSignalTile("Account", progress >= 0.08f, Modifier.weight(1f))
                    StartupSignalTile("Leagues", progress >= 0.35f, Modifier.weight(1f))
                    StartupSignalTile("Roster", progress >= 0.70f, Modifier.weight(1f))
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(BreakoutSurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(12.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(BreakoutPrimary, WaiverAccent)
                                )
                            )
                    )
                }
                Text(
                    "${(animatedProgress * 100).toInt().coerceIn(0, 100)}%",
                    color = BreakoutTextSecondary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Crossfade(targetState = message, label = "startupLoadingMessage") { currentMessage ->
                        Text(
                            currentMessage,
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
private fun StartupSignalTile(label: String, complete: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(if (complete) BreakoutPrimary.copy(alpha = 0.16f) else BreakoutSurfaceVariant)
            .border(
                1.dp,
                if (complete) BreakoutPrimary.copy(alpha = 0.38f) else BreakoutOutline.copy(alpha = 0.28f),
                RoundedCornerShape(BreakoutDimensions.SmallCornerRadius)
            )
            .padding(vertical = BreakoutDimensions.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(if (complete) "OK" else "...", color = if (complete) BreakoutPrimary else BreakoutTextSecondary, fontWeight = FontWeight.Black)
        Text(label, color = BreakoutTextSecondary, style = MaterialTheme.typography.labelMedium, maxLines = 1)
    }
}

@Composable
internal fun DraftFloatingClock(
    league: LeagueUi,
    members: List<LeagueMemberUi>?,
    account: AccountUi?,
    modifier: Modifier = Modifier
) {
    var nowMs by remember { mutableStateOf(SupabaseLeagueService.syncedNowMillis()) }
    LaunchedEffect(league.currentPickStartedAt, league.currentPickIndex, league.settings.pickSeconds) {
        val baseSyncedMs = SupabaseLeagueService.syncedNowMillis()
        val baseElapsedMs = SystemClock.elapsedRealtime()
        nowMs = baseSyncedMs
        while (league.draftStatus == DraftStatus.Live) {
            delay(100)
            nowMs = baseSyncedMs + (SystemClock.elapsedRealtime() - baseElapsedMs)
        }
    }
    val pickStartedMs = league.currentPickStartedAt.parseServerInstantMillis() ?: nowMs
    val secondsLeft = (((pickStartedMs + league.settings.pickSeconds * 1000L) - nowMs + 999L) / 1000L)
        .coerceIn(0L, league.settings.pickSeconds.toLong())
        .toInt()
    val userOnClock = isAccountOnClock(league, members, account)
    Surface(
        color = if (userOnClock) BreakoutPrimary.copy(alpha = 0.92f) else BreakoutSurfaceVariant.copy(alpha = 0.94f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, if (userOnClock) BreakoutPrimary else BreakoutOutline),
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = BreakoutDimensions.md, vertical = BreakoutDimensions.sm),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (userOnClock) "Your Pick" else currentDraftPicker(league, members, account),
                color = if (userOnClock) Color.White else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${secondsLeft}s",
                color = if (userOnClock) Color.White else BreakoutTextSecondary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
internal fun SignInScreen(
    message: String,
    onGoogleSignIn: (() -> Unit)?,
    onSignIn: (AccountUi) -> Unit
) {
    var mode by rememberSaveable { mutableStateOf(AuthMode.Login) }
    var email by rememberSaveable { mutableStateOf("") }
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var mailingList by rememberSaveable { mutableStateOf(true) }
    var authError by remember { mutableStateOf<String?>(null) }
    var signingIn by rememberSaveable { mutableStateOf(false) }
    var authRequestId by rememberSaveable { mutableStateOf(0) }
    var authRequest by remember { mutableStateOf<AuthRequest?>(null) }
    var resetRequestId by rememberSaveable { mutableStateOf(0) }
    var resetRequest by remember { mutableStateOf<PasswordResetRequest?>(null) }
    var signupCooldownUntilMs by rememberSaveable { mutableStateOf(0L) }
    var nowMs by remember { mutableStateOf(SupabaseLeagueService.syncedNowMillis()) }
    var resetSending by rememberSaveable { mutableStateOf(false) }
    val cleanedEmail = email.trim()
    val cleanedLogin = login.trim()
    val cleanedUsername = username.trim()
    val emailError = if (mode == AuthMode.CreateAccount && email.isNotBlank()) emailQualityError(cleanedEmail) else null
    val passwordError = if (mode == AuthMode.CreateAccount && password.isNotBlank()) passwordPolicyError(password) else null
    val signupCooldownSeconds = ((signupCooldownUntilMs - nowMs + 999L) / 1000L).coerceAtLeast(0L)
    val canSubmit = when (mode) {
        AuthMode.Login -> cleanedLogin.length >= 3 && password.isNotBlank()
        AuthMode.CreateAccount -> emailError == null &&
            passwordError == null &&
            cleanedEmail.contains("@") &&
            cleanedUsername.length >= 3 &&
            password == confirmPassword &&
            signupCooldownSeconds == 0L
    }

    LaunchedEffect(signupCooldownUntilMs) {
        while (signupCooldownUntilMs > System.currentTimeMillis()) {
            nowMs = System.currentTimeMillis()
            delay(1000)
        }
        nowMs = System.currentTimeMillis()
    }

    LaunchedEffect(authRequest) {
        val request = authRequest ?: return@LaunchedEffect
        signingIn = true
        authError = null
        val fallbackAccount = AccountUi(
            email = if (request.mode == AuthMode.Login) request.login else request.email,
            username = request.username,
            displayName = request.username,
            mailingList = request.mailingList
        )
        val signedIn = if (isOnlinePlayConfigured()) {
            val result = if (request.mode == AuthMode.Login) {
                SupabaseLeagueService.login(request.login, request.password)
            } else {
                SupabaseLeagueService.createAccount(request.email, request.username, request.password, request.mailingList)
            }
            result.getOrElse {
                val friendly = friendlyAuthError(it.message, request.mode)
                authError = friendly
                if (request.mode == AuthMode.CreateAccount && isRateLimitError(it.message)) {
                    val retrySeconds = (authRetryDelaySeconds(normalizedErrorText(it.message)) ?: 60L).coerceAtLeast(60L)
                    signupCooldownUntilMs = System.currentTimeMillis() + retrySeconds * 1000L
                    nowMs = System.currentTimeMillis()
                } else if (request.mode == AuthMode.CreateAccount && isVerificationEmailNotice(it.message)) {
                    signupCooldownUntilMs = System.currentTimeMillis() + 120_000L
                    nowMs = System.currentTimeMillis()
                }
                signingIn = false
                authRequest = null
                return@LaunchedEffect
            }
        } else {
            fallbackAccount
        }
        signingIn = false
        authRequest = null
        onSignIn(signedIn)
    }

    LaunchedEffect(resetRequest) {
        val request = resetRequest ?: return@LaunchedEffect
        resetSending = true
        authError = null
        val result = if (isOnlinePlayConfigured()) {
            SupabaseLeagueService.requestPasswordReset(request.email)
        } else {
            Result.success(Unit)
        }
        result
            .onSuccess {
                authError = "Password reset link sent. Check your email."
            }
            .onFailure {
                authError = friendlyPasswordResetError(it.message)
            }
        resetSending = false
        resetRequest = null
    }

    fun clearAuthFeedback() {
        authError = null
        authRequest = null
        resetRequest = null
    }

    ScreenColumn {
        Text("Breakout", style = MaterialTheme.typography.headlineLarge)
        Text(
            message,
            color = BreakoutTextSecondary,
            style = MaterialTheme.typography.bodyLarge
        )
        BreakoutCard {
            Text(if (mode == AuthMode.Login) "Log In" else "Create Account", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                SecondaryButton(
                    text = "Log In",
                    modifier = Modifier.weight(1f),
                    enabled = mode != AuthMode.Login,
                    onClick = { mode = AuthMode.Login; clearAuthFeedback() }
                )
                SecondaryButton(
                    text = "Create",
                    modifier = Modifier.weight(1f),
                    enabled = mode != AuthMode.CreateAccount,
                    onClick = { mode = AuthMode.CreateAccount; clearAuthFeedback() }
                )
            }
            if (mode == AuthMode.Login) {
                StyledTextField(
                    value = login,
                    onValueChange = { login = it; clearAuthFeedback() },
                    label = "Email or Username",
                    maxLength = MaxEmailLength,
                    keyboardType = KeyboardType.Email
                )
            } else {
                StyledTextField(
                    value = email,
                    onValueChange = { email = it; clearAuthFeedback() },
                    label = "Email",
                    maxLength = MaxEmailLength,
                    keyboardType = KeyboardType.Email
                )
                emailError?.let {
                    AnimatedFeedbackText(message = it, color = BreakoutCoral)
                }
                StyledTextField(
                    value = username,
                    onValueChange = { username = it.cleanUsernameInput(); clearAuthFeedback() },
                    label = "Username",
                    maxLength = MaxUsernameLength
                )
            }
            StyledTextField(
                value = password,
                onValueChange = { password = it; clearAuthFeedback() },
                label = "Password",
                maxLength = MaxPasswordLength,
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )
            if (mode == AuthMode.Login) {
                SecondaryButton(
                    text = if (resetSending) "Sending Reset Link" else "Forgot Password?",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !resetSending && !signingIn,
                    onClick = {
                        val resetEmail = cleanedLogin.lowercase()
                        val resetError = emailQualityError(resetEmail)
                        if (resetError != null) {
                            authError = if (cleanedLogin.contains("@")) resetError else "Enter your email above first."
                        } else {
                            authError = null
                            resetRequestId += 1
                            resetRequest = PasswordResetRequest(resetRequestId, resetEmail)
                        }
                    }
                )
                onGoogleSignIn?.let {
                    SecondaryButton(
                        text = "Sign In with Google",
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !signingIn && !resetSending,
                        onClick = {
                            authError = null
                            it()
                        }
                    )
                }
            }
            if (mode == AuthMode.CreateAccount) {
                StyledTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; clearAuthFeedback() },
                    label = "Confirm Password",
                    maxLength = MaxPasswordLength,
                    keyboardType = KeyboardType.Password,
                    visualTransformation = PasswordVisualTransformation()
                )
                if (confirmPassword.isNotBlank() && password != confirmPassword) {
                    AnimatedFeedbackText(message = "Passwords do not match.", color = BreakoutCoral)
                }
                if (username.isNotBlank() && !isValidUsername(username)) {
                    AnimatedFeedbackText(message = "Username must be 3-24 letters, numbers, or underscores.", color = BreakoutCoral)
                }
                passwordError?.let {
                    AnimatedFeedbackText(message = it, color = BreakoutCoral)
                }
            }
            if (mode == AuthMode.CreateAccount) {
                ToggleRow(
                    label = "Mailing List",
                    value = if (mailingList) "Subscribed" else "Off",
                    enabled = true,
                    onToggle = { mailingList = !mailingList; clearAuthFeedback() }
                )
            }
            PrimaryButton(
                text = if (signingIn) {
                    if (mode == AuthMode.Login) "Logging In" else "Creating Account"
                } else if (mode == AuthMode.CreateAccount && signupCooldownSeconds > 0L) {
                    "Try Again in ${signupCooldownSeconds}s"
                } else {
                    if (mode == AuthMode.Login) "Log In" else "Create Account"
                },
                enabled = canSubmit && !signingIn,
                onClick = {
                    if (mode == AuthMode.CreateAccount && signupCooldownSeconds > 0L) {
                        authError = "Account creation is cooling down because verification emails were requested too quickly. Try again in ${signupCooldownSeconds}s."
                    } else {
                        authError = null
                        authRequestId += 1
                        authRequest = AuthRequest(
                            id = authRequestId,
                            mode = mode,
                            login = cleanedLogin,
                            email = cleanedEmail,
                            username = cleanedUsername,
                            password = password,
                            mailingList = mailingList
                        )
                    }
                }
            )
            val isNotice = authError?.contains("Check your email", ignoreCase = true) == true ||
                authError?.contains("Account created", ignoreCase = true) == true ||
                authError?.contains("sent", ignoreCase = true) == true
            AnimatedFeedbackText(
                message = authError,
                color = if (isNotice) MaterialTheme.colorScheme.primary else BreakoutCoral
            )
        }
    }
}

@Composable
internal fun ResetPasswordScreen(
    account: AccountUi,
    onPasswordSaved: (AccountUi) -> Unit
) {
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var saving by rememberSaveable { mutableStateOf(false) }
    var saveRequestId by rememberSaveable { mutableStateOf(0) }
    var saveRequest by remember { mutableStateOf<Int?>(null) }
    val passwordError = if (password.isNotBlank()) passwordPolicyError(password) else null
    val canSave = passwordError == null && password.isNotBlank() && password == confirmPassword && !saving

    LaunchedEffect(saveRequest) {
        saveRequest ?: return@LaunchedEffect
        saving = true
        status = null
        SupabaseLeagueService.updatePassword(account.accessToken, password)
            .onSuccess {
                status = "Password updated."
                saving = false
                saveRequest = null
                onPasswordSaved(account)
            }
            .onFailure {
                status = friendlyPasswordResetError(it.message)
                saving = false
                saveRequest = null
            }
    }

    ScreenColumn {
        Text("Set New Password", style = MaterialTheme.typography.headlineLarge)
        Text(
            "Choose a new password for ${account.email}.",
            color = BreakoutTextSecondary,
            style = MaterialTheme.typography.bodyLarge
        )
        BreakoutCard {
            StyledTextField(
                value = password,
                onValueChange = {
                    password = it
                    status = null
                },
                label = "New Password",
                maxLength = MaxPasswordLength,
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )
            StyledTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    status = null
                },
                label = "Confirm Password",
                maxLength = MaxPasswordLength,
                keyboardType = KeyboardType.Password,
                visualTransformation = PasswordVisualTransformation()
            )
            if (confirmPassword.isNotBlank() && password != confirmPassword) {
                AnimatedFeedbackText(message = "Passwords do not match.", color = BreakoutCoral)
            }
            passwordError?.let {
                AnimatedFeedbackText(message = it, color = BreakoutCoral)
            }
            PrimaryButton(
                text = if (saving) "Saving Password" else "Save Password",
                enabled = canSave,
                onClick = {
                    saveRequestId += 1
                    saveRequest = saveRequestId
                }
            )
            AnimatedFeedbackText(
                message = status,
                color = if (status?.contains("updated", ignoreCase = true) == true) MaterialTheme.colorScheme.primary else BreakoutCoral
            )
        }
    }
}

@Composable
internal fun LeagueSetupScreen(
    joinError: String?,
    onClearError: () -> Unit,
    onCreateLeague: (String) -> Unit,
    onJoinLeague: (String) -> Unit
) {
    var leagueName by rememberSaveable { mutableStateOf("") }
    var inviteCode by rememberSaveable { mutableStateOf("") }

    ScreenColumn {
        ScreenHero(
            eyebrow = "Breakout",
            title = "Start Your Music League",
            subtitle = "Create a league, invite friends, draft artists, then compete as momentum changes.",
            stats = listOf(
                Triple("Draft", "Live", "Snake or linear"),
                Triple("Market", "Daily", "Artist signals")
            ),
            accent = BreakoutSecondary
        )
        BreakoutCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberBadge("1")
                Column(modifier = Modifier.weight(1f)) {
                    Text("Create League", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("Name it now. Invite members after it opens.", color = BreakoutTextSecondary)
                }
            }
            StyledTextField(
                value = leagueName,
                onValueChange = {
                    leagueName = it
                    onClearError()
                },
                label = "League Name",
                maxLength = MaxLeagueNameLength
            )
            PrimaryButton(
                text = "Create League",
                modifier = Modifier.fillMaxWidth(),
                enabled = leagueName.isNotBlank(),
                onClick = { onCreateLeague(leagueName.trim()) }
            )
        }
        BreakoutCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberBadge("2")
                Column(modifier = Modifier.weight(1f)) {
                    Text("Join League", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("Paste the 6-character invite code.", color = BreakoutTextSecondary)
                }
            }
            StyledTextField(
                value = inviteCode,
                onValueChange = {
                    inviteCode = it.uppercase().filter { char -> char.isLetterOrDigit() }.take(6)
                    onClearError()
                },
                label = "Invite Code",
                keyboardActions = KeyboardActions(onDone = {
                    if (isValidInviteCode(inviteCode)) onJoinLeague(inviteCode)
                })
            )
            SecondaryButton(
                text = "Join League",
                modifier = Modifier.fillMaxWidth(),
                enabled = isValidInviteCode(inviteCode),
                onClick = { onJoinLeague(inviteCode) }
            )
            AnimatedFeedbackText(message = cleanVisibleError(joinError), color = BreakoutCoral)
        }
    }
}

@Composable
internal fun LeagueSetupStepRow(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(BreakoutPrimary.copy(alpha = 0.18f))
                .border(1.dp, BreakoutPrimary.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = BreakoutPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
        Text(text, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
internal fun NumberBadge(text: String, accent: Color = BreakoutPrimary) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.16f))
            .border(1.dp, accent.copy(alpha = 0.48f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = accent, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
    }
}

@Composable
internal fun ScreenHero(
    eyebrow: String,
    title: String,
    subtitle: String,
    stats: List<Triple<String, String, String>> = emptyList(),
    accent: Color = BreakoutPrimary
) {
    BreakoutCard(
        contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.46f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(74.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(accent)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
            ) {
                Text(eyebrow, color = accent, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black)
                Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (stats.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                stats.chunked(2).forEach { rowStats ->
                    Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                        rowStats.forEach { (label, value, detail) ->
                            StatTile(label, value, detail, Modifier.weight(1f))
                        }
                        if (rowStats.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

