package de.karelwhite.draftable.data.repository.local

import de.karelwhite.draftable.data.repository.room.HostDao
import de.karelwhite.draftable.data.repository.room.toDomain
import de.karelwhite.draftable.data.repository.room.toEntity
import de.karelwhite.draftable.domain.model.Host as DomainHost
import de.karelwhite.draftable.data.repository.room.HostEntity as EntityHost
import de.karelwhite.draftable.domain.repository.HostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HostRepositoryRoomImp @Inject constructor(private val hostDao: HostDao) : HostRepository {

    override suspend fun updateHost(host: DomainHost){
        withContext(Dispatchers.IO) {
            hostDao.updateHost(host.toEntity())
        }
    }

    override suspend fun getHost(): DomainHost? {
        return withContext(Dispatchers.IO) {
            val entityHost = hostDao.getHost() // This now runs on a background thread
            entityHost?.toDomain()
        }
    }

    override suspend fun createHost(hostInput: DomainHost){
        return withContext(Dispatchers.IO) {
            val entityToInsert = hostInput.toEntity()
            hostDao.insertHost(entityToInsert)
            val newlyCreatedEntity : EntityHost = hostDao.getHostById(entityToInsert.id)
                ?: throw IllegalStateException("Host konnte nach dem Erstellen mit ID ${entityToInsert.id} nicht abgerufen werden.")
            newlyCreatedEntity.toDomain()
            }
        }
    }
