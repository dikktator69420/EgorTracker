package com.example.androidtracker.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidtracker.viewmodels.OsmVM
import ovh.plrapps.mapcompose.ui.MapUI

@Composable
fun PathScreen(
    viewModel: OsmVM = viewModel()
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var pointCount by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            isLoading = true
            hasError = false
            viewModel.path(context)
            pointCount = viewModel.pathPoints.size
            isLoading = false
        } catch (e: Exception) {
            println("❌ Error loading path: ${e.message}")
            e.printStackTrace()
            hasError = true
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (pointCount > 0) {
                            "Path ($pointCount points)"
                        } else {
                            "Path"
                        }
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    LoadingState()
                }

                hasError -> {
                    ErrorState(
                        onRetry = {
                            isLoading = true
                            hasError = false
                            viewModel.path(context)
                        }
                    )
                }

                pointCount == 0 -> {
                    EmptyPathState()
                }

                else -> {
                    MapUI(
                        modifier = Modifier.fillMaxSize(),
                        state = viewModel.state
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(
                text = "Loading path...",
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun ErrorState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Error loading path")
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun EmptyPathState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "No saved locations yet",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Start tracking to save locations,\nthen come back to see your path!"
            )
        }
    }
}