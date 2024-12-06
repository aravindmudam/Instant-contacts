package uk.ac.tees.mad.instantcontacts

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uk.ac.tees.mad.instantcontacts.ui.AddEditContactScreen
import uk.ac.tees.mad.instantcontacts.ui.ContactDetailsScreen
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
            AddEditContactScreen(navController = navController)
        }

        composable(
            route = Screen.ContactDetail.route + "/{contactId}",
            arguments = listOf(navArgument("contactId") { type = NavType.StringType })
        ) {
            val contactId = navController.currentBackStackEntry?.arguments?.getString("contactId")
                ?: return@composable
            ContactDetailsScreen(navController = navController, contactId = contactId)
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
    object AddContact : Screen("add_contact")
    object Profile : Screen("profile")
}

