package de.karelwhite.draftable.data.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [HostEntity::class, TournamentEntity::class, PlayerEntity::class, MatchEntity::class],
    version = 2, // Increment this on schema changes
    exportSchema = false // Set to true for production if you want to export schema for migrations
)
@TypeConverters(RoomConverters::class)
abstract class DraftableDatabase : RoomDatabase() {
    abstract fun hostDao(): HostDao

    abstract fun tournamentDao() : TournamentDao

    abstract fun playerDao() : PlayerDao

    abstract fun matchDao() : MatchDao

}