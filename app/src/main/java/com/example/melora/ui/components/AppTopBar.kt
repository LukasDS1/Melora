package com.example.melora.ui.components

import android.widget.SearchView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable // Composable reutilizable: barra superior
fun AppTopBar(
    onOpenDrawer: () -> Unit, // Abre el drawer (hamburguesa)
    onHome: () -> Unit,       // Navega a Home
    onLogin: () -> Unit,      // Navega a Login
    onRegister: () -> Unit,// Navega a Registro
    textFieldState: TextFieldState,
    searchResults: List<String>,
    onSearch: (String) -> Unit,
) {

    CenterAlignedTopAppBar( // Barra alineada al centro
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        title = { // Slot del título
            SearchView(
                textFieldState = textFieldState,
                onSearch = onSearch,
                searchResults = searchResults,
                modifier = Modifier.fillMaxWidth()

            )
        },
        navigationIcon = { // Ícono a la izquierda (hamburguesa)
            IconButton(onClick = onOpenDrawer) { // Al presionar, abre drawer
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú") // Ícono
            }
        },
        actions = {
            IconButton(onClick = onHome) { // Ir a Home
                Icon(Icons.Filled.Home, contentDescription = "Home") // Ícono Home
            }
            IconButton(onClick = onLogin) { // Ir a Login
                Icon(Icons.Filled.AccountCircle, contentDescription = "Login") // Ícono Login
            }
            IconButton(onClick = onRegister) { // Ir a Registro
                Icon(Icons.Filled.Person, contentDescription = "Registro") // Ícono Registro
            }
        }
    )
}