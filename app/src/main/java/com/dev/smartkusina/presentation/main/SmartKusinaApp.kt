import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.dev.smartkusina.presentation.auth.LoginScreen
import com.dev.smartkusina.presentation.auth.LoginViewModel
import com.dev.smartkusina.presentation.auth.state.AuthState
import com.dev.smartkusina.presentation.HomeScreen
import com.dev.smartkusina.presentation.home.HomeViewModel
import com.dev.smartkusina.presentation.home.RecipeDetailScreen
import com.dev.smartkusina.presentation.home.RecipeDetailViewModel
import com.dev.smartkusina.presentation.home.SpoonRecipeDetails
import com.dev.smartkusina.presentation.ingredients.IngredientsScreen
import com.dev.smartkusina.presentation.main.SplashScreen
import kotlinx.coroutines.delay
import java.lang.reflect.Modifier

@Composable
fun SmartKusinaApp() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val authState by loginViewModel.authState.collectAsState()

    var showSplash by remember { mutableStateOf(true) }

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
                },
                onNavigateToRecipeDetail = { recipeId ->
                    navController.navigate("recipeDetail/$recipeId")
                },
                onNavigateToSpoonDetails = {
                    navController.navigate("spoonDetail/$it")
                    },
                onNavtoIngridients = {
                    navController.navigate("ingredients")
                }
            )
        }

        composable("recipeDetail/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")
            if (recipeId != null) {
                val viewModel: RecipeDetailViewModel = hiltViewModel()
                viewModel.fetchMealDetails(recipeId)
                RecipeDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    mealId = recipeId
                )
            }
        }

        composable("spoonDetail/{mealId}") {
            val parentEntry = remember(it) {
                navController.getBackStackEntry("home")
            }
            val selectedRecipeViewModel: HomeViewModel = hiltViewModel(parentEntry)
            val recipe = selectedRecipeViewModel.selectedRecipe

            if (recipe != null) {
                SpoonRecipeDetails(
                    recipe = recipe,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                Column {
                    Text(text = "Recipe not found")
                }
            }
        }

        composable("ingredients") {
            IngredientsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}