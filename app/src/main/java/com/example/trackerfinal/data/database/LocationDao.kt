package com.example.trackerfinal.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert
    suspend fun insert(location: LocationEntity)

    @Insert
    suspend fun insertAll(locations: List<LocationEntity>)

    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE userId = :userId ORDER BY timestamp ASC")
    fun getLocationsByUser(userId: String): Flow<List<LocationEntity>>

    @Query("DELETE FROM locations")
    suspend fun deleteAll()

    @Query("SELECT * FROM locations ORDER BY timestamp ASC")
    suspend fun getAllLocationsAsList(): List<LocationEntity>
}
