package week11.st078050.finalproject.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
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
fun ForgotPasswordScreen(
    onBackClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }

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

            Spacer(modifier = Modifier.height(30.dp))

            // Title
            Text(
                text = "Forgot Password?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Don’t worry! It occurs. Please enter the email\naddress linked with your account.",
                color = TextGrey,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email Input
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", color = TextGrey) },
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

            // SEND CODE button
            Button(
                onClick = onSendClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WhiteButton
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Send Code", color = Color.Black, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Login link
            Row {
                Text("Remember Password? ", color = TextLightGrey)
                Text(
                    "Login",
                    color = YellowAccent,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}
