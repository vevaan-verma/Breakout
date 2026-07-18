package com.vevaan.breakout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vevaan.breakout.core.designsystem.BreakoutDimensions
import com.vevaan.breakout.ui.theme.BreakoutCoral
import com.vevaan.breakout.ui.theme.BreakoutOutline
import com.vevaan.breakout.ui.theme.BreakoutPrimary
import com.vevaan.breakout.ui.theme.BreakoutSurface
import com.vevaan.breakout.ui.theme.BreakoutSurfaceVariant
import com.vevaan.breakout.ui.theme.BreakoutTextSecondary

private const val MaxActiveOutgoingTrades = 8

@Composable
internal fun TradeScreen(
    league: LeagueUi,
    account: AccountUi?,
    roster: Map<RosterSlot, ArtistUi>,
    draftPicks: List<DraftPickUi>,
    members: List<LeagueMemberUi>?,
    tradeOffers: List<TradeOfferUi>,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenMenu: () -> Unit,
    initialTradeUsername: String? = null,
    onInitialTradeUsernameConsumed: () -> Unit = {},
    onSendTrade: (LeagueMemberUi, List<TradeArtistItemUi>, List<TradeArtistItemUi>) -> Unit,
    onAcceptTrade: (TradeOfferUi) -> Unit,
    onDeclineTrade: (TradeOfferUi) -> Unit,
    onCancelTrade: (TradeOfferUi) -> Unit,
    onArtistSelected: (ArtistUi) -> Unit
) {
    val selfName = account?.username.orEmpty()
    val tradeMembers = members.orEmpty()
        .filter { it.username.isNotBlank() && !it.username.equals(selfName, ignoreCase = true) }
    var selectedMemberName by rememberSaveable(league.id, tradeMembers.size) {
        mutableStateOf(tradeMembers.firstOrNull()?.username.orEmpty())
    }
    LaunchedEffect(initialTradeUsername, tradeMembers) {
        val target = initialTradeUsername ?: return@LaunchedEffect
        tradeMembers.firstOrNull { it.username.equals(target, ignoreCase = true) }?.let {
            selectedMemberName = it.username
            onInitialTradeUsernameConsumed()
        }
    }
    val selectedMember = tradeMembers.firstOrNull { it.username == selectedMemberName } ?: tradeMembers.firstOrNull()
    var offeredSlotNames by rememberSaveable(league.id, roster.size) { mutableStateOf(setOf<String>()) }
    var requestedSlotNames by rememberSaveable(league.id, selectedMemberName) { mutableStateOf(setOf<String>()) }
    val memberRoster = selectedMember?.let { rosterForMemberName(it.username, draftPicks) }.orEmpty()
    val offeredItems = roster.entries
        .filter { it.key.name in offeredSlotNames }
        .map { TradeArtistItemUi(it.value, it.key) }
    val requestedItems = memberRoster.entries
        .filter { it.key.name in requestedSlotNames }
        .map { TradeArtistItemUi(it.value, it.key) }
    val duplicatePendingOffer = selectedMember?.let { member ->
        tradeOffers.any { offer ->
            offer.status == "pending" &&
                offer.outgoing &&
                offer.recipientUsername.equals(member.username, ignoreCase = true) &&
                offer.offeredItems.sameTradeArtists(offeredItems) &&
                offer.requestedItems.sameTradeArtists(requestedItems)
        }
    } == true
    val activeOutgoingTrades = tradeOffers.count { it.status == "pending" && it.outgoing }
    val listState = rememberLazyListState()
    var scrollToSentOffer by rememberSaveable(league.id) { mutableStateOf(false) }
    val validation = tradeValidation(
        offeredItems = offeredItems,
        requestedItems = requestedItems,
        myRoster = roster,
        theirRoster = memberRoster,
        settings = league.settings
    )
        ?: if (activeOutgoingTrades >= MaxActiveOutgoingTrades) "You already have $MaxActiveOutgoingTrades active outgoing trades. Cancel one before sending another." else null
        ?: if (duplicatePendingOffer) "You already sent this exact trade. Change the artists or cancel the open offer first." else null

    ScreenColumn(
        refreshing = refreshing,
        onRefresh = onRefresh,
        externalListState = listState,
        stickyTopBar = { TopTitle(title = "Trades", subtitle = league.name, onMenuClick = onOpenMenu) }
    ) {
        BreakoutCard(
            contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding),
            border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.38f))
        ) {
            Text("Trade Center", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text(
                "Build multi-artist offers. Trades expire after 48 hours and process instantly when accepted.",
                color = BreakoutTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                StatTile("Pending", tradeOffers.count { it.status == "pending" }.toString(), "Open offers", Modifier.weight(1f))
                StatTile("Outgoing", "$activeOutgoingTrades/$MaxActiveOutgoingTrades", "Active sent", Modifier.weight(1f))
            }
        }

        if (league.draftStatus != DraftStatus.Complete) {
            StatusCard("Trades Locked", "Trades open after the draft is complete.")
            return@ScreenColumn
        }

        BreakoutCard {
            Text("Build Offer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            if (tradeMembers.isEmpty()) {
                StatusCard("No Members Available", "Invite another member before sending trades.")
            } else {
                TradeMemberPicker(
                    members = tradeMembers,
                    selectedUsername = selectedMember?.username.orEmpty(),
                    onSelected = {
                        selectedMemberName = it.username
                        requestedSlotNames = emptySet()
                    }
                )
                TradeRosterPicker(
                    title = "You Give",
                    entries = roster.entries.toList(),
                    selectedSlots = offeredSlotNames,
                    onSelected = { slot -> offeredSlotNames = offeredSlotNames.toggle(slot.name) },
                    onArtistSelected = onArtistSelected
                )
                Crossfade(targetState = selectedMember?.username.orEmpty(), label = "tradePartnerRoster") {
                    TradeRosterPicker(
                        title = "You Get",
                        entries = memberRoster.entries.toList(),
                        selectedSlots = requestedSlotNames,
                        onSelected = { slot -> requestedSlotNames = requestedSlotNames.toggle(slot.name) },
                        onArtistSelected = onArtistSelected
                    )
                }
                if (validation != null) {
                    StatusCard("Trade Check", validation)
                }
                val grandfatheredTradeArtists = (offeredItems + requestedItems).filter { it.artist.isGrandfatheredFor() }
                if (grandfatheredTradeArtists.isNotEmpty()) {
                    StatusCard(
                        "Grandfathered Eligibility Transfers",
                        grandfatheredTradeArtists.joinToString { item ->
                            "${item.artist.name} keeps ${item.artist.acquiredRole} eligibility if the trade is accepted."
                        }
                    )
                }
                PrimaryButton(
                    text = "Send Trade",
                    enabled = validation == null && selectedMember != null && offeredItems.isNotEmpty() && requestedItems.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val member = selectedMember ?: return@PrimaryButton
                        scrollToSentOffer = true
                        onSendTrade(member, offeredItems, requestedItems)
                        offeredSlotNames = emptySet()
                        requestedSlotNames = emptySet()
                    }
                )
            }
        }

        BreakoutCard {
            Text("Offers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            if (tradeOffers.isEmpty()) {
                Text("No trade offers yet.", color = BreakoutTextSecondary)
            } else {
                tradeOffers.forEach { offer ->
                    TradeOfferCard(
                        offer = offer,
                        selfName = selfName,
                        onAccept = { onAcceptTrade(offer) },
                        onDecline = { onDeclineTrade(offer) },
                        onCancel = { onCancelTrade(offer) },
                        onArtistSelected = onArtistSelected
                    )
                }
            }
        }
    }

    LaunchedEffect(activeOutgoingTrades, scrollToSentOffer) {
        if (scrollToSentOffer && activeOutgoingTrades > 0) {
            listState.animateScrollToItem(2)
            scrollToSentOffer = false
        }
    }
}

@Composable
private fun TradeMemberPicker(
    members: List<LeagueMemberUi>,
    selectedUsername: String,
    onSelected: (LeagueMemberUi) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
        Text("Trade With", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelLarge)
        BoxWithConstraints {
            val menuWidth = maxWidth
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                    .clickable { expanded = true },
                color = BreakoutSurfaceVariant.copy(alpha = 0.76f),
                shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.42f))
            ) {
                Row(
                    modifier = Modifier.padding(BreakoutDimensions.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        selectedUsername.ifBlank { "Choose member" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text("Select", color = BreakoutPrimary, fontWeight = FontWeight.Bold)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(menuWidth)
                    .background(BreakoutSurface)
                    .border(1.dp, BreakoutPrimary.copy(alpha = 0.34f), RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            ) {
                members.forEach { member ->
                    DropdownMenuItem(
                        text = {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = if (member.username == selectedUsername) BreakoutPrimary.copy(alpha = 0.18f) else BreakoutSurfaceVariant.copy(alpha = 0.38f),
                                shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                                border = BorderStroke(1.dp, if (member.username == selectedUsername) BreakoutPrimary.copy(alpha = 0.42f) else BreakoutOutline.copy(alpha = 0.18f))
                            ) {
                                Text(
                                    member.username,
                                    modifier = Modifier.padding(horizontal = BreakoutDimensions.md, vertical = BreakoutDimensions.sm),
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        onClick = {
                            expanded = false
                            onSelected(member)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TradeRosterPicker(
    title: String,
    entries: List<Map.Entry<RosterSlot, ArtistUi>>,
    selectedSlots: Set<String>,
    onSelected: (RosterSlot) -> Unit,
    onArtistSelected: (ArtistUi) -> Unit
) {
    Column(
        modifier = Modifier.animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = BreakoutTextSecondary, style = MaterialTheme.typography.labelLarge)
            Text("${selectedSlots.size} selected", color = BreakoutPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
        if (entries.isEmpty()) {
            StatusCard("No Artists", "This roster has no artists available.")
        } else {
            entries.forEach { (slot, artist) ->
                val selected = slot.name in selectedSlots
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                        .clickable { onSelected(slot) },
                    color = if (selected) BreakoutPrimary.copy(alpha = 0.16f) else BreakoutSurfaceVariant.copy(alpha = 0.70f),
                    shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                    border = BorderStroke(1.dp, if (selected) BreakoutPrimary.copy(alpha = 0.58f) else BreakoutOutline.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(BreakoutDimensions.md),
                        horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.clickable { onArtistSelected(artist) }) {
                            ArtistArtwork(artist = artist, size = BreakoutDimensions.ArtworkList)
                        }
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                artist.name,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(BreakoutDimensions.xs))
                                    .clickable { onArtistSelected(artist) },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(slot.label, color = BreakoutTextSecondary, style = MaterialTheme.typography.bodyMedium)
                        }
                        SelectionDot(selected)
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionDot(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(if (selected) BreakoutPrimary else BreakoutSurface)
            .border(1.dp, if (selected) BreakoutPrimary else BreakoutOutline.copy(alpha = 0.55f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) Text("✓", color = BreakoutSurface, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun TradeOfferCard(
    offer: TradeOfferUi,
    selfName: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onCancel: () -> Unit,
    onArtistSelected: (ArtistUi) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BreakoutSurfaceVariant.copy(alpha = 0.70f),
        shape = RoundedCornerShape(BreakoutDimensions.CardCornerRadius),
        border = BorderStroke(1.dp, tradeAccent(offer).copy(alpha = 0.50f))
    ) {
        Column(
            modifier = Modifier.padding(BreakoutDimensions.md),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    when {
                        offer.incoming -> "Incoming Offer"
                        offer.outgoing -> "Sent Offer"
                        else -> "League Offer"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Text(offer.status.replaceFirstChar { it.uppercase() }, color = tradeAccent(offer), fontWeight = FontWeight.Bold)
            }
            val proposerIsSelf = offer.proposerUsername.equals(selfName, ignoreCase = true)
            val recipientIsSelf = offer.recipientUsername.equals(selfName, ignoreCase = true)
            val leftName = when {
                proposerIsSelf || recipientIsSelf -> "You"
                else -> offer.proposerUsername
            }
            val rightName = when {
                proposerIsSelf -> offer.recipientUsername
                recipientIsSelf -> offer.proposerUsername
                else -> offer.recipientUsername
            }
            val leftItems = if (recipientIsSelf) offer.requestedItems else offer.offeredItems
            val rightItems = if (recipientIsSelf) offer.offeredItems else offer.requestedItems
            TradeSwapRow(
                leftLabel = if (leftName == "You") "You give" else "$leftName gives",
                leftItems = leftItems,
                rightLabel = if (rightName == "You") "You give" else "$rightName gives",
                rightItems = rightItems,
                onArtistSelected = onArtistSelected
            )
            if (offer.status == "pending") {
                Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
                    if (offer.incoming) {
                        PrimaryButton("Accept", modifier = Modifier.weight(1f), onClick = onAccept)
                        DangerButton("Decline", modifier = Modifier.weight(1f), onClick = onDecline)
                    } else if (offer.outgoing) {
                        SecondaryButton("Cancel", modifier = Modifier.fillMaxWidth(), onClick = onCancel)
                    }
                }
            }
        }
    }
}

@Composable
private fun TradeSwapRow(
    leftLabel: String,
    leftItems: List<TradeArtistItemUi>,
    rightLabel: String,
    rightItems: List<TradeArtistItemUi>,
    onArtistSelected: (ArtistUi) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm), verticalAlignment = Alignment.CenterVertically) {
        TradeMiniArtistStack(leftLabel, leftItems, Modifier.weight(1f), onArtistSelected)
        Text("⇄", color = BreakoutPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        TradeMiniArtistStack(rightLabel, rightItems, Modifier.weight(1f), onArtistSelected)
    }
}

@Composable
private fun TradeMiniArtistStack(
    label: String,
    items: List<TradeArtistItemUi>,
    modifier: Modifier,
    onArtistSelected: (ArtistUi) -> Unit
) {
    Surface(
        modifier = modifier,
        color = BreakoutSurface.copy(alpha = 0.55f),
        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
        border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(BreakoutDimensions.sm),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
        ) {
            Text(
                label,
                color = if (label.startsWith("You")) WaiverAccent else BreakoutTextSecondary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (label.startsWith("You")) FontWeight.Black else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            items.take(3).forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(BreakoutDimensions.xs))
                        .clickable { onArtistSelected(item.artist) }
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArtistArtwork(artist = item.artist, size = 34.dp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.artist.displayName(), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(item.slot.label, color = BreakoutTextSecondary, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            if (items.size > 3) {
                Text("+${items.size - 3} more", color = BreakoutPrimary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun tradeValidation(
    offeredItems: List<TradeArtistItemUi>,
    requestedItems: List<TradeArtistItemUi>,
    myRoster: Map<RosterSlot, ArtistUi>,
    theirRoster: Map<RosterSlot, ArtistUi>,
    settings: LeagueSettingsUi
): String? {
    if (offeredItems.isEmpty()) return "Pick at least one artist from your roster."
    if (requestedItems.isEmpty()) return "Pick at least one artist from your trade partner's roster."
    val myFailures = simulateTradeAdds(
        baseRoster = myRoster.filterValues { rosterArtist ->
            offeredItems.none { it.artist.name.equals(rosterArtist.name, ignoreCase = true) }
        },
        incoming = requestedItems.map { it.artist },
        settings = settings
    )
    if (myFailures.isNotEmpty()) {
        return "You do not have space in your roster for the following artists after the trade: ${myFailures.joinToString()}."
    }
    val theirFailures = simulateTradeAdds(
        baseRoster = theirRoster.filterValues { rosterArtist ->
            requestedItems.none { it.artist.name.equals(rosterArtist.name, ignoreCase = true) }
        },
        incoming = offeredItems.map { it.artist },
        settings = settings
    )
    if (theirFailures.isNotEmpty()) {
        return "Your trade partner does not have space in their roster for the following artists after the trade: ${theirFailures.joinToString()}."
    }
    return null
}

private fun simulateTradeAdds(
    baseRoster: Map<RosterSlot, ArtistUi>,
    incoming: List<ArtistUi>,
    settings: LeagueSettingsUi
): List<String> {
    var simulated = baseRoster
    val failures = mutableListOf<String>()
    incoming.forEach { artist ->
        val slot = firstOpenSlotFor(artist, simulated, settings)
        if (slot == null) {
            failures += artist.name
        } else {
            simulated = simulated + (slot to artist)
        }
    }
    return failures
}

private fun tradeAccent(offer: TradeOfferUi) = when (offer.status) {
    "accepted" -> BreakoutPrimary
    "declined", "canceled", "expired" -> BreakoutCoral
    else -> if (offer.incoming) WaiverAccent else BreakoutPrimary
}

private fun Set<String>.toggle(value: String): Set<String> =
    if (value in this) this - value else this + value

private fun List<TradeArtistItemUi>.sameTradeArtists(other: List<TradeArtistItemUi>): Boolean =
    map { it.artist.name.artistKey() }.sorted() == other.map { it.artist.name.artistKey() }.sorted()
