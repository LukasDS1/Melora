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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PlayfairDisplay
import com.example.melora.ui.theme.ResaltadoNegative
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.String

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
        if (state.canSubmit) {
            showExitDialog = true
        } else {
            vm.resetFormToOriginalUser()
            onExit()
        }
    }

    if (showExitDialog) {
        androidx.compose.material3.AlertDialog(
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
                ) {
                    Text("Salir sin guardar", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showExitDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Resaltado)
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
    
    EditProfileScreen(
        nickname = state.nickname,
        email = state.email,
        password = state.password,
        nicknameError = state.nicknameError,
        emailError = state.emailError,
        passwordError = state.passwordError,
        currentPassword = state.currentPassword,
        currentPasswordError = state.currentPasswordError,
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
        confirmPass = state.confirmPassword,
        passwordConfirmErr = state.passwordConfirmError,
        onConfirmPasswordChange = vm::onConfirmPasswordChange,
        onCurrentPasswordChange = vm::onCurrentPasswordChange,
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
    var showPass by remember { mutableStateOf(false) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    // Scroll State
    val scrollState = rememberScrollState()

    // Funciones para cámara y galería
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
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Encabezado
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

            // Imagen de perfil
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AsyncImage(
                    model = profilePicture?.takeIf { it.isNotBlank() }?.let { path -> Uri.fromFile(File(path)) },
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

            // --- Nickname ---
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
                Text(nicknameError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall.copy(fontFamily = Lato))

            Spacer(Modifier.height(20.dp))

            // --- Email ---
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
                Text(emailError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall.copy(fontFamily = Lato))

            Spacer(Modifier.height(20.dp))

            // CURRENT PASSWORD
            OutlinedTextField(
                value = currentPassword,
                onValueChange = onCurrentPasswordChange,
                placeholder = { Text("Current password", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Current password icon",
                        tint = Color.Gray
                    )
                },
                isError = currentPasswordError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(30.dp),
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Hide password" else "Show password"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = SecondaryBg,
                    focusedIndicatorColor = Resaltado,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            if (currentPasswordError != null)
                Text(
                    currentPasswordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = Lato)
                )

            Spacer(Modifier.height(20.dp))

            // --- Password ---
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = { Text("New password", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato)) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Lock icon", tint = Color.Gray) },
                isError = passwordError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(30.dp),
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Hide password" else "Show password"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = SecondaryBg,
                    focusedIndicatorColor = Resaltado,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            if (passwordError != null)
                Text(passwordError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall.copy(fontFamily = Lato))

            Spacer(Modifier.height(20.dp))

            // --- Confirm Password ---
            OutlinedTextField(
                value = confirmPass,
                onValueChange = onConfirmPasswordChange,
                placeholder = { Text("Confirm password", style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato)) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Lock icon", tint = Color.Gray) },
                isError = passwordConfirmErr != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(30.dp),
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPass) "Hide password" else "Show password"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = SecondaryBg,
                    focusedIndicatorColor = Resaltado,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            if (passwordConfirmErr != null)
                Text(passwordConfirmErr, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall.copy(fontFamily = Lato))

            Spacer(Modifier.height(20.dp))

            if (errorMessage != null)
                Text(errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall.copy(fontFamily = Lato))
            if (success)
                Text("Saved changes.", color = Color(0xFF008000), style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato))

            Spacer(Modifier.height(20.dp))

            // --- Botón guardar ---
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                colors = ButtonDefaults.buttonColors(disabledContainerColor = ResaltadoNegative, containerColor = Resaltado),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
            ) {
                Text(
                    if (isSubmitting) "Saving..." else "Save changes",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato)
                )
            }

            Spacer(Modifier.height(30.dp))

            // --- Botón logout ---
            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = ResaltadoNegative),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
            ) {
                Text("Log out", color = Color.White, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = Lato))
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}
