package week11.st078050.finalproject.viewmodel

import android.app.Application
import android.location.Location
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class RunSummary(
    val id: String,
    val distanceMeters: Double,
    val durationSeconds: Long,
    val paceMps: Double,
    val timestampText: String
)

data class RouteUiState(
    val isTracking: Boolean = false,
    val routePoints: List<LatLng> = emptyList(),
    val distanceMeters: Double = 0.0,
    val durationSeconds: Long = 0L,
    val paceMps: Double = 0.0,
    val runHistory: List<RunSummary> = emptyList()
)

class RouteTrackingViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(application)
    private val firestore = FirebaseFirestore.getInstance()
    private val dateFormatter = SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())

    private val _uiState = MutableStateFlow(RouteUiState())
    val uiState: StateFlow<RouteUiState> = _uiState

    private var timerJob: Job? = null
    private var locationCallback: LocationCallback? = null
    private var startTime = 0L

    val mapCameraPosition = MutableStateFlow<LatLng?>(null)

    // -------------------------
    // INITIALIZE MAP + HISTORY
    // -------------------------
    fun initialize() {
        startLocationUpdates()
        loadRunHistory()
    }

    // -------------------------
    // START RUN
    // -------------------------
    fun startRun() {
        if (_uiState.value.isTracking) return

        startTime = System.currentTimeMillis()

        _uiState.value = _uiState.value.copy(
            isTracking = true,
            // reset metrics for a fresh run
            distanceMeters = 0.0,
            durationSeconds = 0L,
            paceMps = 0.0,
            routePoints = emptyList()
        )

        startTimer()
    }

    // -------------------------
    // STOP RUN  +  SAVE TO FIREBASE
    // -------------------------
    fun stopRun() {
        if (!_uiState.value.isTracking) return

        _uiState.value = _uiState.value.copy(isTracking = false)
        timerJob?.cancel()

        saveRunToFirebase()
    }

    // -------------------------
    // FIREBASE SAVE
    // -------------------------
    private fun saveRunToFirebase() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val data = hashMapOf(
            "distanceMeters" to _uiState.value.distanceMeters,
            "durationSeconds" to _uiState.value.durationSeconds,
            "paceMps" to _uiState.value.paceMps,
            "timestamp" to FieldValue.serverTimestamp()
        )

        // Store each GPS run under:
        // users/{uid}/fitness/runs/routes/{autoId}
        firestore.collection("users")
            .document(uid)
            .collection("fitness")
            .document("runs")
            .collection("routes")
            .add(data)
            .addOnSuccessListener {
                // refresh history so the new run appears in the list
                loadRunHistory()
            }
    }

    // -------------------------
    // LOAD HISTORY FROM FIREBASE
    // -------------------------
    private fun loadRunHistory() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .collection("fitness")
            .document("runs")
            .collection("routes")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { snapshot ->
                val runs = snapshot.documents.mapNotNull { doc ->
                    val dist = doc.getDouble("distanceMeters") ?: return@mapNotNull null
                    val duration = doc.getLong("durationSeconds") ?: 0L
                    val pace = doc.getDouble("paceMps") ?: 0.0
                    val ts = doc.getTimestamp("timestamp")?.toDate()
                    val tsText = ts?.let { dateFormatter.format(it) } ?: ""

                    RunSummary(
                        id = doc.id,
                        distanceMeters = dist,
                        durationSeconds = duration,
                        paceMps = pace,
                        timestampText = tsText
                    )
                }

                _uiState.value = _uiState.value.copy(runHistory = runs)
            }
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
        )
            .setMinUpdateDistanceMeters(1f)
            .build()

        if (locationCallback == null) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val newLoc = result.lastLocation ?: return
                    updateLocation(newLoc)
                }
            }
        }

        locationCallback?.let { callback ->
            fusedClient.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        locationCallback?.let { fusedClient.removeLocationUpdates(it) }
    }
}