package com.example.data

import com.example.model.Achievement
import com.example.model.AchievementCategory

object AchievementsRegistry {
  fun defaultAchievements(): List<Achievement> = listOf(
    // PROGRESS (10)
    Achievement("prog_1", "First Rush", "Complete your very first Brain Rush round", AchievementCategory.PROGRESS, 0, 1, false, 25, "🌱"),
    Achievement("prog_5", "Warm-Up", "Complete 5 rounds of Brain Rush", AchievementCategory.PROGRESS, 0, 5, false, 40, "🏃"),
    Achievement("prog_10", "10 Rounds", "Complete 10 total rounds", AchievementCategory.PROGRESS, 0, 10, false, 60, "🏅"),
    Achievement("prog_25", "Dedicated Thinker", "Complete 25 total rounds", AchievementCategory.PROGRESS, 0, 25, false, 80, "🎯"),
    Achievement("prog_50", "Half Century", "Complete 50 rounds of Brain Rush", AchievementCategory.PROGRESS, 0, 50, false, 120, "🎖️"),
    Achievement("prog_100", "100 Rounds", "Reach the 100 rounds milestone", AchievementCategory.PROGRESS, 0, 100, false, 200, "👑"),
    Achievement("prog_250", "Synapse Veteran", "Complete 250 rounds", AchievementCategory.PROGRESS, 0, 250, false, 350, "💎"),
    Achievement("prog_puz_50", "Problem Solver", "Solve 50 puzzles correctly", AchievementCategory.PROGRESS, 0, 50, false, 50, "🧩"),
    Achievement("prog_puz_250", "Puzzle Connoisseur", "Solve 250 puzzles correctly", AchievementCategory.PROGRESS, 0, 250, false, 150, "📚"),
    Achievement("prog_puz_1000", "1,000 Puzzles", "Solve 1,000 puzzles across all modes", AchievementCategory.PROGRESS, 0, 1000, false, 500, "🌌"),

    // SKILL (15)
    Achievement("skill_log_1", "Logic Novice", "Answer 10 Logic puzzles correctly", AchievementCategory.SKILL, 0, 10, false, 40, "🧠"),
    Achievement("skill_log_2", "Logic Master", "Answer 50 Logic puzzles correctly", AchievementCategory.SKILL, 0, 50, false, 100, "🔮"),
    Achievement("skill_log_3", "Deduction Guru", "Answer 100 Logic puzzles correctly", AchievementCategory.SKILL, 0, 100, false, 200, "🧙"),
    Achievement("skill_mem_1", "Memory Novice", "Answer 10 Memory puzzles correctly", AchievementCategory.SKILL, 0, 10, false, 40, "💾"),
    Achievement("skill_mem_2", "Memory Master", "Answer 50 Memory puzzles correctly", AchievementCategory.SKILL, 0, 50, false, 100, "🗃️"),
    Achievement("skill_mem_3", "Photographic Mind", "Answer 100 Memory puzzles correctly", AchievementCategory.SKILL, 0, 100, false, 200, "📸"),
    Achievement("skill_obs_1", "Observation Keen", "Answer 10 Observation puzzles correctly", AchievementCategory.SKILL, 0, 10, false, 40, "👁️"),
    Achievement("skill_obs_2", "Observation King", "Answer 50 Observation puzzles correctly", AchievementCategory.SKILL, 0, 50, false, 100, "🦅"),
    Achievement("skill_obs_3", "Eagle Eye", "Answer 100 Observation puzzles correctly", AchievementCategory.SKILL, 0, 100, false, 200, "🔭"),
    Achievement("skill_mat_1", "Math Whiz", "Answer 10 Math puzzles correctly", AchievementCategory.SKILL, 0, 10, false, 40, "📐"),
    Achievement("skill_mat_2", "Math Prodigy", "Answer 50 Math puzzles correctly", AchievementCategory.SKILL, 0, 50, false, 100, "🔢"),
    Achievement("skill_mat_3", "Human Calculator", "Answer 100 Math puzzles correctly", AchievementCategory.SKILL, 0, 100, false, 200, "💻"),
    Achievement("skill_pat_1", "Pattern Seeker", "Answer 10 Pattern puzzles correctly", AchievementCategory.SKILL, 0, 10, false, 40, "🌀"),
    Achievement("skill_pat_2", "Pattern Master", "Answer 50 Pattern puzzles correctly", AchievementCategory.SKILL, 0, 50, false, 100, "✨"),
    Achievement("skill_all_1", "Brain All-Rounder", "Achieve 70+ in all five skill metrics", AchievementCategory.SKILL, 0, 5, false, 250, "🌟"),

    // SPEED (8)
    Achievement("spd_sub5", "Lightning Fast", "Solve a puzzle correctly in under 5 seconds", AchievementCategory.SPEED, 0, 1, false, 50, "⚡"),
    Achievement("spd_sub3", "Sonic Reflex", "Solve a puzzle correctly in under 3 seconds", AchievementCategory.SPEED, 0, 1, false, 100, "🚀"),
    Achievement("spd_sub2", "Instant Synapse", "Solve a puzzle correctly in under 2 seconds", AchievementCategory.SPEED, 0, 1, false, 150, "💫"),
    Achievement("spd_rnd_fast", "Speed Demon", "Finish a full 5-puzzle round in under 45 seconds", AchievementCategory.SPEED, 0, 1, false, 120, "⏱️"),
    Achievement("spd_10fast", "Rapid Fire", "Solve 10 puzzles under 5 seconds each", AchievementCategory.SPEED, 0, 10, false, 80, "🔥"),
    Achievement("spd_25fast", "Bullet Mind", "Solve 25 puzzles under 5 seconds each", AchievementCategory.SPEED, 0, 25, false, 150, "💥"),
    Achievement("spd_avg_fast", "High Velocity", "Achieve an overall speed rating above 85", AchievementCategory.SPEED, 0, 1, false, 180, "🏎️"),
    Achievement("spd_turbo", "Warp Speed", "Complete a perfect round with avg speed < 4s", AchievementCategory.SPEED, 0, 1, false, 220, "🌠"),

    // STREAK (8)
    Achievement("strk_combo3", "Hot Streak", "Reach a 3-puzzle combo streak in one round", AchievementCategory.STREAK, 0, 3, false, 30, "🔥"),
    Achievement("strk_combo5", "Inferno", "Achieve a full 5-puzzle combo in a round", AchievementCategory.STREAK, 0, 5, false, 75, "🌋"),
    Achievement("strk_combo10", "Supernova", "Maintain a 10-puzzle combo across rounds", AchievementCategory.STREAK, 0, 10, false, 150, "💥"),
    Achievement("strk_day3", "3 Day Streak", "Play Mind Rush for 3 consecutive days", AchievementCategory.STREAK, 0, 3, false, 50, "🗓️"),
    Achievement("strk_day7", "7 Day Streak", "Play Mind Rush for 7 consecutive days", AchievementCategory.STREAK, 0, 7, false, 120, "🛡️"),
    Achievement("strk_day14", "Fortnight Habit", "Maintain a 14-day daily playing streak", AchievementCategory.STREAK, 0, 14, false, 200, "⚔️"),
    Achievement("strk_day30", "30 Day Streak", "Maintain a 30-day streak of brain fitness", AchievementCategory.STREAK, 0, 30, false, 400, "🏆"),
    Achievement("strk_day100", "100 Day Streak", "Century streak! True cognitive dedication", AchievementCategory.STREAK, 0, 100, false, 1000, "👑"),

    // PERFECTION (5)
    Achievement("perf_round", "Perfect Round", "Score 5 out of 5 in a single Brain Rush round", AchievementCategory.PERFECTION, 0, 1, false, 100, "💎"),
    Achievement("perf_score7k", "Silver Mind", "Achieve a round score of 7,000+ points", AchievementCategory.PERFECTION, 0, 1, false, 100, "🥈"),
    Achievement("perf_score8k", "Golden Mind", "Achieve a round score of 8,500+ points", AchievementCategory.PERFECTION, 0, 1, false, 180, "🥇"),
    Achievement("perf_score95k", "Brain Legend", "Achieve a round score of 9,500+ points", AchievementCategory.PERFECTION, 0, 1, false, 300, "👑"),
    Achievement("perf_back2back", "Flawless Duo", "Achieve 2 perfect rounds back to back", AchievementCategory.PERFECTION, 0, 2, false, 250, "✨"),

    // DAILY (4)
    Achievement("daily_first", "First Battle", "Complete your first Daily Brain Battle", AchievementCategory.DAILY, 0, 1, false, 50, "⚔️"),
    Achievement("daily_3", "Daily Contender", "Complete 3 Daily Brain Battles", AchievementCategory.DAILY, 0, 3, false, 90, "🛡️"),
    Achievement("daily_7", "Weekly Champion", "Complete 7 Daily Brain Battles", AchievementCategory.DAILY, 0, 7, false, 180, "🏆"),
    Achievement("daily_30", "Month of Glory", "Complete 30 Daily Brain Battles", AchievementCategory.DAILY, 0, 30, false, 500, "🌟")
  )
}
