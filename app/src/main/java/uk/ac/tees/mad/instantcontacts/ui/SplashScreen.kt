package uk.ac.tees.mad.instantcontacts.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation

import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import androidx.navigation.NavHostController
import androidx.compose.runtime.LaunchedEffect
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.tees.mad.instantcontacts.R
import uk.ac.tees.mad.instantcontacts.Screen

@Composable
fun SplashScreen(navController: NavHostController) {

    val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.call))

    LaunchedEffect(Unit) {
        val isLoggedIn = Firebase.auth.currentUser != null
        delay(3000)
        launch(Dispatchers.Main) {
            navController.navigate(if (isLoggedIn) Screen.Home.route else Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f)  ) {

                LottieAnimation(
                    composition = composition.value,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(200.dp).align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Text(
                text = "InstantContact",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp),
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Stay Connected, Effortlessly.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
