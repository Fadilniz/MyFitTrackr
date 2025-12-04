package week11.st078050.finalproject.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.maps.android.compose.*
import week11.st078050.finalproject.viewmodel.RouteTrackingViewModel

@SuppressLint("MissingPermission")
@Composable
fun RouteTrackingScreen(
    viewModel: RouteTrackingViewModel = viewModel(),
    onBack: () -> Unit
) {
    val ui = viewModel.uiState.collectAsState().value
    val camPos = viewModel.mapCameraPosition.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    val cameraPositionState = rememberCameraPositionState()



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D1A)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text(
                text = "GPS Route Tracking",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }

        // MAP
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(12.dp)
        ) {
            GoogleMap(
                cameraPositionState = cameraPositionState,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Distance", color = Color.White)
                Text("${"%.2f".format(ui.distanceMeters)} m", color = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Time", color = Color.White)
                Text(
                    String.format("%02d:%02d", ui.durationSeconds / 60, ui.durationSeconds % 60),
                    color = Color.White
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pace", color = Color.White)
                Text(
                    if (ui.paceMps > 0) "${"%.2f".format(ui.paceMps)} m/s" else "--",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Start Run button
        Button(
            onClick = { viewModel.startRun() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFFFE766))
        ) {
            Text("Start Run", color = Color.Black)
        }

        // Stop Run button
        Button(
            onClick = { viewModel.stopRun() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(Color.Red)
        ) {
            Text("Stop", color = Color.White)
        }
    }
}
