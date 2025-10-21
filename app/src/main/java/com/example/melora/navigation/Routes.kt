package com.example.melora.navigation

sealed class Route(val path: String) {
    data object Home     : Route("home")
    data object Login    : Route("login")
    data object Register : Route("register")
    data object UploadScreenForm : Route("uploadForm")
    data object SuccesUpload : Route("succesUpload")
    data object SearchView : Route("SearchView")
}