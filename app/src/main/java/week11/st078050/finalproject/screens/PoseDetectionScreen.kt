package week11.st078050.finalproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FaceRetouchingNatural
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import week11.st078050.finalproject.ui.theme.TextGrey
import week11.st078050.finalproject.ui.theme.TextWhite
import week11.st078050.finalproject.ui.theme.YellowAccent
import week11.st078050.finalproject.ui.theme.components.GradientBackground

@Composable
fun PoseDetectionScreen(
    onBackClick: () -> Unit = {}
) {

    GradientBackground {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // 🔙 Back Arrow
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(32.dp)
                    .clickable { onBackClick() }
            )

            // 📸 CAMERA PLACEHOLDER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0x33222222)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Camera",
                    tint = YellowAccent,
                    modifier = Modifier.size(60.dp)
                )
            }

            // 🟨 BOTTOM PANEL
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color(0x22111111))
                    .padding(20.dp)
            ) {

                Text(
                    text = "Pose Detection",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Align your body inside the frame",
                    color = TextGrey,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { /* Start camera later */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YellowAccent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Start Detection",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
