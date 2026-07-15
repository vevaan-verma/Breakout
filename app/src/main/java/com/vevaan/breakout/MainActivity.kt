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

class MainActivity : ComponentActivity() {
    private val authCallbackUri = mutableStateOf<Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ensureDraftNotificationChannel()
        requestDraftNotificationPermissionIfNeeded()
        authCallbackUri.value = intent?.data
        setContent {
            BreakoutTheme {
                BreakoutApp(
                    oauthCallbackUri = authCallbackUri.value,
                    onOauthCallbackHandled = { authCallbackUri.value = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        authCallbackUri.value = intent.data
    }

    private fun ensureDraftNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            DraftNotificationChannelId,
            "Draft Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Draft reminders and live draft alerts"
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun requestDraftNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), DraftNotificationPermissionRequest)
        }
    }
}

class DraftReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(DraftReminderTitleExtra) ?: "Draft Reminder"
        val message = intent.getStringExtra(DraftReminderMessageExtra) ?: "Your draft is coming up."
        val notificationId = intent.getIntExtra(DraftReminderIdExtra, title.hashCode())
        showDraftSystemNotification(context, title, message, notificationId)
    }
}

internal const val DraftNotificationChannelId = "breakout_draft_alerts"
internal const val DraftNotificationPermissionRequest = 42
internal const val DraftReminderTitleExtra = "draft_reminder_title"
internal const val DraftReminderMessageExtra = "draft_reminder_message"
internal const val DraftReminderIdExtra = "draft_reminder_id"

internal enum class BreakoutTab(val title: String, val mark: String) {
    Home("Home", "H"),
    Draft("Draft", "D"),
    Market("Market", "M"),
    Roster("Roster", "R"),
    Matchup("Matchup", "VS"),
    AllMatchups("All Matchups", "AM"),
    DraftSummary("Draft Summary", "DS"),
    Standings("Standings", "S"),
    League("League", "L"),
    Account("Account", "A")
}

internal enum class MarketFilter(val label: String) {
    Headliners("Headliners"),
    Wildcards("Mainstays"),
    Rising("Rising"),
    DeepCuts("Deep Cuts"),
}

internal enum class RosterSlot(val label: String, val hint: String, val filter: MarketFilter) {
    HeadlinerOne("Headliner", "Large-scale artist", MarketFilter.Headliners),
    HeadlinerTwo("Headliner", "Large-scale artist", MarketFilter.Headliners),
    HeadlinerThree("Headliner", "Large-scale artist", MarketFilter.Headliners),
    HeadlinerFour("Headliner", "Large-scale artist", MarketFilter.Headliners),
    WildcardOne("Mainstay", "Established non-headliner", MarketFilter.Wildcards),
    WildcardTwo("Mainstay", "Established non-headliner", MarketFilter.Wildcards),
    WildcardThree("Mainstay", "Established non-headliner", MarketFilter.Wildcards),
    WildcardFour("Mainstay", "Established non-headliner", MarketFilter.Wildcards),
    RisingOne("Rising", "Emerging artist", MarketFilter.Rising),
    RisingTwo("Rising", "Emerging artist", MarketFilter.Rising),
    RisingThree("Rising", "Emerging artist", MarketFilter.Rising),
    RisingFour("Rising", "Emerging artist", MarketFilter.Rising),
    DeepCutOne("Deep Cut", "Under 750K followers", MarketFilter.DeepCuts),
    DeepCutTwo("Deep Cut", "Under 750K followers", MarketFilter.DeepCuts),
    DeepCutThree("Deep Cut", "Under 750K followers", MarketFilter.DeepCuts),
    BenchOne("Bench", "Reserve artist", MarketFilter.Wildcards),
    BenchTwo("Bench", "Reserve artist", MarketFilter.Wildcards),
    BenchThree("Bench", "Reserve artist", MarketFilter.Wildcards),
    BenchFour("Bench", "Reserve artist", MarketFilter.Wildcards),
    BenchFive("Bench", "Reserve artist", MarketFilter.Wildcards),
    BenchSix("Bench", "Reserve artist", MarketFilter.Wildcards)
}

internal enum class DraftFormat(val label: String) {
    Snake("Snake Draft"),
    Linear("Linear Draft"),
    Auction("Auction Draft")
}

internal enum class DraftStatus(val label: String) {
    Scheduled("Scheduled"),
    Lobby("Lobby"),
    Practice("Practice"),
    Live("Live"),
    Complete("Complete")
}

internal enum class AuthMode {
    Login,
    CreateAccount
}

internal enum class DraftRoomView {
    Home,
    Board,
    Roster,
    Picks
}

internal enum class AuthGate {
    Checking,
    SignedOut,
    SignedIn
}

internal data class AuthRequest(
    val id: Int,
    val mode: AuthMode,
    val login: String,
    val email: String,
    val username: String,
    val password: String,
    val mailingList: Boolean
)

internal data class PasswordResetRequest(
    val id: Int,
    val email: String
)

internal data class AuthCallbackUi(
    val account: AccountUi,
    val isPasswordRecovery: Boolean
)

internal const val MaxEmailLength = 254
internal const val MaxUsernameLength = 24
internal const val MinPasswordLength = 8
internal const val MaxPasswordLength = 72
internal const val MaxLeagueNameLength = 40
internal const val ReleaseDraftLeadMinutes = 30L
internal const val DebugDraftLeadMinutes = 1L
internal const val DraftLobbyGraceSeconds = 300
internal const val DraftLobbyReadyCountdownSeconds = 30
internal const val DraftLobbyDelayMinutes = 15L
internal const val MinLeagueMembers = 2
internal const val MaxLeagueMembers = 20
internal const val MinSeasonWeeks = 4
internal const val MaxSeasonWeeks = 24
internal const val MinWaiverClaims = 1
internal const val MaxWaiverClaims = 12
internal const val AuthCallbackUrl = "breakout://auth-callback"
internal val WaiverAccent = Color(0xFFFFB454)
internal val DraftedOtherAccent = Color(0xFFFF7C7C)
internal val DraftDateInputFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
internal val DraftDateDisplayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
internal val DraftReminderOffsets = listOf(
    1_440L to "1 day",
    60L to "1 hour",
    30L to "30 minutes",
    15L to "15 minutes"
)
internal val BlockedSignupEmailDomains = setOf(
    "example.com",
    "example.net",
    "example.org",
    "test.com",
    "mailinator.com",
    "guerrillamail.com",
    "10minutemail.com",
    "tempmail.com",
    "temp-mail.org",
    "yopmail.com",
    "dispostable.com",
    "trashmail.com",
    "fakeinbox.com",
    "throwawaymail.com"
)
internal val CommonEmailDomainTypos = mapOf(
    "gamil.com" to "gmail.com",
    "gmial.com" to "gmail.com",
    "gnail.com" to "gmail.com",
    "gmail.co" to "gmail.com",
    "gmail.con" to "gmail.com",
    "yaho.com" to "yahoo.com",
    "yahoo.co" to "yahoo.com",
    "outlok.com" to "outlook.com",
    "outlook.co" to "outlook.com",
    "hotmial.com" to "hotmail.com",
    "hotmal.com" to "hotmail.com",
    "icloud.co" to "icloud.com"
)
internal val CatalogArtistOverrides = setOf(
    "juice wrld",
    "lil peep",
    "mac miller",
    "pop smoke",
    "xxxtentacion"
)

internal data class AccountUi(
    val email: String,
    val username: String,
    val displayName: String,
    val mailingList: Boolean = true,
    val accessToken: String = "",
    val refreshToken: String = "",
    val userId: String = ""
) {
    val label: String
        get() = username.ifBlank { email }
}

internal data class LeagueSettingsUi(
    val draftFormat: DraftFormat = DraftFormat.Snake,
    val draftDateLabel: String = "Set date",
    val pickSeconds: Int = 90,
    val seasonWeeks: Int = 10,
    val headlinerSlots: Int = 2,
    val risingSlots: Int = 1,
    val wildcardSlots: Int = 1,
    val deepCutSlots: Int = 1,
    val benchSlots: Int = 2,
    val maxWaiverClaims: Int = 5
) {
    val rosterSize: Int
        get() = headlinerSlots + risingSlots + wildcardSlots + deepCutSlots + benchSlots
}

internal data class LeagueUi(
    val id: String = "",
    val name: String,
    val inviteCode: String,
    val onlineReady: Boolean,
    val memberCount: Int = 1,
    val maxMembers: Int = 10,
    val invitesOpen: Boolean = true,
    val isManager: Boolean = true,
    val draftStatus: DraftStatus = DraftStatus.Scheduled,
    val currentPickIndex: Int = 0,
    val currentPickStartedAt: String = "",
    val lobbyReadyAt: String = "",
    val autoPickEnabled: Boolean = false,
    val settings: LeagueSettingsUi = LeagueSettingsUi()
) {
    val inviteState: String
        get() = when {
            memberCount >= maxMembers -> "Full"
            invitesOpen -> "Invites open"
            else -> "Invites closed"
    }
}

internal data class LeagueMemberUi(
    val username: String,
    val teamName: String,
    val role: String,
    val autoPickEnabled: Boolean = false
) {
    val isManager: Boolean
        get() = role.equals("manager", ignoreCase = true)
}

internal data class DraftPickUi(
    val pickNumber: Int,
    val pickedBy: String,
    val slot: RosterSlot,
    val artist: ArtistUi,
    val secondsToPick: Int? = null,
    val autoPicked: Boolean = false
)

internal data class DraftPresenceUi(
    val presentCount: Int = 0,
    val requiredCount: Int = 2,
    val memberCount: Int = 2,
    val readyAt: String = ""
)

internal data class MatchupWeekUi(
    val week: Int,
    val opponent: String?,
    val isBye: Boolean
)

internal data class WaiverClaimUi(
    val artist: ArtistUi,
    val slot: RosterSlot,
    val dropSlot: RosterSlot? = null
)

internal data class ArtistUi(
    val id: Long?,
    val name: String,
    val listeners: Long?,
    val albumCount: Int?,
    val imageUrl: String?,
    val source: String,
    val scoreStatus: String,
    val spotifyId: String? = null,
    val spotifyPopularity: Int? = null,
    val trackPopularity: Int? = null,
    val lastFmListeners: Long? = null,
    val lastFmPlaycount: Long? = null,
    val kworbRank: Int? = null,
    val kworbDailyListenerChange: Long? = null,
    val kworbPeakListeners: Long? = null,
    val kworbTotalStreams: Long? = null,
    val kworbLeadStreams: Long? = null,
    val kworbSoloStreams: Long? = null,
    val kworbFeatureStreams: Long? = null,
    val kworbLeadDailyStreams: Long? = null,
    val kworbSoloDailyStreams: Long? = null,
    val kworbFeatureDailyStreams: Long? = null,
    val kworbDataUpdatedAt: String? = null,
    val kworbDailyStreams: Long? = null,
    val kworbTopSongTitle: String? = null,
    val kworbTopSongStreams: Long? = null,
    val kworbTopSongDailyStreams: Long? = null,
    val topTrackImageUrl: String? = null,
    val topTrackReleaseDate: String? = null,
    val kworbDailyTopSongTitle: String? = null,
    val kworbDailyTopSongStreams: Long? = null,
    val latestReleaseTitle: String? = null,
    val latestReleaseDate: String? = null,
    val latestReleaseImageUrl: String? = null,
    val latestReleaseType: String? = null,
    val releaseRecencyScore: Double? = null
) {
    val initials: String = name
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .take(2)
        .joinToString("")
        .ifBlank { "?" }

    val price: String
        get() {
            val listenersValue = listeners ?: return "Pending"
            val millions = (5.0 + (log10(max(listenersValue.toDouble(), 10_000.0)) - 4.0) * 4.0).coerceIn(5.0, 32.0)
            return "\$${"%.1f".format(millions)}M"
        }

    val tag: String
        get() = when {
            name.lowercase() in CatalogArtistOverrides -> "Catalog"
            listeners == null -> "Data pending"
            isHeadlinerEligible() -> "Headliner"
            isDeepCutEligible() -> "Deep Cut"
            isRisingEligible() -> "Rising"
            marketBucket() == MarketFilter.Wildcards -> "Mainstay"
            else -> "Headliner"
        }

    val audienceLabel: String
        get() = listeners?.let { "${it.formatCompact()} listeners" } ?: "Listeners developing"

    val compactAudienceLabel: String
        get() = listeners?.formatCompact() ?: "Sizing Up"

    val cardAudienceLabel: String
        get() = listeners?.let { "${it.formatCompact()} listeners" } ?: "Listeners"

    val activityLabel: String
        get() = albumCount?.let { "$it releases" } ?: "Catalog developing"

    val marketNote: String
        get() = when (tag) {
            "Deep Cut" -> if ((listeners ?: 0L) < 10_000L) "Unproven discovery watch" else "High-upside discovery slot"
            "Rising" -> "Momentum watch candidate"
            "Mainstay" -> "Established roster anchor"
            "Catalog" -> "Reliable catalog, lower breakout upside"
            "Headliner" -> "Reliable floor, lower growth upside"
            else -> "Waiting for audience data"
        }

    val compactRead: String
        get() = when (tag) {
            "Deep Cut" -> "High upside"
            "Rising" -> "Momentum watch"
            "Mainstay" -> "Established"
            "Catalog" -> "Catalog floor"
            "Headliner" -> "Reliable floor"
            else -> "Sizing up"
        }

    val riskLabel: String
        get() = when (tag) {
            "Headliner" -> "Low variance"
            "Catalog" -> "Low growth"
            "Mainstay" -> "Medium variance"
            "Rising" -> "Medium variance"
            "Deep Cut" -> "High variance"
            else -> "Unrated"
        }

    val bestImageUrl: String?
        get() = imageUrl
            ?.replace("56x56", "1000x1000")
            ?.replace("250x250", "1000x1000")
            ?.replace("500x500", "1000x1000")

    val projectedScore: Int
        get() {
            val scale = listeners ?: return 0
            val catalogPenalty = if (name.lowercase() in CatalogArtistOverrides) 18.0 else 0.0
            val base = 18.0
            val discoveryUpside = when {
                scale < 750_000 -> 36.0
                scale < 2_500_000 -> 34.0
                scale < 8_000_000 -> 24.0
                else -> 10.0
            }
            val safety = when {
                scale >= 8_000_000 -> 14.0
                scale >= 2_500_000 -> 10.0
                scale >= 750_000 -> 8.0
                else -> 4.0
            }
            val trackSignal = ((trackPopularity ?: spotifyPopularity ?: 45).coerceIn(0, 100) / 100.0) * 14.0
            val volatilityBonus = when {
                scale < 750_000 -> 10.0
                scale < 2_500_000 -> 12.0
                else -> 0.0
            }
            val dailyMomentum = kworbDailyListenerChange
                ?.let { (signedLogSignal(it) / 100.0) * 14.0 }
                ?: 0.0
            return (base + discoveryUpside + safety + trackSignal + volatilityBonus + dailyMomentum - catalogPenalty)
                .toInt()
                .coerceAtLeast(8)
        }
}

internal data class SnapshotUi(
    val previousListeners: Long,
    val currentListeners: Long,
    val previousTrackPopularity: Int?,
    val currentTrackPopularity: Int?,
    val previousLastFmListeners: Long?,
    val currentLastFmListeners: Long?,
    val previousLastFmPlaycount: Long?,
    val currentLastFmPlaycount: Long?,
    val releaseRecencyScore: Double,
    val capturedAt: Long
)

internal data class SignalRowUi(
    val label: String,
    val value: String,
    val detail: String? = null
)

internal data class ArtistWeekPointsUi(
    val week: Int,
    val points: Double?,
    val isCurrent: Boolean = false
)

internal sealed interface MarketState {
    data object Loading : MarketState
    data class Ready(val artists: List<ArtistUi>) : MarketState
    data class Empty(val message: String) : MarketState
    data class Error(val message: String) : MarketState
}

internal object DeezerArtistService {
    private const val Endpoint = "https://api.deezer.com"
    private val listCache = mutableMapOf<String, List<ArtistUi>>()
    private val artistCache = mutableMapOf<Long, ArtistUi>()

    suspend fun topArtists(): List<ArtistUi> = cachedList("all-market-v3") {
        (headlinerCandidates() + risingCandidates() + wildcardCandidates() + deepCutCandidates())
            .distinctBy { it.name.lowercase() }
            .sortedWith(
                compareByDescending<ArtistUi> { it.isHeadlinerEligible() }
                    .thenByDescending { it.listeners ?: 0L }
            )
    }

    suspend fun headlinerCandidates(): List<ArtistUi> = cachedList("headliners-v3") {
        val searchSeeds = listOf(
            "Drake",
            "Taylor Swift",
            "The Weeknd",
            "Bad Bunny",
            "Ariana Grande",
            "Billie Eilish",
            "Kendrick Lamar",
            "SZA",
            "Travis Scott",
            "Post Malone",
            "Rihanna",
            "Beyonce",
            "Ed Sheeran",
            "Justin Bieber",
            "Dua Lipa",
            "Olivia Rodrigo",
            "Bruno Mars",
            "Future",
            "Metro Boomin",
            "Nicki Minaj",
            "Doja Cat",
            "J. Cole",
            "Morgan Wallen",
            "Karol G",
            "Peso Pluma",
            "Feid",
            "Lana Del Rey",
            "Lady Gaga",
            "Miley Cyrus",
            "Harry Styles",
            "Adele",
            "Kanye West",
            "Eminem",
            "BTS",
            "BLACKPINK",
            "Shakira",
            "Imagine Dragons",
            "Coldplay",
            "Maroon 5",
            "Hozier",
            "Sabrina Carpenter",
            "Tate McRae",
            "Tyler, The Creator",
            "Playboi Carti",
            "21 Savage",
            "Lil Uzi Vert",
            "Central Cee",
            "Rauw Alejandro",
            "Anitta",
            "Tyla"
        )
        searchSeeds.parallelMap { seed ->
            runCatching {
                val results = search(seed)
                results.firstOrNull { it.name.equals(seed, ignoreCase = true) } ?: results.firstOrNull()
            }.getOrNull()
        }.mapNotNull { it }
            .distinctBy { it.name.lowercase() }
            .filter { it.isHeadlinerEligible() }
            .sortedByDescending { it.listeners ?: 0L }
    }

    suspend fun wildcardCandidates(): List<ArtistUi> = cachedList("wildcards-v3") {
        val chartArtists = runCatching {
            request("$Endpoint/chart/0/artists?limit=100")
            .getJSONArray("data")
            .toArtistList()
            .enriched()
            .filter { it.listeners != null }
        }.getOrDefault(emptyList())
        val wildcardSeeds = listOf(
            "breakout",
            "viral",
            "new music",
            "alt pop",
            "rap caviar",
            "dance hits",
            "indie hits",
            "latin hits",
            "country hits",
            "afrobeats hits",
            "electronic hits",
            "r&b hits"
        )
        val seededArtists = wildcardSeeds.parallelMap { seed ->
            runCatching { search(seed) }.getOrDefault(emptyList())
        }.flatten()
        val pool = (chartArtists + seededArtists)
            .distinctBy { it.name.lowercase() }
            .filter { !it.isHeadlinerEligible() }
        val ideal = pool
            .filter { artist ->
                (artist.listeners ?: 0L) >= 750_000
            }
            .sortedByDescending { it.projectedScore }
            .take(180)
        ideal.ifEmpty {
            val fallbackSeeds = listOf(
                "Pop Smoke",
                "Don Toliver",
                "Yeat",
                "Teezo Touchdown",
                "PinkPantheress",
                "FLO",
                "Amaarae",
                "Tems",
                "Ice Spice",
                "Sexyy Red",
                "Ken Carson",
                "Destroy Lonely",
                "Benson Boone",
                "Renee Rapp",
                "Doechii"
            )
            (pool + exactArtistSeeds(fallbackSeeds))
                .distinctBy { it.name.lowercase() }
                .filter { !it.isHeadlinerEligible() }
                .sortedByDescending { it.projectedScore }
                .take(180)
        }
    }

    suspend fun search(query: String): List<ArtistUi> = cachedList("search:${query.lowercase()}") {
        request("$Endpoint/search/artist?q=${query.urlEncoded()}&limit=50")
            .getJSONArray("data")
            .toArtistList()
            .enriched()
            .filter { it.listeners != null }
    }

    suspend fun deepCutCandidates(): List<ArtistUi> = cachedList("deep-cuts-v3") {
        val searchSeeds = listOf(
            "bedroom",
            "hyperpop",
            "shoegaze",
            "alt r&b",
            "indie pop",
            "underground rap",
            "dream pop",
            "indie rock",
            "new jazz",
            "afrobeats",
            "pluggnb",
            "cloud rap",
            "neo soul",
            "post punk",
            "lofi",
            "jersey club",
            "garage",
            "phonk",
            "singer songwriter",
            "alternative rap",
            "electropop",
            "new wave"
        )
        val pool = searchSeeds.parallelMap { seed ->
            runCatching { search(seed) }.getOrDefault(emptyList())
        }.flatten()
            .distinctBy { it.name.lowercase() }
        val ideal = pool
            .filter { (it.listeners ?: Long.MAX_VALUE) in 1_000..999_999 }
            .sortedByDescending { it.discoveryScore() }
            .take(180)
        ideal.ifEmpty {
            val fallbackSeeds = listOf(
                "Nourished by Time",
                "Mk.gee",
                "Jane Remover",
                "underscores",
                "Lola Young",
                "The Dare",
                "Samara Cyn",
                "Snow Strippers",
                "Clairo",
                "Magdalena Bay",
                "Yves Tumor",
                "Faye Webster",
                "Rachel Chinouriri",
                "Artemas",
                "2hollis",
                "Nettspend"
            )
            (pool + exactArtistSeeds(fallbackSeeds))
                .filter { !it.isHeadlinerEligible() }
                .sortedByDescending { it.discoveryScore() }
                .take(180)
        }
    }

    suspend fun risingCandidates(): List<ArtistUi> = cachedList("rising-v3") {
        val searchSeeds = listOf(
            "pop",
            "r&b",
            "rap",
            "indie",
            "latin",
            "dance",
            "country",
            "afro pop",
            "k-pop",
            "reggaeton",
            "uk rap",
            "alt pop",
            "bedroom pop",
            "country pop",
            "drill",
            "trap",
            "house",
            "techno",
            "punk",
            "metal",
            "jazz",
            "folk"
        )
        val pool = searchSeeds.parallelMap { seed ->
            runCatching { search(seed) }.getOrDefault(emptyList())
        }.flatten()
            .distinctBy { it.name.lowercase() }
            .filter { !it.isHeadlinerEligible() }
        val ideal = pool
            .filter { artist ->
                (artist.listeners ?: Long.MAX_VALUE) in 250_000..1_999_999
            }
            .sortedByDescending { it.listeners ?: 0L }
            .take(180)
        ideal.ifEmpty {
            val fallbackSeeds = listOf(
                "Chappell Roan",
                "Tyla",
                "Tate McRae",
                "Gracie Abrams",
                "Benson Boone",
                "Tommy Richman",
                "Dasha",
                "Myles Smith",
                "Teddy Swims",
                "Doechii",
                "Ravyn Lenae",
                "Laufey",
                "Artemas",
                "Flo Milli",
                "The Marias",
                "d4vd"
            )
            (pool + exactArtistSeeds(fallbackSeeds))
                .distinctBy { it.name.lowercase() }
                .filter { !it.isHeadlinerEligible() }
                .sortedByDescending { it.listeners ?: 0L }
                .take(180)
        }
    }

    private suspend fun exactArtistSeeds(seeds: List<String>): List<ArtistUi> =
        seeds.parallelMap { seed ->
            runCatching {
                val results = search(seed)
                results.firstOrNull { it.name.equals(seed, ignoreCase = true) } ?: results.firstOrNull()
            }.getOrNull()
        }.mapNotNull { it }

    private suspend fun cachedList(key: String, loader: suspend () -> List<ArtistUi>): List<ArtistUi> {
        listCache[key]?.let { return it }
        return loader().also { artists ->
            if (artists.isNotEmpty()) {
                listCache[key] = artists
            }
        }
    }

    private suspend fun List<ArtistUi>.enriched(): List<ArtistUi> = parallelMap { artist ->
        val id = artist.id
        if (id == null || artist.listeners != null && artist.albumCount != null && !artist.imageUrl.isNullOrBlank()) {
            artist
        } else {
            artistCache[id] ?: runCatching {
                request("$Endpoint/artist/$id").toArtistUi()
            }.getOrDefault(artist).also { artistCache[id] = it }
        }
    }

    private suspend fun request(url: String): JSONObject = withContext(Dispatchers.IO) {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 10_000
        }
        connection.inputStream.bufferedReader().use { JSONObject(it.readText()) }
    }

    private fun JSONArray.toArtistList(): List<ArtistUi> = List(length()) { index ->
        getJSONObject(index).toArtistUi()
    }

    private fun JSONObject.toArtistUi(): ArtistUi = ArtistUi(
        id = if (has("id")) optLong("id") else null,
        name = optString("name", "Unknown artist"),
        listeners = when {
            has("nb_fan") -> optLong("nb_fan")
            has("fans") -> optLong("fans")
            has("nb_fans") -> optLong("nb_fans")
            else -> null
        },
        albumCount = if (has("nb_album")) optInt("nb_album") else null,
        imageUrl = optString("picture_xl")
            .ifBlank { optString("picture_big") }
            .ifBlank { optString("picture_medium") }
            .ifBlank { null },
        source = "Music data",
        scoreStatus = "Market watch"
    )

    private fun String.urlEncoded(): String = URLEncoder.encode(this, Charsets.UTF_8.name())

    private fun ArtistUi.discoveryScore(): Double {
        val scale = listeners ?: return 0.0
        val scaleBalance = 1.0 - kotlin.math.abs(scale - 150_000) / 150_000.0
        val lowCrowdBonus = if (scale < 80_000) 20.0 else 0.0
        return scaleBalance.coerceIn(0.0, 1.0) * 80 + lowCrowdBonus
    }

    private suspend fun <T, R> Iterable<T>.parallelMap(transform: suspend (T) -> R): List<R> = coroutineScope {
        map { item -> async { transform(item) } }.map { deferred -> deferred.await() }
    }
}

internal data class KworbArtistStats(
    val name: String,
    val monthlyListeners: Long?,
    val dailyListenerChange: Long?,
    val peakListeners: Long?,
    val rank: Int?,
    val totalStreams: Long? = null,
    val leadStreams: Long? = null,
    val soloStreams: Long? = null,
    val featureStreams: Long? = null,
    val leadDailyStreams: Long? = null,
    val soloDailyStreams: Long? = null,
    val featureDailyStreams: Long? = null,
    val dataUpdatedAt: String? = null,
    val dailyStreams: Long? = null,
    val topSongTitle: String? = null,
    val topSongStreams: Long? = null,
    val topSongDailyStreams: Long? = null,
    val dailyTopSongTitle: String? = null,
    val dailyTopSongStreams: Long? = null
)

internal object KworbArtistService {
    private const val ListenersUrl = "https://kworb.net/spotify/listeners.html"
    private const val ArtistStreamsUrl = "https://kworb.net/spotify/artists.html"
    private const val SongsUrl = "https://kworb.net/spotify/songs.html"
    private const val CacheMillis = 6 * 60 * 60 * 1000L
    private var cachedAudienceAt = 0L
    private var cachedFullAt = 0L
    private var cachedAudienceStats: Map<String, KworbArtistStats> = emptyMap()
    private var cachedFullStats: Map<String, KworbArtistStats> = emptyMap()

    suspend fun enrichAudience(artists: List<ArtistUi>): List<ArtistUi> =
        enrichWithStats(artists, full = false)

    suspend fun audienceLeaderNames(limit: Int): List<String> =
        statsByName(full = false)
            .values
            .sortedWith(
                compareBy<KworbArtistStats> { it.rank ?: Int.MAX_VALUE }
                    .thenByDescending { it.monthlyListeners ?: 0L }
            )
            .map { it.name }
            .filter { it.isNotBlank() }
            .take(limit)

    suspend fun enrichFull(artists: List<ArtistUi>): List<ArtistUi> {
        val base = enrichWithStats(artists, full = false)
        return base.map { artist ->
            val spotifyId = artist.spotifyId ?: return@map artist
            val pageStats = runCatching { artistPageStats(spotifyId, artist.name) }.getOrNull() ?: return@map artist
            artist.withKworbStats(pageStats)
        }
    }

    private suspend fun enrichWithStats(artists: List<ArtistUi>, full: Boolean): List<ArtistUi> {
        if (artists.isEmpty()) return artists
        val statsByName = statsByName(full)
        if (statsByName.isEmpty()) return artists
        return artists.map { artist ->
            val stats = statsByName[artist.name.artistKey()] ?: return@map artist
            artist.withKworbStats(stats)
        }
    }

    private fun ArtistUi.withKworbStats(stats: KworbArtistStats): ArtistUi = copy(
        listeners = stats.monthlyListeners ?: listeners,
        kworbRank = stats.rank ?: kworbRank,
        kworbDailyListenerChange = stats.dailyListenerChange ?: kworbDailyListenerChange,
        kworbPeakListeners = stats.peakListeners ?: kworbPeakListeners,
        kworbTotalStreams = stats.totalStreams ?: kworbTotalStreams,
        kworbLeadStreams = stats.leadStreams ?: kworbLeadStreams,
        kworbSoloStreams = stats.soloStreams ?: kworbSoloStreams,
        kworbFeatureStreams = stats.featureStreams ?: kworbFeatureStreams,
        kworbLeadDailyStreams = stats.leadDailyStreams ?: kworbLeadDailyStreams,
        kworbSoloDailyStreams = stats.soloDailyStreams ?: kworbSoloDailyStreams,
        kworbFeatureDailyStreams = stats.featureDailyStreams ?: kworbFeatureDailyStreams,
        kworbDataUpdatedAt = stats.dataUpdatedAt ?: kworbDataUpdatedAt,
        kworbDailyStreams = stats.dailyStreams ?: kworbDailyStreams,
        kworbTopSongTitle = stats.topSongTitle ?: kworbTopSongTitle,
        kworbTopSongStreams = stats.topSongStreams ?: kworbTopSongStreams,
        kworbTopSongDailyStreams = stats.topSongDailyStreams ?: kworbTopSongDailyStreams,
        kworbDailyTopSongTitle = stats.dailyTopSongTitle ?: kworbDailyTopSongTitle,
        kworbDailyTopSongStreams = stats.dailyTopSongStreams ?: kworbDailyTopSongStreams,
        source = "Spotify and chart data"
    )

    private suspend fun statsByName(full: Boolean): Map<String, KworbArtistStats> {
        val now = System.currentTimeMillis()
        if (!full && cachedAudienceStats.isNotEmpty() && now - cachedAudienceAt < CacheMillis) return cachedAudienceStats
        if (full && cachedFullStats.isNotEmpty() && now - cachedFullAt < CacheMillis) return cachedFullStats
        val parsed = withContext(Dispatchers.Default) {
            val listenerHtml = runCatching { requestText(ListenersUrl) }.getOrNull()
            val listenerStats = listenerHtml?.let { parseListenerRows(it) }.orEmpty()
            val streamStats = if (full) {
                runCatching { parseArtistStreamRows(requestText(ArtistStreamsUrl)) }.getOrDefault(emptyList())
            } else {
                emptyList()
            }
            val songStats = if (full) {
                runCatching { parseSongRows(requestText(SongsUrl)) }.getOrDefault(emptyList())
            } else {
                emptyList()
            }
            (listenerStats + streamStats + songStats)
                .groupBy { it.name.artistKey() }
                .mapValues { (_, rows) -> rows.mergeKworbRows() }
                .filterKeys { it.isNotBlank() }
        }
        if (parsed.isNotEmpty()) {
            if (full) {
                cachedFullStats = parsed
                cachedFullAt = now
            } else {
                cachedAudienceStats = parsed
                cachedAudienceAt = now
            }
        }
        return if (full) cachedFullStats else cachedAudienceStats
    }

    private fun parseListenerRows(html: String): List<KworbArtistStats> {
        val rowRegex = Regex("<tr[^>]*>(.*?)</tr>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        val anchorRegex = Regex("<a[^>]*>(.*?)</a>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
        return rowRegex.findAll(html).mapNotNull { rowMatch ->
            val rowHtml = rowMatch.groupValues[1]
            val name = anchorRegex.find(rowHtml)
                ?.groupValues
                ?.getOrNull(1)
                ?.cleanHtml()
                ?.takeIf { it.isNotBlank() }
                ?: return@mapNotNull null
            val text = rowHtml.cleanHtml(" ")
            val tokens = Regex("[+-]?\\d[\\d,]*").findAll(text).map { it.value }.toList()
            if (tokens.size < 2) return@mapNotNull null
            val values = tokens.mapNotNull { token ->
                token.replace(",", "").toLongOrNull()?.let { token to it }
            }
            val rank = values.firstOrNull()?.second?.takeIf { it in 1L..Int.MAX_VALUE.toLong() }?.toInt()
            val signedDaily = values.firstOrNull { (token, value) ->
                (token.startsWith("+") || token.startsWith("-")) && kotlin.math.abs(value) < 20_000_000L
            }?.second
            val listenerCandidates = values
                .drop(1)
                .filter { (token, value) -> !token.startsWith("+") && !token.startsWith("-") && kotlin.math.abs(value) >= 100_000L }
                .map { it.second }
                .map { kotlin.math.abs(it) }
            val monthlyListeners = listenerCandidates.firstOrNull()
            val peakListeners = listenerCandidates.drop(1).firstOrNull()
            KworbArtistStats(
                name = name,
                monthlyListeners = monthlyListeners,
                dailyListenerChange = signedDaily,
                peakListeners = peakListeners,
                rank = rank
            )
        }.toList()
    }

    private fun parseArtistStreamRows(html: String): List<KworbArtistStats> {
        return htmlRows(html).mapNotNull { rowHtml ->
            val name = rowHtml.firstAnchorText() ?: return@mapNotNull null
            val values = rowHtml.numericTokens()
            if (values.size < 2) return@mapNotNull null
            val totals = values.drop(1)
                .filter { (token, _) -> !token.startsWith("+") && !token.startsWith("-") }
                .map { it.second }
                .filter { kotlin.math.abs(it) >= 1_000_000L }
            val totalStreams = totals.firstOrNull()?.let { kotlin.math.abs(it) }
            val dailyStreams = values.firstOrNull { (token, value) ->
                (token.startsWith("+") || token.startsWith("-")) && kotlin.math.abs(value) in 10_000L..200_000_000L
            }?.second ?: totals.drop(1).firstOrNull()?.takeIf { it < 200_000_000L }
            KworbArtistStats(
                name = name,
                monthlyListeners = null,
                dailyListenerChange = null,
                peakListeners = null,
                rank = null,
                totalStreams = totalStreams,
                dailyStreams = dailyStreams
            )
        }.toList()
    }

    private fun parseSongRows(html: String): List<KworbArtistStats> {
        return htmlRows(html).take(2500).mapNotNull { rowHtml ->
            val anchors = rowHtml.anchorTexts()
            val text = rowHtml.cleanHtml(" ")
            val values = rowHtml.numericTokens()
            val anchorPair = if (anchors.size >= 2) {
                val possibleArtist = anchors.firstOrNull { anchor -> text.artistKey().contains(anchor.artistKey()) } ?: anchors.getOrNull(1)
                val possibleSong = anchors.firstOrNull { it != possibleArtist }
                possibleArtist to possibleSong
            } else {
                null to null
            }
            val textPair = Regex("""^\s*\d*\s*([^-|]+?)\s+-\s+(.+?)(?:\s+\d|$)""")
                .find(text)
                ?.let { it.groupValues.getOrNull(1)?.trim() to it.groupValues.getOrNull(2)?.trim() }
            val artistName = anchorPair.first ?: textPair?.first
            val songTitle = anchorPair.second ?: textPair?.second ?: return@mapNotNull null
            val totals = values.drop(1)
                .filter { (token, _) -> !token.startsWith("+") && !token.startsWith("-") }
                .map { it.second }
                .filter { kotlin.math.abs(it) >= 100_000L }
            val dailyStreams = values.firstOrNull { (token, value) ->
                (token.startsWith("+") || token.startsWith("-")) && kotlin.math.abs(value) in 1_000L..50_000_000L
            }?.second ?: totals.drop(1).firstOrNull()?.takeIf { it < 50_000_000L }
            KworbArtistStats(
                name = artistName ?: return@mapNotNull null,
                monthlyListeners = null,
                dailyListenerChange = null,
                peakListeners = null,
                rank = null,
                dailyTopSongTitle = songTitle,
                dailyTopSongStreams = dailyStreams
            )
        }.toList()
    }

    private fun List<KworbArtistStats>.mergeKworbRows(): KworbArtistStats {
        val rows = this
        return KworbArtistStats(
            name = rows.firstOrNull { it.name.isNotBlank() }?.name.orEmpty(),
            monthlyListeners = rows.firstNotNullOfOrNull { it.monthlyListeners },
            dailyListenerChange = rows.firstNotNullOfOrNull { it.dailyListenerChange },
            peakListeners = rows.firstNotNullOfOrNull { it.peakListeners },
            rank = rows.firstNotNullOfOrNull { it.rank },
            totalStreams = rows.firstNotNullOfOrNull { it.totalStreams },
            dailyStreams = rows.firstNotNullOfOrNull { it.dailyStreams },
            topSongTitle = rows.firstNotNullOfOrNull { it.topSongTitle },
            topSongStreams = rows.firstNotNullOfOrNull { it.topSongStreams },
            topSongDailyStreams = rows.firstNotNullOfOrNull { it.topSongDailyStreams },
            dailyTopSongTitle = rows.firstNotNullOfOrNull { it.dailyTopSongTitle },
            dailyTopSongStreams = rows.firstNotNullOfOrNull { it.dailyTopSongStreams }
        )
    }

    private suspend fun artistPageStats(spotifyId: String, artistName: String): KworbArtistStats {
        val html = requestText("https://kworb.net/spotify/artist/${spotifyId}_songs.html")
        val split = parseArtistStreamSplit(html)
        val rows = parseArtistSongRows(html)
        val topDaily = rows.maxByOrNull { it.dailyStreams ?: 0L }
        return KworbArtistStats(
            name = artistName,
            monthlyListeners = null,
            dailyListenerChange = null,
            peakListeners = null,
            rank = null,
            totalStreams = split.totalStreams ?: rows.sumOf { it.totalStreams ?: 0L }.takeIf { it > 0L },
            leadStreams = split.leadStreams,
            soloStreams = split.soloStreams,
            featureStreams = split.featureStreams,
            dailyStreams = split.dailyStreams ?: rows.sumOf { it.dailyStreams ?: 0L }.takeIf { it > 0L },
            leadDailyStreams = split.leadDailyStreams,
            soloDailyStreams = split.soloDailyStreams,
            featureDailyStreams = split.featureDailyStreams,
            dataUpdatedAt = split.updatedAt,
            topSongTitle = topDaily?.title,
            topSongStreams = topDaily?.dailyStreams,
            topSongDailyStreams = topDaily?.dailyStreams,
            dailyTopSongTitle = topDaily?.title,
            dailyTopSongStreams = topDaily?.dailyStreams
        )
    }

    private data class KworbStreamSplit(
        val updatedAt: String?,
        val totalStreams: Long?,
        val leadStreams: Long?,
        val soloStreams: Long?,
        val featureStreams: Long?,
        val dailyStreams: Long?,
        val leadDailyStreams: Long?,
        val soloDailyStreams: Long?,
        val featureDailyStreams: Long?
    )

    private fun parseArtistStreamSplit(html: String): KworbStreamSplit {
        val text = html.cleanHtml(" ")
        val updatedAt = Regex("Last updated:\\s*(\\d{4}/\\d{2}/\\d{2})")
            .find(text)
            ?.groupValues
            ?.getOrNull(1)
        fun rowValues(label: String): List<Long?> {
            val row = Regex("$label\\s+([\\d,]+)\\s+([\\d,]+)\\s+([\\d,]+)\\s+([\\d,]+)", RegexOption.IGNORE_CASE)
                .find(text)
                ?: return List(4) { null }
            return (1..4).map { index -> row.groupValues.getOrNull(index)?.replace(",", "")?.toLongOrNull() }
        }
        val streams = rowValues("Streams")
        val daily = rowValues("Daily")
        return KworbStreamSplit(
            updatedAt = updatedAt,
            totalStreams = streams.getOrNull(0),
            leadStreams = streams.getOrNull(1),
            soloStreams = streams.getOrNull(2),
            featureStreams = streams.getOrNull(3),
            dailyStreams = daily.getOrNull(0),
            leadDailyStreams = daily.getOrNull(1),
            soloDailyStreams = daily.getOrNull(2),
            featureDailyStreams = daily.getOrNull(3)
        )
    }

    private data class KworbSongRow(
        val title: String,
        val totalStreams: Long?,
        val dailyStreams: Long?
    )

    private fun parseArtistSongRows(html: String): List<KworbSongRow> =
        htmlRows(html).mapNotNull { rowHtml ->
            val cells = rowHtml.tableCells()
            val anchors = rowHtml.anchorTexts()
            val fallbackText = rowHtml.cleanHtml(" ")
            val fallbackNumbers = Regex("\\d[\\d,]{3,}").findAll(fallbackText).map { it.value }.toList()
            if (cells.size < 3 && fallbackNumbers.size < 2) return@mapNotNull null
            val firstCell = cells.getOrNull(0).orEmpty()
            val rank = firstCell.replace("*", "").trim().toIntOrNull()
                ?: Regex("^\\s*(\\d+)\\b").find(firstCell)?.groupValues?.getOrNull(1)?.toIntOrNull()
                ?: Regex("^\\s*(\\d+)\\s+").find(fallbackText)?.groupValues?.getOrNull(1)?.toIntOrNull()
                ?: return@mapNotNull null
            if (rank <= 0) return@mapNotNull null
            val titleFromCombinedRankCell = Regex("^\\s*\\d+\\s*\\*?\\s*(.+)$")
                .find(firstCell)
                ?.groupValues
                ?.getOrNull(1)
                ?.trim()
                ?.takeIf { it.isNotBlank() }
            val splitCells = cells.size >= 4
            val titleFromLink = anchors
                .firstOrNull { anchor ->
                    val key = anchor.artistKey()
                    key.isNotBlank() &&
                        key != "song title" &&
                        key != "streams" &&
                        key != "daily" &&
                        !key.all { it.isDigit() }
                }
            val title = titleFromLink ?: if (splitCells) {
                cells.getOrNull(1)
                    ?.removePrefix("*")
                    ?.trim()
                    ?.takeIf { it.isNotBlank() && !it.equals("Song Title", ignoreCase = true) }
            } else {
                titleFromCombinedRankCell
            }
                ?: fallbackNumbers.firstOrNull()?.let { firstNumber ->
                    fallbackText
                        .substringAfter(rank.toString())
                        .substringBefore(firstNumber)
                        .removePrefix("*")
                        .trim()
                        .takeIf { it.isNotBlank() && !it.equals("Song Title", ignoreCase = true) }
                }
                ?: return@mapNotNull null
            val totalStreams = cells.getOrNull(if (splitCells) 2 else 1)?.parseCompactLong()
                ?: fallbackNumbers.getOrNull(0)?.parseCompactLong()
            val dailyStreams = cells.getOrNull(if (splitCells) 3 else 2)?.parseCompactLong()
                ?: fallbackNumbers.getOrNull(1)?.parseCompactLong()
            if (totalStreams == null && dailyStreams == null) return@mapNotNull null
            KworbSongRow(title = title, totalStreams = totalStreams, dailyStreams = dailyStreams)
        }.toList()

    private fun String.tableCells(): List<String> =
        Regex("<t[dh][^>]*>(.*?)</t[dh]>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .findAll(this)
            .map { it.groupValues[1].cleanHtml(" ") }
            .filter { it.isNotBlank() }
            .toList()

    private fun htmlRows(html: String): Sequence<String> =
        Regex("<tr[^>]*>(.*?)</tr>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .findAll(html)
            .map { it.groupValues[1] }

    private fun String.anchorTexts(): List<String> =
        Regex("<a[^>]*>(.*?)</a>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .findAll(this)
            .map { it.groupValues[1].cleanHtml() }
            .filter { it.isNotBlank() }
            .toList()

    private fun String.firstAnchorText(): String? = anchorTexts().firstOrNull()

    private fun String.numericTokens(): List<Pair<String, Long>> =
        Regex("[+-]?\\d[\\d,]*").findAll(cleanHtml(" "))
            .mapNotNull { match ->
                val token = match.value
                token.replace(",", "").toLongOrNull()?.let { token to it }
            }
            .toList()

    private suspend fun requestText(url: String): String = withContext(Dispatchers.IO) {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("User-Agent", "Mozilla/5.0 Breakout Android")
        }
        connection.inputStream.bufferedReader().use { it.readText() }
    }
}

internal object MusicArtistService {
    private const val SpotifyApi = "https://api.spotify.com/v1"
    private const val SpotifyAccounts = "https://accounts.spotify.com/api/token"
    private val listCache = mutableMapOf<String, List<ArtistUi>>()
    private val detailCache = mutableMapOf<String, ArtistUi>()
    private val detailCacheTime = mutableMapOf<String, Long>()
    private const val DetailCacheMillis = 30 * 60 * 1000L
    private var spotifyToken: String? = null
    private var spotifyTokenExpiresAt: Long = 0L

    private val discoveryQueries = listOf(
        "pop",
        "hip hop",
        "r&b",
        "indie",
        "latin",
        "country",
        "dance",
        "electronic",
        "rock",
        "alternative",
        "afrobeats",
        "k-pop",
        "viral",
        "new music",
        "breakout"
    )

    suspend fun topArtists(): List<ArtistUi> = cachedList("music-all-v9") {
        (headlinerCandidates() + wildcardCandidates() + risingCandidates() + deepCutCandidates())
            .marketDistinct()
            .sortedWith(
                compareBy<ArtistUi> { it.kworbRank ?: Int.MAX_VALUE }
                    .thenByDescending { it.isHeadlinerEligible() }
                    .thenByDescending { it.listeners ?: 0L }
                    .thenByDescending { it.breakoutScore(null) }
            )
    }

    suspend fun headlinerCandidates(): List<ArtistUi> = cachedList("music-headliners-v9") {
        discoveryPool()
            .filter { it.isHeadlinerEligible() }
            .sortedWith(compareBy<ArtistUi> { it.kworbRank ?: Int.MAX_VALUE }.thenByDescending { it.listeners ?: 0L })
            .take(500)
    }

    suspend fun risingCandidates(): List<ArtistUi> = cachedList("music-rising-v9") {
        val pool = discoveryPool()
        val artists = pool
            .filter { it.marketBucket() == MarketFilter.Rising }
            .sortedWith(compareByDescending<ArtistUi> { it.breakoutScore(null) }.thenByDescending { it.listeners ?: 0L })
        (if (artists.size >= 20) artists else {
            (artists + pool
                .filter { !it.isHeadlinerEligible() && (it.listeners ?: 0L) in 750_000L until 15_000_000L }
                .sortedByDescending { it.breakoutScore(null) }
            ).marketDistinct()
        }).take(500)
    }

    suspend fun wildcardCandidates(): List<ArtistUi> = cachedList("music-spotlights-v9") {
        val pool = discoveryPool()
        val artists = pool
            .filter { it.marketBucket() == MarketFilter.Wildcards }
            .sortedWith(compareByDescending<ArtistUi> { it.breakoutScore(null) }.thenByDescending { it.listeners ?: 0L })
        (if (artists.size >= 20) artists else {
            (artists + pool
                .filter { !it.isHeadlinerEligible() && (it.listeners ?: 0L) >= 8_000_000L }
                .sortedByDescending { it.listeners ?: 0L }
            ).marketDistinct()
        }).take(500)
    }

    suspend fun deepCutCandidates(): List<ArtistUi> = cachedList("music-deep-cuts-v9") {
        val pool = discoveryPool()
        val artists = pool
            .filter { it.marketBucket() == MarketFilter.DeepCuts }
            .sortedByDescending { it.breakoutScore(null) + it.discoverySortValue() }
        (if (artists.size >= 20) artists else {
            (artists + pool
                .filter { !it.isHeadlinerEligible() && (it.listeners ?: Long.MAX_VALUE) < 1_250_000L }
                .sortedByDescending { it.breakoutScore(null) + it.discoverySortValue() }
            ).marketDistinct()
        }).take(500)
    }

    suspend fun search(query: String): List<ArtistUi> = cachedList("music-search-v6:${query.lowercase()}") {
        val artists = if (spotifyConfigured()) {
            spotifyRankedSearch(query, limit = 40).map { it.enrichWithLastFm() }
        } else {
            DeezerArtistService.search(query)
        }
        KworbArtistService.enrichAudience(artists)
            .filter { it.kworbRank != null && it.listeners != null }
    }

    suspend fun enrichSignals(artist: ArtistUi): ArtistUi {
        val cacheKey = artist.spotifyId ?: artist.name.artistKey()
        val now = System.currentTimeMillis()
        detailCache[cacheKey]?.takeIf { now - (detailCacheTime[cacheKey] ?: 0L) < DetailCacheMillis }?.let { return it }
        val searchableArtist = if (artist.spotifyId.isNullOrBlank() && spotifyConfigured()) {
            runCatching {
                val results = spotifySearch(artist.name, limit = 5, includeTrackSignal = false)
                results.firstOrNull { it.name.equals(artist.name, ignoreCase = true) } ?: results.firstOrNull()
            }.getOrNull()?.let { fresh ->
                fresh.copy(
                    lastFmListeners = artist.lastFmListeners ?: fresh.lastFmListeners,
                    lastFmPlaycount = artist.lastFmPlaycount ?: fresh.lastFmPlaycount
                )
            } ?: artist
        } else {
            artist
        }
        val spotifyId = searchableArtist.spotifyId ?: return (KworbArtistService.enrichFull(listOf(searchableArtist.enrichWithLastFm())).firstOrNull() ?: searchableArtist)
            .also {
                detailCache[cacheKey] = it
                detailCacheTime[cacheKey] = now
            }
        val topTrackSignal = runCatching { spotifyTopTrackSignal(spotifyId) }.getOrNull()
        val release = runCatching { spotifyLatestRelease(spotifyId) }.getOrNull()
        return (KworbArtistService.enrichFull(listOf(searchableArtist.copy(
            trackPopularity = searchableArtist.trackPopularity ?: topTrackSignal?.popularity,
            releaseRecencyScore = release?.releaseRecencyScore ?: searchableArtist.releaseRecencyScore ?: topTrackSignal?.releaseRecencyScore,
            latestReleaseTitle = release?.title ?: searchableArtist.latestReleaseTitle,
            latestReleaseDate = release?.date ?: searchableArtist.latestReleaseDate,
            latestReleaseImageUrl = release?.imageUrl ?: searchableArtist.latestReleaseImageUrl,
            latestReleaseType = release?.type ?: searchableArtist.latestReleaseType,
            kworbTopSongTitle = searchableArtist.kworbTopSongTitle ?: topTrackSignal?.title,
            topTrackImageUrl = searchableArtist.topTrackImageUrl ?: topTrackSignal?.imageUrl,
            topTrackReleaseDate = searchableArtist.topTrackReleaseDate ?: topTrackSignal?.releaseDate
        ).enrichWithLastFm())).firstOrNull() ?: searchableArtist).also {
            detailCache[cacheKey] = it
            searchableArtist.spotifyId?.let { spotifyKey ->
                detailCache[spotifyKey] = it
                detailCacheTime[spotifyKey] = now
            }
            detailCacheTime[cacheKey] = now
        }
    }

    private suspend fun discoveryPool(): List<ArtistUi> = cachedList("music-discovery-pool-v9") {
        val artists = if (spotifyConfigured()) {
            val queryArtists = discoveryQueries.parallelMap { query ->
                listOf(0, 50).parallelMap { offset ->
                    runCatching { spotifySearch(query, limit = 50, offset = offset, includeTrackSignal = false) }.getOrDefault(emptyList())
                }.flatten()
            }
                .flatten()
            val chartArtists = runCatching {
                seededArtists(KworbArtistService.audienceLeaderNames(1_500))
            }.getOrDefault(emptyList())
            (queryArtists + chartArtists).marketDistinct()
        } else {
            discoveryQueries.parallelMap { query ->
                runCatching { DeezerArtistService.search(query) }.getOrDefault(emptyList())
            }.flatten().marketDistinct()
        }
        KworbArtistService.enrichAudience(artists)
            .filter { it.listeners != null }
            .marketDistinct()
            .sortedWith(compareBy<ArtistUi> { it.kworbRank ?: Int.MAX_VALUE }.thenByDescending { it.listeners ?: 0L })
    }

    private suspend fun seededArtists(seeds: List<String>): List<ArtistUi> =
        if (spotifyConfigured()) {
            seeds.parallelMap { seed ->
                runCatching {
                    val results = spotifySearch(seed, limit = 5, includeTrackSignal = true)
                    results.firstOrNull { it.name.equals(seed, ignoreCase = true) } ?: results.firstOrNull()
                }.getOrNull()
            }
                .mapNotNull { it }
                .parallelMap { it.enrichWithLastFm() }
                .distinctBy { it.name.lowercase() }
        } else {
            seeds.parallelMap { seed ->
                runCatching {
                    val results = DeezerArtistService.search(seed)
                    results.firstOrNull { it.name.equals(seed, ignoreCase = true) } ?: results.firstOrNull()
                }.getOrNull()
            }.mapNotNull { it }.distinctBy { it.name.lowercase() }
        }

    private suspend fun spotifySearch(
        query: String,
        limit: Int,
        offset: Int = 0,
        includeTrackSignal: Boolean = false
    ): List<ArtistUi> {
        val artists = spotifyGet("$SpotifyApi/search?q=${query.urlEncoded()}&type=artist&market=US&limit=$limit&offset=$offset")
            .getJSONObject("artists")
            .getJSONArray("items")
        return List(artists.length()) { artists.getJSONObject(it) }
            .parallelMap { artistJson ->
                val topTrackSignal = if (includeTrackSignal) {
                    val spotifyId = artistJson.getString("id")
                    runCatching { spotifyTopTrackSignal(spotifyId) }.getOrNull()
                } else {
                    null
                }
                artistJson.toSpotifyArtistUi(topTrackSignal)
            }
    }

    private suspend fun spotifyRankedSearch(query: String, limit: Int): List<ArtistUi> {
        val trimmed = query.trim()
        val variants = listOf(
            "artist:\"$trimmed\"",
            trimmed,
            trimmed.wordsForSearch().joinToString(" ")
        ).distinct().filter { it.isNotBlank() }
        val results = variants.parallelMap { variant ->
            runCatching { spotifySearch(variant, limit = limit, includeTrackSignal = true) }.getOrDefault(emptyList())
        }.flatten()
            .marketDistinct()
        return results.sortedWith(
            compareByDescending<ArtistUi> { it.name.searchMatchScore(trimmed) }
                .thenByDescending { it.listeners ?: 0L }
                .thenByDescending { it.breakoutScore(null) }
        )
    }

    private data class SpotifyTopTrackSignal(
        val popularity: Int?,
        val releaseRecencyScore: Double?,
        val title: String?,
        val imageUrl: String?,
        val releaseDate: String?
    )

    private suspend fun spotifyTopTrackSignal(spotifyId: String): SpotifyTopTrackSignal {
        val tracks = spotifyGet("$SpotifyApi/artists/$spotifyId/top-tracks?market=US")
            .optJSONArray("tracks") ?: return SpotifyTopTrackSignal(null, null, null, null, null)
        val trackItems = List(tracks.length()) { tracks.getJSONObject(it) }
        val popularity = trackItems
            .map { it.optInt("popularity", 0) }
            .sortedDescending()
            .take(3)
            .takeIf { it.isNotEmpty() }
            ?.average()
            ?.toInt()
        val recency = trackItems
            .mapNotNull { it.optJSONObject("album")?.optString("release_date")?.releaseRecencyScore() }
            .maxOrNull()
        val topTrack = trackItems.maxByOrNull { it.optInt("popularity", 0) }
        val album = topTrack?.optJSONObject("album")
        val images = album?.optJSONArray("images")
        val imageUrl = images?.takeIf { it.length() > 0 }?.getJSONObject(0)?.optString("url")?.ifBlank { null }
        return SpotifyTopTrackSignal(
            popularity = popularity,
            releaseRecencyScore = recency,
            title = topTrack?.optString("name")?.ifBlank { null },
            imageUrl = imageUrl,
            releaseDate = album?.optString("release_date")?.ifBlank { null }
        )
    }

    private data class SpotifyReleaseSignal(
        val title: String,
        val date: String?,
        val type: String?,
        val imageUrl: String?,
        val releaseRecencyScore: Double?
    )

    private suspend fun spotifyLatestRelease(spotifyId: String): SpotifyReleaseSignal? {
        val albums = spotifyGet(
            "$SpotifyApi/artists/$spotifyId/albums?include_groups=album,single,appears_on&market=US&limit=20"
        ).optJSONArray("items") ?: return null
        return List(albums.length()) { albums.getJSONObject(it) }
            .mapNotNull { album ->
                val date = album.optString("release_date").ifBlank { null }
                val releaseDate = date?.toReleaseLocalDate() ?: return@mapNotNull null
                val type = album.optString("album_type").ifBlank { null }
                val typeWeight = when (type) {
                    "album" -> 1.0
                    "single" -> 0.86
                    "appears_on" -> 0.64
                    else -> 0.78
                }
                val images = album.optJSONArray("images")
                val imageUrl = images?.takeIf { it.length() > 0 }?.getJSONObject(0)?.optString("url")?.ifBlank { null }
                Triple(
                    releaseDate,
                    typeWeight,
                    SpotifyReleaseSignal(
                        title = album.optString("name", "Latest Release"),
                        date = date,
                        type = type,
                        imageUrl = imageUrl,
                        releaseRecencyScore = date.releaseRecencyScore(typeWeight)
                    )
                )
            }
            .maxByOrNull { it.first }
            ?.third
    }

    private suspend fun spotifyGet(url: String): JSONObject = requestJson(url, bearerToken = spotifyAccessToken())

    private suspend fun spotifyAccessToken(): String {
        val cached = spotifyToken
        if (!cached.isNullOrBlank() && System.currentTimeMillis() < spotifyTokenExpiresAt) return cached
        return withContext(Dispatchers.IO) {
            val credentials = "${BuildConfig.SPOTIFY_CLIENT_ID}:${BuildConfig.SPOTIFY_CLIENT_SECRET}"
            val encoded = Base64.getEncoder().encodeToString(credentials.toByteArray())
            val connection = (URL(SpotifyAccounts).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("Authorization", "Basic $encoded")
                setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            }
            connection.outputStream.use { it.write("grant_type=client_credentials".toByteArray()) }
            val response = connection.inputStream.bufferedReader().use { JSONObject(it.readText()) }
            response.getString("access_token").also { token ->
                spotifyToken = token
                spotifyTokenExpiresAt = System.currentTimeMillis() + ((response.optLong("expires_in", 3600) - 60) * 1000)
            }
        }
    }

    private suspend fun ArtistUi.enrichWithLastFm(): ArtistUi {
        if (BuildConfig.LASTFM_API_KEY.isBlank()) return this
        return runCatching {
            val stats = requestJson(
                "https://ws.audioscrobbler.com/2.0/?method=artist.getInfo&artist=${name.urlEncoded()}&api_key=${BuildConfig.LASTFM_API_KEY}&format=json"
            ).optJSONObject("artist")?.optJSONObject("stats")
            copy(
                lastFmListeners = stats?.optString("listeners")?.toLongOrNull(),
                lastFmPlaycount = stats?.optString("playcount")?.toLongOrNull()
            )
        }.getOrDefault(this)
    }

    private fun JSONObject.toSpotifyArtistUi(topTrackSignal: SpotifyTopTrackSignal?): ArtistUi {
        val images = optJSONArray("images")
        val imageUrl = images?.let { array ->
            if (array.length() == 0) null else array.getJSONObject(0).optString("url").ifBlank { null }
        }
        return ArtistUi(
            id = null,
            name = optString("name", "Unknown artist"),
            listeners = optJSONObject("followers")?.optLong("total"),
            albumCount = null,
            imageUrl = imageUrl,
            source = "Music data",
            scoreStatus = "Market watch",
            spotifyId = optString("id").ifBlank { null },
            spotifyPopularity = optInt("popularity").takeIf { it > 0 },
            trackPopularity = topTrackSignal?.popularity,
            releaseRecencyScore = topTrackSignal?.releaseRecencyScore,
            kworbTopSongTitle = topTrackSignal?.title,
            topTrackImageUrl = topTrackSignal?.imageUrl,
            topTrackReleaseDate = topTrackSignal?.releaseDate
        )
    }

    private suspend fun requestJson(url: String, bearerToken: String? = null): JSONObject = withContext(Dispatchers.IO) {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = 10_000
            readTimeout = 10_000
            bearerToken?.let { setRequestProperty("Authorization", "Bearer $it") }
        }
        JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
    }

    private suspend fun cachedList(key: String, loader: suspend () -> List<ArtistUi>): List<ArtistUi> {
        listCache[key]?.let { return it }
        return loader().also { artists ->
            if (artists.isNotEmpty()) {
                listCache[key] = artists
            }
        }
    }

    private fun spotifyConfigured(): Boolean =
        BuildConfig.SPOTIFY_CLIENT_ID.isNotBlank() && BuildConfig.SPOTIFY_CLIENT_SECRET.isNotBlank()

    private fun String.urlEncoded(): String = URLEncoder.encode(this, Charsets.UTF_8.name())

    private fun String.toReleaseLocalDate(): LocalDate? = runCatching {
        when (length) {
            4 -> LocalDate.of(toInt(), 1, 1)
            7 -> LocalDate.parse("$this-01")
            else -> LocalDate.parse(take(10))
        }
    }.getOrNull()

    private fun String.releaseRecencyScore(typeWeight: Double = 0.86): Double? {
        val date = toReleaseLocalDate() ?: return null
        val daysOld = ChronoUnit.DAYS.between(date, LocalDate.now()).coerceAtLeast(0)
        val freshness = 100.0 * kotlin.math.exp(-daysOld.toDouble() / 620.0)
        return (22.0 + freshness * 0.78 * typeWeight).coerceIn(18.0, 100.0)
    }

    private suspend fun <T, R> Iterable<T>.parallelMap(transform: suspend (T) -> R): List<R> = coroutineScope {
        map { item -> async { transform(item) } }.map { deferred -> deferred.await() }
    }
}

internal suspend fun prefetchArtistImages(context: Context, artists: List<ArtistUi>, limit: Int = 80) {
    artists
        .mapNotNull { it.bestImageUrl }
        .distinct()
        .take(limit)
        .forEach { imageUrl ->
            runCatching {
                context.imageLoader.execute(
                    ImageRequest.Builder(context)
                        .data(imageUrl)
                        .allowHardware(false)
                        .build()
                )
            }
        }
}

internal object SupabaseLeagueService {
    @Volatile
    private var serverClockOffsetMs: Long = 0L

    fun syncedNowMillis(): Long = System.currentTimeMillis() + serverClockOffsetMs

    private val baseUrl: String
        get() = BuildConfig.SUPABASE_URL.trimEnd('/')

    suspend fun login(login: String, password: String): Result<AccountUi> =
        runCatching {
            if (!isOnlinePlayConfigured()) error("Online accounts are not configured.")
            val email = if (login.contains("@")) login.trim().lowercase() else emailForUsername(login.trim())
            val session = signIn(email, password).getOrThrow()
            profileForUser(session.accessToken, session.userId)?.let { profile ->
                session.copy(username = profile.username, displayName = profile.username, mailingList = profile.mailingList)
            } ?: session.copy(username = login.substringBefore("@"), displayName = login.substringBefore("@"))
        }

    suspend fun createAccount(email: String, username: String, password: String, mailingList: Boolean): Result<AccountUi> =
        runCatching {
            if (!isOnlinePlayConfigured()) error("Online accounts are not configured.")
            emailQualityError(email)?.let { error(it) }
            if (!isValidUsername(username)) error("Username must be 3-24 letters, numbers, or underscores.")
            passwordPolicyError(password)?.let { error(it) }
            if (!isEmailAvailable(email)) error("That email already has an account. Use Log In instead.")
            if (!isUsernameAvailable(username)) error("That username is already taken.")
            val session = signUp(email.trim().lowercase(), username, password).getOrThrow()
            upsertProfile(session.accessToken, session.email, username, mailingList)
            session.copy(username = username, displayName = username, mailingList = mailingList)
        }

    suspend fun updateProfile(account: AccountUi): Result<AccountUi> = runCatching {
        if (account.accessToken.isBlank() || !isOnlinePlayConfigured()) error("Sign in again to continue.")
        if (!isValidUsername(account.username)) error("Username must be 3-24 letters, numbers, or underscores.")
        if (!isUsernameAvailableForAccount(account.username, account.userId)) error("That username is already taken.")
        upsertProfile(account.accessToken, account.email, account.username, account.mailingList)
        account.copy(displayName = account.username)
    }

    suspend fun deleteAccount(accessToken: String): Result<Unit> = runCatching {
        if (accessToken.isBlank() || !isOnlinePlayConfigured()) error("Sign in again to continue.")
        requestText(
            url = "$baseUrl/rest/v1/rpc/delete_my_account",
            method = "POST",
            accessToken = accessToken,
            body = "{}"
        )
    }

    suspend fun ensureProfile(account: AccountUi): Result<AccountUi> = runCatching {
        if (account.accessToken.isBlank() || !isOnlinePlayConfigured()) error("Sign in again to continue.")
        profileForUser(account.accessToken, account.userId)?.let { profile ->
            return@runCatching account.copy(
                email = profile.email.ifBlank { account.email },
                username = profile.username.ifBlank { account.username },
                displayName = profile.displayName.ifBlank { account.displayName },
                mailingList = profile.mailingList
            )
        }
        val username = profileUsernameFor(account)
        val repairedUsername = runCatching {
            upsertProfile(
                accessToken = account.accessToken,
                email = account.email,
                username = username,
                mailingList = account.mailingList
            )
            username
        }.getOrElse {
            val fallbackUsername = profileFallbackUsername(account)
            if (fallbackUsername == username) throw it
            upsertProfile(
                accessToken = account.accessToken,
                email = account.email,
                username = fallbackUsername,
                mailingList = account.mailingList
            )
            fallbackUsername
        }
        account.copy(username = repairedUsername, displayName = repairedUsername)
    }

    suspend fun refreshSession(refreshToken: String): Result<AccountUi> = runCatching {
        if (refreshToken.isBlank() || !isOnlinePlayConfigured()) error("Sign in again to continue.")
        parseSession(
            requestText(
                url = "$baseUrl/auth/v1/token?grant_type=refresh_token",
                method = "POST",
                accessToken = "",
                body = JSONObject().put("refresh_token", refreshToken).toString()
            )
        )
    }

    suspend fun requestPasswordReset(email: String): Result<Unit> = runCatching {
        if (!isOnlinePlayConfigured()) error("Online accounts are not configured.")
        emailQualityError(email)?.let { error(it) }
        requestText(
            url = "$baseUrl/auth/v1/recover",
            method = "POST",
            accessToken = "",
            body = JSONObject()
                .put("email", email.trim().lowercase())
                .put("redirect_to", AuthCallbackUrl)
                .toString()
        )
    }

    suspend fun updatePassword(accessToken: String, password: String): Result<Unit> = runCatching {
        if (accessToken.isBlank()) error("Sign in again to continue.")
        passwordPolicyError(password)?.let { error(it) }
        requestText(
            url = "$baseUrl/auth/v1/user",
            method = "PUT",
            accessToken = accessToken,
            body = JSONObject().put("password", password).toString()
        )
    }

    fun googleSignInUri(): Uri =
        Uri.parse("$baseUrl/auth/v1/authorize")
            .buildUpon()
            .appendQueryParameter("provider", "google")
            .appendQueryParameter("redirect_to", AuthCallbackUrl)
            .build()

    suspend fun accountFromAuthCallback(uri: Uri): Result<AuthCallbackUi> = runCatching {
        val callbackError = uri.getQueryParameter("error_description")
            ?: uri.getQueryParameter("error")
            ?: callbackParams(uri).optString("error_description").takeIf { it.isNotBlank() }
            ?: callbackParams(uri).optString("error").takeIf { it.isNotBlank() }
        if (!callbackError.isNullOrBlank()) error(callbackError)
        val params = callbackParams(uri)
        val accessToken = params.optString("access_token")
        if (accessToken.isBlank()) error("Could not finish sign in. Please try again.")
        val refreshToken = params.optString("refresh_token")
        val user = authUser(accessToken)
        val metadata = user.optJSONObject("user_metadata")
        val fullName = metadata?.optString("full_name").orEmpty()
        val name = metadata?.optString("name").orEmpty()
        val preferredUsername = metadata?.optString("username").orEmpty()
        val email = user.optString("email")
        val userId = user.optString("id")
        val username = preferredUsername
            .ifBlank { fullName.cleanUsernameInput() }
            .ifBlank { name.cleanUsernameInput() }
            .ifBlank { email.substringBefore("@").cleanUsernameInput() }
            .ifBlank { "user_${userId.filter { it.isLetterOrDigit() }.take(8)}" }
            .take(MaxUsernameLength)
        AuthCallbackUi(
            account = AccountUi(
            email = email,
            username = username,
            displayName = username,
            mailingList = true,
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId
            ),
            isPasswordRecovery = params.optString("type").equals("recovery", ignoreCase = true)
        )
    }

    suspend fun loadLeagues(accessToken: String, userId: String): Result<List<LeagueUi>> = runCatching {
        if (accessToken.isBlank() || !isOnlinePlayConfigured()) return@runCatching emptyList()
        val array = JSONArray(
            requestText(
                url = "$baseUrl/rest/v1/rpc/my_leagues",
                method = "POST",
                accessToken = accessToken,
                body = "{}"
            )
        )
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            item.toLeagueUi(isManager = item.optBoolean("is_manager", item.optString("owner_id") == userId))
                .copy(memberCount = item.optInt("member_count", 1).coerceAtLeast(1))
        }
    }

    suspend fun createLeague(accessToken: String, name: String, inviteCode: String, teamName: String): Result<LeagueUi> = runCatching {
        val leagueId = requestText(
            url = "$baseUrl/rest/v1/rpc/create_league_with_manager",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("requested_name", name)
                .put("requested_invite_code", inviteCode)
                .put("requested_team_name", teamName)
                .toString()
        ).trim('"')
        fetchLeague(accessToken, leagueId, isManager = true)
    }

    suspend fun joinLeague(accessToken: String, inviteCode: String, teamName: String): Result<LeagueUi> = runCatching {
        val leagueId = requestText(
            url = "$baseUrl/rest/v1/rpc/join_league_by_code",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("code", inviteCode)
                .put("requested_team_name", teamName)
                .toString()
        ).trim('"')
        fetchLeague(accessToken, leagueId, isManager = false)
    }

    suspend fun leaveLeague(accessToken: String, leagueId: String): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        requestText(
            url = "$baseUrl/rest/v1/rpc/leave_league",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject().put("target_league_id", leagueId).toString()
        )
    }

    suspend fun deleteLeague(accessToken: String, leagueId: String): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        requestText(
            url = "$baseUrl/rest/v1/rpc/delete_league",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject().put("target_league_id", leagueId).toString()
        )
    }

    suspend fun updateLeague(accessToken: String, league: LeagueUi): Result<Unit> = runCatching {
        if (accessToken.isBlank() || league.id.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        val body = JSONObject()
            .put("name", league.name)
            .put("invites_open", league.invitesOpen)
            .put("max_members", league.maxMembers)
            .put("draft_format", league.settings.draftFormat.name.lowercase())
            .put("pick_seconds", league.settings.pickSeconds)
            .put("season_weeks", league.settings.seasonWeeks)
            .put("headliner_slots", league.settings.headlinerSlots)
            .put("rising_slots", league.settings.risingSlots)
            .put("wildcard_slots", league.settings.wildcardSlots)
            .put("deep_cut_slots", league.settings.deepCutSlots)
            .put("bench_slots", league.settings.benchSlots)
            .put("max_waiver_claims", league.settings.maxWaiverClaims)
            .put("draft_status", league.draftStatus.name.lowercase())
            .put("current_pick_index", league.currentPickIndex)
        body.putNullable(
            "current_pick_started_at",
            league.currentPickStartedAt.ifBlank { null }
        )
        body.putNullable("draft_starts_at", league.settings.draftDateLabel.toDraftServerTimestamp())
        requestText(
            url = "$baseUrl/rest/v1/leagues?id=eq.${league.id}",
            method = "PATCH",
            accessToken = accessToken,
            body = body.toString()
        )
    }

    suspend fun advanceDraftPick(accessToken: String, leagueId: String, expectedPickIndex: Int): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        requestText(
            url = "$baseUrl/rest/v1/rpc/advance_draft_pick",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("target_league_id", leagueId)
                .put("expected_pick_index", expectedPickIndex)
                .toString()
        )
    }

    suspend fun startLiveDraft(accessToken: String, leagueId: String): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        requestText(
            url = "$baseUrl/rest/v1/rpc/start_live_draft",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("target_league_id", leagueId)
                .toString()
        )
    }

    suspend fun openDraftLobby(accessToken: String, leagueId: String): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        requestText(
            url = "$baseUrl/rest/v1/rpc/open_draft_lobby",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("target_league_id", leagueId)
                .toString()
        )
    }

    suspend fun delayDraftLobby(accessToken: String, leagueId: String): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        requestText(
            url = "$baseUrl/rest/v1/rpc/delay_draft_lobby",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("target_league_id", leagueId)
                .toString()
        )
    }

    suspend fun touchDraftPresence(accessToken: String, leagueId: String): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        requestText(
            url = "$baseUrl/rest/v1/rpc/touch_draft_room_presence",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("target_league_id", leagueId)
                .toString()
        )
    }

    suspend fun loadDraftPresence(accessToken: String, leagueId: String): Result<DraftPresenceUi> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching DraftPresenceUi()
        val raw = requestText(
            url = "$baseUrl/rest/v1/rpc/league_draft_presence",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("target_league_id", leagueId)
                .toString()
        )
        val array = JSONArray(raw)
        if (array.length() == 0) {
            DraftPresenceUi()
        } else {
            val item = array.getJSONObject(0)
            DraftPresenceUi(
                presentCount = item.optInt("present_count", 0),
                requiredCount = item.optInt("required_count", 2).coerceAtLeast(1),
                memberCount = item.optInt("member_count", 2).coerceAtLeast(1),
                readyAt = item.optString("ready_at")
            )
        }
    }

    suspend fun setMyAutoPick(accessToken: String, leagueId: String, enabled: Boolean): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        requestText(
            url = "$baseUrl/rest/v1/rpc/set_my_auto_pick",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("target_league_id", leagueId)
                .put("enabled", enabled)
                .toString()
        )
    }

    suspend fun makeDraftPick(
        accessToken: String,
        leagueId: String,
        expectedPickIndex: Int,
        artist: ArtistUi,
        slot: RosterSlot,
        autoPicked: Boolean
    ): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) error("Sign in again to make your pick.")
        val baseBody = JSONObject()
            .put("target_league_id", leagueId)
            .put("expected_pick_index", expectedPickIndex)
            .put("requested_artist_name", artist.name)
            .put("requested_provider_name", artist.source.ifBlank { "spotify" })
            .put("requested_provider_url", artist.spotifyId?.let { "spotify:artist:$it" }.orEmpty())
            .put("requested_image_url", artist.imageUrl.orEmpty())
            .put("requested_listeners", artist.listeners ?: 0L)
            .put("requested_playcount", artist.lastFmPlaycount ?: 0L)
            .put("requested_roster_slot", slot.name)
            .put("requested_acquisition_value", artist.projectedScore)
        val bodyWithAutoPick = JSONObject(baseBody.toString()).put("requested_auto_pick", autoPicked)
        try {
            requestText(
                url = "$baseUrl/rest/v1/rpc/make_draft_pick",
                method = "POST",
                accessToken = accessToken,
                body = bodyWithAutoPick.toString()
            )
        } catch (error: Throwable) {
            val message = error.message.orEmpty()
            if (
                message.contains("requested_auto_pick", ignoreCase = true) ||
                message.contains("function", ignoreCase = true) && message.contains("make_draft_pick", ignoreCase = true)
            ) {
                requestText(
                    url = "$baseUrl/rest/v1/rpc/make_draft_pick",
                    method = "POST",
                    accessToken = accessToken,
                    body = baseBody.toString()
                )
            } else {
                throw error
            }
        }
    }

    suspend fun loadDraftPicks(accessToken: String, leagueId: String): Result<List<DraftPickUi>> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching emptyList()
        val raw = requestText(
            url = "$baseUrl/rest/v1/rpc/league_draft_picks",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject().put("target_league_id", leagueId).toString()
        )
        val array = JSONArray(raw)
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            val slot = RosterSlot.entries.firstOrNull { it.name == item.optString("roster_slot") }
                ?: RosterSlot.BenchOne
            DraftPickUi(
                pickNumber = item.optInt("pick_number", index + 1),
                pickedBy = item.optString("username", "Member"),
                slot = slot,
                artist = ArtistUi(
                    id = null,
                    name = item.optString("artist_name", "Unknown Artist"),
                    listeners = if (item.isNull("listeners")) null else item.optLong("listeners"),
                    albumCount = null,
                    imageUrl = item.optString("image_url").ifBlank { null },
                    source = "Draft",
                    scoreStatus = "Drafted",
                    lastFmPlaycount = if (item.isNull("playcount")) null else item.optLong("playcount")
                ),
                secondsToPick = if (item.isNull("seconds_to_pick")) null else item.optInt("seconds_to_pick"),
                autoPicked = item.optBoolean("auto_picked", false)
            )
        }
    }

    suspend fun transferManager(accessToken: String, leagueId: String, username: String): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        requestText(
            url = "$baseUrl/rest/v1/rpc/transfer_league_manager",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("target_league_id", leagueId)
                .put("target_username", username.trim())
                .toString()
        )
    }

    suspend fun kickMember(accessToken: String, leagueId: String, username: String): Result<Unit> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching
        requestText(
            url = "$baseUrl/rest/v1/rpc/kick_league_member",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("target_league_id", leagueId)
                .put("target_username", username.trim())
                .toString()
        )
    }

    suspend fun loadMembers(accessToken: String, leagueId: String): Result<List<LeagueMemberUi>> = runCatching {
        if (accessToken.isBlank() || leagueId.isBlank() || !isOnlinePlayConfigured()) return@runCatching emptyList()
        val raw = requestText(
            url = "$baseUrl/rest/v1/rpc/league_member_list",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject().put("target_league_id", leagueId).toString()
        )
        val array = JSONArray(raw)
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            LeagueMemberUi(
                username = item.optString("username"),
                teamName = item.optString("username"),
                role = item.optString("role", "member"),
                autoPickEnabled = item.optBoolean("auto_pick_enabled", false)
            )
        }
    }

    private suspend fun fetchLeague(accessToken: String, leagueId: String, isManager: Boolean): LeagueUi {
        val array = JSONArray(
            requestText(
                url = "$baseUrl/rest/v1/leagues?id=eq.$leagueId&select=*",
                method = "GET",
                accessToken = accessToken
            )
        )
        if (array.length() == 0) error("League not found.")
        val league = array.getJSONObject(0).toLeagueUi(isManager)
        return league.copy(memberCount = activeMemberCount(accessToken, league.id))
    }

    private suspend fun activeMemberCount(accessToken: String, leagueId: String): Int {
        if (leagueId.isBlank()) return 1
        val raw = requestText(
            url = "$baseUrl/rest/v1/league_members?league_id=eq.$leagueId&status=eq.active&select=user_id",
            method = "GET",
            accessToken = accessToken
        )
        return JSONArray(raw).length().coerceAtLeast(1)
    }

    private suspend fun emailForUsername(username: String): String =
        requestText(
            url = "$baseUrl/rest/v1/rpc/email_for_username",
            method = "POST",
            accessToken = "",
            body = JSONObject().put("requested_username", username).toString()
        ).trim('"').also {
            if (it.isBlank() || it == "null") error("No account found for that username.")
        }

    private suspend fun isUsernameAvailable(username: String): Boolean =
        requestText(
            url = "$baseUrl/rest/v1/rpc/username_available",
            method = "POST",
            accessToken = "",
            body = JSONObject().put("requested_username", username).toString()
        ).trim().trim('"').equals("true", ignoreCase = true)

    private suspend fun isEmailAvailable(email: String): Boolean =
        runCatching {
            requestText(
                url = "$baseUrl/rest/v1/rpc/email_available",
                method = "POST",
                accessToken = "",
                body = JSONObject().put("requested_email", email.trim().lowercase()).toString()
            ).trim().trim('"').equals("true", ignoreCase = true)
        }.getOrDefault(true)

    private suspend fun isUsernameAvailableForAccount(username: String, userId: String): Boolean =
        if (userId.isBlank()) {
            isUsernameAvailable(username)
        } else {
            requestText(
                url = "$baseUrl/rest/v1/rpc/username_available_for_account",
                method = "POST",
                accessToken = "",
                body = JSONObject()
                    .put("requested_username", username)
                    .put("current_user_id", userId)
                    .toString()
            ).trim().trim('"').equals("true", ignoreCase = true)
        }

    private suspend fun signIn(email: String, password: String): Result<AccountUi> = runCatching {
        parseSession(
            requestText(
                url = "$baseUrl/auth/v1/token?grant_type=password",
                method = "POST",
                accessToken = "",
                body = JSONObject().put("email", email).put("password", password).toString()
            )
        )
    }

    private suspend fun signUp(email: String, username: String, password: String): Result<AccountUi> = runCatching {
        parseSession(
            requestText(
                url = "$baseUrl/auth/v1/signup",
                method = "POST",
                accessToken = "",
                body = JSONObject()
                    .put("email", email)
                    .put("password", password)
                    .put("data", JSONObject().put("username", username))
                    .toString()
            )
        )
    }

    private suspend fun upsertProfile(accessToken: String, email: String, username: String, mailingList: Boolean) {
        requestText(
            url = "$baseUrl/rest/v1/rpc/upsert_profile",
            method = "POST",
            accessToken = accessToken,
            body = JSONObject()
                .put("requested_email", email)
                .put("requested_username", username)
                .put("requested_display_name", username)
                .put("requested_mailing_list", mailingList)
                .toString()
        )
    }

    private suspend fun profileForUser(accessToken: String, userId: String): AccountUi? {
        val raw = requestText(
            url = "$baseUrl/rest/v1/profiles?id=eq.$userId&select=email,username,mailing_list_opt_in",
            method = "GET",
            accessToken = accessToken
        )
        val array = JSONArray(raw)
        if (array.length() == 0) return null
        val item = array.getJSONObject(0)
        return AccountUi(
            email = item.optString("email"),
            username = item.optString("username"),
            displayName = item.optString("username"),
            mailingList = item.optBoolean("mailing_list_opt_in", true),
            accessToken = accessToken,
            refreshToken = "",
            userId = userId
        )
    }

    private suspend fun authUser(accessToken: String): JSONObject =
        JSONObject(
            requestText(
                url = "$baseUrl/auth/v1/user",
                method = "GET",
                accessToken = accessToken
            )
        )

    private fun callbackParams(uri: Uri): JSONObject {
        val params = JSONObject()
        uri.fragment
            ?.split("&")
            ?.filter { it.isNotBlank() }
            ?.forEach { pair ->
                val key = pair.substringBefore("=")
                val value = pair.substringAfter("=", "")
                params.put(Uri.decode(key), Uri.decode(value))
            }
        uri.queryParameterNames.forEach { key ->
            params.put(key, uri.getQueryParameter(key).orEmpty())
        }
        return params
    }

    private fun parseSession(raw: String): AccountUi {
        val json = JSONObject(raw)
        val user = json.getJSONObject("user")
        if (!json.has("access_token") || json.isNull("access_token")) {
            error("Account created. This account is waiting for email verification before it can log in.")
        }
        val metadata = user.optJSONObject("user_metadata")
        val metadataUsername = metadata?.optString("username").orEmpty()
        return AccountUi(
            email = user.optString("email"),
            username = metadataUsername,
            displayName = metadataUsername,
            mailingList = true,
            accessToken = json.getString("access_token"),
            refreshToken = json.optString("refresh_token"),
            userId = user.optString("id")
        )
    }

    private fun JSONObject.toLeagueUi(isManager: Boolean): LeagueUi = LeagueUi(
        id = getString("id"),
        name = getString("name"),
        inviteCode = getString("invite_code"),
        onlineReady = true,
        maxMembers = optInt("max_members", 10).coerceIn(MinLeagueMembers, MaxLeagueMembers),
        invitesOpen = optBoolean("invites_open", true),
        isManager = isManager,
        draftStatus = DraftStatus.entries.firstOrNull { it.name.equals(optString("draft_status"), ignoreCase = true) } ?: DraftStatus.Scheduled,
        currentPickIndex = optInt("current_pick_index", 0),
        currentPickStartedAt = optString("current_pick_started_at"),
        lobbyReadyAt = optString("lobby_ready_at"),
        autoPickEnabled = optBoolean("auto_pick_enabled", false),
        settings = LeagueSettingsUi(
        draftFormat = DraftFormat.entries.firstOrNull { it.name.equals(optString("draft_format"), ignoreCase = true) } ?: DraftFormat.Snake,
        draftDateLabel = optString("draft_starts_at").cleanDraftDateLabel(),
        pickSeconds = optInt("pick_seconds", 90).coerceIn(30, 300),
        seasonWeeks = optInt("season_weeks", 10).coerceIn(MinSeasonWeeks, MaxSeasonWeeks),
            headlinerSlots = optInt("headliner_slots", 2).coerceIn(1, 4),
            risingSlots = optInt("rising_slots", 1).coerceIn(0, 4),
            wildcardSlots = optInt("wildcard_slots", 1).coerceIn(0, 4),
            deepCutSlots = optInt("deep_cut_slots", 1).coerceIn(0, 3),
            benchSlots = optInt("bench_slots", 2).coerceIn(0, 6),
            maxWaiverClaims = optInt("max_waiver_claims", 5).coerceIn(MinWaiverClaims, MaxWaiverClaims)
        )
    )

    private suspend fun requestText(url: String, method: String, accessToken: String, body: String? = null): String =
        withContext(Dispatchers.IO) {
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = 10_000
                readTimeout = 10_000
                setRequestProperty("apikey", BuildConfig.SUPABASE_ANON_KEY)
                setRequestProperty("Content-Type", "application/json")
                if (accessToken.isNotBlank()) {
                    setRequestProperty("Authorization", "Bearer $accessToken")
                }
                if (body != null) {
                    doOutput = true
                    outputStream.bufferedWriter().use { it.write(body) }
                }
            }
            val status = connection.responseCode
            val serverDate = connection.date
            if (serverDate > 0L) {
                serverClockOffsetMs = serverDate - System.currentTimeMillis()
            }
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (status !in 200..299) error(text.ifBlank { "Server request failed." })
            text
        }
}

internal object LocalBreakoutStore {
    private const val FileName = "breakout_state"
    private const val LeaguesKey = "leagues"
    private const val ActiveLeagueKey = "active_league"
    private const val RosterKey = "roster"
    private const val WaiversKey = "waivers"
    private const val DroppedArtistsKey = "dropped_artists"
    private const val DroppedArtistDatesKey = "dropped_artist_dates"
    private const val SnapshotsKey = "snapshots"
    private const val AccountKey = "account"

    fun loadAccount(context: Context): AccountUi? {
        val raw = prefs(context).getString(AccountKey, null) ?: return null
        return runCatching {
            JSONObject(raw).let { item ->
                AccountUi(
                    email = item.getString("email"),
                    username = item.optString("username"),
                    displayName = item.optString("displayName"),
                    mailingList = item.optBoolean("mailingList", true),
                    accessToken = item.optString("accessToken"),
                    refreshToken = item.optString("refreshToken"),
                    userId = item.optString("userId")
                )
            }
        }.getOrNull()
    }

    fun saveAccount(context: Context, account: AccountUi?) {
        val editor = prefs(context).edit()
        if (account == null) {
            editor.remove(AccountKey)
        } else {
            editor.putString(AccountKey, JSONObject().apply {
                put("email", account.email)
                put("username", account.username)
                put("displayName", account.displayName)
                put("mailingList", account.mailingList)
                put("accessToken", account.accessToken)
                put("refreshToken", account.refreshToken)
                put("userId", account.userId)
            }.toString())
        }
        editor.apply()
    }

    fun loadLeagues(context: Context): List<LeagueUi> {
        val raw = prefs(context).getString(LeaguesKey, "[]").orEmpty()
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                LeagueUi(
                    id = item.optString("id"),
                    name = item.getString("name"),
                    inviteCode = item.getString("inviteCode"),
                    onlineReady = item.optBoolean("onlineReady"),
                    memberCount = item.optInt("memberCount", 1),
                    maxMembers = item.optInt("maxMembers", 10).coerceIn(MinLeagueMembers, MaxLeagueMembers),
                    invitesOpen = item.optBoolean("invitesOpen", true),
                    isManager = item.optBoolean("isManager", true),
                    draftStatus = DraftStatus.entries.firstOrNull { it.name == item.optString("draftStatus") } ?: DraftStatus.Scheduled,
                    currentPickIndex = item.optInt("currentPickIndex", 0),
                    currentPickStartedAt = item.optString("currentPickStartedAt"),
                    lobbyReadyAt = item.optString("lobbyReadyAt"),
                    autoPickEnabled = item.optBoolean("autoPickEnabled", false),
                    settings = item.optJSONObject("settings")?.toLeagueSettings() ?: LeagueSettingsUi()
                )
            }
        }.getOrDefault(emptyList())
    }

    fun saveLeagues(context: Context, leagues: List<LeagueUi>) {
        val array = JSONArray()
        leagues.forEach { league ->
            array.put(JSONObject().apply {
                put("name", league.name)
                put("id", league.id)
                put("inviteCode", league.inviteCode)
                put("onlineReady", league.onlineReady)
                put("memberCount", league.memberCount)
                put("maxMembers", league.maxMembers)
                put("invitesOpen", league.invitesOpen)
                put("isManager", league.isManager)
                put("draftStatus", league.draftStatus.name)
                put("currentPickIndex", league.currentPickIndex)
                put("currentPickStartedAt", league.currentPickStartedAt)
                put("lobbyReadyAt", league.lobbyReadyAt)
                put("autoPickEnabled", league.autoPickEnabled)
                put("settings", league.settings.toJson())
            })
        }
        prefs(context).edit().putString(LeaguesKey, array.toString()).apply()
    }

    fun loadActiveLeagueCode(context: Context): String? = prefs(context).getString(ActiveLeagueKey, null)

    fun saveActiveLeagueCode(context: Context, code: String?) {
        prefs(context).edit().putString(ActiveLeagueKey, code).apply()
    }

    private fun rosterStorageKey(leagueKey: String?): String =
        leagueKey?.takeIf { it.isNotBlank() }?.let { "$RosterKey:$it" } ?: RosterKey

    private fun waiverStorageKey(leagueKey: String?): String =
        leagueKey?.takeIf { it.isNotBlank() }?.let { "$WaiversKey:$it" } ?: WaiversKey

    private fun droppedArtistsStorageKey(leagueKey: String?): String =
        leagueKey?.takeIf { it.isNotBlank() }?.let { "$DroppedArtistsKey:$it" } ?: DroppedArtistsKey

    private fun droppedArtistDatesStorageKey(leagueKey: String?): String =
        leagueKey?.takeIf { it.isNotBlank() }?.let { "$DroppedArtistDatesKey:$it" } ?: DroppedArtistDatesKey

    fun loadRoster(context: Context, leagueKey: String? = null): Map<RosterSlot, ArtistUi> {
        val raw = prefs(context).getString(rosterStorageKey(leagueKey), "[]").orEmpty()
        return runCatching {
            val array = JSONArray(raw)
            buildMap {
                List(array.length()) { index -> array.getJSONObject(index) }.forEachIndexed { index, item ->
                    val slotName = item.optString("slot")
                    val slot = RosterSlot.entries.firstOrNull { it.name == slotName }
                        ?: RosterSlot.entries.getOrNull(index)
                    val artist = item.toStoredArtist()
                    if (slot != null && artist.listeners != null) {
                        put(slot, artist)
                    }
                }
            }
        }.getOrDefault(emptyMap())
    }

    fun saveRoster(context: Context, roster: Map<RosterSlot, ArtistUi>, leagueKey: String? = null) {
        val array = JSONArray()
        roster.forEach { (slot, artist) ->
            array.put(JSONObject().apply {
                put("slot", slot.name)
                putStoredArtist(artist)
            })
        }
        prefs(context).edit().putString(rosterStorageKey(leagueKey), array.toString()).apply()
    }

    fun loadWaiverClaims(context: Context, leagueKey: String? = null): List<WaiverClaimUi> {
        val raw = prefs(context).getString(waiverStorageKey(leagueKey), "[]").orEmpty()
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { index -> array.getJSONObject(index) }.mapNotNull { item ->
                val slot = RosterSlot.entries.firstOrNull { it.name == item.optString("slot") } ?: return@mapNotNull null
                val dropSlot = RosterSlot.entries.firstOrNull { it.name == item.optString("dropSlot") }
                val artist = item.toStoredArtist()
                WaiverClaimUi(artist, slot, dropSlot)
            }
        }.getOrDefault(emptyList())
    }

    fun saveWaiverClaims(context: Context, claims: List<WaiverClaimUi>, leagueKey: String? = null) {
        val array = JSONArray()
        claims.forEach { claim ->
            array.put(JSONObject().apply {
                put("slot", claim.slot.name)
                claim.dropSlot?.let { put("dropSlot", it.name) }
                putStoredArtist(claim.artist)
            })
        }
        prefs(context).edit().putString(waiverStorageKey(leagueKey), array.toString()).apply()
    }

    fun loadDroppedArtists(context: Context, leagueKey: String? = null): Set<String> {
        val raw = prefs(context).getString(droppedArtistsStorageKey(leagueKey), "[]").orEmpty()
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { index -> array.optString(index) }
                .filter { it.isNotBlank() }
                .toSet()
        }.getOrDefault(emptySet())
    }

    fun saveDroppedArtists(context: Context, names: Set<String>, leagueKey: String? = null) {
        val array = JSONArray()
        names.forEach { array.put(it) }
        prefs(context).edit().putString(droppedArtistsStorageKey(leagueKey), array.toString()).apply()
    }

    fun loadDroppedArtistDates(context: Context, leagueKey: String? = null): Map<String, Long> {
        val raw = prefs(context).getString(droppedArtistDatesStorageKey(leagueKey), "{}").orEmpty()
        return runCatching {
            val item = JSONObject(raw.ifBlank { "{}" })
            item.keys().asSequence().associateWith { key -> item.optLong(key) }
        }.getOrDefault(emptyMap())
    }

    fun saveDroppedArtistDates(context: Context, dates: Map<String, Long>, leagueKey: String? = null) {
        val item = JSONObject()
        dates.forEach { (name, date) -> item.put(name, date) }
        prefs(context).edit().putString(droppedArtistDatesStorageKey(leagueKey), item.toString()).apply()
    }

    fun clearLeagueState(context: Context, leagueKey: String) {
        prefs(context).edit()
            .remove(rosterStorageKey(leagueKey))
            .remove(waiverStorageKey(leagueKey))
            .remove(droppedArtistsStorageKey(leagueKey))
            .remove(droppedArtistDatesStorageKey(leagueKey))
            .apply()
    }

    fun updateSnapshots(context: Context, artists: List<ArtistUi>): Map<String, SnapshotUi> {
        val prefs = prefs(context)
        val existing = JSONObject(prefs.getString(SnapshotsKey, "{}").orEmpty().ifBlank { "{}" })
        val now = System.currentTimeMillis()
        artists.forEach { artist ->
            val listeners = artist.listeners ?: return@forEach
            val key = artist.name.lowercase()
            val previous = existing.optJSONObject(key)
            val firstListeners = previous?.optLong("previousListeners", listeners) ?: listeners
            val firstTrackPopularity = previous?.nullableInt("previousTrackPopularity") ?: artist.trackPopularity
            val firstLastFmListeners = previous?.nullableLong("previousLastFmListeners") ?: artist.lastFmListeners
            val firstLastFmPlaycount = previous?.nullableLong("previousLastFmPlaycount") ?: artist.lastFmPlaycount
            val firstCapturedAt = previous?.optLong("capturedAt", now) ?: now
            existing.put(key, JSONObject().apply {
                put("previousListeners", firstListeners)
                put("currentListeners", listeners)
                putNullable("previousTrackPopularity", firstTrackPopularity)
                putNullable("currentTrackPopularity", artist.trackPopularity)
                putNullable("previousLastFmListeners", firstLastFmListeners)
                putNullable("currentLastFmListeners", artist.lastFmListeners)
                putNullable("previousLastFmPlaycount", firstLastFmPlaycount)
                putNullable("currentLastFmPlaycount", artist.lastFmPlaycount)
                put("releaseRecencyScore", artist.releaseRecencyScore ?: previous?.optDouble("releaseRecencyScore", 0.0) ?: 0.0)
                put("capturedAt", firstCapturedAt)
            })
        }
        prefs.edit().putString(SnapshotsKey, existing.toString()).apply()
        return existing.keys().asSequence().associateWith { key ->
            existing.getJSONObject(key).let {
                SnapshotUi(
                    previousListeners = it.getLong("previousListeners"),
                    currentListeners = it.getLong("currentListeners"),
                    previousTrackPopularity = it.nullableInt("previousTrackPopularity"),
                    currentTrackPopularity = it.nullableInt("currentTrackPopularity"),
                    previousLastFmListeners = it.nullableLong("previousLastFmListeners"),
                    currentLastFmListeners = it.nullableLong("currentLastFmListeners"),
                    previousLastFmPlaycount = it.nullableLong("previousLastFmPlaycount"),
                    currentLastFmPlaycount = it.nullableLong("currentLastFmPlaycount"),
                    releaseRecencyScore = it.optDouble("releaseRecencyScore", 0.0),
                    capturedAt = it.getLong("capturedAt")
                )
            }
        }
    }

    private fun JSONObject.toStoredArtist(): ArtistUi = ArtistUi(
        id = if (isNull("id")) null else optLong("id"),
        name = getString("name"),
        listeners = if (isNull("listeners")) null else optLong("listeners"),
        albumCount = if (isNull("albumCount")) null else optInt("albumCount"),
        imageUrl = optString("imageUrl").ifBlank { null },
        source = optString("source", "Music data"),
        scoreStatus = optString("scoreStatus", "Market watch"),
        spotifyId = optString("spotifyId").ifBlank { null },
        spotifyPopularity = if (isNull("spotifyPopularity")) null else optInt("spotifyPopularity"),
        trackPopularity = if (isNull("trackPopularity")) null else optInt("trackPopularity"),
        lastFmListeners = if (isNull("lastFmListeners")) null else optLong("lastFmListeners"),
        lastFmPlaycount = if (isNull("lastFmPlaycount")) null else optLong("lastFmPlaycount"),
        kworbRank = if (isNull("kworbRank")) null else optInt("kworbRank"),
        kworbDailyListenerChange = if (isNull("kworbDailyListenerChange")) null else optLong("kworbDailyListenerChange"),
        kworbPeakListeners = if (isNull("kworbPeakListeners")) null else optLong("kworbPeakListeners"),
        kworbTotalStreams = if (isNull("kworbTotalStreams")) null else optLong("kworbTotalStreams"),
        kworbLeadStreams = if (isNull("kworbLeadStreams")) null else optLong("kworbLeadStreams"),
        kworbSoloStreams = if (isNull("kworbSoloStreams")) null else optLong("kworbSoloStreams"),
        kworbFeatureStreams = if (isNull("kworbFeatureStreams")) null else optLong("kworbFeatureStreams"),
        kworbLeadDailyStreams = if (isNull("kworbLeadDailyStreams")) null else optLong("kworbLeadDailyStreams"),
        kworbSoloDailyStreams = if (isNull("kworbSoloDailyStreams")) null else optLong("kworbSoloDailyStreams"),
        kworbFeatureDailyStreams = if (isNull("kworbFeatureDailyStreams")) null else optLong("kworbFeatureDailyStreams"),
        kworbDataUpdatedAt = optString("kworbDataUpdatedAt").ifBlank { null },
        kworbDailyStreams = if (isNull("kworbDailyStreams")) null else optLong("kworbDailyStreams"),
        kworbTopSongTitle = optString("kworbTopSongTitle").ifBlank { null },
        kworbTopSongStreams = if (isNull("kworbTopSongStreams")) null else optLong("kworbTopSongStreams"),
        kworbTopSongDailyStreams = if (isNull("kworbTopSongDailyStreams")) null else optLong("kworbTopSongDailyStreams"),
        topTrackImageUrl = optString("topTrackImageUrl").ifBlank { null },
        topTrackReleaseDate = optString("topTrackReleaseDate").ifBlank { null },
        kworbDailyTopSongTitle = optString("kworbDailyTopSongTitle").ifBlank { null },
        kworbDailyTopSongStreams = if (isNull("kworbDailyTopSongStreams")) null else optLong("kworbDailyTopSongStreams"),
        latestReleaseTitle = optString("latestReleaseTitle").ifBlank { null },
        latestReleaseDate = optString("latestReleaseDate").ifBlank { null },
        latestReleaseImageUrl = optString("latestReleaseImageUrl").ifBlank { null },
        latestReleaseType = optString("latestReleaseType").ifBlank { null },
        releaseRecencyScore = if (isNull("releaseRecencyScore")) null else optDouble("releaseRecencyScore")
    )

    private fun JSONObject.putStoredArtist(artist: ArtistUi) {
        put("id", artist.id)
        put("name", artist.name)
        put("listeners", artist.listeners)
        put("albumCount", artist.albumCount)
        put("imageUrl", artist.imageUrl)
        put("source", artist.source)
        put("scoreStatus", artist.scoreStatus)
        put("spotifyId", artist.spotifyId)
        put("spotifyPopularity", artist.spotifyPopularity)
        put("trackPopularity", artist.trackPopularity)
        put("lastFmListeners", artist.lastFmListeners)
        put("lastFmPlaycount", artist.lastFmPlaycount)
        put("kworbRank", artist.kworbRank)
        put("kworbDailyListenerChange", artist.kworbDailyListenerChange)
        put("kworbPeakListeners", artist.kworbPeakListeners)
        put("kworbTotalStreams", artist.kworbTotalStreams)
        put("kworbLeadStreams", artist.kworbLeadStreams)
        put("kworbSoloStreams", artist.kworbSoloStreams)
        put("kworbFeatureStreams", artist.kworbFeatureStreams)
        put("kworbLeadDailyStreams", artist.kworbLeadDailyStreams)
        put("kworbSoloDailyStreams", artist.kworbSoloDailyStreams)
        put("kworbFeatureDailyStreams", artist.kworbFeatureDailyStreams)
        put("kworbDataUpdatedAt", artist.kworbDataUpdatedAt)
        put("kworbDailyStreams", artist.kworbDailyStreams)
        put("kworbTopSongTitle", artist.kworbTopSongTitle)
        put("kworbTopSongStreams", artist.kworbTopSongStreams)
        put("kworbTopSongDailyStreams", artist.kworbTopSongDailyStreams)
        put("topTrackImageUrl", artist.topTrackImageUrl)
        put("topTrackReleaseDate", artist.topTrackReleaseDate)
        put("kworbDailyTopSongTitle", artist.kworbDailyTopSongTitle)
        put("kworbDailyTopSongStreams", artist.kworbDailyTopSongStreams)
        put("latestReleaseTitle", artist.latestReleaseTitle)
        put("latestReleaseDate", artist.latestReleaseDate)
        put("latestReleaseImageUrl", artist.latestReleaseImageUrl)
        put("latestReleaseType", artist.latestReleaseType)
        put("releaseRecencyScore", artist.releaseRecencyScore)
    }

    private fun JSONObject.toLeagueSettings(): LeagueSettingsUi = LeagueSettingsUi(
        draftFormat = DraftFormat.entries.firstOrNull { it.name == optString("draftFormat") } ?: DraftFormat.Snake,
        draftDateLabel = optString("draftDateLabel", "Set date").cleanDraftDateLabel(),
        pickSeconds = optInt("pickSeconds", 90).coerceIn(30, 300),
        seasonWeeks = optInt("seasonWeeks", 10).coerceIn(MinSeasonWeeks, MaxSeasonWeeks),
        headlinerSlots = optInt("headlinerSlots", 2).coerceIn(1, 4),
        risingSlots = optInt("risingSlots", 1).coerceIn(0, 4),
        wildcardSlots = optInt("wildcardSlots", 1).coerceIn(0, 4),
        deepCutSlots = optInt("deepCutSlots", 1).coerceIn(0, 3),
        benchSlots = optInt("benchSlots", 2).coerceIn(0, 6),
        maxWaiverClaims = optInt("maxWaiverClaims", 5).coerceIn(MinWaiverClaims, MaxWaiverClaims)
    )

    private fun LeagueSettingsUi.toJson(): JSONObject = JSONObject().apply {
        put("draftFormat", draftFormat.name)
        put("draftDateLabel", draftDateLabel)
        put("pickSeconds", pickSeconds)
        put("seasonWeeks", seasonWeeks)
        put("headlinerSlots", headlinerSlots)
        put("risingSlots", risingSlots)
        put("wildcardSlots", wildcardSlots)
        put("deepCutSlots", deepCutSlots)
        put("benchSlots", benchSlots)
        put("maxWaiverClaims", maxWaiverClaims)
    }

    private fun prefs(context: Context) = context.getSharedPreferences(FileName, Context.MODE_PRIVATE)
}

@Composable
internal fun BreakoutApp(
    oauthCallbackUri: Uri? = null,
    onOauthCallbackHandled: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var account by remember { mutableStateOf(LocalBreakoutStore.loadAccount(context)) }
    var leagues by remember { mutableStateOf(LocalBreakoutStore.loadLeagues(context)) }
    var activeLeagueCode by rememberSaveable { mutableStateOf(LocalBreakoutStore.loadActiveLeagueCode(context)) }
    var selectedTab by rememberSaveable { mutableStateOf(BreakoutTab.Home) }
    var previousTab by rememberSaveable { mutableStateOf(BreakoutTab.Home) }
    var marketStartFilter by rememberSaveable { mutableStateOf(MarketFilter.Headliners) }
    var marketQuery by rememberSaveable { mutableStateOf("") }
    var marketActiveFilter by rememberSaveable { mutableStateOf<MarketFilter?>(MarketFilter.Headliners) }
    var marketPreviousFilter by rememberSaveable { mutableStateOf(MarketFilter.Headliners) }
    var marketState by remember { mutableStateOf<MarketState>(MarketState.Loading) }
    var marketSnapshots by remember { mutableStateOf<Map<String, SnapshotUi>>(emptyMap()) }
    var openMarketActionArtistKey by rememberSaveable { mutableStateOf<String?>(null) }
    var marketVisibleCount by rememberSaveable { mutableStateOf(20) }
    var marketLastKey by rememberSaveable { mutableStateOf<String?>(null) }
    var marketLoadedKey by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedArtist by remember { mutableStateOf<ArtistUi?>(null) }
    var selectedArtistReadOnly by remember { mutableStateOf(false) }
    var roster by remember { mutableStateOf<Map<RosterSlot, ArtistUi>>(emptyMap()) }
    var draftLeagueName by rememberSaveable { mutableStateOf("") }
    var draftInviteCode by rememberSaveable { mutableStateOf("") }
    var joinError by rememberSaveable { mutableStateOf<String?>(null) }
    var draftPickMode by rememberSaveable { mutableStateOf(false) }
    var draftRoomOpen by rememberSaveable { mutableStateOf(false) }
    var draftRoomLeagueId by rememberSaveable { mutableStateOf<String?>(null) }
    var passwordResetOpen by rememberSaveable { mutableStateOf(false) }
    var waiverClaims by remember { mutableStateOf<List<WaiverClaimUi>>(emptyList()) }
    var authGate by remember { mutableStateOf(if (isOnlinePlayConfigured()) AuthGate.Checking else AuthGate.SignedIn) }
    var refreshingLeagueData by remember { mutableStateOf(false) }
    var leagueMembersById by remember { mutableStateOf<Map<String, List<LeagueMemberUi>>>(emptyMap()) }
    var draftPicksByLeagueId by remember { mutableStateOf<Map<String, List<DraftPickUi>>>(emptyMap()) }
    var draftPresenceByLeagueId by remember { mutableStateOf<Map<String, DraftPresenceUi>>(emptyMap()) }
    var droppedArtistsByLeagueId by remember { mutableStateOf<Map<String, Set<String>>>(emptyMap()) }
    var droppedArtistDatesByLeagueId by remember { mutableStateOf<Map<String, Map<String, Long>>>(emptyMap()) }
    var preloadedMarketArtists by remember { mutableStateOf<List<ArtistUi>?>(null) }
    var managerStatusByLeagueId by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }
    var startupLoading by remember { mutableStateOf(true) }
    var startupProgress by remember { mutableStateOf(0f) }
    var startupMessage by remember { mutableStateOf("Opening Breakout") }
    var pendingDraftExitTab by remember { mutableStateOf<BreakoutTab?>(null) }
    var navigationRefreshTick by remember { mutableStateOf(0) }
    var restoreRosterMemberUsername by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedAllMatchupsWeek by rememberSaveable { mutableStateOf(1) }
    var forcedAutoPickLeagueIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    val marketListState = rememberLazyListState()
    val league = leagues.firstOrNull { it.inviteCode == activeLeagueCode } ?: leagues.firstOrNull()
    val requiresOnlineAccount = isOnlinePlayConfigured()
    val hasUsableAccount = if (requiresOnlineAccount) {
        authGate == AuthGate.SignedIn && account?.accessToken?.isNotBlank() == true
    } else {
        account != null
    }

    LaunchedEffect(oauthCallbackUri) {
        val uri = oauthCallbackUri ?: return@LaunchedEffect
        if (uri.scheme == "breakout" && uri.host == "auth-callback") {
            val callback = SupabaseLeagueService.accountFromAuthCallback(uri).getOrNull()
            val callbackAccount = callback?.account
            if (callbackAccount?.accessToken?.isNotBlank() == true) {
                val repaired = SupabaseLeagueService.ensureProfile(callbackAccount).getOrNull() ?: callbackAccount
                account = repaired
                authGate = AuthGate.SignedIn
                passwordResetOpen = callback?.isPasswordRecovery == true
                joinError = null
                LocalBreakoutStore.saveAccount(context, repaired)
            } else {
                authGate = AuthGate.SignedOut
            }
            onOauthCallbackHandled()
        }
    }

    suspend fun currentOnlineAccount(): AccountUi? {
        val current = account ?: return null
        if (!isOnlinePlayConfigured()) return null
        if (current.accessToken.isBlank()) {
            account = current.copy(accessToken = "", refreshToken = "")
            return null
        }
        val ensuredResult = SupabaseLeagueService.ensureProfile(current)
        val ensured = ensuredResult.getOrNull()
        if (ensured != null) {
            account = ensured
            return ensured
        }
        val ensureError = ensuredResult.exceptionOrNull()
        if (current.refreshToken.isNotBlank() && isAuthFailure(ensureError?.message)) {
            val refreshed = SupabaseLeagueService.refreshSession(current.refreshToken).getOrNull()
            if (refreshed?.accessToken?.isNotBlank() == true) {
                val merged = current.copy(
                    accessToken = refreshed.accessToken,
                    refreshToken = refreshed.refreshToken.ifBlank { current.refreshToken },
                    userId = refreshed.userId.ifBlank { current.userId },
                    email = refreshed.email.ifBlank { current.email }
                )
                val repaired = SupabaseLeagueService.ensureProfile(merged).getOrNull() ?: merged
                account = repaired
                return repaired
            }
            account = current.copy(accessToken = "", refreshToken = "")
            return null
        }
        return current
    }

    fun applyRemoteLeagues(remoteLeagues: List<LeagueUi>, notifyDeleted: Boolean = true) {
        val previousActive = league
        val activeWasRemoved = previousActive != null &&
            remoteLeagues.none { remote ->
                (previousActive.id.isNotBlank() && remote.id == previousActive.id) ||
                    remote.inviteCode == previousActive.inviteCode
            }
        leagues = remoteLeagues
        activeLeagueCode = remoteLeagues.firstOrNull { it.inviteCode == activeLeagueCode }?.inviteCode
            ?: remoteLeagues.firstOrNull()?.inviteCode
        if (activeWasRemoved) {
            if (notifyDeleted) {
                Toast.makeText(context, "This league was deleted.", Toast.LENGTH_LONG).show()
            }
            roster = emptyMap()
            selectedArtist = null
            selectedArtistReadOnly = false
            draftPickMode = false
            draftRoomOpen = false
            draftRoomLeagueId = null
            pendingDraftExitTab = null
            selectedTab = BreakoutTab.Home
            previousActive?.id?.takeIf { it.isNotBlank() }?.let { removedId ->
                leagueMembersById = leagueMembersById - removedId
                draftPicksByLeagueId = draftPicksByLeagueId - removedId
                draftPresenceByLeagueId = draftPresenceByLeagueId - removedId
                droppedArtistsByLeagueId = droppedArtistsByLeagueId - removedId
                droppedArtistDatesByLeagueId = droppedArtistDatesByLeagueId - removedId
                LocalBreakoutStore.clearLeagueState(context, removedId)
            }
        }
    }

    fun rosterForPicker(leagueId: String, pickerUsername: String): Map<RosterSlot, ArtistUi> =
        draftPicksByLeagueId[leagueId].orEmpty()
            .filter { it.pickedBy.equals(pickerUsername, ignoreCase = true) }
            .associate { it.slot to it.artist }

    fun rosterForAccount(activeLeague: LeagueUi): Map<RosterSlot, ArtistUi> {
        val username = account?.username.orEmpty()
        if (username.isBlank()) return roster
        val droppedNames = droppedArtistsByLeagueId[activeLeague.id].orEmpty()
        val draftedRoster = rosterForPicker(activeLeague.id, username)
            .filterValues { it.name.lowercase() !in droppedNames }
        return if (activeLeague.draftStatus == DraftStatus.Live || activeLeague.draftStatus == DraftStatus.Complete) {
            val localNames = roster.values.map { it.name.lowercase() }.toSet()
            val draftedNames = draftedRoster.values.map { it.name.lowercase() }.toSet()
            if (activeLeague.draftStatus == DraftStatus.Complete && roster.isNotEmpty() && localNames == draftedNames) {
                roster.filterValues { it.name.lowercase() !in droppedNames }
            } else {
                draftedRoster
            }
        } else {
            roster
        }
    }

    fun makeDraftPick(artist: ArtistUi, activeLeague: LeagueUi, autoPicked: Boolean = false) {
        if (activeLeague.draftStatus != DraftStatus.Live || activeLeague.memberCount < 2) return
        val draftRoster = if (activeLeague.currentPickIndex == 0) emptyMap() else rosterForAccount(activeLeague)
        if (draftRoster.values.any { it.name == artist.name }) return
        if (draftPicksByLeagueId[activeLeague.id].orEmpty().any { it.artist.name.equals(artist.name, ignoreCase = true) }) return
        val slot = firstOpenSlotFor(artist, draftRoster, activeLeague.settings) ?: return
        scope.launch {
            val current = currentOnlineAccount()?.takeIf { it.accessToken.isNotBlank() }
            if (current == null) {
                Toast.makeText(context, "Sign in again to make your pick.", Toast.LENGTH_SHORT).show()
                return@launch
            }
            SupabaseLeagueService.makeDraftPick(
                accessToken = current.accessToken,
                leagueId = activeLeague.id,
                expectedPickIndex = activeLeague.currentPickIndex,
                artist = artist,
                slot = slot,
                autoPicked = autoPicked
            ).onSuccess {
                roster = draftRoster + (slot to artist)
                SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                    applyRemoteLeagues(remoteLeagues)
                }
                SupabaseLeagueService.loadDraftPicks(current.accessToken, activeLeague.id).onSuccess { picks ->
                    draftPicksByLeagueId = draftPicksByLeagueId + (activeLeague.id to picks)
                }
                draftPickMode = false
                selectedArtist = null
                selectedTab = BreakoutTab.Draft
            }.onFailure { error ->
                val message = error.message.orEmpty()
                if (!message.contains("already moved", ignoreCase = true) &&
                    !message.contains("already been drafted", ignoreCase = true)
                ) {
                    Toast.makeText(context, friendlyDraftPickError(message), Toast.LENGTH_LONG).show()
                }
                SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                    applyRemoteLeagues(remoteLeagues)
                }
                SupabaseLeagueService.loadDraftPicks(current.accessToken, activeLeague.id).onSuccess { picks ->
                    draftPicksByLeagueId = draftPicksByLeagueId + (activeLeague.id to picks)
                }
            }
        }
    }

    fun makeDueAutoPick(activeLeague: LeagueUi, reasonableDelayElapsed: Boolean = false) {
        if (activeLeague.draftStatus != DraftStatus.Live || activeLeague.memberCount < 2) return
        val pickerUsername = currentDraftPickerUsername(activeLeague, leagueMembersById[activeLeague.id]) ?: return
        val pickerMember = leagueMembersById[activeLeague.id].orEmpty()
            .firstOrNull { it.username.equals(pickerUsername, ignoreCase = true) }
        val pickStartedMs = activeLeague.currentPickStartedAt.parseServerInstantMillis() ?: return
        val elapsedMs = SupabaseLeagueService.syncedNowMillis() - pickStartedMs
        val due = elapsedMs >= activeLeague.settings.pickSeconds * 1000L ||
            (pickerMember?.autoPickEnabled == true && (reasonableDelayElapsed || elapsedMs >= 5_000L))
        if (!due) return
        val draftedNames = draftPicksByLeagueId[activeLeague.id].orEmpty().map { it.artist.name.lowercase() }.toSet()
        val pickerRoster = rosterForPicker(activeLeague.id, pickerUsername)
        scope.launch {
            val current = currentOnlineAccount()?.takeIf { it.accessToken.isNotBlank() } ?: return@launch
            val pick = bestEligibleDraftPick(pickerRoster, activeLeague, draftedNames) ?: return@launch
            val slot = firstOpenSlotFor(pick, pickerRoster, activeLeague.settings) ?: return@launch
            SupabaseLeagueService.makeDraftPick(
                accessToken = current.accessToken,
                leagueId = activeLeague.id,
                expectedPickIndex = activeLeague.currentPickIndex,
                artist = pick,
                slot = slot,
                autoPicked = true
            ).onSuccess {
                SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                    applyRemoteLeagues(remoteLeagues)
                }
                SupabaseLeagueService.loadDraftPicks(current.accessToken, activeLeague.id).onSuccess { picks ->
                    draftPicksByLeagueId = draftPicksByLeagueId + (activeLeague.id to picks)
                }
            }.onFailure { error ->
                if (!error.message.orEmpty().contains("already moved", ignoreCase = true)) {
                    SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { applyRemoteLeagues(it) }
                }
            }
        }
    }

    fun openDraftLobbyFor(target: LeagueUi) {
        if (target.draftStatus != DraftStatus.Scheduled || target.memberCount < 2) return
        scope.launch {
            val current = currentOnlineAccount()?.takeIf { it.accessToken.isNotBlank() } ?: return@launch
            SupabaseLeagueService.openDraftLobby(current.accessToken, target.id)
                .onSuccess {
                    SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                        applyRemoteLeagues(remoteLeagues)
                    }
                    SupabaseLeagueService.loadDraftPresence(current.accessToken, target.id).onSuccess { presence ->
                        draftPresenceByLeagueId = draftPresenceByLeagueId + (target.id to presence)
                    }
                }
                .onFailure {
                    Toast.makeText(context, friendlyDraftStartError(it.message), Toast.LENGTH_LONG).show()
                }
        }
    }

    fun startLiveDraftFromLobby(target: LeagueUi) {
        if (target.draftStatus != DraftStatus.Lobby || target.memberCount < 2) return
        scope.launch {
            val current = currentOnlineAccount()?.takeIf { it.accessToken.isNotBlank() } ?: return@launch
            SupabaseLeagueService.startLiveDraft(current.accessToken, target.id)
                .onSuccess {
                    roster = emptyMap()
                    draftPickMode = false
                    SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                        applyRemoteLeagues(remoteLeagues)
                    }
                    SupabaseLeagueService.loadDraftPicks(current.accessToken, target.id).onSuccess { picks ->
                        draftPicksByLeagueId = draftPicksByLeagueId + (target.id to picks)
                    }
                }
                .onFailure {
                    SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                        applyRemoteLeagues(remoteLeagues)
                    }
                }
        }
    }

    fun delayDraftLobbyFor(target: LeagueUi) {
        if (target.draftStatus != DraftStatus.Lobby) return
        scope.launch {
            val current = currentOnlineAccount()?.takeIf { it.accessToken.isNotBlank() } ?: return@launch
            SupabaseLeagueService.delayDraftLobby(current.accessToken, target.id)
                .onSuccess {
                    Toast.makeText(context, "Draft delayed ${DraftLobbyDelayMinutes} minutes while members join.", Toast.LENGTH_LONG).show()
                    showDraftSystemNotification(
                        context = context,
                        title = "Draft delayed",
                        message = "${target.name} will wait for more members before starting.",
                        notificationId = target.id.hashCode() + 7
                    )
                    SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                        applyRemoteLeagues(remoteLeagues)
                    }
                }
        }
    }

    fun createLeague(name: String) {
        val cleanedName = name.trim()
        if (cleanedName.isBlank()) return
        val inviteCode = generateInviteCode()
        scope.launch {
            val currentAccount = currentOnlineAccount()
            if (account != null && isOnlinePlayConfigured() && currentAccount?.accessToken.isNullOrBlank()) {
                joinError = "Sign in again to continue."
                account = account?.copy(accessToken = "", refreshToken = "")
                authGate = AuthGate.SignedOut
                return@launch
            }
            val newLeague = if (currentAccount?.accessToken?.isNotBlank() == true && isOnlinePlayConfigured()) {
                SupabaseLeagueService.createLeague(currentAccount.accessToken, cleanedName, inviteCode, currentAccount.username)
                    .getOrElse {
                        val message = friendlyLeagueError(it.message)
                        joinError = message
                        if (isSignInAgainMessage(message)) {
                            account = currentAccount.copy(accessToken = "", refreshToken = "")
                            authGate = AuthGate.SignedOut
                        }
                        return@launch
                    }
            } else {
                LeagueUi(name = cleanedName, inviteCode = inviteCode, onlineReady = isOnlinePlayConfigured(), isManager = true)
            }
            leagues = if (currentAccount?.accessToken?.isNotBlank() == true && isOnlinePlayConfigured()) {
                SupabaseLeagueService.loadLeagues(currentAccount.accessToken, currentAccount.userId).getOrDefault(
                    leagues.filterNot { it.id.isNotBlank() && it.id == newLeague.id } + newLeague
                )
            } else {
                leagues.filterNot { it.id.isNotBlank() && it.id == newLeague.id } + newLeague
            }
            activeLeagueCode = newLeague.inviteCode
            draftLeagueName = ""
            joinError = null
            selectedTab = BreakoutTab.Home
            drawerState.close()
        }
    }

    fun joinLeague(code: String) {
        val cleanedCode = code.trim().uppercase()
        if (!isValidInviteCode(cleanedCode)) {
            joinError = "Invite codes must be exactly 6 characters."
            return
        }
        val existing = leagues.firstOrNull { it.inviteCode == cleanedCode }
        if (existing != null) {
            joinError = "You are already in that league."
            return
        }
        scope.launch {
            val currentAccount = currentOnlineAccount()
            if (currentAccount?.accessToken?.isBlank() != false || !isOnlinePlayConfigured()) {
                joinError = "Your saved account needs to sign in again before joining leagues from another device."
                authGate = AuthGate.SignedOut
                return@launch
            }
            val joinedLeague = SupabaseLeagueService.joinLeague(currentAccount.accessToken, cleanedCode, currentAccount.username)
                .getOrElse {
                    val message = friendlyJoinError(it.message)
                    joinError = message
                    if (isSignInAgainMessage(message)) {
                        account = currentAccount.copy(accessToken = "", refreshToken = "")
                        authGate = AuthGate.SignedOut
                    }
                    return@launch
                }
            if (leagues.any { it.id.isNotBlank() && it.id == joinedLeague.id }) {
                joinError = "You are already in that league."
                return@launch
            }
            leagues = SupabaseLeagueService.loadLeagues(currentAccount.accessToken, currentAccount.userId)
                .getOrDefault(leagues + joinedLeague)
            activeLeagueCode = joinedLeague.inviteCode
            draftInviteCode = ""
            joinError = null
            selectedTab = BreakoutTab.Home
            drawerState.close()
        }
    }

    fun leaveLeague(target: LeagueUi) {
        scope.launch {
            currentOnlineAccount()?.accessToken?.takeIf { it.isNotBlank() }?.let { token ->
                SupabaseLeagueService.leaveLeague(token, target.id)
            }
            drawerState.close()
        }
        val remainingLeagues = leagues.filterNot { it.inviteCode == target.inviteCode }
        leagues = remainingLeagues
        joinError = null
        selectedArtist = null
        draftPickMode = false
        if (draftRoomLeagueId == target.id) {
            draftRoomOpen = false
            draftPickMode = false
            draftRoomLeagueId = null
        }
        if (activeLeagueCode == target.inviteCode) {
            activeLeagueCode = remainingLeagues.firstOrNull()?.inviteCode
            roster = emptyMap()
            selectedTab = BreakoutTab.Home
        }
    }

    fun deleteLeague(target: LeagueUi) {
        if (!target.isManager) return
        scope.launch {
            val current = currentOnlineAccount()
            if (current?.accessToken.isNullOrBlank()) {
                joinError = "Your session expired. Please log in again."
                authGate = AuthGate.SignedOut
                return@launch
            }
            SupabaseLeagueService.deleteLeague(current!!.accessToken, target.id)
                .onSuccess {
                    val remainingLeagues = leagues.filterNot { it.id == target.id || it.inviteCode == target.inviteCode }
                    leagues = remainingLeagues
                    activeLeagueCode = remainingLeagues.firstOrNull()?.inviteCode
                    roster = emptyMap()
                    selectedArtist = null
                    draftRoomOpen = false
                    draftPickMode = false
                    draftRoomLeagueId = null
                    selectedTab = BreakoutTab.Home
                    joinError = null
                    drawerState.close()
                }
                .onFailure {
                    joinError = friendlyLeagueError(it.message)
                }
        }
    }

    fun updateActiveLeague(transform: (LeagueUi) -> LeagueUi) {
        val current = league ?: return
        val requested = transform(current)
        val updated = if (current.draftStatus != DraftStatus.Scheduled) {
            requested.copy(
                maxMembers = current.maxMembers,
                settings = current.settings
            )
        } else {
            requested
        }
        leagues = leagues.map { if (it.inviteCode == current.inviteCode) updated else it }
        scope.launch {
            currentOnlineAccount()?.accessToken?.takeIf { it.isNotBlank() }?.let { token ->
                SupabaseLeagueService.updateLeague(token, updated)
            }
        }
    }

    fun isDraftRoomOpenForCurrentLeague(): Boolean {
        val current = league ?: return false
        return draftRoomOpen && draftRoomLeagueId == current.id
    }

    fun setMyAutoPick(target: LeagueUi, enabled: Boolean, forced: Boolean = false) {
        forcedAutoPickLeagueIds = if (enabled && forced) {
            forcedAutoPickLeagueIds + target.id
        } else if (!forced) {
            forcedAutoPickLeagueIds - target.id
        } else {
            forcedAutoPickLeagueIds
        }
        val username = account?.username.orEmpty()
        leagueMembersById[target.id]?.let { members ->
            leagueMembersById = leagueMembersById + (target.id to members.map { member ->
                if (member.username.equals(username, ignoreCase = true)) {
                    member.copy(autoPickEnabled = enabled)
                } else {
                    member
                }
            })
        }
        scope.launch {
            val current = currentOnlineAccount()
            if (current?.accessToken?.isNotBlank() == true) {
                SupabaseLeagueService.setMyAutoPick(current.accessToken, target.id, enabled)
                    .onFailure { Toast.makeText(context, friendlyLeagueError(it.message), Toast.LENGTH_LONG).show() }
                SupabaseLeagueService.loadMembers(current.accessToken, target.id).onSuccess { loadedMembers ->
                    leagueMembersById = leagueMembersById + (target.id to loadedMembers)
                }
            } else {
                updateActiveLeague { it.copy(autoPickEnabled = enabled) }
            }
        }
    }

    fun closeDraftRoomWithAutoPick() {
        val current = league
        if (isDraftRoomOpenForCurrentLeague() && current?.draftStatus == DraftStatus.Live) {
            val alreadyAuto = isMemberAutoPickEnabled(leagueMembersById[current.id], account, current)
            setMyAutoPick(current, true, forced = !alreadyAuto)
        }
        draftRoomOpen = false
        draftPickMode = false
        draftRoomLeagueId = null
    }

    fun openDraftRoomForCurrentLeague() {
        val current = league ?: return
        draftRoomLeagueId = current.id
        draftRoomOpen = true
        if (current.draftStatus == DraftStatus.Lobby) {
            scope.launch {
                val online = currentOnlineAccount()?.takeIf { it.accessToken.isNotBlank() } ?: return@launch
                SupabaseLeagueService.touchDraftPresence(online.accessToken, current.id)
                SupabaseLeagueService.loadDraftPresence(online.accessToken, current.id).onSuccess { presence ->
                    draftPresenceByLeagueId = draftPresenceByLeagueId + (current.id to presence)
                }
            }
        }
        if (current.id in forcedAutoPickLeagueIds) {
            forcedAutoPickLeagueIds = forcedAutoPickLeagueIds - current.id
            setMyAutoPick(current, false)
        }
    }

    fun closeDraftRoomState() {
        draftRoomOpen = false
        draftPickMode = false
        draftRoomLeagueId = null
    }

    fun isDraftRoomTab(tab: BreakoutTab): Boolean =
        tab == BreakoutTab.Draft || tab == BreakoutTab.Market || tab == BreakoutTab.Roster

    fun navigateFromDraftAware(tab: BreakoutTab) {
        val active = league
        val currentDraftRoomOpen = isDraftRoomOpenForCurrentLeague()
        if (currentDraftRoomOpen && active?.draftStatus == DraftStatus.Live && !isDraftRoomTab(tab)) {
            pendingDraftExitTab = tab
            return
        }
        if (!isDraftRoomTab(tab)) {
            closeDraftRoomWithAutoPick()
        }
        if (selectedTab != tab) previousTab = selectedTab
        selectedTab = tab
        selectedArtist = null
        draftPickMode = currentDraftRoomOpen && tab == BreakoutTab.Market
        joinError = null
        navigationRefreshTick++
    }

    fun openMarketForFilter(filter: MarketFilter, draftMode: Boolean = false) {
        draftPickMode = draftMode
        marketQuery = ""
        marketPreviousFilter = filter
        marketStartFilter = filter
        marketActiveFilter = filter
        marketVisibleCount = 20
        marketLastKey = null
        openMarketActionArtistKey = null
        selectedTab = BreakoutTab.Market
        scope.launch { marketListState.scrollToItem(0) }
    }

    fun requestDraftRoomExit() {
        val active = league
        if (isDraftRoomOpenForCurrentLeague() && active?.draftStatus == DraftStatus.Live) {
            pendingDraftExitTab = BreakoutTab.Draft
        } else {
            closeDraftRoomWithAutoPick()
        }
    }

    BackHandler(enabled = drawerState.isOpen || selectedArtist != null || isDraftRoomOpenForCurrentLeague() || selectedTab != BreakoutTab.Home) {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }
            selectedArtist != null -> selectedArtist = null
            isDraftRoomOpenForCurrentLeague() && selectedTab != BreakoutTab.Draft -> {
                previousTab = selectedTab
                selectedTab = BreakoutTab.Draft
            }
            isDraftRoomOpenForCurrentLeague() -> requestDraftRoomExit()
            selectedTab != BreakoutTab.Home -> {
                val target = previousTab.takeIf { it != selectedTab } ?: BreakoutTab.Home
                previousTab = BreakoutTab.Home
                selectedTab = target
            }
        }
        joinError = null
        navigationRefreshTick++
    }

    fun refreshRemoteLeagues() {
        if (isOnlinePlayConfigured()) {
            scope.launch {
                refreshingLeagueData = true
                try {
                    val current = currentOnlineAccount() ?: return@launch
                    SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                        applyRemoteLeagues(remoteLeagues)
                    }
                    league?.id?.takeIf { it.isNotBlank() }?.let { leagueId ->
                        SupabaseLeagueService.loadMembers(current.accessToken, leagueId).onSuccess { loadedMembers ->
                            leagueMembersById = leagueMembersById + (leagueId to loadedMembers)
                        }
                        SupabaseLeagueService.loadDraftPicks(current.accessToken, leagueId).onSuccess { picks ->
                            draftPicksByLeagueId = draftPicksByLeagueId + (leagueId to picks)
                        }
                        if (league?.draftStatus == DraftStatus.Lobby) {
                            SupabaseLeagueService.loadDraftPresence(current.accessToken, leagueId).onSuccess { presence ->
                                draftPresenceByLeagueId = draftPresenceByLeagueId + (leagueId to presence)
                            }
                        }
                    }
                } finally {
                    refreshingLeagueData = false
                }
            }
        }
    }

    fun pickIfCurrentTurnNeedsAuto(reasonableDelayElapsed: Boolean = false) {
        val active = league ?: return
        makeDueAutoPick(active, reasonableDelayElapsed)
    }

    LaunchedEffect(navigationRefreshTick) {
        if (navigationRefreshTick > 0) {
            refreshRemoteLeagues()
            pickIfCurrentTurnNeedsAuto()
        }
    }

    LaunchedEffect(
        league?.id,
        league?.draftStatus,
        league?.currentPickIndex,
        league?.currentPickStartedAt,
        leagueMembersById[league?.id].orEmpty().joinToString { "${it.username}:${it.autoPickEnabled}" }
    ) {
        while (league?.draftStatus == DraftStatus.Live) {
            pickIfCurrentTurnNeedsAuto()
            delay(1_000)
        }
    }

    LaunchedEffect(league?.id, league?.draftStatus, draftRoomOpen, draftRoomLeagueId) {
        val active = league ?: return@LaunchedEffect
        if (active.draftStatus != DraftStatus.Lobby || !isDraftRoomOpenForCurrentLeague()) return@LaunchedEffect
        while (league?.id == active.id && league?.draftStatus == DraftStatus.Lobby && isDraftRoomOpenForCurrentLeague()) {
            val current = currentOnlineAccount()
            if (current?.accessToken?.isNotBlank() == true) {
                SupabaseLeagueService.touchDraftPresence(current.accessToken, active.id)
                SupabaseLeagueService.loadDraftPresence(current.accessToken, active.id).onSuccess { presence ->
                    draftPresenceByLeagueId = draftPresenceByLeagueId + (active.id to presence)
                    val readyAtMs = presence.readyAt.parseServerInstantMillis()
                    if (
                        presence.presentCount >= presence.requiredCount &&
                        readyAtMs != null &&
                        SupabaseLeagueService.syncedNowMillis() >= readyAtMs + DraftLobbyReadyCountdownSeconds * 1000L
                    ) {
                        startLiveDraftFromLobby(active)
                    }
                }
            }
            val lobbyPresence = draftPresenceByLeagueId[active.id]
            delay(if (lobbyPresence?.readyAt?.isNotBlank() == true) 500 else 2_500)
        }
    }

    LaunchedEffect(league?.id, league?.draftStatus, league?.currentPickStartedAt) {
        val active = league ?: return@LaunchedEffect
        if (active.draftStatus != DraftStatus.Lobby) return@LaunchedEffect
        val lobbyStartedMs = active.currentPickStartedAt.parseServerInstantMillis() ?: return@LaunchedEffect
        val delayMs = (lobbyStartedMs + DraftLobbyGraceSeconds * 1000L - SupabaseLeagueService.syncedNowMillis()).coerceAtLeast(0)
        delay(delayMs)
        val latestLeague = league ?: return@LaunchedEffect
        val presence = draftPresenceByLeagueId[latestLeague.id]
        if (
            latestLeague.id == active.id &&
            latestLeague.draftStatus == DraftStatus.Lobby &&
            (presence == null || presence.presentCount < presence.requiredCount)
        ) {
            delayDraftLobbyFor(latestLeague)
        }
    }

    DisposableEffect(lifecycleOwner, authGate, account?.accessToken, draftRoomOpen, draftRoomLeagueId, league?.id, league?.draftStatus) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && authGate == AuthGate.SignedIn && account?.accessToken?.isNotBlank() == true) {
                refreshRemoteLeagues()
                pickIfCurrentTurnNeedsAuto()
            }
            if (event == Lifecycle.Event.ON_STOP && isDraftRoomOpenForCurrentLeague() && league?.draftStatus == DraftStatus.Live) {
                closeDraftRoomState()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    fun loadMembersForLeague(target: LeagueUi, force: Boolean = false) {
        if (!isOnlinePlayConfigured() || target.id.isBlank()) return
        if (!force && leagueMembersById.containsKey(target.id)) return
        if (force) {
            leagueMembersById = leagueMembersById - target.id
        }
        scope.launch {
            val current = currentOnlineAccount() ?: return@launch
            SupabaseLeagueService.loadMembers(current.accessToken, target.id).onSuccess { loadedMembers ->
                leagueMembersById = leagueMembersById + (target.id to loadedMembers)
            }
        }
    }

    fun transferManager(targetUsername: String) {
        val active = league ?: return
        val token = account?.accessToken.orEmpty()
        if (targetUsername.trim().isBlank() || token.isBlank()) return
        scope.launch {
            SupabaseLeagueService.transferManager(token, active.id, targetUsername).onSuccess {
                Toast.makeText(context, "$targetUsername is now the manager.", Toast.LENGTH_SHORT).show()
                val current = currentOnlineAccount()
                if (current?.accessToken?.isNotBlank() == true) {
                    SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                        applyRemoteLeagues(remoteLeagues)
                    }
                    SupabaseLeagueService.loadMembers(current.accessToken, active.id).onSuccess { loadedMembers ->
                        leagueMembersById = leagueMembersById + (active.id to loadedMembers)
                    }
                } else {
                    refreshRemoteLeagues()
                }
            }
        }
    }

    fun kickMember(targetUsername: String) {
        val active = league ?: return
        val token = account?.accessToken.orEmpty()
        if (targetUsername.trim().isBlank() || token.isBlank()) return
        scope.launch {
            SupabaseLeagueService.kickMember(token, active.id, targetUsername).onSuccess {
                refreshRemoteLeagues()
            }
        }
    }

    LaunchedEffect(leagues) {
        LocalBreakoutStore.saveLeagues(context, leagues)
    }

    LaunchedEffect(account) {
        LocalBreakoutStore.saveAccount(context, account)
    }

    LaunchedEffect(selectedTab, league?.id) {
        if (selectedTab == BreakoutTab.Matchup || selectedTab == BreakoutTab.Draft) {
            league?.let { loadMembersForLeague(it, force = true) }
        }
    }

    LaunchedEffect(Unit) {
        startupLoading = true
        suspend fun startupStep(progress: Float, message: String) {
            startupProgress = progress
            startupMessage = message
            delay(120)
        }

        startupStep(0.08f, "Checking your account")
        if (!requiresOnlineAccount) {
            authGate = AuthGate.SignedIn
        } else {
            authGate = AuthGate.Checking
            if (account == null) {
                authGate = AuthGate.SignedOut
            } else {
                val current = currentOnlineAccount()
                authGate = if (current?.accessToken?.isNotBlank() == true) {
                    AuthGate.SignedIn
                } else {
                    account = null
                    leagues = emptyList()
                    activeLeagueCode = null
                    roster = emptyMap()
                    AuthGate.SignedOut
                }
            }
        }

        startupStep(0.35f, "Loading your leagues")
        val current = if (authGate == AuthGate.SignedIn) currentOnlineAccount() else null
        var startupLeagues = leagues
        if (current?.accessToken?.isNotBlank() == true && isOnlinePlayConfigured()) {
            SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                startupLeagues = remoteLeagues
                applyRemoteLeagues(remoteLeagues, notifyDeleted = false)
            }.onFailure { error ->
                if (error !is CancellationException) joinError = friendlyLeagueLoadError(error.message)
            }
        }

        startupStep(0.70f, "Syncing rosters and matchups")
        val activeAfterLoad = startupLeagues.firstOrNull { it.inviteCode == activeLeagueCode } ?: startupLeagues.firstOrNull()
        if (current?.accessToken?.isNotBlank() == true && activeAfterLoad?.id?.isNotBlank() == true) {
            SupabaseLeagueService.loadMembers(current.accessToken, activeAfterLoad.id).onSuccess { loadedMembers ->
                leagueMembersById = leagueMembersById + (activeAfterLoad.id to loadedMembers)
            }
            SupabaseLeagueService.loadDraftPicks(current.accessToken, activeAfterLoad.id).onSuccess { picks ->
                draftPicksByLeagueId = draftPicksByLeagueId + (activeAfterLoad.id to picks)
            }
        }

        startupStep(1f, "Opening your league")
        delay(260)
        startupLoading = false
    }

    LaunchedEffect(authGate, account?.accessToken) {
        if (authGate == AuthGate.SignedIn && account?.accessToken?.isNotBlank() == true && isOnlinePlayConfigured()) {
            val current = currentOnlineAccount()
            if (current?.accessToken?.isNotBlank() == true) {
                SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).onSuccess { remoteLeagues ->
                    applyRemoteLeagues(remoteLeagues)
                }.onFailure { error ->
                    if (error is CancellationException) return@onFailure
                    joinError = friendlyLeagueLoadError(error.message)
                }
            } else {
                account = null
                authGate = AuthGate.SignedOut
            }
        }
    }

    LaunchedEffect(authGate, account?.accessToken, activeLeagueCode, league?.draftStatus, selectedTab) {
        if (authGate == AuthGate.SignedIn && account?.accessToken?.isNotBlank() == true && isOnlinePlayConfigured()) {
            while (true) {
                delay(
                    when {
                        league?.draftStatus == DraftStatus.Live -> 1_000
                        selectedTab == BreakoutTab.League -> 3_000
                        else -> 10_000
                    }
                )
                val current = currentOnlineAccount() ?: run {
                    account = null
                    authGate = AuthGate.SignedOut
                    return@LaunchedEffect
                }
                val remoteLeagues = SupabaseLeagueService.loadLeagues(current.accessToken, current.userId).getOrElse { error ->
                    if (error is CancellationException) return@LaunchedEffect
                    joinError = friendlyLeagueLoadError(error.message)
                    return@LaunchedEffect
                }
                applyRemoteLeagues(remoteLeagues)
                remoteLeagues.firstOrNull { it.inviteCode == activeLeagueCode }?.id?.takeIf { it.isNotBlank() }?.let { leagueId ->
                    SupabaseLeagueService.loadMembers(current.accessToken, leagueId).onSuccess { loadedMembers ->
                        leagueMembersById = leagueMembersById + (leagueId to loadedMembers)
                    }
                    SupabaseLeagueService.loadDraftPicks(current.accessToken, leagueId).onSuccess { picks ->
                        draftPicksByLeagueId = draftPicksByLeagueId + (leagueId to picks)
                    }
                }
            }
        }
    }

    LaunchedEffect(activeLeagueCode) {
        LocalBreakoutStore.saveActiveLeagueCode(context, activeLeagueCode)
    }

    LaunchedEffect(league?.id, league?.isManager) {
        val active = league ?: return@LaunchedEffect
        val previous = managerStatusByLeagueId[active.id]
        if (previous == false && active.isManager) {
            Toast.makeText(context, "You are now the manager.", Toast.LENGTH_SHORT).show()
        }
        managerStatusByLeagueId = managerStatusByLeagueId + (active.id to active.isManager)
    }

    LaunchedEffect(league?.id) {
        if (draftRoomLeagueId != null && draftRoomLeagueId != league?.id) {
            draftRoomOpen = false
            draftPickMode = false
            draftRoomLeagueId = null
            pendingDraftExitTab = null
            selectedArtistReadOnly = false
        }
        val leagueKey = league?.id?.ifBlank { league?.inviteCode.orEmpty() }
        roster = leagueKey?.let { LocalBreakoutStore.loadRoster(context, it) } ?: emptyMap()
        waiverClaims = leagueKey?.let { LocalBreakoutStore.loadWaiverClaims(context, it) } ?: emptyList()
        leagueKey?.let { key ->
            droppedArtistsByLeagueId = droppedArtistsByLeagueId + (key to LocalBreakoutStore.loadDroppedArtists(context, key))
            droppedArtistDatesByLeagueId = droppedArtistDatesByLeagueId + (key to LocalBreakoutStore.loadDroppedArtistDates(context, key))
        }
    }

    LaunchedEffect(roster) {
        league?.let { LocalBreakoutStore.saveRoster(context, roster, it.id.ifBlank { it.inviteCode }) }
    }

    LaunchedEffect(league?.id, waiverClaims) {
        league?.let { LocalBreakoutStore.saveWaiverClaims(context, waiverClaims, it.id.ifBlank { it.inviteCode }) }
    }

    LaunchedEffect(league?.id, droppedArtistsByLeagueId) {
        league?.let { active ->
            val key = active.id.ifBlank { active.inviteCode }
            LocalBreakoutStore.saveDroppedArtists(context, droppedArtistsByLeagueId[key].orEmpty(), key)
        }
    }

    LaunchedEffect(league?.id, droppedArtistDatesByLeagueId) {
        league?.let { active ->
            val key = active.id.ifBlank { active.inviteCode }
            LocalBreakoutStore.saveDroppedArtistDates(context, droppedArtistDatesByLeagueId[key].orEmpty(), key)
        }
    }

    LaunchedEffect(league?.inviteCode, league?.draftStatus, league?.currentPickIndex) {
        val active = league ?: return@LaunchedEffect
        if (active.draftStatus == DraftStatus.Live && active.currentPickIndex == 0 && roster.isNotEmpty()) {
            roster = emptyMap()
        }
    }

    LaunchedEffect(league?.id, league?.draftStatus, league?.settings?.draftDateLabel) {
        val active = league ?: return@LaunchedEffect
        if (active.draftStatus == DraftStatus.Scheduled && active.settings.draftDateLabel != "Set date") {
            scheduleDraftReminderNotifications(context, active)
        }
    }

    LaunchedEffect(league?.inviteCode, league?.draftStatus, league?.settings?.draftDateLabel, league?.memberCount) {
        val active = league ?: return@LaunchedEffect
        if (
            active.draftStatus != DraftStatus.Scheduled ||
            active.memberCount < 2 ||
            active.settings.draftDateLabel == "Set date"
        ) {
            return@LaunchedEffect
        }
        val scheduledAt = parseDraftDateTime(active.settings.draftDateLabel) ?: return@LaunchedEffect
        val delayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), scheduledAt).coerceAtLeast(0)
        if (delayMs > 60_000) {
            delay(delayMs)
        } else {
            delay(delayMs)
        }
        val latestLeague = league ?: return@LaunchedEffect
        if (
            latestLeague.inviteCode == active.inviteCode &&
            latestLeague.draftStatus == DraftStatus.Scheduled &&
            latestLeague.memberCount >= 2
        ) {
            roster = emptyMap()
            draftPickMode = false
            openDraftLobbyFor(latestLeague)
            Toast.makeText(context, "Draft lobby is open. Join when you are ready.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(league?.inviteCode, league?.draftStatus, league?.settings?.draftDateLabel) {
        val active = league ?: return@LaunchedEffect
        if (
            active.draftStatus != DraftStatus.Scheduled ||
            active.settings.draftDateLabel == "Set date"
        ) {
            return@LaunchedEffect
        }
        val scheduledAt = parseDraftDateTime(active.settings.draftDateLabel) ?: return@LaunchedEffect
        val delayMs = ChronoUnit.MILLIS.between(LocalDateTime.now(), scheduledAt).coerceAtLeast(0)
        if (!active.isManager && delayMs > 60_000) {
            delay(delayMs + 1_000)
        } else {
            delay(delayMs + 1_000)
        }
        repeat(8) {
            refreshRemoteLeagues()
            delay(1_500)
        }
    }

    LaunchedEffect(
        league?.inviteCode,
        league?.draftStatus,
        league?.currentPickIndex,
        draftRoomOpen,
        draftRoomLeagueId,
        leagueMembersById[league?.id].orEmpty().joinToString { "${it.username}:${it.autoPickEnabled}" }
    ) {
        val active = league ?: return@LaunchedEffect
        if (active.draftStatus == DraftStatus.Live && !isDraftRoomOpenForCurrentLeague() && isAccountOnClock(active, leagueMembersById[active.id], account)) {
            val autoEnabled = isMemberAutoPickEnabled(leagueMembersById[active.id], account, active)
            delay(if (autoEnabled) 5_000 else active.settings.pickSeconds * 1000L)
            val latestLeague = league ?: return@LaunchedEffect
            if (
                latestLeague.inviteCode == active.inviteCode &&
                latestLeague.currentPickIndex == active.currentPickIndex &&
                latestLeague.draftStatus == DraftStatus.Live &&
                !isDraftRoomOpenForCurrentLeague()
            ) {
                val draftedNames = draftPicksByLeagueId[latestLeague.id].orEmpty().map { it.artist.name.lowercase() }.toSet()
                val pickerUsername = currentDraftPickerUsername(latestLeague, leagueMembersById[latestLeague.id])
                val pickerRoster = pickerUsername?.let { rosterForPicker(latestLeague.id, it) } ?: emptyMap()
                bestEligibleDraftPick(pickerRoster, latestLeague, draftedNames)?.let { makeDueAutoPick(latestLeague, reasonableDelayElapsed = true) }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            LeagueDrawer(
                leagues = leagues,
                activeLeague = league,
                drawerOpen = drawerState.isOpen,
                draftLeagueName = draftLeagueName,
                draftInviteCode = draftInviteCode,
                onLeagueNameChange = {
                    draftLeagueName = it
                    joinError = null
                },
                onInviteCodeChange = {
                    draftInviteCode = it
                    joinError = null
                },
                joinError = joinError,
                onSwitchLeague = { selected ->
                    closeDraftRoomWithAutoPick()
                    activeLeagueCode = selected.inviteCode
                    joinError = null
                    selectedTab = BreakoutTab.Home
                    navigationRefreshTick++
                    scope.launch { drawerState.close() }
                },
                onCreateLeague = { createLeague(draftLeagueName) },
                onJoinLeague = { joinLeague(draftInviteCode) },
                onLeaveLeague = { league?.let { leaveLeague(it) } },
                onNavigate = { tab ->
                    navigateFromDraftAware(tab)
                    scope.launch { drawerState.close() }
                }
            )
        },
        gesturesEnabled = league != null && hasUsableAccount
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (!startupLoading && league != null && hasUsableAccount && selectedArtist == null) {
                    BreakoutBottomNavigation(
                        selectedTab = selectedTab,
                        onTabSelected = {
                            navigateFromDraftAware(it)
                        }
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val activeLeague = league
                if (startupLoading) {
                    AppStartupLoadingScreen(progress = startupProgress, message = startupMessage)
                } else if (requiresOnlineAccount && authGate == AuthGate.Checking) {
                    AuthCheckingScreen()
                } else if (requiresOnlineAccount && !hasUsableAccount) {
                    SignInScreen(
                        message = "Sign in before joining leagues, drafting artists, or saving a roster.",
                        onGoogleSignIn = if (isOnlinePlayConfigured()) ({
                            context.startActivity(Intent(Intent.ACTION_VIEW, SupabaseLeagueService.googleSignInUri()))
                        }) else null,
                        onSignIn = {
                            account = it
                            authGate = AuthGate.SignedIn
                            joinError = null
                        }
                    )
                } else if (!requiresOnlineAccount && account == null) {
                    SignInScreen(
                        message = "Sign in before joining leagues, drafting artists, or saving a roster.",
                        onGoogleSignIn = if (isOnlinePlayConfigured()) ({
                            context.startActivity(Intent(Intent.ACTION_VIEW, SupabaseLeagueService.googleSignInUri()))
                        }) else null,
                        onSignIn = {
                            account = it
                            authGate = AuthGate.SignedIn
                            joinError = null
                        }
                    )
                } else if (passwordResetOpen && account?.accessToken?.isNotBlank() == true) {
                    ResetPasswordScreen(
                        account = account!!,
                        onPasswordSaved = { updatedAccount ->
                            account = updatedAccount
                            passwordResetOpen = false
                            LocalBreakoutStore.saveAccount(context, updatedAccount)
                        }
                    )
                } else if (activeLeague == null) {
                    LeagueSetupScreen(
                        joinError = joinError,
                        onClearError = { joinError = null },
                        onCreateLeague = ::createLeague,
                        onJoinLeague = ::joinLeague
                    )
                } else {
                    val activeDraftRoomOpen = draftRoomOpen && draftRoomLeagueId == activeLeague.id
                    val visibleRoster = rosterForAccount(activeLeague)
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(260)) + slideInHorizontally(animationSpec = tween(260)) { it / 10 }) togetherWith
                                (fadeOut(animationSpec = tween(180)) + slideOutHorizontally(animationSpec = tween(180)) { -it / 12 })
                        },
                        label = "breakoutPageTransition"
                    ) { activeTab ->
                    when (activeTab) {
                        BreakoutTab.Home -> HomeScreen(
                            account = account,
                            league = activeLeague,
                            roster = visibleRoster.values.toList(),
                            refreshing = refreshingLeagueData,
                            onRefresh = ::refreshRemoteLeagues,
                            onOpenMenu = { scope.launch { drawerState.open() } }
                        )
                        BreakoutTab.Draft -> DraftScreen(
                            league = activeLeague,
                            account = account,
                            members = leagueMembersById[activeLeague.id],
                            draftPresence = draftPresenceByLeagueId[activeLeague.id],
                            draftPicks = draftPicksByLeagueId[activeLeague.id].orEmpty(),
                            roster = visibleRoster,
                            draftRoomOpen = activeDraftRoomOpen,
                            refreshing = refreshingLeagueData,
                            onRefresh = ::refreshRemoteLeagues,
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onUpdateLeague = ::updateActiveLeague,
                            onOpenDraftRoom = { openDraftRoomForCurrentLeague() },
                            onCloseDraftRoom = ::requestDraftRoomExit,
                            onOpenDraftMarket = {
                                openDraftRoomForCurrentLeague()
                                openMarketForFilter(MarketFilter.Headliners, draftMode = true)
                            },
                            onOpenDraftRoster = {
                                openDraftRoomForCurrentLeague()
                                selectedTab = BreakoutTab.Roster
                            },
                            onOpenRoster = {
                                closeDraftRoomState()
                                selectedTab = BreakoutTab.Roster
                            },
                            onOpenDraftSummary = {
                                closeDraftRoomState()
                                selectedTab = BreakoutTab.DraftSummary
                            },
                            onOpenHome = {
                                closeDraftRoomState()
                                selectedTab = BreakoutTab.Home
                            },
                            onOpenLeagueSettings = {
                                selectedTab = BreakoutTab.League
                                selectedArtist = null
                            },
                            onArtistSelected = { selectedArtist = it },
                            onDraftPick = { artist, autoPicked -> makeDraftPick(artist, activeLeague, autoPicked) },
                            onAutoPickChange = { enabled -> setMyAutoPick(activeLeague, enabled) },
                            onStartLiveDraft = { startLiveDraftFromLobby(activeLeague) },
                            onSetupWarningSelected = { warning ->
                                if (warning.contains("draft time", ignoreCase = true)) {
                                    selectedTab = BreakoutTab.League
                                } else if (warning.contains("member", ignoreCase = true)) {
                                    scope.launch { drawerState.open() }
                                }
                            }
                        )
                        BreakoutTab.Market -> MarketScreen(
                            roster = visibleRoster,
                            draftPicks = draftPicksByLeagueId[activeLeague.id].orEmpty(),
                            droppedArtistNames = droppedArtistsByLeagueId[activeLeague.id].orEmpty(),
                            waiverQueuedNames = waiverClaims.map { it.artist.name.lowercase() }.toSet(),
                            leagueSettings = activeLeague.settings,
                            draftStatus = activeLeague.draftStatus,
                            draftPickMode = activeDraftRoomOpen && draftPickMode,
                            canMakeDraftPick = activeDraftRoomOpen &&
                                draftPickMode &&
                                activeLeague.draftStatus == DraftStatus.Live &&
                                isAccountOnClock(activeLeague, leagueMembersById[activeLeague.id], account),
                            showExpiredFeedback = !isMemberAutoPickEnabled(leagueMembersById[activeLeague.id], account, activeLeague),
                            currentPickIndex = activeLeague.currentPickIndex,
                            memberCount = activeLeague.memberCount,
                            startFilter = marketStartFilter,
                            query = marketQuery,
                            onQueryChange = { marketQuery = it },
                            activeFilter = marketActiveFilter,
                            onActiveFilterChange = { marketActiveFilter = it },
                            previousFilter = marketPreviousFilter,
                            onPreviousFilterChange = { marketPreviousFilter = it },
                            marketState = marketState,
                            onMarketStateChange = { marketState = it },
                            snapshots = marketSnapshots,
                            onSnapshotsChange = { marketSnapshots = it },
                            visibleCount = marketVisibleCount,
                            onVisibleCountChange = { marketVisibleCount = it },
                            lastMarketKey = marketLastKey,
                            onLastMarketKeyChange = { marketLastKey = it },
                            loadedMarketKey = marketLoadedKey,
                            onLoadedMarketKeyChange = { marketLoadedKey = it },
                            openActionArtistKey = openMarketActionArtistKey,
                            onOpenActionArtistKeyChange = { openMarketActionArtistKey = it },
                            listState = marketListState,
                            preloadedArtists = preloadedMarketArtists,
                            refreshing = refreshingLeagueData,
                            onRefresh = {
                                openMarketActionArtistKey = null
                                val requestKey = marketQuery.trim().lowercase()
                                scope.launch {
                                    runCatching { MusicArtistService.topArtists() }.onSuccess { artists ->
                                        preloadedMarketArtists = artists
                                        if (marketQuery.isBlank() && artists.size >= 100) {
                                            marketState = MarketState.Ready(artists)
                                            marketSnapshots = LocalBreakoutStore.updateSnapshots(context, artists)
                                            marketLoadedKey = requestKey
                                        } else if (marketQuery.isBlank()) {
                                            marketState = MarketState.Loading
                                            marketLoadedKey = null
                                        }
                                        prefetchArtistImages(context, artists.filter { !it.imageUrl.isNullOrBlank() }.take(80))
                                    }
                                }
                                refreshRemoteLeagues()
                            },
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onArtistSelected = { selectedArtist = it },
                            onToggleArtist = { artist -> makeDraftPick(artist, activeLeague) },
                            onRemoveRosterArtist = { artist ->
                                val slot = visibleRoster.entries.firstOrNull { it.value.name.equals(artist.name, ignoreCase = true) }?.key
                                if (slot != null) {
                                    roster = roster - slot
                                    droppedArtistsByLeagueId = droppedArtistsByLeagueId + (
                                        activeLeague.id to (droppedArtistsByLeagueId[activeLeague.id].orEmpty() + artist.name.lowercase())
                                    )
                                    droppedArtistDatesByLeagueId = droppedArtistDatesByLeagueId + (
                                        activeLeague.id to (droppedArtistDatesByLeagueId[activeLeague.id].orEmpty() + (artist.name.lowercase() to System.currentTimeMillis()))
                                    )
                                    Toast.makeText(context, "${artist.name} dropped.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onQueueWaiverArtist = { artist, dropSlot ->
                                if (waiverClaims.size >= activeLeague.settings.maxWaiverClaims) {
                                    Toast.makeText(context, "Your waiver queue is full.", Toast.LENGTH_SHORT).show()
                                } else claimableSlotFor(artist, visibleRoster, activeLeague.settings)?.let { slot ->
                                    val resolvedDropSlot = dropSlot ?: if (visibleRoster[slot] == null) null else slot
                                    waiverClaims = waiverClaims + WaiverClaimUi(artist, slot, resolvedDropSlot)
                                    Toast.makeText(context, "${artist.name} added to waivers.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCancelWaiverArtist = { artist ->
                                waiverClaims = waiverClaims.filterNot { it.artist.name.equals(artist.name, ignoreCase = true) }
                                Toast.makeText(context, "${artist.name} removed from waivers.", Toast.LENGTH_SHORT).show()
                            },
                            onPickExpired = {
                                draftPickMode = false
                                selectedTab = BreakoutTab.Draft
                                Toast.makeText(context, "Your pick time expired.", Toast.LENGTH_SHORT).show()
                            }
                        )
                        BreakoutTab.Roster -> RosterScreen(
                            roster = visibleRoster,
                            waiverClaims = waiverClaims,
                            leagueSettings = activeLeague.settings,
                            memberCount = activeLeague.memberCount,
                            draftStatus = activeLeague.draftStatus,
                            draftRoomContext = activeDraftRoomOpen,
                            refreshing = refreshingLeagueData,
                            onRefresh = ::refreshRemoteLeagues,
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onArtistSelected = { selectedArtist = it },
                            onRemoveArtist = { slot ->
                                visibleRoster[slot]?.let { artist ->
                                    if (activeLeague.draftStatus == DraftStatus.Complete) {
                                        droppedArtistsByLeagueId = droppedArtistsByLeagueId + (
                                            activeLeague.id to (droppedArtistsByLeagueId[activeLeague.id].orEmpty() + artist.name.lowercase())
                                        )
                                        droppedArtistDatesByLeagueId = droppedArtistDatesByLeagueId + (
                                            activeLeague.id to (droppedArtistDatesByLeagueId[activeLeague.id].orEmpty() + (artist.name.lowercase() to System.currentTimeMillis()))
                                        )
                                    }
                                }
                                roster = roster - slot
                            },
                            onMoveArtist = { from, to ->
                                    val fromArtist = visibleRoster[from]
                                    if (fromArtist != null) {
                                        val toArtist = visibleRoster[to]
                                        val updatedRoster = if (toArtist == null) {
                                        (visibleRoster - from) + (to to fromArtist)
                                    } else {
                                        (visibleRoster - from - to) + (to to fromArtist) + (from to toArtist)
                                    }
                                    roster = updatedRoster
                                    LocalBreakoutStore.saveRoster(context, updatedRoster, activeLeague.id.ifBlank { activeLeague.inviteCode })
                                    val username = account?.username.orEmpty()
                                    draftPicksByLeagueId = draftPicksByLeagueId + (
                                        activeLeague.id to draftPicksByLeagueId[activeLeague.id].orEmpty().map { pick ->
                                            if (!pick.pickedBy.equals(username, ignoreCase = true)) {
                                                pick
                                            } else when (pick.slot) {
                                                from -> pick.copy(slot = to)
                                                to -> pick.copy(slot = from)
                                                else -> pick
                                            }
                                        }
                                    )
                                }
                            },
                            onRunWaivers = {
                                var updatedRoster = visibleRoster
                                waiverClaims.forEach { claim ->
                                    if (updatedRoster.values.none { it.name == claim.artist.name }) {
                                        claim.dropSlot?.let { dropSlot ->
                                            updatedRoster = updatedRoster - dropSlot
                                        }
                                        firstOpenSlotFor(claim.artist, updatedRoster, activeLeague.settings)?.let { slot ->
                                            updatedRoster = updatedRoster + (slot to claim.artist)
                                        }
                                    }
                                }
                                roster = updatedRoster
                                waiverClaims = emptyList()
                            },
                            onCancelWaiver = { claim -> waiverClaims = waiverClaims - claim },
                            onMoveWaiver = { claim, direction ->
                                val index = waiverClaims.indexOf(claim)
                                val target = (index + direction).coerceIn(0, waiverClaims.lastIndex)
                                if (index >= 0 && target != index) {
                                    waiverClaims = waiverClaims.toMutableList().apply {
                                        removeAt(index)
                                        add(target, claim)
                                    }
                                }
                            },
                            onOpenMarket = { filter ->
                                openMarketForFilter(filter)
                            }
                        )
                        BreakoutTab.Matchup -> MatchupScreen(
                            league = activeLeague,
                            account = account,
                            roster = visibleRoster,
                            draftPicks = draftPicksByLeagueId[activeLeague.id].orEmpty(),
                            members = leagueMembersById[activeLeague.id],
                            refreshing = refreshingLeagueData,
                            onRefresh = ::refreshRemoteLeagues,
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onOpenWeek = { week ->
                                selectedAllMatchupsWeek = week
                                previousTab = BreakoutTab.Matchup
                                selectedTab = BreakoutTab.AllMatchups
                            },
                            onOpenMarket = { filter ->
                                openMarketForFilter(filter)
                            },
                            onArtistSelected = { selectedArtist = it }
                        )
                        BreakoutTab.AllMatchups -> AllMatchupsScreen(
                            league = activeLeague,
                            members = leagueMembersById[activeLeague.id],
                            draftPicks = draftPicksByLeagueId[activeLeague.id].orEmpty(),
                            initialWeek = selectedAllMatchupsWeek,
                            refreshing = refreshingLeagueData,
                            onRefresh = ::refreshRemoteLeagues,
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onArtistSelected = { selectedArtist = it }
                        )
                        BreakoutTab.Standings -> StandingsScreen(
                            league = activeLeague,
                            refreshing = refreshingLeagueData,
                            onRefresh = ::refreshRemoteLeagues,
                            onOpenMenu = { scope.launch { drawerState.open() } }
                        )
                        BreakoutTab.DraftSummary -> DraftSummaryScreen(
                            league = activeLeague,
                            draftPicks = draftPicksByLeagueId[activeLeague.id].orEmpty(),
                            refreshing = refreshingLeagueData,
                            onRefresh = ::refreshRemoteLeagues,
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onArtistSelected = { selectedArtist = it }
                        )
                        BreakoutTab.League -> LeagueScreen(
                            league = activeLeague,
                            account = account,
                            draftPicks = draftPicksByLeagueId[activeLeague.id].orEmpty(),
                            refreshing = refreshingLeagueData,
                            onRefresh = ::refreshRemoteLeagues,
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onUpdateLeague = ::updateActiveLeague,
                            onTransferManager = ::transferManager,
                            onKickMember = ::kickMember,
                            onOpenRoster = {
                                selectedTab = BreakoutTab.Roster
                                selectedArtist = null
                                selectedArtistReadOnly = false
                            },
                            memberRosterToOpen = if (selectedArtist == null) restoreRosterMemberUsername else null,
                            onMemberRosterOpened = { restoreRosterMemberUsername = null },
                            onArtistSelected = {
                                selectedArtistReadOnly = true
                                selectedArtist = it
                            },
                            onMemberRosterArtistSelected = { member, artist ->
                                restoreRosterMemberUsername = member.username
                                selectedArtistReadOnly = true
                                selectedArtist = artist
                            },
                            onRunWaivers = {
                                var updatedRoster = visibleRoster
                                waiverClaims.forEach { claim ->
                                    if (updatedRoster.values.none { it.name == claim.artist.name }) {
                                        claim.dropSlot?.let { dropSlot ->
                                            updatedRoster = updatedRoster - dropSlot
                                        }
                                        firstOpenSlotFor(claim.artist, updatedRoster, activeLeague.settings)?.let { slot ->
                                            updatedRoster = updatedRoster + (slot to claim.artist)
                                        }
                                    }
                                }
                                roster = updatedRoster
                                waiverClaims = emptyList()
                                Toast.makeText(context, "Waivers processed.", Toast.LENGTH_SHORT).show()
                            },
                            onLeaveLeague = { leaveLeague(activeLeague) },
                            onDeleteLeague = { deleteLeague(activeLeague) }
                        )
                        BreakoutTab.Account -> AccountScreen(
                            account = account,
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            onSaveAccount = { updatedAccount, onResult ->
                                scope.launch {
                                    val current = currentOnlineAccount()
                                    if (current == null) {
                                        account = null
                                        authGate = AuthGate.SignedOut
                                        onResult("Your session expired. Please log in again.")
                                        return@launch
                                    }
                                    SupabaseLeagueService.updateProfile(
                                        updatedAccount.copy(
                                            accessToken = current.accessToken,
                                            refreshToken = current.refreshToken,
                                            userId = current.userId.ifBlank { updatedAccount.userId }
                                        )
                                    )
                                        .onSuccess { savedAccount ->
                                            account = savedAccount
                                            onResult(null)
                                        }
                                        .onFailure { onResult(friendlyAccountError(it.message)) }
                                }
                            },
                            onDeleteAccount = { onResult ->
                                val token = account?.accessToken.orEmpty()
                                scope.launch {
                                    SupabaseLeagueService.deleteAccount(token)
                                        .onSuccess {
                                            account = null
                                            leagues = emptyList()
                                            activeLeagueCode = null
                                            selectedTab = BreakoutTab.Home
                                            roster = emptyMap()
                                            authGate = AuthGate.SignedOut
                                            onResult(null)
                                        }
                                        .onFailure { onResult(friendlyAccountError(it.message)) }
                                }
                            },
                            onSignOut = {
                                account = null
                                leagues = emptyList()
                                authGate = if (requiresOnlineAccount) AuthGate.SignedOut else AuthGate.SignedIn
                                activeLeagueCode = null
                                selectedTab = BreakoutTab.Home
                                roster = emptyMap()
                            }
                        )
                    }
                    }

                    selectedArtist?.let { artist ->
                        val droppedNames = droppedArtistsByLeagueId[activeLeague.id].orEmpty()
                        val originalDraftedPick = draftPicksByLeagueId[activeLeague.id].orEmpty()
                            .firstOrNull { it.artist.name.equals(artist.name, ignoreCase = true) }
                        val draftedPick = originalDraftedPick?.takeUnless { artist.name.lowercase() in droppedNames }
                        val droppedAt = droppedArtistDatesByLeagueId[activeLeague.id].orEmpty()[artist.name.lowercase()]
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            ArtistDetailScreen(
                                artist = artist,
                                isInRoster = visibleRoster.values.any { it.name == artist.name },
                                draftStatus = activeLeague.draftStatus,
                                weeklyPoints = weeklyPointsForArtist(artist, activeLeague),
                                draftedStatusLabel = draftedPick?.let {
                                    "Drafted by ${it.pickedBy} in Round ${((it.pickNumber - 1) / activeLeague.memberCount.coerceAtLeast(1)) + 1}, Pick ${it.pickNumber}"
                                },
                                draftedHistoryLabel = originalDraftedPick?.let {
                                    "Drafted by ${it.pickedBy} in Round ${((it.pickNumber - 1) / activeLeague.memberCount.coerceAtLeast(1)) + 1}, Pick ${it.pickNumber}"
                                },
                                droppedAtMillis = droppedAt,
                                isWaiverQueued = waiverClaims.any { it.artist.name == artist.name },
                                canAddToRoster = !selectedArtistReadOnly &&
                                    activeDraftRoomOpen &&
                                    activeLeague.draftStatus == DraftStatus.Live &&
                                    isAccountOnClock(activeLeague, leagueMembersById[activeLeague.id], account) &&
                                draftedPick == null &&
                                visibleRoster.values.none { it.name == artist.name } &&
                                firstOpenSlotFor(artist, visibleRoster, activeLeague.settings) != null,
                                canQueueWaiver = !selectedArtistReadOnly &&
                                    activeLeague.draftStatus == DraftStatus.Complete &&
                                    draftedPick == null &&
                                    waiverClaims.size < activeLeague.settings.maxWaiverClaims &&
                                    waiverClaims.none { it.artist.name == artist.name } &&
                                    claimableSlotFor(artist, visibleRoster, activeLeague.settings) != null,
                                waiverReplacementOptions = if (firstOpenSlotFor(artist, visibleRoster, activeLeague.settings) == null) {
                                    waiverReplacementOptionsFor(artist, visibleRoster, activeLeague.settings)
                                } else {
                                    emptyList()
                                },
                                onAddToRoster = {
                                    firstOpenSlotFor(artist, visibleRoster, activeLeague.settings)?.let { slot ->
                                        roster = roster + (slot to artist)
                                    }
                                },
                            onRemoveFromRoster = {
                                if (activeLeague.draftStatus == DraftStatus.Complete) {
                                    droppedArtistsByLeagueId = droppedArtistsByLeagueId + (
                                        activeLeague.id to (droppedArtistsByLeagueId[activeLeague.id].orEmpty() + artist.name.lowercase())
                                    )
                                    droppedArtistDatesByLeagueId = droppedArtistDatesByLeagueId + (
                                        activeLeague.id to (droppedArtistDatesByLeagueId[activeLeague.id].orEmpty() + (artist.name.lowercase() to System.currentTimeMillis()))
                                    )
                                }
                                roster = roster.filterValues { it.name != artist.name }
                            },
                            onQueueWaiver = { dropSlot ->
                                if (waiverClaims.size >= activeLeague.settings.maxWaiverClaims) {
                                    Toast.makeText(context, "Your waiver queue is full.", Toast.LENGTH_SHORT).show()
                                } else claimableSlotFor(artist, visibleRoster, activeLeague.settings)?.let { slot ->
                                    waiverClaims = waiverClaims + WaiverClaimUi(artist, slot, dropSlot)
                                    Toast.makeText(context, "${artist.name} added to waivers.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCancelWaiver = {
                                waiverClaims = waiverClaims.filterNot { it.artist.name.equals(artist.name, ignoreCase = true) }
                                Toast.makeText(context, "${artist.name} removed from waivers.", Toast.LENGTH_SHORT).show()
                            },
                            onDraftPick = { makeDraftPick(artist, activeLeague) },
                            onBack = {
                                selectedArtist = null
                                selectedArtistReadOnly = false
                            }
                        )
                        }
                    }
                    pendingDraftExitTab?.let { targetTab ->
                        ConfirmActionCard(
                            title = "Leave Draft Room?",
                            detail = "Leaving the draft room turns on auto-pick until you rejoin. The Market and Roster tabs stay inside the draft room.",
                            confirmText = "Leave",
                            onCancel = { pendingDraftExitTab = null },
                            onConfirm = {
                                pendingDraftExitTab = null
                                closeDraftRoomWithAutoPick()
                                if (targetTab == BreakoutTab.Matchup) {
                                    league?.let { loadMembersForLeague(it, force = true) }
                                }
                                selectedTab = if (targetTab == BreakoutTab.Draft) BreakoutTab.Draft else targetTab
                                selectedArtist = null
                                joinError = null
                            }
                        )
                    }
                    if (
                        activeDraftRoomOpen &&
                        activeLeague.draftStatus == DraftStatus.Live &&
                        selectedArtist == null
                    ) {
                        DraftFloatingClock(
                            league = activeLeague,
                            members = leagueMembersById[activeLeague.id],
                            account = account,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .statusBarsPadding()
                                .padding(top = BreakoutDimensions.sm)
                        )
                    }
                }
            }
        }
    }
}

