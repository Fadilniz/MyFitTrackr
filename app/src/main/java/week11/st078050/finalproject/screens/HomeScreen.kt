package week11.st078050.finalproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun HomeScreen(
    onStartRoute: () -> Unit = {},
    onStartPoseDetection: () -> Unit = {},
    onLogout: () -> Unit = {},
    onStepsClick: () -> Unit = {} // ADDED HANDLER
) {

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

            // ------------------------------
            // TODAY'S ACTIVITY CARD
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
                        StatItem("Steps", "5420")
                        StatItem("Calories", "220 kcal")
                        StatItem("Distance", "3.9 km")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ------------------------------
            // STEP COUNTER CARD (Clickable)
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
                        text = "5420",
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
