package week11.st078050.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import week11.st078050.finalproject.screens.*

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") },
                onGuestClick = { /* later */ }
            )
        }

        composable("login") {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { /* later */ },
                onForgotPasswordClick = { navController.navigate("forgot") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate("login") }
            )
        }

        composable("forgot") {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onSendClick = { navController.navigate("otp") },
                onLoginClick = { navController.navigate("login") }
            )
        }

        composable("otp") {
            OtpScreen(
                onBackClick = { navController.popBackStack() },
                onVerifyClick = { navController.navigate("createNewPassword") },
                onResendClick = { /* resend code */ }
            )
        }

        composable("createNewPassword") {
            CreateNewPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onResetClick = { navController.navigate("passwordChanged") }
            )
        }

        composable("passwordChanged") {
            PasswordChangedScreen(
                onLoginClick = { navController.navigate("login") }
            )
        }
    }
}
