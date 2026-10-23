package com.example.model

data class RoundResult(
  val roundId: String,
  val timestamp: Long,
  val isDaily: Boolean,
  val totalPuzzles: Int,
  val correctCount: Int,
  val accuracyPercent: Int,
  val avgSpeedSeconds: Double,
  val maxStreak: Int,
  val baseScore: Int,
  val speedBonus: Int,
  val streakBonus: Int,
  val difficultyBonus: Int,
  val finalScore: Int,
  val coinsEarned: Int,
  val isNewPersonalBest: Boolean,
  val categoryBreakdown: Map<String, Boolean> // puzzle title -> correct
)

data class UserProfile(
  val nickname: String = "BrainRushing",
  val country: String = "United States",
  val countryCode: String = "US",
  val countryFlag: String = "🇺🇸",
  val avatarId: String = "avatar_cyber_brain",
  val bestBrainScore: Int = 0,
  val bestDailyScore: Int = 0,
  val bestStreak: Int = 0,
  val bestSpeedSeconds: Double = 0.0,
  val totalRoundsPlayed: Int = 0,
  val totalPuzzlesSolved: Int = 0,
  val coins: Int = 150, // Starting welcome coins
  val currentStreak: Int = 1,
  val longestStreak: Int = 1,
  val lastActiveDate: String = "",
  val xp: Int = 120, // Starting XP
  val streakFreezeCount: Int = 1, // Grace protection for streaks
  val lastStreakClaimDate: String = "",
  val categoryScores: Map<String, Int> = mapOf(
    "Logic" to 60,
    "Memory" to 60,
    "Observation" to 60,
    "Math" to 60,
    "Pattern" to 60,
    "Speed" to 60
  ),
  val isPro: Boolean = false,
  val soundEnabled: Boolean = true,
  val hapticEnabled: Boolean = true
) {
  val level: Int
    get() = (xp / 400) + 1

  val levelProgress: Float
    get() = ((xp % 400).toFloat() / 400f).coerceIn(0f, 1f)

  val rankTitle: String
    get() = when {
      level >= 25 -> "Brain Legend"
      level >= 17 -> "Quantum Mind"
      level >= 12 -> "Cortex Master"
      level >= 8 -> "Cognitive Prodigy"
      level >= 5 -> "Synapse Explorer"
      level >= 3 -> "Neural Initiate"
      else -> "Novice Thinker"
    }

  val overallBrainScore: Int
    get() = if (bestBrainScore > 0) bestBrainScore else {
      val avg = categoryScores.values.average().toInt()
      avg * 85
    }

  val strongestSkill: Pair<String, Int>
    get() {
      val max = categoryScores.entries.maxByOrNull { entry -> entry.value }
      return if (max != null) (max.key to max.value) else ("Logic" to 60)
    }

  val weakestSkill: Pair<String, Int>
    get() {
      val min = categoryScores.entries.minByOrNull { entry -> entry.value }
      return if (min != null) (min.key to min.value) else ("Math" to 60)
    }

  val mostImprovedSkill: Pair<String, Int>
    get() {
      val skills = categoryScores.toList().sortedByDescending { it.second }
      return if (skills.size > 1) (skills[1].first to 12) else ("Logic" to 10)
    }
}

enum class AchievementCategory(val label: String, val icon: String) {
  PROGRESS("Progress", "🏆"),
  SKILL("Skill", "🧠"),
  SPEED("Speed", "⚡"),
  STREAK("Streak", "🔥"),
  PERFECTION("Perfection", "💎"),
  DAILY("Daily", "📅")
}

data class Achievement(
  val id: String,
  val title: String,
  val description: String,
  val category: AchievementCategory,
  val currentProgress: Int,
  val maxProgress: Int,
  val isUnlocked: Boolean = false,
  val coinReward: Int = 50,
  val iconEmoji: String = "⭐",
  val isClaimed: Boolean = false
) {
  val progressPercent: Float
    get() = (currentProgress.toFloat() / maxProgress.toFloat()).coerceIn(0f, 1f)
}

data class LeaderboardEntry(
  val rank: Int,
  val name: String,
  val avatarId: String,
  val score: Int,
  val country: String,
  val countryCode: String,
  val countryFlag: String,
  val isCurrentUser: Boolean = false
)

data class CountryInfo(
  val name: String,
  val code: String,
  val flag: String
)

val SupportedCountries = listOf(
  CountryInfo("United States", "US", "🇺🇸"),
  CountryInfo("United Kingdom", "GB", "🇬🇧"),
  CountryInfo("Canada", "CA", "🇨🇦"),
  CountryInfo("Germany", "DE", "🇩🇪"),
  CountryInfo("France", "FR", "🇫🇷"),
  CountryInfo("Algeria", "DZ", "🇩🇿"),
  CountryInfo("Japan", "JP", "🇯🇵"),
  CountryInfo("Brazil", "BR", "🇧🇷"),
  CountryInfo("Australia", "AU", "🇦🇺"),
  CountryInfo("Spain", "ES", "🇪🇸"),
  CountryInfo("Italy", "IT", "🇮🇹"),
  CountryInfo("South Korea", "KR", "🇰🇷"),
  CountryInfo("India", "IN", "🇮🇳"),
  CountryInfo("Mexico", "MX", "🇲🇽"),
  CountryInfo("Netherlands", "NL", "🇳🇱"),
  CountryInfo("Sweden", "SE", "🇸🇪"),
  CountryInfo("Switzerland", "CH", "🇨🇭"),
  CountryInfo("United Arab Emirates", "AE", "🇦🇪"),
  CountryInfo("Morocco", "MA", "🇲🇦"),
  CountryInfo("Egypt", "EG", "🇪🇬")
)
