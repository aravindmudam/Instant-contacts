package uk.ac.tees.mad.instantcontacts.ui


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import uk.ac.tees.mad.instantcontacts.Screen
import uk.ac.tees.mad.instantcontacts.domain.Resource
import uk.ac.tees.mad.instantcontacts.domain.UserProfile
import uk.ac.tees.mad.instantcontacts.ui.viewmodel.ProfileViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val profileState by profileViewModel.profileState.collectAsState()
    val nameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val phoneState = remember { mutableStateOf("") }
    val imageUrlState = remember { mutableStateOf<String?>(null) }
    val imageUriState = remember { mutableStateOf<Uri?>(null) }
    val photoFile = remember { File(context.cacheDir, "contact_photo.jpg") }
    val photoUri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                imageUriState.value = photoUri
            }
        }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUriState.value = uri
        }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val storagePermissionState =
        rememberPermissionState(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(profileState) {
        if (profileState is Resource.Success) {
            val profile = (profileState as Resource.Success).data
            nameState.value = profile.name
            emailState.value = profile.email
            imageUrlState.value = profile.profileImageUrl
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = bottomSheetState,
                windowInsets = WindowInsets.ime
            ) {
                ImageSelector(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onCameraClick = {
                        coroutineScope.launch { bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        if (cameraPermissionState.status.isGranted) {
                            cameraLauncher.launch(photoUri)
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                            storagePermissionState.launchPermissionRequest()
                        }
                    },
                    onGalleryClick = {
                        coroutineScope.launch { bottomSheetState.hide() }
                            .invokeOnCompletion {
                                if (!bottomSheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        imagePickerLauncher.launch("image/*")
                    })
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Image
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable {
                        // Open image picker
                        val intent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        val activity = context as Activity
                        activity.startActivityForResult(intent, 1001)
                    }
            ) {
                imageUriState.value?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } ?: imageUrlState.value?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } ?: Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Name Field
            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = phoneState.value,
                onValueChange = { phoneState.value = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    val updatedProfile = UserProfile(
                        id = (profileState as Resource.Success).data.id,
                        name = nameState.value,
                        email = emailState.value,
                        profileImageUrl = imageUrlState.value,
                        phone = phoneState.value
                    )
                    profileViewModel.updateProfile(updatedProfile)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
