package week11.st078050.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

class FitnessViewModel : ViewModel() {

    // -------- LIVE STATS --------
    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private val _calories = MutableStateFlow(0.0)
    val calories: StateFlow<Double> = _calories

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm

    // -------- WEEKLY GRAPH DATA --------
    private val _weeklySteps = MutableStateFlow(
        listOf(1200, 3450, 2890, 5000, 4200, 1500, 0) // sample initial data
    )
    val weeklySteps: StateFlow<List<Int>> = _weeklySteps

    /** Called whenever StepsScreen updates steps */
    fun updateSteps(newSteps: Int) {
        _steps.value = newSteps
        _distanceKm.value = newSteps * 0.0008
        _calories.value = newSteps * 0.04

        updateTodaySteps(newSteps)
    }

    /** Stores today's step value in the weekly list */
    private fun updateTodaySteps(todaySteps: Int) {
        val calendar = Calendar.getInstance()
        val dayIndex = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }

        val updated = _weeklySteps.value.toMutableList()
        updated[dayIndex] = todaySteps
        _weeklySteps.value = updated
    }
}
