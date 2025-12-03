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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
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

@OptIn(ExperimentalMaterial3Api::class)
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

    // -------- ACCELEROMETER GRAPH DATA --------
    val accelData = remember { mutableStateListOf<Float>() }

    AccelerometerListener(
        context = context,
        onData = { value ->
            if (accelData.size > 80) accelData.removeAt(0)
            accelData.add(value)
        }
    )

    val progressGoal by animateFloatAsState(
        targetValue = (steps.coerceAtMost(10_000) / 10_000f),
        label = "stepGoalProgress"
    )

    GradientBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Sensor dashboard",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextWhite
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {

                // -------- PROGRESS RING --------
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x33222233)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(180.dp)) {
                            // background ring
                            drawArc(
                                color = Color(0x33FFFFFF),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(18f, cap = StrokeCap.Round)
                            )

                            // animated progress
                            drawArc(
                                color = YellowAccent,
                                startAngle = -90f,
                                sweepAngle = progressGoal * 360f,
                                useCenter = false,
                                style = Stroke(18f, cap = StrokeCap.Round)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = steps.toString(),
                                fontSize = 38.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            Text(
                                text = "Steps today",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextGrey)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // -------- LIVE STATS BOX --------
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x44222222)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 16.dp)
                    ) {
                        Text(
                            "Live activity stats",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

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

                Spacer(modifier = Modifier.height(22.dp))

                // -------- ACCELEROMETER GRAPH --------
                Text(
                    text = "Motion sensor waveform",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x33222222)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val midY = size.height / 2f
                            val stepX =
                                if (accelData.size <= 1) 0f else size.width / (accelData.size - 1)

                            // baseline
                            drawLine(
                                color = Color(0x33FFFFFF),
                                start = androidx.compose.ui.geometry.Offset(0f, midY),
                                end = androidx.compose.ui.geometry.Offset(size.width, midY),
                                strokeWidth = 2f
                            )

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
    }
}

// ------------------------
// SMALL COMPOSABLE
// ------------------------
@Composable
fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(color = TextGrey)
        )
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextWhite,
                fontWeight = FontWeight.SemiBold
            )
        )
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
                    onData(event.values[0]) // X-axis for waveform
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
