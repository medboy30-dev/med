package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Achievement
import com.example.model.RoundResult
import com.example.model.UserProfile
import com.example.ui.theme.CorrectEmerald
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyCardBorder
import com.example.ui.theme.NavyCardElevated
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

/**
 * Ambient game background with subtle glowing neural gradient meshes.
 */
@Composable
fun AmbientGameBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(NavyBackground)
      .drawBehind {
        // Deep cyan radial glow top-left
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(ElectricCyan.copy(alpha = 0.08f), Color.Transparent),
            center = Offset(size.width * 0.15f, size.height * 0.12f),
            radius = size.width * 0.7f
          )
        )
        // Neon purple radial glow bottom-right
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(NeonPurple.copy(alpha = 0.08f), Color.Transparent),
            center = Offset(size.width * 0.85f, size.height * 0.8f),
            radius = size.width * 0.8f
          )
        )
      },
    contentAlignment = Alignment.TopCenter
  ) {
    content()
  }
}

/**
 * Sleek Glass Card with subtle gradient borders and interactive scaling.
 */
@Composable
fun GlassCard(
  modifier: Modifier = Modifier,
  borderColor: Color = NavyCardBorder,
  backgroundColor: Color = NavyCard,
  glowColor: Color = Color.Transparent,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed && onClick != null) 0.98f else 1f,
    animationSpec = spring(),
    label = "glassCardScale"
  )

  val cardModifier = if (onClick != null) {
    modifier
      .scale(scale)
      .clip(RoundedCornerShape(22.dp))
      .clickable(
        interactionSource = interactionSource,
        indication = null
      ) { onClick() }
  } else {
    modifier.clip(RoundedCornerShape(22.dp))
  }

  Surface(
    modifier = cardModifier
      .border(
        BorderStroke(
          1.dp,
          Brush.verticalGradient(
            listOf(
              borderColor.copy(alpha = 0.9f),
              borderColor.copy(alpha = 0.35f)
            )
          )
        ),
        RoundedCornerShape(22.dp)
      ),
    color = backgroundColor,
    shape = RoundedCornerShape(22.dp),
    shadowElevation = 4.dp
  ) {
    content()
  }
}

@Composable
fun TopEconomyBar(
  profile: UserProfile,
  onCoinsClick: () -> Unit,
  onProClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Country Flag, Nickname & Level Pill
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(RoundedCornerShape(24.dp))
        .background(NavyCard)
        .border(1.dp, NavyCardBorder, RoundedCornerShape(24.dp))
        .defaultMinSize(minHeight = 44.dp)
        .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
      Text(
        text = profile.countryFlag,
        fontSize = 17.sp,
        modifier = Modifier.padding(end = 5.dp)
      )
      Text(
        text = profile.nickname,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )

      Spacer(modifier = Modifier.width(6.dp))

      // Level Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(10.dp))
          .background(ElectricCyan.copy(alpha = 0.2f))
          .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = "Lv.${profile.level}",
          fontSize = 10.sp,
          fontWeight = FontWeight.Black,
          color = ElectricCyan
        )
      }

      if (profile.isPro) {
        Spacer(modifier = Modifier.width(5.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.horizontalGradient(listOf(ElectricCyan, NeonPurple)))
            .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
          Text("PRO", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
        }
      }
    }

    // Coins & Pro Action
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Coins Pill
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(24.dp))
          .background(NavyCard)
          .border(1.dp, GoldAmber.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
          .defaultMinSize(minHeight = 48.dp)
          .clickable { onCoinsClick() }
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .testTag("coins_pill")
      ) {
        Text("🪙", fontSize = 15.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "%,d".format(profile.coins),
          fontWeight = FontWeight.Black,
          fontSize = 14.sp,
          color = GoldYellow
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "+",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 14.sp,
          color = ElectricCyan
        )
      }

      // Pro CTA Pill (if not pro)
      if (!profile.isPro) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(NeonPurple, ElectricCyan)))
            .defaultMinSize(minHeight = 48.dp)
            .clickable { onProClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = "Pro",
              tint = Color.Black,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text("PRO", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
          }
        }
      }
    }
  }
}

@Composable
fun AnimatedScoreCounter(
  targetScore: Int,
  modifier: Modifier = Modifier
) {
  var displayedScore by remember { mutableIntStateOf(0) }

  LaunchedEffect(targetScore) {
    if (targetScore <= 0) {
      displayedScore = 0
      return@LaunchedEffect
    }
    val steps = 30
    val stepVal = targetScore / steps
    for (i in 1..steps) {
      delay(25)
      displayedScore = (stepVal * i).coerceAtMost(targetScore)
    }
    displayedScore = targetScore
  }

  Text(
    text = "%,d".format(displayedScore),
    style = MaterialTheme.typography.displayLarge.copy(
      fontWeight = FontWeight.Black,
      fontSize = 46.sp,
      letterSpacing = (-1).sp,
      brush = Brush.linearGradient(listOf(ElectricCyan, ElectricBlue, NeonPurple))
    ),
    textAlign = TextAlign.Center,
    modifier = modifier.testTag("animated_score_counter")
  )
}

@Composable
fun AchievementUnlockToast(
  achievement: Achievement?,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = achievement != null,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
    modifier = modifier
  ) {
    if (achievement != null) {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
        border = BorderStroke(1.5.dp, GoldAmber),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(GoldAmber.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = achievement.iconEmoji, fontSize = 24.sp)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "🏆 ACHIEVEMENT UNLOCKED!",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = GoldAmber,
              letterSpacing = 1.sp
            )
            Text(
              text = achievement.title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = achievement.description,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )
          }
          Column(horizontalAlignment = Alignment.End) {
            Text(
              text = "+${achievement.coinReward}",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = GoldYellow
            )
            Text(
              text = "COINS",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = GoldAmber
            )
          }
        }
      }
    }
  }
}

@Composable
fun LevelUpCelebrationToast(
  levelData: Pair<Int, String>?,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = levelData != null,
    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
    modifier = modifier
  ) {
    if (levelData != null) {
      val (level, rankTitle) = levelData
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
        border = BorderStroke(2.dp, Brush.horizontalGradient(listOf(ElectricCyan, NeonPurple))),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(Brush.radialGradient(listOf(ElectricCyan, NeonPurple))),
            contentAlignment = Alignment.Center
          ) {
            Text("⚡", fontSize = 26.sp)
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "LEVEL UP! COGNITIVE PROMOTION",
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = ElectricCyan,
              letterSpacing = 1.sp
            )
            Text(
              text = "Level $level • $rankTitle",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Black,
              color = TextPrimary
            )
            Text(
              text = "Promotion bonus: +50 Mind Coins awarded!",
              style = MaterialTheme.typography.bodySmall,
              color = GoldYellow
            )
          }
        }
      }
    }
  }
}

@Composable
fun ScoreBreakdownDialog(
  result: RoundResult?,
  onDismiss: () -> Unit
) {
  if (result == null) return

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = NavySurface),
      border = BorderStroke(1.5.dp, ElectricCyan.copy(alpha = 0.6f)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "SCORE BREAKDOWN",
            style = MaterialTheme.typography.titleLarge,
            color = ElectricCyan,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Breakdown items
        BreakdownRow(
          label = "Base Score (${result.correctCount}/${result.totalPuzzles})",
          points = "+${result.baseScore}",
          color = TextPrimary
        )
        BreakdownRow(
          label = "Difficulty Tier Multiplier",
          points = "+${result.difficultyBonus}",
          color = ElectricBlue
        )
        BreakdownRow(
          label = "Speed Velocity Bonus (avg ${result.avgSpeedSeconds}s)",
          points = "+${result.speedBonus}",
          color = CorrectEmerald
        )
        BreakdownRow(
          label = "Consecutive Streak Combo (x${result.maxStreak})",
          points = "+${result.streakBonus}",
          color = NeonPurple
        )

        Spacer(modifier = Modifier.height(12.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(NavyCardBorder)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Total Brain Score",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = "%,d".format(result.finalScore),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = GoldYellow
          )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
        ) {
          Text("Got It", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }
    }
  }
}

@Composable
private fun BreakdownRow(label: String, points: String, color: Color) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    Text(text = points, fontWeight = FontWeight.Black, fontSize = 15.sp, color = color)
  }
}

@Composable
fun ProUpgradeModal(
  onDismiss: () -> Unit,
  onUpgrade: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(26.dp),
      colors = CardDefaults.cardColors(containerColor = NavySurface),
      border = BorderStroke(1.5.dp, NeonPurple),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(NeonPurple, ElectricCyan))),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(38.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "MIND RUSH PRO",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Black,
          color = TextPrimary,
          letterSpacing = 1.sp
        )
        Text(
          text = "Unlock peak cognitive capability",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProPerkItem("🚫", "100% Ad-Free Experience", "Zero interruptions during sessions")
        ProPerkItem("⚡", "2x Mind Coins Multiplier", "Double coin rewards on every round")
        ProPerkItem("⚔️", "Daily Pro Challenges", "Exclusive daily battles with multipliers")
        ProPerkItem("📊", "Deep Cognitive Analytics", "Full percentile radar breakdowns")
        ProPerkItem("👑", "Pro Cyber Crown Badge", "Exclusive animated leaderboard status")

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onUpgrade,
          colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("confirm_pro_upgrade_btn")
        ) {
          Text("Unlock Mind Rush Pro", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Modular Google Play Billing Integration Ready",
          style = MaterialTheme.typography.labelSmall,
          color = TextMuted
        )
      }
    }
  }
}

@Composable
private fun ProPerkItem(emoji: String, title: String, subtitle: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = emoji, fontSize = 20.sp)
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
      Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
    }
  }
}
