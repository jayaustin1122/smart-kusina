import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.smartkusina.presentation.auth.LoginScreen
import com.dev.smartkusina.presentation.auth.LoginViewModel
import com.dev.smartkusina.presentation.auth.state.AuthState
import com.dev.smartkusina.presentation.home.HomeScreen
import com.dev.smartkusina.presentation.main.SplashScreen
import kotlinx.coroutines.delay

@Composable
fun SmartKusinaApp() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val authState by loginViewModel.authState.collectAsState()

    var showSplash by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(5000)
        showSplash = false
    }

    val startDestination = when {
        showSplash -> "splash"
        authState is AuthState.Authenticated -> "home"
        else -> "login"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("splash") {
            SplashScreen()
        }

        composable("login") {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}
