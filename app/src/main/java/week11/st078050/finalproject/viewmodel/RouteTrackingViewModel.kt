package week11.st078050.finalproject.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RouteUiState(
    val isTracking: Boolean = false,
    val routePoints: List<LatLng> = emptyList(),
    val distanceMeters: Double = 0.0,
    val durationMillis: Long = 0L,
    val formattedDuration: String = "00:00",
    val paceString: String = "--"
)

class RouteTrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(application)

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState

    private var locationCallback: LocationCallback? = null
    private var timerJob: Job? = null
    private var startTime = 0L

    // -------------------------
    // START RUN
    // -------------------------
    fun startRun() {

        if (_uiState.value.isTracking) return

        startTime = System.currentTimeMillis()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { loc -> updateLocation(loc) }
            }
        }

        _uiState.value = _uiState.value.copy(isTracking = true)

        startTimer()   // ← IMPORTANT
    }

    // -------------------------
    // STOP RUN
    // -------------------------
    fun stopRun() {
        fusedClient.removeLocationUpdates(locationCallback!!)
        locationCallback = null
        timerJob?.cancel()

        _uiState.value = _uiState.value.copy(isTracking = false)
    }

    // -------------------------
    // TIMER — updates EXACTLY every 1 second
    // -------------------------
    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (_uiState.value.isTracking) {

                val elapsed = System.currentTimeMillis() - startTime

                _uiState.value = _uiState.value.copy(
                    durationMillis = elapsed,
                    formattedDuration = formatDuration(elapsed)
                )

                delay(1000L) // EXACT 1 second
            }
        }
    }

    private fun formatDuration(ms: Long): String {
        val totalSec = (ms / 1000).toInt()
        val min = totalSec / 60
        val sec = totalSec % 60
        return "%02d:%02d".format(min, sec)
    }

    // -------------------------
    // UPDATE LOCATION + DISTANCE + PACE
    // -------------------------
    private fun updateLocation(location: Location) {

        if (!_uiState.value.isTracking) return

        val newPoint = LatLng(location.latitude, location.longitude)
        val points = _uiState.value.routePoints

        var newDistance = _uiState.value.distanceMeters

        if (points.isNotEmpty()) {
            val last = points.last()

            val result = FloatArray(1)
            Location.distanceBetween(
                last.latitude, last.longitude,
                newPoint.latitude, newPoint.longitude,
                result
            )
            newDistance += result[0]
        }

        _uiState.value = _uiState.value.copy(
            routePoints = points + newPoint,
            distanceMeters = newDistance,
            paceString = calculatePace(newDistance)
        )
    }

    private fun calculatePace(distanceMeters: Double): String {
        if (distanceMeters < 10) return "--"

        val km = distanceMeters / 1000
        val sec = (_uiState.value.durationMillis / 1000).toInt()

        val paceSecPerKm = sec / km
        val paceMin = (paceSecPerKm / 60).toInt()
        val paceSec = (paceSecPerKm % 60).toInt()

        return "%d:%02d /km".format(paceMin, paceSec)
    }
}
