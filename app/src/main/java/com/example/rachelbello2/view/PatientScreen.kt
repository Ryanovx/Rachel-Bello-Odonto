package com.example.rachelbello2.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rachelbello2.controller.AppointmentController
import com.example.rachelbello2.controller.DentistController
import com.example.rachelbello2.model.Appointment
import com.example.rachelbello2.model.Availability
import com.example.rachelbello2.model.User
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientScreen(
    currentUser: User,
    dentistController: DentistController,
    appointmentController: AppointmentController,
    onBack: () -> Unit
) {
    var allAvailabilities by remember { mutableStateOf(emptyList<Availability>()) }
    var myAppointments by remember { mutableStateOf(emptyList<Appointment>()) }
    val scope = rememberCoroutineScope()
    
    val datePickerState = rememberDatePickerState()
    val selectedDateString = remember(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
        }
    }

    // Modal state for DatePicker to avoid layout breaking
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        allAvailabilities = dentistController.getAvailabilities()
        myAppointments = appointmentController.getAppointments().filter { it.patientName == currentUser.name }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Olá, ${currentUser.name}") },
                actions = {
                    TextButton(onClick = onBack) {
                        Text("Sair")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Selecione o dia para ver horários:", style = MaterialTheme.typography.titleMedium)
            
            // Replaced inline DatePicker with a Button + Modal Dialog
            // Inline DatePicker in Material 3 is very large and often breaks layout on smaller screens
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text(selectedDateString ?: "Selecionar Data no Calendário")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Horários Disponíveis:",
                style = MaterialTheme.typography.titleMedium
            )
            
            val filteredAvailabilities = remember(selectedDateString, allAvailabilities) {
                if (selectedDateString == null) emptyList()
                else allAvailabilities.filter { it.dateTime.startsWith(selectedDateString) }
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (selectedDateString == null) {
                    item {
                        Text("Por favor, selecione uma data acima.", modifier = Modifier.padding(16.dp))
                    }
                } else if (filteredAvailabilities.isEmpty()) {
                    item {
                        Text("Nenhum horário disponível para este dia.", modifier = Modifier.padding(16.dp))
                    }
                }
                
                items(filteredAvailabilities) { availability ->
                    ListItem(
                        headlineContent = { Text(availability.dateTime.split(" ")[1]) },
                        trailingContent = {
                            Button(onClick = {
                                scope.launch {
                                    val appointment = Appointment(
                                        patientName = currentUser.name,
                                        dentistName = "Dra. Rachel",
                                        dateTime = availability.dateTime
                                    )
                                    appointmentController.bookAppointment(appointment, availability)
                                    allAvailabilities = dentistController.getAvailabilities()
                                    myAppointments = appointmentController.getAppointments().filter { it.patientName == currentUser.name }
                                }
                            }) {
                                Text("Agendar")
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Meus Agendamentos:", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(myAppointments) { appointment ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = appointment.dateTime, style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Dentista: ${appointment.dentistName}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("OK")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
