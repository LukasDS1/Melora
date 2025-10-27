package com.example.melora.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.melora.ui.theme.PlayfairDisplay
import com.example.melora.ui.theme.PrimaryBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable // Composable reutilizable: barra superior
fun AppTopBar(
    onHome: () -> Unit,       //  go Home
    onEditProfile: () -> Unit, // go Edit profile
) {

    val topcol = PrimaryBg
    CenterAlignedTopAppBar( // Barra alineada al centro
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = topcol
        ),
        title = {
           Text(text = "Melora", color = Color.Black, style = MaterialTheme.typography.titleLarge.copy(fontFamily = PlayfairDisplay)
           )
        },
        navigationIcon = {
            IconButton(onClick = onEditProfile) {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Edit profile Icon", tint = Color.Black) // Ícono
            }
        },
        actions = {
            IconButton(onClick = onHome ) {
                Icon(Icons.Filled.Home, contentDescription = "Home icon", tint = Color.Black)
            }
        }
    )
}