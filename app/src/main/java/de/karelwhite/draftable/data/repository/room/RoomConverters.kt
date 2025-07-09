package de.karelwhite.draftable.data.repository.room

import androidx.room.TypeConverter
import de.karelwhite.draftable.domain.model.Host as DomainHost

class RoomConverters {

    @TypeConverter
    fun toDomainHost(hostEntity: HostEntity): DomainHost {
        return DomainHost(hostEntity.name, hostEntity.id)
    }
}