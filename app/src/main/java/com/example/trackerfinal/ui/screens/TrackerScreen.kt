package com.example.trackerfinal.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trackerfinal.service.LocationService
import com.example.trackerfinal.viewmodel.OsmVM
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TrackerScreen(viewModel: OsmVM = viewModel()) {
    val context = LocalContext.current
    var isTracking by remember { mutableStateOf(false) }

    // Request foreground location and notification permissions first
    val foregroundPermissions = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val foregroundPermissionsState = rememberMultiplePermissionsState(foregroundPermissions)

    // Background location must be requested separately on Android 10+
    val backgroundPermissions = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    val backgroundPermissionsState = rememberMultiplePermissionsState(backgroundPermissions)

    val allPermissionsGranted = foregroundPermissionsState.allPermissionsGranted &&
        (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || backgroundPermissionsState.allPermissionsGranted)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Location Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    if (allPermissionsGranted) {
                        val serviceIntent = Intent(context, LocationService::class.java).apply {
                            action = LocationService.ACTION_START
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent)
                        } else {
                            context.startService(serviceIntent)
                        }
                        isTracking = true
                    }
                },
                enabled = !isTracking && allPermissionsGranted
            ) {
                Text("Start Tracking")
            }

            Button(
                onClick = {
                    val serviceIntent = Intent(context, LocationService::class.java).apply {
                        action = LocationService.ACTION_STOP
                    }
                    context.startService(serviceIntent)
                    isTracking = false
                },
                enabled = isTracking,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Stop Tracking")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (!foregroundPermissionsState.allPermissionsGranted) {
            Card(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Location permissions required",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { foregroundPermissionsState.launchMultiplePermissionRequest() }) {
                        Text("Grant Location Permissions")
                    }
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !backgroundPermissionsState.allPermissionsGranted) {
            Card(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Background location permission required for tracking",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { backgroundPermissionsState.launchMultiplePermissionRequest() }) {
                        Text("Grant Background Permission")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Database Management",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.insertTestData() }
            ) {
                Text("Add Test Data")
            }

            Button(
                onClick = { viewModel.clearDatabase() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Clear Database")
            }
        }

        if (isTracking) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tracking active...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
