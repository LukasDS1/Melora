package com.example.melora.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.melora.ui.theme.PlayfairDisplay
import com.example.melora.ui.theme.PrimaryBg
import com.example.melora.R
import android.util.Base64


@OptIn(ExperimentalMaterial3Api::class)
@Composable // Composable reutilizable: barra superior
fun AppTopBar(
    onUpload: () -> Unit,       //  go Home
    profileImageUrl: String?,
    onMyProfile: () -> Unit // go Edit profile
) {

    val bgContainer = MaterialTheme.colorScheme.background
    CenterAlignedTopAppBar(
        modifier = Modifier.clip(shape = RoundedCornerShape(bottomStart = 18.dp, bottomEnd = 18.dp)),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = bgContainer
        ),
        navigationIcon = {
            IconButton(onClick = onMyProfile) {

                val context = LocalContext.current

                val decodedBytes = profileImageUrl?.let {
                    runCatching { Base64.decode(it, Base64.DEFAULT) }.getOrNull()
                }

                AsyncImage(
                    model = decodedBytes ?: R.drawable.defaultprofilepicture,
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            }
        },
        title = {
            Text(text = "Melora", color = Color.White, style = MaterialTheme.typography.titleLarge.copy(fontFamily = PlayfairDisplay)
            )
        },
        actions = {
            IconButton(onClick = onUpload ) {
                Icon(Icons.Filled.AddCircle, contentDescription = "Upload music icon", tint = Color.White)
            }
        }
    )
}