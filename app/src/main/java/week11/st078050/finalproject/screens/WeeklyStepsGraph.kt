package week11.st078050.finalproject.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.TextWhite
import week11.st078050.finalproject.ui.theme.YellowAccent

@Composable
fun WeeklyStepsGraph(
    stepsList: List<Int>      // ← THIS NAME MUST MATCH HomeScreen
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
                .height(140.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            stepsList.forEachIndexed { index, value ->
                val maxHeight = 110f
                val barHeight = (value / 12000f).coerceIn(0f, 1f) * maxHeight

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Canvas(
                        modifier = Modifier
                            .width(22.dp)
                            .height(120.dp)
                    ) {
                        drawRoundRect(
                            color = YellowAccent,
                            topLeft = androidx.compose.ui.geometry.Offset(
                                x = 0f,
                                y = size.height - barHeight
                            ),
                            size = androidx.compose.ui.geometry.Size(
                                width = size.width,
                                height = barHeight
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(days[index], color = TextWhite, fontSize = 12.sp)
                }
            }
        }
    }
}
