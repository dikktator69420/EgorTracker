package com.example.androidtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.androidtracker.screens.LocationListScreen
import com.example.androidtracker.screens.LocationScreen
import com.example.androidtracker.screens.MapScreen
import com.example.androidtracker.screens.PathScreen
import com.example.androidtracker.screens.TrackerScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedScreen by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = selectedScreen,
                onScreenSelected = { selectedScreen = it }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedScreen) {
                0 -> MapScreen()
                1 -> LocationScreen()
                2 -> TrackerScreen()
                3 -> LocationListScreen()
                4 -> PathScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedScreen: Int,
    onScreenSelected: (Int) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedScreen == 0,
            onClick = { onScreenSelected(0) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Map"
                )
            },
            label = { Text("Map") }
        )

        NavigationBarItem(
            selected = selectedScreen == 1,
            onClick = { onScreenSelected(1) },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location"
                )
            },
            label = { Text("Location") }
        )

        NavigationBarItem(
            selected = selectedScreen == 2,
            onClick = { onScreenSelected(2) },
            icon = {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Tracker"
                )
            },
            label = { Text("Tracker") }
        )

        NavigationBarItem(
            selected = selectedScreen == 3,
            onClick = { onScreenSelected(3) },
            icon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "List"
                )
            },
            label = { Text("List") }
        )

        NavigationBarItem(
            selected = selectedScreen == 4,
            onClick = { onScreenSelected(4) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = "Path"
                )
            },
            label = { Text("Path") }
        )
    }
}