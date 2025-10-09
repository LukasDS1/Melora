package com.example.melora.ui.screen
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun UploadScreen(
    onRegistered: () -> Unit,
    onGoLogin: () -> Unit
) {
    val bg = Color(0xFF84D2BA) // Fondo agradable para Home
    //recordar el archivo seleccionado

    var selectedAudio by remember { mutableStateOf<Uri?>(null) }

    //crear launcher
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ){  //se ejecutara cuando el usuario elija el archivo
        uri: Uri? -> selectedAudio = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sube tu musica",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFFFFF)
                )

            }
            Spacer(Modifier.height(20.dp))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Comparte tu musica con el mundo",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick ={
                        pickerLauncher.launch("audio/*")

                    } ) {
                        Icon(Icons.Filled.Add,"Sube tu audio")
                    }
                    Spacer(Modifier.height(10.dp))

                    if(selectedAudio != null){
                        Text(
                            text = "Archivo seleccionado $selectedAudio",
                            textAlign = TextAlign.Center
                        )
                    } else{
                        Text(
                            "Aún has seleccion ningún archivo",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}