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

    // -------------------------
    // ⭐ GET LIVE VALUES FROM VIEWMODEL
    // -------------------------
    val vm = LocalFitnessViewModel.current
    val steps = vm.steps.collectAsState().value
    val calories = vm.calories.collectAsState().value
    val distance = vm.distanceKm.collectAsState().value

    GradientBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // Header
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

            // PROFILE BUTTON (TOP RIGHT)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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

            Spacer(modifier = Modifier.height(8.dp))


            // ------------------------------
            // TODAY'S ACTIVITY CARD (LIVE STATS)
            // ------------------------------
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatItem("Steps", steps.toString())
                        StatItem("Calories", String.format("%.0f kcal", calories))
                        StatItem("Distance", String.format("%.2f km", distance))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            // ------------------------------
            // LIVE STEP COUNTER CARD
            // ------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
                    .clickable { onStepsClick() }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "Live Step Counter",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = steps.toString(),
                        color = YellowAccent,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Steps Today",
                        color = TextGrey,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            // ------------------------------
            // GPS ROUTE TRACKING CARD
            // ------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {

                Column(horizontalAlignment = Alignment.Start) {
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
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            // ------------------------------
            // POSE DETECTION CARD
            // ------------------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
                    .clickable { onStartPoseDetection() }
            ) {

                Column(horizontalAlignment = Alignment.Start) {
                    Text("AI Pose Detection", color = TextWhite, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Track your form using ML Kit.", color = TextGrey, fontSize = 14.sp)
                }
            }

            // ------------------------------
            // LOGOUT BUTTON (Simple)
            // ------------------------------
            Text(
                text = "Logout",
                color = YellowAccent,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable { onLogout() }
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
