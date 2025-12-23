package com.example.androidtracker.viewmodels

import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidtracker.database.AppDatabase
import kotlinx.coroutines.launch
import ovh.plrapps.mapcompose.api.addLayer
import ovh.plrapps.mapcompose.api.addMarker
import ovh.plrapps.mapcompose.api.enableRotation
import ovh.plrapps.mapcompose.api.scrollTo
import ovh.plrapps.mapcompose.core.TileStreamProvider
import ovh.plrapps.mapcompose.ui.layout.Forced
import ovh.plrapps.mapcompose.ui.state.MapState
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

class OsmVM : ViewModel() {

    private val maxLevel = 16
    private val minLevel = 12
    private val mapSize = mapSizeAtLevel(maxLevel, tileSize = 256)

    private var currentLat = 48.856667
    private var currentLon = 2.351667

    var currentLocation by mutableStateOf(latLonToNormalized(currentLat, currentLon))
        private set

    val pathPoints = mutableStateListOf<Pair<Double, Double>>()

    val state = MapState(
        levelCount = maxLevel + 1,
        fullWidth = mapSize,
        fullHeight = mapSize,
        workerCount = 16
    ) {
        minimumScaleMode(Forced(1 / 2.0.pow(maxLevel - minLevel)))
        scroll(currentLocation.first, currentLocation.second)
        scale(0.0)
        enableRotation()
    }.apply {
        addLayer(makeTileStreamProvider())

        addMarker(
            id = "currentLocation",
            x = currentLocation.first,
            y = currentLocation.second
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Current Location",
                modifier = Modifier.size(50.dp),
                tint = Color(0xCC2196F3)
            )
        }
    }

    fun updateLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val newLocation = latLonToNormalized(latitude, longitude)
            currentLocation = newLocation
            currentLat = latitude
            currentLon = longitude

            val x = newLocation.first
            val y = newLocation.second

            state.removeMarker("currentLocation")

            state.addMarker("currentLocation", x, y) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Current Location",
                    modifier = Modifier.size(50.dp),
                    tint = Color(0xCC2196F3)
                )
            }

            state.scrollTo(x, y)
        }
    }

    fun path(context: Context) {
        viewModelScope.launch {
            try {
                val database = AppDatabase.getInstance(context)
                val dao = database.locationDao()
                val locations = dao.getAllLocations()

                println("📍 Loading path with ${locations.size} locations")

                if (locations.isEmpty()) {
                    println("⚠️ No locations in database")
                    return@launch
                }

                pathPoints.clear()

                locations.indices.forEach { index ->
                    state.removeMarker("path_$index")
                }

                val points = locations.map { location ->
                    latLonToNormalized(location.latitude, location.longitude)
                }

                pathPoints.addAll(points)

                points.forEachIndexed { index, point ->
                    state.addMarker("path_$index", point.first, point.second) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Path point $index",
                            modifier = Modifier.size(30.dp),
                            tint = Color(0xCCF44336)
                        )
                    }
                }

                scrollToPath(points)

                println("✅ Path loaded with ${points.size} points")

            } catch (e: Exception) {
                println("❌ Error loading path: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private suspend fun scrollToPath(points: List<Pair<Double, Double>>) {
        if (points.isEmpty()) return

        val centerX = points.map { it.first }.average()
        val centerY = points.map { it.second }.average()

        state.scrollTo(centerX, centerY)

        println("📍 Scrolled to path center: ($centerX, $centerY)")
    }
}

private fun makeTileStreamProvider() =
    TileStreamProvider { row, col, zoomLvl ->
        try {
            val url = URL("https://tile.openstreetmap.org/$zoomLvl/$col/$row.png")
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", "AndroidTracker/1.0")
            connection.doInput = true
            connection.connect()
            BufferedInputStream(connection.inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

private fun mapSizeAtLevel(wmtsLevel: Int, tileSize: Int): Int {
    return tileSize * 2.0.pow(wmtsLevel).toInt()
}

private fun latLonToNormalized(lat: Double, lon: Double): Pair<Double, Double> {
    val x = (lon + 180.0) / 360.0
    val latRad = lat * PI / 180.0
    val mercatorY = ln(tan(PI / 4.0 + latRad / 2.0))
    val y = (1.0 - mercatorY / PI) / 2.0
    return Pair(x, y)
}