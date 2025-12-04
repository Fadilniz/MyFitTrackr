package week11.st078050.finalproject.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import week11.st078050.finalproject.viewmodel.RouteTrackingViewModel

data class RunHistoryEntry(
    val id: Int,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val paceMps: Double
)

@SuppressLint("MissingPermission")
@Composable
fun RouteTrackingScreen(
    viewModel: RouteTrackingViewModel = viewModel(),
    onBack: () -> Unit
) {
    val ui by viewModel.uiState.collectAsState()
    val camPos by viewModel.mapCameraPosition.collectAsState()

    // local history list (only for this screen session)
    val runHistory = remember { mutableStateListOf<RunHistoryEntry>() }
    var nextId by remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    val cameraState = rememberCameraPositionState()

    // update map camera when location changes
    LaunchedEffect(camPos) {
        camPos?.let {
            cameraState.position = CameraPosition.fromLatLngZoom(it, 17f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D1A)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Top Bar
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

        // Map Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(12.dp)
        ) {
            GoogleMap(
                cameraPositionState = cameraState,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(10.dp))

        // Stats Row
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

        Spacer(Modifier.height(20.dp))

        // Start Run
        Button(
            onClick = { viewModel.startRun() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFFFFE766))
        ) {
            Text("Start Run", color = Color.Black)
        }

        // Stop Run
        Button(
            onClick = {
                // 1) stop tracking in ViewModel
                viewModel.stopRun()

                // 2) take the final stats and push into history
                if (ui.durationSeconds > 0 && ui.distanceMeters > 0.0) {
                    val entry = RunHistoryEntry(
                        id = nextId++,
                        distanceMeters = ui.distanceMeters,
                        durationSeconds = ui.durationSeconds,
                        paceMps = ui.paceMps
                    )
                    runHistory.add(0, entry) // add at top
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(Color.Red)
        ) {
            Text("Stop", color = Color.White)
        }

        // -----------------------------
        // History under the Stop button
        // -----------------------------
        if (runHistory.isNotEmpty()) {
            Text(
                text = "History",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true) // take the remaining space
                    .padding(horizontal = 20.dp)
            ) {
                items(runHistory, key = { it.id }) { run ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2A))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Distance in km
                            Text(
                                text = "Distance: ${"%.2f".format(run.distanceMeters / 1000)} km",
                                color = Color(0xFFFFE766),
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Time mm:ss
                            Text(
                                text = "Time: ${
                                    String.format(
                                        "%02d:%02d",
                                        run.durationSeconds / 60,
                                        run.durationSeconds % 60
                                    )
                                }",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            // Average speed
                            Text(
                                text = "Average Speed: ${
                                    if (run.paceMps > 0)
                                        "${"%.2f".format(run.paceMps)} m/s"
                                    else
                                        "--"
                                }",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}