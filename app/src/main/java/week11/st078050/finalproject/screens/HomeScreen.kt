package week11.st078050.finalproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.components.WeeklyStepsGraph
import week11.st078050.finalproject.ui.theme.TextGrey
import week11.st078050.finalproject.ui.theme.TextWhite
import week11.st078050.finalproject.ui.theme.YellowAccent
import week11.st078050.finalproject.ui.theme.components.GradientBackground
import week11.st078050.finalproject.viewmodel.LocalFitnessViewModel

@Composable
fun HomeScreen(
    onStartRoute: () -> Unit = {},
    onStartPoseDetection: () -> Unit = {},
    onLogout: () -> Unit = {},
    onStepsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {

    val vm = LocalFitnessViewModel.current

    val steps = vm.steps.collectAsState().value
    val calories = vm.calories.collectAsState().value
    val distance = vm.distanceKm.collectAsState().value
    val weeklySteps = vm.weeklySteps.collectAsState().value

    GradientBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // HEADER
            Text(
                text = "Hi, User!",
                color = TextWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Welcome back!",
                color = TextGrey,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Profile",
                    color = YellowAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onProfileClick() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // WEEKLY STEPS GRAPH
            WeeklyStepsGraph(stepsList = weeklySteps)

            Spacer(modifier = Modifier.height(25.dp))

            // TODAY'S ACTIVITY
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Today's Activity", color = TextGrey, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem("Steps", steps.toString())
                        StatItem("Calories", String.format("%.0f kcal", calories))
                        StatItem("Distance", String.format("%.2f km", distance))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // STEP COUNTER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
                    .clickable { onStepsClick() }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Live Step Counter", color = TextWhite, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = steps.toString(),
                        color = YellowAccent,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Steps Today", color = TextGrey, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ROUTE TRACKING
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {

                Column {
                    Text("Track Your Route", color = TextWhite, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Use GPS to record your running path.", color = TextGrey, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(15.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(YellowAccent, RoundedCornerShape(12.dp))
                            .clickable { onStartRoute() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Start Route Tracking",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Track your form using ML Kit.", color = TextGrey, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Logout",
                color = YellowAccent,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onLogout() }
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGrey, fontSize = 14.sp)
        Text(value, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
