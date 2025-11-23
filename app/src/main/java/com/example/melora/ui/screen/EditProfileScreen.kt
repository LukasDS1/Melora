package com.example.melora.ui.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PlayfairDisplay
import com.example.melora.ui.theme.ResaltadoNegative
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// -----------------------------
//  BASE64 UTILITIES
// -----------------------------
fun fileToBase64(path: String): String {
    val file = File(path)
    val bytes = file.readBytes()
    return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
}

fun decodeBase64Image(base64: String): ByteArray? {
    return try {
        android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
    } catch (e: Exception) {
        null
    }
}

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

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (state.canSubmit) showExitDialog = true
        else {
            vm.resetFormToOriginalUser()
            onExit()
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Cambios sin guardar") },
            text = { Text("Tienes cambios sin guardar. ¿Seguro que deseas salir sin guardar?") },
            confirmButton = {
                Button(
                    onClick = {
                        showExitDialog = false
                        vm.resetFormToOriginalUser()
                        onExit()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
                ) { Text("Salir sin guardar", color = Color.White) }
            },
            dismissButton = {
                Button(
                    onClick = { showExitDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Resaltado)
                ) { Text("Cancelar", color = Color.White) }
            }
        )
    }

    EditProfileScreen(
        nickname = state.nickname,
        email = state.email,
        password = state.password,
        confirmPass = state.confirmPassword,
        nicknameError = state.nicknameError,
        emailError = state.emailError,
        passwordError = state.passwordError,
        passwordConfirmErr = state.passwordConfirmError,
        profilePicture = state.profilePictureUrl,
        currentPassword = state.currentPassword,
        currentPasswordError = state.currentPasswordError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        success = state.success,
        errorMessage = state.errorMessage,
        onNicknameChange = vm::onNicknameChange,
        onEmailChange = vm::onEmailChange,
        onPasswordChange = vm::onPasswordChange,
        onConfirmPasswordChange = vm::onConfirmPasswordChange,
        onCurrentPasswordChange = vm::onCurrentPasswordChange,
        onPictureChange = vm::onProfilePictureChange,
        onSubmit = vm::submitChanges,
        resetForm = vm::resetFormToOriginalUser,
        onExit = onExit,
        onLogoutClick = {
            vm.resetFormToOriginalUser()
            vm.logout(onLogout)
        }
    )
}

@Composable
fun EditProfileScreen(
    nickname: String,
    email: String,
    password: String,
    confirmPass: String,
    nicknameError: String?,
    emailError: String?,
    passwordError: String?,
    passwordConfirmErr: String?,
    profilePicture: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    success: Boolean,
    currentPassword : String,
    currentPasswordError : String?,
    errorMessage: String?,
    onNicknameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onCurrentPasswordChange: (String) -> Unit,
    onPictureChange: (String?) -> Unit,
    onSubmit: () -> Unit,
    resetForm: () -> Unit,
    onExit: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val bg = PrimaryBg
    var showCurrentPass by remember { mutableStateOf(false) }
    var showNewPass by remember { mutableStateOf(false) }
    var showConfirmPass by remember { mutableStateOf(false) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    val scrollState = rememberScrollState()

    // -----------------------------
    // File creation utilities
    // -----------------------------

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

    // -----------------------------
    //  Launchers convert to BASE64
    // -----------------------------
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCaptureUri?.let { uri ->
                val base64 = fileToBase64(uri.path!!)
                onPictureChange(base64)
            }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val savedPath = savePermanentImage(context, it)
            val base64 = fileToBase64(savedPath!!)
            onPictureChange(base64)
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
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // -----------------------------
            // HEADER
            // -----------------------------
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
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = PlayfairDisplay),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 48.dp, top = 6.dp),
                    textAlign = TextAlign.Center
                )
            }

            val imageModel by remember(profilePicture) {
                mutableStateOf(
                    when {
                        profilePicture != null && profilePicture.startsWith("/9j/") -> {
                            decodeBase64Image(profilePicture)
                        }
                        profilePicture != null -> {
                            Uri.fromFile(File(profilePicture))
                        }
                        else -> null
                    }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                AsyncImage(
                    model = imageModel,
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(16.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(50.dp)),
                    placeholder = painterResource(id = R.drawable.defaultprofilepicture),
                    error = painterResource(id = R.drawable.defaultprofilepicture),
                    contentScale = ContentScale.Crop
                )

                Column {
                    IconButton(onClick = {
                        val file = createPermanentImageFile(context)
                        val uri = getImageUriFile(context, file)
                        pendingCaptureUri = uri
                        takePictureLauncher.launch(uri)
                    }) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = "Camera icon")
                    }

                    Spacer(Modifier.height(20.dp))

                    IconButton(onClick = { pickImageLauncher.launch("image/*") }) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = "Gallery icon")
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // -----------------------------
            // NICKNAME
            // -----------------------------
            OutlinedTextField(
                value = nickname,
                onValueChange = onNicknameChange,
                placeholder = { Text("New nickname", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato)) },
                leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Account icon", tint = Color.Gray) },
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
            if (nicknameError != null)
                Text(nicknameError, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(20.dp))

            // -----------------------------
            // EMAIL
            // -----------------------------
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = { Text("New email", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato)) },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email icon", tint = Color.Gray) },
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
            if (emailError != null)
                Text(emailError, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(20.dp))

            // -----------------------------
            // CURRENT PASSWORD
            // -----------------------------
            OutlinedTextField(
                value = currentPassword,
                onValueChange = onCurrentPasswordChange,
                placeholder = { Text("Current password", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato)) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Current password icon", tint = Color.Gray) },
                isError = currentPasswordError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(30.dp),
                visualTransformation = if (showCurrentPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showCurrentPass = !showCurrentPass }) {
                        Icon(
                            imageVector = if (showCurrentPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            )
            if (currentPasswordError != null)
                Text(currentPasswordError, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(20.dp))

            // -----------------------------
            // NEW PASSWORD
            // -----------------------------
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("New password", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato)) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Lock icon", tint = Color.Gray) },
                isError = passwordError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(30.dp),
                visualTransformation = if (showNewPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showNewPass = !showNewPass }) {
                        Icon(
                            imageVector = if (showCurrentPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            )
            if (passwordError != null)
                Text(passwordError, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(20.dp))

            // -----------------------------
            // CONFIRM PASSWORD
            // -----------------------------
            OutlinedTextField(
                value = confirmPass,
                onValueChange = onConfirmPasswordChange,
                placeholder = { Text("Confirm password", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato)) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Lock icon", tint = Color.Gray) },
                isError = passwordConfirmErr != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(30.dp),
                visualTransformation = if (showConfirmPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showConfirmPass = !showConfirmPass }) {
                        Icon(
                            imageVector = if (showCurrentPass) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                }
            )
            if (passwordConfirmErr != null)
                Text(passwordConfirmErr, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(20.dp))

            if (errorMessage != null)
                Text(errorMessage, color = MaterialTheme.colorScheme.error)

            if (success)
                Text("Saved changes.", color = Color(0xFF008000))

            Spacer(Modifier.height(20.dp))

            // -----------------------------
            // SAVE BUTTON
            // -----------------------------
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = ResaltadoNegative,
                    containerColor = Resaltado
                ),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
            ) {
                Text(
                    if (isSubmitting) "Saving..." else "Save changes",
                    color = Color.White
                )
            }

            Spacer(Modifier.height(30.dp))

            // -----------------------------
            // LOGOUT
            // -----------------------------
            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = ResaltadoNegative),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
            ) {
                Text("Log out", color = Color.White)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
