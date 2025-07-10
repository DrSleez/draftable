package de.karelwhite.draftable.domain.model

import de.karelwhite.draftable.domain.model.Player
import java.util.UUID

data class Tournament(
    var id: String = UUID.randomUUID().toString(), // Unique ID for the tournament
    val name: String,
    var hostPlayerId: String, // ID of the player who created it
    val players: List<Player> = emptyList(),
    val matches: List<Match> = emptyList(),
    var currentRound: Int = 0,
    var isStarted: Boolean = false,
    var isFinished: Boolean = false,
    val numberOfRounds: Int = 3,
    val pointsForWin: Int = 3,
    val pointsForDraw: Int = 1,
    val pointsForLoss: Int = 0
)