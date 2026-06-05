package com.example.rachelbello2.controller

import com.example.rachelbello2.model.Appointment
import com.example.rachelbello2.model.AppointmentDao
import com.example.rachelbello2.model.Availability
import com.example.rachelbello2.model.AvailabilityDao

class AppointmentController(
    private val appointmentDao: AppointmentDao,
    private val availabilityDao: AvailabilityDao
) {

    suspend fun getAppointments(): List<Appointment> {
        return appointmentDao.getAllAppointments()
    }

    suspend fun bookAppointment(appointment: Appointment, availability: Availability) {
        // Mark availability as no longer available
        val updatedAvailability = availability.copy(available = false)
        availabilityDao.updateAvailability(updatedAvailability)
        
        // Save appointment
        appointmentDao.insertAppointment(appointment)
    }

    suspend fun cancelAppointment(appointment: Appointment) {
        // Find the corresponding availability
        val availability = availabilityDao.getAvailabilityByDateTime(appointment.dateTime)
        
        if (availability != null) {
            // Mark availability as available again
            val updatedAvailability = availability.copy(available = true)
            availabilityDao.updateAvailability(updatedAvailability)
        }
        
        // Remove appointment
        appointmentDao.deleteAppointment(appointment)
    }
}
