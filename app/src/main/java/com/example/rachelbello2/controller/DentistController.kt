package com.example.rachelbello2.controller

import com.example.rachelbello2.model.Availability
import com.example.rachelbello2.model.AvailabilityDao
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DentistController(private val availabilityDao: AvailabilityDao) {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    suspend fun getAvailabilities(): List<Availability> {
        return availabilityDao.getAllAvailable()
    }

    suspend fun addAvailability(availability: Availability): Boolean {
        val newStart = LocalDateTime.parse(availability.dateTime, formatter)
        val newEnd = newStart.plusHours(1)

        val existing = availabilityDao.getAllAvailable()
        val hasConflict = existing.any { 
            val existingStart = LocalDateTime.parse(it.dateTime, formatter)
            val existingEnd = existingStart.plusHours(1)
            
            // Overlap logic: (StartA < EndB) and (EndA > StartB)
            newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)
        }

        if (hasConflict) return false

        availabilityDao.insertAvailability(availability)
        return true
    }

    suspend fun updateAvailability(availability: Availability) {
        availabilityDao.updateAvailability(availability)
    }

    suspend fun deleteAvailability(availability: Availability) {
        availabilityDao.deleteAvailability(availability)
    }
}
