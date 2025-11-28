package week11.st078050.finalproject.viewmodel

// ... imports for ViewModel, StateFlow, Coroutines ...
import week11.st078050.finalproject.data.repository.ActivityRepository
// ... other imports
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

    // State to hold and observe the live step count (uses Flow from Repository)
    private val _liveSteps = MutableStateFlow(0)
    val liveSteps: StateFlow<Int> = _liveSteps.asStateFlow()

    init {
        viewModelScope.launch {
            // Collects the real-time data from the Repository Flow
            activityRepository.getTodaySteps(currentUserId).collect { steps ->
                _liveSteps.value = steps
            }
        }
    }
    // ... functions to update UI, like fetching daily stats ...
}