package com.example.melora.ui.screen

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.ui.theme.SecondaryBg
import com.example.melora.viewmodel.UploadApiViewModel
import com.example.melora.viewmodel.UploadViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


private fun saveAudioLocally(context: Context, sourceUri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(sourceUri)
    val destDir = File(context.filesDir, "songs").apply { mkdirs() }
    val fileName = "song_${System.currentTimeMillis()}.mp3"
    val destFile = File(destDir, fileName)

    inputStream?.use { input ->
        destFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }

    return destFile.absolutePath
}
private fun createTempImageFile(context: Context): File{
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.cacheDir,"images").apply {
        if(!exists()) mkdirs()
    }
    return File(storageDir,"IMG_$timeStamp.jpg")
}

private fun getImageUriFile(context: Context,file: File):Uri{
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context,authority,file)
}

@Composable
fun UploadScreenVm(
    vm: UploadApiViewModel,
    onGoSuccess: () -> Unit,
    userId: Long
) {
    val context = LocalContext.current
    val state by vm.uiState.collectAsStateWithLifecycle()

    if (state.success) {
        vm.clearState()
        onGoSuccess()
    }

    UploadScreen(
        songName = state.songName,
        songDescription = state.songDescription,
        coverArt = state.coverArtUri,
        song = state.songUri,
        songNameError = state.songNameError,
        coverArtError = state.coverArtError,
        songError = state.songError,
        isSubmitting = state.isSubmitting,
        canSubmit = state.canSubmit,
        errorMessage = state.errorMessage,
        onSongNameChange = vm::onSongNameChange,
        onSongDescriptionChange = vm::onSongDescriptionChange,
        onSelectCoverArt = { uri -> vm.onCoverArtChange(context, uri) },
        onSelectSongFile = { uri -> vm.onSongFileChange(context, uri) },
        submit = { vm.submitUpload(userId) }
    )
}

@Composable
fun UploadScreen(
    songName: String,
    songDescription: String?,
    coverArt: Uri?,
    song: Uri?,
    songNameError: String?,
    coverArtError: String?,
    songError: String?,
    isSubmitting: Boolean,
    canSubmit: Boolean,
    errorMessage: String?,
    onSongNameChange: (String) -> Unit,
    onSongDescriptionChange: (String) -> Unit,
    onSelectCoverArt: (Uri?) -> Unit,
    onSelectSongFile: (Uri?) -> Unit,
    submit: () -> Unit
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // ------------------------------------------------------------------
    // Pickers
    // ------------------------------------------------------------------

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onSelectSongFile(uri)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onSelectCoverArt(uri)
    }

    // ------------------------------------------------------------------
    // UI
    // ------------------------------------------------------------------

    Box(
        Modifier
            .fillMaxSize()
            .background(Resaltado)
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(700.dp)
                    .padding(bottom = 40.dp),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = SecondaryBg,
                    contentColor = Color.Black
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ---------------------------
                    // AUDIO PICKER
                    // ---------------------------
                    Text(
                        "Upload your audio file ( mp3)",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBg),
                        onClick = {audioPickerLauncher.launch("audio/mpeg")}
                    ) {
                        Icon(Icons.Filled.Add, "Music")
                        Text(" Select Audio", fontFamily = Lato)
                    }

                    Spacer(Modifier.height(10.dp))

                    if (song != null) {
                        Text(song.lastPathSegment ?: "Audio")

                        LaunchedEffect(song) {
                            mediaPlayer?.release()
                            mediaPlayer = MediaPlayer.create(context, song)
                        }

                        DisposableEffect(Unit) {
                            onDispose {
                                mediaPlayer?.release()
                                mediaPlayer = null
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                mediaPlayer?.let {
                                    if (isPlaying) {
                                        it.pause()
                                        isPlaying = false
                                    } else {
                                        it.start()
                                        isPlaying = true
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = if (isPlaying)
                                        Icons.Filled.Clear else Icons.Filled.PlayArrow,
                                    contentDescription = "Play/Pause"
                                )
                            }
                            Text(if (isPlaying) "Reproduciendo..." else "Reproducir")
                        }
                    } else {
                        if (songError != null)
                            Text(songError, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(Modifier.height(10.dp))

                    // ---------------------------
                    // COVER ART
                    // ---------------------------
                    Text(
                        "Upload your Cover Art",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBg),
                        onClick = { photoPickerLauncher.launch("image/*") }
                    ) {
                        Icon(Icons.Filled.Add, "Cover Art")
                        Text(" Select Image", fontFamily = Lato)
                    }

                    Spacer(Modifier.height(10.dp))

                    if (coverArt != null) {
                        AsyncImage(
                            model = coverArt,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentDescription = "Cover Art",
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        if (coverArtError != null)
                            Text(coverArtError, color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(Modifier.height(10.dp))

                    // ---------------------------
                    // SONG NAME
                    // ---------------------------
                    OutlinedTextField(
                        value = songName,
                        onValueChange = onSongNameChange,
                        label = { Text("Song name") },
                        singleLine = true,
                        isError = songNameError != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (songNameError != null)
                        Text(songNameError, color = MaterialTheme.colorScheme.error)

                    Spacer(Modifier.height(10.dp))

                    // ---------------------------
                    // DESCRIPTION
                    // ---------------------------
                    OutlinedTextField(
                        value = songDescription ?: "",
                        onValueChange = onSongDescriptionChange,
                        label = { Text("Song description (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(15.dp))

                    // ---------------------------
                    // SUBMIT
                    // ---------------------------
                    Button(
                        onClick = submit,
                        enabled = canSubmit && !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = Resaltado),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Uploading...")
                        } else {
                            Text("Upload")
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(Modifier.height(10.dp))
                        Text(errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

