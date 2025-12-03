package week11.st078050.finalproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import week11.st078050.finalproject.data.repository.LocalFitnessViewModel

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

    val weeklySteps = vm.weeklySteps.collectAsState().value

    val stepProg = vm.stepProgress.collectAsState().value
    val calorieProg = vm.calorieProgress.collectAsState().value
    val distanceProg = vm.distanceProgress.collectAsState().value

    // User info for greeting + profile picture
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

    var userName by remember { mutableStateOf("User") }
    var photoUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val uid = auth.currentUser?.uid ?: return@LaunchedEffect
            val snapshot = firestore.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
            if (user != null) {
                if (user.username.isNotBlank()) userName = user.username
                photoUrl = user.photoUrl
            }
        } catch (_: Exception) {
            // keep defaults
        }
    }

    GradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                HomeTopBar(
                    userName = userName,
                    photoUrl = photoUrl,
                    onProfileClick = onProfileClick
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {

                // WEEKLY GRAPH
                Text(
                    text = "Weekly activity",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(Modifier.height(10.dp))

                val weeklyList = weeklySteps.values.toList()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x33222233)
                    )
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        WeeklyStepsGraph(
                            stepsList = if (weeklyList.isNotEmpty()) {
                                weeklyList
                            } else {
                                listOf(steps, steps, steps, steps, steps, steps, steps)
                            }
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // PROGRESS RINGS
                Text(
                    text = "Today’s overview",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x33222233)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProgressRing(
                            label = "Steps",
                            valueText = steps.toString(),
                            progress = stepProg
                        )
                        ProgressRing(
                            label = "Calories",
                            valueText = String.format("%.0f", calories),
                            progress = calorieProg
                        )
                        ProgressRing(
                            label = "Distance",
                            valueText = String.format("%.2f km", distance),
                            progress = distanceProg
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // LIVE STEP COUNTER
                HomeSectionCard(
                    onClick = onStepsClick
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Live step counter",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            steps.toString(),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = YellowAccent
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Steps today",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextGrey)
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // ROUTE TRACKING
                HomeSectionCard {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Route tracking",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Use GPS to record your walking or running route.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextGrey)
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = onStartRoute,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowAccent
                            )
                        ) {
                            Text(
                                "Start route tracking",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))

                // POSE DETECTION
                HomeSectionCard(
                    onClick = onStartPoseDetection
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "AI pose detection",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Analyse your exercise form using ML Kit.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextGrey)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // OPTIONAL: link to sensor dashboard
                TextButton(
                    onClick = onSensorDashboardClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Open sensor dashboard",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextLightGrey)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // LOGOUT
                Text(
                    "Logout",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = YellowAccent,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable { onLogout() }
                        .padding(vertical = 6.dp)
                )

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    userName: String,
    photoUrl: String,
    onProfileClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Hi, $userName",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = TextWhite
        ),
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(42.dp)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(YellowAccent),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Profile photo",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = userName.firstOrNull()?.uppercase() ?: "U",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun HomeSectionCard(
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x44222233)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp, vertical = 14.dp),
            content = content
        )
    }
}
