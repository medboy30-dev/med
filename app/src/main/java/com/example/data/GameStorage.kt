package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Achievement
import com.example.model.UserProfile
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameStorage(context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("mind_rush_storage", Context.MODE_PRIVATE)

  // Current date key format "yyyyMMdd"
  val todayKey: String
    get() = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())

  fun loadUserProfile(): UserProfile {
    val jsonStr = prefs.getString("user_profile_json", null) ?: return UserProfile(lastActiveDate = todayKey)
    return try {
      val json = JSONObject(jsonStr)
      val catScores = mutableMapOf<String, Int>()
      if (json.has("categoryScores")) {
        val catJson = json.getJSONObject("categoryScores")
        catJson.keys().forEach { key ->
          catScores[key] = catJson.getInt(key)
        }
      } else {
        catScores.putAll(mapOf("Logic" to 60, "Memory" to 60, "Observation" to 60, "Math" to 60, "Pattern" to 60, "Speed" to 60))
      }

      UserProfile(
        nickname = json.optString("nickname", "BrainRushing"),
        country = json.optString("country", "United States"),
        countryCode = json.optString("countryCode", "US"),
        countryFlag = json.optString("countryFlag", "🇺🇸"),
        avatarId = json.optString("avatarId", "avatar_cyber_brain"),
        bestBrainScore = json.optInt("bestBrainScore", 0),
        bestDailyScore = json.optInt("bestDailyScore", 0),
        bestStreak = json.optInt("bestStreak", 0),
        bestSpeedSeconds = json.optDouble("bestSpeedSeconds", 0.0),
        totalRoundsPlayed = json.optInt("totalRoundsPlayed", 0),
        totalPuzzlesSolved = json.optInt("totalPuzzlesSolved", 0),
        coins = json.optInt("coins", 150),
        currentStreak = json.optInt("currentStreak", 1),
        longestStreak = json.optInt("longestStreak", 1),
        lastActiveDate = json.optString("lastActiveDate", todayKey),
        xp = json.optInt("xp", 120),
        streakFreezeCount = json.optInt("streakFreezeCount", 1),
        lastStreakClaimDate = json.optString("lastStreakClaimDate", ""),
        categoryScores = catScores,
        isPro = json.optBoolean("isPro", false),
        soundEnabled = json.optBoolean("soundEnabled", true),
        hapticEnabled = json.optBoolean("hapticEnabled", true)
      )
    } catch (e: Exception) {
      UserProfile(lastActiveDate = todayKey)
    }
  }

  fun saveUserProfile(profile: UserProfile) {
    try {
      val json = JSONObject().apply {
        put("nickname", profile.nickname)
        put("country", profile.country)
        put("countryCode", profile.countryCode)
        put("countryFlag", profile.countryFlag)
        put("avatarId", profile.avatarId)
        put("bestBrainScore", profile.bestBrainScore)
        put("bestDailyScore", profile.bestDailyScore)
        put("bestStreak", profile.bestStreak)
        put("bestSpeedSeconds", profile.bestSpeedSeconds)
        put("totalRoundsPlayed", profile.totalRoundsPlayed)
        put("totalPuzzlesSolved", profile.totalPuzzlesSolved)
        put("coins", profile.coins)
        put("currentStreak", profile.currentStreak)
        put("longestStreak", profile.longestStreak)
        put("lastActiveDate", profile.lastActiveDate)
        put("xp", profile.xp)
        put("streakFreezeCount", profile.streakFreezeCount)
        put("lastStreakClaimDate", profile.lastStreakClaimDate)
        put("isPro", profile.isPro)
        put("soundEnabled", profile.soundEnabled)
        put("hapticEnabled", profile.hapticEnabled)

        val catJson = JSONObject()
        profile.categoryScores.forEach { (k, v) -> catJson.put(k, v) }
        put("categoryScores", catJson)
      }
      prefs.edit().putString("user_profile_json", json.toString()).apply()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  fun loadAchievements(): List<Achievement> {
    val defaults = AchievementsRegistry.defaultAchievements()
    val savedJson = prefs.getString("achievements_json", null) ?: return defaults
    return try {
      val array = JSONArray(savedJson)
      val map = mutableMapOf<String, Triple<Int, Boolean, Boolean>>()
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        val id = obj.getString("id")
        val progress = obj.getInt("progress")
        val unlocked = obj.getBoolean("unlocked")
        val claimed = obj.optBoolean("claimed", false)
        map[id] = Triple(progress, unlocked, claimed)
      }
      defaults.map { ach ->
        val saved = map[ach.id]
        if (saved != null) {
          ach.copy(currentProgress = saved.first, isUnlocked = saved.second, isClaimed = saved.third)
        } else ach
      }
    } catch (e: Exception) {
      defaults
    }
  }

  fun saveAchievements(achievements: List<Achievement>) {
    try {
      val array = JSONArray()
      achievements.forEach { ach ->
        val obj = JSONObject().apply {
          put("id", ach.id)
          put("progress", ach.currentProgress)
          put("unlocked", ach.isUnlocked)
          put("claimed", ach.isClaimed)
        }
        array.put(obj)
      }
      prefs.edit().putString("achievements_json", array.toString()).apply()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }


  fun isDailyChallengeCompleted(dateKey: String = todayKey): Boolean {
    return prefs.getBoolean("daily_completed_$dateKey", false)
  }

  fun markDailyChallengeCompleted(dateKey: String = todayKey, score: Int) {
    prefs.edit()
      .putBoolean("daily_completed_$dateKey", true)
      .putInt("daily_score_$dateKey", score)
      .apply()
  }

  fun getDailyChallengeScore(dateKey: String = todayKey): Int {
    return prefs.getInt("daily_score_$dateKey", 0)
  }

  fun resetAllData() {
    prefs.edit().clear().apply()
  }
}
