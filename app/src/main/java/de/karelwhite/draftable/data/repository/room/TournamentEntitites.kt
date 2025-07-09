package de.karelwhite.draftable.data.repository.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "my_tournaments")
data class TournamentEntity(
    var numberOfRounds: Int,
    var isFinished: Boolean,
    var isStarted: Boolean,
    var currentRound: Int,
    val hostPlayerId: String,
    var name: String,
    @PrimaryKey
    var id : String = UUID.randomUUID().toString()
)

@Entity(
    tableName = "my_players",
    foreignKeys = [
        ForeignKey(
            entity = TournamentEntity::class,
            parentColumns = ["id"],
            childColumns = ["tournament_id"],
            onDelete = ForeignKey.CASCADE)
                  ],
    indices = [Index(value = ["tournament_id"])]
)
data class PlayerEntity(
    var name: String,
    var score: Int = 0,
    var matchesPlayed: Int = 0,
    var matchesWon: Int = 0,
    var matchesLost: Int = 0,
    var matchesDrawn: Int = 0,
    var isDropped: Boolean = false,
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "tournament_id") val tournamentId: String,
)

@Entity(
    tableName = "my_matches",
    foreignKeys = [
        ForeignKey(entity = TournamentEntity::class, parentColumns = ["id"], childColumns = ["tournament_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = PlayerEntity::class, parentColumns = ["id"], childColumns = ["player1_id"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = PlayerEntity::class, parentColumns = ["id"], childColumns = ["player2_id"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index(value = ["tournament_id"]), Index(value = ["player1_id"]), Index(value = ["player2_id"])]
)
data class MatchEntity(
    var player1Wins: Int = 0,
    var player2Wins: Int = 0,
    var draws: Int = 0,
    var isFinished: Boolean = false,
    var reportedByPlayerId: String? = null, // Optional: ID of player who reported
    var roundNumber: Int,
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "tournament_id") val tournamentId: String,
    @ColumnInfo(name = "player1_id") val player1Id: String?,
    @ColumnInfo(name = "player2_id") val player2Id: String?
)