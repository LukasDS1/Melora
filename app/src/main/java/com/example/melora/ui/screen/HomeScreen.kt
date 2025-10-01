package com.example.melora.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(){
    val bg = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = Modifier.fillMaxSize().background(bg).padding(10.dp),
        contentAlignment = Alignment.Center
    ){
        Column ( horizontalAlignment = Alignment.CenterHorizontally){
            Row (verticalAlignment = Alignment.CenterVertically){
                Text("Melora", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(8.dp))
                AssistChip(
                    onClick = {},
                    label = {Text("navega desde arriba o desde aquí")}
                )
                Spacer(Modifier.width(8.dp))
                Text("Menu de hamburguesa deberia ir aqui",
                    style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}