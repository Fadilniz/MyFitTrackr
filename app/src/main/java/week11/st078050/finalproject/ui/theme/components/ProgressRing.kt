package week11.st078050.finalproject.ui.theme.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.TextGrey
import week11.st078050.finalproject.ui.theme.TextWhite
import week11.st078050.finalproject.ui.theme.YellowAccent

@Composable
fun ProgressRing(
    label: String,
    valueText: String,
    progress: Float,          // Must be 0f → 1f
    modifier: Modifier = Modifier,
) {
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ), label = ""
    )

    val gradientColors = listOf(
        YellowAccent,
        Color(0xFFFFD54F),
        Color(0xFFFFC107)
    )

    val glowColor = YellowAccent.copy(alpha = 0.6f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(110.dp)
    ) {

        // Outer Glow
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    ambientColor = glowColor,
                    spotColor = glowColor
                )
        )

        Canvas(modifier = Modifier.size(110.dp)) {

            val strokeWidth = 14.dp.toPx()
            val size = Size(size.width, size.height)

            // Background ring (inner shadow look)
            drawArc(
                color = Color(0x22FFFFFF),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth)
            )

            // Gradient ring
            drawArc(
                brush = Brush.sweepGradient(gradientColors),
                startAngle = -90f,
                sweepAngle = animatedProgress.value * 360f,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            // Inner shadow effect (fake by overlay arc)
            drawArc(
                color = Color(0x11000000),
                startAngle = -90f,
                sweepAngle = animatedProgress.value * 360f,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth / 1.5f,
                    cap = StrokeCap.Round
                )
            )
        }

        // Text inside
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = valueText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextGrey
            )
        }
    }
}
