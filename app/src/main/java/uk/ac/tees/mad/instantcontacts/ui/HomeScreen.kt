package uk.ac.tees.mad.instantcontacts.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import uk.ac.tees.mad.instantcontacts.R
import uk.ac.tees.mad.instantcontacts.Screen
import uk.ac.tees.mad.instantcontacts.domain.Contact
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.ui.viewmodel.ContactViewModel
import kotlin.math.roundToInt

@Composable
fun HomeScreen(navController: NavHostController, contactViewModel: ContactViewModel = viewModel()) {
    val contactsState by contactViewModel.contactsState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        contactViewModel.fetchContacts()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.AddContact.route)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { inn ->
        Box(modifier = Modifier.padding(inn)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.White),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Emergency Contacts",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when (contactsState) {
                    is Resource.Idle -> {

                    }

                    is Resource.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    is Resource.Success -> {
                        val contacts = (contactsState as Resource.Success<List<Contact>>).data
                        if (contacts.isEmpty()) {
                            Text(
                                text = "No contacts found.",
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(contacts) { contact ->
                                    ContactItem(contact = contact, onClick = {
                                        navController.navigate("${Screen.ContactDetail.route}/${contact.id}")
                                    }, onCallClick = {
                                        Log.d("ONNNN", "on call invoked")
                                        val intent = Intent(Intent.ACTION_DIAL)
                                        intent.data = Uri.parse("tel:${contact.phone}")
                                        context.startActivity(intent)
                                        contactViewModel.addCallHistory(contact.id, contact.phone)
                                    })
                                }
                            }
                        }
                    }

                    is Resource.Error -> {
                        val exception = (contactsState as Resource.Error).exception
                        Text(
                            text = "Error: ${exception.message}",
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = {
                    navController.navigate(Screen.Profile.route)
                }) {
                    Text(
                        "View Profile and Medical Information",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

}

@Composable
fun ContactItem(contact: Contact, onClick: () -> Unit, onCallClick: () -> Unit) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red.copy(0.7f))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        scope.launch {

                            offsetX.stop()
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            if (offsetX.value < -50f) {
                                onCallClick()
                                offsetX.animateTo(0f)
                            } else {
                                offsetX.animateTo(0f)
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            val newOffset = (offsetX.value + dragAmount.x).coerceIn(-200f, 0f)
                            offsetX.snapTo(newOffset)
                        }
                    }
                )
            }
            .offset { IntOffset(offsetX.value.roundToInt(), 0) }
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)

                .background(Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                contact.imageUrl?.let {
                    Image(
                        painter = rememberAsyncImagePainter(contact.imageUrl),
                        contentDescription = null,
                        modifier = Modifier.size(90.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = contact.name, fontWeight = FontWeight.Medium, fontSize = 20.sp)
                    Text(text = contact.phone, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        AnimatedVisibility(
            visible = offsetX.value < -50f,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .zIndex(1f)
        ) {
            val composition = rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.call))

            LottieAnimation(
                composition = composition.value,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
            )
//            Icon(
//                imageVector = Icons.Filled.Call,
//                contentDescription = "Call",
//                tint = Color.Red,
//                modifier = Modifier.size(30.dp)
//            )
        }
    }
}
