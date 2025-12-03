package week11.st078050.finalproject.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    val storage = remember { FirebaseStorage.getInstance() }
    val repository = remember { UserRepository(auth, firestore) }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Editable fields
    var heightText by remember { mutableStateOf("") }   // cm
    var weightText by remember { mutableStateOf("") }   // kg
    var stepGoalText by remember { mutableStateOf("") } // steps

    // Profile photo
    var photoUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Image picker (gallery)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            scope.launch {
                isUploadingPhoto = true
                message = null
                try {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        val ref = storage.reference
                            .child("profile_pictures")
                            .child("$uid.jpg")

                        ref.putFile(uri).await()
                        val downloadUrl = ref.downloadUrl.await().toString()

                        val success = repository.updatePhotoUrl(downloadUrl)
                        if (success) {
                            photoUrl = downloadUrl
                            message = "Profile photo updated."
                        } else {
                            message = "Failed to save photo URL."
                        }
                    } else {
                        message = "User not logged in."
                    }
                } catch (e: Exception) {
                    message = e.message ?: "Failed to upload photo."
                } finally {
                    isUploadingPhoto = false
                }
            }
        }
    }

    // Load profile on screen start
    LaunchedEffect(Unit) {
        try {
            val profile = repository.getCurrentUserProfile()
            if (profile != null) {
                username = profile.username
                email = profile.email

                if (profile.heightCm > 0) heightText = profile.heightCm.toString()
                if (profile.weightKg > 0) weightText = profile.weightKg.toString()
                if (profile.stepGoal > 0) stepGoalText = profile.stepGoal.toString()
                photoUrl = profile.photoUrl
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
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // 👈 NOW SCROLLABLE
            ) {

                // Top Bar
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

                // Loading Indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = YellowAccent)
                    }
                } else {
                    // Profile Form
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // 🔹 Profile picture circle
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (!isUploadingPhoto) {
                                        imagePickerLauncher.launch("image/*")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (photoUrl.isNotEmpty() || selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri ?: photoUrl,
                                    contentDescription = "Profile picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = "Add\nPhoto",
                                    color = TextLightGrey,
                                    textAlign = TextAlign.Center,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isUploadingPhoto) {
                            Text(
                                text = "Uploading photo...",
                                color = YellowAccent,
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = "Tap to change photo",
                                color = TextLightGrey,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // ========== FORM FIELDS ==========

                        // Username
                        Text(text = "Username", color = TextGrey, fontSize = 14.sp)
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
                        Text(text = "Email", color = TextGrey, fontSize = 14.sp)
                        OutlinedTextField(
                            value = email,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            singleLine = true,
                            enabled = false
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Height
                        Text(text = "Height (cm)", color = TextGrey, fontSize = 14.sp)
                        OutlinedTextField(
                            value = heightText,
                            onValueChange = { heightText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Weight
                        Text(text = "Weight (kg)", color = TextGrey, fontSize = 14.sp)
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { weightText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Step Goal
                        Text(text = "Daily Step Goal", color = TextGrey, fontSize = 14.sp)
                        OutlinedTextField(
                            value = stepGoalText,
                            onValueChange = { stepGoalText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Update your profile details. Height, weight and daily step goal help personalize your fitness stats.",
                            color = TextLightGrey,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🔹 Save + Reset buttons row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Reset button
                            OutlinedButton(
                                onClick = {
                                    message = null
                                    isSaving = true
                                    scope.launch {
                                        val success = repository.resetProfileData()
                                        isSaving = false
                                        if (success) {
                                            heightText = ""
                                            weightText = ""
                                            stepGoalText = ""
                                            photoUrl = ""
                                            selectedImageUri = null
                                            message = "Profile reset to defaults."
                                        } else {
                                            message = "Failed to reset profile."
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Reset")
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Save button
                            Button(
                                onClick = {
                                    message = null

                                    if (username.isBlank()) {
                                        message = "Username cannot be empty."
                                    } else {
                                        val height = heightText.toIntOrNull() ?: 0
                                        val weight = weightText.toIntOrNull() ?: 0
                                        val stepGoal = stepGoalText.toIntOrNull() ?: 0

                                        isSaving = true
                                        scope.launch {
                                            val success = repository.updateProfile(
                                                username = username.trim(),
                                                heightCm = height,
                                                weightKg = weight,
                                                stepGoal = stepGoal
                                            )
                                            isSaving = false
                                            message =
                                                if (success) "Profile updated successfully."
                                                else "Failed to update profile."
                                        }
                                    }
                                },
                                enabled = !isSaving && !isUploadingPhoto,
                                colors = ButtonDefaults.buttonColors(containerColor = YellowAccent),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = if (isSaving) "Saving..." else "Save Changes",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Message
                        message?.let { msg ->
                            Text(
                                text = msg,
                                color = if (msg.contains("success", ignoreCase = true))
                                    Color(0xFF4CAF50) else Color.Red,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}