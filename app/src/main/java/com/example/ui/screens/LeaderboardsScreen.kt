package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LeaderboardEntry
import com.example.service.LeaderboardType
import com.example.ui.components.AmbientGameBackground
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavyCardElevated
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameViewModel

@Composable
fun LeaderboardsScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val selectedType by viewModel.leaderboardType.collectAsState()
  val entries by viewModel.leaderboardList.collectAsState()
  val profile by viewModel.userProfile.collectAsState()

  AmbientGameBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .widthIn(max = 600.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "LEADERBOARDS",
          style = MaterialTheme.typography.displaySmall.copy(
            fontWeight = FontWeight.Black,
            brush = Brush.horizontalGradient(listOf(ElectricCyan, NeonPurple))
          )
        )
        Text(
          text = "Compete against players worldwide",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Filter Tabs Row
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(LeaderboardType.entries) { type ->
          val isSelected = type == selectedType
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(
                if (isSelected) Brush.horizontalGradient(listOf(ElectricCyan, NeonPurple))
                else Brush.horizontalGradient(listOf(NavyCard, NavyCard))
              )
              .border(
                1.5.dp,
                if (isSelected) ElectricCyan else NavyCardBorder,
                RoundedCornerShape(20.dp)
              )
              .clickable { viewModel.setLeaderboardType(type) }
              .padding(horizontal = 16.dp, vertical = 8.dp)
              .testTag("leaderboard_tab_${type.name.lowercase()}")
          ) {
            Text(
              text = if (type == LeaderboardType.COUNTRY) "${profile.countryFlag} Country" else type.title,
              fontWeight = FontWeight.Black,
              fontSize = 13.sp,
              color = if (isSelected) Color.Black else TextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Leaderboard Entries List
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(entries) { entry ->
          LeaderboardRow(entry = entry)
        }
      }
    }
  }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
  val isTop3 = entry.rank in 1..3
  val rankColor = when (entry.rank) {
    1 -> GoldYellow
    2 -> Color(0xFFE2E8F0) // Silver
    3 -> Color(0xFFD97706) // Bronze
    else -> TextMuted
  }

  val borderColor = if (entry.isCurrentUser) ElectricCyan else if (isTop3) rankColor.copy(alpha = 0.5f) else NavyCardBorder
  val bgColor = if (entry.isCurrentUser) NavyCardElevated else NavyCard

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .border(BorderStroke(1.2.dp, borderColor), RoundedCornerShape(18.dp)),
    color = bgColor
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 13.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Rank Badge
      Box(
        modifier = Modifier.width(36.dp),
        contentAlignment = Alignment.CenterStart
      ) {
        if (isTop3) {
          Text(
            text = when (entry.rank) {
              1 -> "🥇"
              2 -> "🥈"
              else -> "🥉"
            },
            fontSize = 22.sp
          )
        } else {
          Text(
            text = "#${entry.rank}",
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            color = rankColor
          )
        }
      }

      // Country Flag & Avatar
      Text(text = entry.countryFlag, fontSize = 22.sp)
      Spacer(modifier = Modifier.width(12.dp))

      // Name & Country label
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = entry.name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (entry.isCurrentUser) ElectricCyan else TextPrimary
          )
          if (entry.isCurrentUser) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ElectricCyan.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text("YOU", fontSize = 9.sp, fontWeight = FontWeight.Black, color = ElectricCyan)
            }
          }
        }
        Text(text = entry.country, fontSize = 11.sp, color = TextMuted)
      }

      // Score
      Text(
        text = "%,d".format(entry.score),
        fontWeight = FontWeight.Black,
        fontSize = 16.sp,
        color = if (isTop3) GoldAmber else TextPrimary
      )
    }
  }
}
