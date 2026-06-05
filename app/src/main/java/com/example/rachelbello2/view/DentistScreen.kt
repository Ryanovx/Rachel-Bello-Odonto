package com.example.rachelbello2.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rachelbello2.controller.AppointmentController
import com.example.rachelbello2.controller.DentistController
import com.example.rachelbello2.model.Appointment
import com.example.rachelbello2.model.Availability
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentistScreen(
    dentistController: DentistController,
    appointmentController: AppointmentController,
    onBack: () -> Unit
) {
    var availabilities by remember { mutableStateOf(emptyList<Availability>()) }
    var appointments by remember { mutableStateOf(emptyList<Appointment>()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Date/Time picker states
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    LaunchedEffect(Unit) {
        availabilities = dentistController.getAvailabilities()
        appointments = appointmentController.getAppointments()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Área do Dentista") },
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
            Text(text = "Adicionar Disponibilidade:", style = MaterialTheme.typography.titleMedium)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Selecionar Data")
                }
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Selecionar Hora")
                }
            }

            Button(
                onClick = {
                    if (selectedDate != null && selectedTime != null) {
                        val dateTimeStr = "${selectedDate} ${selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm"))}"
                        scope.launch {
                            val success = dentistController.addAvailability(Availability(dateTime = dateTimeStr))
                            if (success) {
                                availabilities = dentistController.getAvailabilities()
                                snackbarHostState.showSnackbar("Horário adicionado!")
                            } else {
                                snackbarHostState.showSnackbar("Erro: Conflito de horário (janela de 1h)!")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Confirmar Horário")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(text = "Agendamentos Marcados:", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(appointments) { appointment ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Paciente: ${appointment.patientName}", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Data/Hora: ${appointment.dateTime}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "Horários Livres:", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(availabilities) { availability ->
                    ListItem(
                        headlineContent = { Text(availability.dateTime) },
                        trailingContent = {
                            IconButton(onClick = {
                                scope.launch {
                                    dentistController.deleteAvailability(availability)
                                    availabilities = dentistController.getAvailabilities()
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remover")
                            }
                        }
                    )
                }
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        showDatePicker = false
                    }) { Text("Confirmar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    }) { Text("Confirmar") }
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }
    }
}
