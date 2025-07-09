package de.karelwhite.draftable.domain.model

import de.karelwhite.draftable.domain.model.Player
import java.util.UUID

data class Tournament(
    var id: String = UUID.randomUUID().toString(), // Unique ID for the tournament
    val name: String,
    var hostPlayerId: String, // ID of the player who created it
    val players: List<Player> = emptyList(),
    var matches: MutableList<Match>? = mutableListOf(),
    var currentRound: Int = 0,
    var isStarted: Boolean = false,
    var isFinished: Boolean = false,
    val numberOfRounds: Int = 3 // Example, can be calculated based on players
)