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
    val durationSeconds: Long = 0L,
    val paceMps: Double = 0.0,  // meters/second
)

class RouteTrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(application)

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState

    private var locationCallback: LocationCallback? = null
    private var timerJob: Job? = null
    private var startTime = 0L

    val mapCameraPosition = MutableStateFlow<LatLng?>(null)

    // -------------------------
    // INITIALIZATION
    // -------------------------
    fun initialize() {
        startLocationUpdates()
    }

    // -------------------------
    // START RUN
    // -------------------------
    fun startRun() {
        if (_uiState.value.isTracking) return

        startTime = System.currentTimeMillis()

        _uiState.value = _uiState.value.copy(
            isTracking = true
        )

        startTimer()
    }

    // -------------------------
    // STOP RUN
    // -------------------------
    fun stopRun() {
        _uiState.value = _uiState.value.copy(
            isTracking = false
        )
        timerJob?.cancel()
    }

    // -------------------------
    // LOCATION UPDATES
    // -------------------------
    private fun startLocationUpdates() {

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000L
        ).setMinUpdateDistanceMeters(1f).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val newLoc = result.lastLocation ?: return
                updateLocation(newLoc)
            }
        }

        fusedClient.requestLocationUpdates(
            request,
            locationCallback!!,
            null
        )
    }

    // -------------------------
    // TIMER 1-second updates
    // -------------------------
    private fun startTimer() {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            while (_uiState.value.isTracking) {
                val elapsedSec = (System.currentTimeMillis() - startTime) / 1000
                _uiState.value = _uiState.value.copy(
                    durationSeconds = elapsedSec
                )
                delay(1000L)
            }
        }
    }

    // -------------------------
    // UPDATE LOCATION + DISTANCE + PACE (m/s)
    // -------------------------
    private fun updateLocation(location: Location) {

        val newPoint = LatLng(location.latitude, location.longitude)

        // Always update camera target
        mapCameraPosition.value = newPoint

        if (!_uiState.value.isTracking) return

        val points = _uiState.value.routePoints
        var dist = _uiState.value.distanceMeters

        if (points.isNotEmpty()) {
            val last = points.last()
            val temp = FloatArray(1)
            Location.distanceBetween(
                last.latitude, last.longitude,
                newPoint.latitude, newPoint.longitude,
                temp
            )
            dist += temp[0]
        }

        // Calculate pace (m/s)
        val seconds = _uiState.value.durationSeconds
        val pace = if (seconds > 0) dist / seconds else 0.0

        _uiState.value = _uiState.value.copy(
            routePoints = points + newPoint,
            distanceMeters = dist,
            paceMps = pace
        )
    }
}
