package de.karelwhite.draftable.domain.model

import java.util.UUID

data class Match(
    val id: String = UUID.randomUUID().toString(),
    val tournamentId: String,
    val roundNumber: Int,

    val player1Id: String?,
    val player2Id: String?,

    var player1Wins: Int = 0,
    var player2Wins: Int = 0,
    var draws: Int = 0,
    var isFinished: Boolean = false,
    var reportedByPlayerId: String? = null // Optional: ID of player who reported
)