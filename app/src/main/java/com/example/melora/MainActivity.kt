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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.melora.data.local.database.MeloraDB
import com.example.melora.data.repository.SongRepository
import com.example.melora.data.repository.UploadRepository
import com.example.melora.data.repository.UserRepository
import com.example.melora.navigation.AppNavGraph
import com.example.melora.ui.screen.LoginScreen
import com.example.melora.ui.screen.RegisterScreen
import com.example.melora.ui.theme.MeloraTheme
import com.example.melora.viewmodel.AuthViewModel
import com.example.melora.viewmodel.AuthViewModelFactory
import com.example.melora.viewmodel.SearchViewModel
import com.example.melora.viewmodel.SearchViewModelFactory
import com.example.melora.viewmodel.UploadViewModel
import com.example.melora.viewmodel.UploadViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    val context = androidx.compose.ui.platform.LocalContext.current

    val db = MeloraDB.getInstance(context)

    val songDao = db.songDao()

    val uploadDao = db.uploadDao()

    val userDao = db.userDao()

    val songRepository = SongRepository(songDao)

    val uploadRepository = UploadRepository(uploadDao)

    val loginRepository = UserRepository(userDao)

    val loginViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(loginRepository)
    )

    val uploadViewModel: UploadViewModel = viewModel(
        factory = UploadViewModelFactory(songRepository)
    )

    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(songRepository)
    )

    val navController = rememberNavController()

    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(
                navController = navController,
                uploadViewModel = uploadViewModel,
                searchViewModel = searchViewModel,
                authViewModel = loginViewModel
            )
        }
    }
}