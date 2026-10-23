package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameStorage
import com.example.engine.DifficultyAdaptor
import com.example.engine.PuzzleGenerator
import com.example.engine.ScoringEngine
import com.example.model.Achievement
import com.example.model.AchievementCategory
import com.example.model.CountryInfo
import com.example.model.DifficultyLevel
import com.example.model.PowerUpType
import com.example.model.Puzzle
import com.example.model.RoundResult
import com.example.model.UserProfile
import com.example.service.AdRewardType
import com.example.service.LeaderboardType
import com.example.service.LocalAdService
import com.example.service.LocalAnalyticsService
import com.example.service.LocalLeaderboardService
import com.example.service.LocalPremiumService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class AppScreen {
  HOME,
  GAME,
  RESULT,
  DAILY_BATTLE,
  PROFILE,
  LEADERBOARD,
  ACHIEVEMENTS,
  SHOP
}

class GameViewModel(application: Application) : AndroidViewModel(application) {
  private val storage = GameStorage(application)
  private val analytics = LocalAnalyticsService()
  private val adService = LocalAdService()
  private val premiumService = LocalPremiumService()
  private val leaderboardService = LocalLeaderboardService()

  private val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

  val todayKey: String
    get() = storage.todayKey

  // Navigation State
  private val _currentScreen = MutableStateFlow(AppScreen.HOME)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  // Profile & Offline Persistence
  private val _userProfile = MutableStateFlow(storage.loadUserProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _achievements = MutableStateFlow(storage.loadAchievements())
  val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

  // Game Round State
  private val _puzzles = MutableStateFlow<List<Puzzle>>(emptyList())
  val puzzles: StateFlow<List<Puzzle>> = _puzzles.asStateFlow()

  private val _currentPuzzleIndex = MutableStateFlow(0)
  val currentPuzzleIndex: StateFlow<Int> = _currentPuzzleIndex.asStateFlow()

  private val _timeRemainingSeconds = MutableStateFlow(15f)
  val timeRemainingSeconds: StateFlow<Float> = _timeRemainingSeconds.asStateFlow()

  private val _isAnsweringLocked = MutableStateFlow(false)
  val isAnsweringLocked: StateFlow<Boolean> = _isAnsweringLocked.asStateFlow()

  private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
  val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

  private val _lastAnswerCorrect = MutableStateFlow<Boolean?>(null)
  val lastAnswerCorrect: StateFlow<Boolean?> = _lastAnswerCorrect.asStateFlow()

  private val _currentStreak = MutableStateFlow(0)
  val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

  private val _maxStreakInRound = MutableStateFlow(0)
  val maxStreakInRound: StateFlow<Int> = _maxStreakInRound.asStateFlow()

  private val _disabledOptions = MutableStateFlow<Set<Int>>(emptySet())
  val disabledOptions: StateFlow<Set<Int>> = _disabledOptions.asStateFlow()

  private val _activeHint = MutableStateFlow<String?>(null)
  val activeHint: StateFlow<String?> = _activeHint.asStateFlow()

  private val _isShieldActive = MutableStateFlow(false)
  val isShieldActive: StateFlow<Boolean> = _isShieldActive.asStateFlow()

  private val _latestResult = MutableStateFlow<RoundResult?>(null)
  val latestResult: StateFlow<RoundResult?> = _latestResult.asStateFlow()

  private val _isDailyMode = MutableStateFlow(false)
  val isDailyMode: StateFlow<Boolean> = _isDailyMode.asStateFlow()

  private val _unlockedToast = MutableStateFlow<Achievement?>(null)
  val unlockedToast: StateFlow<Achievement?> = _unlockedToast.asStateFlow()

  private val _levelUpToast = MutableStateFlow<Pair<Int, String>?>(null)
  val levelUpToast: StateFlow<Pair<Int, String>?> = _levelUpToast.asStateFlow()

  private val _leaderboardType = MutableStateFlow(LeaderboardType.GLOBAL)
  val leaderboardType: StateFlow<LeaderboardType> = _leaderboardType.asStateFlow()

  private val _leaderboardList = MutableStateFlow<List<com.example.model.LeaderboardEntry>>(emptyList())
  val leaderboardList: StateFlow<List<com.example.model.LeaderboardEntry>> = _leaderboardList.asStateFlow()

  private val _isAdLoading = MutableStateFlow(false)
  val isAdLoading: StateFlow<Boolean> = _isAdLoading.asStateFlow()

  private val _showBreakdownModal = MutableStateFlow(false)
  val showBreakdownModal: StateFlow<Boolean> = _showBreakdownModal.asStateFlow()

  private val _showProModal = MutableStateFlow(false)
  val showProModal: StateFlow<Boolean> = _showProModal.asStateFlow()

  private val _showShareCard = MutableStateFlow(false)
  val showShareCard: StateFlow<Boolean> = _showShareCard.asStateFlow()

  private var timerJob: Job? = null
  private val puzzleTimes = mutableListOf<Double>()
  private val correctAnswersMap = mutableMapOf<Int, Boolean>()
  private var currentDifficulty = DifficultyLevel.NOVICE

  init {
    analytics.logEvent("session_started")
    checkDailyStreakLogin()
    refreshLeaderboard()
  }

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
    if (screen == AppScreen.LEADERBOARD) {
      refreshLeaderboard()
    }
  }

  private fun checkDailyStreakLogin() {
    val profile = _userProfile.value
    val today = storage.todayKey
    if (profile.lastActiveDate.isNotEmpty() && profile.lastActiveDate != today) {
      try {
        val lastDate = profile.lastActiveDate.toLongOrNull() ?: 0L
        val todayDate = today.toLongOrNull() ?: 0L
        val diffDays = todayDate - lastDate

        val (newStreak, newFreezes) = when {
          diffDays == 1L -> (profile.currentStreak + 1) to profile.streakFreezeCount
          diffDays > 1L && profile.streakFreezeCount > 0 -> {
            // Shield activated: keep current streak and consume 1 freeze
            profile.currentStreak to (profile.streakFreezeCount - 1)
          }
          else -> 1 to 1 // Reset gently to 1 with 1 fresh freeze
        }

        val longest = maxOf(newStreak, profile.longestStreak)
        val updated = profile.copy(
          currentStreak = newStreak,
          longestStreak = longest,
          streakFreezeCount = newFreezes,
          lastActiveDate = today
        )
        saveProfile(updated)
        recordStreakProgress(newStreak)
      } catch (e: Exception) {
        saveProfile(profile.copy(lastActiveDate = today))
      }
    }
  }

  fun startBrainRush() {
    _isDailyMode.value = false
    analytics.logEvent("game_started", mapOf("mode" to "brain_rush"))
    startRoundWithPuzzles(PuzzleGenerator.generateRound(difficulty = currentDifficulty))
  }

  fun startDailyBattle() {
    _isDailyMode.value = true
    analytics.logEvent("daily_challenge_started", mapOf("date" to storage.todayKey))
    startRoundWithPuzzles(PuzzleGenerator.generateDailyRound(storage.todayKey))
  }

  private fun startRoundWithPuzzles(puzzleList: List<Puzzle>) {
    _puzzles.value = puzzleList
    _currentPuzzleIndex.value = 0
    _currentStreak.value = 0
    _maxStreakInRound.value = 0
    _isAnsweringLocked.value = false
    _selectedOptionIndex.value = null
    _lastAnswerCorrect.value = null
    _disabledOptions.value = emptySet()
    _activeHint.value = null
    _isShieldActive.value = false
    puzzleTimes.clear()
    correctAnswersMap.clear()

    _currentScreen.value = AppScreen.GAME
    startCurrentPuzzle()
  }

  private fun startCurrentPuzzle() {
    val puzzle = _puzzles.value.getOrNull(_currentPuzzleIndex.value) ?: return
    _timeRemainingSeconds.value = puzzle.timeLimitSeconds.toFloat()
    _isAnsweringLocked.value = false
    _selectedOptionIndex.value = null
    _lastAnswerCorrect.value = null
    _disabledOptions.value = emptySet()
    _activeHint.value = null

    analytics.logEvent("puzzle_started", mapOf("category" to puzzle.category.name, "id" to puzzle.id))

    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      val totalMillis = puzzle.timeLimitSeconds * 1000L
      var elapsedMillis = 0L
      while (elapsedMillis < totalMillis) {
        delay(100)
        elapsedMillis += 100
        val remaining = ((totalMillis - elapsedMillis) / 1000f).coerceAtLeast(0f)
        _timeRemainingSeconds.value = remaining
      }
      // Time up
      onTimeExpired()
    }
  }

  private fun onTimeExpired() {
    if (_isAnsweringLocked.value) return
    _isAnsweringLocked.value = true

    // Check if second chance shield is active
    if (_isShieldActive.value) {
      _isShieldActive.value = false
      _timeRemainingSeconds.value = 8f
      _isAnsweringLocked.value = false
      return
    }

    _lastAnswerCorrect.value = false
    _currentStreak.value = 0
    correctAnswersMap[_currentPuzzleIndex.value] = false
    val puzzle = _puzzles.value[_currentPuzzleIndex.value]
    puzzleTimes.add(puzzle.timeLimitSeconds.toDouble())

    triggerHaptic(false)
    analytics.logEvent("puzzle_failed", mapOf("reason" to "time_expired"))

    viewModelScope.launch {
      delay(1800)
      advanceToNextPuzzle()
    }
  }

  fun submitAnswer(chosenIndex: Int) {
    if (_isAnsweringLocked.value) return
    _isAnsweringLocked.value = true
    timerJob?.cancel()

    _selectedOptionIndex.value = chosenIndex
    val puzzle = _puzzles.value[_currentPuzzleIndex.value]
    val isCorrect = (chosenIndex == puzzle.correctIndex)
    _lastAnswerCorrect.value = isCorrect

    val timeSpent = (puzzle.timeLimitSeconds - _timeRemainingSeconds.value).toDouble().coerceAtLeast(0.5)
    puzzleTimes.add(timeSpent)

    if (isCorrect) {
      val streak = _currentStreak.value + 1
      _currentStreak.value = streak
      if (streak > _maxStreakInRound.value) {
        _maxStreakInRound.value = streak
      }
      correctAnswersMap[_currentPuzzleIndex.value] = true
      triggerHaptic(true)
      analytics.logEvent("puzzle_completed", mapOf("category" to puzzle.category.name, "time" to timeSpent))
      trackSpeedAchievements(timeSpent)
    } else {
      // Check 2nd chance shield
      if (_isShieldActive.value) {
        _isShieldActive.value = false
        _isAnsweringLocked.value = false
        _lastAnswerCorrect.value = null
        _selectedOptionIndex.value = null
        _timeRemainingSeconds.value = (_timeRemainingSeconds.value + 5f).coerceAtLeast(6f)
        return
      }

      _currentStreak.value = 0
      correctAnswersMap[_currentPuzzleIndex.value] = false
      triggerHaptic(false)
      analytics.logEvent("puzzle_failed", mapOf("category" to puzzle.category.name, "selected" to chosenIndex))
    }

    viewModelScope.launch {
      delay(1600)
      advanceToNextPuzzle()
    }
  }

  private fun advanceToNextPuzzle() {
    val nextIndex = _currentPuzzleIndex.value + 1
    if (nextIndex < _puzzles.value.size) {
      _currentPuzzleIndex.value = nextIndex
      startCurrentPuzzle()
    } else {
      finalizeRound()
    }
  }

  private fun finalizeRound() {
    val totalPuzzles = _puzzles.value.size
    val correctCount = correctAnswersMap.values.count { it }
    val totalTime = puzzleTimes.sum()
    val maxStreak = _maxStreakInRound.value
    val isDaily = _isDailyMode.value

    val breakdown = ScoringEngine.calculate(
      correctCount = correctCount,
      totalPuzzles = totalPuzzles,
      totalTimeTakenSeconds = totalTime,
      maxStreak = maxStreak,
      difficulty = currentDifficulty,
      isDaily = isDaily
    )

    val currentProfile = _userProfile.value
    val isPersonalBest = breakdown.totalScore > currentProfile.bestBrainScore

    val result = RoundResult(
      roundId = "round_${System.currentTimeMillis()}",
      timestamp = System.currentTimeMillis(),
      isDaily = isDaily,
      totalPuzzles = totalPuzzles,
      correctCount = correctCount,
      accuracyPercent = breakdown.accuracyPercent,
      avgSpeedSeconds = breakdown.avgSpeedSeconds,
      maxStreak = maxStreak,
      baseScore = breakdown.baseScore,
      speedBonus = breakdown.speedBonus,
      streakBonus = breakdown.streakBonus,
      difficultyBonus = breakdown.difficultyBonus,
      finalScore = breakdown.totalScore,
      coinsEarned = breakdown.coinsEarned,
      isNewPersonalBest = isPersonalBest,
      categoryBreakdown = _puzzles.value.mapIndexed { idx, p ->
        p.title to (correctAnswersMap[idx] == true)
      }.toMap()
    )

    _latestResult.value = result

    // Update user stats & adaptive difficulty
    currentDifficulty = DifficultyAdaptor.adapt(listOf(correctCount), currentDifficulty)

    val newBestScore = maxOf(currentProfile.bestBrainScore, breakdown.totalScore)
    val newBestStreak = maxOf(currentProfile.bestStreak, maxStreak)
    val newBestDaily = if (isDaily) maxOf(currentProfile.bestDailyScore, breakdown.totalScore) else currentProfile.bestDailyScore

    // Update Category Performance Radar
    val updatedCategoryScores = currentProfile.categoryScores.toMutableMap()
    _puzzles.value.forEachIndexed { idx, p ->
      val success = correctAnswersMap[idx] == true
      val currentVal = updatedCategoryScores[p.category.displayName] ?: 60
      val delta = if (success) 3 else -1
      updatedCategoryScores[p.category.displayName] = (currentVal + delta).coerceIn(40, 99)
    }

    // Update Speed rating
    val speedScore = ((15.0 - breakdown.avgSpeedSeconds).coerceIn(2.0, 12.0) / 12.0 * 100).toInt()
    updatedCategoryScores["Speed"] = speedScore.coerceIn(40, 99)

    val updatedProfile = currentProfile.copy(
      bestBrainScore = newBestScore,
      bestDailyScore = newBestDaily,
      bestStreak = newBestStreak,
      totalRoundsPlayed = currentProfile.totalRoundsPlayed + 1,
      totalPuzzlesSolved = currentProfile.totalPuzzlesSolved + correctCount,
      coins = currentProfile.coins + breakdown.coinsEarned,
      categoryScores = updatedCategoryScores
    )

    saveProfile(updatedProfile)

    // Meaningful progression XP awards
    val xpEarned = (correctCount * 40) + 120 + (if (correctCount == totalPuzzles) 100 else 0) + (if (isDaily) 150 else 0)
    addXp(xpEarned)

    if (isDaily) {
      storage.markDailyChallengeCompleted(storage.todayKey, breakdown.totalScore)
      analytics.logEvent("daily_challenge_completed", mapOf("score" to breakdown.totalScore))
      updateAchievementProgress("daily_first", 1)
      incrementAchievementProgress("daily_3", 1)
      incrementAchievementProgress("daily_7", 1)
      incrementAchievementProgress("daily_30", 1)
    }

    // Process Round Achievements
    incrementAchievementProgress("prog_1", 1)
    incrementAchievementProgress("prog_5", 1)
    incrementAchievementProgress("prog_10", 1)
    incrementAchievementProgress("prog_25", 1)
    incrementAchievementProgress("prog_50", 1)
    incrementAchievementProgress("prog_100", 1)
    incrementAchievementProgress("prog_250", 1)
    incrementAchievementProgress("prog_puz_50", correctCount)
    incrementAchievementProgress("prog_puz_250", correctCount)
    incrementAchievementProgress("prog_puz_1000", correctCount)

    if (correctCount == totalPuzzles) {
      incrementAchievementProgress("perf_round", 1)
    }
    if (breakdown.totalScore >= 7000) incrementAchievementProgress("perf_score7k", 1)
    if (breakdown.totalScore >= 8500) incrementAchievementProgress("perf_score8k", 1)
    if (breakdown.totalScore >= 9500) incrementAchievementProgress("perf_score95k", 1)

    _currentScreen.value = AppScreen.RESULT
    analytics.logEvent("session_completed", mapOf("score" to breakdown.totalScore))
  }

  // --- POWER-UPS ---
  fun usePowerUp(type: PowerUpType) {
    val profile = _userProfile.value
    if (profile.coins < type.cost) return

    val currentPuzzle = _puzzles.value.getOrNull(_currentPuzzleIndex.value) ?: return

    when (type) {
      PowerUpType.HINT -> {
        _activeHint.value = currentPuzzle.hint
        analytics.logEvent("hint_used", mapOf("category" to currentPuzzle.category.name))
      }
      PowerUpType.FIFTY_FIFTY -> {
        val wrongIndices = currentPuzzle.options.indices.filter { it != currentPuzzle.correctIndex }.shuffled()
        _disabledOptions.value = wrongIndices.take(2).toSet()
      }
      PowerUpType.EXTRA_TIME -> {
        _timeRemainingSeconds.value = (_timeRemainingSeconds.value + 10f).coerceAtMost(30f)
      }
      PowerUpType.SECOND_CHANCE -> {
        _isShieldActive.value = true
      }
    }

    saveProfile(profile.copy(coins = profile.coins - type.cost))
  }

  // --- REWARDED ADS ---
  fun watchRewardedAd(rewardType: AdRewardType) {
    viewModelScope.launch {
      _isAdLoading.value = true
      adService.showRewardedAd(
        rewardType = rewardType,
        onRewardEarned = { reward ->
          _isAdLoading.value = false
          analytics.logEvent("rewarded_ad_watched", mapOf("reward" to reward.name))
          when (reward) {
            AdRewardType.COINS_50 -> {
              val updated = _userProfile.value.copy(coins = _userProfile.value.coins + 50)
              saveProfile(updated)
            }
            AdRewardType.FREE_HINT -> {
              val currentPuzzle = _puzzles.value.getOrNull(_currentPuzzleIndex.value)
              if (currentPuzzle != null) {
                _activeHint.value = currentPuzzle.hint
              }
            }
            AdRewardType.SECOND_CHANCE -> {
              _isShieldActive.value = true
            }
            AdRewardType.DOUBLE_ROUND_REWARD -> {
              val result = _latestResult.value
              if (result != null) {
                val updatedCoins = _userProfile.value.coins + result.coinsEarned
                saveProfile(_userProfile.value.copy(coins = updatedCoins))
                _latestResult.value = result.copy(coinsEarned = result.coinsEarned * 2)
              }
            }
          }
        },
        onAdFailed = {
          _isAdLoading.value = false
        }
      )
    }
  }

  // --- PREMIUM PURCHASE ---
  fun buyProPass() {
    viewModelScope.launch {
      premiumService.purchaseProPass(
        skuId = "pro_lifetime_pass",
        onSuccess = {
          val updated = _userProfile.value.copy(isPro = true)
          saveProfile(updated)
          analytics.logEvent("premium_clicked", mapOf("status" to "success"))
          _showProModal.value = false
        },
        onError = {
          _showProModal.value = false
        }
      )
    }
  }

  fun purchaseCoinPackage(coins: Int, skuId: String = "") {
    viewModelScope.launch {
      premiumService.purchaseProPass(
        skuId = skuId.ifEmpty { "coins_$coins" },
        onSuccess = {
          val profile = _userProfile.value
          saveProfile(profile.copy(coins = profile.coins + coins))
          analytics.logEvent("coins_purchased", mapOf("amount" to coins))
          triggerHaptic(true)
        },
        onError = {
          // Dev mock fallback
          val profile = _userProfile.value
          saveProfile(profile.copy(coins = profile.coins + coins))
          analytics.logEvent("coins_purchased_mock", mapOf("amount" to coins))
          triggerHaptic(true)
        }
      )
    }
  }

  // --- LEADERBOARDS ---
  fun setLeaderboardType(type: LeaderboardType) {
    _leaderboardType.value = type
    refreshLeaderboard()
  }

  private fun refreshLeaderboard() {
    viewModelScope.launch {
      val entries = leaderboardService.fetchLeaderboard(
        type = _leaderboardType.value,
        userCountryCode = _userProfile.value.countryCode,
        currentUser = _userProfile.value
      )
      _leaderboardList.value = entries
    }
  }

  // --- PROFILE UPDATES ---
  fun updateCountry(countryInfo: CountryInfo) {
    val updated = _userProfile.value.copy(
      country = countryInfo.name,
      countryCode = countryInfo.code,
      countryFlag = countryInfo.flag
    )
    saveProfile(updated)
    refreshLeaderboard()
  }

  fun updateNickname(newName: String) {
    if (newName.isNotBlank()) {
      val updated = _userProfile.value.copy(nickname = newName.trim())
      saveProfile(updated)
      refreshLeaderboard()
    }
  }

  fun toggleSound() {
    val updated = _userProfile.value.copy(soundEnabled = !_userProfile.value.soundEnabled)
    saveProfile(updated)
  }

  fun toggleHaptic() {
    val updated = _userProfile.value.copy(hapticEnabled = !_userProfile.value.hapticEnabled)
    saveProfile(updated)
  }

  fun addXp(amount: Int) {
    val current = _userProfile.value
    val oldLevel = current.level
    val newXp = current.xp + amount
    val newLevel = (newXp / 400) + 1
    if (newLevel > oldLevel) {
      val bonusCoins = (newLevel - oldLevel) * 50
      val updatedWithLevel = current.copy(
        xp = newXp,
        coins = current.coins + bonusCoins
      )
      saveProfile(updatedWithLevel)
      _levelUpToast.value = (newLevel to updatedWithLevel.rankTitle)
      triggerHaptic(true)
      viewModelScope.launch {
        delay(4000)
        _levelUpToast.value = null
      }
    } else {
      saveProfile(current.copy(xp = newXp))
    }
  }

  fun claimDailyStreakReward(day: Int, coins: Int) {
    val profile = _userProfile.value
    if (profile.lastStreakClaimDate == storage.todayKey) return
    val updated = profile.copy(
      coins = profile.coins + coins,
      lastStreakClaimDate = storage.todayKey
    )
    saveProfile(updated)
    addXp(120)
    triggerHaptic(true)
  }

  fun claimAchievementReward(achievementId: String) {
    val list = _achievements.value.toMutableList()
    val index = list.indexOfFirst { it.id == achievementId }
    if (index >= 0) {
      val ach = list[index]
      if (ach.isUnlocked && !ach.isClaimed) {
        val updated = ach.copy(isClaimed = true)
        list[index] = updated
        _achievements.value = list
        storage.saveAchievements(list)

        val profile = _userProfile.value
        saveProfile(profile.copy(coins = profile.coins + ach.coinReward))
        addXp(100)
        triggerHaptic(true)
      }
    }
  }

  fun resetAllData() {
    storage.resetAllData()
    _userProfile.value = storage.loadUserProfile()
    _achievements.value = storage.loadAchievements()
    _currentScreen.value = AppScreen.HOME
  }

  // --- ACHIEVEMENTS TRACKING ---
  private fun trackSpeedAchievements(timeSpent: Double) {
    if (timeSpent < 5.0) incrementAchievementProgress("spd_sub5", 1)
    if (timeSpent < 3.0) incrementAchievementProgress("spd_sub3", 1)
    if (timeSpent < 2.0) incrementAchievementProgress("spd_sub2", 1)
  }

  private fun recordStreakProgress(streak: Int) {
    if (streak >= 3) updateAchievementProgress("strk_day3", streak)
    if (streak >= 7) updateAchievementProgress("strk_day7", streak)
    if (streak >= 14) updateAchievementProgress("strk_day14", streak)
    if (streak >= 30) updateAchievementProgress("strk_day30", streak)
    if (streak >= 100) updateAchievementProgress("strk_day100", streak)
  }

  private fun updateAchievementProgress(id: String, progress: Int) {
    val list = _achievements.value.toMutableList()
    val index = list.indexOfFirst { it.id == id }
    if (index >= 0) {
      val ach = list[index]
      val newProg = maxOf(ach.currentProgress, progress)
      val unlockedNow = !ach.isUnlocked && newProg >= ach.maxProgress
      val updated = ach.copy(
        currentProgress = newProg,
        isUnlocked = ach.isUnlocked || unlockedNow
      )
      list[index] = updated
      _achievements.value = list
      storage.saveAchievements(list)

      if (unlockedNow) {
        onAchievementUnlocked(updated)
      }
    }
  }

  private fun incrementAchievementProgress(id: String, delta: Int) {
    val list = _achievements.value.toMutableList()
    val index = list.indexOfFirst { it.id == id }
    if (index >= 0) {
      val ach = list[index]
      val newProg = (ach.currentProgress + delta).coerceAtMost(ach.maxProgress)
      val unlockedNow = !ach.isUnlocked && newProg >= ach.maxProgress
      val updated = ach.copy(
        currentProgress = newProg,
        isUnlocked = ach.isUnlocked || unlockedNow
      )
      list[index] = updated
      _achievements.value = list
      storage.saveAchievements(list)

      if (unlockedNow) {
        onAchievementUnlocked(updated)
      }
    }
  }

  private fun onAchievementUnlocked(ach: Achievement) {
    _unlockedToast.value = ach
    val profile = _userProfile.value
    saveProfile(profile.copy(coins = profile.coins + ach.coinReward))
    analytics.logEvent("achievement_unlocked", mapOf("id" to ach.id, "title" to ach.title))
    viewModelScope.launch {
      delay(3500)
      if (_unlockedToast.value?.id == ach.id) {
        _unlockedToast.value = null
      }
    }
  }

  // --- SOCIAL SHARING ---
  fun shareResult(context: Context) {
    val result = _latestResult.value ?: return
    val text = """
      ⚡ I scored ${result.finalScore} in Mind Rush!
      Accuracy: ${result.accuracyPercent}% | Speed: ${result.avgSpeedSeconds}s/puzzle
      Can you beat my brain score?
      #MindRush #ThinkFastThinkSmart
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "My Mind Rush Brain Score")
      putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Mind Rush Score"))
  }

  fun shareViralChallenge(context: Context) {
    val profile = _userProfile.value
    val best = profile.bestBrainScore
    val challengeUrl = "mindrush://challenge?id=MR${System.currentTimeMillis() % 100000}&from=${profile.nickname}&target=$best"
    val text = """
      🔥 ${profile.nickname} challenges you to a Mind Rush duel!
      My Record: $best points!
      Can you outsmart me? Tap to accept: $challengeUrl
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
      type = "text/plain"
      putExtra(Intent.EXTRA_SUBJECT, "Mind Rush Challenge")
      putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Challenge a Friend"))
  }

  private fun saveProfile(profile: UserProfile) {
    _userProfile.value = profile
    storage.saveUserProfile(profile)
  }

  private fun triggerHaptic(success: Boolean) {
    if (!_userProfile.value.hapticEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = if (success) {
          VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
        } else {
          VibrationEffect.createWaveform(longArrayOf(0, 40, 50, 40), -1)
        }
        vibrator?.vibrate(effect)
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(50)
      }
    } catch (_: Exception) {}
  }

  fun setShowBreakdownModal(show: Boolean) {
    _showBreakdownModal.value = show
  }

  fun setShowProModal(show: Boolean) {
    _showProModal.value = show
  }
}
