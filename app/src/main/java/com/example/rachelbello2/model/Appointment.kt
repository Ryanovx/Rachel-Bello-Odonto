package com.example.rachelbello2.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientName: String,
    val dentistName: String,
    val dateTime: String
)
