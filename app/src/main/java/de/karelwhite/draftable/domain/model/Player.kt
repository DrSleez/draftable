package de.karelwhite.draftable.domain.model

import java.util.UUID

data class Player(
    val id: String = UUID.randomUUID().toString(),
    val tournamentId: String, // Foreign key linking to Tournament
    var name: String,
    var score: Int = 0,
    var matchesPlayed: Int = 0,
    var matchesWon: Int = 0,
    var matchesLost: Int = 0,
    var matchesDrawn: Int = 0,
    var isDropped: Boolean = false,
    var opponentHistory: List<String> = emptyList()
)