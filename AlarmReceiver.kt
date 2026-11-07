package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.WakeUpApp
import com.example.scheduler.AlarmScheduler
import com.example.service.AlarmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)
        Log.d("AlarmReceiver", "Alarm trigger received for ID: $alarmId")

        if (alarmId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = WakeUpApp.instance.repository
                val alarm = repository.getAlarmById(alarmId)

                if (alarm != null && alarm.isEnabled) {
                    // Start ringing in foreground service
                    AlarmService.start(
                        context,
                        alarm.id,
                        alarm.label,
                        alarm.difficulty
                    )

                    if (alarm.isRepeating()) {
                        // Reschedule next recurrence
                        AlarmScheduler.scheduleAlarm(context, alarm)
                    } else {
                        // One-time alarm: mark disabled in database
                        repository.updateAlarm(alarm.copy(isEnabled = false))
                    }
                }
            } catch (e: Exception) {
                Log.e("AlarmReceiver", "Error processing alarm trigger: ${e.message}")
            } finally {
                pendingResult.finish()
            }
        }
    }
}
