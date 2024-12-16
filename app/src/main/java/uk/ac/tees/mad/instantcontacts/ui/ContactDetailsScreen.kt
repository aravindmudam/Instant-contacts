package uk.ac.tees.mad.instantcontacts.ui


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.ui.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    navController: NavHostController,
    contactId: String,
    contactViewModel: ContactViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val contactState = contactViewModel.contactState.collectAsState().value
    val isExpanded = remember { mutableStateOf(false) }
    val deleteContactState = contactViewModel.deleteState.collectAsState().value
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = contactId) {
        contactViewModel.fetchContactById(contactId)
    }

    LaunchedEffect(key1 = deleteContactState) {
        when (deleteContactState) {
            is Resource.Success -> {
                navController.popBackStack()
            }

            is Resource.Error -> {
                Toast.makeText(context, deleteContactState.exception.message, Toast.LENGTH_SHORT)
                    .show()
            }

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Contact Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showDeleteConfirmation = true
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Contact")
                    }
                }
            )
        }

    ) { paddingValues ->
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            try {
                                val contact = (contactState as Resource.Success).data
                                contactViewModel.deleteContact(contact)
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text(text = "Delete", color = Color.Red)
                    }
                },
                title = {
                    Text(text = "Delete Contact")
                },
                text = {
                    Text(text = "Are you sure you want to delete this contact?")
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirmation = false }
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }
        when (contactState) {
            is Resource.Error -> {
                val error = contactState.exception.message ?: "Unexpected error"
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }

            Resource.Idle -> Unit

            Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is Resource.Success -> {
                val contact = contactState.data
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image
                    Image(
                        painter = rememberAsyncImagePainter(contact.imageUrl),
                        contentDescription = "Contact Image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contact Details
                    Text(
                        text = contact.name,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = contact.phone,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ActionButton(
                            icon = Icons.Default.Phone,
                            label = "Call",
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${contact.phone}")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ActionButton(
                            icon = Icons.Default.Message,
                            label = "Message",
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("smsto:${contact.phone}")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ActionButton(
                            icon = Icons.Default.ShareLocation,
                            label = "Share Location",
                            onClick = {
                                val gmmIntentUri = Uri.parse("geo:0,0?q=current+location")
                                val mapIntent =
                                    Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                context.startActivity(mapIntent)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Email: ${contact.email}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Relationship: ${contact.relationship}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Notes: ${contact.notes}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Call History",
                                style = MaterialTheme.typography.titleMedium
                            )
                            TextButton(onClick = { isExpanded.value = !isExpanded.value }) {
                                Text(text = if (isExpanded.value) "Show Less" else "View All")
                            }
                        }
                        val historySize = contact.callHistory.size
                        if (historySize == 0) {
                            Text(text = "No call history available.")
                        }
                        AnimatedVisibility(visible = isExpanded.value) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Column {

                                contact.callHistory
                                    .forEach { call ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Icon(
                                                imageVector = Icons.Default.ArrowOutward,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = call.timestamp,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                            }
                        }

                        AnimatedVisibility(visible = !isExpanded.value) {
                            Column {

                                contact.callHistory.slice(0..if (historySize > 3) 3 else historySize - 1)
                                    .forEach { call ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {

                                            Icon(
                                                imageVector = Icons.Default.ArrowOutward,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = call.timestamp,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}