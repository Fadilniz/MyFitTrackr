package week11.st078050.finalproject.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import week11.st078050.finalproject.ui.theme.TextGrey
import week11.st078050.finalproject.ui.theme.TextWhite
import week11.st078050.finalproject.ui.theme.YellowAccent
import week11.st078050.finalproject.ui.theme.components.GradientBackground

@Composable
fun StepsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // --- STATE ---
    var steps by remember { mutableStateOf(0) }
    var hasSensor by remember { mutableStateOf(true) }
    var hasPermission by remember { mutableStateOf(false) }

    // --- PERMISSION LAUNCHER (for ACTIVITY_RECOGNITION) ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    // Check permission once when screen opens
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Before Android 10, no special permission needed
            hasPermission = true
        } else {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) {
                hasPermission = true
            } else {
                permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    // --- SENSOR LOGIC ---
    LaunchedEffect(hasPermission) {
        // Just triggers recomposition when permission changes
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Back arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBackClick() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Step Counter",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (!hasPermission)
                    "Waiting for activity permission..."
                else if (!hasSensor)
                    "Step counter sensor not available on this device."
                else
                    "Walk with your phone to see live steps.",
                color = TextGrey,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- Start sensor listening only when we have permission ---
            if (hasPermission) {
                StepSensorListener(
                    context = context,
                    onStepsChanged = { newSteps ->
                        steps = newSteps
                    },
                    onSensorAvailability = { available ->
                        hasSensor = available
                    }
                )
            }

            // Circular progress UI
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    // Background arc
                    drawArc(
                        color = Color(0x44FFFFFF),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Fake progress (just for visuals)
                    val progress = (steps.coerceAtMost(10000) / 10000f) * 360f
                    drawArc(
                        color = YellowAccent,
                        startAngle = -90f,
                        sweepAngle = progress,
                        useCenter = false,
                        style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = steps.toString(),
                        color = TextWhite,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "steps today",
                        color = TextGrey,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Simple stats row (will connect more logic later)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBox("Goal", "10,000")
                StatBox("Progress", "${steps.coerceAtMost(10000) / 100}%")
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { /* later: reset / start pause logic */ },
                colors = ButtonDefaults.buttonColors(containerColor = YellowAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "Keep Moving",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String) {
    Column {
        Text(label, color = TextGrey, fontSize = 14.sp)
        Text(value, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

/**
 * Listens to the TYPE_STEP_COUNTER sensor and calls onStepsChanged with
 * the number of steps taken since this composable started.
 */
@Composable
private fun StepSensorListener(
    context: Context,
    onStepsChanged: (Int) -> Unit,
    onSensorAvailability: (Boolean) -> Unit
) {
    // We keep baseStep as the first reading, so we only count "new" steps
    var baseStep by remember { mutableStateOf(-1f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            onSensorAvailability(false)
            onDispose { }
        } else {
            onSensorAvailability(true)

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val totalSinceBoot = event.values[0] // float
                    if (baseStep < 0f) {
                        baseStep = totalSinceBoot // first reading as baseline
                    }
                    val diff = totalSinceBoot - baseStep
                    if (diff >= 0) {
                        onStepsChanged(diff.toInt())
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            sensorManager.registerListener(
                listener,
                stepSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }
}
