package uk.ac.tees.mad.instantcontacts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import uk.ac.tees.mad.instantcontacts.Screen
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(navController: NavHostController, authViewModel: AuthViewModel = hiltViewModel()) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    val registerState by authViewModel.registerState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Create Your Account",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Join us and stay connected!",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email
                    ),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    shape = MaterialTheme.shapes.medium,
                    visualTransformation = PasswordVisualTransformation(),
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        authViewModel.register(name.text, email.text, password.text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (registerState is Resource.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    }
                    if (registerState is Resource.Idle) {
                        Text("Register", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Already have an account? Log In",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { navController.navigate(Screen.Login.route) }
                        .padding(vertical = 16.dp)
                )
            }
            when (registerState) {


                is Resource.Success -> {
                    Text("Registration Successful!", color = Color.Green)
                    navController.navigate(Screen.Home.route)
                }

                is Resource.Error -> {
                    Text(
                        "Registration Failed: ${(registerState as Resource.Error).exception.message}",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }

                else -> {}
            }
            Spacer(modifier = Modifier.height(8.dp))


        }
    }
}
