package com.example.trackerfinal.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val longitude: Double,
    val latitude: Double,
    val timestamp: Long
)
