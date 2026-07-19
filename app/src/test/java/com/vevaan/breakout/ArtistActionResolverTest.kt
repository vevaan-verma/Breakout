package com.vevaan.breakout

import org.junit.Assert.assertEquals
import org.junit.Test

class ArtistActionResolverTest {
    private val settings = LeagueSettingsUi(
        headlinerSlots = 1,
        risingSlots = 1,
        wildcardSlots = 1,
        deepCutSlots = 1,
        benchSlots = 1,
        maxWaiverClaims = 2
    )

    @Test
    fun completeDraftFreeAgentQueuesWaiverInsteadOfDirectAdd() {
        val artist = artist("Synthetic Rising", 2_000_000)
        val action = resolveArtistAction(
            artist = artist,
            roster = mapOf(RosterSlot.HeadlinerOne to artist("Synthetic Headliner", 80_000_000)),
            draftPicks = emptyList(),
            waiverQueuedNames = emptySet(),
            leagueSettings = settings,
            draftStatus = DraftStatus.Complete,
            draftPickMode = false,
            canMakeDraftPick = false
        )

        assertEquals(ArtistActionKind.QueueWaiver, action.kind)
    }

    @Test
    fun fullWaiverQueueStillResolvesToWaiverSurfaceNotPlusAdd() {
        val artist = artist("Synthetic Deep Cut", 250_000)
        val action = resolveArtistAction(
            artist = artist,
            roster = emptyMap(),
            draftPicks = emptyList(),
            waiverQueuedNames = setOf("claim one", "claim two"),
            leagueSettings = settings,
            draftStatus = DraftStatus.Complete,
            draftPickMode = false,
            canMakeDraftPick = false
        )

        assertEquals(ArtistActionKind.WaiverQueueFull, action.kind)
    }

    @Test
    fun queuedArtistResolvesToCancelWaiverEverywhere() {
        val artist = artist("Queued Synthetic", 500_000)
        val action = resolveArtistAction(
            artist = artist,
            roster = emptyMap(),
            draftPicks = emptyList(),
            waiverQueuedNames = setOf("queued synthetic"),
            leagueSettings = settings,
            draftStatus = DraftStatus.Complete,
            draftPickMode = false,
            canMakeDraftPick = false
        )

        assertEquals(ArtistActionKind.CancelWaiver, action.kind)
    }

    @Test
    fun liveDraftPickResolvesToDraftOnlyWhenPickIsAvailable() {
        val artist = artist("Draft Synthetic", 60_000_000)
        val action = resolveArtistAction(
            artist = artist,
            roster = emptyMap(),
            draftPicks = emptyList(),
            waiverQueuedNames = emptySet(),
            leagueSettings = settings,
            draftStatus = DraftStatus.Live,
            draftPickMode = true,
            canMakeDraftPick = true
        )

        assertEquals(ArtistActionKind.Draft, action.kind)
    }

    private fun artist(name: String, listeners: Long): ArtistUi = ArtistUi(
        id = null,
        name = name,
        listeners = listeners,
        albumCount = null,
        imageUrl = null,
        source = "test",
        scoreStatus = "test"
    )
}
