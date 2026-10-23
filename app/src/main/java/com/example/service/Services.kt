package com.example.service

import android.util.Log
import com.example.model.LeaderboardEntry
import com.example.model.UserProfile
import kotlinx.coroutines.delay

/**
 * Analytics service interface for tracking game lifecycle and user actions.
 * In production, connect Firebase Analytics or standard telemetry here.
 */
interface AnalyticsService {
  fun logEvent(eventName: String, params: Map<String, Any> = emptyMap())
}

class LocalAnalyticsService : AnalyticsService {
  override fun logEvent(eventName: String, params: Map<String, Any>) {
    Log.d("MindRushAnalytics", "Event: $eventName | Params: $params")
  }
}

/**
 * AdService abstraction for Rewarded Ads.
 * In production: connect Google Mobile Ads (AdMob) RewardedAd.
 */
enum class AdRewardType(val rewardTitle: String) {
  COINS_50("+50 Mind Coins"),
  FREE_HINT("Free Tactical Hint"),
  SECOND_CHANCE("Streak Shield Second Chance"),
  DOUBLE_ROUND_REWARD("Double Round Reward")
}

interface AdService {
  fun isAdLoaded(): Boolean
  suspend fun showRewardedAd(
    rewardType: AdRewardType,
    onRewardEarned: (AdRewardType) -> Unit,
    onAdFailed: (String) -> Unit
  )
}

class MockAdService : AdService {
  override fun isAdLoaded(): Boolean = true

  override suspend fun showRewardedAd(
    rewardType: AdRewardType,
    onRewardEarned: (AdRewardType) -> Unit,
    onAdFailed: (String) -> Unit
  ) {
    // Simulate brief high-fidelity rewarded ad countdown (2.5 seconds)
    try {
      delay(2500)
      onRewardEarned(rewardType)
    } catch (e: Exception) {
      onAdFailed(e.message ?: "Ad playback was interrupted")
    }
  }
}

typealias LocalAdService = MockAdService
typealias LocalPremiumService = MockPremiumService

/**
 * PremiumService abstraction for Mind Rush Pro subscription / one-time pass.
 * In production: connect Google Play Billing Client (BillingClient.newBuilder).
 */
interface PremiumService {
  fun isProActive(userProfile: UserProfile): Boolean
  suspend fun purchaseProPass(
    skuId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  )
  suspend fun restorePurchases(onRestored: (Boolean) -> Unit)
}

class MockPremiumService : PremiumService {
  override fun isProActive(userProfile: UserProfile): Boolean = userProfile.isPro

  override suspend fun purchaseProPass(
    skuId: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
  ) {
    delay(1000)
    // Simulate successful Google Play Billing flow
    onSuccess()
  }

  override suspend fun restorePurchases(onRestored: (Boolean) -> Unit) {
    delay(800)
    onRestored(true)
  }
}

/**
 * Leaderboard service abstraction supporting Global, Weekly, Daily, Country, and Friends ranks.
 */
enum class LeaderboardType(val title: String) {
  GLOBAL("Global"),
  WEEKLY("Weekly"),
  DAILY("Daily"),
  COUNTRY("Country"),
  FRIENDS("Friends")
}

interface LeaderboardService {
  suspend fun fetchLeaderboard(
    type: LeaderboardType,
    userCountryCode: String,
    currentUser: UserProfile
  ): List<LeaderboardEntry>
}

class LocalLeaderboardService : LeaderboardService {
  private val mockNames = listOf(
    "ApexThinker" to ("US" to "🇺🇸"),
    "SynapseViper" to ("GB" to "🇬🇧"),
    "NeuroSamurai" to ("JP" to "🇯🇵"),
    "LogicSorcerer" to ("DE" to "🇩🇪"),
    "MindDrifter" to ("CA" to "🇨🇦"),
    "AtlasQ" to ("FR" to "🇫🇷"),
    "DesertFalcon" to ("DZ" to "🇩🇿"),
    "AlphaCortex" to ("AU" to "🇦🇺"),
    "PulseMaster" to ("BR" to "🇧🇷"),
    "QuantumEcho" to ("KR" to "🇰🇷"),
    "VeloxMind" to ("ES" to "🇪🇸"),
    "HyperSynapse" to ("IT" to "🇮🇹"),
    "ChronoRider" to ("IN" to "🇮🇳"),
    "ZenithSpark" to ("NL" to "🇳🇱")
  )

  override suspend fun fetchLeaderboard(
    type: LeaderboardType,
    userCountryCode: String,
    currentUser: UserProfile
  ): List<LeaderboardEntry> {
    delay(200) // Realistic light async fetch

    val userScore = when (type) {
      LeaderboardType.DAILY -> if (currentUser.bestDailyScore > 0) currentUser.bestDailyScore else 6800
      else -> if (currentUser.bestBrainScore > 0) currentUser.bestBrainScore else 7400
    }

    val baseEntries = mutableListOf<LeaderboardEntry>()

    // Filter or adjust based on type
    val competitors = when (type) {
      LeaderboardType.COUNTRY -> mockNames.filter { it.second.first == userCountryCode }.ifEmpty {
        listOf(
          "CortexPro" to (userCountryCode to currentUser.countryFlag),
          "SynapseAce" to (userCountryCode to currentUser.countryFlag),
          "MindStriker" to (userCountryCode to currentUser.countryFlag)
        )
      }
      LeaderboardType.FRIENDS -> listOf(
        "Sarah_Mind" to ("US" to "🇺🇸"),
        "David_K" to ("GB" to "🇬🇧"),
        "Elena_R" to ("DE" to "🇩🇪"),
        "Sam_Rush" to ("CA" to "🇨🇦")
      )
      else -> mockNames
    }

    // Generate scaled scores
    val maxScore = when (type) {
      LeaderboardType.DAILY -> 9450
      LeaderboardType.WEEKLY -> 9890
      else -> 9980
    }

    competitors.forEachIndexed { index, (name, countryPair) ->
      val score = (maxScore - (index * 260) + (index * 17 % 43)).coerceAtLeast(3500)
      baseEntries.add(
        LeaderboardEntry(
          rank = 0,
          name = name,
          avatarId = "avatar_${(index % 6) + 1}",
          score = score,
          country = countryPair.first,
          countryCode = countryPair.first,
          countryFlag = countryPair.second,
          isCurrentUser = false
        )
      )
    }

    // Add current user
    baseEntries.add(
      LeaderboardEntry(
        rank = 0,
        name = currentUser.nickname,
        avatarId = currentUser.avatarId,
        score = userScore,
        country = currentUser.country,
        countryCode = currentUser.countryCode,
        countryFlag = currentUser.countryFlag,
        isCurrentUser = true
      )
    )

    // Sort by score descending and re-assign ranks
    return baseEntries.sortedByDescending { it.score }.mapIndexed { i, entry ->
      entry.copy(rank = i + 1)
    }
  }
}

/**
 * CloudSaveService abstraction for cloud backup and cross-device sync.
 */
interface CloudSaveService {
  suspend fun saveBackup(profile: UserProfile): Result<Boolean>
  suspend fun restoreBackup(): Result<UserProfile?>
}

class LocalCloudSaveService : CloudSaveService {
  override suspend fun saveBackup(profile: UserProfile): Result<Boolean> {
    delay(400)
    return Result.success(true)
  }

  override suspend fun restoreBackup(): Result<UserProfile?> {
    delay(400)
    return Result.success(null)
  }
}
