package com.example

import com.example.data.AlarmEntity
import com.example.math.DifficultyLevel
import com.example.math.MathChallengeGenerator
import com.example.scheduler.AlarmScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {

    @Test
    fun testMathChallengeEasyGeneration() {
        val questions = MathChallengeGenerator.generateQuestions(DifficultyLevel.EASY, 5)
        assertEquals(5, questions.size)
        for (q in questions) {
            assertNotNull(q.expression)
            assertTrue(q.expression.isNotBlank())
        }
    }

    @Test
    fun testMathChallengeMediumGeneration() {
        val questions = MathChallengeGenerator.generateQuestions(DifficultyLevel.MEDIUM, 5)
        assertEquals(5, questions.size)
        for (q in questions) {
            assertNotNull(q.expression)
        }
    }

    @Test
    fun testMathChallengeHardGeneration() {
        val questions = MathChallengeGenerator.generateQuestions(DifficultyLevel.HARD, 5)
        assertEquals(5, questions.size)
        for (q in questions) {
            assertNotNull(q.expression)
        }
    }

    @Test
    fun testAlarmEntityFormatting() {
        val alarmAm = AlarmEntity(hour = 6, minute = 30)
        assertEquals("6:30 ص", alarmAm.getFormattedTime())

        val alarmPm = AlarmEntity(hour = 14, minute = 5)
        assertEquals("2:05 م", alarmPm.getFormattedTime())

        val alarmMidnight = AlarmEntity(hour = 0, minute = 0)
        assertEquals("12:00 ص", alarmMidnight.getFormattedTime())
    }

    @Test
    fun testAlarmRepeatingDays() {
        val repeatingAlarm = AlarmEntity(
            hour = 7,
            minute = 0,
            daysOfWeek = "1,2,3,4,5"
        )
        assertTrue(repeatingAlarm.isRepeating())
        assertEquals(listOf(1, 2, 3, 4, 5), repeatingAlarm.getDaysList())

        val oneShotAlarm = AlarmEntity(hour = 7, minute = 0, daysOfWeek = "")
        assertFalse(oneShotAlarm.isRepeating())
        assertTrue(oneShotAlarm.getDaysList().isEmpty())
    }

    @Test
    fun testAlarmSchedulerTriggerCalculation() {
        val now = System.currentTimeMillis()
        val trigger = AlarmScheduler.calculateNextTriggerTime(8, 0, emptyList())
        assertTrue(trigger >= now - 1000)
    }
}
