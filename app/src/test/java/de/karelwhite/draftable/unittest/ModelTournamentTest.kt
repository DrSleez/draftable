package de.karelwhite.draftable.unittest


import de.karelwhite.draftable.domain.model.Match
import de.karelwhite.draftable.domain.model.Player
import de.karelwhite.draftable.domain.model.Tournament
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

@DisplayName("Model Tournament Class Tests")
internal class ModelTournamentTest {

    private val defaultTournamentName = "Grand Championship"
    private val defaultHostId = "host-001"
    private val defaultTournamentId = "tourney-007" // Wird für einige Tests explizit gesetzt

    // Hilfsfunktionen zum Erstellen von Testdaten
    private fun createTestPlayer(id: String, name: String, tournamentId: String = defaultTournamentId): Player {
        return Player(id = id, tournamentId = tournamentId, name = name)
    }

    private fun createTestMatch(
        id: String,
        tournamentId: String = defaultTournamentId,
        round: Int,
        p1Id: String?,
        p2Id: String?
    ): Match {
        return Match(
            id = id,
            tournamentId = tournamentId,
            roundNumber = round,
            player1Id = p1Id,
            player2Id = p2Id
        )
    }

    @Test
    @DisplayName("Should create tournament with mandatory fields and generate ID")
    fun `create tournament with mandatory fields generates id`() {
        val tournament = Tournament(
            name = defaultTournamentName,
            hostPlayerId = defaultHostId
        )

        assertNotNull(tournament.id, "ID should be generated and not null")
        assertTrue(isValidUUID(tournament.id), "ID should be a valid UUID string: ${tournament.id}")
        assertEquals(defaultTournamentName, tournament.name, "Name should be set correctly")
        assertEquals(defaultHostId, tournament.hostPlayerId, "HostPlayerId should be set correctly")
    }

    @Test
    @DisplayName("Should create tournament with all default values for optional fields")
    fun `create tournament with default values for optional fields`() {
        val tournament = Tournament(
            name = defaultTournamentName,
            hostPlayerId = defaultHostId
        )

        assertTrue(tournament.players.isEmpty(), "Players list should be empty by default")
        assertTrue(tournament.matches.isEmpty(), "Matches list should be empty by default")
        assertEquals(0, tournament.currentRound, "Current round should be 0 by default")
        assertFalse(tournament.isStarted, "isStarted should be false by default")
        assertFalse(tournament.isFinished, "isFinished should be false by default")
        assertEquals(3, tournament.numberOfRounds, "Number of rounds should be 3 by default")
        assertEquals(3, tournament.pointsForWin, "Points for win should be 3 by default")
        assertEquals(1, tournament.pointsForDraw, "Points for draw should be 1 by default")
        assertEquals(0, tournament.pointsForLoss, "Points for loss should be 0 by default")
    }

    @Test
    @DisplayName("Should allow providing a custom ID")
    fun `create tournament with custom id`() {
        val customId = "custom-tournament-id-main"
        val tournament = Tournament(
            id = customId,
            name = defaultTournamentName,
            hostPlayerId = defaultHostId
        )
        assertEquals(customId, tournament.id, "Custom ID should be used")
    }

    @Test
    @DisplayName("Should allow providing custom values for all fields including Player and Match lists")
    fun `create tournament with all custom values and lists`() {
        val player1 = createTestPlayer("p1", "Alice")
        val player2 = createTestPlayer("p2", "Bob")
        val match1 = createTestMatch("m1", round = 1, p1Id = "p1", p2Id = "p2")

        val tournament = Tournament(
            id = "custom-id-all-fields-tournament",
            name = "Ultimate Showdown",
            hostPlayerId = "ultimateHost",
            players = listOf(player1, player2),
            matches = listOf(match1),
            currentRound = 1,
            isStarted = true,
            isFinished = false,
            numberOfRounds = 5,
            pointsForWin = 5,
            pointsForDraw = 2,
            pointsForLoss = -1
        )

        assertEquals("custom-id-all-fields-tournament", tournament.id)
        assertEquals("Ultimate Showdown", tournament.name)
        assertEquals("ultimateHost", tournament.hostPlayerId)

        assertEquals(2, tournament.players.size, "Should have 2 players")
        assertTrue(tournament.players.contains(player1), "Player list should contain player1")
        assertTrue(tournament.players.contains(player2), "Player list should contain player2")

        assertEquals(1, tournament.matches.size, "Should have 1 match")
        assertTrue(tournament.matches.contains(match1), "Match list should contain match1")

        assertEquals(1, tournament.currentRound)
        assertTrue(tournament.isStarted)
        assertFalse(tournament.isFinished)
        assertEquals(5, tournament.numberOfRounds)
        assertEquals(5, tournament.pointsForWin)
        assertEquals(2, tournament.pointsForDraw)
        assertEquals(-1, tournament.pointsForLoss)
    }

    @Nested
    @DisplayName("Mutability Tests")
    inner class MutabilityTests {
        private fun createDefaultTestTournament(): Tournament {
            return Tournament(name = defaultTournamentName, hostPlayerId = defaultHostId, id = defaultTournamentId)
        }

        @Test
        @DisplayName("Should allow changing mutable property 'id'")
        fun `allow changing id`() {
            val tournament = createDefaultTestTournament()
            val newId = "new-tournament-id-mut"
            tournament.id = newId
            assertEquals(newId, tournament.id, "ID should be updatable")
        }

        @Test
        @DisplayName("Should allow changing mutable property 'hostPlayerId'")
        fun `allow changing hostPlayerId`() {
            val tournament = createDefaultTestTournament()
            val newHostId = "newHost789"
            tournament.hostPlayerId = newHostId
            assertEquals(newHostId, tournament.hostPlayerId, "HostPlayerId should be updatable")
        }

        @Test
        @DisplayName("Should allow changing mutable property 'currentRound'")
        fun `allow changing currentRound`() {
            val tournament = createDefaultTestTournament()
            tournament.currentRound = 2
            assertEquals(2, tournament.currentRound, "Current round should be updatable")
        }

        @Test
        @DisplayName("Should allow changing mutable property 'isStarted'")
        fun `allow changing isStarted`() {
            val tournament = createDefaultTestTournament()
            tournament.isStarted = true
            assertTrue(tournament.isStarted, "isStarted should be updatable")
        }

        @Test
        @DisplayName("Should allow changing mutable property 'isFinished'")
        fun `allow changing isFinished`() {
            val tournament = createDefaultTestTournament()
            tournament.isFinished = true
            assertTrue(tournament.isFinished, "isFinished should be updatable")
        }

        // players und matches sind 'val' und können nicht als Ganzes neu zugewiesen werden.
        // Ihre Inhalte könnten sich ändern, wenn sie MutableListen wären, aber hier sind sie List (immutable by default).
    }

    @Test
    @DisplayName("Two instances with same primary constructor data should be equal")
    fun `equality check for data class`() {
        // Primärkonstruktor-Eigenschaften: id, name, hostPlayerId, players, matches,
        // numberOfRounds, pointsForWin, pointsForDraw, pointsForLoss.
        // currentRound, isStarted, isFinished sind 'var' außerhalb und beeinflussen equals/hashCode nicht.

        val playerA = createTestPlayer("pA", "Player A", tournamentId = "eq-tourney")
        val playerB = createTestPlayer("pB", "Player B", tournamentId = "eq-tourney")
        val matchAB = createTestMatch("mAB", tournamentId = "eq-tourney", round = 1, p1Id = "pA", p2Id = "pB")

        val tournament1 = Tournament(
            id = "eq-tourney",
            name = "Equality Tournament",
            hostPlayerId = "eq-host",
            players = listOf(playerA, playerB),
            matches = listOf(matchAB),
            numberOfRounds = 3,
            pointsForWin = 3,
            pointsForDraw = 1,
            pointsForLoss = 0
            // currentRound, isStarted, isFinished sind hier auf default
        )

        val tournament2 = Tournament(
            id = "eq-tourney",
            name = "Equality Tournament",
            hostPlayerId = "eq-host",
            players = listOf(playerA, playerB), // Wichtig: Gleiche Player-Instanzen oder zumindest Player, die equal sind
            matches = listOf(matchAB), // Wichtig: Gleiche Match-Instanzen oder Matches, die equal sind
            numberOfRounds = 3,
            pointsForWin = 3,
            pointsForDraw = 1,
            pointsForLoss = 0
        )

        val tournament3DifferentPlayers = Tournament(
            id = "eq-tourney",
            name = "Equality Tournament",
            hostPlayerId = "eq-host",
            players = listOf(playerA), // Unterschiedliche Spielerliste
            matches = listOf(matchAB),
            numberOfRounds = 3
            // ...
        )


        assertEquals(tournament1, tournament2, "Two tournaments with the same data should be equal")
        assertEquals(tournament1.hashCode(), tournament2.hashCode(), "HashCodes should be equal for equal objects")
        assertNotEquals(tournament1, tournament3DifferentPlayers, "Tournaments with different player lists should not be equal")


        // Test mit unterschiedlichen 'var' Feldern, die nicht Teil von equals/hashCode sind
        tournament2.currentRound = 1
        tournament2.isStarted = true
        // Obwohl currentRound und isStarted unterschiedlich sind, sollten tournament1 und tournament2
        // immer noch als gleich betrachtet werden, da diese Felder nicht im Primärkonstruktor sind.
        assertEquals(tournament1, tournament2, "Tournaments should still be equal even if non-primary constructor vars differ")
    }

    @Test
    @DisplayName("Copy function should create a new instance with optionally modified properties")
    fun `copy function test`() {
        val originalPlayer1 = createTestPlayer("pCopy1", "Player Copy 1", tournamentId = "copy-tourney")
        val originalPlayer2 = createTestPlayer("pCopy2", "Player Copy 2", tournamentId = "copy-tourney")
        val originalMatch1 = createTestMatch("mCopy1", tournamentId = "copy-tourney", 1, "pCopy1", "pCopy2")

        val originalTournament = Tournament(
            id = "copy-tourney",
            name = "Tournament To Copy",
            hostPlayerId = "copy-host",
            players = listOf(originalPlayer1),
            matches = listOf(originalMatch1),
            currentRound = 1,
            isStarted = true
        )

        val copiedTournamentSameData = originalTournament.copy()
        assertEquals(originalTournament, copiedTournamentSameData, "Copied tournament with same data should be equal")
        assertEquals(1, copiedTournamentSameData.currentRound, "var field 'currentRound' should be copied")
        assertTrue(copiedTournamentSameData.isStarted, "var field 'isStarted' should be copied")
        assertNotSame(originalTournament, copiedTournamentSameData, "Copied tournament should be a new instance")
        assertSame(originalTournament.players, copiedTournamentSameData.players, "Players list reference should be the same by default on copy")


        val newPlayerList = listOf(originalPlayer2)
        val copiedTournamentModified = originalTournament.copy(
            name = "Copied and Modified Name",
            players = newPlayerList,
            currentRound = 2 // Auch ein var-Feld ändern
        )
        assertEquals("Copied and Modified Name", copiedTournamentModified.name)
        assertEquals(newPlayerList, copiedTournamentModified.players, "Players list should be updated")
        assertNotSame(originalTournament.players, copiedTournamentModified.players, "Players list reference should be different")
        assertEquals(originalTournament.hostPlayerId, copiedTournamentModified.hostPlayerId) // Andere bleiben gleich
        assertEquals(originalTournament.matches, copiedTournamentModified.matches) // Matches bleiben gleich
        assertEquals(2, copiedTournamentModified.currentRound, "var field 'currentRound' should be updated in copy")
        assertTrue(copiedTournamentModified.isStarted, "var field 'isStarted' should be copied from original as it wasn't specified in copy")
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

