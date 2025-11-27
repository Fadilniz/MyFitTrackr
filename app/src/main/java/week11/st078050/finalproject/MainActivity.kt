package week11.st078050.finalproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import week11.st078050.finalproject.navigation.AppNavGraph
import week11.st078050.finalproject.ui.theme.MyFitTrackrTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyFitTrackrTheme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}