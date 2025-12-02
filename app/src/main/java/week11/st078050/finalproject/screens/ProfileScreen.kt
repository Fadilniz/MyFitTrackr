package week11.st078050.finalproject.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import week11.st078050.finalproject.data.repository.UserRepository
import week11.st078050.finalproject.ui.theme.TextGrey
import week11.st078050.finalproject.ui.theme.TextLightGrey
import week11.st078050.finalproject.ui.theme.TextWhite
import week11.st078050.finalproject.ui.theme.YellowAccent
import week11.st078050.finalproject.ui.theme.components.GradientBackground

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {}
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val repository = remember { UserRepository(auth, firestore) }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // 🔹 Load profile when screen opens and also
    LaunchedEffect(Unit) {
        try {
            val profile = repository.getCurrentUserProfile()
            if (profile != null) {
                username = profile.username
                email = profile.email
            } else {
                message = "No profile found."
            }
        } catch (e: Exception) {
            message = e.message ?: "Failed to load profile."
        } finally {
            isLoading = false
        }
    }

    GradientBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 🔹 Top bar with Back + title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextLightGrey,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onBackClick() }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Profile",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    // 🔹 Loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = YellowAccent)
                    }
                } else {
                    // 🔹 Profile form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {

                        // Username field
                        Text(
                            text = "Username",
                            color = TextGrey,
                            fontSize = 14.sp
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Email (read-only)
                        Text(
                            text = "Email",
                            color = TextGrey,
                            fontSize = 14.sp
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            singleLine = true,
                            enabled = false
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Info text
                        Text(
                            text = "You can update your username. Email is linked to your Firebase account.",
                            color = TextLightGrey,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🔹 Save button
                        Button(
                            onClick = {
                                message = null
                                if (username.isBlank()) {
                                    message = "Username cannot be empty."
                                } else {
                                    isSaving = true
                                    scope.launch {
                                        val success = repository.updateUsername(username.trim())
                                        isSaving = false
                                        message = if (success) {
                                            "Profile updated successfully."
                                        } else {
                                            "Failed to update profile."
                                        }
                                    }
                                }
                            },
                            enabled = !isSaving,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowAccent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp)
                        ) {
                            Text(
                                text = if (isSaving) "Saving..." else "Save Changes",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🔹 Message section (error/success)
                        message?.let { msg ->
                            Text(
                                text = msg,
                                color = if (msg.contains("success", ignoreCase = true)) Color(0xFF4CAF50) else Color.Red,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
