package com.example.trackerfinal.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackerfinal.data.database.LocationDatabase
import com.example.trackerfinal.data.database.LocationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OsmVM(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext

    private val database = LocationDatabase.getDatabase(application)
    private val locationDao = database.locationDao()

    private val _pathLocations = MutableStateFlow<List<LocationEntity>>(emptyList())
    val pathLocations: StateFlow<List<LocationEntity>> = _pathLocations

    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation

    init {
        Log.d("OsmVM", "ViewModel initialized")
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        Log.d("OsmVM", "Updating location: lat=$latitude, lon=$longitude")
        _currentLocation.value = Pair(latitude, longitude)
    }

    fun loadPath() {
        viewModelScope.launch {
            Log.d("OsmVM", "Loading path from database")
            val locations = locationDao.getAllLocationsAsList()
            _pathLocations.value = locations
            Log.d("OsmVM", "Loaded ${locations.size} locations")
        }
    }

    fun insertTestData() {
        viewModelScope.launch {
            val testLocations = listOf(
                LocationEntity(
                    userId = "test_user",
                    longitude = 16.356373567789245,
                    latitude = 48.18699396030242,
                    timestamp = System.currentTimeMillis()
                ),
                LocationEntity(
                    userId = "test_user",
                    longitude = 16.35773567789245,
                    latitude = 48.18799396030242,
                    timestamp = System.currentTimeMillis() + 60000
                ),
                LocationEntity(
                    userId = "test_user",
                    longitude = 16.35837367789245,
                    latitude = 48.18899396030242,
                    timestamp = System.currentTimeMillis() + 120000
                )
            )
            locationDao.insertAll(testLocations)
            Toast.makeText(appContext, "Test data added! Go to Path screen to view.", Toast.LENGTH_LONG).show()
            Log.d("OsmVM", "Test data inserted: ${testLocations.size} locations")
        }
    }

    fun clearDatabase() {
        viewModelScope.launch {
            locationDao.deleteAll()
            _pathLocations.value = emptyList()
            Toast.makeText(appContext, "Database cleared!", Toast.LENGTH_SHORT).show()
            Log.d("OsmVM", "Database cleared")
        }
    }
}
