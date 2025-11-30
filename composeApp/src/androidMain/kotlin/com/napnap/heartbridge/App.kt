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

//        val rows = listOf(
//            Measurement(10,"30-11-2025", "15:20", "73","Z325"),
//            Measurement(9, "30-11-2025", "15:05", "76","Z325"),
//            Measurement(8, "30-11-2025", "14:50", "69","Z325"),
//            Measurement(7, "30-11-2025", "14:35", "71","Z325"),
//            Measurement(6, "30-11-2025", "14:20", "74","Z325"),
//
//            Measurement(5, "29-11-2025", "09:15", "67","Z325"),
//            Measurement(4, "29-11-2025", "09:00", "70","Z325"),
//            Measurement(3, "29-11-2025", "08:45", "72","Z325"),
//            Measurement(2, "29-11-2025", "08:30", "68","Z325"),
//            Measurement(1, "29-11-2025", "08:15", "65","Z325"),
//        )
//        JsonManager.save(context,rows)


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
