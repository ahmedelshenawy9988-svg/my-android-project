package com.example.math

import kotlin.random.Random

data class MathQuestion(
    val id: Int,
    val expression: String,
    val answer: Int
)

enum class DifficultyLevel(val key: String, val labelArabic: String, val descriptionArabic: String) {
    EASY("EASY", "سهل", "عمليات جمع وطرح بسيطة لتنشيط الذهن"),
    MEDIUM("MEDIUM", "متوسط", "جمع وطرح ثنائي وضرب للمزيد من التركيز"),
    HARD("HARD", "صعب", "معادلات مركبة تستدعي الاستيقاظ الكامل");

    companion object {
        fun fromKey(key: String): DifficultyLevel {
            return entries.find { it.key.equals(key, ignoreCase = true) } ?: MEDIUM
        }
    }
}

object MathChallengeGenerator {

    fun generateQuestions(difficulty: DifficultyLevel, count: Int = 5): List<MathQuestion> {
        return (1..count).map { index ->
            generateSingleQuestion(difficulty, index)
        }
    }

    fun generateSingleQuestion(difficulty: DifficultyLevel, id: Int): MathQuestion {
        return when (difficulty) {
            DifficultyLevel.EASY -> generateEasy(id)
            DifficultyLevel.MEDIUM -> generateMedium(id)
            DifficultyLevel.HARD -> generateHard(id)
        }
    }

    private fun generateEasy(id: Int): MathQuestion {
        val op = Random.nextInt(3)
        return when (op) {
            0 -> {
                // Addition: 10..40 + 5..30
                val a = Random.nextInt(10, 45)
                val b = Random.nextInt(5, 30)
                MathQuestion(id, "$a + $b", a + b)
            }
            1 -> {
                // Subtraction: 20..60 - 5..20
                val a = Random.nextInt(20, 60)
                val b = Random.nextInt(5, a - 5)
                MathQuestion(id, "$a - $b", a - b)
            }
            else -> {
                // Single-digit multiplication
                val a = Random.nextInt(3, 10)
                val b = Random.nextInt(3, 10)
                MathQuestion(id, "$a × $b", a * b)
            }
        }
    }

    private fun generateMedium(id: Int): MathQuestion {
        val op = Random.nextInt(4)
        return when (op) {
            0 -> {
                // Two-digit addition with carrying: 25..85 + 25..85
                val a = Random.nextInt(25, 85)
                val b = Random.nextInt(25, 85)
                MathQuestion(id, "$a + $b", a + b)
            }
            1 -> {
                // Two-digit subtraction: 50..120 - 15..45
                val a = Random.nextInt(50, 120)
                val b = Random.nextInt(15, 45)
                MathQuestion(id, "$a - $b", a - b)
            }
            2 -> {
                // Multiplication: 11..16 * 4..9
                val a = Random.nextInt(11, 16)
                val b = Random.nextInt(4, 9)
                MathQuestion(id, "$a × $b", a * b)
            }
            else -> {
                // 3 numbers: a + b - c
                val a = Random.nextInt(20, 50)
                val b = Random.nextInt(15, 40)
                val c = Random.nextInt(5, 25)
                MathQuestion(id, "$a + $b - $c", a + b - c)
            }
        }
    }

    private fun generateHard(id: Int): MathQuestion {
        val op = Random.nextInt(3)
        return when (op) {
            0 -> {
                // Combined: (a * b) + c
                val a = Random.nextInt(12, 20)
                val b = Random.nextInt(5, 9)
                val c = Random.nextInt(15, 50)
                MathQuestion(id, "($a × $b) + $c", (a * b) + c)
            }
            1 -> {
                // Multi-step: a - b + c
                val a = Random.nextInt(120, 250)
                val b = Random.nextInt(45, 95)
                val c = Random.nextInt(20, 60)
                MathQuestion(id, "$a - $b + $c", a - b + c)
            }
            else -> {
                // Double digit multiplication or division combo: (a * b) - c
                val a = Random.nextInt(14, 25)
                val b = Random.nextInt(6, 12)
                val c = Random.nextInt(10, 40)
                MathQuestion(id, "($a × $b) - $c", (a * b) - c)
            }
        }
    }
}
