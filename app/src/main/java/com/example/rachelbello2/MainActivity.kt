package com.example.rachelbello2

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rachelbello2.controller.AppointmentController
import com.example.rachelbello2.controller.DentistController
import com.example.rachelbello2.controller.LoginController
import com.example.rachelbello2.model.AppDatabase
import com.example.rachelbello2.ui.theme.RachelBelloTheme
import com.example.rachelbello2.view.DentistScreen
import com.example.rachelbello2.view.LoginScreen
import com.example.rachelbello2.view.PatientScreen
import com.example.rachelbello2.view.RegistrationScreen

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)
        val loginController = LoginController(db.userDao())
        val dentistController = DentistController(db.availabilityDao())
        val appointmentController = AppointmentController(db.appointmentDao(), db.availabilityDao())

        setContent {
            RachelBelloTheme {
                AppNavigation(loginController, dentistController, appointmentController)
            }
        }
    }
}

@Composable
fun AppNavigation(
    loginController: LoginController,
    dentistController: DentistController,
    appointmentController: AppointmentController
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                loginController = loginController,
                onLoginSuccess = { user ->
                    if (user.role == "Dentista") {
                        navController.navigate("dentist")
                    } else {
                        navController.navigate("patient/${user.name}")
                    }
                },
                onNavigateToRegistration = {
                    navController.navigate("registration")
                }
            )
        }
        composable("registration") {
            RegistrationScreen(
                loginController = loginController,
                onRegistrationSuccess = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable("dentist") {
            DentistScreen(
                dentistController = dentistController,
                appointmentController = appointmentController
            ) {
                navController.popBackStack()
            }
        }
        composable("patient/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: ""
            // Mocking a User object for the PatientScreen since it needs the full object
            val user = remember { com.example.rachelbello2.model.User(name = userName, role = "Paciente", email = "", password = "") }
            
            PatientScreen(
                currentUser = user,
                dentistController = dentistController,
                appointmentController = appointmentController
            ) {
                navController.popBackStack()
            }
        }
    }
}
