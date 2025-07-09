package de.karelwhite.draftable.data.repository.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "host")
data class HostEntity(
    var name: String,
    @PrimaryKey
    var id : String = UUID.randomUUID().toString()
) {
}