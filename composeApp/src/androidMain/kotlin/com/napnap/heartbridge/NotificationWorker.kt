package com.napnap.heartbridge

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("WORKER", "doWork() called")
        withContext(Dispatchers.Main){
            showNotification(applicationContext, "Your BPM was .")
        }
        return Result.success()
    }

    private fun showNotification(context: Context, content: String) {
        val channelId = "default_channel_id"
        val notificationId = 1

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.outline_monitor_heart_24)
            .setContentTitle("Hearth Bridge")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("NO_PERMS","POST_NOTIFICATIONS not granted")
            return
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}