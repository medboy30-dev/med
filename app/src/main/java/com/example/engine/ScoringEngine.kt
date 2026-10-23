package com.example.engine

import com.example.model.DifficultyLevel
import kotlin.math.roundToInt

data class ScoreBreakdown(
  val baseScore: Int,
  val difficultyBonus: Int,
  val speedBonus: Int,
  val streakBonus: Int,
  val totalScore: Int,
  val accuracyPercent: Int,
  val avgSpeedSeconds: Double,
  val coinsEarned: Int
)

object ScoringEngine {

  /**
   * Calculates transparent brain score for a completed round.
   *
   * @param correctCount Number of correct answers (0..totalPuzzles)
   * @param totalPuzzles Total puzzles in round (typically 5)
   * @param totalTimeTakenSeconds Total elapsed seconds across all puzzles
   * @param maxStreak Longest consecutive correct streak within the round
   * @param difficulty Round difficulty level
   * @param isDaily Whether this round is the Daily Brain Battle (1.2x bonus)
   */
  fun calculate(
    correctCount: Int,
    totalPuzzles: Int,
    totalTimeTakenSeconds: Double,
    maxStreak: Int,
    difficulty: DifficultyLevel,
    isDaily: Boolean = false
  ): ScoreBreakdown {
    val accuracyPercent = if (totalPuzzles > 0) ((correctCount.toDouble() / totalPuzzles) * 100).roundToInt() else 0
    val avgSpeed = if (totalPuzzles > 0) (totalTimeTakenSeconds / totalPuzzles) else 10.0

    // Base score: 1,000 points per correct puzzle
    val base = correctCount * 1000

    // Difficulty bonus
    val diffMultiplier = difficulty.multiplier - 1.0 // 0.0 for Novice, 0.25 for Adept, etc.
    val difficultyBonus = (base * diffMultiplier).roundToInt()

    // Speed bonus: up to 400 pts per puzzle if answered quickly (e.g. under 8 seconds avg)
    // Non-punitive: even a 15-second solve awards some points if correct!
    val speedFactor = ((15.0 - avgSpeed).coerceIn(0.0, 12.0) / 12.0)
    val speedBonus = (correctCount * 350 * speedFactor).roundToInt()

    // Streak combo bonus: rewarding consecutive focus
    val streakBonus = when (maxStreak) {
      5 -> 1000 // Flawless round combo
      4 -> 600
      3 -> 350
      2 -> 150
      else -> 0
    }

    var total = base + difficultyBonus + speedBonus + streakBonus
    if (isDaily) {
      total = (total * 1.20).roundToInt() // Daily Battle 20% competitive multiplier
    }

    // Coin earnings: fair and rewarding
    val coins = 20 + (correctCount * 10) + (if (correctCount == totalPuzzles) 25 else 0) + (if (isDaily) 30 else 0)

    return ScoreBreakdown(
      baseScore = base,
      difficultyBonus = difficultyBonus,
      speedBonus = speedBonus,
      streakBonus = streakBonus,
      totalScore = total,
      accuracyPercent = accuracyPercent,
      avgSpeedSeconds = (avgSpeed * 10).roundToInt() / 10.0,
      coinsEarned = coins
    )
  }
}

object DifficultyAdaptor {
  fun adapt(recentCorrectAnswers: List<Int>, currentDifficulty: DifficultyLevel): DifficultyLevel {
    if (recentCorrectAnswers.size < 2) return currentDifficulty

    val lastTwo = recentCorrectAnswers.takeLast(2)
    val avg = lastTwo.average()

    return when {
      avg >= 4.5 && currentDifficulty.ordinal < DifficultyLevel.entries.size - 1 -> {
        DifficultyLevel.entries[currentDifficulty.ordinal + 1]
      }
      avg <= 1.5 && currentDifficulty.ordinal > 0 -> {
        DifficultyLevel.entries[currentDifficulty.ordinal - 1]
      }
      else -> currentDifficulty
    }
  }
}
