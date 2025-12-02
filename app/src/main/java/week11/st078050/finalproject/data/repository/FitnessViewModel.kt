package week11.st078050.finalproject.data.repository

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FitnessViewModel : ViewModel() {

    // -----------------------------
    // LIVE READINGS
    // -----------------------------
    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private val _calories = MutableStateFlow(0.0)
    val calories: StateFlow<Double> = _calories

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm

    // -----------------------------
    // USER GOALS
    // -----------------------------
    private val dailyStepGoal = 10000
    private val calorieGoal = 400.0
    private val distanceGoal = 6.0

    // -----------------------------
    // LIVE PROGRESS FOR RINGS
    // -----------------------------
    val stepProgress = MutableStateFlow(0f)
    val calorieProgress = MutableStateFlow(0f)
    val distanceProgress = MutableStateFlow(0f)

    // Called from StepSensorListener
    fun updateSteps(newSteps: Int) {
        _steps.value = newSteps

        _distanceKm.value = newSteps * 0.0008          // 1 step ≈ 0.8m
        _calories.value = newSteps * 0.04              // simple kcal formula

        stepProgress.value =
            (newSteps / dailyStepGoal.toFloat()).coerceIn(0f, 1f)

        calorieProgress.value =
            (_calories.value / calorieGoal).toFloat().coerceIn(0f, 1f)

        distanceProgress.value =
            (_distanceKm.value / distanceGoal).toFloat().coerceIn(0f, 1f)
    }
}
