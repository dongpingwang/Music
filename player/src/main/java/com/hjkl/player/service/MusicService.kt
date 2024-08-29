package com.hjkl.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.hjkl.comm.AppUtil



class MusicService: Service() {

    companion object {
        private const val TAG = "MusicService"

         fun start() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppUtil.getContext().startForegroundService(Intent(AppUtil.getContext(),
                    MusicService::class.java))
            }else {
                AppUtil.getContext().startService(Intent(AppUtil.getContext(), MusicService::class.java))
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, ">>onCreate<<")
        val notification = if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "10000",
                "MusicPlayer",
                NotificationManager.IMPORTANCE_HIGH
            );
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel);

            Notification.Builder(this, "10000").build();
        } else {
            Notification.Builder(this).build()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(10000, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(10000, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, ">>onStartCommand<<")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, ">>onDestroy<<")
    }
}