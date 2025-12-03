package week11.st078050.finalproject.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
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

    var heightText by remember { mutableStateOf("") }
    var weightText by remember { mutableStateOf("") }
    var stepGoalText by remember { mutableStateOf("") }

    var photoUrl by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var isUploadingPhoto by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

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

                        // 1) Create a ref – use a unique name to avoid caching issues
                        val ref = storage.reference
                            .child("profile_pictures")
                            .child("${uid}_${System.currentTimeMillis()}.jpg")

                        // 2) Upload file
                        val uploadResult = ref.putFile(uri).await()

                        // 3) Get download URL from the *same* storage ref
                        val downloadUrl = uploadResult.storage.downloadUrl.await().toString()

                        // 4) Save URL into Firestore via repository
                        val success = repository.updatePhotoUrl(downloadUrl)
                        if (success) {
                            photoUrl = downloadUrl
                            selectedImageUri = null
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
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Profile",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextLightGrey
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YellowAccent)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // AVATAR
                    Box(
                        modifier = Modifier
                            .size(100.dp)
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
                                text = "Add\nphoto",
                                color = TextLightGrey,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isUploadingPhoto) "Uploading photo…" else "Tap to change photo",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextLightGrey)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // FORM CARD
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0x33222233)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 18.dp)
                        ) {

                            ProfileLabel("Username")
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 10.dp),
                                singleLine = true
                            )

                            ProfileLabel("Email")
                            OutlinedTextField(
                                value = email,
                                onValueChange = {},
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 10.dp),
                                singleLine = true,
                                enabled = false
                            )

                            ProfileLabel("Height (cm)")
                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { heightText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 10.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )

                            ProfileLabel("Weight (kg)")
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 10.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )

                            ProfileLabel("Daily step goal")
                            OutlinedTextField(
                                value = stepGoalText,
                                onValueChange = { stepGoalText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 14.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )

                            Text(
                                text = "These details are used to personalise your fitness statistics.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextLightGrey)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
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
                                        .height(46.dp)
                                ) {
                                    Text("Reset")
                                }

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
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = YellowAccent
                                    ),
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(46.dp)
                                ) {
                                    Text(
                                        text = if (isSaving) "Saving…" else "Save changes",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = Color.Black,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    message?.let { msg ->
                        Text(
                            text = msg,
                            color = if (msg.contains("success", ignoreCase = true))
                                Color(0xFF4CAF50) else Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            color = TextGrey,
            fontWeight = FontWeight.SemiBold
        )
    )
}
