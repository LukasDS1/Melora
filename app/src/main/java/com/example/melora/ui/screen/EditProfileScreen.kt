package com.example.melora.ui.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.ui.theme.SecondaryBg
import com.example.melora.viewmodel.EditProfileViewModel
import com.example.melora.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EditProfileScreenVm(
    vm: EditProfileViewModel,
    onExit: () -> Unit,
    onProfileUpdated: () -> Unit,
    onLogout: () -> Unit
) {

    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.success) {
        if (state.success) {
            vm.clearResult()
            onProfileUpdated()
        }
    }

    BackHandler {
        vm.resetFormToOriginalUser()
        onExit()
    }

    EditProfileScreen(
        nickname = state.nickname,
        email = state.email,
        password = state.password,
        nicknameError = state.nicknameError,
        emailError = state.emailError,
        passwordError = state.passwordError,
        profilePicture = state.profilePictureUrl,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        success = state.success,
        errorMessage = state.errorMessage,
        onNicknameChange = vm::onNicknameChange,
        onEmailChange = vm::onEmailChange,
        onPasswordChange = vm::onPasswordChange,
        onPictureChange = vm::onProfilePictureChange,
        onSubmit = vm::submitChanges,
        resetForm = vm::resetFormToOriginalUser,
        onExit = onExit,
        onLogoutClick = {
            vm.resetFormToOriginalUser()
            vm.logout(onLogout) }
    )
}

@Composable
fun EditProfileScreen(
    nickname: String,
    email: String,
    password: String,
    nicknameError: String?,
    emailError: String?,
    passwordError: String?,
    profilePicture: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    success: Boolean,
    errorMessage: String?,
    onNicknameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPictureChange: (String?) -> Unit,
    onSubmit: () -> Unit,
    resetForm: () -> Unit,
    onExit: () -> Unit,
    onLogoutClick: () -> Unit

) {
    // fun to make a temporary file where the camera will save the photo
    // fun to get Uri of the file using FileProvider
    val bg = PrimaryBg
    var showPass by remember { mutableStateOf(false) }

    fun createPermanentImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(context.filesDir, "images").apply {
            if (!exists()) mkdirs()
        }
        return File(storageDir, "IMG_$timeStamp.jpg")
    }

    fun savePermanentImage(context: Context, uri: Uri): String? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = "profile_${System.currentTimeMillis()}.jpg"
        val outputFile = File(context.filesDir, "images/$fileName").apply { parentFile?.mkdirs() }
        inputStream.use { input -> outputFile.outputStream().use { output -> input.copyTo(output) } }
        return outputFile.absolutePath
    }


    fun getImageUriFile(context: Context, file: File): Uri {
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    // Launchers for camera and gallery
    val context = LocalContext.current

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCaptureUri?.let { uri ->
                onPictureChange(uri.path)
            }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = savePermanentImage(context, it)
            onPictureChange(savedPath)
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFfef7ff),
                        bg
                    )
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        resetForm()
                        onExit()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Go back button"
                    )
                }
                Text(
                    text = "Edit Profile",
                    color = Resaltado,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 48.dp, top = 6.dp),
                    textAlign = TextAlign.Center
                )
            }


            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = profilePicture?.let { path -> Uri.fromFile(File(path)) },
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(50.dp)),
                    placeholder = painterResource(id = R.drawable.defaultprofilepicture),
                    error = painterResource(id = R.drawable.defaultprofilepicture),
                    contentScale = ContentScale.Crop
                )
                Column{
                    IconButton(onClick = {
                        val file = createPermanentImageFile(context)
                        val uri = getImageUriFile(context, file)
                        pendingCaptureUri = uri
                        takePictureLauncher.launch(uri)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Camera icon"
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    IconButton(onClick = {
                        pickImageLauncher.launch("image/*")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = "Gallery icon"
                        )
                    }
                }


            }


            Spacer(Modifier.height(20.dp))

            // NICKNAME
            OutlinedTextField(
                value = nickname,
                onValueChange = onNicknameChange,
                placeholder = { Text("New nickname") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Account icon",
                        tint = Color.Gray
                    )
                },
                shape = RoundedCornerShape(30.dp),
                isError = nicknameError != null,
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = SecondaryBg,
                    focusedIndicatorColor = Resaltado,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(Modifier.height(10.dp))
            if (nicknameError != null)
                Text(nicknameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)

            Spacer(Modifier.height(30.dp))

            // EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = { Text("New email") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email icon",
                        tint = Color.Gray
                    )
                },
                isError = emailError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = SecondaryBg,
                    focusedIndicatorColor = Resaltado,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(Modifier.height(10.dp))
            if (emailError != null)
                Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)

            Spacer(Modifier.height(30.dp))

            // PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("New password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Lock icon",
                        tint = Color.Gray
                    )
                },
                isError = passwordError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(30.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = SecondaryBg,
                    focusedIndicatorColor = Resaltado,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton( onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Hide password" else "Show password"
                        )
                    }
                }
            )
            Spacer(Modifier.height(10.dp))
            if (passwordError != null)
                Text(passwordError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)

            Spacer(Modifier.height(10.dp))

            if (errorMessage != null)
                Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)

            if (success)
                Text("Saved changes.", color = Color.Green)

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                colors = ButtonDefaults.buttonColors(disabledContainerColor = Color(0xFF5C8374), containerColor = Color(0xFF93B1A6)),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
            ) {
                Text(if (isSubmitting) "Saving..." else "Save changes", color = Color.White)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF660B05)),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
            ) {
                Text("Log out", color = Color.White)
            }
        }

    }


}