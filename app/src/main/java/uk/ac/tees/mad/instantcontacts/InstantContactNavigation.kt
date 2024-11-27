package uk.ac.tees.mad.instantcontacts

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uk.ac.tees.mad.instantcontacts.ui.HomeScreen
import uk.ac.tees.mad.instantcontacts.ui.LoginScreen
import uk.ac.tees.mad.instantcontacts.ui.RegisterScreen
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
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.AddContact.route) {

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
    object AddContact: Screen("add_contact")
    object Profile : Screen("profile")
}

