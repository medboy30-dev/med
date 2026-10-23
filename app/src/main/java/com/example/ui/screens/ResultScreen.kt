package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RoundResult
import com.example.service.AdRewardType
import com.example.ui.components.AmbientGameBackground
import com.example.ui.components.AnimatedScoreCounter
import com.example.ui.components.GlassCard
import com.example.ui.theme.CorrectEmerald
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

@Composable
fun ResultScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val result by viewModel.latestResult.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val scrollState = rememberScrollState()

  val res = result ?: return

  val infiniteTransition = rememberInfiniteTransition(label = "resultPulse")
  val pbScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.04f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pbScale"
  )

  AmbientGameBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .widthIn(max = 600.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header
      Text(
        text = "BRAIN RUSH COMPLETE",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Black,
        color = ElectricCyan,
        letterSpacing = 2.sp
      )

      Spacer(modifier = Modifier.height(14.dp))

      // PERSONAL BEST CELEBRATION BANNER
      if (res.isNewPersonalBest) {
        Box(
          modifier = Modifier
            .scale(pbScale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(GoldAmber, NeonPurple)))
            .border(2.dp, GoldYellow, RoundedCornerShape(18.dp))
            .padding(vertical = 12.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "🔥 NEW PERSONAL BEST! 🔥",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // MAIN SCORE DISPLAY CARD
      GlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("score_display_card"),
        borderColor = if (res.isNewPersonalBest) GoldAmber else NavyCardBorder,
        backgroundColor = NavySurface
      ) {
        Column(
          modifier = Modifier.padding(26.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "BRAIN SCORE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = TextSecondary,
            letterSpacing = 2.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Large animated score counter
          AnimatedScoreCounter(targetScore = res.finalScore)

          Spacer(modifier = Modifier.height(10.dp))

          // Score Calculation breakdown trigger
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(NavyCard)
              .clickable { viewModel.setShowBreakdownModal(true) }
              .padding(horizontal = 14.dp, vertical = 7.dp)
              .testTag("view_breakdown_btn")
          ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "View Score Calculation",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = ElectricCyan
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // PERFORMANCE METRICS GRID
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricBox(
          title = "ACCURACY",
          value = "${res.accuracyPercent}%",
          subtitle = "${res.correctCount}/${res.totalPuzzles} Solved",
          color = CorrectEmerald,
          modifier = Modifier.weight(1f)
        )
        MetricBox(
          title = "SPEED",
          value = "${res.avgSpeedSeconds}s",
          subtitle = "Avg / Puzzle",
          color = ElectricBlue,
          modifier = Modifier.weight(1f)
        )
        MetricBox(
          title = "STREAK",
          value = "x${res.maxStreak}",
          subtitle = "Max Combo",
          color = GoldAmber,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // REWARD EARNED CARD
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = GoldAmber.copy(alpha = 0.6f),
        backgroundColor = NavyCard
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🪙", fontSize = 30.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "+${res.coinsEarned} MIND COINS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = GoldYellow
              )
              Text(
                text = "Total balance: ${userProfile.coins} coins",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
            }
          }

          // Double Round Coins CTA
          Button(
            onClick = { viewModel.watchRewardedAd(AdRewardType.DOUBLE_ROUND_REWARD) },
            colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.testTag("double_coins_btn")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Tv, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("2X COINS", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // ACTION BUTTONS
      // 1. PLAY AGAIN (Primary CTA)
      Button(
        onClick = { viewModel.startBrainRush() },
        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("play_again_btn")
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Replay, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("PLAY AGAIN", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 16.sp)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. DAILY CHALLENGE (if active)
      OutlinedButton(
        onClick = { viewModel.startDailyBattle() },
        border = BorderStroke(1.5.dp, GoldAmber),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("daily_challenge_btn")
      ) {
        Text("DAILY BRAIN BATTLE", color = GoldAmber, fontWeight = FontWeight.Black, fontSize = 15.sp)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3. SHARE RESULT & HOME
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = { viewModel.shareResult(context) },
          border = BorderStroke(1.5.dp, NeonPurple),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("share_result_btn")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Share, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("SHARE", color = NeonPurple, fontWeight = FontWeight.Black, fontSize = 14.sp)
          }
        }

        OutlinedButton(
          onClick = { viewModel.navigateTo(AppScreen.HOME) },
          border = BorderStroke(1.dp, NavyCardBorder),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .weight(1f)
            .height(50.dp)
            .testTag("home_btn")
        ) {
          Text("HOME", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}

@Composable
private fun MetricBox(
  title: String,
  value: String,
  subtitle: String,
  color: Color,
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
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        color = color
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = title,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Text(
        text = subtitle,
        fontSize = 9.sp,
        color = TextMuted
      )
    }
  }
}
