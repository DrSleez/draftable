package de.karelwhite.draftable.domain.businesslogic

import androidx.compose.ui.unit.round
import de.karelwhite.draftable.domain.model.Match
import de.karelwhite.draftable.domain.model.Player
import de.karelwhite.draftable.domain.model.Tournament
import java.util.UUID

object RoundGenerator {

    /**
     * Erstellt die Paarungen für die erste Runde eines Turniers nach dem Schweizer System.
     * In der ersten Runde werden Spieler in der Regel zufällig oder nach initialer Setzliste gepaart.
     * Diese einfache Implementierung sortiert nach Namen und paart dann benachbarte Spieler.
     * Eine fortgeschrittenere Implementierung könnte eine zufällige Durchmischung oder eine Setzliste verwenden.
     */
    fun generateFirstRound(tournament: Tournament): Tournament {
        if (tournament.isStarted) {
            // Sollte nicht passieren, wenn die Logik im ViewModel korrekt ist
            println("Warnung: Erste Runde wird für ein bereits gestartetes Turnier generiert.")
            // return tournament // Oder Fehler werfen/behandeln
        }

        val activePlayers = tournament.players.filterNot { it.isDropped }.toMutableList()
        if (activePlayers.size < 2) {
            // Nicht genug Spieler für Matches
            return tournament.copy(
                isStarted = true, // Turnier kann als gestartet markiert werden, auch wenn keine Matches generiert werden
                currentRound = 1, // Wir setzen die Runde auf 1, da der Startprozess abgeschlossen ist
                matches = emptyList()
            )
        }

        // Einfache Sortierung für die erste Runde (z.B. nach Namen, dann aufteilen)
        // Eine bessere erste Runde könnte eine zufällige Sortierung oder eine initiale Setzliste verwenden.
        activePlayers.sortBy { it.name } // Oder shuffle() für Zufall

        val newMatches = mutableListOf<Match>()
        val pairedPlayerIds = mutableSetOf<String>()
        var matchIdCounter = (tournament.matches?.maxOfOrNull { it.id.hashCode() } ?: 0) + 1 // Einfache ID-Generierung


        // Spieler in Paare aufteilen
        for (i in 0 until activePlayers.size / 2) {
            val player1 = activePlayers[i * 2]
            val player2 = activePlayers[i * 2 + 1]

            newMatches.add(
                Match(
                    tournamentId = tournament.id,
                    roundNumber = 1,
                    player1Id = player1.id,
                    player2Id = player2.id,
                    player1Wins = 0,
                    player2Wins = 0,
                    draws = 0,
                    isFinished = false,
                )
            )
            pairedPlayerIds.add(player1.id)
            pairedPlayerIds.add(player2.id)
        }

        // Umgang mit ungerader Spieleranzahl (Freilos / Bye)
        if (activePlayers.size % 2 != 0) {
            val lastPlayer = activePlayers.last() // Der letzte Spieler in der sortierten Liste bekommt das Freilos
            if (!pairedPlayerIds.contains(lastPlayer.id)) { // Sicherstellen, dass der Spieler nicht schon gepaart wurde (sollte der Fall sein)
                newMatches.add(
                    Match(
                        id = UUID.randomUUID().toString(),
                        tournamentId = tournament.id,
                        roundNumber = 1,
                        player1Id = lastPlayer.id,
                        player2Id = null,
                        player1Wins = 2,
                        player2Wins = 0,
                        draws = 0,
                        isFinished = true,
                    )
                )

                val updatedPlayers = tournament.players.map { player ->
                    if (player.id == lastPlayer.id) {
                        player.copy(
                            score = player.score + 3, // Punkte für Sieg bei Freilos
                            matchesWon = player.matchesWon + 1,
                        )
                    } else {
                        player
                    }
                }
                return tournament.copy(
                    isStarted = true,
                    currentRound = 1,
                    matches = (tournament.matches) + newMatches,
                    players = updatedPlayers
                )
            }
        }

        return tournament.copy(
            isStarted = true,
            currentRound = 1,
            matches = (tournament.matches ?: emptyList()) + newMatches
            // Spieler müssen hier noch nicht aktualisiert werden, da die Matches erst gespielt werden
        )
    }

    /**
     * Erstellt Paarungen für die nächste Runde basierend auf den aktuellen Punkteständen (Schweizer System).
     * Vereinfachte Implementierung: Sortiert Spieler nach Punkten, paart dann benachbarte Spieler.
     * TODO: Implementiere robustere Logik (Vermeidung von Wiederholungspaarungen, Umgang mit Freilosen, Tie-Breaking).
     */
    fun generateNextRound(tournament: Tournament): Tournament {
        if (!tournament.isStarted || tournament.isFinished) {
            println("Kann nächste Runde nicht generieren: Turnier nicht gestartet oder bereits beendet.")
            return tournament
        }
        if (tournament.currentRound >= tournament.numberOfRounds) {
            println("Maximale Rundenanzahl erreicht.")
            return tournament.copy(isFinished = true) // Turnier als beendet markieren
        }

        // Stelle sicher, dass alle Matches der vorherigen Runde beendet sind (außer Freilose)
        val previousRoundMatches = tournament.matches?.filter { it.roundNumber == tournament.currentRound }
        if (previousRoundMatches?.any { it.player2Id != null && !it.isFinished } == true) {
            println("Fehler: Nicht alle Matches der Runde ${tournament.currentRound} sind beendet.")
            // Hier könntest du einen Fehler im State des ViewModels setzen
            return tournament // Keine neue Runde generieren
        }


        val nextRoundNumber = tournament.currentRound + 1
        val activePlayers = tournament.players.filterNot { it.isDropped }.toMutableList()

        if (activePlayers.size < 2) {
            // Nicht genug Spieler für weitere Matches
            return tournament.copy(
                currentRound = nextRoundNumber, // Runde erhöhen, auch wenn keine Matches
                isFinished = true // Wahrscheinlich beendet, wenn nicht genug Spieler
            )
        }

        // Spieler nach Punkten sortieren (Hauptkriterium im Schweizer System)
        // TODO: Füge Tie-Breaker hinzu (z.B. Buchholz, Median-Buchholz, direkter Vergleich)
        activePlayers.sortByDescending { it.score }

        val newMatches = mutableListOf<Match>()
        val pairedPlayerIds = mutableSetOf<String>() // Um zu verfolgen, wer schon gepaart wurde in dieser Runde
        val playersToPair = activePlayers.toMutableList() // Kopie für die Paarung

        while (playersToPair.size >= 2) {
            val player1 = playersToPair.removeAt(0) // Nimm den Spieler mit den meisten Punkten

            // Finde einen passenden Gegner für player1
            // Kriterien:
            // 1. Noch nicht in dieser Runde gepaart
            // 2. Idealerweise noch nicht gegeneinander gespielt
            // 3. Idealerweise ähnliche Punktzahl
            var player2: Player? = null
            var bestOpponentIndex = -1

            for (i in playersToPair.indices) {
                val potentialOpponent = playersToPair[i]
                if (!hasPlayedAgainst(player1, potentialOpponent, tournament.matches ?: emptyList())) {
                    // Einfachster Fall: Nimm den ersten passenden Gegner
                    // Fortgeschritten: Suche den Gegner mit der ähnlichsten Punktzahl, der noch nicht gespielt hat
                    player2 = potentialOpponent
                    bestOpponentIndex = i
                    break
                }
            }

            // Falls kein idealer Gegner gefunden wurde (alle übrigen haben schon gegeneinander gespielt),
            // dann muss man Wiederholungspaarungen zulassen (oder komplexere Regeln anwenden).
            // Für diese vereinfachte Version nehmen wir den nächsten verfügbaren, wenn kein "neuer" Gegner da ist.
            if (player2 == null && playersToPair.isNotEmpty()) {
                player2 = playersToPair[0]
                bestOpponentIndex = 0
                println("Hinweis: Wiederholungspaarung für Spieler ${player1.name} mit ${player2.name} in Runde $nextRoundNumber.")
            }


            if (player2 != null && bestOpponentIndex != -1) {
                playersToPair.removeAt(bestOpponentIndex) // Entferne player2 aus der Liste der zu Paarenden
                newMatches.add(
                    Match(
                        tournamentId = tournament.id,
                        roundNumber = nextRoundNumber,
                        player1Id = player1.id,
                        player2Id = player2.id,
                        // Ergebnisse werden zurückgesetzt
                        player1Wins = 0,
                        player2Wins = 0,
                        draws = 0,
                        isFinished = false,
                    )
                )
                pairedPlayerIds.add(player1.id)
                pairedPlayerIds.add(player2.id)
            } else if (player2 == null && playersToPair.isEmpty() && !pairedPlayerIds.contains(player1.id)) {
                // player1 ist übrig und es gibt niemanden mehr zum Paaren -> Freilos
                // Dieser Fall sollte durch die Logik oben (playersToPair.size >= 2) selten auftreten,
                // aber als Absicherung für den letzten Spieler bei ungerader Anzahl.
                // Normalerweise wird der Bye zuerst vergeben (oft an den Spieler mit den wenigsten Punkten,
                // der noch kein Bye hatte).
            }
        }


        // Umgang mit ungerader Spieleranzahl (Freilos / Bye) für die nächste Runde
        // Der letzte verbleibende Spieler in `playersToPair` (wenn einer übrig ist) bekommt das Freilos.
        // Idealerweise sollte der Spieler mit den wenigsten Punkten, der noch kein Freilos hatte,
        // das Freilos bekommen. Diese Implementierung ist vereinfacht.
        var updatedPlayers = tournament.players // Start mit den aktuellen Spielern
        if (playersToPair.size == 1) {
            val byePlayer = playersToPair.removeAt(0)
            if (!pairedPlayerIds.contains(byePlayer.id)) { // Nur wenn nicht schon Teil eines regulären Matches
                newMatches.add(
                    Match(
                        tournamentId = tournament.id,
                        roundNumber = nextRoundNumber,
                        player1Id = byePlayer.id,
                        player2Id = null, // Kennzeichnet ein Freilos
                        player1Wins = tournament.pointsForWin,
                        player2Wins = tournament.pointsForLoss,
                        isFinished = true,
                        draws = 0,
                    )
                )
                // Spieler, der das Freilos bekommt, muss aktualisiert werden
                updatedPlayers = tournament.players.map { p ->
                    if (p.id == byePlayer.id) {
                        p.copy(
                            score = p.score + tournament.pointsForWin, // Punkte für Sieg bei Freilos
                            matchesWon = p.matchesWon + 1,
                            // opponentHistory für Freilos ggf. speziell behandeln
                        )
                    } else {
                        p
                    }
                }
                pairedPlayerIds.add(byePlayer.id) // Markiere als "gepaart" für diese Runde (mit Freilos)
                println("Spieler ${byePlayer.name} erhält ein Freilos in Runde $nextRoundNumber.")
            }
        }


        // Turnier als beendet markieren, wenn die maximale Rundenzahl erreicht ist
        val isNowFinished = nextRoundNumber >= tournament.numberOfRounds

        return tournament.copy(
            currentRound = nextRoundNumber,
            matches = (tournament.matches ?: emptyList()) + newMatches,
            players = updatedPlayers, // Aktualisiere Spieler falls ein Freilos vergeben wurde
            isFinished = isNowFinished
        )
    }

    /**
     * Hilfsfunktion, um zu prüfen, ob zwei Spieler bereits gegeneinander gespielt haben.
     */
    private fun hasPlayedAgainst(player1: Player, player2: Player, allMatches: List<Match>): Boolean {
        return allMatches.any { match ->
            (match.player1Id == player1.id && match.player2Id == player2.id) ||
                    (match.player1Id == player2.id && match.player2Id == player1.id)
        }
    }

    /**
     * Aktualisiert die Spielerpunkte und Statistiken basierend auf den Ergebnissen der abgeschlossenen Matches.
     * Diese Funktion sollte aufgerufen werden, NACHDEM ein Matchergebnis eingetragen wurde.
     * Für das Schweizer System ist es oft besser, die Punkte direkt nach jedem Match zu aktualisieren.
     * Diese Funktion hier könnte verwendet werden, um den gesamten Spielerpool zu synchronisieren, falls nötig.
     */
    fun updatePlayerStatsFromMatches(tournament: Tournament, updatedMatch: Match): Tournament {
        if (!updatedMatch.isFinished) return tournament

        val player1 = tournament.players.find { it.id == updatedMatch.player1Id }
        val player2 = tournament.players.find { it.id == updatedMatch.player2Id }

        if (player1 == null) return tournament // Sollte nicht passieren

        var p1Updated = player1
        var p2Updated = player2

        if (updatedMatch.player2Id == null) {
            // Freilos-Logik: Punkte wurden idealerweise schon bei der Rundengenerierung vergeben.
            // Falls nicht, könntest du es hier als Fallback behandeln, aber besser ist es,
            // dies konsistent bei der Freilos-Erstellung zu machen.
            // Für den Moment nehmen wir an, dass p1Updated bereits korrekt ist.
        } else if (player2 != null) { // Reguläres Match mit zwei Spielern
            val p1PointsEarned: Int
            val p2PointsEarned: Int

            // ANGEPASSTE when-Bedingung:
            when {
                updatedMatch.draws > 0 -> { // Wenn draws > 0, ist es ein Unentschieden
                    p1PointsEarned = tournament.pointsForDraw
                    p2PointsEarned = tournament.pointsForDraw
                }
                updatedMatch.player1Wins > updatedMatch.player2Wins -> { // Player 1 hat gewonnen
                    p1PointsEarned = tournament.pointsForWin
                    p2PointsEarned = tournament.pointsForLoss
                }
                updatedMatch.player2Wins > updatedMatch.player1Wins -> { // Player 2 hat gewonnen
                    p1PointsEarned = tournament.pointsForLoss
                    p2PointsEarned = tournament.pointsForWin
                }
                else -> {
                    // Dieser Fall sollte bei einem Bo3 mit klaren Sieg/Niederlage/Draw-Regeln
                    // (2-0, 2-1, 0-2, 1-2, 1-1) nicht eintreten, wenn die Eingabe korrekt ist.
                    // Man könnte hier einen Fehler loggen oder Standardpunkte (z.B. Verlust für beide) vergeben.
                    // Fürs Erste behandeln wir es als unwahrscheinlich oder als Fehler in den Matchdaten.
                    println("WARNUNG: Unerwarteter Matchausgang in updatePlayerStatsFromMatches: p1Wins=${updatedMatch.player1Wins}, p2Wins=${updatedMatch.player2Wins}, draws=${updatedMatch.draws}")
                    p1PointsEarned = tournament.pointsForLoss // Fallback
                    p2PointsEarned = tournament.pointsForLoss // Fallback
                }
            }

            p1Updated = player1.copy(
                score = player1.score + p1PointsEarned,
                matchesPlayed = player1.matchesPlayed + 1,
                matchesWon = player1.matchesWon + if (p1PointsEarned == tournament.pointsForWin) 1 else 0,
                matchesLost = player1.matchesLost + if (p1PointsEarned == tournament.pointsForLoss) 1 else 0,
                matchesDrawn = player1.matchesDrawn + if (p1PointsEarned == tournament.pointsForDraw && updatedMatch.draws > 0) 1 else 0, // Zähle Draw nur, wenn es wirklich einer war
                opponentHistory = player1.opponentHistory + player2.id
            )
            p2Updated = player2.copy(
                score = player2.score + p2PointsEarned,
                matchesPlayed = player2.matchesPlayed + 1,
                matchesWon = player2.matchesWon + if (p2PointsEarned == tournament.pointsForWin) 1 else 0,
                matchesLost = player2.matchesLost + if (p2PointsEarned == tournament.pointsForLoss) 1 else 0,
                matchesDrawn = player2.matchesDrawn + if (p2PointsEarned == tournament.pointsForDraw && updatedMatch.draws > 0) 1 else 0, // Zähle Draw nur, wenn es wirklich einer war
                opponentHistory = player2.opponentHistory + player1.id
            )
        }

        val finalPlayers = tournament.players.map { player ->
            when (player.id) {
                p1Updated.id -> p1Updated
                p2Updated?.id -> p2Updated // player2 kann null sein (Freilos)
                else -> player
            }
        }
        return tournament.copy(players = finalPlayers)
    }
}

