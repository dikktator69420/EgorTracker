package com.example.androidtracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg locations: LocationEntity)

    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    fun getAllLocationsFlow(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getLocationsByUserId(userId: String): List<LocationEntity>

    @Query("SELECT * FROM locations ORDER BY timestamp DESC LIMIT :count")
    suspend fun getRecentLocations(count: Int): List<LocationEntity>

    @Query("SELECT COUNT(*) FROM locations")
    suspend fun getLocationCount(): Int

    @Delete
    suspend fun delete(location: LocationEntity)

    @Query("DELETE FROM locations")
    suspend fun deleteAll()

    @Query("DELETE FROM locations WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}