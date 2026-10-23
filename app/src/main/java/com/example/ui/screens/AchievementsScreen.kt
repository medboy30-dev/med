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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Achievement
import com.example.model.AchievementCategory
import com.example.ui.components.AmbientGameBackground
import com.example.ui.theme.CorrectEmerald
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavyCardElevated
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameViewModel

@Composable
fun AchievementsScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val achievements by viewModel.achievements.collectAsState()
  var selectedCategory by remember { mutableStateOf<AchievementCategory?>(null) }

  val filteredList = if (selectedCategory == null) {
    achievements
  } else {
    achievements.filter { it.category == selectedCategory }
  }

  val unlockedCount = achievements.count { it.isUnlocked }

  AmbientGameBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .widthIn(max = 600.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header & Summary
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "ACHIEVEMENTS",
            style = MaterialTheme.typography.displaySmall.copy(
              fontWeight = FontWeight.Black,
              brush = Brush.horizontalGradient(listOf(GoldYellow, ElectricCyan))
            )
          )
          Text(
            text = "$unlockedCount of ${achievements.size} Unlocked",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(GoldAmber.copy(alpha = 0.2f))
            .border(1.2.dp, GoldAmber, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "${(unlockedCount * 100) / achievements.size}% COMPLETE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = GoldYellow
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Filter Chips Row
      LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          FilterPill(
            title = "All (${achievements.size})",
            isSelected = selectedCategory == null,
            onClick = { selectedCategory = null }
          )
        }
        items(AchievementCategory.entries) { cat ->
          val count = achievements.count { it.category == cat }
          FilterPill(
            title = "${cat.icon} ${cat.label} ($count)",
            isSelected = selectedCategory == cat,
            onClick = { selectedCategory = cat }
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Achievements List
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filteredList) { ach ->
          AchievementCard(achievement = ach, onClaim = { viewModel.claimAchievementReward(it) })
        }
      }
    }
  }
}

@Composable
private fun FilterPill(title: String, isSelected: Boolean, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(20.dp))
      .background(
        if (isSelected) Brush.horizontalGradient(listOf(ElectricCyan, NeonPurple))
        else Brush.horizontalGradient(listOf(NavyCard, NavyCard))
      )
      .border(1.2.dp, if (isSelected) ElectricCyan else NavyCardBorder, RoundedCornerShape(20.dp))
      .clickable { onClick() }
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp,
      color = if (isSelected) Color.Black else TextSecondary
    )
  }
}

@Composable
private fun AchievementCard(achievement: Achievement, onClaim: (String) -> Unit) {
  val borderColor = if (achievement.isUnlocked) GoldAmber else NavyCardBorder
  val bgColor = if (achievement.isUnlocked) NavyCardElevated else NavyCard

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .border(BorderStroke(1.2.dp, borderColor), RoundedCornerShape(18.dp))
      .testTag("achievement_${achievement.id}"),
    color = bgColor
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Icon Box
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(if (achievement.isUnlocked) GoldAmber.copy(alpha = 0.2f) else NavyBackground),
        contentAlignment = Alignment.Center
      ) {
        Text(text = achievement.iconEmoji, fontSize = 24.sp)
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = achievement.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = if (achievement.isUnlocked) GoldYellow else TextPrimary
          )
          if (achievement.isUnlocked) {
            Spacer(modifier = Modifier.width(6.dp))
            Text("✓", color = CorrectEmerald, fontWeight = FontWeight.Black, fontSize = 15.sp)
          }
        }

        Text(
          text = achievement.description,
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        LinearProgressIndicator(
          progress = { achievement.progressPercent },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = if (achievement.isUnlocked) CorrectEmerald else ElectricCyan,
          trackColor = NavyBackground
        )

        Spacer(modifier = Modifier.height(3.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "${achievement.currentProgress} / ${achievement.maxProgress}",
            fontSize = 10.sp,
            color = TextMuted
          )

          if (achievement.isUnlocked) {
            if (!achievement.isClaimed) {
              androidx.compose.material3.Button(
                onClick = { onClaim(achievement.id) },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GoldAmber),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp).testTag("claim_ach_${achievement.id}")
              ) {
                Text(
                  text = "CLAIM +${achievement.coinReward} 🪙",
                  color = Color.Black,
                  fontWeight = FontWeight.Black,
                  fontSize = 10.sp
                )
              }
            } else {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(CorrectEmerald.copy(alpha = 0.2f))
                  .padding(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text("✓ CLAIMED", fontSize = 9.sp, fontWeight = FontWeight.Black, color = CorrectEmerald)
              }
            }
          } else {
            Text(
              text = "+${achievement.coinReward} 🪙",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = GoldAmber
            )
          }
        }
      }
    }
  }
}
