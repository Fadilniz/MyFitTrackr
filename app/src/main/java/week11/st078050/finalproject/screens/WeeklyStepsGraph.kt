package week11.st078050.finalproject.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.TextGrey
import week11.st078050.finalproject.ui.theme.YellowAccent

@Composable
fun WeeklyStepsGraph(stepsList: List<Int>) {

    val maxSteps = (stepsList.maxOrNull() ?: 1).toFloat()

    val animatedHeights = stepsList.map {
        animateFloatAsState(targetValue = it / maxSteps).value
    }

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Column(modifier = Modifier.fillMaxWidth()) {

        // Graph Title
        Text(
            text = "Weekly Activity",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Canvas Graph
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 10.dp)
        ) {
            val barWidth = size.width / 14f // 7 bars + spaces
            val maxHeight = size.height

            animatedHeights.forEachIndexed { index, anim ->
                val barHeight = anim * maxHeight

                drawLine(
                    color = YellowAccent,
                    start = Offset(
                        x = barWidth * (index * 2 + 1),
                        y = maxHeight
                    ),
                    end = Offset(
                        x = barWidth * (index * 2 + 1),
                        y = maxHeight - barHeight
                    ),
                    strokeWidth = barWidth,
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Day Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                Text(day, color = TextGrey, fontSize = 12.sp)
            }
        }
    }
}
