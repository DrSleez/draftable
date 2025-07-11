package de.karelwhite.draftable.unittest


import de.karelwhite.draftable.domain.model.Player
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Model Player Class Tests")
internal class ModelPlayerTest {

    private val defaultTournamentId = "tourney-789"
    private val defaultPlayerName = "Alice"

    @Test
    @DisplayName("Should create player with mandatory fields, generate ID, and set defaults for stats")
    fun `create player with mandatory fields generates id and sets defaults`() {
        val player = Player(
            tournamentId = defaultTournamentId,
            name = defaultPlayerName
        )

        assertNotNull(player.id, "ID should be generated and not null")
        assertTrue(isValidUUID(player.id), "ID should be a valid UUID string")
        assertEquals(defaultTournamentId, player.tournamentId)
        assertEquals(defaultPlayerName, player.name)

        // Default values for stats and status
        assertEquals(0, player.score, "Score should be 0 by default")
        assertEquals(0, player.matchesPlayed, "Matches played should be 0 by default")
        assertEquals(0, player.matchesWon, "Matches won should be 0 by default")
        assertEquals(0, player.matchesLost, "Matches lost should be 0 by default")
        assertEquals(0, player.matchesDrawn, "Matches drawn should be 0 by default")
        assertFalse(player.isDropped, "isDropped should be false by default")
        assertTrue(player.opponentHistory.isEmpty(), "Opponent history should be empty by default")
    }

    @Test
    @DisplayName("Should allow providing a custom ID")
    fun `create player with custom id`() {
        val customId = "custom-player-id-abc"
        val player = Player(
            id = customId,
            tournamentId = defaultTournamentId,
            name = defaultPlayerName
        )
        assertEquals(customId, player.id, "Custom ID should be used")
    }

    @Test
    @DisplayName("Should allow providing custom values for all stats and status fields")
    fun `create player with all custom values`() {
        val opponentList = listOf("opponent1", "opponent2")
        val player = Player(
            id = "custom-full-player-id",
            tournamentId = "tourney-custom-player",
            name = "Bob The Builder",
            score = 10,
            matchesPlayed = 5,
            matchesWon = 3,
            matchesLost = 1,
            matchesDrawn = 1,
            isDropped = true,
            opponentHistory = opponentList
        )

        assertEquals("custom-full-player-id", player.id)
        assertEquals("tourney-custom-player", player.tournamentId)
        assertEquals("Bob The Builder", player.name)
        assertEquals(10, player.score)
        assertEquals(5, player.matchesPlayed)
        assertEquals(3, player.matchesWon)
        assertEquals(1, player.matchesLost)
        assertEquals(1, player.matchesDrawn)
        assertTrue(player.isDropped)
        assertEquals(opponentList, player.opponentHistory)
        assertEquals(2, player.opponentHistory.size)
    }

    @Nested
    @DisplayName("Mutability Tests")
    inner class MutabilityTests {

        private fun createDefaultPlayer() = Player(
            tournamentId = defaultTournamentId,
            name = defaultPlayerName
        )

        @Test
        @DisplayName("Should allow changing 'name'")
        fun `allow changing name`() {
            val player = createDefaultPlayer()
            val newName = "Alicia Keys"
            player.name = newName
            assertEquals(newName, player.name)
        }

        @Test
        @DisplayName("Should allow changing 'score'")
        fun `allow changing score`() {
            val player = createDefaultPlayer()
            player.score = 5
            assertEquals(5, player.score)
        }

        @Test
        @DisplayName("Should allow changing 'matchesPlayed'")
        fun `allow changing matchesPlayed`() {
            val player = createDefaultPlayer()
            player.matchesPlayed = 2
            assertEquals(2, player.matchesPlayed)
        }

        @Test
        @DisplayName("Should allow changing 'matchesWon'")
        fun `allow changing matchesWon`() {
            val player = createDefaultPlayer()
            player.matchesWon = 1
            assertEquals(1, player.matchesWon)
        }

        @Test
        @DisplayName("Should allow changing 'matchesLost'")
        fun `allow changing matchesLost`() {
            val player = createDefaultPlayer()
            player.matchesLost = 1
            assertEquals(1, player.matchesLost)
        }

        @Test
        @DisplayName("Should allow changing 'matchesDrawn'")
        fun `allow changing matchesDrawn`() {
            val player = createDefaultPlayer()
            player.matchesDrawn = 1
            assertEquals(1, player.matchesDrawn)
        }

        @Test
        @DisplayName("Should allow changing 'isDropped'")
        fun `allow changing isDropped`() {
            val player = createDefaultPlayer()
            player.isDropped = true
            assertTrue(player.isDropped)
        }

        @Test
        @DisplayName("Should allow changing 'opponentHistory'")
        fun `allow changing opponentHistory`() {
            val player = createDefaultPlayer()
            val newHistory = listOf("opponentA", "opponentB")
            player.opponentHistory = newHistory
            assertEquals(newHistory, player.opponentHistory)
            assertEquals(2, player.opponentHistory.size)

            player.opponentHistory = emptyList() // Also allow setting back to empty
            assertTrue(player.opponentHistory.isEmpty())
        }
    }

    @Test
    @DisplayName("Two instances with same primary constructor data should be equal")
    fun `equality check for data class`() {
        // Properties im Primärkonstruktor: id, tournamentId, name
        // Die anderen (score, etc.) sind 'var' und nicht im Primärkonstruktor für equals/hashCode.
        val id = "fixed-player-id"
        val player1 = Player(
            id = id,
            tournamentId = "tourney-eq-player",
            name = "Charlie"
        )

        val player2 = Player(
            id = id,
            tournamentId = "tourney-eq-player",
            name = "Charlie"
        )

        val player3DifferentName = Player(
            id = id,
            tournamentId = "tourney-eq-player",
            name = "Charles" // Different
        )

        assertEquals(player1, player2, "Two players with the same primary constructor data should be equal")
        assertEquals(player1.hashCode(), player2.hashCode(), "HashCodes should be equal for equal objects")
        assertNotEquals(player1, player3DifferentName, "Players with different names should not be equal")
    }

    @Test
    @DisplayName("Copy function should create new instance with optionally modified properties")
    fun `copy function test`() {
        val originalPlayer = Player(
            id = "original-p-id",
            tournamentId = "tourney-copy-player",
            name = "Dave",
            score = 3, // Setze einen nicht-default Wert
            opponentHistory = listOf("oppX")
        )

        val copiedPlayerSameData = originalPlayer.copy()
        assertEquals(originalPlayer, copiedPlayerSameData, "Copied player with same data should be equal")
        assertEquals(3, copiedPlayerSameData.score)
        assertEquals(listOf("oppX"), copiedPlayerSameData.opponentHistory)
        assertNotSame(originalPlayer, copiedPlayerSameData, "Copied player should be a new instance")

        val copiedPlayerModifiedName = originalPlayer.copy(name = "David")
        assertEquals("David", copiedPlayerModifiedName.name)
        assertEquals(originalPlayer.tournamentId, copiedPlayerModifiedName.tournamentId)
        assertEquals(3, copiedPlayerModifiedName.score) // Score bleibt vom Original erhalten

        val copiedPlayerModifiedScoreAndHistory = originalPlayer.copy(score = 6, opponentHistory = listOf("oppY", "oppZ"), isDropped = true)
        assertEquals(6, copiedPlayerModifiedScoreAndHistory.score)
        assertEquals(listOf("oppY", "oppZ"), copiedPlayerModifiedScoreAndHistory.opponentHistory)
        assertTrue(copiedPlayerModifiedScoreAndHistory.isDropped)
        assertEquals(originalPlayer.name, copiedPlayerModifiedScoreAndHistory.name)
    }

    // Hilfsfunktion zur UUID-Validierung
    private fun isValidUUID(uuidString: String): Boolean {
        return try {
            UUID.fromString(uuidString)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
