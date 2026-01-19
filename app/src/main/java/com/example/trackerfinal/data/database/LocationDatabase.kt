package com.example.trackerfinal.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: LocationDatabase? = null

        fun getDatabase(context: Context): LocationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocationDatabase::class.java,
                    "location_database"
                )
                    .addCallback(LocationDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class LocationDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.locationDao())
                }
            }
        }

        suspend fun populateDatabase(locationDao: LocationDao) {
            // Pre-populate with test data
            val testLocations = listOf(
                LocationEntity(
                    userId = "test_user",
                    longitude = 16.356373567789245,
                    latitude = 48.18699396030242,
                    timestamp = System.currentTimeMillis() - 3600000
                ),
                LocationEntity(
                    userId = "test_user",
                    longitude = 16.357373567789245,
                    latitude = 48.18799396030242,
                    timestamp = System.currentTimeMillis() - 3000000
                ),
                LocationEntity(
                    userId = "test_user",
                    longitude = 16.358373567789245,
                    latitude = 48.18899396030242,
                    timestamp = System.currentTimeMillis() - 2400000
                ),
                LocationEntity(
                    userId = "test_user",
                    longitude = 16.359373567789245,
                    latitude = 48.18999396030242,
                    timestamp = System.currentTimeMillis() - 1800000
                ),
                LocationEntity(
                    userId = "test_user",
                    longitude = 16.360373567789245,
                    latitude = 48.19099396030242,
                    timestamp = System.currentTimeMillis() - 1200000
                )
            )
            locationDao.insertAll(testLocations)
        }
    }
}
