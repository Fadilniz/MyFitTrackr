package week11.st078050.finalproject.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.*
import week11.st078050.finalproject.ui.theme.components.GradientBackground
import androidx.compose.ui.draw.clip

@Composable
fun RouteTrackingScreen(
    onBackClick: () -> Unit = {}
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            // Back Arrow
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextLightGrey,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "GPS Route Tracking",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0x33222222)),
                contentAlignment = Alignment.Center
            ) {

                // Fake Path Polyline
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        color = YellowAccent,
                        start = Offset(100f, 300f),
                        end = Offset(300f, 150f),
                        strokeWidth = 12f,
                        cap = StrokeCap.Round
                    )

                    drawLine(
                        color = YellowAccent,
                        start = Offset(300f, 150f),
                        end = Offset(450f, 250f),
                        strokeWidth = 12f,
                        cap = StrokeCap.Round
                    )
                }

                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Location",
                    tint = YellowAccent,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Running Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RunStat("Distance", "1.42 km")
                RunStat("Time", "12:48")
                RunStat("Pace", "8'55\"")
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Start / Stop Button
            Button(
                onClick = { /* real logic later */ },
                colors = ButtonDefaults.buttonColors(containerColor = YellowAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Start Run",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun RunStat(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = TextGrey, fontSize = 14.sp)
        Text(value, color = TextWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}
