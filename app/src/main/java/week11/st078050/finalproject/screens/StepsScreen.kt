package week11.st078050.finalproject.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.speech.tts.TextToSpeech
import java.util.Locale
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import week11.st078050.finalproject.data.repository.LocalFitnessViewModel

@Composable
fun StepsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val vm = LocalFitnessViewModel.current

    // 🔥 steps now come from ViewModel (per-user, persistent)
    val steps by vm.steps.collectAsState()

    var hasSensor by remember { mutableStateOf(true) }
    var hasPermission by remember { mutableStateOf(false) }

    // Text-to-Speech
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var lastAnnouncedHundreds by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) {}
        tts.language = Locale.US
        textToSpeech = tts

        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    // Permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            hasPermission = true
        } else {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED

            if (granted) hasPermission = true
            else permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
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
                text = when {
                    !hasPermission -> "Waiting for activity permission..."
                    !hasSensor -> "Step counter sensor not available on this device."
                    else -> "Walk with your phone to see live steps."
                },
                color = TextGrey,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (hasPermission) {
                StepSensorListener(
                    context = context,
                    onStepsDelta = { delta ->
                        vm.addSteps(delta)   // 🔥 per-user, also saved to Firestore
                    },
                    onSensorAvailability = { hasSensor = it }
                )
            }

            // Voice alerts every 100 steps
            LaunchedEffect(steps) {
                if (steps >= 100) {
                    val hundreds = steps / 100
                    if (hundreds > lastAnnouncedHundreds) {
                        val roundedSteps = hundreds * 100
                        textToSpeech?.speak(
                            "You have walked $roundedSteps steps today",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "steps-$roundedSteps"
                        )
                        lastAnnouncedHundreds = hundreds
                    }
                } else {
                    lastAnnouncedHundreds = 0
                }
            }

            // Circle UI
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    drawArc(
                        color = Color(0x44FFFFFF),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                    )

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

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { /* future logic */ },
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
private fun StepSensorListener(
    context: Context,
    onStepsDelta: (Int) -> Unit,
    onSensorAvailability: (Boolean) -> Unit
) {
    // We store only last TOTAL from sensor to compute deltas
    var lastTotal by remember { mutableStateOf<Float?>(null) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            onSensorAvailability(false)
            onDispose { }
        } else {
            onSensorAvailability(true)

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val total = event.values[0]
                    val previous = lastTotal
                    lastTotal = total

                    if (previous != null) {
                        val diff = (total - previous).toInt()
                        if (diff > 0) {
                            onStepsDelta(diff)   // 🔥 send ONLY the new steps
                        }
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
