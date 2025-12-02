package week11.st078050.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import week11.st078050.finalproject.navigation.AppNavGraph
import week11.st078050.finalproject.ui.theme.MyFitTrackrTheme
import week11.st078050.finalproject.viewmodel.FitnessViewModel
import week11.st078050.finalproject.viewmodel.LocalFitnessViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyFitTrackrTheme {

                val fitnessVM: FitnessViewModel = viewModel()
                val navController = rememberNavController()

                CompositionLocalProvider(
                    LocalFitnessViewModel provides fitnessVM
                ) {
                    AppNavGraph(navController)
                }
            }
        }
    }
}
