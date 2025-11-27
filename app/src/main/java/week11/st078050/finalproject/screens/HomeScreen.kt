package week11.st078050.finalproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.components.GradientBackground
import week11.st078050.finalproject.ui.theme.*

@Composable
fun HomeScreen(
    stepsToday: Int = 5420,
    calories: Int = 220,
    distanceKm: Double = 3.9,
    onStartRoute: () -> Unit = {},
    onStartPoseDetection: () -> Unit = {}
) {
    var voiceAlertsEnabled by remember { mutableStateOf(false) }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // -------------------------
            // HEADER
            // -------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hi, User",
                        fontSize = 28.sp,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Welcome back!",
                        color = TextGrey,
                        fontSize = 16.sp
                    )
                }

                // Profile Placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3C3454))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -------------------------
            // ACTIVITY DASHBOARD CARD
            // -------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2438)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "Today's Activity",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        ActivityStat("Steps", stepsToday.toString())
                        ActivityStat("Calories", "${calories} kcal")
                        ActivityStat("Distance", "${distanceKm} km")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Placeholder for chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color(0xFF1F1B2E), RoundedCornerShape(16.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -------------------------
            // LIVE STEP COUNTER
            // -------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2438)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Live Step Counter", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3C3454)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stepsToday.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = YellowAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Steps Today", color = TextGrey)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -------------------------
            // GPS ROUTE TRACKING SECTION
            // -------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2438)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "Track Your Route",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Start GPS tracking to monitor your path.",
                        color = TextGrey
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onStartRoute,
                        colors = ButtonDefaults.buttonColors(containerColor = YellowAccent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("Start Route Tracking", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // -------------------------
            // VOICE ALERTS SWITCH
            // -------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2A2438), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {
                    Text("Voice Alerts", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Enable audio updates while running", color = TextGrey)
                }

                Switch(
                    checked = voiceAlertsEnabled,
                    onCheckedChange = { voiceAlertsEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = YellowAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // -------------------------
            // POSE DETECTION BUTTON
            // -------------------------
            Button(
                onClick = onStartPoseDetection,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Start Pose Detection", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ActivityStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(label, color = TextGrey, fontSize = 14.sp)
        Text(value, color = YellowAccent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}
