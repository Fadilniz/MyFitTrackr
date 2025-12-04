package week11.st078050.finalproject.data.repository

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class FitnessViewModel : ViewModel() {

    // 🔥 Firestore
    private val firestore = FirebaseFirestore.getInstance()
    private var currentUserId: String? = null

    // LIVE VALUES (TODAY)
    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private val _calories = MutableStateFlow(0.0)
    val calories: StateFlow<Double> = _calories

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm

    // WEEKLY STEPS (Mon..Sun)
    val weeklySteps = MutableStateFlow(
        mutableMapOf(
            "Mon" to 0, "Tue" to 0, "Wed" to 0,
            "Thu" to 0, "Fri" to 0, "Sat" to 0, "Sun" to 0
        )
    )

    // GOALS
    private val dailyStepGoal = 10000
    private val calorieGoal = 400.0
    private val distanceGoal = 6.0

    val stepProgress = MutableStateFlow(0f)
    val calorieProgress = MutableStateFlow(0f)
    val distanceProgress = MutableStateFlow(0f)

    // 🔹 Call this from LoginScreen when login succeeds
    fun onUserLogin(uid: String) {
        currentUserId = uid
        loadUserFitnessData(uid)
    }

    // 🔹 Call this from Logout click (before navigating away)
    fun onUserLogout() {
        currentUserId = null
        resetInMemory()
    }

    // Increase steps by a delta from the sensor
    fun addSteps(delta: Int) {
        if (delta <= 0) return  // ignore noise / backwards
        val newSteps = _steps.value + delta
        updateFromAbsoluteSteps(newSteps)
        saveUserFitnessData()
    }

    // --- INTERNAL HELPERS ---

    private fun resetInMemory() {
        _steps.value = 0
        _calories.value = 0.0
        _distanceKm.value = 0.0

        stepProgress.value = 0f
        calorieProgress.value = 0f
        distanceProgress.value = 0f

        weeklySteps.value = mutableMapOf(
            "Mon" to 0, "Tue" to 0, "Wed" to 0,
            "Thu" to 0, "Fri" to 0, "Sat" to 0, "Sun" to 0
        )
    }

    private fun updateFromAbsoluteSteps(newSteps: Int) {
        _steps.value = newSteps
        _distanceKm.value = newSteps * 0.0008
        _calories.value = newSteps * 0.04

        stepProgress.value = (newSteps / dailyStepGoal.toFloat()).coerceIn(0f, 1f)
        calorieProgress.value =
            (_calories.value / calorieGoal).toFloat().coerceIn(0f, 1f)
        distanceProgress.value =
            (_distanceKm.value / distanceGoal).toFloat().coerceIn(0f, 1f)

        // Which day is today? ("Mon", "Tue", ...)
        val today = LocalDate.now()
            .dayOfWeek
            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            .substring(0, 3)

        // Update weekly map
        weeklySteps.value = weeklySteps.value.toMutableMap().also {
            it[today] = newSteps
        }
    }

    private fun loadUserFitnessData(uid: String) {
        val docRef = firestore
            .collection("users")
            .document(uid)
            .collection("fitness")
            .document("summary")

        docRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null && snapshot.exists()) {
                    val savedSteps = (snapshot.getLong("steps") ?: 0L).toInt()
                    val weekly = snapshot.get("weeklySteps") as? Map<*, *>

                    // Apply loaded values
                    updateFromAbsoluteSteps(savedSteps)

                    if (weekly != null) {
                        val converted = weekly.mapNotNull { (k, v) ->
                            val key = k as? String ?: return@mapNotNull null
                            val value = (v as? Number)?.toInt() ?: 0
                            key to value
                        }.toMap()

                        weeklySteps.value = weeklySteps.value.toMutableMap().also {
                            it.putAll(converted)
                        }
                    }
                } else {
                    // No data yet for this user -> start clean
                    resetInMemory()
                }
            }
            .addOnFailureListener {
                // If Firestore fails, keep current defaults
            }
    }

    private fun saveUserFitnessData() {
        val uid = currentUserId ?: return

        val docRef = firestore
            .collection("users")
            .document(uid)
            .collection("fitness")
            .document("summary")

        val data = mapOf(
            "steps" to _steps.value,
            "calories" to _calories.value,
            "distanceKm" to _distanceKm.value,
            "weeklySteps" to weeklySteps.value
        )

        docRef.set(data, SetOptions.merge())
    }
}
