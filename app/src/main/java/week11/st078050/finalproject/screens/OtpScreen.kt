package week11.st078050.finalproject.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import week11.st078050.finalproject.ui.theme.components.GradientBackground
import week11.st078050.finalproject.ui.theme.*

@Composable
fun OtpScreen(
    onBackClick: () -> Unit = {},
    onVerifyClick: () -> Unit = {},
    onResendClick: () -> Unit = {}
) {
    var otp1 by remember { mutableStateOf("") }
    var otp2 by remember { mutableStateOf("") }
    var otp3 by remember { mutableStateOf("") }
    var otp4 by remember { mutableStateOf("") }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // BACK BUTTON
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextLightGrey,
                modifier = Modifier
                    .align(Alignment.Start)
                    .size(30.dp)
                    .clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.height(30.dp))

            // TITLE
            Text(
                text = "OTP Verification",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Enter the verification code we just sent on\nyour email address.",
                color = TextGrey,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // OTP BOXES ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OtpBox(value = otp1, onChange = { if (it.length <= 1) otp1 = it })
                OtpBox(value = otp2, onChange = { if (it.length <= 1) otp2 = it })
                OtpBox(value = otp3, onChange = { if (it.length <= 1) otp3 = it })
                OtpBox(value = otp4, onChange = { if (it.length <= 1) otp4 = it })
            }

            Spacer(modifier = Modifier.height(40.dp))

            // VERIFY BUTTON
            Button(
                onClick = onVerifyClick,
                colors = ButtonDefaults.buttonColors(containerColor = WhiteButton),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Verify", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // RESEND LINK
            Row {
                Text("Didn’t receive code? ", color = TextLightGrey)

                Text(
                    text = "Resend",
                    color = YellowAccent,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onResendClick() }
                )
            }
        }
    }
}

@Composable
fun OtpBox(value: String, onChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 22.sp,
            color = TextWhite,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier
            .size(60.dp)
            .border(1.dp, TextLightGrey)
            .background(Color.Transparent),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = YellowAccent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}
