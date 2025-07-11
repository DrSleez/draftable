package de.karelwhite.draftable.data.repository.room

import de.karelwhite.draftable.domain.model.Host as DomainHost
import de.karelwhite.draftable.data.repository.room.HostEntity as EntityHost

fun DomainHost.toEntity(): EntityHost {
    return EntityHost(
        id = this.id,
        name = this.name
    )
}

fun EntityHost.toDomain(): DomainHost {
    return DomainHost(
        id = this.id,
        name = this.name
    )
}