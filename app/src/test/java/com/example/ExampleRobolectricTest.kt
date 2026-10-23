package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AchievementsRegistry
import com.example.engine.DifficultyAdaptor
import com.example.engine.PuzzleGenerator
import com.example.engine.PuzzleValidator
import com.example.engine.ScoringEngine
import com.example.model.DifficultyLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context verifies Mind Rush app name`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Mind Rush", appName)
  }

  @Test
  fun `puzzle generator produces 5 valid puzzles per round across categories`() {
    val puzzles = PuzzleGenerator.generateRound(DifficultyLevel.NOVICE, seed = 12345L)
    assertEquals(5, puzzles.size)
    puzzles.forEach { puzzle ->
      assertTrue("Puzzle ${puzzle.id} must be valid", PuzzleValidator.validate(puzzle))
      assertTrue("Puzzle options must contain correct index", puzzle.correctIndex in puzzle.options.indices)
      assertNotNull(puzzle.explanation)
    }
  }

  @Test
  fun `daily challenge produces deterministic identical round for same date key`() {
    val roundA = PuzzleGenerator.generateDailyRound("20261023")
    val roundB = PuzzleGenerator.generateDailyRound("20261023")
    assertEquals(roundA.size, roundB.size)
    roundA.indices.forEach { i ->
      assertEquals(roundA[i].title, roundB[i].title)
      assertEquals(roundA[i].question, roundB[i].question)
      assertEquals(roundA[i].correctAnswer, roundB[i].correctAnswer)
    }
  }

  @Test
  fun `scoring engine calculates non-punitive transparent score with bonuses`() {
    val breakdown = ScoringEngine.calculate(
      correctCount = 5,
      totalPuzzles = 5,
      totalTimeTakenSeconds = 20.0,
      maxStreak = 5,
      difficulty = DifficultyLevel.ADEPT,
      isDaily = false
    )
    assertTrue("Total score should be high for 5/5 perfect round", breakdown.totalScore > 5000)
    assertEquals(100, breakdown.accuracyPercent)
    assertTrue("Coins should be awarded generously", breakdown.coinsEarned >= 75)
  }

  @Test
  fun `achievements registry contains exactly 50 achievements`() {
    val achievements = AchievementsRegistry.defaultAchievements()
    assertEquals(50, achievements.size)
  }
}
