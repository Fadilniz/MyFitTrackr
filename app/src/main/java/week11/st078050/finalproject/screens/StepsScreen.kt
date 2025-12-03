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
import week11.st078050.finalproject.viewmodel.LocalFitnessViewModel

@Composable
fun StepsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val vm = LocalFitnessViewModel.current   // <-- GLOBAL VIEWMODEL

    // STATE
    // STATE
    var steps by remember { mutableStateOf(0) }
    var hasSensor by remember { mutableStateOf(true) }
    var hasPermission by remember { mutableStateOf(false) }

    // Text-to-Speech for voice alerts
    // Text-to-Speech for voice alerts
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var lastAnnouncedHundreds by remember { mutableStateOf(0) }


    DisposableEffect(Unit) {
        // Create TextToSpeech instance
        val tts = TextToSpeech(context) { status ->
            // You can log status here if you want, but we don't need tts inside
            // this lambda anymore, so no error.
        }

        // Set language *after* creating it
        tts.language = Locale.US

        textToSpeech = tts

        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }


    // PERMISSION


    // PERMISSION
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

            // SENSOR LISTENER
            if (hasPermission) {
                StepSensorListener(
                    context = context,
                    onStepsChanged = {
                        steps = it
                        vm.updateSteps(it)   // <-- UPDATE VIEWMODEL
                    },
                    onSensorAvailability = { hasSensor = it }
                )
            }
            // Voice alert when user reaches 100 steps
            // Voice alert every 100 steps (100, 200, 300, ...)
            LaunchedEffect(steps) {
                if (steps >= 100) {
                    val hundreds = steps / 100               // 0–99 -> 0, 100–199 -> 1, etc.
                    if (hundreds > lastAnnouncedHundreds) {
                        val roundedSteps = hundreds * 100    // 1 -> 100, 2 -> 200...

                        textToSpeech?.speak(
                            "You have walked $roundedSteps steps today",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "steps-$roundedSteps"
                        )

                        lastAnnouncedHundreds = hundreds
                    }
                } else {
                    // If steps go back below 100, reset, so a new session can re-announce
                    lastAnnouncedHundreds = 0
                }
            }



            // CIRCLE UI
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
    onStepsChanged: (Int) -> Unit,
    onSensorAvailability: (Boolean) -> Unit
) {
    var baseStep by remember { mutableStateOf(-1f) }

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
                    if (baseStep < 0f) baseStep = total

                    val diff = total - baseStep
                    if (diff >= 0) onStepsChanged(diff.toInt())
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
