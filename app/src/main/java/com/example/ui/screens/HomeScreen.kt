package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.UserProfile
import com.example.service.AdRewardType
import com.example.ui.components.AmbientGameBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.CorrectEmerald
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavyCardElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameViewModel

@Composable
fun HomeScreen(
  viewModel: GameViewModel,
  profile: UserProfile,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  // Infinite breathing transition for hero glowing brain
  val infiniteTransition = rememberInfiniteTransition(label = "homeBreathingGlow")
  val brainGlowScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "brainGlowScale"
  )

  AmbientGameBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .widthIn(max = 600.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // HERO BRAIN BANNER
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(26.dp))
          .background(
            Brush.verticalGradient(
              listOf(NavyCardElevated, NavySurface)
            )
          )
          .border(
            BorderStroke(
              1.dp,
              Brush.horizontalGradient(
                listOf(ElectricCyan.copy(alpha = 0.5f), NeonPurple.copy(alpha = 0.5f))
              )
            ),
            RoundedCornerShape(26.dp)
          )
          .padding(22.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          // Glowing Pulsing Brain Logo
          Box(
            modifier = Modifier
              .scale(brainGlowScale)
              .size(104.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  colors = listOf(ElectricCyan.copy(alpha = 0.35f), NeonPurple.copy(alpha = 0.15f), Color.Transparent)
                )
              )
              .padding(6.dp),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.ic_launcher_mindrush_1792772023692),
              contentDescription = "Mind Rush Logo",
              modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .border(2.5.dp, Brush.horizontalGradient(listOf(ElectricCyan, NeonPurple)), CircleShape)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "MIND RUSH",
            style = MaterialTheme.typography.displayMedium.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 2.sp,
              brush = Brush.horizontalGradient(listOf(ElectricCyan, NeonPurple))
            )
          )
          Text(
            text = "Think Fast. Think Smart.",
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // COGNITIVE RANK & XP PROGRESSION CARD
      GlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("progression_rank_card"),
        borderColor = ElectricCyan.copy(alpha = 0.45f),
        backgroundColor = NavyCard
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Brush.radialGradient(listOf(ElectricCyan, NeonPurple))),
                contentAlignment = Alignment.Center
              ) {
                Text("⚡", fontSize = 18.sp)
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "LEVEL ${profile.level} • ${profile.rankTitle.uppercase()}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = "Rank Promotion: Solve puzzles & complete rounds",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            Text(
              text = "${profile.xp % 400} / 400 XP",
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = ElectricCyan
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          androidx.compose.material3.LinearProgressIndicator(
            progress = { profile.levelProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(7.dp)
              .clip(RoundedCornerShape(3.5.dp)),
            color = ElectricCyan,
            trackColor = com.example.ui.theme.NavyBackground
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // PRIMARY CALL TO ACTION: PLAY BRAIN RUSH
      Button(
        onClick = { viewModel.startBrainRush() },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(68.dp)
          .clip(RoundedCornerShape(22.dp))
          .background(
            Brush.horizontalGradient(listOf(ElectricCyan, NeonPurple))
          )
          .testTag("play_brain_rush_btn")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(26.dp)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Text(
              text = "PLAY BRAIN RUSH",
              fontSize = 19.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp,
              color = Color.Black
            )
            Text(
              text = "5 Rapid Puzzles • ~60 Seconds",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black.copy(alpha = 0.75f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // DAILY BRAIN BATTLE CARD
      GlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("daily_battle_card"),
        borderColor = GoldAmber.copy(alpha = 0.7f),
        backgroundColor = NavySurface,
        onClick = { viewModel.startDailyBattle() }
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(GoldAmber.copy(alpha = 0.2f))
              .border(1.5.dp, GoldAmber, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = GoldYellow,
              modifier = Modifier.size(28.dp)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "DAILY BRAIN BATTLE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = GoldAmber
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(GoldAmber)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text("1.2x SCORE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
              }
            }
            Text(
              text = "One universal challenge. Compete globally.",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }

          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = GoldYellow,
            modifier = Modifier.size(26.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 7-DAY DAILY STREAK SECTION
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = NavyCardBorder,
        backgroundColor = NavyCard
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🔥", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "DAILY STREAK",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Black,
                  color = TextPrimary
                )
                Text(
                  text = "Return daily for bonus Mind Coins",
                  fontSize = 11.sp,
                  color = TextSecondary
                )
              }
            }

            Text(
              text = "${profile.currentStreak} DAYS",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Black,
              color = GoldYellow
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 7 Day Tracker Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            val rewards = listOf(25, 30, 40, 50, 75, 100, 150)
            rewards.forEachIndexed { index, coins ->
              val dayNumber = index + 1
              val isCompleted = dayNumber <= profile.currentStreak
              val isCurrent = dayNumber == (profile.currentStreak % 7) + 1

              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                      when {
                        isCompleted -> CorrectEmerald
                        isCurrent -> ElectricCyan.copy(alpha = 0.35f)
                        else -> NavyCardElevated
                      }
                    )
                    .border(
                      1.5.dp,
                      if (isCompleted) CorrectEmerald else if (isCurrent) ElectricCyan else NavyCardBorder,
                      RoundedCornerShape(12.dp)
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (isCompleted) {
                    Text("✓", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 17.sp)
                  } else {
                    Text(
                      text = if (dayNumber == 7) "👑" else "$coins",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Black,
                      color = if (isCurrent) ElectricCyan else TextMuted
                    )
                  }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                  text = "D$dayNumber",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCompleted) TextPrimary else TextMuted
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          val todayDayIndex = ((profile.currentStreak - 1) % 7).coerceIn(0, 6)
          val todayCoinsReward = listOf(25, 30, 40, 50, 75, 100, 150)[todayDayIndex]
          val isClaimedToday = profile.lastStreakClaimDate == viewModel.todayKey

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🛡️", fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (profile.streakFreezeCount > 0) "Streak Shield Ready (${profile.streakFreezeCount})" else "Grace Protection",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricCyan
              )
            }

            if (!isClaimedToday) {
              Button(
                onClick = { viewModel.claimDailyStreakReward(todayDayIndex + 1, todayCoinsReward) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("claim_daily_streak_btn")
              ) {
                Text(
                  text = "CLAIM +$todayCoinsReward 🪙",
                  color = Color.Black,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp
                )
              }
            } else {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(CorrectEmerald.copy(alpha = 0.2f))
                  .border(1.dp, CorrectEmerald, RoundedCornerShape(10.dp))
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text("✓ CLAIMED TODAY", fontSize = 10.sp, fontWeight = FontWeight.Black, color = CorrectEmerald)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // QUICK COGNITIVE SNAPSHOT ROW
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatCard(
          title = "BEST SCORE",
          value = if (profile.bestBrainScore > 0) "%,d".format(profile.bestBrainScore) else "--",
          icon = "🏆",
          accentColor = GoldYellow,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "BEST STREAK",
          value = "${profile.bestStreak}",
          icon = "🔥",
          accentColor = GoldAmber,
          modifier = Modifier.weight(1f)
        )
        StatCard(
          title = "TOTAL SOLVED",
          value = "${profile.totalPuzzlesSolved}",
          icon = "🧩",
          accentColor = ElectricCyan,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // CHALLENGE & REWARDED AD TILES
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Challenge a Friend Card
        GlassCard(
          modifier = Modifier.weight(1f),
          borderColor = NeonPurple.copy(alpha = 0.6f),
          backgroundColor = NavyCard,
          onClick = { viewModel.shareViralChallenge(context) }
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(NeonPurple.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Share, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("CHALLENGE", fontWeight = FontWeight.Black, fontSize = 13.sp, color = TextPrimary)
            Text("Duel a Friend", fontSize = 11.sp, color = TextSecondary)
          }
        }

        // Rewarded Ad for Free Coins Card
        GlassCard(
          modifier = Modifier.weight(1f),
          borderColor = GoldAmber.copy(alpha = 0.6f),
          backgroundColor = NavyCard,
          onClick = { viewModel.watchRewardedAd(AdRewardType.COINS_50) }
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(GoldAmber.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.Tv, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("+50 COINS", fontWeight = FontWeight.Black, fontSize = 13.sp, color = GoldYellow)
            Text("Watch Quick Ad", fontSize = 11.sp, color = TextSecondary)
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}

@Composable
private fun StatCard(
  title: String,
  value: String,
  icon: String,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  GlassCard(
    modifier = modifier,
    borderColor = NavyCardBorder,
    backgroundColor = NavyCard
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = icon, fontSize = 20.sp)
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Black,
        color = accentColor
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = title,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 0.5.sp
      )
    }
  }
}
