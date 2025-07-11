package de.karelwhite.draftable.unittest

import de.karelwhite.draftable.domain.model.Match
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Model Match Class Tests")
internal class ModelMatchTest {

    private val defaultTournamentId = "tourney-123"
    private val defaultRoundNumber = 1
    private val defaultPlayer1Id = "playerA"
    private val defaultPlayer2Id = "playerB"

    @Test
    @DisplayName("Should create match with mandatory fields, generate ID, and set defaults for scores and status")
    fun `create match with mandatory fields generates id and sets defaults`() {
        val match = Match(
            tournamentId = defaultTournamentId,
            roundNumber = defaultRoundNumber,
            player1Id = defaultPlayer1Id,
            player2Id = defaultPlayer2Id
        )

        assertNotNull(match.id, "ID should be generated and not null")
        assertTrue(isValidUUID(match.id), "ID should be a valid UUID string")
        assertEquals(defaultTournamentId, match.tournamentId)
        assertEquals(defaultRoundNumber, match.roundNumber)
        assertEquals(defaultPlayer1Id, match.player1Id)
        assertEquals(defaultPlayer2Id, match.player2Id)

        // Default values for scores and status
        assertEquals(0, match.player1Wins, "Player 1 wins should be 0 by default")
        assertEquals(0, match.player2Wins, "Player 2 wins should be 0 by default")
        assertEquals(0, match.draws, "Draws should be 0 by default")
        assertFalse(match.isFinished, "isFinished should be false by default")
        assertNull(match.reportedByPlayerId, "reportedByPlayerId should be null by default")
    }

    @Test
    @DisplayName("Should allow providing a custom ID")
    fun `create match with custom id`() {
        val customId = "custom-match-id-xyz"
        val match = Match(
            id = customId,
            tournamentId = defaultTournamentId,
            roundNumber = defaultRoundNumber,
            player1Id = defaultPlayer1Id,
            player2Id = defaultPlayer2Id
        )
        assertEquals(customId, match.id, "Custom ID should be used")
    }

    @Test
    @DisplayName("Should allow null values for player IDs (e.g., bye)")
    fun `create match with null player ids`() {
        val matchWithOnePlayer = Match(
            tournamentId = defaultTournamentId,
            roundNumber = defaultRoundNumber,
            player1Id = defaultPlayer1Id,
            player2Id = null // Player 2 might be a bye
        )
        assertNull(matchWithOnePlayer.player2Id, "player2Id should allow null")

        val matchWithNoPlayers = Match( // Szenario für z.B. einen noch nicht zugewiesenen Slot
            tournamentId = defaultTournamentId,
            roundNumber = defaultRoundNumber,
            player1Id = null,
            player2Id = null
        )
        assertNull(matchWithNoPlayers.player1Id, "player1Id should allow null")
        assertNull(matchWithNoPlayers.player2Id, "player2Id should also allow null")
    }

    @Test
    @DisplayName("Should allow providing custom values for scores, status, and reporter")
    fun `create match with all custom values`() {
        val match = Match(
            id = "custom-full-id",
            tournamentId = "tourney-custom",
            roundNumber = 3,
            player1Id = "playerX",
            player2Id = "playerY",
            player1Wins = 2,
            player2Wins = 1,
            draws = 0,
            isFinished = true,
            reportedByPlayerId = "playerX"
        )

        assertEquals("custom-full-id", match.id)
        assertEquals("tourney-custom", match.tournamentId)
        assertEquals(3, match.roundNumber)
        assertEquals("playerX", match.player1Id)
        assertEquals("playerY", match.player2Id)
        assertEquals(2, match.player1Wins)
        assertEquals(1, match.player2Wins)
        assertEquals(0, match.draws)
        assertTrue(match.isFinished)
        assertEquals("playerX", match.reportedByPlayerId)
    }


    @Nested
    @DisplayName("Mutability Tests")
    inner class MutabilityTests {

        private fun createDefaultMatch() = Match(
            tournamentId = defaultTournamentId,
            roundNumber = defaultRoundNumber,
            player1Id = defaultPlayer1Id,
            player2Id = defaultPlayer2Id
        )

        @Test
        @DisplayName("Should allow changing 'player1Wins'")
        fun `allow changing player1Wins`() {
            val match = createDefaultMatch()
            match.player1Wins = 2
            assertEquals(2, match.player1Wins)
        }

        @Test
        @DisplayName("Should allow changing 'player2Wins'")
        fun `allow changing player2Wins`() {
            val match = createDefaultMatch()
            match.player2Wins = 1
            assertEquals(1, match.player2Wins)
        }

        @Test
        @DisplayName("Should allow changing 'draws'")
        fun `allow changing draws`() {
            val match = createDefaultMatch()
            match.draws = 1
            assertEquals(1, match.draws)
        }

        @Test
        @DisplayName("Should allow changing 'isFinished'")
        fun `allow changing isFinished`() {
            val match = createDefaultMatch()
            match.isFinished = true
            assertTrue(match.isFinished)
        }

        @Test
        @DisplayName("Should allow changing 'reportedByPlayerId'")
        fun `allow changing reportedByPlayerId`() {
            val match = createDefaultMatch()
            match.reportedByPlayerId = defaultPlayer1Id
            assertEquals(defaultPlayer1Id, match.reportedByPlayerId)

            match.reportedByPlayerId = null // Also allow setting back to null
            assertNull(match.reportedByPlayerId)
        }
    }


    @Test
    @DisplayName("Two instances with same data should be equal")
    fun `equality check for data class`() {
        // Properties im Primärkonstruktor: id, tournamentId, roundNumber, player1Id, player2Id
        // Die anderen (player1Wins, etc.) sind 'var' und nicht im Primärkonstruktor für equals/hashCode.
        // Um das zu testen, müssten sie val im Primärkonstruktor sein, oder wir testen die Gleichheit
        // von Datenklassen, die nach einer 'copy' mit diesen Werten erstellt wurden.
        // Für diesen Test konzentrieren wir uns auf die Primärkonstruktor-Eigenschaften.

        val id = "fixed-match-id"
        val match1 = Match(
            id = id,
            tournamentId = "tourney-eq-1",
            roundNumber = 2,
            player1Id = "pA",
            player2Id = "pB"
            // Scores etc. haben ihre Defaultwerte
        )

        val match2 = Match(
            id = id,
            tournamentId = "tourney-eq-1",
            roundNumber = 2,
            player1Id = "pA",
            player2Id = "pB"
        )

        val match3DifferentPlayer = Match(
            id = id,
            tournamentId = "tourney-eq-1",
            roundNumber = 2,
            player1Id = "pC", // Different
            player2Id = "pB"
        )

        assertEquals(match1, match2, "Two matches with the same primary constructor data should be equal")
        assertEquals(match1.hashCode(), match2.hashCode(), "HashCodes should be equal for equal objects")
        assertNotEquals(match1, match3DifferentPlayer, "Matches with different player1Id should not be equal")
    }

    @Test
    @DisplayName("Copy function should create new instance with optionally modified properties")
    fun `copy function test`() {
        val originalMatch = Match(
            tournamentId = "tourney-copy",
            roundNumber = 1,
            player1Id = "p1-copy",
            player2Id = "p2-copy",
            player1Wins = 1 // Setze einen nicht-default Wert für die Kopie
        )

        val copiedMatchSameData = originalMatch.copy()
        // Vergleicht basierend auf Primärkonstruktor-Feldern UND den anderen Feldern, wenn sie
        // beim Kopieren explizit übergeben werden oder gleich bleiben.
        // Wenn player1Wins nicht in copy() angegeben wird, wird der Wert vom Original übernommen.
        assertEquals(originalMatch, copiedMatchSameData, "Copied match with same data should be equal")
        assertEquals(1, copiedMatchSameData.player1Wins)
        assertNotSame(originalMatch, copiedMatchSameData, "Copied match should be a new instance")

        val copiedMatchModifiedRound = originalMatch.copy(roundNumber = 2)
        assertEquals(2, copiedMatchModifiedRound.roundNumber)
        assertEquals(originalMatch.player1Id, copiedMatchModifiedRound.player1Id)
        assertEquals(1, copiedMatchModifiedRound.player1Wins) // player1Wins bleibt vom Original erhalten

        val copiedMatchModifiedWins = originalMatch.copy(player1Wins = 2, isFinished = true)
        assertEquals(2, copiedMatchModifiedWins.player1Wins)
        assertTrue(copiedMatchModifiedWins.isFinished)
        assertEquals(originalMatch.player2Wins, copiedMatchModifiedWins.player2Wins) // player2Wins (default) bleibt
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
