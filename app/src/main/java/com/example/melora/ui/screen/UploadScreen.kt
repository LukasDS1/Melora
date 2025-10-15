package com.example.melora.ui.screen

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.melora.viewmodel.UploadViewModel
import java.util.Date
import com.example.melora.R

@Composable
fun UploadScreenVm(
    onGoSucces: () -> Unit
) {
    val context = LocalContext.current
    val vm: UploadViewModel = viewModel()
    val state by vm.upload.collectAsStateWithLifecycle()

    if (state.success) {
        vm.clearUpload()
        onGoSucces()
    }

    UploadScreen(
        artistName = state.artistName,
        song = state.song,
        songName = state.songName,
        songDescription = state.songDescription,
        releaseDate = state.releaseDate,
        coverArt = state.coverArt,
        artistNameError = state.artistNameError,
        songNameError = state.songNameError,
        coverArtError = state.coverArtError,
        songError = state.songError,
        isSubmitting = state.isSubmitting,
        canSubmit = state.canSubmit,
        success = state.success,
        errorMsg = state.errorMsg,
        onSongNameChange = vm::onSongNameChange,
        onArtistNameChange = vm::onArtistNameChange,
        onSongDescription = vm::onSongDescriptionChange,
        onSongCoverChange = { uri -> vm.onSongCoverChange(context, uri) },
        onSongChange = { uri -> vm.onSongChange(context, uri) },
        submitMusic = vm::submitMusic
    )
}

@Composable
private fun UploadScreen(
    artistName: String,
    songName: String,
    songDescription: String?,
    releaseDate: Date,
    coverArt: Uri?,
    song: Uri?,
    artistNameError: String?,
    songNameError: String?,
    coverArtError: String?,
    songError: String?,
    isSubmitting: Boolean,
    canSubmit: Boolean,
    success: Boolean,
    errorMsg: String?,
    onSongNameChange: (String) -> Unit,
    onArtistNameChange: (String) -> Unit,
    onSongDescription: (String) -> Unit,
    onSongCoverChange: (Uri?) -> Unit,
    onSongChange: (Uri?) -> Unit,
    submitMusic: () -> Unit
) {
    val bg = Color(0xFF4b4b4b)
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var selectedAudio by remember { mutableStateOf<Uri?>(null) }
    var selectedPhoto by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedAudio = uri
        onSongChange(uri)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedPhoto = uri
        onSongCoverChange(uri)
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
                    containerColor = Color(0xFF414141),
                    contentColor = Color(0xFF6650a4)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Upload your audio file (wav & mp3)",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    // Audio picker
                    Button(onClick = { audioPickerLauncher.launch("audio/*") }) {
                        Icon(Icons.Filled.Add, "Upload your music!")
                    }

                    Spacer(Modifier.height(10.dp))

                    if (selectedAudio != null) {
                        Text(
                            text = selectedAudio?.lastPathSegment ?: "Audio Name",
                            style = MaterialTheme.typography.bodySmall
                        )

                        LaunchedEffect(selectedAudio) {
                            mediaPlayer?.release()
                            mediaPlayer = MediaPlayer.create(context, selectedAudio)
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
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Text("The Song cannot be empty", textAlign = TextAlign.Center,color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Upload your Covert Art",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    // Cover art
                    Button(onClick = { photoPickerLauncher.launch("image/*") }) {
                        Icon(Icons.Filled.Add, "Upload your CoverArt")
                    }

                    Spacer(Modifier.height(10.dp))

                    if (selectedPhoto != null) {
                        AsyncImage(
                            model = selectedPhoto,
                            contentDescription = "Cover Art",
                            modifier = Modifier
                                .height(150.dp)
                                .fillMaxWidth()
                        )
                    } else {
                        Text("Cover art has not been selected",color = MaterialTheme.colorScheme.error,textAlign = TextAlign.Center)
                    }

                    Spacer(Modifier.height(10.dp))
                    // Artist name
                    OutlinedTextField(
                        value = artistName,
                        onValueChange = onArtistNameChange,
                        label = { Text("Artist Name", color = Color.White) },
                        singleLine = true,
                        isError = artistNameError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.White)
                    )
                    if (artistNameError != null) {
                        Text(
                            artistNameError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Song name
                    OutlinedTextField(
                        value = songName,
                        onValueChange = onSongNameChange,
                        label = { Text("Song name", color = Color.White) },
                        singleLine = true,
                        isError = songNameError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.White)
                    )
                    if (songNameError != null) {
                        Text(
                            songNameError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Description
                    OutlinedTextField(
                        value = songDescription ?: "",
                        onValueChange = onSongDescription,
                        label = { Text("Song description (optional)", color = Color.White) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(color = Color.White)
                    )

                    Spacer(Modifier.height(16.dp))

                    // Submit button
                    Button(
                        onClick = submitMusic,
                        enabled = canSubmit && !isSubmitting,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        if (isSubmitting) {
                            ButtonColors(containerColor = Color(0xFF6650a4), contentColor = Color.White, disabledContentColor = Color.Black, disabledContainerColor = Color.Gray)
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Uploading Music...")
                        } else {
                            Text("Upload")
                        }
                    }
                }
            }
        }
    }
}
