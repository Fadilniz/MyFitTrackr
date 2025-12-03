package week11.st078050.finalproject.data.repository

import androidx.compose.runtime.staticCompositionLocalOf

val LocalFitnessViewModel = staticCompositionLocalOf<FitnessViewModel> {
    error("FitnessViewModel not provided")
}
