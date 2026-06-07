package com.example.rachelbello2.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.rachelbello2.controller.LoginController
import com.example.rachelbello2.model.User
import com.example.rachelbello2.model.UserDao
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    userDao: UserDao,
    loginController: LoginController,
    onBack: () -> Unit
) {
    var dentists by remember { mutableStateOf(emptyList<User>()) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        dentists = userDao.getUsersByRole("Dentista")
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Painel Administrativo") }, actions = {
                TextButton(onClick = onBack) { Text("Sair") }
            })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Cadastrar Novo Dentista", style = MaterialTheme.typography.titleMedium)
            
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome do Dentista") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        scope.launch {
                            val success = loginController.register(name, email, password, "Dentista")
                            if (success) {
                                name = ""; email = ""; password = ""
                                dentists = userDao.getUsersByRole("Dentista")
                                snackbarHostState.showSnackbar("Dentista cadastrado!")
                            } else {
                                snackbarHostState.showSnackbar("Erro: E-mail já existe.")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Text("Cadastrar Dentista")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Dentistas Cadastrados", style = MaterialTheme.typography.titleMedium)
            
            LazyColumn {
                items(dentists) { dentist ->
                    ListItem(
                        headlineContent = { Text(dentist.name) },
                        supportingContent = { Text(dentist.email) },
                        leadingContent = { Icon(Icons.Default.Person, contentDescription = null) },
                        trailingContent = {
                            IconButton(onClick = {
                                scope.launch {
                                    userDao.deleteUser(dentist)
                                    dentists = userDao.getUsersByRole("Dentista")
                                    snackbarHostState.showSnackbar("Dentista removido.")
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                }
            }
        }
    }
}
