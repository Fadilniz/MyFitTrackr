package week11.st078050.finalproject.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.*
import week11.st078050.finalproject.ui.theme.components.GradientBackground
import androidx.compose.foundation.background

@Composable
fun StepsScreen(
    onBackClick: () -> Unit = {}
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "Step Counter",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Circular Step Indicator
            Box(
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    drawArc(
                        color = YellowAccent.copy(alpha = 0.7f),
                        startAngle = -90f,
                        sweepAngle = 240f, // fake progress
                        useCenter = false,
                        style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "4,520",
                        color = TextWhite,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "steps",
                        color = TextGrey,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                StatCard("Calories", "120 kcal")
                StatCard("Distance", "3.4 km")
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Mock chart placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chart Placeholder",
                    color = TextLightGrey
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Start Button
            Button(
                onClick = { /* logic later */ },
                colors = ButtonDefaults.buttonColors(containerColor = YellowAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(
                    "Start Tracking",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(title, color = TextGrey, fontSize = 14.sp)
        Text(value, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
