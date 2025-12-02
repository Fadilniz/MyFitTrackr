package week11.st078050.finalproject.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.TextGrey
import week11.st078050.finalproject.ui.theme.TextWhite
import week11.st078050.finalproject.ui.theme.YellowAccent
import week11.st078050.finalproject.ui.theme.components.GradientBackground
import week11.st078050.finalproject.viewmodel.LocalFitnessViewModel

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun SensorDashboardScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val vm = LocalFitnessViewModel.current

    val steps = vm.steps.collectAsState().value
    val calories = vm.calories.collectAsState().value
    val distance = vm.distanceKm.collectAsState().value

    // -------- ACCELEROMETER GRAPH --------
    val accelData = remember { mutableStateListOf<Float>() }

    AccelerometerListener(
        context = context,
        onData = { value ->
            if (accelData.size > 60) accelData.removeAt(0)

            accelData.add(value)
        }
    )

    val progressGoal = animateFloatAsState(
        targetValue = (steps.coerceAtMost(10000) / 10000f)
    )

    GradientBackground {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBackClick() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Sensor Dashboard",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // -------- PROGRESS RING --------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    // background ring
                    drawArc(
                        color = Color(0x33FFFFFF),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(20f, cap = StrokeCap.Round)
                    )

                    // animated progress
                    drawArc(
                        color = YellowAccent,
                        startAngle = -90f,
                        sweepAngle = progressGoal.value * 360f,
                        useCenter = false,
                        style = Stroke(20f, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = steps.toString(),
                        color = TextWhite,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Steps Today", color = TextGrey)
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // -------- LIVE STATS BOX --------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x44222222), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {

                Column {
                    Text("Live Activity Stats", color = TextGrey, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatBox("Steps", steps.toString())
                        StatBox("Calories", String.format("%.1f kcal", calories))
                        StatBox("Distance", String.format("%.2f km", distance))
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // -------- ACCELEROMETER GRAPH --------
            Text(
                text = "Motion Sensor Waveform",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0x33222222), RoundedCornerShape(16.dp))
                    .padding(10.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val maxVal = 20f
                    val midY = size.height / 2f
                    val stepX = size.width / (accelData.size.coerceAtLeast(1))

                    for (i in 1 until accelData.size) {
                        drawLine(
                            color = YellowAccent,
                            start = androidx.compose.ui.geometry.Offset(
                                (i - 1) * stepX,
                                midY - (accelData[i - 1] * 4)
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                i * stepX,
                                midY - (accelData[i] * 4)
                            ),
                            strokeWidth = 4f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }
    }
}


// ------------------------
// SMALL COMPOSABLE
// ------------------------
@Composable
fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGrey, fontSize = 14.sp)
        Text(value, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}


// ------------------------
// ACCELEROMETER LISTENER
// ------------------------
@Composable
fun AccelerometerListener(
    context: Context,
    onData: (Float) -> Unit
) {
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accel == null) {
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    onData(event.values[0]) // Use X-axis for waveform
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            sensorManager.registerListener(listener, accel, SensorManager.SENSOR_DELAY_GAME)

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }
}
