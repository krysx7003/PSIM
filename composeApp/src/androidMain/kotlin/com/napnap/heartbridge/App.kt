package com.napnap.heartbridge


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.napnap.heartbridge.theme.AppTheme
import com.napnap.heartbridge.ui.HistoryScreen
import com.napnap.heartbridge.ui.MainScreen
import com.napnap.heartbridge.ui.Screen
import com.napnap.heartbridge.ui.SettingsScreen
import com.napnap.heartbridge.ui.components.AppTopBar

@Composable
fun App() {
    AppTheme {
        val navController = rememberNavController()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { AppTopBar(navController) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Main.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Main.route) { MainScreen() }
                composable(Screen.Settings.route) { SettingsScreen() }
                composable(Screen.History.route) { HistoryScreen() }
            }
        }
    }
}
