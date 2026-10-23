package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientGameBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.CorrectEmerald
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
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DailyBattleScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.userProfile.collectAsState()
  val leaderboard by viewModel.leaderboardList.collectAsState()
  val scrollState = rememberScrollState()

  val todayFormatted = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US).format(Date())
  val hasPlayedToday = profile.bestDailyScore > 0

  AmbientGameBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .widthIn(max = 600.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header
      Text(
        text = "DAILY BRAIN BATTLE",
        style = MaterialTheme.typography.displaySmall.copy(
          fontWeight = FontWeight.Black,
          brush = Brush.horizontalGradient(listOf(GoldYellow, GoldAmber))
        )
      )
      Text(
        text = todayFormatted,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(18.dp))

      // TODAY'S CHALLENGE BANNER
      GlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("daily_overview_card"),
        borderColor = GoldAmber.copy(alpha = 0.7f),
        backgroundColor = NavySurface
      ) {
        Column(
          modifier = Modifier.padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(GoldAmber.copy(alpha = 0.18f))
              .border(2.dp, GoldAmber, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(34.dp))
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = if (hasPlayedToday) "TODAY'S BATTLE COMPLETED" else "TODAY'S BATTLE ACTIVE",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = if (hasPlayedToday) CorrectEmerald else TextPrimary
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "5 deterministic puzzles identical for all players globally.\n1.2x score multiplier & limited tactical hints.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = TextSecondary,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(18.dp))

          if (hasPlayedToday) {
            // Show Today's Stats
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              DailyStatBox(title = "TODAY'S SCORE", value = "%,d".format(profile.bestDailyScore), color = GoldAmber, modifier = Modifier.weight(1f))
              DailyStatBox(title = "GLOBAL RANK", value = "#12", color = ElectricCyan, modifier = Modifier.weight(1f))
              DailyStatBox(title = "COUNTRY RANK", value = "#3", color = NeonPurple, modifier = Modifier.weight(1f))
            }
          } else {
            Button(
              onClick = { viewModel.startDailyBattle() },
              colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
              shape = RoundedCornerShape(18.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("start_daily_battle_btn")
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ENTER DAILY BATTLE", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 16.sp)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // DAILY LEADERBOARD PREVIEW
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "TODAY'S TOP CONTENDERS",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Black,
          color = TextPrimary
        )

        Text(
          text = "Global Standings",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = ElectricCyan
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Top 5 entries
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        leaderboard.take(5).forEach { entry ->
          GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = if (entry.isCurrentUser) ElectricCyan else NavyCardBorder,
            backgroundColor = if (entry.isCurrentUser) NavyCardElevated else NavyCard
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "#${entry.rank}",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = when (entry.rank) {
                  1 -> GoldYellow
                  2 -> TextPrimary
                  3 -> GoldAmber
                  else -> TextSecondary
                },
                modifier = Modifier.width(36.dp)
              )

              Text(text = entry.countryFlag, fontSize = 20.sp)
              Spacer(modifier = Modifier.width(10.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = entry.name,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = if (entry.isCurrentUser) ElectricCyan else TextPrimary
                )
                Text(text = entry.country, fontSize = 11.sp, color = TextMuted)
              }

              Text(
                text = "%,d".format(entry.score),
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = GoldAmber
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}

@Composable
private fun DailyStatBox(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(NavyCard)
      .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp))
      .padding(vertical = 12.dp, horizontal = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = value, fontWeight = FontWeight.Black, fontSize = 15.sp, color = color)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = title, fontSize = 8.sp, fontWeight = FontWeight.Black, color = TextMuted, textAlign = TextAlign.Center)
    }
  }
}
