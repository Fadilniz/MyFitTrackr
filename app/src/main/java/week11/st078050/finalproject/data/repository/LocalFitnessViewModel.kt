package week11.st078050.finalproject.viewmodel

import androidx.compose.runtime.staticCompositionLocalOf
import week11.st078050.finalproject.data.repository.FitnessViewModel


val LocalFitnessViewModel = staticCompositionLocalOf<FitnessViewModel> {
    error("FitnessViewModel not provided")
}
