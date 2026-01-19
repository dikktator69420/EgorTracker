package com.example.trackerfinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trackerfinal.viewmodel.OsmVM
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun PathScreen(viewModel: OsmVM = viewModel()) {
    val context = LocalContext.current
    val pathLocations by viewModel.pathLocations.collectAsState()

    // Configure osmdroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        viewModel.loadPath()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(14.0)

                    // Set initial position to Vienna
                    val vienna = GeoPoint(48.18699396030242, 16.356373567789245)
                    controller.setCenter(vienna)
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { mapView ->
                // Clear previous overlays
                mapView.overlays.clear()

                if (pathLocations.isNotEmpty()) {
                    // Create list of GeoPoints from locations
                    val geoPoints = pathLocations.map { location ->
                        GeoPoint(location.latitude, location.longitude)
                    }

                    // Draw polyline connecting all points
                    val polyline = Polyline(mapView)
                    polyline.setPoints(geoPoints)
                    polyline.color = android.graphics.Color.BLUE
                    polyline.width = 5f
                    mapView.overlays.add(polyline)

                    // Add markers for each point
                    geoPoints.forEachIndexed { index, geoPoint ->
                        val marker = Marker(mapView)
                        marker.position = geoPoint
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = "Point ${index + 1}"
                        mapView.overlays.add(marker)
                    }

                    // Center on first point
                    val firstPoint = geoPoints.first()
                    mapView.controller.setCenter(firstPoint)

                    mapView.invalidate()
                }
            }
        )

        // Info card
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            Text(
                text = if (pathLocations.isEmpty()) {
                    "No path data. Add test data in Tracker screen."
                } else {
                    "Showing ${pathLocations.size} points"
                },
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
