package week11.st078050.finalproject.viewmodel

import week11.st078050.finalproject.data.repository.ActivityRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val activityRepository: ActivityRepository,
    private val currentUserId: String
) : ViewModel() {

    private val _liveSteps = MutableStateFlow(0)
    val liveSteps: StateFlow<Int> = _liveSteps.asStateFlow()

    init {
        viewModelScope.launch {
            activityRepository.getTodaySteps(currentUserId).collect { steps ->
                _liveSteps.value = steps
            }
        }
    }

}