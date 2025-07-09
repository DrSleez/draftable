package de.karelwhite.draftable.data.repository.room

import de.karelwhite.draftable.domain.model.Match // Annahme: Dein Domain-Modell
import de.karelwhite.draftable.domain.model.Player // Annahme: Dein Domain-Modell
import de.karelwhite.draftable.domain.model.Tournament // Annahme: Dein Domain-Modell

// --- Von Domain-Modell zu Entity ---

/**
 * Konvertiert ein Tournament Domain-Modell in eine TournamentEntity für die Datenbank.
 * Spieler und Matches werden hier NICHT direkt konvertiert, da sie normalerweise
 * separat in ihren eigenen Tabellen gespeichert werden und über IDs verknüpft sind.
 * Das Speichern der Spieler und Matches muss separat im Repository gehandhabt werden.
 */
fun Tournament.toEntity(): TournamentEntity {
    return TournamentEntity(
        id = this.id,
        name = this.name,
        hostPlayerId = this.hostPlayerId,
        numberOfRounds = this.numberOfRounds,
        isStarted = this.isStarted,
        isFinished = this.isFinished,
        currentRound = this.currentRound,
    )
}

/**
 * Konvertiert ein Player Domain-Modell in ein PlayerEntity.
 */
fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(
        id = this.id,
        tournamentId = this.tournamentId,
        name = this.name,
        score = this.score,
        matchesPlayed = this.matchesPlayed,
        matchesWon = this.matchesWon,
        matchesLost = this.matchesLost,
        matchesDrawn = this.matchesDrawn,
        isDropped = this.isDropped
    )
}

/**
 * Konvertiert ein Match Domain-Modell in ein MatchEntity.
 */
fun Match.toEntity(): MatchEntity {
    return MatchEntity(
        id = this.id,
        tournamentId = this.tournamentId,
        player1Id = this.player1Id,
        player2Id = this.player2Id,
        roundNumber = this.roundNumber,
        player1Wins = this.player1Wins,
        player2Wins = this.player2Wins,
        draws = this.draws,
        isFinished = this.isFinished,
        reportedByPlayerId = this.reportedByPlayerId
    )
}


// --- Von Entity zu Domain-Modell ---

/**
 * Konvertiert eine TournamentEntity aus der Datenbank in ein Tournament Domain-Modell.
 * Diese Funktion nimmt Listen von bereits konvertierten Player und Match Domain-Modellen entgegen,
 * da diese typischerweise durch separate Queries oder Relationen geladen und gemappt werden.
 */
fun TournamentEntity.toDomain(
    players: List<PlayerEntity>,
    matches: List<MatchEntity>
): Tournament {
    val domainPlayers : MutableList<Player> = players.map { it.toDomain() } as MutableList<Player>
    val domainMatches : MutableList<Match> = matches.map { it.toDomain() } as MutableList<Match>
    return Tournament(
        id = this.id,
        name = this.name,
        hostPlayerId = this.hostPlayerId,
        numberOfRounds = this.numberOfRounds,
        isStarted = this.isStarted,
        isFinished = this.isFinished,
        currentRound = this.currentRound,
        players = domainPlayers,
        matches = domainMatches
    )
}

/**
 * Konvertiert eine PlayerEntity in ein Player Domain-Modell.
 */
fun PlayerEntity.toDomain(): Player {
    return Player(
        id = this.id,
        tournamentId = this.tournamentId,
        name = this.name,
        score = this.score,
        matchesPlayed = this.matchesPlayed,
        matchesWon = this.matchesWon,
        matchesLost = this.matchesLost,
        matchesDrawn = this.matchesDrawn,
        isDropped = this.isDropped
    )
}

/**
 * Konvertiert eine MatchEntity in ein Match Domain-Modell.
 */
fun MatchEntity.toDomain(): Match {
    return Match(
        id = this.id,
        player1Id = this.player1Id,
        player2Id = this.player2Id,
        tournamentId = this.tournamentId,
        roundNumber = this.roundNumber,
        player1Wins = this.player1Wins,
        player2Wins = this.player2Wins,
        draws = this.draws,
        isFinished = this.isFinished,
        reportedByPlayerId = this.reportedByPlayerId
    )
}


