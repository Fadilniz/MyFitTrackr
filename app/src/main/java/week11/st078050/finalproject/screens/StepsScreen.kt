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
import week11.st078050.finalproject.data.repository.FitnessViewModel
import week11.st078050.finalproject.data.repository.LocalFitnessViewModel

@Composable
fun StepsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val vm = LocalFitnessViewModel.current     // GLOBAL VIEWMODEL

    // Steps start from persistent savedSteps
    var steps by remember { mutableStateOf(vm.savedSteps) }
    var hasSensor by remember { mutableStateOf(true) }
    var hasPermission by remember { mutableStateOf(false) }

    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var lastAnnouncedHundreds by remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) {}
        tts.language = Locale.US
        textToSpeech = tts

        onDispose {
            tts.stop(); tts.shutdown()
        }
    }

    // Permission logic
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

            Text("Step Counter", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Text(
                text = when {
                    !hasPermission -> "Waiting for activity permission..."
                    !hasSensor -> "Step counter sensor not available."
                    else -> "Walk with your phone to see live steps."
                },
                color = TextGrey,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (hasPermission) {
                StepSensorListener(
                    context = context,
                    vm = vm,
                    onStepsChanged = { newSteps ->
                        steps = newSteps
                        vm.saveSteps(newSteps)   // 🔥 SAVE PERSISTENTLY
                    },
                    onSensorAvailability = { hasSensor = it }
                )
            }

            LaunchedEffect(steps) {
                val hundreds = steps / 100
                if (hundreds > lastAnnouncedHundreds) {
                    val announcement = hundreds * 100
                    textToSpeech?.speak(
                        "You have walked $announcement steps today",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "steps-$announcement"
                    )
                    lastAnnouncedHundreds = hundreds
                }
            }

            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(200.dp)) {

                    drawArc(
                        color = Color(0x44FFFFFF),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(18.dp.toPx(), cap = StrokeCap.Round)
                    )

                    val progress = (steps.coerceAtMost(10000) / 10000f) * 360f

                    drawArc(
                        color = YellowAccent,
                        startAngle = -90f,
                        sweepAngle = progress,
                        useCenter = false,
                        style = Stroke(18.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(steps.toString(), color = TextWhite, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    Text("steps today", color = TextGrey, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = YellowAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Keep Moving", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

@Composable
private fun StepSensorListener(
    context: Context,
    vm: FitnessViewModel,
    onStepsChanged: (Int) -> Unit,
    onSensorAvailability: (Boolean) -> Unit
) {
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

                    // 🔥 Persistent baseline
                    if (vm.baseStep == null) vm.baseStep = total

                    val diff = total - (vm.baseStep ?: total)
                    if (diff >= 0) onStepsChanged(diff.toInt())
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            sensorManager.registerListener(listener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }
}
