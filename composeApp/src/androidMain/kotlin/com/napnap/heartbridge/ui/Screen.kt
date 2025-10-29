package com.napnap.heartbridge.ui

sealed class Screen(val route: String) {
    object Main : Screen("main_screen")
    object Settings : Screen("settings_screen")
    object History : Screen("history_screen")
}
