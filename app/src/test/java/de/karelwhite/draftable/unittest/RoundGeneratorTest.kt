package de.karelwhite.draftable.unittest

import de.karelwhite.draftable.domain.businesslogic.RoundGenerator
import de.karelwhite.draftable.domain.model.Match
import de.karelwhite.draftable.domain.model.Player
import de.karelwhite.draftable.domain.model.Tournament
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID

class RoundGeneratorTest {

    private val roundGenerator = RoundGenerator
    private val defaultTournamentId = "test-tournament-fixed-id"
    private val defaultHostId = "test-host-fixed-id"

    private fun createPlayer(name: String, id: String = UUID.randomUUID().toString()): Player {
        return Player(id = id, name = name, tournamentId = defaultTournamentId)
    }

    private fun createTournament(
        players: List<Player>,
        id: String = defaultTournamentId,
        currentRound: Int = 0,
        matches: List<Match> = emptyList(),
        isStarted: Boolean = false,
        isFinished: Boolean = false,
        numberOfRounds: Int = 3,
        pointsForWin: Int = 3,
        pointsForDraw: Int = 1,
        pointsForLoss: Int = 0
    ): Tournament {
        return Tournament(
            id = id,
            name = "Test Tournament",
            hostPlayerId = defaultHostId,
            players = players,
            matches = matches,
            currentRound = currentRound,
            isStarted = isStarted,
            isFinished = isFinished,
            numberOfRounds = numberOfRounds,
            pointsForWin = pointsForWin,
            pointsForDraw = pointsForDraw,
            pointsForLoss = pointsForLoss
        )
    }

    // --- Korrigierte Testfälle ---

    @Test
    fun `generateNextRound creates pairings avoiding rematches if possible`() {
        val p1 = createPlayer("P1", "p1").copy(score = 3)
        val p2 = createPlayer("P2", "p2").copy(score = 0)
        val p3 = createPlayer("P3", "p3").copy(score = 3)
        val p4 = createPlayer("P4", "p4").copy(score = 0)
        val players = listOf(p1, p2, p3, p4)

        val r1Match1 = Match(
            id = "m1", // Eindeutige ID für das Match
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p1.id,
            player2Id = p2.id,
            player1Wins = 2, // p1 gewinnt
            player2Wins = 0,
            draws = 0,
            isFinished = true // Match ist beendet
        )
        val r1Match2 = Match(
            id = "m2",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p3.id,
            player2Id = p4.id,
            player1Wins = 2, // p3 gewinnt
            player2Wins = 0,
            draws = 0,
            isFinished = true
        )
        val initialMatches = mutableListOf(r1Match1, r1Match2)

        var tournament = createTournament(
            players,
            currentRound = 1,
            matches = initialMatches,
            isStarted = true,
            numberOfRounds = 2
        )

        tournament = roundGenerator.generateNextRound(tournament)

        Assertions.assertEquals(2, tournament.currentRound)
        Assertions.assertEquals(4, tournament.matches.size)

        val round2Matches = tournament.matches.filter { it.roundNumber == 2 }
        Assertions.assertEquals(2, round2Matches.size)

        val matchP1P3 = round2Matches.find { (it.player1Id == p1.id && it.player2Id == p3.id) || (it.player1Id == p3.id && it.player2Id == p1.id) }
        val matchP2P4 = round2Matches.find { (it.player1Id == p2.id && it.player2Id == p4.id) || (it.player1Id == p4.id && it.player2Id == p2.id) }

        Assertions.assertNotNull(matchP1P3, "Match zwischen P1 und P3 erwartet.")
        Assertions.assertNotNull(matchP2P4, "Match zwischen P2 und P4 erwartet.")
    }


    @Test
    fun `generateNextRound handles odd players with a bye for lowest scorer without bye`() {
        val p1 = createPlayer("P1", "p1").copy(score = 3)
        val p2 = createPlayer("P2", "p2").copy(score = 0) // Dieser Spieler sollte das Freilos bekommen
        val p3 = createPlayer("P3", "p3").copy(score = 3)
        val players = listOf(p1, p2, p3)

        // Annahme: p1 hat gegen "someOpponentId" gespielt und gewonnen, p3 gegen "anotherOpponentId" und gewonnen
        // p2 hatte noch kein Spiel oder Freilos
        val r1MatchP1 = Match(
            id = "r1mP1",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p1.id,
            player2Id = "someOpponentId", // Fiktiver Gegner
            player1Wins = 2,
            player2Wins = 0,
            draws = 0,
            isFinished = true
        )
        val r1MatchP3 = Match(
            id = "r1mP3",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p3.id,
            player2Id = "anotherOpponentId", // Fiktiver Gegner
            player1Wins = 2,
            player2Wins = 0,
            draws = 0,
            isFinished = true
        )
        // p2 hatte in Runde 1 implizit kein Match oder wird jetzt mit 0 Punkten betrachtet
        val initialMatches = mutableListOf(r1MatchP1, r1MatchP3)

        var tournament = createTournament(
            players,
            currentRound = 1,
            matches = initialMatches,
            isStarted = true,
            numberOfRounds = 2,
            pointsForWin = 3
        )

        tournament = roundGenerator.generateNextRound(tournament)
        Assertions.assertEquals(2, tournament.currentRound)
        // Erwartet: 1 reguläres Match (P1 vs P3) + 1 Freilos-Match (für P2) + 2 alte Matches = 4
        Assertions.assertEquals(
            initialMatches.size + 2,
            tournament.matches.size,
            "Sollte 1 neues reguläres Match und 1 Freilos zu den bestehenden hinzufügen."
        )

        val round2Matches = tournament.matches.filter { it.roundNumber == 2 }
        Assertions.assertEquals(2, round2Matches.size)

        val byeMatchR2 = round2Matches.find { it.player2Id == null }
        Assertions.assertNotNull(byeMatchR2, "Freilos-Match in Runde 2 erwartet.")

        val regularMatchR2 = round2Matches.find { it.player2Id != null }
        Assertions.assertNotNull(regularMatchR2, "Reguläres Match in Runde 2 erwartet.")

        // Nach aktueller Logik (P1(3), P3(3), P2(0)):
        // P1 wird mit P3 gepaart.
        // P2 bleibt übrig und bekommt das Freilos.
        Assertions.assertEquals(
            p2.id,
            byeMatchR2!!.player1Id,
            "P2 sollte das Freilos bekommen haben."
        )
        Assertions.assertTrue(byeMatchR2.isFinished, "Freilos sollte sofort beendet sein.")
        // In deiner Logik: player1Wins = tournament.pointsForWin für Freilos-Match
        Assertions.assertEquals(
            tournament.pointsForWin,
            byeMatchR2.player1Wins,
            "Freilos-Gewinner sollte 'pointsForWin' als player1Wins haben."
        )

        val byePlayerUpdated = tournament.players.find { it.id == p2.id }
        Assertions.assertNotNull(byePlayerUpdated)
        Assertions.assertEquals(
            0 + tournament.pointsForWin,
            byePlayerUpdated!!.score,
            "Score von P2 sollte um Punkte für Freilos erhöht werden."
        )
        Assertions.assertEquals(
            1,
            byePlayerUpdated.matchesWon,
            "P2 sollte 1 gewonnenes Match (Freilos) haben."
        )

        Assertions.assertTrue(
            (regularMatchR2!!.player1Id == p1.id && regularMatchR2.player2Id == p3.id) ||
                    (regularMatchR2.player1Id == p3.id && regularMatchR2.player2Id == p1.id),
            "P1 und P3 sollten gepaart werden."
        )
    }

    @Test
    fun `generateNextRound finishes tournament if max rounds reached`() {
        val players = listOf(createPlayer("P1"), createPlayer("P2"))
        val tournament = createTournament(
            players,
            currentRound = 2,
            numberOfRounds = 2,
            isStarted = true
        )
        val updatedTournament = roundGenerator.generateNextRound(tournament)
        Assertions.assertTrue(
            updatedTournament.isFinished,
            "Turnier sollte als beendet markiert sein."
        )
        Assertions.assertEquals(
            2,
            updatedTournament.currentRound,
            "Rundenzahl sollte nicht erhöht werden, wenn max Runden erreicht."
        )
    }

    @Test
    fun `generateNextRound does not generate if previous round matches not finished`() {
        val p1 = createPlayer("P1", "p1")
        val p2 = createPlayer("P2", "p2")
        val p3 = createPlayer("P3", "p3")
        val p4 = createPlayer("P4", "p4")
        val players = listOf(p1, p2, p3, p4)

        val r1Match1 = Match(
            id = "mUnfinished",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p1.id,
            player2Id = p2.id,
            isFinished = false // NICHT BEENDET
        )
        val r1Match2 = Match(
            id = "mFinished",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p3.id,
            player2Id = p4.id,
            player1Wins = 2,
            isFinished = true
        )
        val initialMatches = mutableListOf(r1Match1, r1Match2)

        val tournament = createTournament(
            players,
            currentRound = 1,
            matches = initialMatches,
            isStarted = true
        )
        val updatedTournament = roundGenerator.generateNextRound(tournament)

        // Vergleiche relevante Felder, nicht das ganze Objekt, wenn IDs sich ändern könnten
        Assertions.assertEquals(tournament.currentRound, updatedTournament.currentRound)
        Assertions.assertEquals(tournament.matches.size, updatedTournament.matches.size)
        Assertions.assertEquals(tournament.isFinished, updatedTournament.isFinished)
        // Oder wenn du sicher bist, dass keine IDs generiert werden bei Nicht-Aktion:
        // assertEquals(tournament, updatedTournament, "Turnier sollte unverändert bleiben...")
        Assertions.assertEquals(1, updatedTournament.currentRound)
        Assertions.assertFalse(updatedTournament.isFinished) // Beachte: org.junit.Assert.assertFalse ist JUnit 4, besser JUnit 5 Assertions.assertFalse
    }

    @Test
    fun `generateNextRound handles less than 2 active players`() {
        val p1 = createPlayer("P1", "p1").copy(isDropped = false)
        val p2 = createPlayer("P2", "p2").copy(isDropped = true) // Gedroppt
        val players = listOf(p1, p2)
        val tournament = createTournament(
            players,
            currentRound = 1,
            isStarted = true,
            numberOfRounds = 3
        )
        val updatedTournament = roundGenerator.generateNextRound(tournament)

        Assertions.assertEquals(2, updatedTournament.currentRound, "Runde sollte erhöht werden.")
        Assertions.assertTrue(
            updatedTournament.matches.filter { it.roundNumber == 2 }.isEmpty(),
            "Keine neuen Matches für Runde 2."
        )
        Assertions.assertTrue(
            updatedTournament.isFinished,
            "Turnier sollte als beendet markiert werden, da nicht genug Spieler."
        ) // JUnit 5
    }

    @Test
    fun `generateNextRound allows rematches if no other opponents available`() {
        val p1 = createPlayer("P1", "p1").copy(score = 3)
        val p2 = createPlayer("P2", "p2").copy(score = 0)
        val players = listOf(p1, p2)

        val r1Match = Match(
            id = "r1m-rematch",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p1.id,
            player2Id = p2.id,
            player1Wins = 2,
            isFinished = true
        )
        val initialMatches = mutableListOf(r1Match)

        var tournament = createTournament(
            players,
            currentRound = 1,
            matches = initialMatches,
            isStarted = true,
            numberOfRounds = 2
        )
        tournament = roundGenerator.generateNextRound(tournament)
        Assertions.assertEquals(2, tournament.currentRound)
        val round2Matches = tournament.matches.filter { it.roundNumber == 2 }
        Assertions.assertEquals(1, round2Matches.size, "Sollte ein Match für Runde 2 haben.")
        val r2Match = round2Matches.first()
        Assertions.assertTrue(
            (r2Match.player1Id == p1.id && r2Match.player2Id == p2.id) || (r2Match.player1Id == p2.id && r2Match.player2Id == p1.id),
            "P1 und P2 sollten erneut gepaart werden."
        )
    }

    @Test
    fun `hasPlayedAgainst returns true if players have matched as P1 vs P2`() {
        val p1 = createPlayer("P1", "p1")
        val p2 = createPlayer("P2", "p2")
        val p3 = createPlayer("P3", "p3")
        val p4 = createPlayer("P4", "p4")
        val players = listOf(p1.copy(score = 3), p2.copy(score = 0), p3.copy(score = 3), p4.copy(score = 0))

        val r1m1 = Match(
            id = "hp_r1m1",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p1.id,
            player2Id = p2.id,
            player1Wins = 2,
            isFinished = true
        ) // P1 vs P2
        val r1m2 = Match(
            id = "hp_r1m2",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p3.id,
            player2Id = p4.id,
            player1Wins = 2,
            isFinished = true
        ) // P3 vs P4

        var tournament = createTournament(players, currentRound = 1, matches = listOf(r1m1, r1m2), isStarted = true, numberOfRounds = 2)
        tournament = roundGenerator.generateNextRound(tournament)

        val r2matches = tournament.matches.filter { it.roundNumber == 2 }
        Assertions.assertFalse(
            r2matches.any { (it.player1Id == p1.id && it.player2Id == p2.id) || (it.player1Id == p2.id && it.player2Id == p1.id) },
            "P1 und P2 sollten in Runde 2 nicht erneut gegeneinander spielen."
        )
        Assertions.assertTrue(
            r2matches.any { (it.player1Id == p1.id && it.player2Id == p3.id) || (it.player1Id == p3.id && it.player2Id == p1.id) },
            "P1 und P3 sollten in Runde 2 gegeneinander spielen."
        )
    }

    @Test
    fun `hasPlayedAgainst returns true if players have matched as P2 vs P1`() {
        val p1 = createPlayer("P1", "p1")
        val p2 = createPlayer("P2", "p2")
        val p3 = createPlayer("P3", "p3")
        val p4 = createPlayer("P4", "p4")
        val players = listOf(p1.copy(score = 0), p2.copy(score = 3), p3.copy(score = 3), p4.copy(score = 0))

        val r1m1 = Match(
            id = "hp_r1m1_rev",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p2.id, // P2 als player1
            player2Id = p1.id,
            player1Wins = 2, // P2 gewinnt
            isFinished = true
        ) // P2 vs P1
        val r1m2 = Match(
            id = "hp_r1m2_rev",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p3.id,
            player2Id = p4.id,
            player1Wins = 2,
            isFinished = true
        ) // P3 vs P4

        var tournament = createTournament(players, currentRound = 1, matches = listOf(r1m1, r1m2), isStarted = true, numberOfRounds = 2)
        tournament = roundGenerator.generateNextRound(tournament)

        val r2matches = tournament.matches.filter { it.roundNumber == 2 }
        Assertions.assertFalse(
            r2matches.any { (it.player1Id == p1.id && it.player2Id == p2.id) || (it.player1Id == p2.id && it.player2Id == p1.id) },
            "P1 und P2 sollten in Runde 2 nicht erneut gegeneinander spielen."
        )
    }

    @Test
    fun `hasPlayedAgainst returns false if players have not matched`() {
        val p1 = createPlayer("P1", "p1")
        val p2 = createPlayer("P2", "p2")
        val p3 = createPlayer("P3", "p3")
        // p1 vs p3 hat gespielt
        val unrelatedMatch = Match(
            id = "hp_unrelated",
            tournamentId = defaultTournamentId,
            roundNumber = 1,
            player1Id = p1.id,
            player2Id = p3.id,
            player1Wins = 2,
            isFinished = true
        )
        val players = listOf(p1.copy(score = 3), p2.copy(score = 3), p3.copy(score = 0))

        var tournament = createTournament(players, currentRound = 1, matches = listOf(unrelatedMatch), isStarted = true, numberOfRounds = 2)
        tournament = roundGenerator.generateNextRound(tournament)

        val r2matches = tournament.matches.filter { it.roundNumber == 2 }
        // Wir wollen, dass P1 und P2 gepaart werden, da sie noch nicht gespielt haben
        Assertions.assertTrue(
            r2matches.any { (it.player1Id == p1.id && it.player2Id == p2.id) || (it.player1Id == p2.id && it.player2Id == p1.id) },
            "P1 und P2 sollten in Runde 2 gegeneinander spielen, da sie noch nicht gespielt haben."
        )
    }

    // ... weitere Tests für generateNextRound, Freilose, Unentschieden etc. ...
}