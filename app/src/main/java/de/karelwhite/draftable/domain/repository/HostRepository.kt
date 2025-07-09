package de.karelwhite.draftable.domain.repository

import de.karelwhite.draftable.domain.model.Host
import kotlinx.coroutines.flow.Flow

interface HostRepository {
    suspend fun updateHost(host: Host)
    suspend fun getHost() : Host?
    suspend fun createHost(host : Host)
}