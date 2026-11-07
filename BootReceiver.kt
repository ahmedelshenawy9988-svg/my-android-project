package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.WakeUpApp
import com.example.scheduler.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("BootReceiver", "Device boot or package update received: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val repository = WakeUpApp.instance.repository
                    val enabledAlarms = repository.getEnabledAlarms()
                    Log.d("BootReceiver", "Rescheduling ${enabledAlarms.size} alarms after boot")

                    for (alarm in enabledAlarms) {
                        AlarmScheduler.scheduleAlarm(context, alarm)
                    }
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error rescheduling alarms on boot: ${e.message}")
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
