package week11.st078050.finalproject.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable

import androidx.compose.ui.platform.LocalContext
import week11.st078050.finalproject.ui.theme.TextWhite
import week11.st078050.finalproject.ui.theme.TextGrey
import week11.st078050.finalproject.ui.theme.YellowAccent
import week11.st078050.finalproject.ui.theme.components.GradientBackground

@Composable
fun SensorDashboardScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Sensor States
    var accelX by remember { mutableStateOf(0f) }
    var accelY by remember { mutableStateOf(0f) }
    var accelZ by remember { mutableStateOf(0f) }

    var gyroX by remember { mutableStateOf(0f) }
    var gyroY by remember { mutableStateOf(0f) }
    var gyroZ by remember { mutableStateOf(0f) }

    // Start sensor listener
    SensorDashboardListener(
        context = context,
        onAccelerometer = { x, y, z ->
            accelX = x; accelY = y; accelZ = z
        },
        onGyroscope = { x, y, z ->
            gyroX = x; gyroY = y; gyroZ = z
        }
    )

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            // Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
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

                Spacer(modifier = Modifier.width(20.dp))

                Text(
                    text = "Sensor Dashboard",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Accelerometer Card
            SensorCard(title = "Accelerometer", values = listOf(accelX, accelY, accelZ))

            Spacer(modifier = Modifier.height(20.dp))

            // Gyroscope Card
            SensorCard(title = "Gyroscope", values = listOf(gyroX, gyroY, gyroZ))
        }
    }
}

@Composable
fun SensorCard(title: String, values: List<Float>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0x44222222), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(title, color = YellowAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            Text("X: %.2f".format(values[0]), color = TextWhite, fontSize = 16.sp)
            Text("Y: %.2f".format(values[1]), color = TextWhite, fontSize = 16.sp)
            Text("Z: %.2f".format(values[2]), color = TextWhite, fontSize = 16.sp)
        }
    }
}

@Composable
fun SensorDashboardListener(
    context: Context,
    onAccelerometer: (Float, Float, Float) -> Unit,
    onGyroscope: (Float, Float, Float) -> Unit,
) {
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER ->
                        onAccelerometer(event.values[0], event.values[1], event.values[2])

                    Sensor.TYPE_GYROSCOPE ->
                        onGyroscope(event.values[0], event.values[1], event.values[2])
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(listener, gyroSensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}
