package com.example.model

enum class PuzzleCategory(
  val displayName: String,
  val iconEmoji: String,
  val description: String
) {
  LOGIC("Logic", "🧠", "Deduction, truth verification, and spatial inference"),
  MEMORY("Memory", "⚡", "Sequence retention and visual recall"),
  OBSERVATION("Observation", "👁️", "Pattern distinction and rapid identification"),
  MATH("Mathematics", "📐", "Mental calculation and quantitative equations"),
  PATTERN("Pattern", "🔮", "Analogies, matrices, and rule extrapolation")
}

enum class DifficultyLevel(
  val label: String,
  val multiplier: Double,
  val basePoints: Int
) {
  NOVICE("Novice", 1.0, 1000),
  ADEPT("Adept", 1.25, 1300),
  MASTER("Master", 1.5, 1700),
  GRANDMASTER("Grandmaster", 2.0, 2200)
}

enum class PowerUpType(
  val title: String,
  val cost: Int,
  val icon: String,
  val description: String
) {
  HINT("Smart Hint", 25, "💡", "Get an immediate tactical clue"),
  FIFTY_FIFTY("50 / 50", 35, "✂️", "Eliminate two wrong answers"),
  EXTRA_TIME("+10s Time", 30, "⏳", "Add 10 seconds to the timer"),
  SECOND_CHANCE("2nd Chance", 50, "🛡️", "Shield your streak upon error")
}

data class Puzzle(
  val id: String,
  val category: PuzzleCategory,
  val title: String,
  val question: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String,
  val hint: String,
  val difficulty: DifficultyLevel = DifficultyLevel.NOVICE,
  val timeLimitSeconds: Int = 15,
  val visualType: String = "TEXT", // TEXT, STROOP, SEQUENCE, MEMORY_FLASH, MATH_EXPR
  val visualData: String = ""
) {
  val correctAnswer: String
    get() = options.getOrElse(correctIndex) { "" }
}
