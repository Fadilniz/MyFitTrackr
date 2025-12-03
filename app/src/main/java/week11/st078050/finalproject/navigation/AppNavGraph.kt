package week11.st078050.finalproject.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import week11.st078050.finalproject.screens.*
import week11.st078050.finalproject.viewmodel.RouteTrackingViewModel

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
                onGuestClick = { navController.navigate("home") }
            )
        }

        composable("login") {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate("home") },
                onForgotPasswordClick = { navController.navigate("forgot") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = { navController.navigate("home") },
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
                onResendClick = {}
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

        composable("steps") {
            StepsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("route_tracking") {
            val vm: RouteTrackingViewModel = viewModel()
            RouteTrackingScreen(
                viewModel = vm,
                onBackClick = { navController.popBackStack() }
            )
        }


        composable("pose") {
            PoseDetectionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                onStepsClick = { navController.navigate("steps") },
                onStartRoute = { navController.navigate("route_tracking") },
                onStartPoseDetection = { navController.navigate("pose") },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onProfileClick = { navController.navigate("profile") }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
