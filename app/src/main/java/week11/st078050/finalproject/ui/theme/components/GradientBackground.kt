package week11.st078050.finalproject.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import week11.st078050.finalproject.ui.theme.DarkPurple
import week11.st078050.finalproject.ui.theme.DarkPurple2

@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkPurple,
                        DarkPurple2
                    )
                )
            )
    ) {
        content()
    }
}
