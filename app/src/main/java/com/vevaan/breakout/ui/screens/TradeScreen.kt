package com.vevaan.breakout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    onSendTrade: (LeagueMemberUi, RosterSlot, ArtistUi, RosterSlot, ArtistUi) -> Unit,
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
    val selectedMember = tradeMembers.firstOrNull { it.username == selectedMemberName } ?: tradeMembers.firstOrNull()
    var offeredSlotName by rememberSaveable(league.id, roster.size) { mutableStateOf("") }
    var requestedSlotName by rememberSaveable(league.id, selectedMemberName) { mutableStateOf("") }
    val selectedOffer = roster.entries.firstOrNull { it.key.name == offeredSlotName } ?: roster.entries.firstOrNull()
    val memberRoster = selectedMember?.let { rosterForMemberName(it.username, draftPicks) }.orEmpty()
    val selectedRequest = memberRoster.entries.firstOrNull { it.key.name == requestedSlotName } ?: memberRoster.entries.firstOrNull()
    val validation = tradeValidation(selectedOffer, selectedRequest)

    ScreenColumn(
        refreshing = refreshing,
        onRefresh = onRefresh,
        stickyTopBar = { TopTitle(title = "Trades", subtitle = league.name, onMenuClick = onOpenMenu) }
    ) {
        BreakoutCard(
            contentPadding = PaddingValues(BreakoutDimensions.HeroCardPadding),
            border = BorderStroke(1.dp, BreakoutPrimary.copy(alpha = 0.38f))
        ) {
            Text("Trade Center", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text(
                "Offers expire after 48 hours and process instantly when accepted.",
                color = BreakoutTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.CardSpacing)) {
                StatTile("Pending", tradeOffers.count { it.status == "pending" }.toString(), "Open offers", Modifier.weight(1f))
                StatTile("Incoming", tradeOffers.count { it.status == "pending" && it.incoming }.toString(), "Need review", Modifier.weight(1f))
            }
        }

        if (league.draftStatus != DraftStatus.Complete) {
            StatusCard("Trades Locked", "Trades open after the draft is complete.")
            return@ScreenColumn
        }

        BreakoutCard {
            Text("Build Offer", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            if (tradeMembers.isEmpty()) {
                StatusCard("No Trade Partners", "Invite another member before sending trades.")
            } else {
                TradeMemberPicker(
                    members = tradeMembers,
                    selectedUsername = selectedMember?.username.orEmpty(),
                    onSelected = {
                        selectedMemberName = it.username
                        requestedSlotName = ""
                    }
                )
                TradeRosterPicker(
                    title = "You Give",
                    entries = roster.entries.toList(),
                    selectedSlot = selectedOffer?.key,
                    onSelected = { offeredSlotName = it.name },
                    onArtistSelected = onArtistSelected
                )
                TradeRosterPicker(
                    title = "You Get",
                    entries = memberRoster.entries.toList(),
                    selectedSlot = selectedRequest?.key,
                    onSelected = { requestedSlotName = it.name },
                    onArtistSelected = onArtistSelected
                )
                if (validation != null) {
                    StatusCard("Trade Check", validation)
                }
                PrimaryButton(
                    text = "Send Trade",
                    enabled = validation == null && selectedMember != null && selectedOffer != null && selectedRequest != null,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val member = selectedMember ?: return@PrimaryButton
                        val offer = selectedOffer ?: return@PrimaryButton
                        val request = selectedRequest ?: return@PrimaryButton
                        onSendTrade(member, offer.key, offer.value, request.key, request.value)
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
                        onAccept = { onAcceptTrade(offer) },
                        onDecline = { onDeclineTrade(offer) },
                        onCancel = { onCancelTrade(offer) },
                        onArtistSelected = onArtistSelected
                    )
                }
            }
        }
    }
}

@Composable
private fun TradeMemberPicker(
    members: List<LeagueMemberUi>,
    selectedUsername: String,
    onSelected: (LeagueMemberUi) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
        Text("Partner", color = BreakoutTextSecondary, style = MaterialTheme.typography.labelLarge)
        members.forEach { member ->
            val selected = member.username == selectedUsername
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
                    .clickable { onSelected(member) },
                color = if (selected) BreakoutPrimary.copy(alpha = 0.18f) else BreakoutSurfaceVariant.copy(alpha = 0.72f),
                shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
                border = BorderStroke(1.dp, if (selected) BreakoutPrimary.copy(alpha = 0.58f) else BreakoutOutline.copy(alpha = 0.35f))
            ) {
                Text(
                    member.username,
                    modifier = Modifier.padding(BreakoutDimensions.md),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TradeRosterPicker(
    title: String,
    entries: List<Map.Entry<RosterSlot, ArtistUi>>,
    selectedSlot: RosterSlot?,
    onSelected: (RosterSlot) -> Unit,
    onArtistSelected: (ArtistUi) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
        Text(title, color = BreakoutTextSecondary, style = MaterialTheme.typography.labelLarge)
        if (entries.isEmpty()) {
            StatusCard("No Artists", "This roster has no artists available.")
        } else {
            entries.forEach { (slot, artist) ->
                val selected = slot == selectedSlot
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(artist.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
            TradeSwapRow(
                leftLabel = "${offer.proposerUsername} gives",
                leftArtist = offer.offeredArtist,
                rightLabel = "${offer.recipientUsername} gives",
                rightArtist = offer.requestedArtist,
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
    leftArtist: ArtistUi,
    rightLabel: String,
    rightArtist: ArtistUi,
    onArtistSelected: (ArtistUi) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(BreakoutDimensions.sm)) {
        TradeMiniArtist(leftLabel, leftArtist, Modifier.weight(1f), onClick = { onArtistSelected(leftArtist) })
        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
            Text("⇄", color = BreakoutPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }
        TradeMiniArtist(rightLabel, rightArtist, Modifier.weight(1f), onClick = { onArtistSelected(rightArtist) })
    }
}

@Composable
private fun TradeMiniArtist(label: String, artist: ArtistUi, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(BreakoutDimensions.SmallCornerRadius))
            .clickable(onClick = onClick),
        color = BreakoutSurface.copy(alpha = 0.55f),
        shape = RoundedCornerShape(BreakoutDimensions.SmallCornerRadius),
        border = BorderStroke(1.dp, BreakoutOutline.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(BreakoutDimensions.sm),
            verticalArrangement = Arrangement.spacedBy(BreakoutDimensions.xs)
        ) {
            ArtistArtwork(artist = artist, size = 48.dp)
            Text(label, color = BreakoutTextSecondary, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(artist.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

private fun tradeValidation(
    selectedOffer: Map.Entry<RosterSlot, ArtistUi>?,
    selectedRequest: Map.Entry<RosterSlot, ArtistUi>?
): String? {
    val offer = selectedOffer ?: return "Pick one artist from your roster."
    val request = selectedRequest ?: return "Pick one artist from the other roster."
    return when {
        !offer.key.canHold(request.value) -> "${request.value.name} does not fit in your ${offer.key.label} slot."
        !request.key.canHold(offer.value) -> "${offer.value.name} does not fit in their ${request.key.label} slot."
        else -> null
    }
}

private fun tradeAccent(offer: TradeOfferUi) = when (offer.status) {
    "accepted" -> BreakoutPrimary
    "declined", "canceled", "expired" -> BreakoutCoral
    else -> if (offer.incoming) WaiverAccent else BreakoutPrimary
}
