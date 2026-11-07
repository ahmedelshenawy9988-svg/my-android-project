package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.WakeUpApp
import com.example.data.AlarmEntity
import com.example.scheduler.AlarmScheduler
import com.example.service.AlarmAudioPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MainUiState(
    val alarms: List<AlarmEntity> = emptyList(),
    val nextAlarm: AlarmEntity? = null,
    val nextAlarmRemainingText: String = "",
    val isTestingSound: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WakeUpApp.instance.repository
    private val context = application.applicationContext
    private var testAudioPlayer: AlarmAudioPlayer? = null

    private val _isTestingSound = MutableStateFlow(false)
    val isTestingSound: StateFlow<Boolean> = _isTestingSound.asStateFlow()

    val alarms: StateFlow<List<AlarmEntity>> = repository.allAlarms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val nextAlarmInfo: StateFlow<Pair<AlarmEntity?, String>> = alarms.map { alarmList ->
        val enabledAlarms = alarmList.filter { it.isEnabled }
        if (enabledAlarms.isEmpty()) {
            null to ""
        } else {
            // Find earliest upcoming alarm
            val nextAlarmWithTime = enabledAlarms.map { alarm ->
                val nextTime = AlarmScheduler.calculateNextTriggerTime(
                    alarm.hour,
                    alarm.minute,
                    alarm.getDaysList()
                )
                alarm to nextTime
            }.minByOrNull { it.second }

            if (nextAlarmWithTime != null) {
                val remaining = AlarmScheduler.getRemainingTimeArabic(nextAlarmWithTime.second)
                nextAlarmWithTime.first to remaining
            } else {
                null to ""
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null to "")

    fun toggleAlarm(alarm: AlarmEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = isEnabled)
            repository.updateAlarm(updated)

            if (isEnabled) {
                AlarmScheduler.scheduleAlarm(context, updated)
            } else {
                AlarmScheduler.cancelAlarm(context, alarm.id)
            }
        }
    }

    fun saveAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            if (alarm.id == 0L) {
                val newId = repository.insertAlarm(alarm)
                val insertedAlarm = alarm.copy(id = newId)
                if (insertedAlarm.isEnabled) {
                    AlarmScheduler.scheduleAlarm(context, insertedAlarm)
                }
            } else {
                repository.updateAlarm(alarm)
                AlarmScheduler.cancelAlarm(context, alarm.id)
                if (alarm.isEnabled) {
                    AlarmScheduler.scheduleAlarm(context, alarm)
                }
            }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            AlarmScheduler.cancelAlarm(context, alarm.id)
            repository.deleteAlarm(alarm)
        }
    }

    fun toggleTestSound() {
        if (_isTestingSound.value) {
            stopTestSound()
        } else {
            startTestSound()
        }
    }

    private fun startTestSound() {
        if (testAudioPlayer == null) {
            testAudioPlayer = AlarmAudioPlayer(context)
        }
        testAudioPlayer?.start(enableVibration = true)
        _isTestingSound.value = true
    }

    fun stopTestSound() {
        testAudioPlayer?.stop()
        _isTestingSound.value = false
    }

    override fun onCleared() {
        stopTestSound()
        super.onCleared()
    }
}
