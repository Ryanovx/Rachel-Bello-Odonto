package com.example.rachelbello2.controller

import com.example.rachelbello2.model.User
import com.example.rachelbello2.model.UserDao

class LoginController(private val userDao: UserDao) {

    suspend fun login(email: String, password: String): User? {
        val user = userDao.getUserByEmail(email)
        return if (user != null && user.password == password) {
            user
        } else {
            null
        }
    }

    suspend fun register(name: String, email: String, password: String, role: String = "Paciente"): Boolean {
        // Check if user already exists
        if (userDao.getUserByEmail(email) != null) return false

        val newUser = User(name = name, email = email, password = password, role = role)
        userDao.insertUser(newUser)
        return true
    }
    
    suspend fun createInitialAdmin() {
        if (userDao.getUserByEmail("admin@dental.com") == null) {
            val admin = User(name = "Administrador", email = "admin@dental.com", password = "admin", role = "Admin")
            userDao.insertUser(admin)
        }
    }
}
