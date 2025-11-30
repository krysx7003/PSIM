package com.napnap.heartbridge



import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.napnap.heartbridge.theme.AppTheme
import com.napnap.heartbridge.ui.HistoryScreen
import com.napnap.heartbridge.ui.HistoryViewModel
import com.napnap.heartbridge.ui.MainScreen
import com.napnap.heartbridge.ui.MainViewModel
import com.napnap.heartbridge.ui.SettingsScreen
import com.napnap.heartbridge.ui.SettingsViewModel
import com.napnap.heartbridge.ui.components.AppTopBar
import com.napnap.heartbridge.ui.components.Screen
import com.napnap.heartbridge.ui.components.SettingsStore
import java.util.concurrent.TimeUnit

@Composable
fun App() {
    AppTheme {
        val navController = rememberNavController()
        val context = LocalContext.current

        val mainViewModel: MainViewModel = viewModel()
        mainViewModel.initConnection(context)
        val settingsViewModel: SettingsViewModel = viewModel()
        val historyViewModel: HistoryViewModel = viewModel()

        startWorker(context)

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { AppTopBar(navController) }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Main.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Main.route) { MainScreen(mainViewModel) }
                composable(Screen.Settings.route) { SettingsScreen(settingsViewModel) }
                composable(Screen.History.route) { HistoryScreen(historyViewModel) }
            }
        }
    }
}
fun startWorker(context: Context){
    val time = SettingsStore.readS(context,"interval","15")

    val request = PeriodicWorkRequestBuilder<NotificationWorker>(
        time.toLong(),
        TimeUnit.MINUTES
    ).build()

    Log.d("WORKER", "calling doWork()")
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "notification_worker",
        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
        request
    )
}
