package com.example.melora.ui.screen

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
    vm: UploadViewModel,
    onGoSucces: () -> Unit,
    userId : Long,
    goHome: () -> Unit
) {
    val context = LocalContext.current
    val state by vm.upload.collectAsStateWithLifecycle()

    if (state.success) {
        vm.clearForm()
        vm.clearUpload()
        onGoSucces()
    }

    BackHandler {
        vm.clearForm()
        goHome()
    }

    UploadScreen(
        song = state.song,
        songName = state.songName,
        songDescription = state.songDescription,
        releaseDate = state.releaseDate,
        coverArt = state.coverArt,
        songNameError = state.songNameError,
        coverArtError = state.coverArtError,
        songError = state.songError,
        isSubmitting = state.isSubmitting,
        canSubmit = state.canSubmit,
        success = state.success,
        errorMsg = state.errorMsg,
        onSongNameChange = vm::onSongNameChange,
        onSongDescription = vm::onSongDescriptionChange,
        onSongCoverChange = { uri -> vm.onSongCoverChange(context, uri) },
        onSongChange = { uri -> vm.onSongChange(context, uri) },
        submitMusic = { vm.submitMusic(context, userId) },
        onCancel = {
            vm.clearForm()
            goHome()
        }
    )
}

@Composable
private fun UploadScreen(
    songName: String,
    songDescription: String?,
    releaseDate: Date,
    coverArt: Uri?,
    song: Uri?,
    songNameError: String?,
    coverArtError: String?,
    songError: String?,
    isSubmitting: Boolean,
    canSubmit: Boolean,
    success: Boolean,
    errorMsg: String?,
    onSongNameChange: (String) -> Unit,
    onSongDescription: (String) -> Unit,
    onSongCoverChange: (Uri?) -> Unit,
    onSongChange: (Uri?) -> Unit,
    submitMusic: () -> Unit,
    onCancel: () -> Unit
) {
    val bg = Resaltado

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    var isPlaying by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val localPath = saveAudioLocally(context, it)
            onSongChange(Uri.fromFile(File(localPath)))
        }
    }

   val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { selec ->try {
       val inputStream = context.contentResolver.openInputStream(selec)
       val tempFile = createTempImageFile(context)
       inputStream?.use { input ->
           tempFile.outputStream().use { output -> input.copyTo(output)
           }
       }
       val savedUri = getImageUriFile(context,tempFile)
       onSongCoverChange(savedUri)
    }catch (e: Exception){
       Toast.makeText(context,"Error canceled upload", Toast.LENGTH_SHORT).show()
    }
   }
   }

    Box( Modifier.background(bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )

            Spacer(Modifier.height(20.dp))

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(700.dp)
                    .padding(bottom = 40.dp),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = SecondaryBg,
                    contentColor = Color(0xFF03000A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Upload your audio file (wav & mp3)",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        fontFamily = Lato
                    )

                    Spacer(Modifier.height(12.dp))

                    // Audio picker
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBg, contentColor = Color.White),
                        onClick = { audioPickerLauncher.launch("audio/*") }
                    ) {
                        Icon(Icons.Filled.Add, "Upload your music!")
                    }

                    Spacer(Modifier.height(4.dp))

                    if (songError != null) {
                        Text(
                            songError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = Lato,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    if (song != null) {
                        Text(
                            text = song.lastPathSegment ?: "Audio Name",
                            style = MaterialTheme.typography.bodySmall
                        )
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            IconButton(onClick = {
                                mediaPlayer?.let { player ->
                                    if (isPlaying) {
                                        player.pause()
                                        isPlaying = false
                                    } else {
                                        player.start()
                                        isPlaying = true
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Filled.Clear else Icons.Filled.PlayArrow,
                                    contentDescription = if (isPlaying) "Pause" else "Play"
                                )
                            }
                            Text(
                                text = if (isPlaying) "Reproduciendo..." else "Reproducir",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = Lato
                            )
                        }
                    } else {
                        Text("The Song cannot be empty", textAlign = TextAlign.Center,color = MaterialTheme.colorScheme.error, fontFamily = Lato)
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "Upload your Covert Art",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        fontFamily = Lato
                    )
                    // Cover art
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBg, contentColor = Color.White),
                        onClick = { photoPickerLauncher.launch("image/*") }
                    ) {
                        Icon(Icons.Filled.Add, "Upload your cover art")
                    }

                    Spacer(Modifier.height(4.dp))

                    if (coverArtError != null) {
                        Text(
                            coverArtError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = Lato,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    if (coverArt != null) {
                        AsyncImage(
                            model = coverArt,
                            contentDescription = "Cover Art",
                            modifier = Modifier
                                .height(250.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("Cover art has not been selected",color = MaterialTheme.colorScheme.error,textAlign = TextAlign.Center, fontFamily = Lato)
                    }

                    Spacer(Modifier.height(10.dp))

                    // Song name
                    OutlinedTextField(
                        value = songName,
                        onValueChange = onSongNameChange,
                        label = { Text("Song name", color = Color.Black, fontFamily = Lato) },
                        singleLine = true,
                        isError = songNameError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black)
                    )
                    if (songNameError != null) {
                        Text(
                            songNameError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = Lato
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    // Description
                    OutlinedTextField(
                        value = songDescription ?: "",
                        onValueChange = onSongDescription,
                        label = { Text("Song description (optional)", color = Color.Black, fontFamily = Lato) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.Black)
                    )
                    Spacer(Modifier.height(16.dp))
                    // Submit button
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Resaltado),
                        onClick = submitMusic,
                        enabled = canSubmit && !isSubmitting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Uploading Music...", fontFamily = Lato)
                            Toast.makeText(context, "Music upload Successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Text("Upload", fontFamily = Lato)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Resaltado),
                        onClick = onCancel,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", fontFamily = Lato)
                    }
                }
            }
        }
    }
}
