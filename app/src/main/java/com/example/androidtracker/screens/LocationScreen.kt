package com.example.androidtracker.screens

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtracker.utils.LocationManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen() {
    val context = LocalContext.current

    val locationManager = remember { LocationManager(context) }

    var currentLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            isLoading = true

            val location = locationManager.getCurrentLocation()

            currentLocation = location
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (currentLocation != null) {
                            "Location: ${currentLocation!!.first}, ${currentLocation!!.second}"
                        } else {
                            "Location Screen"
                        }
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                !locationPermissions.allPermissionsGranted -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Location permission is required",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(16.dp)
                        )

                        Button(
                            onClick = {
                                locationPermissions.launchMultiplePermissionRequest()
                            }
                        ) {
                            Text("Grant Location Permission")
                        }
                    }
                }


                isLoading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Getting your location...")
                    }
                }


                currentLocation != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Your Current Location:",
                            fontSize = 24.sp
                        )

                        Text(
                            text = "Latitude: ${currentLocation!!.first}",
                            fontSize = 20.sp
                        )

                        Text(
                            text = "Longitude: ${currentLocation!!.second}",
                            fontSize = 20.sp
                        )

                        Button(
                            onClick = {
                                isLoading = true
                                kotlinx.coroutines.CoroutineScope(
                                    kotlinx.coroutines.Dispatchers.Main
                                ).launch {
                                    val newLocation = locationManager.getCurrentLocation()
                                    currentLocation = newLocation
                                    isLoading = false
                                }
                            }
                        ) {
                            Text("Refresh Location")
                        }
                    }
                }


                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Unable to get location",
                            fontSize = 18.sp
                        )

                        Text(
                            text = "Make sure GPS is enabled on your device",
                            modifier = Modifier.padding(16.dp)
                        )

                        Button(
                            onClick = {
                                isLoading = true
                                kotlinx.coroutines.CoroutineScope(
                                    kotlinx.coroutines.Dispatchers.Main
                                ).launch {
                                    val newLocation = locationManager.getCurrentLocation()
                                    currentLocation = newLocation
                                    isLoading = false
                                }
                            }
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }
    }
}