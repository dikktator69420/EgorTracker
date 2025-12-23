package com.example.androidtracker.screens

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.androidtracker.utils.LocationManager
import com.example.androidtracker.viewmodels.OsmVM
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay
import ovh.plrapps.mapcompose.ui.MapUI


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: OsmVM = OsmVM()
) {
    val context = LocalContext.current

    val locationManager = LocationManager(context)

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            delay(500)

            val location = locationManager.getCurrentLocation()

            if (location != null) {
                val (lat, lon) = location
                viewModel.updateLocation(lat, lon)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Map Screen") }
            )
        }
    ) { padding ->

        if (!locationPermissions.allPermissionsGranted) {
            PermissionRequestScreen(
                onRequestPermissions = {
                    locationPermissions.launchMultiplePermissionRequest()
                },
                modifier = Modifier.padding(padding)
            )
        } else {
            MapUI(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                state = viewModel.state
            )
        }
    }
}

@Composable
fun PermissionRequestScreen(
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Location permission is required to show your position on the map",
                modifier = Modifier.padding(16.dp)
            )

            Button(onClick = onRequestPermissions) {
                Text("Grant Location Permission")
            }
        }
    }
}