package week11.st078050.finalproject.data.repository

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class FitnessViewModel : ViewModel() {

    // LIVE VALUES
    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private val _calories = MutableStateFlow(0.0)
    val calories: StateFlow<Double> = _calories

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm

    // WEEKLY STEPS STORAGE
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

    // UPDATE FROM SENSOR
    fun updateSteps(newSteps: Int) {
        _steps.value = newSteps
        _distanceKm.value = newSteps * 0.0008
        _calories.value = newSteps * 0.04

        stepProgress.value = (newSteps / dailyStepGoal.toFloat()).coerceIn(0f, 1f)
        calorieProgress.value = (_calories.value / calorieGoal).toFloat().coerceIn(0f, 1f)
        distanceProgress.value = (_distanceKm.value / distanceGoal).toFloat().coerceIn(0f, 1f)

        // Which day is today?
        val today = LocalDate.now()
            .dayOfWeek
            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            .substring(0, 3) // "Mon", "Tue"...

        // IMPORTANT: Create a NEW map so Compose recomposes
        weeklySteps.value = weeklySteps.value.toMutableMap().also {
            it[today] = newSteps
        }
    }
}
