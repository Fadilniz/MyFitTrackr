package week11.st078050.finalproject.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.TextWhite
import week11.st078050.finalproject.ui.theme.YellowAccent

@Composable
fun WeeklyStepsGraph(
    stepsList: List<Int>,   // Monday → Sunday
    stepGoal: Int = 10000   // dynamically changeable
) {
    val days = listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

    Column(modifier = Modifier.fillMaxWidth()) {

        Text(
            text = "Weekly Activity",
            color = TextWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .height(180.dp),   // BIGGER GRAPH
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {

            stepsList.forEachIndexed { index, steps ->

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Canvas(
                        modifier = Modifier
                            .width(26.dp)
                            .height(150.dp)   // TALLER BAR AREA
                    ) {

                        val fullHeight = size.height

                        // 🔳 BACKGROUND BAR (gray)
                        drawRoundRect(
                            color = Color(0x33FFFFFF),
                            topLeft = Offset(0f, 0f),
                            size = Size(size.width, fullHeight),
                            cornerRadius = CornerRadius(10f, 10f)
                        )

                        // Prevent divide-by-zero
                        val safeGoal = if (stepGoal <= 0) 1 else stepGoal

                        // Normalize step height based on USER’S goal
                        val ratio = (steps.toFloat() / safeGoal).coerceIn(0f, 1f)
                        val barHeight = ratio * fullHeight

                        // 🟨 YELLOW BAR
                        drawRoundRect(
                            color = YellowAccent,
                            topLeft = Offset(
                                x = 0f,
                                y = fullHeight - barHeight
                            ),
                            size = Size(
                                width = size.width,
                                height = barHeight
                            ),
                            cornerRadius = CornerRadius(10f, 10f)
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(days[index], color = TextWhite, fontSize = 13.sp)
                }
            }
        }
    }
}
