package com.napnap.heartbridge.ui

sealed class Screen(val route: String,val title: String) {
    object Main : Screen("main_screen","")
    object Settings : Screen("settings_screen","Ustawienia")
    object History : Screen("history_screen","Historia")
}