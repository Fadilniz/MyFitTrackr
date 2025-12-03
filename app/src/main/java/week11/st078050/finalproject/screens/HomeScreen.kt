package week11.st078050.finalproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.*
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

    // NEW — REAL WEEKLY DATA
    val weeklySteps = vm.weeklySteps.collectAsState().value

    val stepProg = vm.stepProgress.collectAsState().value
    val calorieProg = vm.calorieProgress.collectAsState().value
    val distanceProg = vm.distanceProgress.collectAsState().value

    GradientBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Hi, User!", color = TextWhite, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    Text("Welcome back!", color = TextGrey, fontSize = 16.sp)
                }

                Text(
                    text = "Profile",
                    color = YellowAccent,
                    fontSize = 17.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable { onProfileClick() }
                )
            }

            Spacer(Modifier.height(18.dp))

            // WEEKLY GRAPH (NOW REAL VALUES)
            WeeklyStepsGraph(stepsList = weeklySteps.values.toList())

            Spacer(Modifier.height(20.dp))

            // PROGRESS RINGS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
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

            Spacer(Modifier.height(24.dp))

            // LIVE STEP COUNTER CARD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
                    .clickable { onStepsClick() }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Live Step Counter", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Text(steps.toString(), color = YellowAccent, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                    Text("Steps Today", color = TextGrey, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ROUTE TRACKING
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {

                Column {
                    Text("Track Your Route", color = TextWhite, fontSize = 18.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("Use GPS to record your running path.", color = TextGrey, fontSize = 14.sp)
                    Spacer(Modifier.height(15.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(YellowAccent, RoundedCornerShape(12.dp))
                            .clickable { onStartRoute() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Start Route Tracking", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // POSE DETECTION
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
                    Text("Track your form using ML Kit.", color = TextGrey, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            // LOGOUT
            Text(
                "Logout",
                color = YellowAccent,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier.clickable { onLogout() }
            )
        }
    }
}
