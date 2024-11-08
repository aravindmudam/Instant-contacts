package uk.ac.tees.mad.instantcontacts

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uk.ac.tees.mad.instantcontacts.ui.SplashScreen

@Composable
fun InstantContactNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(route = Screen.Login.route) {

        }
        composable(route = Screen.Register.route) {

        }
        composable(route = Screen.Home.route) {

        }
        composable(route = Screen.ContactDetail.route) {

        }
        composable(route = Screen.Profile.route) {

        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ContactDetail : Screen("contact_detail")
    object Profile : Screen("profile")
}

