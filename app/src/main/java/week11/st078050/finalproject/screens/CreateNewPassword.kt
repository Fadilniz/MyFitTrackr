package week11.st078050.finalproject.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
fun CreateNewPasswordScreen(
    onBackClick: () -> Unit = {},
    onResetClick: () -> Unit = {}
) {
    var pass1 by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    var pass1Visible by remember { mutableStateOf(false) }
    var pass2Visible by remember { mutableStateOf(false) }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextLightGrey,
                modifier = Modifier
                    .align(Alignment.Start)
                    .size(30.dp)
                    .clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Create new\npassword",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your new password must be unique from those previously used.",
                color = TextGrey,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // New Password
            TextField(
                value = pass1,
                onValueChange = { pass1 = it },
                placeholder = { Text("New Password", color = TextGrey) },
                trailingIcon = {
                    IconButton(onClick = { pass1Visible = !pass1Visible }) {
                        Icon(
                            imageVector = if (pass1Visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = TextLightGrey
                        )
                    }
                },
                visualTransformation =
                    if (pass1Visible) androidx.compose.ui.text.input.VisualTransformation.None
                    else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyInput,
                    unfocusedContainerColor = GreyInput,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            TextField(
                value = pass2,
                onValueChange = { pass2 = it },
                placeholder = { Text("Confirm Password", color = TextGrey) },
                trailingIcon = {
                    IconButton(onClick = { pass2Visible = !pass2Visible }) {
                        Icon(
                            imageVector = if (pass2Visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = null,
                            tint = TextLightGrey
                        )
                    }
                },
                visualTransformation =
                    if (pass2Visible) androidx.compose.ui.text.input.VisualTransformation.None
                    else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GreyInput,
                    unfocusedContainerColor = GreyInput,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onResetClick,
                colors = ButtonDefaults.buttonColors(containerColor = WhiteButton),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Reset Password", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
