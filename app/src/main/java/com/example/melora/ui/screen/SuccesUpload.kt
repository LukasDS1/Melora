package com.example.melora.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.melora.R
import com.example.melora.ui.theme.Lato
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.ui.theme.Resaltado
import com.example.melora.ui.theme.SecondaryBg

@Composable
fun SuccesUpload(
    onGoHome: () -> Unit,
    onGoUpload: () -> Unit
) {
    val cardBg = R.drawable.backgroundtonegray
    val bg = Color(0xFF4b4b4b)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        ElevatedCard(
            modifier = Modifier.padding(24.dp).wrapContentHeight().fillMaxWidth().align(Alignment.Center),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            )
        ) {
            Box(modifier = Modifier.fillMaxWidth().background(SecondaryBg)) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Your song has been uploaded successfully",
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp,
                        color = Color.Black,
                        fontFamily = Lato
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(text = "Keep uploading more music or listen to your music",
                        textAlign = TextAlign.Center,
                        color = Color.Black,
                        fontFamily = Lato
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = onGoUpload,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Resaltado)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Upload more music!", fontFamily = Lato)
                        }
                        Button(
                            onClick = onGoHome,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Resaltado)
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Listen More Music", fontFamily = Lato)
                        }
                    }
                }
            }
        }
    }
}