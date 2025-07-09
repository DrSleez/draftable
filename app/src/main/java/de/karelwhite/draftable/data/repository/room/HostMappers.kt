package de.karelwhite.draftable.data.repository.room

import de.karelwhite.draftable.domain.model.Host as DomainHost
import de.karelwhite.draftable.data.repository.room.HostEntity as EntityHost

// Konvertiert DomainHost zu EntityHost
fun DomainHost.toEntity(): EntityHost {
    return EntityHost(
        id = this.id,
        name = this.name
    )
}

// Konvertiert EntityHost zu DomainHost
fun EntityHost.toDomain(): DomainHost {
    return DomainHost(
        id = this.id,
        name = this.name
    )
}