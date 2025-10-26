package com.example.melora.navigation

sealed class Route(val path: String) {
    data object Home     : Route("home")
    data object Login    : Route("login")
    data object Register : Route("register")
    data object editProfile : Route("edit_profile")
    data object UploadScreenForm : Route("uploadForm")
    data object SuccesUpload : Route("succesUpload")
    data object SearchView : Route("SearchView")

    data object Favorites: Route("favorites")

    data object ArtistProfile: Route("artistProfile/{artistId}"){
        fun createRoute(artistId:Long) = "artistProfile/$artistId"
    }
    data object Player: Route("player/{songId}"){
        fun createRoutePlayer(songId:Long) = "player/$songId"
    }
}