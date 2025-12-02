package week11.st078050.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FitnessViewModel : ViewModel() {

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps

    private val _calories = MutableStateFlow(0.0)
    val calories: StateFlow<Double> = _calories

    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm

    /** Updates from StepsScreen */
    fun updateSteps(newSteps: Int) {
        _steps.value = newSteps
        _distanceKm.value = newSteps * 0.0008      // simple formula
        _calories.value = newSteps * 0.04          // simple formula
    }
}
