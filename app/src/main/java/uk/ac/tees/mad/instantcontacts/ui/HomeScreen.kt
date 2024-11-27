package uk.ac.tees.mad.instantcontacts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import uk.ac.tees.mad.instantcontacts.Screen
import uk.ac.tees.mad.instantcontacts.domain.Contact
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.ui.viemodel.ContactViewModel

@Composable
fun HomeScreen(navController: NavHostController, contactViewModel: ContactViewModel = viewModel()) {
    val contactsState by contactViewModel.contactsState.collectAsState()

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
                            LazyColumn {
                                items(contacts) { contact ->
                                    ContactItem(contact = contact, onClick = {
//                                        navController.navigate("${Screen.ContactDetail.route}/${contact.id}")
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
fun ContactItem(contact: Contact, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            contact.imageUrl?.let {
                AsyncImage(
                    model = ImageRequest
                        .Builder(LocalContext.current)
                        .data(it)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Crop
                )
            } ?: Icon(
                painter = rememberAsyncImagePainter("https://ui-avatars.com/api/?name=${contact.name}"),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = contact.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = contact.phone, style = MaterialTheme.typography.bodyMedium)
                contact.email?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}