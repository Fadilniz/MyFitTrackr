package week11.st078050.finalproject.viewmodel

import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
    val paceMps: Double = 0.0
)

class RouteTrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(application)

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState

    private var timerJob: Job? = null
    private var locationCallback: LocationCallback? = null
    private var startTime = 0L

    val mapCameraPosition = MutableStateFlow<LatLng?>(null)

    // -------------------------
    // INITIALIZE MAP LOCATION
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

        _uiState.value = _uiState.value.copy(isTracking = true)

        startTimer()
    }

    // -------------------------
    // STOP RUN  +  SAVE TO FIREBASE
    // -------------------------
    fun stopRun() {
        _uiState.value = _uiState.value.copy(isTracking = false)
        timerJob?.cancel()

        saveRunToFirebase()
    }

    // -------------------------
    // FIREBASE SAVE
    // -------------------------
    private fun saveRunToFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        val data = hashMapOf(
            "distanceMeters" to _uiState.value.distanceMeters,
            "durationSeconds" to _uiState.value.durationSeconds,
            "paceMps" to _uiState.value.paceMps,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("users")
            .document(uid)
            .collection("fitness")
            .add(data)
    }

    // -------------------------
    // TIMER
    // -------------------------
    private fun startTimer() {

        timerJob = viewModelScope.launch {
            while (_uiState.value.isTracking) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                _uiState.value = _uiState.value.copy(durationSeconds = elapsed)
                delay(1000)
            }
        }
    }

    // -------------------------
    // LOCATION UPDATES
    // -------------------------
    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).setMinUpdateDistanceMeters(1f).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val newLoc = result.lastLocation ?: return
                updateLocation(newLoc)
            }
        }


    }

    // -------------------------
    // UPDATE LOCATION, DISTANCE, PACE
    // -------------------------
    private fun updateLocation(loc: Location) {

        val newPoint = LatLng(loc.latitude, loc.longitude)

        // update camera position
        mapCameraPosition.value = newPoint

        if (!_uiState.value.isTracking) return

        val points = _uiState.value.routePoints
        var totalDist = _uiState.value.distanceMeters

        if (points.isNotEmpty()) {
            val last = points.last()
            val result = FloatArray(1)
            Location.distanceBetween(
                last.latitude, last.longitude,
                newPoint.latitude, newPoint.longitude,
                result
            )
            totalDist += result[0]
        }

        val sec = _uiState.value.durationSeconds
        val pace = if (sec > 0) totalDist / sec else 0.0

        _uiState.value = _uiState.value.copy(
            routePoints = points + newPoint,
            distanceMeters = totalDist,
            paceMps = pace
        )
    }
}
