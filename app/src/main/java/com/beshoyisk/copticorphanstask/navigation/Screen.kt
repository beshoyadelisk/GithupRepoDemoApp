package com.beshoyisk.copticorphanstask.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("sign_up")
    object Home : Screen("home/{username}?profilePicture={profilePicture}") {
        fun createRoute(username: String, profilePicture: String?) =
            "home/$username?profilePicture=$profilePicture"
    }
}
