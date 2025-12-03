package week11.st078050.finalproject.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.accompanist.permissions.*
import week11.st078050.finalproject.viewmodel.RouteTrackingViewModel

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("MissingPermission")
@Composable
fun RouteTrackingScreen(
    viewModel: RouteTrackingViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {

    val uiState by viewModel.uiState.collectAsState()

    val locationPermission = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        if (!locationPermission.status.isGranted) locationPermission.launchPermissionRequest()
    }

    if (!locationPermission.status.isGranted) {
        Text(
            "Please allow location permission to use GPS tracking.",
            color = Color.White,
            modifier = Modifier.padding(20.dp)
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D1A))
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(onClick = { onBackClick() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    "GPS Route Tracking",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            val cameraPositionState = rememberCameraPositionState()

            LaunchedEffect(uiState.routePoints) {
                if (uiState.routePoints.isNotEmpty()) {
                    val last = uiState.routePoints.last()
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(last, 17f),
                        durationMs = 700
                    )
                }
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true)
            ) {
                if (uiState.routePoints.size > 1) {
                    Polyline(
                        points = uiState.routePoints,
                        color = Color.Yellow,
                        width = 8f
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBox("Distance", "%.2f km".format(uiState.distanceMeters / 1000))
                StatBox("Time", uiState.formattedDuration)
                StatBox("Pace", uiState.paceString)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (uiState.isTracking) viewModel.stopRun()
                    else viewModel.startRun()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isTracking) Color(0xFFE53935) else Color(0xFFFFEB3B),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(
                    text = if (uiState.isTracking) "Stop Run" else "Start Run",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

