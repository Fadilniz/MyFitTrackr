package week11.st078050.finalproject.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.components.GradientBackground
import week11.st078050.finalproject.ui.theme.*

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextLightGrey,
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onBackClick() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Logo
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Title
            Text(
                text = "Hello! Register to get\nstarted",
                color = TextWhite,
                textAlign = TextAlign.Center,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            // USERNAME
            TextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Username", color = TextGrey, fontSize = 15.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyInput,
                    unfocusedContainerColor = GreyInput,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = TextLightGrey
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // EMAIL
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", color = TextGrey, fontSize = 15.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyInput,
                    unfocusedContainerColor = GreyInput,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = TextLightGrey
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PASSWORD
            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = TextGrey, fontSize = 15.sp) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle Password",
                            tint = TextLightGrey
                        )
                    }
                },
                visualTransformation =
                    if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None
                    else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyInput,
                    unfocusedContainerColor = GreyInput,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = TextLightGrey
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CONFIRM PASSWORD
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Confirm password", color = TextGrey, fontSize = 15.sp) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle Confirm Password",
                            tint = TextLightGrey
                        )
                    }
                },
                visualTransformation =
                    if (confirmPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None
                    else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyInput,
                    unfocusedContainerColor = GreyInput,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = TextLightGrey
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // REGISTER BUTTON
            Button(
                onClick = onRegisterClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WhiteButton
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(
                    text = "Register",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login link
            Row {
                Text(
                    text = "Already have an account? ",
                    color = TextLightGrey
                )
                Text(
                    text = "Login Now",
                    color = YellowAccent,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}
