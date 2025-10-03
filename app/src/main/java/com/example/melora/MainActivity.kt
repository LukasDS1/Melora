package com.example.melora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.melora.navigation.AppNavGraph
import com.example.melora.ui.screen.LoginScreen
import com.example.melora.ui.screen.RegisterScreen
import com.example.melora.ui.theme.MeloraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}



@Composable // Indica que esta función dibuja UI
fun AppRoot() { // Raíz de la app para separar responsabilidades
    val navController = rememberNavController() // Controlador de navegación
    MaterialTheme { // Provee colores/tipografías Material 3
        Surface(color = MaterialTheme.colorScheme.background) { // Fondo general
            AppNavGraph(navController = navController) // Carga el NavHost + Scaffold + Drawer
        }
    }
}