package com.example.androidtracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LocationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
                instance
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "location_database"
            ).build()
        }

        suspend fun prepopulateDatabase(context: Context) {
            val database = getInstance(context)
            val dao = database.locationDao()
            val count = dao.getLocationCount()

            if (count == 0) {
                populateWithTestData(dao)
            }
        }

        suspend fun clearAndRepopulate(context: Context) {
            val database = getInstance(context)
            val dao = database.locationDao()
            dao.deleteAll()
            populateWithTestData(dao)
            println("✅ Database cleared and repopulated")
        }

        private suspend fun populateWithTestData(dao: LocationDao) {
            val currentTime = System.currentTimeMillis()
            val twoHoursInMs = 2 * 60 * 60 * 1000L
            val baseLat = 48.856667
            val baseLon = 2.351667

            val testLocations = (0..9).map { index ->
                LocationEntity(
                    id = 0,
                    userId = "test_user",
                    latitude = baseLat + (index * 0.001),
                    longitude = baseLon + (index * 0.001),
                    timestamp = currentTime - twoHoursInMs + (index * (twoHoursInMs / 10))
                )
            }

            dao.insertAll(*testLocations.toTypedArray())
            println("✅ Database pre-populated with ${testLocations.size} test locations")
        }
    }
}