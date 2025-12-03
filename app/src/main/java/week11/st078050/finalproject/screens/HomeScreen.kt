package week11.st078050.finalproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import week11.st078050.finalproject.data.model.User
import week11.st078050.finalproject.ui.theme.TextGrey
import week11.st078050.finalproject.ui.theme.TextLightGrey
import week11.st078050.finalproject.ui.theme.TextWhite
import week11.st078050.finalproject.ui.theme.YellowAccent
import week11.st078050.finalproject.ui.theme.components.GradientBackground
import week11.st078050.finalproject.ui.theme.components.ProgressRing
import week11.st078050.finalproject.viewmodel.LocalFitnessViewModel

@Composable
fun HomeScreen(
    onStartRoute: () -> Unit = {},
    onStartPoseDetection: () -> Unit = {},
    onLogout: () -> Unit = {},
    onStepsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onSensorDashboardClick: () -> Unit = {}
) {
    val vm = LocalFitnessViewModel.current

    val steps = vm.steps.collectAsState().value
    val calories = vm.calories.collectAsState().value
    val distance = vm.distanceKm.collectAsState().value

    val stepProg = vm.stepProgress.collectAsState().value
    val calorieProg = vm.calorieProgress.collectAsState().value
    val distanceProg = vm.distanceProgress.collectAsState().value

    // 🔹 User info for greeting + profile picture
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

    var userName by remember { mutableStateOf("User") }
    var photoUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val uid = auth.currentUser?.uid ?: return@LaunchedEffect

            val snapshot = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
            if (user != null) {
                if (user.username.isNotBlank()) {
                    userName = user.username
                }
                photoUrl = user.photoUrl
            }
        } catch (_: Exception) {
            // keep defaults
        }
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // scrolling on smaller phones
                .padding(horizontal = 20.dp)
        ) {

            // 🔹 Header: greeting + avatar under it (more stable layout)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Hi, $userName!",
                        color = TextWhite,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Welcome back!",
                        color = TextLightGrey,
                        fontSize = 14.sp
                    )
                }

                // 🔥 VERY VISIBLE avatar button
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer yellow ring
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(YellowAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUrl.isNotEmpty()) {
                            // Inner image, slightly smaller, clipped again
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = "Profile photo",
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = userName.firstOrNull()?.uppercase() ?: "U",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // 🔹 Weekly Steps Graph
            WeeklyStepsGraph(
                stepsList = listOf(
                    steps,
                    (steps * 0.8f).toInt(),
                    (steps * 0.6f).toInt(),
                    (steps * 1.1f).toInt(),
                    (steps * 0.9f).toInt(),
                    (steps * 0.4f).toInt(),
                    (steps * 0.3f).toInt()
                )
            )

            Spacer(Modifier.height(20.dp))

            // 🔹 Progress Rings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressRing(
                    label = "Steps",
                    valueText = steps.toString(),
                    progress = stepProg.coerceIn(0f, 1f)
                )

                ProgressRing(
                    label = "Calories",
                    valueText = String.format("%.0f", calories),
                    progress = calorieProg.coerceIn(0f, 1f)
                )

                ProgressRing(
                    label = "Distance",
                    valueText = String.format("%.2f km", distance),
                    progress = distanceProg.coerceIn(0f, 1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // 🔹 Live Step Counter Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
                    .clickable { onStepsClick() }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Live Step Counter",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        steps.toString(),
                        color = YellowAccent,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Steps Today", color = TextGrey, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            // 🔹 Route Tracking Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Track Your Route", color = TextWhite, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Use GPS to record your running path.",
                        color = TextGrey,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(YellowAccent, RoundedCornerShape(12.dp))
                            .clickable { onStartRoute() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Start Route Tracking",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 🔹 Pose Detection Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
                    .clickable { onStartPoseDetection() }
            ) {
                Column {
                    Text("AI Pose Detection", color = TextWhite, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Track your form using ML Kit.",
                        color = TextGrey,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 🔹 Logout
            Text(
                "Logout",
                color = YellowAccent,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.clickable { onLogout() }
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

//finished !!!