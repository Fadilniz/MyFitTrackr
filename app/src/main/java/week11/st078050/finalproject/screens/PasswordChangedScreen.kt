package week11.st078050.finalproject.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.components.GradientBackground
import week11.st078050.finalproject.ui.theme.*

@Composable
fun PasswordChangedScreen(
    onLoginClick: () -> Unit = {}
) {
    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = YellowAccent,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Password Changed!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your password has been changed\nsuccessfully.",
                color = TextGrey,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(containerColor = WhiteButton),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Back to Login", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
