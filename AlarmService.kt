package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.WakeUpApp
import com.example.ui.AlarmRingingActivity

class AlarmService : Service() {

    private var audioPlayer: AlarmAudioPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val NOTIFICATION_ID = 2001
        const val ACTION_START_ALARM = "com.aistudio.wakeup.ACTION_START_ALARM"
        const val ACTION_STOP_ALARM = "com.aistudio.wakeup.ACTION_STOP_ALARM"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_LABEL = "extra_alarm_label"
        const val EXTRA_DIFFICULTY = "extra_difficulty"

        var isRinging = false
            private set

        fun start(context: Context, alarmId: Long, label: String, difficulty: String) {
            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_START_ALARM
                putExtra(EXTRA_ALARM_ID, alarmId)
                putExtra(EXTRA_ALARM_LABEL, label)
                putExtra(EXTRA_DIFFICULTY, difficulty)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_STOP_ALARM
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioPlayer = AlarmAudioPlayer(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == ACTION_STOP_ALARM) {
            stopAlarm()
            stopSelf()
            return START_NOT_STICKY
        }

        if (action == ACTION_START_ALARM) {
            val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)
            val label = intent.getStringExtra(EXTRA_ALARM_LABEL) ?: "منبه WAKE UP"
            val difficulty = intent.getStringExtra(EXTRA_DIFFICULTY) ?: "MEDIUM"

            acquireWakeLock()
            val notification = buildForegroundNotification(alarmId, label, difficulty)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }

            audioPlayer?.start(enableVibration = true)
            isRinging = true

            // Launch AlarmRingingActivity directly to wake screen and present challenge
            val fullScreenIntent = Intent(this, AlarmRingingActivity::class.java).apply {
                putExtra(AlarmRingingActivity.EXTRA_ALARM_ID, alarmId)
                putExtra(AlarmRingingActivity.EXTRA_ALARM_LABEL, label)
                putExtra(AlarmRingingActivity.EXTRA_DIFFICULTY, difficulty)
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
            }
            startActivity(fullScreenIntent)
        }

        return START_STICKY
    }

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = powerManager?.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "WakeUpApp::AlarmServiceWakeLock"
            )?.apply {
                acquire(10 * 60 * 1000L) // 10 minutes max
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            wakeLock = null
        }
    }

    private fun buildForegroundNotification(alarmId: Long, label: String, difficulty: String): Notification {
        val fullScreenIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            putExtra(AlarmRingingActivity.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmRingingActivity.EXTRA_ALARM_LABEL, label)
            putExtra(AlarmRingingActivity.EXTRA_DIFFICULTY, difficulty)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            alarmId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, WakeUpApp.ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("حان وقت الاستيقاظ! $label")
            .setContentText("قم بحل المسائل الحسابية الخمس لإيقاف رنين المنبه")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .build()
    }

    private fun stopAlarm() {
        isRinging = false
        audioPlayer?.stop()
        releaseWakeLock()
    }

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
