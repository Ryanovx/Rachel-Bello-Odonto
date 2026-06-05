package com.example.rachelbello2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "availabilities")
data class Availability(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: String,
    val available: Boolean = true
)
