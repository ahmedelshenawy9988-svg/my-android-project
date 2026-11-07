package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.example.data.AppDatabase
import com.example.data.AlarmRepository

class WakeUpApp : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var repository: AlarmRepository
        private set

    companion object {
        const val ALARM_CHANNEL_ID = "wake_up_alarm_channel"
        const val TEST_CHANNEL_ID = "wake_up_test_channel"
        lateinit var instance: WakeUpApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getDatabase(this)
        repository = AlarmRepository(database.alarmDao())

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                getString(R.string.alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.alarm_channel_description)
                setSound(alarmSound, audioAttributes)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 800, 400, 800, 400)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true)
            }

            val testChannel = NotificationChannel(
                TEST_CHANNEL_ID,
                "اختبار المنبه",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعار اختبار صوت المنبه"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(alarmChannel)
            notificationManager?.createNotificationChannel(testChannel)
        }
    }
}
