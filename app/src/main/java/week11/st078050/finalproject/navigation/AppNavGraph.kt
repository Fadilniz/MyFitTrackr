package week11.st078050.finalproject.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import week11.st078050.finalproject.screens.*

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        // SPLASH
        composable("splash") {
            SplashScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") },
                onGuestClick = { navController.navigate("home") }
            )
        }

        // LOGIN
        composable("login") {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = {
                    navController.navigate("home")
                },
                onForgotPasswordClick = { navController.navigate("forgot") },
                onRegisterClick = { navController.navigate("register") }
            )
        }

        // REGISTER
        composable("register") {
            RegisterScreen(
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("home")
                },
                onLoginClick = { navController.navigate("login") }
            )
        }

        // FORGOT PASSWORD
        composable("forgot") {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onSendClick = { navController.navigate("otp") },
                onLoginClick = { navController.navigate("login") }
            )
        }

        // OTP
        composable("otp") {
            OtpScreen(
                onBackClick = { navController.popBackStack() },
                onVerifyClick = { navController.navigate("createNewPassword") },
                onResendClick = {}
            )
        }

        // CREATE NEW PASSWORD
        composable("createNewPassword") {
            CreateNewPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onResetClick = { navController.navigate("passwordChanged") }
            )
        }

        // PASSWORD CHANGED SUCCESS
        composable("passwordChanged") {
            PasswordChangedScreen(
                onLoginClick = { navController.navigate("login") }
            )
        }

        // DAILY STEPS DETAIL
        composable("steps") {
            StepsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ROUTE TRACKING
        composable("track") {
            RouteTrackingScreen(
                onBack = { navController.popBackStack() }
            )
        }


        // POSE DETECTION
        composable("pose") {
            PoseDetectionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // HOME
        composable("home") {
            HomeScreen(
                onStepsClick = { navController.navigate("steps") },
                onStartRoute = { navController.navigate("track") },
                onStartPoseDetection = { navController.navigate("pose") },
                onLogout = {
                    // FirebaseAuth.getInstance().signOut()  // if you want
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onProfileClick = { navController.navigate("profile") },
                onSensorDashboardClick = { navController.navigate("sensorDashboard") }
            )
        }

        // PROFILE
        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // SENSOR DASHBOARD
        composable("sensorDashboard") {
            SensorDashboardScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
