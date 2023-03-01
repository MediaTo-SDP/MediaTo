package com.github.sgueissa.bootcamp

sealed class Routes (val route: String) {
    object Home : Routes("home")
    object Greeting : Routes("greeting")
}