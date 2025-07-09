package de.karelwhite.draftable.data.repository.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface HostDao {

    @Upsert
    fun insertHost(hostEntity : HostEntity)

    @Update
    fun updateHost(hostEntity : HostEntity)

    @Query("SELECT * FROM host LIMIT 1")
    suspend fun getHost(): HostEntity?
    @Query("SELECT * FROM host WHERE id = :id")
    fun getHostById(id: String) : HostEntity?

}