package uk.ac.tees.mad.instantcontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.instantcontacts.ui.theme.InstantContactsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstantContactsTheme {
                val navController = rememberNavController()
                InstantContactNavigation(navController = navController)
            }
        }
    }
}