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

private const val DraftNotificationChannelId = "breakout_draft_alerts"
private const val DraftNotificationPermissionRequest = 42
private const val DraftReminderTitleExtra = "draft_reminder_title"
private const val DraftReminderMessageExtra = "draft_reminder_message"
private const val DraftReminderIdExtra = "draft_reminder_id"

private enum class BreakoutTab(val title: String, val mark: String) {
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

private enum class MarketFilter(val label: String) {
    Headliners("Headliners"),
    Rising("Rising"),
    Wildcards("Wildcards"),
    DeepCuts("Deep Cuts"),
    Breakouts("Trending"),
}

private enum class RosterSlot(val label: String, val hint: String, val filter: MarketFilter) {
    HeadlinerOne("Headliner", "Large-scale artist", MarketFilter.Headliners),
    HeadlinerTwo("Headliner", "Large-scale artist", MarketFilter.Headliners),
    HeadlinerThree("Headliner", "Large-scale artist", MarketFilter.Headliners),
    HeadlinerFour("Headliner", "Large-scale artist", MarketFilter.Headliners),
    RisingOne("Rising Artist", "Under 2.5M followers", MarketFilter.Rising),
    RisingTwo("Rising Artist", "Under 2.5M followers", MarketFilter.Rising),
    RisingThree("Rising Artist", "Under 2.5M followers", MarketFilter.Rising),
    RisingFour("Rising Artist", "Under 2.5M followers", MarketFilter.Rising),
    WildcardOne("Wildcard", "Flexible non-headliner", MarketFilter.Wildcards),
    WildcardTwo("Wildcard", "Flexible non-headliner", MarketFilter.Wildcards),
    WildcardThree("Wildcard", "Flexible non-headliner", MarketFilter.Wildcards),
    WildcardFour("Wildcard", "Flexible non-headliner", MarketFilter.Wildcards),
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

private enum class DraftFormat(val label: String) {
    Snake("Snake Draft"),
    Linear("Linear Draft"),
    Auction("Auction Draft")
}

private enum class DraftStatus(val label: String) {
    Scheduled("Scheduled"),
    Lobby("Lobby"),
    Practice("Practice"),
    Live("Live"),
    Complete("Complete")
}

private enum class AuthMode {
    Login,
    CreateAccount
}

private enum class DraftRoomView {
    Home,
    Board,
    Roster,
    Picks
}

private enum class AuthGate {
    Checking,
    SignedOut,
    SignedIn
}

private data class AuthRequest(
    val id: Int,
    val mode: AuthMode,
    val login: String,
    val email: String,
    val username: String,
    val password: String,
    val mailingList: Boolean
)

private data class PasswordResetRequest(
    val id: Int,
    val email: String
)

private data class AuthCallbackUi(
    val account: AccountUi,
    val isPasswordRecovery: Boolean
)

private const val MaxEmailLength = 254
private const val MaxUsernameLength = 24
private const val MinPasswordLength = 8
private const val MaxPasswordLength = 72
private const val MaxLeagueNameLength = 40
private const val ReleaseDraftLeadMinutes = 30L
private const val DebugDraftLeadMinutes = 1L
private const val DraftLobbyGraceSeconds = 300
private const val DraftLobbyReadyCountdownSeconds = 30
private const val DraftLobbyDelayMinutes = 15L
private const val MinLeagueMembers = 2
private const val MaxLeagueMembers = 20
private const val MinSeasonWeeks = 4
private const val MaxSeasonWeeks = 24
private const val MinWaiverClaims = 1
private const val MaxWaiverClaims = 12
private const val AuthCallbackUrl = "breakout://auth-callback"
private val WaiverAccent = Color(0xFFFFB454)
private val DraftedOtherAccent = Color(0xFFFF7C7C)
private val DraftDateInputFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
private val DraftDateDisplayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")
private val DraftReminderOffsets = listOf(
    1_440L to "1 day",
    60L to "1 hour",
    30L to "30 minutes",
    15L to "15 minutes"
)
private val BlockedSignupEmailDomains = setOf(
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
private val CommonEmailDomainTypos = mapOf(
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
private val CatalogArtistOverrides = setOf(
    "juice wrld",
    "lil peep",
    "mac miller",
    "pop smoke",
    "xxxtentacion"
)

private data class AccountUi(
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

private data class LeagueSettingsUi(
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

private data class LeagueUi(
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

private data class LeagueMemberUi(
    val username: String,
    val teamName: String,
    val role: String,
    val autoPickEnabled: Boolean = false
) {
    val isManager: Boolean
        get() = role.equals("manager", ignoreCase = true)
}

private data class DraftPickUi(
    val pickNumber: Int,
    val pickedBy: String,
    val slot: RosterSlot,
    val artist: ArtistUi,
    val secondsToPick: Int? = null,
    val autoPicked: Boolean = false
)

private data class DraftPresenceUi(
    val presentCount: Int = 0,
    val requiredCount: Int = 2,
    val memberCount: Int = 2,
    val readyAt: String = ""
)

private data class MatchupWeekUi(
    val week: Int,
    val opponent: String?,
    val isBye: Boolean
)

private data class WaiverClaimUi(
    val artist: ArtistUi,
    val slot: RosterSlot
)

private data class ArtistUi(
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
            marketBucket() == MarketFilter.Wildcards -> "Wildcard"
            else -> "Headliner"
        }

    val audienceLabel: String
        get() = listeners?.let { "${it.formatCompact()} audience" } ?: "Audience developing"

    val compactAudienceLabel: String
        get() = listeners?.formatCompact() ?: "Sizing Up"

    val cardAudienceLabel: String
        get() = listeners?.let { "${it.formatCompact()} aud." } ?: "Audience"

    val activityLabel: String
        get() = albumCount?.let { "$it releases" } ?: "Catalog developing"

    val marketNote: String
        get() = when (tag) {
            "Deep Cut" -> if ((listeners ?: 0L) < 10_000L) "Unproven discovery watch" else "High-upside discovery slot"
            "Rising" -> "Momentum watch candidate"
            "Wildcard" -> "Flexible upside pick"
            "Catalog" -> "Reliable catalog, lower breakout upside"
            "Headliner" -> "Reliable floor, lower growth upside"
            else -> "Waiting for audience data"
        }

    val compactRead: String
        get() = when (tag) {
            "Deep Cut" -> "High upside"
            "Rising" -> "Momentum watch"
            "Wildcard" -> "Flexible upside"
            "Catalog" -> "Catalog floor"
            "Headliner" -> "Reliable floor"
            else -> "Sizing up"
        }

    val riskLabel: String
        get() = when (tag) {
            "Headliner" -> "Low variance"
            "Catalog" -> "Low growth"
            "Wildcard" -> "Medium variance"
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

private data class SnapshotUi(
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

private data class SignalRowUi(
    val label: String,
    val value: String,
    val detail: String? = null
)

private sealed interface MarketState {
    data object Loading : MarketState
    data class Ready(val artists: List<ArtistUi>) : MarketState
    data class Empty(val message: String) : MarketState
    data class Error(val message: String) : MarketState
}

private object DeezerArtistService {
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
                "Reneé Rapp",
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
                "The Marías",
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

private data class KworbArtistStats(
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

private object KworbArtistService {
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

private object MusicArtistService {
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

    suspend fun topArtists(): List<ArtistUi> = cachedList("music-all-v6") {
        (headlinerCandidates() + wildcardCandidates() + risingCandidates() + deepCutCandidates())
            .marketDistinct()
            .sortedWith(
                compareByDescending<ArtistUi> { it.isHeadlinerEligible() }
                    .thenByDescending { it.listeners ?: 0L }
                    .thenByDescending { it.breakoutScore(null) }
            )
    }

    suspend fun headlinerCandidates(): List<ArtistUi> = cachedList("music-headliners-v6") {
        discoveryPool()
            .filter { it.isHeadlinerEligible() }
            .sortedWith(compareByDescending<ArtistUi> { it.listeners ?: 0L }.thenByDescending { it.breakoutScore(null) })
            .take(500)
    }

    suspend fun risingCandidates(): List<ArtistUi> = cachedList("music-rising-v6") {
        val pool = discoveryPool()
        val artists = pool
            .filter { it.marketBucket() == MarketFilter.Rising }
            .sortedWith(compareByDescending<ArtistUi> { it.breakoutScore(null) }.thenByDescending { it.listeners ?: 0L })
        (if (artists.size >= 20) artists else {
            (artists + pool
                .filter { !it.isHeadlinerEligible() }
                .sortedByDescending { it.breakoutScore(null) }
            ).marketDistinct()
        }).take(500)
    }

    suspend fun wildcardCandidates(): List<ArtistUi> = cachedList("music-wildcards-v6") {
        val pool = discoveryPool()
        val artists = pool
            .filter { it.marketBucket() == MarketFilter.Wildcards }
            .sortedWith(compareByDescending<ArtistUi> { it.breakoutScore(null) }.thenByDescending { it.listeners ?: 0L })
        (if (artists.size >= 20) artists else {
            (artists + pool
                .filter { !it.isHeadlinerEligible() }
                .sortedByDescending { it.listeners ?: 0L }
            ).marketDistinct()
        }).take(500)
    }

    suspend fun deepCutCandidates(): List<ArtistUi> = cachedList("music-deep-cuts-v6") {
        val pool = discoveryPool()
        val artists = pool
            .filter { it.marketBucket() == MarketFilter.DeepCuts }
            .sortedByDescending { it.breakoutScore(null) + it.discoverySortValue() }
        (if (artists.size >= 20) artists else {
            (artists + pool
                .filter { !it.isHeadlinerEligible() }
                .sortedByDescending { it.breakoutScore(null) + it.discoverySortValue() }
            ).marketDistinct()
        }).take(500)
    }

    suspend fun search(query: String): List<ArtistUi> = cachedList("music-search-v5:${query.lowercase()}") {
        val artists = if (spotifyConfigured()) {
            spotifyRankedSearch(query, limit = 40).map { it.enrichWithLastFm() }
        } else {
            DeezerArtistService.search(query)
        }.filter { it.listeners != null }
        KworbArtistService.enrichAudience(artists)
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

    private suspend fun discoveryPool(): List<ArtistUi> = cachedList("music-discovery-pool-v6") {
        val artists = if (spotifyConfigured()) {
            val queryArtists = discoveryQueries.parallelMap { query ->
                listOf(0, 50).parallelMap { offset ->
                    runCatching { spotifySearch(query, limit = 50, offset = offset, includeTrackSignal = false) }.getOrDefault(emptyList())
                }.flatten()
            }
                .flatten()
            val chartArtists = runCatching {
                seededArtists(KworbArtistService.audienceLeaderNames(90))
            }.getOrDefault(emptyList())
            (queryArtists + chartArtists).marketDistinct()
        } else {
            discoveryQueries.parallelMap { query ->
                runCatching { DeezerArtistService.search(query) }.getOrDefault(emptyList())
            }.flatten().marketDistinct()
        }.filter { it.listeners != null }
        KworbArtistService.enrichAudience(artists).marketDistinct()
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

private suspend fun prefetchArtistImages(context: Context, artists: List<ArtistUi>) {
    artists
        .mapNotNull { it.bestImageUrl }
        .distinct()
        .take(80)
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

private object SupabaseLeagueService {
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

private object LocalBreakoutStore {
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
                val artist = item.toStoredArtist()
                WaiverClaimUi(artist, slot)
            }
        }.getOrDefault(emptyList())
    }

    fun saveWaiverClaims(context: Context, claims: List<WaiverClaimUi>, leagueKey: String? = null) {
        val array = JSONArray()
        claims.forEach { claim ->
            array.put(JSONObject().apply {
                put("slot", claim.slot.name)
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
private fun BreakoutApp(
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
                refreshRemoteLeagues()
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
        if (!requiresOnlineAccount) {
            authGate = AuthGate.SignedIn
            return@LaunchedEffect
        }
        authGate = AuthGate.Checking
        if (account == null) {
            authGate = AuthGate.SignedOut
            return@LaunchedEffect
        }
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

    LaunchedEffect(authGate, account?.accessToken, activeLeagueCode, league?.draftStatus) {
        if (authGate == AuthGate.SignedIn && account?.accessToken?.isNotBlank() == true && isOnlinePlayConfigured()) {
            while (true) {
                delay(if (league?.draftStatus == DraftStatus.Live) 1_000 else 20_000)
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

    LaunchedEffect(Unit) {
        launch {
            runCatching { MusicArtistService.topArtists() }.onSuccess { artists ->
                preloadedMarketArtists = artists
                prefetchArtistImages(context, artists.filter { !it.imageUrl.isNullOrBlank() }.take(80))
            }
        }
        launch { runCatching { MusicArtistService.headlinerCandidates() } }
        launch { runCatching { MusicArtistService.risingCandidates() } }
        launch { runCatching { MusicArtistService.deepCutCandidates() } }
        launch { runCatching { MusicArtistService.wildcardCandidates() } }
    }

    LaunchedEffect(activeLeagueCode) {
        LocalBreakoutStore.saveActiveLeagueCode(context, activeLeagueCode)
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
                if (league != null && hasUsableAccount && selectedArtist == null) {
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
                if (requiresOnlineAccount && authGate == AuthGate.Checking) {
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
                    when (selectedTab) {
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
                                draftPickMode = true
                                marketStartFilter = MarketFilter.Headliners
                                selectedTab = BreakoutTab.Market
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
                                scope.launch {
                                    runCatching { MusicArtistService.topArtists() }.onSuccess { artists ->
                                        preloadedMarketArtists = artists
                                        if (marketQuery.isBlank()) {
                                            marketState = MarketState.Ready(artists)
                                            marketSnapshots = LocalBreakoutStore.updateSnapshots(context, artists)
                                            marketLoadedKey = marketLastKey
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
                            onQueueWaiverArtist = { artist ->
                                if (waiverClaims.size >= activeLeague.settings.maxWaiverClaims) {
                                    Toast.makeText(context, "Your waiver queue is full.", Toast.LENGTH_SHORT).show()
                                } else firstOpenSlotFor(artist, visibleRoster, activeLeague.settings)?.let { slot ->
                                    waiverClaims = waiverClaims + WaiverClaimUi(artist, slot)
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
                                draftPickMode = false
                                marketStartFilter = filter
                                selectedTab = BreakoutTab.Market
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
                                    firstOpenSlotFor(artist, visibleRoster, activeLeague.settings) != null,
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
                            onQueueWaiver = {
                                if (waiverClaims.size >= activeLeague.settings.maxWaiverClaims) {
                                    Toast.makeText(context, "Your waiver queue is full.", Toast.LENGTH_SHORT).show()
                                } else firstOpenSlotFor(artist, visibleRoster, activeLeague.settings)?.let { slot ->
                                    waiverClaims = waiverClaims + WaiverClaimUi(artist, slot)
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

@Composable
private fun ScreenColumn(
    refreshing: Boolean = false,
    onRefresh: (() -> Unit)? = null,
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
private fun AuthCheckingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(BreakoutDimensions.ScreenHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
        ) {
            CircularProgressIndicator(color = BreakoutPrimary)
            Text(
                "Checking your account",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DraftFloatingClock(
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
private fun SignInScreen(
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
private fun ResetPasswordScreen(
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
private fun LeagueSetupScreen(
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
private fun LeagueSetupStepRow(number: String, text: String) {
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
private fun NumberBadge(text: String, accent: Color = BreakoutPrimary) {
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
private fun ScreenHero(
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

@Composable
private fun HomeScreen(
    account: AccountUi?,
    league: LeagueUi,
    roster: List<ArtistUi>,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        TopTitle(title = league.name, subtitle = "${account?.label ?: "Signed in"} · ${league.memberCount} Member League", onMenuClick = onOpenMenu)
        ScreenHero(
            eyebrow = "League Board",
            title = if (roster.isEmpty()) "Build Your First Roster" else "${roster.size} ${if (roster.size == 1) "Artist" else "Artists"} Ready",
            subtitle = if (roster.isEmpty()) {
                "Find your foundation in the market, then chase upside before the rest of the league catches on."
            } else {
                "Track your roster, watch the draft clock, and keep an eye on artists gaining momentum."
            },
            stats = listOf(
                Triple("Roster", roster.size.toString(), "${league.settings.rosterSize} max"),
                Triple(
                    "Deep Cuts",
                    roster.count { it.tag == "Deep Cut" }.toString(),
                    if (roster.count { it.tag == "Deep Cut" } == 1) "Discovery slot" else "Discovery slots"
                ),
                Triple("Members", league.memberCount.toString(), "In league"),
                Triple("Status", league.draftStatus.label, "Draft")
            )
        )
        InviteCodeCard(
            inviteCode = league.inviteCode,
            onCopy = { clipboard.setText(AnnotatedString(league.inviteCode)) }
        )
        DraftCountdownCard(league = league)
    }
}

@Composable
private fun DraftCountdownCard(league: LeagueUi) {
    if (league.draftStatus != DraftStatus.Scheduled || league.settings.draftDateLabel == "Set date") return
    val countdown = draftCountdownLabel(league.settings.draftDateLabel) ?: return
    BreakoutCard {
        Text("Draft Countdown", style = MaterialTheme.typography.titleLarge)
        ScoreLine("Starts In", countdown)
        ScoreLine("Draft Time", league.settings.draftDateLabel.displayDraftDateLabel())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MarketScreen(
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
    onQueueWaiverArtist: (ArtistUi) -> Unit,
    onCancelWaiverArtist: (ArtistUi) -> Unit,
    onPickExpired: () -> Unit
) {
    val context = LocalContext.current
    var hideDrafted by rememberSaveable { mutableStateOf(true) }
    var hadPickControl by rememberSaveable { mutableStateOf(false) }
    var pickSubmitted by rememberSaveable { mutableStateOf(false) }
    var pendingActionArtist by remember { mutableStateOf<ArtistUi?>(null) }
    var pendingActionLabel by remember { mutableStateOf<String?>(null) }
    var showMarketLoadingOverlay by remember { mutableStateOf(false) }
    var loadingMoreArtists by remember { mutableStateOf(false) }
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

    LaunchedEffect(preloadedArtists, query) {
        val artists = preloadedArtists
        if (
            artists != null &&
            artists.isNotEmpty() &&
            query.isBlank() &&
            loadedMarketKey == null &&
            marketState !is MarketState.Ready
        ) {
            val warmedArtists = artists.take(visibleCount)
            prefetchArtistImages(context, warmedArtists)
            onSnapshotsChange(LocalBreakoutStore.updateSnapshots(context, artists))
            onMarketStateChange(MarketState.Ready(artists))
            onLoadedMarketKeyChange(dataKey)
        }
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

    fun List<ArtistUi>.filtered(): List<ArtistUi> =
        if (query.isNotBlank()) {
            val cleanQuery = query.trim()
            sortedWith(
                compareByDescending<ArtistUi> { it.name.searchMatchScore(cleanQuery) }
                    .thenByDescending { it.listeners ?: 0L }
                    .thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
            )
        } else when (activeFilter) {
            MarketFilter.Headliners -> filter { it.isHeadlinerEligible() }
                .sortedWith(compareByDescending<ArtistUi> { it.listeners ?: 0L }.thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) })
            MarketFilter.Rising -> filter { it.marketBucket() == MarketFilter.Rising }
                .sortedByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
            MarketFilter.Wildcards -> filter { it.marketBucket() == MarketFilter.Wildcards }
                .sortedByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
            MarketFilter.DeepCuts -> filter { it.marketBucket() == MarketFilter.DeepCuts }
                .sortedByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) + it.discoverySortValue() }
            MarketFilter.Breakouts -> filter { artist ->
                val snapshot = snapshots[artist.name.lowercase()]
                snapshot != null && snapshot.currentListeners > snapshot.previousListeners
            }.sortedByDescending { artist -> artist.breakoutScore(snapshots[artist.name.lowercase()]) }
            null -> sortedWith(
                compareByDescending<ArtistUi> { it.isHeadlinerEligible() }
                    .thenByDescending { it.listeners ?: 0L }
                    .thenByDescending { it.breakoutScore(snapshots[it.name.lowercase()]) }
            )
        }

    val readyArtists = (displayedState as? MarketState.Ready)?.artists
    val filteredPreview = remember(readyArtists, filterKey, visibleCount, draftedByArtist, hideDrafted, roster, query, activeFilter, snapshots) {
        readyArtists
            ?.filtered()
            ?.filter { artist -> !draftPickMode || !hideDrafted || draftedByArtist[artist.name.lowercase()] == null }
            ?.sortedBy { artist -> draftPickMode && roster.values.any { it.name == artist.name } }
            ?.marketDistinct()
            ?.take(visibleCount)
            .orEmpty()
    }

    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            activeFilter?.let { onPreviousFilterChange(it) }
            onActiveFilterChange(null)
        }
    }

    LaunchedEffect(query, activeFilter) {
        if (lastMarketKey != null && lastMarketKey != filterKey) {
            onVisibleCountChange(20)
            if (query.isNotBlank() || activeFilter != previousFilter) {
                listState.scrollToItem(0)
            }
        }
        onLastMarketKeyChange(filterKey)
    }

    LaunchedEffect(listState, displayedState, visibleCount) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible to total
        }
            .map { (lastVisible, total) -> total > 0 && lastVisible >= total - 4 }
            .distinctUntilChanged()
            .collect { nearBottom ->
                val ready = displayedState as? MarketState.Ready ?: return@collect
                val available = ready.artists.filtered().size
                if (nearBottom && visibleCount < available) {
                    val filteredArtists = ready.artists.filtered()
                    val nextCount = (visibleCount + 20).coerceAtMost(available)
                    loadingMoreArtists = true
                    prefetchArtistImages(context, filteredArtists.drop(visibleCount).take(nextCount - visibleCount))
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
        if (query.isBlank() && warmedArtists.isNotEmpty()) {
            onSnapshotsChange(LocalBreakoutStore.updateSnapshots(context, warmedArtists))
            prefetchArtistImages(context, warmedArtists.filtered().take(visibleCount))
            onMarketStateChange(MarketState.Ready(warmedArtists))
            onLoadedMarketKeyChange(requestKey)
            return@LaunchedEffect
        }
        if (query.isBlank()) {
            onMarketStateChange(MarketState.Loading)
            onLoadedMarketKeyChange(null)
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
            showMarketLoadingOverlay = true
        } else if (showMarketLoadingOverlay) {
            delay(280)
            showMarketLoadingOverlay = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!initialMarketLoading) LazyColumn(
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
            item {
                TopTitle(
                    title = if (draftPickMode) "Draft Market" else "Market",
                    subtitle = when {
                        draftPickMode -> "Pick one artist for this turn"
                        draftStatus == DraftStatus.Scheduled -> "Scout artists before the draft"
                        else -> "Audience Scale and Discovery"
                    },
                    onMenuClick = onOpenMenu
                )
            }
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
                            .padding(vertical = BreakoutDimensions.sm),
                        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
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
                    val filteredArtists = filteredPreview
                    if (filteredArtists.isEmpty()) {
                        item {
                            StatusCard(
                                title = "${activeFilter?.label ?: "Search"} Unavailable",
                                detail = if (activeFilter == MarketFilter.Breakouts) {
                                    "New movement rankings will appear as the market refreshes over time."
                                } else {
                                    "No artists in this result match the selected filter."
                                }
                            )
                        }
                    } else {
                        items(filteredArtists, key = { it.stableListKey() }) { artist ->
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
                                firstOpenSlotFor(artist, roster, leagueSettings) != null
                            val canDraftFromMarket = draftPickMode &&
                                canMakeDraftPick &&
                                !drafted &&
                                !draftedByYou &&
                                firstOpenSlotFor(artist, roster, leagueSettings) != null
                            val hasSwipeAction = draftedByYou || waiverQueued || canDraftFromMarket || canQueueFromMarket
                            val artistKey = artist.stableListKey()
                            ArtistRow(
                                modifier = Modifier.animateItem(),
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
                        val totalAvailable = current.artists.filtered()
                            .filter { artist -> !draftPickMode || !hideDrafted || draftedByArtist[artist.name.lowercase()] == null }
                            .marketDistinct()
                            .size
                        if (visibleCount < totalAvailable || loadingMoreArtists) {
                            item {
                                LoadingState(if (loadingMoreArtists) "Loading More Artists" else "Scroll for More Artists")
                            }
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = showMarketLoadingOverlay,
            enter = fadeIn() + slideInVertically { it / 12 },
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
                        "waiver" -> onQueueWaiverArtist(artist)
                        "cancel" -> onCancelWaiverArtist(artist)
                        "drop" -> onRemoveRosterArtist(artist)
                    }
                    pendingActionArtist = null
                    pendingActionLabel = null
                }
            )
        }
    }
}

@Composable
private fun RosterScreen(
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
    var rosterReady by remember(rosterArtists.joinToString { it.name }) { mutableStateOf(rosterArtists.isEmpty()) }
    LaunchedEffect(rosterArtists.joinToString { it.name }) {
        rosterReady = rosterArtists.isEmpty()
        if (rosterArtists.isNotEmpty()) {
            prefetchArtistImages(context, rosterArtists)
            rosterReady = true
        }
    }
    if (!rosterReady) ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        TopTitle(
            title = "Roster",
            subtitle = "Loading Artists",
            onMenuClick = onOpenMenu
        )
        LoadingState("Loading Roster")
    } else ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        val slots = activeRosterSlots(leagueSettings)
        TopTitle(
            title = "Roster",
            subtitle = "${roster.size} of ${slots.size} Filled",
            onMenuClick = onOpenMenu
        )
        if (draftStatus == DraftStatus.Scheduled || draftStatus == DraftStatus.Lobby) {
            StatusCard(
                title = "Roster Locked",
                detail = "Rosters are filled during the live draft."
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 760.dp),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)
        ) {
            items(slots, key = { slot -> "${slot.name}:${roster[slot]?.stableListKey().orEmpty()}" }) { slot ->
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
                            .heightIn(max = 460.dp),
                        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
                    ) {
                        items(waiverClaims, key = { it.artist.stableListKey() }) { claim ->
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
private fun MoveRosterSlotDialog(
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
private fun FillRosterSlotDialog(
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
private fun WaiverClaimRow(
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
        color = WaiverAccent.copy(alpha = 0.10f),
        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
        border = BorderStroke(
            width = 1.dp,
            color = WaiverAccent.copy(alpha = 0.38f)
        )
    ) {
        Row(
            modifier = Modifier.padding(BreakoutDimensions.md),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(46.dp)
                    .heightIn(min = 76.dp)
                    .clip(
                        RoundedCornerShape(
                            BreakoutDimensions.SmallCornerRadius
                        )
                    )
                    .background(
                        WaiverAccent.copy(alpha = 0.16f)
                    )
                    .border(
                        width = 1.dp,
                        color = WaiverAccent.copy(alpha = 0.32f),
                        shape = RoundedCornerShape(
                            BreakoutDimensions.SmallCornerRadius
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "#",
                    color = WaiverAccent.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = priority.toString(),
                    color = WaiverAccent,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                    .clickable(onClick = onArtistSelected)
                    .padding(horizontal = BreakoutDimensions.xs, vertical = BreakoutDimensions.xs)
                    .heightIn(min = 76.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = claim.artist.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
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

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    RosterActionIcon(
                        text = "Up",
                        accent = BreakoutPrimary,
                        enabled = canMoveUp,
                        size = 32.dp,
                        onClick = onMoveUp
                    )

                    RosterActionIcon(
                        text = "Dn",
                        accent = BreakoutPrimary,
                        enabled = canMoveDown,
                        size = 32.dp,
                        onClick = onMoveDown
                    )
                }

                RosterActionIcon(
                    text = "Cancel",
                    accent = BreakoutCoral,
                    size = 36.dp,
                    width = 78.dp,
                    onClick = onCancel
                )
            }
        }
    }
}

@Composable
private fun WaiverOrderRow(rank: Int, team: String) {
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

@Composable
private fun DraftScreen(
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

    ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        TopTitle(title = "Draft", subtitle = league.draftStatus.label, onMenuClick = onOpenMenu)
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
            ScoreLine("Rising Artists", league.settings.risingSlots.toString())
            ScoreLine("Wildcards", league.settings.wildcardSlots.toString())
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
private fun DraftLobbyScreen(
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
private fun DraftRoomScreen(
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
                        MarketFilter.entries.filter { it != MarketFilter.Breakouts }.forEach { filter ->
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
private fun DraftPickStrip(
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
private fun DraftPickChip(
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
private fun DraftPickSummaryRow(
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
private fun DraftEndedScreen(
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
private fun DraftSummaryScreen(
    league: LeagueUi,
    draftPicks: List<DraftPickUi>,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit
) {
    val memberCount = league.memberCount.coerceAtLeast(1)
    val sortedPicks = draftPicks.sortedBy { it.pickNumber }
    val rounds = sortedPicks.groupBy { ((it.pickNumber - 1) / memberCount) + 1 }
    ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        TopTitle(title = "Draft Summary", subtitle = league.name, onMenuClick = onOpenMenu)
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
                BreakoutCard(border = BorderStroke(1.dp, BreakoutSecondary.copy(alpha = 0.24f))) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Round $round", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        Text("${picks.size} picks", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelLarge)
                    }
                    picks.forEach { pick ->
                        DraftSummaryPickRow(
                            pick = pick,
                            onArtistSelected = { onArtistSelected(pick.artist) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DraftSummaryPickRow(pick: DraftPickUi, onArtistSelected: () -> Unit) {
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

@Composable
private fun MatchupScreen(
    league: LeagueUi,
    account: AccountUi?,
    roster: Map<RosterSlot, ArtistUi>,
    draftPicks: List<DraftPickUi>,
    members: List<LeagueMemberUi>?,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    onOpenWeek: (Int) -> Unit,
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
    val currentWeek = 1
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
    if (!matchupReady || waitingForMembers) ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        TopTitle(title = "Matchup", subtitle = league.name, onMenuClick = onOpenMenu)
        LoadingState("Loading Matchup")
    } else ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        TopTitle(title = "Matchup", subtitle = league.name, onMenuClick = onOpenMenu)
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
private fun AllMatchupsScreen(
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
    LaunchedEffect(initialWeek, weekCount) {
        selectedWeek = initialWeek.coerceIn(1, weekCount)
    }
    val pairs = matchupPairsForWeek(memberNames, selectedWeek)
    ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        TopTitle(title = "All Matchups", subtitle = league.name, onMenuClick = onOpenMenu)
        BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RosterActionIcon(
                    text = "<",
                    accent = BreakoutPrimary,
                    onClick = { selectedWeek = if (selectedWeek == 1) weekCount else selectedWeek - 1 }
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
                    onClick = { selectedWeek = if (selectedWeek == weekCount) 1 else selectedWeek + 1 }
                )
            }
        }
        if (memberNames.size < 2) {
            StatusCard("Waiting for Members", "Matchups appear once at least two members are in the league.")
        } else {
            pairs.forEach { pair ->
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

@Composable
private fun AllMatchupCard(
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
private fun MatchupScheduleRow(week: MatchupWeekUi, isCurrent: Boolean, onClick: () -> Unit = {}) {
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
private fun MatchupScoreboardCard(
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
private fun MatchupScoreSide(
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
        Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(current, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        Text("Proj. $projected", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun ScheduleCheckCard(messages: List<String>) {
    val scheduleYellow = Color(0xFFFFC857)
    AlertNoticeCard(
        title = "Schedule Check",
        messages = messages,
        accent = scheduleYellow,
        symbol = "!"
    )
}

@Composable
private fun BenchDivider() {
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
private fun MatchupSlotComparisonRow(
    slot: RosterSlot,
    userArtist: ArtistUi?,
    opponentArtist: ArtistUi?,
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
                artist = userArtist,
                alignEnd = false,
                onArtistSelected = onArtistSelected,
                modifier = Modifier.weight(1f)
            )
            Text(":", color = BreakoutOutline, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            MatchupSideCell(
                artist = opponentArtist,
                alignEnd = true,
                onArtistSelected = onArtistSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MatchupSideCell(
    artist: ArtistUi?,
    alignEnd: Boolean,
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
            .then(if (artist != null) Modifier.clickable { onArtistSelected(artist) } else Modifier)
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
private fun MatchupArtistRow(slot: RosterSlot, artist: ArtistUi, onClick: () -> Unit) {
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

@Composable
private fun StandingsScreen(
    league: LeagueUi,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit
) {
    ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        TopTitle(title = "Standings", subtitle = league.name, onMenuClick = onOpenMenu)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Awaiting Week 1", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("Results appear after scoring closes.", color = BreakoutTextSecondary)
                }
                NumberBadge("1", BreakoutSecondary)
            }
            listOf("Rank" to "--", "Record" to "0-0", "Points For" to "--", "Points Against" to "--").forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                        .background(BreakoutSurfaceVariant.copy(alpha = 0.45f))
                        .padding(BreakoutDimensions.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(label, color = BreakoutTextSecondary, style = MaterialTheme.typography.titleSmall)
                    Text(value, color = BreakoutPrimary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeagueScreen(
    league: LeagueUi,
    account: AccountUi?,
    draftPicks: List<DraftPickUi>,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    onUpdateLeague: ((LeagueUi) -> LeagueUi) -> Unit,
    onTransferManager: (String) -> Unit,
    onKickMember: (String) -> Unit,
    onOpenRoster: () -> Unit,
    memberRosterToOpen: String?,
    onMemberRosterOpened: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit,
    onMemberRosterArtistSelected: (LeagueMemberUi, ArtistUi) -> Unit,
    onRunWaivers: () -> Unit,
    onLeaveLeague: () -> Unit,
    onDeleteLeague: () -> Unit
) {
    val settings = league.settings
    val draftSettingsEditable = league.isManager && league.draftStatus == DraftStatus.Scheduled
    val draftTimeEditable = draftSettingsEditable && league.memberCount >= MinLeagueMembers
    var editedLeagueName by rememberSaveable(league.inviteCode) { mutableStateOf(league.name) }
    var showMembers by rememberSaveable(league.inviteCode) { mutableStateOf(false) }
    var members by remember { mutableStateOf<List<LeagueMemberUi>>(emptyList()) }
    var selectedMember by remember { mutableStateOf<LeagueMemberUi?>(null) }
    var rosterMember by remember { mutableStateOf<LeagueMemberUi?>(null) }
    var pendingTransfer by remember { mutableStateOf<LeagueMemberUi?>(null) }
    var pendingKick by remember { mutableStateOf<LeagueMemberUi?>(null) }
    var confirmRunWaivers by rememberSaveable(league.inviteCode) { mutableStateOf(false) }
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
    val visibleMembers = members.ifEmpty {
        listOf(
            LeagueMemberUi(
                username = accountMemberName,
                teamName = accountMemberName,
                role = if (league.isManager) "manager" else "member"
            )
        )
    }

    LaunchedEffect(showMembers, league.id, account?.accessToken) {
        val token = account?.accessToken.orEmpty()
        if (showMembers && token.isNotBlank()) {
            members = SupabaseLeagueService.loadMembers(token, league.id).getOrDefault(emptyList())
        }
    }

    LaunchedEffect(memberRosterToOpen, members, account?.accessToken) {
        val username = memberRosterToOpen ?: return@LaunchedEffect
        showMembers = true
        val loadedMembers = if (members.isEmpty() && account?.accessToken?.isNotBlank() == true) {
            SupabaseLeagueService.loadMembers(account.accessToken, league.id).getOrDefault(emptyList()).also { members = it }
        } else {
            members
        }
        loadedMembers.firstOrNull { it.username.equals(username, ignoreCase = true) }?.let {
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

    ScreenColumn(refreshing = refreshing, onRefresh = onRefresh) {
        TopTitle(
            title = "League",
            subtitle = if (league.isManager) "Manager tools" else "League rules",
            onMenuClick = onOpenMenu
        )
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
            ScoreLine("Invites", league.inviteState)
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
            StepperRow("Rising Artists", settings.risingSlots.toString(), draftSettingsEditable,
                minusEnabled = settings.risingSlots > 0,
                plusEnabled = settings.risingSlots < 4,
                onMinus = { onUpdateLeague { it.copy(settings = settings.copy(risingSlots = (settings.risingSlots - 1).coerceAtLeast(0))) } },
                onPlus = { onUpdateLeague { it.copy(settings = settings.copy(risingSlots = (settings.risingSlots + 1).coerceAtMost(4))) } }
            )
            StepperRow("Wildcards", settings.wildcardSlots.toString(), draftSettingsEditable,
                minusEnabled = settings.wildcardSlots > 0,
                plusEnabled = settings.wildcardSlots < 4,
                onMinus = { onUpdateLeague { it.copy(settings = settings.copy(wildcardSlots = (settings.wildcardSlots - 1).coerceAtLeast(0))) } },
                onPlus = { onUpdateLeague { it.copy(settings = settings.copy(wildcardSlots = (settings.wildcardSlots + 1).coerceAtMost(4))) } }
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
            Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                if (league.isManager) {
                    SecondaryButton(
                        if (league.invitesOpen) "Close Invites" else "Open Invites",
                        modifier = Modifier.weight(1f),
                        enabled = league.isManager,
                        onClick = { onUpdateLeague { it.copy(invitesOpen = !it.invitesOpen) } }
                    )
                }
                SecondaryButton(
                    text = if (showMembers) "Hide Members" else "Review Members",
                    modifier = Modifier.weight(1f),
                    onClick = { showMembers = !showMembers }
                )
            }
            AnimatedVisibility(
                visible = showMembers,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
                    visibleMembers.forEach { member ->
                        MemberReviewRow(
                            member = member,
                            currentUsername = accountUsername,
                            canManage = league.isManager && members.isNotEmpty(),
                            onOpen = { selectedMember = member },
                            onTransfer = { pendingTransfer = member },
                            onKick = { pendingKick = member }
                        )
                    }
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
                    showMembers = false
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
                    showMembers = false
                }
            )
        }
        selectedMember?.let { member ->
            MemberDetailDialog(
                member = member,
                currentUsername = accountUsername,
                canManage = league.isManager && members.isNotEmpty() && !member.isManager &&
                    !member.username.equals(accountUsername, ignoreCase = true),
                canViewRoster = !member.username.equals(accountUsername, ignoreCase = true),
                onDismiss = { selectedMember = null },
                onViewRoster = {
                    selectedMember = null
                    rosterMember = member
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

@Composable
private fun AccountScreen(
    account: AccountUi?,
    onOpenMenu: () -> Unit,
    onSaveAccount: (AccountUi, (String?) -> Unit) -> Unit,
    onDeleteAccount: ((String?) -> Unit) -> Unit,
    onSignOut: () -> Unit
) {
    var email by rememberSaveable(account?.email.orEmpty()) { mutableStateOf(account?.email.orEmpty()) }
    var username by rememberSaveable(account?.username.orEmpty()) { mutableStateOf(account?.username.orEmpty()) }
    var mailingList by rememberSaveable(account?.mailingList ?: true) { mutableStateOf(account?.mailingList ?: true) }
    var accountError by rememberSaveable { mutableStateOf<String?>(null) }
    var accountStatus by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by rememberSaveable { mutableStateOf(false) }
    var deleting by rememberSaveable { mutableStateOf(false) }
    var confirmSignOut by rememberSaveable { mutableStateOf(false) }
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    val canSave = email.trim().contains("@") && isValidUsername(username.trim()) && !saving

    ScreenColumn {
        TopTitle(title = "Account", subtitle = account?.label ?: "Signed out", onMenuClick = onOpenMenu)
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
            accent = BreakoutSecondary
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
                AnimatedFeedbackText(message = "Username must be 3-24 letters, numbers, or underscores.", color = BreakoutCoral)
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
        BreakoutCard {
            Text("Account Actions", style = MaterialTheme.typography.titleLarge)
            DangerButton(text = "Log Out", onClick = { confirmSignOut = true })
            DangerButton(text = if (deleting) "Deleting Account" else "Delete Account", onClick = { confirmDelete = true })
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
private fun MemberReviewRow(
    member: LeagueMemberUi,
    currentUsername: String,
    canManage: Boolean,
    onOpen: () -> Unit,
    onTransfer: () -> Unit,
    onKick: () -> Unit
) {
    val isSelf = member.username.equals(currentUsername, ignoreCase = true)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(BreakoutSurfaceVariant)
            .clickable(onClick = onOpen)
            .padding(BreakoutDimensions.md),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(member.username, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                if (member.isManager) "Manager" else "Member",
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
private fun MemberDetailDialog(
    member: LeagueMemberUi,
    currentUsername: String,
    canManage: Boolean,
    canViewRoster: Boolean,
    onDismiss: () -> Unit,
    onViewRoster: () -> Unit,
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
                    SecondaryButton(text = "View Roster", modifier = Modifier.fillMaxWidth(), onClick = onViewRoster)
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
private fun MemberRosterDialog(
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
private fun MemberRosterPickRow(pick: DraftPickUi, onClick: () -> Unit) {
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
            Text(pick.artist.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${pick.slot.label} - Pick ${pick.pickNumber}", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        TagLabel(pick.artist.tag)
    }
}

@Composable
private fun DraftedStatusCard(detail: String) {
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
private fun ArtistOwnershipBanner(
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
                Text(if (isInRoster) "✓" else "W", color = accent, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
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
private fun ArtistAvailabilityBanner(detail: String) {
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
private fun OverlayBackButton(onClick: () -> Unit) {
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
private fun LatestReleaseCard(
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
private fun TopTrackCard(
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
private fun ChartSignalsCard(artist: ArtistUi) {
    val hasChartData = artist.kworbRank != null ||
        artist.kworbDailyListenerChange != null ||
        artist.kworbTotalStreams != null ||
        artist.kworbDailyStreams != null ||
        artist.kworbTopSongTitle != null ||
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
                label = "Audience Move",
                value = artist.kworbDailyListenerChange?.formatSignedCompact() ?: "--",
                caption = "Today vs yesterday",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
            StatTile(
                label = "Daily Streams",
                value = artist.kworbDailyStreams?.formatSignedCompact() ?: "--",
                caption = artist.kworbLeadDailyStreams?.let { "${it.formatCompact()} as lead artist" } ?: "Artist streams today",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StreamSplitCard(artist: ArtistUi) {
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
private fun ArtistHistoryTimelineCard(
    draftedDetail: String?,
    droppedAtMillis: Long?
) {
    val events = buildList {
        draftedDetail?.let { add(Triple("Drafted", it.removePrefix("Drafted by "), BreakoutPrimary)) }
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
                    Text(event.second, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ConfirmActionCard(
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

@Composable
private fun ArtistDetailScreen(
    artist: ArtistUi,
    isInRoster: Boolean,
    draftStatus: DraftStatus,
    draftedStatusLabel: String?,
    draftedHistoryLabel: String?,
    droppedAtMillis: Long?,
    isWaiverQueued: Boolean,
    canAddToRoster: Boolean,
    canQueueWaiver: Boolean,
    onAddToRoster: () -> Unit,
    onRemoveFromRoster: () -> Unit,
    onQueueWaiver: () -> Unit,
    onCancelWaiver: () -> Unit,
    onDraftPick: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var detailArtist by remember(artist.name) { mutableStateOf(artist) }
    var detailReady by remember(artist.name) { mutableStateOf(false) }
    var detailContentVisible by remember(artist.name) { mutableStateOf(false) }
    var confirmRemove by remember { mutableStateOf(false) }
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
    if (!detailReady) {
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
    } else Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = detailContentVisible,
            enter = fadeIn() + slideInVertically { it / 12 },
            exit = fadeOut()
        ) {
            ScreenColumn {
                Box(modifier = Modifier.height(58.dp))
                ArtistArtwork(artist = shownArtist, size = 260.dp)
                Text(shownArtist.name, style = MaterialTheme.typography.headlineLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                    StatTile("Audience", shownArtist.listeners?.formatCompact() ?: "Sizing Up", shownArtist.tag, Modifier.weight(1f))
                    StatTile("Breakout", shownArtist.breakoutScore(null).formatScore(), "Score", Modifier.weight(1f))
                }
            AnimatedVisibility(
                visible = isInRoster || isWaiverQueued,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ArtistOwnershipBanner(
                    isInRoster = isInRoster,
                    isWaiverQueued = isWaiverQueued
                )
            }
            AnimatedVisibility(
                visible = !isInRoster && !isWaiverQueued && draftStatus == DraftStatus.Complete && !canQueueWaiver,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ArtistAvailabilityBanner(
                    detail = if (draftedStatusLabel != null) {
                        "This artist is already on a roster."
                    } else {
                        "Your roster does not have an open slot for this artist."
                    }
                )
            }
            BreakoutCard {
                Text("Draft Profile", style = MaterialTheme.typography.titleLarge)
                ScoreLine("Role", shownArtist.tag)
                InfoScoreLine(
                    label = "Market Value",
                    value = shownArtist.price,
                    info = listOf(
                        "A rough draft-market estimate based mostly on audience scale and current signal.",
                        "Useful for comparing artist scale at a glance."
                    )
                )
                shownArtist.albumCount?.let { ScoreLine("Catalog", "$it releases") }
                ScoreLine("Read", shownArtist.marketNote)
                ScoreLine("Risk", shownArtist.riskLabel)
            }
            BreakoutCard {
                Text("Scoring Signals", style = MaterialTheme.typography.titleLarge)
                shownArtist.projectionRows().forEach { row ->
                    val help = scoringSignalInfo(row.label)
                    if (help != null) {
                        SignalMetricRow(label = row.label, value = row.value, detail = row.detail, info = help)
                    } else {
                        ScoreLine(row.label, row.value)
                    }
                }
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
            if (draftedHistoryLabel != null || droppedAtMillis != null) {
                ArtistHistoryTimelineCard(
                    draftedDetail = draftedHistoryLabel,
                    droppedAtMillis = droppedAtMillis
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
        if ((canAddToRoster || canQueueWaiver || isWaiverQueued || isInRoster) && draftedStatusLabel == null) {
            RosterToggleButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(end = BreakoutDimensions.lg, bottom = BreakoutDimensions.lg),
                added = isInRoster,
                waiver = canQueueWaiver || isWaiverQueued,
                waiverCancel = isWaiverQueued,
                size = 56.dp,
                solid = true,
                onClick = when {
                    isInRoster -> ({ confirmRemove = true })
                    isWaiverQueued -> onCancelWaiver
                    canQueueWaiver -> onQueueWaiver
                    else -> onDraftPick
                }
            )
        }
        if (confirmRemove) {
            ConfirmActionCard(
                title = "Drop ${shownArtist.name}?",
                detail = "This removes the artist from your roster.",
                confirmText = "Drop",
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
private fun LeagueDrawer(
    leagues: List<LeagueUi>,
    activeLeague: LeagueUi?,
    draftLeagueName: String,
    draftInviteCode: String,
    onLeagueNameChange: (String) -> Unit,
    onInviteCodeChange: (String) -> Unit,
    joinError: String?,
    onSwitchLeague: (LeagueUi) -> Unit,
    onCreateLeague: () -> Unit,
    onJoinLeague: () -> Unit,
    onLeaveLeague: () -> Unit,
    onNavigate: (BreakoutTab) -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    var confirmLeave by rememberSaveable(activeLeague?.inviteCode) { mutableStateOf(false) }
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
                .verticalScroll(rememberScrollState())
                .padding(BreakoutDimensions.CardPadding),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)
        ) {
            BreakoutCard(
                contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding),
                border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.38f))
            ) {
                Text("Breakout", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                Text(activeLeague?.name ?: "Create or join a league", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (activeLeague != null) {
                InviteCodeCard(
                    inviteCode = activeLeague.inviteCode,
                    onCopy = { clipboard.setText(AnnotatedString(activeLeague.inviteCode)) }
                )
                BreakoutCard {
                    Text("Current League", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(activeLeague.name, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    DangerButton(text = leaveActionText, onClick = { confirmLeave = true })
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
                }
            }
            BreakoutCard {
                Text("Leagues", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
            BreakoutCard {
                Text("Navigate", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                DrawerNavRow("League Settings", BreakoutTab.League, onNavigate)
                DrawerNavRow("All Matchups", BreakoutTab.AllMatchups, onNavigate)
                if (activeLeague?.draftStatus == DraftStatus.Complete) {
                    DrawerNavRow("Draft Summary", BreakoutTab.DraftSummary, onNavigate)
                }
                DrawerNavRow("Standings", BreakoutTab.Standings, onNavigate)
                DrawerNavRow("Account", BreakoutTab.Account, onNavigate)
            }
            BreakoutCard {
                Text("Create League", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                StyledTextField(
                    value = draftLeagueName,
                    onValueChange = onLeagueNameChange,
                    label = "League Name",
                    maxLength = MaxLeagueNameLength
                )
                PrimaryButton(
                    text = "Create League",
                    enabled = draftLeagueName.isNotBlank(),
                    onClick = onCreateLeague
                )
            }
            BreakoutCard {
                Text("Join League", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                StyledTextField(
                    value = draftInviteCode,
                    onValueChange = { value ->
                        onInviteCodeChange(value.uppercase().filter { it.isLetterOrDigit() }.take(6))
                    },
                    label = "Invite Code",
                    keyboardActions = KeyboardActions(onDone = {
                        if (isValidInviteCode(draftInviteCode)) onJoinLeague()
                    })
                )
                SecondaryButton(
                    text = "Join League",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isValidInviteCode(draftInviteCode),
                    onClick = onJoinLeague
                )
                AnimatedFeedbackText(message = cleanVisibleError(joinError), color = BreakoutCoral)
            }
        }
    }
}

@Composable
private fun DrawerNavRow(label: String, tab: BreakoutTab, onNavigate: (BreakoutTab) -> Unit) {
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

@Composable
private fun TopTitle(title: String, subtitle: String, onMenuClick: (() -> Unit)? = null) {
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
private fun MenuButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(BreakoutDimensions.MinimumTouchTarget)
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(BreakoutSurfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("☰", color = BreakoutPrimary, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
    }
}

@Composable
private fun InviteCodeCard(inviteCode: String, onCopy: () -> Unit) {
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
private fun LeagueSwitcherRow(
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
            Text("${league.inviteCode} · ${league.memberCount} members", color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        if (selected) {
            Pill("Active")
        }
    }
}

@Composable
private fun BreakoutCard(
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
private fun RecommendedPickButton(
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
                        "${artist.name} - ${artist.tag} - Best fit right now",
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
private fun MarketHeroHeader(
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
private fun MiniSignalPill(text: String, accent: Color) {
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
private fun ArtistRow(
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
    val actionGutter = BreakoutDimensions.md
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
        actionsOpen && hasPrimaryAction -> -revealPx
        else -> 0f
    }
    val cardOffset by animateFloatAsState(targetValue = targetOffset, label = "artistActionReveal")
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
                .clickable(enabled = !actionsOpen, onClick = onClick)
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
        border = BorderStroke(
            1.dp,
            when {
                isWaiverQueued -> WaiverAccent.copy(alpha = 0.9f)
                isInRoster -> BreakoutPrimary.copy(alpha = 0.78f)
                isDraftedByOther -> DraftedOtherAccent.copy(alpha = 0.78f)
                else -> BreakoutOutline.copy(alpha = 0.55f)
            }
        )
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
                    .background(
                        when {
                            isWaiverQueued -> WaiverAccent
                            isInRoster -> BreakoutPrimary
                            isDraftedByOther -> DraftedOtherAccent
                            else -> BreakoutOutline.copy(alpha = 0.62f)
                        }
                    )
            )
            ArtistArtwork(artist = artist, size = BreakoutDimensions.ArtworkCard)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = BreakoutDimensions.ArtworkCard),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    artist.name,
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
                    statusLabel ?: if (isWaiverQueued) "Waiver #${waiverQueuePosition ?: ""}".trim() else artist.compactRead,
                    color = when {
                        isInRoster -> BreakoutPrimary
                        isDraftedByOther -> DraftedOtherAccent
                        isWaiverQueued -> WaiverAccent
                        else -> BreakoutTextSecondary
                    },
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
private fun RosterSlotCard(
    modifier: Modifier = Modifier,
    slot: RosterSlot,
    artist: ArtistUi?,
    draftStatus: DraftStatus,
    draftRoomContext: Boolean,
    onArtistSelected: (ArtistUi) -> Unit,
    onOpenMarket: () -> Unit,
    canRemove: Boolean = true,
    canMove: Boolean = false,
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
            if (artist != null) BreakoutPrimary.copy(alpha = 0.35f) else BreakoutOutline.copy(alpha = 0.55f)
        )
    ) {
        Crossfade(targetState = artist?.stableListKey() ?: "empty:${slot.name}", label = "rosterSlotSwap") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val shownArtist = artist
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
                    Text(slot.label, color = BreakoutSecondary, style = MaterialTheme.typography.labelLarge, maxLines = 1)
                    Text(
                        text = shownArtist?.name ?: emptyTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        shownArtist?.audienceLabel ?: slot.hint,
                        color = BreakoutTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (shownArtist == null) {
                    if (canMove) {
                        RosterActionIcon(text = "Swap", accent = BreakoutPrimary, width = 58.dp, onClick = onMoveArtist)
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
                }
            }
        }
    }
}

@Composable
private fun RosterActionIcon(
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
                text == "↑" || text == "↓" -> {
                    MaterialTheme.typography.headlineSmall
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
private fun EmptySlotArtwork() {
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
private fun ArtistArtwork(artist: ArtistUi, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(if (size > 100.dp) RoundedCornerShape(BreakoutDimensions.HeroCornerRadius) else RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .background(Brush.linearGradient(listOf(BreakoutPrimary, BreakoutCoral, BreakoutSecondary))),
        contentAlignment = Alignment.Center
    ) {
        if (!artist.imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = artist.bestImageUrl,
                contentDescription = "${artist.name} artist image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(artist.initials, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
private fun FilterChipRow(
    filters: List<MarketFilter>,
    selected: MarketFilter?,
    onSelected: (MarketFilter?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
    ) {
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
private fun StatusCard(title: String, detail: String) {
    BreakoutCard {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(detail, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DraftDatePickerDialog(
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
private fun DraftTimePickerDialog(
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
private fun AnimatedFeedbackText(message: String?, color: Color) {
    AnimatedVisibility(
        visible = !message.isNullOrBlank(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Text(message.orEmpty(), color = color, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun WarningCard(
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
private fun AlertNoticeCard(
    title: String,
    messages: List<String>,
    accent: Color,
    symbol: String,
    onMessageClick: ((String) -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = BreakoutSurfaceVariant.copy(alpha = 0.72f)),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.42f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(accent.copy(alpha = 0.18f), Color.Transparent)
                    )
                )
                .padding(BreakoutDimensions.CardPadding),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.18f))
                    .border(1.dp, accent.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(symbol, color = accent, fontWeight = FontWeight.Black)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                messages.forEach { message ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                            .background(BreakoutSurface.copy(alpha = 0.42f))
                            .then(if (onMessageClick != null) Modifier.clickable { onMessageClick(message) } else Modifier)
                            .padding(BreakoutDimensions.sm),
                        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(accent)
                        )
                        Text(
                            message,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreLine(label: String, value: String) {
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
private fun InfoScoreLine(label: String, value: String, info: List<String>) {
    var showInfo by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = BreakoutTextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(BreakoutSurfaceVariant)
                    .border(1.dp, BreakoutOutline.copy(alpha = 0.45f), CircleShape)
                    .clickable { showInfo = true },
                contentAlignment = Alignment.Center
            ) {
                Text("?", color = BreakoutSecondary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
        Text(value, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
    if (showInfo) {
        SignalInfoDialog(
            title = label,
            details = info,
            onDismiss = { showInfo = false }
        )
    }
}

@Composable
private fun SignalMetricRow(label: String, value: String, detail: String?, info: List<String>) {
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
private fun SignalInfoDialog(
    title: String,
    details: List<String>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.widthIn(max = 380.dp),
            color = BreakoutSurface.copy(alpha = 0.99f),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.78f)),
            tonalElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BreakoutDimensions.xl),
                verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.md)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 64.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(BreakoutPrimary)
                )
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
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("x", color = BreakoutTextSecondary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    color = BreakoutSurfaceVariant.copy(alpha = 0.62f),
                    shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                    border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(BreakoutDimensions.md),
                        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
                    ) {
                        details.forEach { detail ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 7.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(BreakoutSecondary)
                                )
                                Text(
                                    detail,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
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
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = BreakoutDimensions.sm, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Done", color = BreakoutSecondary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun scoringSignalInfo(label: String): List<String>? = when (label) {
    "Breakout Score" -> listOf(
        "Combines audience movement, track momentum, listener growth, play growth, and release freshness",
        "Runs from 0 to 100",
        "Higher means a stronger breakout profile"
    )
    "Audience Move" -> listOf(
        "Uses daily listener movement from chart data when available",
        "Runs from 0 to 100",
        "50 is flat; above 50 means gains, below 50 means losses"
    )
    "Daily Streams" -> listOf(
        "Uses daily stream movement from chart data when available",
        "Runs from 0 to 100",
        "50 is flat; above 50 means stronger listening activity"
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
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    maxLength: Int = Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text,
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
            imeAction = ImeAction.Next
        ),
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation
    )
}

@Composable
private fun StepperRow(
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
private fun OptionCycleRow(
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
private fun ToggleRow(
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
private fun CompactButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
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
private fun LoadingState(label: String) {
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
private fun InlineMarketWarmupCard() {
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
private fun ArtistLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(BreakoutDimensions.ScreenHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.xl)) {
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
                    Text("A", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = BreakoutSecondary)
                }
                Text("Loading Artist", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
private fun MarketInitializingScreen(onOpenMenu: () -> Unit) {
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
    var lineIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (lineIndex < loadingLines.lastIndex) {
            delay(1850)
            lineIndex += 1
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(BreakoutDimensions.ScreenHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        MenuButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = BreakoutDimensions.xs),
            onClick = onOpenMenu
        )
        BreakoutCard(contentPadding = PaddingValues(BreakoutDimensions.xl)) {
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
                    Text("M", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, color = BreakoutPrimary)
                }
                Text("Market Loading", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
private fun StatTile(label: String, value: String, caption: String, modifier: Modifier = Modifier) {
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
private fun Pill(text: String) {
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
private fun TagLabel(text: String) {
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
private fun RosterToggleButton(
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
private fun PrimaryButton(
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
private fun SecondaryButton(
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
private fun AccentButton(
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
private fun DangerButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = BreakoutDimensions.MinimumTouchTarget),
        colors = ButtonDefaults.buttonColors(containerColor = BreakoutCoral.copy(alpha = 0.28f)),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius)
    ) {
        Text(text, color = BreakoutCoral, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DangerMiniButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
private fun AccentConfirmButton(
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
private fun DangerIconButton(onClick: () -> Unit) {
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
private fun SecondaryMiniButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
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
private fun BreakoutBottomNavigation(
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

private fun isOnlinePlayConfigured(): Boolean =
    BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()

private fun String.cleanUsernameInput(): String =
    filter { it.isLetterOrDigit() || it == '_' }.take(MaxUsernameLength)

private fun isValidUsername(username: String): Boolean =
    username.matches(Regex("^[A-Za-z0-9_]{3,$MaxUsernameLength}$"))

private fun isValidEmail(email: String): Boolean =
    email.trim().let { value ->
        value.length in 5..MaxEmailLength &&
            value.count { it == '@' } == 1 &&
            value.substringBefore("@").isNotBlank() &&
            value.substringAfter("@").contains(".") &&
            value.none { it.isWhitespace() }
    }

private fun passwordPolicyError(password: String): String? = when {
    password.length < MinPasswordLength -> "Password must be at least $MinPasswordLength characters."
    password.none { it.isLowerCase() } -> "Password must include a lowercase letter."
    password.none { it.isUpperCase() } -> "Password must include an uppercase letter."
    password.none { it.isDigit() } -> "Password must include a number."
    password.none { !it.isLetterOrDigit() } -> "Password must include a symbol."
    else -> null
}

private fun emailQualityError(email: String): String? {
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

private fun String.cleanDraftDateLabel(): String =
    trim()
        .takeUnless { it.isBlank() || it.equals("null", ignoreCase = true) }
        ?.normalizeDraftDateLabel()
        ?: "Set date"

private fun String.displayDraftDateLabel(): String =
    cleanDraftDateLabel().let { label ->
        if (label == "Set date") {
            "Date Not Set"
        } else {
            parseDraftDateTime(label)?.format(DraftDateDisplayFormatter) ?: label
        }
    }

private fun formatDraftLength(totalSeconds: Int): String {
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

private fun draftCountdownLabel(draftDateLabel: String): String? {
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

private fun formatPickDuration(seconds: Int?): String {
    val safeSeconds = seconds ?: return "--"
    return if (safeSeconds < 60) {
        "${safeSeconds}s"
    } else {
        "${safeSeconds / 60}m ${safeSeconds % 60}s"
    }
}

private fun String.normalizeDraftDateLabel(): String {
    val value = trim()
    return parseDraftDateTime(value)?.format(DraftDateInputFormatter) ?: value
}

private fun parseDraftDateTime(value: String): LocalDateTime? {
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

private fun String.parseServerInstantMillis(): Long? {
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

private fun draftDateInputPart(label: String): String =
    parseDraftDateTime(label)?.toLocalDate()?.toString() ?: ""

private fun draftTimeInputPart(label: String): String =
    parseDraftDateTime(label)?.toLocalTime()?.let { "%02d:%02d".format(it.hour, it.minute) } ?: ""

private fun draftDateDisplayPart(date: String): String =
    runCatching {
        LocalDate.parse(date.trim()).format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }.getOrDefault("Choose Date")

private fun draftTimeDisplayPart(time: String): String =
    runCatching {
        LocalDateTime.parse("2026-01-01 ${time.trim()}", DraftDateInputFormatter)
            .toLocalTime()
            .format(DateTimeFormatter.ofPattern("h:mm a"))
    }.getOrDefault("Choose Time")

private fun draftDateValidationError(date: String, time: String): String? {
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

private fun Long.minuteLabel(): String = if (this == 1L) "1 minute" else "$this minutes"

private fun draftDateLabelFromInputs(date: String, time: String): String =
    LocalDateTime.parse("${date.trim()} ${time.trim()}", DraftDateInputFormatter).format(DraftDateInputFormatter)

private fun String.toDraftServerTimestamp(): String? =
    parseDraftDateTime(this)
        ?.atZone(ZoneId.systemDefault())
        ?.toOffsetDateTime()
        ?.toString()

private fun profileUsernameFor(account: AccountUi): String {
    val preferred = account.username.cleanUsernameInput()
    if (isValidUsername(preferred)) return preferred
    val emailName = account.email.substringBefore("@").cleanUsernameInput()
    if (isValidUsername(emailName)) return emailName
    return profileFallbackUsername(account)
}

private fun profileFallbackUsername(account: AccountUi): String {
    val userSuffix = account.userId.filter { it.isLetterOrDigit() }.take(8).ifBlank { "000000" }
    return "user_$userSuffix".take(MaxUsernameLength)
}

private fun showDraftSystemNotification(context: Context, title: String, message: String, notificationId: Int) {
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

private fun scheduleDraftReminderNotifications(context: Context, league: LeagueUi) {
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
                    if (offsetMinutes == 0L) "Draft lobby open" else "Draft starts in $label"
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

private fun scheduleReminderAlarm(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
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

private fun isSignInAgainMessage(message: String): Boolean =
    message.contains("Sign in again", ignoreCase = true) ||
        message.contains("session expired", ignoreCase = true)

private fun isAuthFailure(rawMessage: String?): Boolean {
    val message = rawMessage.orEmpty()
    return message.contains("JWT expired", ignoreCase = true) ||
        message.contains("invalid JWT", ignoreCase = true) ||
        message.contains("invalid claim", ignoreCase = true) ||
        message.contains("not authenticated", ignoreCase = true) ||
        message.contains("Authentication required", ignoreCase = true) ||
        message.contains("PGRST301", ignoreCase = true) ||
        message.contains("PGRST303", ignoreCase = true)
}

private fun friendlyLeagueLoadError(rawMessage: String?): String {
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

private fun cleanVisibleError(rawMessage: String?): String {
    val message = rawMessage.orEmpty()
    return when {
        message.isBlank() -> ""
        isAuthFailure(message) -> "Your session expired. Please log in again."
        message.trim().startsWith("{") -> "Something went wrong. Please try again."
        else -> message
    }
}

private fun normalizedErrorText(rawMessage: String?): String {
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

private fun authRetryDelaySeconds(message: String): Long? =
    Regex("after\\s+(\\d+)\\s+seconds?", RegexOption.IGNORE_CASE)
        .find(message)
        ?.groupValues
        ?.getOrNull(1)
        ?.toLongOrNull()

private fun isRateLimitError(rawMessage: String?): Boolean {
    val message = normalizedErrorText(rawMessage)
    return message.contains("over_email_send_rate_limit", ignoreCase = true) ||
        message.contains("email rate limit", ignoreCase = true) ||
        message.contains("rate limit", ignoreCase = true) ||
        message.contains("wait a few seconds", ignoreCase = true) ||
        message.contains("too many", ignoreCase = true) && message.contains("attempt", ignoreCase = true) ||
        message.contains("only request this after", ignoreCase = true)
}

private fun isVerificationEmailNotice(rawMessage: String?): Boolean {
    val message = normalizedErrorText(rawMessage)
    return message.contains("Check your email", ignoreCase = true) ||
        message.contains("waiting for email verification", ignoreCase = true) ||
        message.contains("Email not confirmed", ignoreCase = true) ||
        message.contains("email_not_confirmed", ignoreCase = true)
}

private fun rateLimitMessage(message: String, mode: AuthMode): String {
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

private fun friendlyAuthError(rawMessage: String?, mode: AuthMode): String {
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
            "Username must be 3-24 letters, numbers, or underscores."
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

private fun passwordBackendMessage(message: String): String = when {
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

private fun friendlyPasswordResetError(rawMessage: String?): String {
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

private fun friendlyAccountError(rawMessage: String?): String {
    val message = rawMessage.orEmpty()
    return when {
        message.contains("JWT expired", ignoreCase = true) ||
            message.contains("invalid JWT", ignoreCase = true) ||
            message.contains("Sign in again", ignoreCase = true) ->
            "Your session expired. Please log in again."
        message.contains("Username must be", ignoreCase = true) ||
            message.contains("profiles_username_format", ignoreCase = true) ->
            "Username must be 3-24 letters, numbers, or underscores."
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

private fun friendlyJoinError(rawMessage: String?): String {
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

private fun friendlyDraftPickError(rawMessage: String?): String {
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

private fun friendlyDraftStartError(rawMessage: String?): String {
    val message = rawMessage.orEmpty()
    return when {
        isAuthFailure(message) -> "Your session expired. Please log in again."
        message.contains("at least one more member", ignoreCase = true) -> "Invite at least one more member before the draft can start."
        message.contains("manager", ignoreCase = true) -> "Only the manager can start the draft."
        else -> "Could not start the draft. Please refresh and try again."
    }
}

private fun friendlyLeagueError(rawMessage: String?): String {
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

private fun firstOpenSlotFor(
    artist: ArtistUi,
    roster: Map<RosterSlot, ArtistUi>,
    settings: LeagueSettingsUi
): RosterSlot? = preferredSlotsFor(artist).firstOrNull { slot ->
    slot in activeRosterSlots(settings) &&
    roster[slot] == null && slot.canHold(artist)
}

private fun preferredSlotsFor(artist: ArtistUi): List<RosterSlot> = when {
    artist.isDeepCutEligible() -> deepCutSlots() + risingSlots() + wildcardSlots() + benchSlots()
    artist.isRisingEligible() -> risingSlots() + wildcardSlots() + benchSlots()
    else -> headlinerSlots() + benchSlots()
}

private fun RosterSlot.canHold(artist: ArtistUi): Boolean = when (this) {
    RosterSlot.HeadlinerOne,
    RosterSlot.HeadlinerTwo,
    RosterSlot.HeadlinerThree,
    RosterSlot.HeadlinerFour -> artist.isHeadlinerEligible()
    RosterSlot.RisingOne,
    RosterSlot.RisingTwo,
    RosterSlot.RisingThree,
    RosterSlot.RisingFour -> artist.isRisingEligible()
    RosterSlot.WildcardOne,
    RosterSlot.WildcardTwo,
    RosterSlot.WildcardThree,
    RosterSlot.WildcardFour -> !artist.isHeadlinerEligible()
    RosterSlot.BenchOne,
    RosterSlot.BenchTwo,
    RosterSlot.BenchThree,
    RosterSlot.BenchFour,
    RosterSlot.BenchFive,
    RosterSlot.BenchSix -> true
    RosterSlot.DeepCutOne,
    RosterSlot.DeepCutTwo,
    RosterSlot.DeepCutThree -> artist.isDeepCutEligible()
}

private fun activeRosterSlots(settings: LeagueSettingsUi): List<RosterSlot> =
    headlinerSlots().take(settings.headlinerSlots) +
        risingSlots().take(settings.risingSlots) +
        wildcardSlots().take(settings.wildcardSlots) +
        deepCutSlots().take(settings.deepCutSlots) +
        benchSlots().take(settings.benchSlots)

private fun headlinerSlots(): List<RosterSlot> = listOf(
    RosterSlot.HeadlinerOne,
    RosterSlot.HeadlinerTwo,
    RosterSlot.HeadlinerThree,
    RosterSlot.HeadlinerFour
)

private fun risingSlots(): List<RosterSlot> = listOf(
    RosterSlot.RisingOne,
    RosterSlot.RisingTwo,
    RosterSlot.RisingThree,
    RosterSlot.RisingFour
)

private fun wildcardSlots(): List<RosterSlot> = listOf(
    RosterSlot.WildcardOne,
    RosterSlot.WildcardTwo,
    RosterSlot.WildcardThree,
    RosterSlot.WildcardFour
)

private fun deepCutSlots(): List<RosterSlot> = listOf(
    RosterSlot.DeepCutOne,
    RosterSlot.DeepCutTwo,
    RosterSlot.DeepCutThree
)

private fun benchSlots(): List<RosterSlot> = listOf(
    RosterSlot.BenchOne,
    RosterSlot.BenchTwo,
    RosterSlot.BenchThree,
    RosterSlot.BenchFour,
    RosterSlot.BenchFive,
    RosterSlot.BenchSix
)

private fun ArtistUi.isHeadlinerEligible(): Boolean =
    (listeners ?: 0L) >= 8_000_000 || (kworbRank ?: Int.MAX_VALUE) <= 250

private fun ArtistUi.isRisingEligible(): Boolean =
    !isHeadlinerEligible() && ((listeners ?: Long.MAX_VALUE) < 2_500_000 || (spotifyPopularity ?: 0) in 45..63)

private fun ArtistUi.isDeepCutEligible(): Boolean =
    !isHeadlinerEligible() && (listeners ?: Long.MAX_VALUE) < 750_000 && (spotifyPopularity ?: 0) < 45

private fun ArtistUi.marketBucket(): MarketFilter = when {
    isHeadlinerEligible() -> MarketFilter.Headliners
    isDeepCutEligible() -> MarketFilter.DeepCuts
    isRisingEligible() -> MarketFilter.Rising
    else -> MarketFilter.Wildcards
}

private fun ArtistUi.discoverySortValue(): Double {
    val scale = listeners ?: return 0.0
    val distance = kotlin.math.abs(scale - 250_000) / 250_000.0
    return (1.0 - distance).coerceIn(0.0, 1.0) * 100
}

private fun List<ArtistUi>.marketDistinct(): List<ArtistUi> =
    groupBy { it.name.artistKey() }
        .values
        .mapNotNull { group ->
            group.maxWithOrNull(
                compareBy<ArtistUi> { it.listeners ?: 0L }
                    .thenBy { it.spotifyPopularity ?: 0 }
                    .thenBy { it.trackPopularity ?: 0 }
            )
        }

private fun ArtistUi.stableListKey(): String =
    spotifyId?.let { "spotify:$it" } ?: id?.let { "id:$it" } ?: "name:${name.artistKey()}:${listeners ?: 0L}:${imageUrl.orEmpty().hashCode()}"

private fun ArtistUi.breakoutScore(snapshot: SnapshotUi?): Double {
    val trackSignal = (trackPopularity ?: spotifyPopularity ?: 50).coerceIn(0, 100).toDouble()
    val artistSignal = (spotifyPopularity ?: trackPopularity ?: 50).coerceIn(0, 100).toDouble()
    val recency = (snapshot?.releaseRecencyScore ?: releaseRecencyScore ?: 45.0).coerceIn(0.0, 100.0)
    val audience = normalizedAudienceFloor(listeners) * 100.0
    val kworbDailySignal = kworbDailyListenerChange?.let { signedLogSignal(it) }
    val streamSignal = kworbDailyStreams?.let { dailyStreamSignal(it) }
    val peakSignal = kworbPeakListeners?.let { normalizedAudienceFloor(it) * 100.0 }
    val discovery = when {
        listeners == null -> 42.0
        listeners < 750_000 -> 82.0
        listeners < 2_500_000 -> 74.0
        listeners < 8_000_000 -> 58.0
        else -> 38.0
    }
    val audienceTrend = when {
        kworbDailySignal != null && peakSignal != null -> (kworbDailySignal * 0.70) + ((audience - peakSignal + 50.0).coerceIn(0.0, 100.0) * 0.30)
        kworbDailySignal != null -> kworbDailySignal
        else -> audience
    }
    val fallbackScore = (trackSignal * 0.19) +
        (artistSignal * 0.14) +
        (audience * 0.15) +
        (audienceTrend * 0.23) +
        ((streamSignal ?: trackSignal) * 0.13) +
        (discovery * 0.08) +
        (recency * 0.08)
    val availableSignalCount = listOfNotNull(
        trackPopularity,
        spotifyPopularity,
        kworbDailyListenerChange,
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
    val growthScore = spotifyFollowerGrowth * 0.30 +
        trackGrowth * 0.25 +
        lastFmListenerGrowth * 0.20 +
        streamGrowth * 0.15 +
        recency * 0.10

    return (growthScore * 0.72 + confidenceAdjustedScore * 0.28).coerceIn(0.0, 100.0)
}

private fun SnapshotUi?.percentGrowth(previous: Long?, current: Long?): Double {
    if (previous == null || current == null || previous <= 0L) return 0.0
    val percent = ((current - previous).toDouble() / previous.toDouble()) * 100.0
    return (percent * 5.0).coerceIn(0.0, 100.0)
}

private fun SnapshotUi?.pointGrowth(previous: Int?, current: Int?): Double {
    if (previous == null || current == null) return 0.0
    return ((current - previous).toDouble() * 5.0).coerceIn(0.0, 100.0)
}

private fun signedLogSignal(value: Long): Double {
    if (value == 0L) return 50.0
    val magnitude = (log10(kotlin.math.abs(value).toDouble() + 1.0) / 6.0).coerceIn(0.0, 1.0)
    return if (value > 0) {
        50.0 + magnitude * 50.0
    } else {
        50.0 - magnitude * 50.0
    }.coerceIn(0.0, 100.0)
}

private fun dailyStreamSignal(value: Long): Double {
    if (value <= 0L) return 0.0
    val logValue = log10(value.toDouble() + 1.0)
    return ((logValue - 4.0) / 4.8 * 100.0).coerceIn(0.0, 98.0)
}

private fun JSONObject.nullableInt(key: String): Int? = if (isNull(key) || !has(key)) null else optInt(key)

private fun JSONObject.nullableLong(key: String): Long? = if (isNull(key) || !has(key)) null else optLong(key)

private fun JSONObject.putNullable(key: String, value: Any?) {
    if (value == null) put(key, JSONObject.NULL) else put(key, value)
}

private fun ArtistUi.projectionRows(): List<SignalRowUi> {
    return listOf(
        SignalRowUi("Breakout Score", breakoutScore(null).formatScore()),
        SignalRowUi(
            "Audience Move",
            kworbDailyListenerChange?.let { signedLogSignal(it).formatScore() } ?: "Not Available"
        ),
        SignalRowUi(
            "Daily Streams",
            kworbDailyStreams?.let { dailyStreamSignal(it).formatScore() } ?: "Not Available"
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

private fun Double.formatScore(): String = "%.1f".format(coerceIn(0.0, 100.0))

private fun Double.formatPoints(): String = "%.1f".format(coerceAtLeast(0.0))

private fun DraftFormat.previous(): DraftFormat {
    val entries = DraftFormat.entries
    return entries[(ordinal - 1 + entries.size) % entries.size]
}

private fun DraftFormat.next(): DraftFormat {
    val entries = DraftFormat.entries
    return entries[(ordinal + 1) % entries.size]
}

private fun isValidInviteCode(code: String): Boolean =
    code.matches(Regex("[A-Z2-9]{6}"))

private fun sampleDraftOrder(league: LeagueUi): List<String> =
    List(league.memberCount.coerceAtLeast(1)) { index ->
        if (index == 0) "You" else "Team ${index + 1}"
    }.let { order ->
        if (league.settings.draftFormat == DraftFormat.Snake && league.currentPickIndex / order.size % 2 == 1) {
            order.reversed()
        } else {
            order
        }
    }

private fun currentDraftPicker(
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

private fun isAccountOnClock(league: LeagueUi, members: List<LeagueMemberUi>?, account: AccountUi?): Boolean =
    currentDraftPickerUsername(league, members)?.equals(account?.username.orEmpty(), ignoreCase = true) == true

private fun isMemberAutoPickEnabled(members: List<LeagueMemberUi>?, account: AccountUi?, league: LeagueUi): Boolean =
    members
        ?.firstOrNull { it.username.equals(account?.username.orEmpty(), ignoreCase = true) }
        ?.autoPickEnabled
        ?: league.autoPickEnabled

private fun currentDraftPickerUsername(league: LeagueUi, members: List<LeagueMemberUi>?): String? {
    return draftPickerUsernameAt(league, members, league.currentPickIndex)
}

private fun scheduleForUser(members: List<String>, username: String, seasonWeeks: Int): List<MatchupWeekUi> =
    (1..seasonWeeks.coerceAtLeast(1)).map { week -> matchupForWeek(members, username, week) ?: MatchupWeekUi(week, null, false) }

private fun rosterForMemberName(username: String, draftPicks: List<DraftPickUi>): Map<RosterSlot, ArtistUi> =
    draftPicks
        .filter { it.pickedBy.equals(username, ignoreCase = true) }
        .associate { it.slot to it.artist }

private fun matchupPairsForWeek(members: List<String>, week: Int): List<Pair<String, String?>> {
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

private fun matchupScheduleWarnings(memberCount: Int, seasonWeeks: Int): List<String> {
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

private fun matchupForWeek(members: List<String>, username: String, week: Int): MatchupWeekUi? {
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

private fun Int.floorMod(modulus: Int): Int =
    ((this % modulus) + modulus) % modulus

private fun draftPickerLabelAt(
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

private fun draftPickerUsernameAt(league: LeagueUi, members: List<LeagueMemberUi>?, pickIndex: Int): String? {
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

private suspend fun bestEligibleDraftPick(
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

private fun List<ArtistUi>.strategicDraftRecommendation(
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

private fun normalizedAudienceFloor(listeners: Long?): Double {
    val scale = listeners ?: return 0.0
    return ((log10(max(scale.toDouble(), 10_000.0)) - 4.0) / 5.0).coerceIn(0.0, 1.0)
}

private fun ArtistUi.riskScore(): Double {
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

private fun RosterSlot.isBenchSlot(): Boolean = name.startsWith("Bench")

private fun projectedDraftNeeds(settings: LeagueSettingsUi, roster: Map<RosterSlot, ArtistUi>): List<String> {
    val slots = activeRosterSlots(settings)
    return slots
        .filter { roster[it] == null }
        .take(5)
        .map { slot -> "${slot.label} fit" }
        .ifEmpty { listOf("Roster filled") }
}

private fun waiverOrderPreview(memberCount: Int): List<String> =
    List(memberCount.coerceAtLeast(1)) { index ->
        if (index == 0) "You" else "Team ${index + 1}"
    }

private fun String.nextDraftDateLabel(): String = when (this) {
    "Set date" -> "Tonight"
    "Tonight" -> "Tomorrow"
    "Tomorrow" -> "This weekend"
    "This weekend" -> "Next week"
    else -> "Set date"
}

private fun generateInviteCode(): String =
    "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".let { characters ->
        List(6) { characters[Random.nextInt(characters.length)] }.joinToString("")
    }

private fun String.artistKey(): String =
    lowercase()
        .replace("&amp;", "&")
        .replace(Regex("[^a-z0-9]+"), "")

private fun String.wordsForSearch(): List<String> =
    lowercase()
        .replace(Regex("[^a-z0-9\\s]+"), " ")
        .split(Regex("\\s+"))
        .filter { it.length >= 2 }

private fun String.searchMatchScore(query: String): Double {
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

private fun String.removeLeadingArticle(): String =
    trim().replace(Regex("^(the|a|an)\\s+", RegexOption.IGNORE_CASE), "")

private fun String.levenshteinDistance(other: String): Int {
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

private fun String.cleanHtml(separator: String = ""): String =
    replace(Regex("<[^>]+>"), separator)
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&#039;", "'")
        .replace("&apos;", "'")
        .replace("&nbsp;", " ")
        .replace(Regex("\\s+"), " ")
        .trim()

private fun Long.formatCompact(): String = when {
    this >= 1_000_000_000 -> "${"%.1f".format(this / 1_000_000_000.0)}B"
    this >= 1_000_000 -> "${"%.1f".format(this / 1_000_000.0)}M"
    this >= 1_000 -> "${"%.1f".format(this / 1_000.0)}K"
    else -> toString()
}

private fun Long.formatSignedCompact(): String =
    if (this >= 0) "+${formatCompact()}" else "-${kotlin.math.abs(this).formatCompact()}"

private fun String.displayReleaseDate(): String =
    runCatching {
        val date = when (length) {
            4 -> LocalDate.of(toInt(), 1, 1)
            7 -> LocalDate.parse("$this-01")
            else -> LocalDate.parse(take(10))
        }
        DateTimeFormatter.ofPattern("MMM d, yyyy").format(date)
    }.getOrDefault(this)

private fun String.displayKworbDate(): String =
    runCatching {
        DateTimeFormatter.ofPattern("MMM d, yyyy")
            .format(LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy/MM/dd")))
    }.getOrDefault(this)

private fun Long.formatLocalDateTime(): String =
    runCatching {
        DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a")
            .format(Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()))
    }.getOrDefault("Recently")

private fun String.parseCompactLong(): Long? {
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
private fun LeagueSetupPreview() {
    BreakoutTheme(darkTheme = true) {
        LeagueSetupScreen(joinError = null, onClearError = {}, onCreateLeague = {}, onJoinLeague = {})
    }
}

@Preview(showBackground = true, widthDp = 412, fontScale = 1.3f)
@Composable
private fun MarketPreview() {
    BreakoutTheme(darkTheme = true) {
        val previewListState = rememberLazyListState()
        var previewQuery by rememberSaveable { mutableStateOf("") }
        var previewFilter by rememberSaveable { mutableStateOf<MarketFilter?>(MarketFilter.Headliners) }
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
            activeFilter = previewFilter,
            onActiveFilterChange = { previewFilter = it },
            previousFilter = previewPreviousFilter,
            onPreviousFilterChange = { previewPreviousFilter = it },
            marketState = previewMarketState,
            onMarketStateChange = { previewMarketState = it },
            snapshots = previewSnapshots,
            onSnapshotsChange = { previewSnapshots = it },
            visibleCount = previewVisibleCount,
            onVisibleCountChange = { previewVisibleCount = it },
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
            onQueueWaiverArtist = {},
            onCancelWaiverArtist = {},
            onPickExpired = {}
        )
    }
}
