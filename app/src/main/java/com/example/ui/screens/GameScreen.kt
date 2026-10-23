package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DifficultyLevel
import com.example.model.PowerUpType
import com.example.model.PuzzleCategory
import com.example.ui.components.AmbientGameBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.CorrectEmerald
import com.example.ui.theme.CorrectLight
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
import com.example.ui.theme.WrongLight
import com.example.ui.theme.WrongRose
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

@Composable
fun GameScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val puzzles by viewModel.puzzles.collectAsState()
  val currentIndex by viewModel.currentPuzzleIndex.collectAsState()
  val timeRemaining by viewModel.timeRemainingSeconds.collectAsState()
  val isLocked by viewModel.isAnsweringLocked.collectAsState()
  val selectedIndex by viewModel.selectedOptionIndex.collectAsState()
  val lastCorrect by viewModel.lastAnswerCorrect.collectAsState()
  val streak by viewModel.currentStreak.collectAsState()
  val disabledOptions by viewModel.disabledOptions.collectAsState()
  val activeHint by viewModel.activeHint.collectAsState()
  val isShieldActive by viewModel.isShieldActive.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()

  val currentPuzzle = puzzles.getOrNull(currentIndex) ?: return
  val categoryColor = getCategoryColor(currentPuzzle.category)
  val scrollState = rememberScrollState()

  var showExitConfirmDialog by remember { mutableStateOf(false) }

  // Safeguard against accidental exit during active puzzles
  BackHandler(enabled = true) {
    showExitConfirmDialog = true
  }

  // Pulsing infinite transition for warning timer and streak
  val infiniteTransition = rememberInfiniteTransition(label = "gameScreenPulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  // Timer Bar Animation
  val timerProgress = (timeRemaining / currentPuzzle.timeLimitSeconds.toFloat()).coerceIn(0f, 1f)
  val animatedProgress by animateFloatAsState(targetValue = timerProgress, label = "timerProgress")
  val timerColor by animateColorAsState(
    targetValue = when {
      timeRemaining <= 3f -> WrongRose
      timeRemaining <= 6f -> GoldAmber
      else -> ElectricCyan
    },
    label = "timerColor"
  )

  AmbientGameBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .widthIn(max = 600.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // TOP BAR: Exit, Puzzle Step, Category Pill, Combo Streak
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Exit button with safe touch target
        IconButton(
          onClick = { showExitConfirmDialog = true },
          modifier = Modifier.size(48.dp)
        ) {
          Icon(Icons.Default.Close, contentDescription = "Exit Round", tint = TextSecondary)
        }

        // Puzzle Step Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NavyCard)
            .border(1.dp, NavyCardBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "PUZZLE ${currentIndex + 1} / ${puzzles.size}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
          )
        }

        // Category Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(categoryColor.copy(alpha = 0.18f))
            .border(1.dp, categoryColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = currentPuzzle.category.iconEmoji, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = currentPuzzle.category.displayName.uppercase(),
              fontSize = 11.sp,
              fontWeight = FontWeight.Black,
              color = categoryColor,
              letterSpacing = 0.5.sp
            )
          }
        }

        // Combo Streak Pill
        if (streak > 1) {
          Box(
            modifier = Modifier
              .scale(if (streak >= 3) pulseScale else 1f)
              .clip(RoundedCornerShape(20.dp))
              .background(Brush.horizontalGradient(listOf(GoldAmber, NeonPurple)))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "🔥 x$streak",
              fontSize = 12.sp,
              fontWeight = FontWeight.Black,
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // COUNTDOWN TIMER BAR WITH WARNING PULSE
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "TIME REMAINING",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
          )
          Text(
            text = "%.1fs".format(timeRemaining),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = timerColor,
            modifier = if (timeRemaining <= 3f) Modifier.scale(pulseScale) else Modifier
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(NavyCard)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(animatedProgress)
              .height(10.dp)
              .clip(RoundedCornerShape(5.dp))
              .background(
                Brush.horizontalGradient(
                  listOf(timerColor.copy(alpha = 0.7f), timerColor)
                )
              )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // PUZZLE CARD CONTAINER
      GlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("puzzle_card"),
        borderColor = categoryColor.copy(alpha = 0.5f),
        backgroundColor = NavySurface
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Difficulty Tier Badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(NavyCard)
              .border(1.dp, NavyCardBorder, RoundedCornerShape(10.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = currentPuzzle.difficulty.label.uppercase(),
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = when (currentPuzzle.difficulty) {
                DifficultyLevel.NOVICE -> TextSecondary
                DifficultyLevel.ADEPT -> ElectricBlue
                DifficultyLevel.MASTER -> NeonPurple
                DifficultyLevel.GRANDMASTER -> GoldAmber
              },
              letterSpacing = 1.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = currentPuzzle.title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = ElectricCyan,
            letterSpacing = 1.2.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Question or Special Visual
          if (currentPuzzle.visualType == "STROOP" && currentPuzzle.visualData.contains(":")) {
            val parts = currentPuzzle.visualData.split(":")
            val word = parts.getOrNull(0) ?: "COLOR"
            val inkName = parts.getOrNull(1) ?: "RED"
            val inkColor = when (inkName) {
              "RED" -> WrongRose
              "BLUE" -> ElectricBlue
              "GREEN" -> CorrectEmerald
              "YELLOW" -> GoldYellow
              else -> TextPrimary
            }

            Text(
              text = currentPuzzle.question,
              style = MaterialTheme.typography.titleMedium,
              textAlign = TextAlign.Center,
              color = TextSecondary,
              fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Large Glowing Ink Word
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(NavyCardElevated)
                .border(2.dp, inkColor, RoundedCornerShape(20.dp))
                .padding(horizontal = 32.dp, vertical = 14.dp)
            ) {
              Text(
                text = word,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp,
                color = inkColor
              )
            }
          } else if (currentPuzzle.visualType == "SEQUENCE") {
            Text(
              text = currentPuzzle.question,
              style = MaterialTheme.typography.titleMedium,
              textAlign = TextAlign.Center,
              color = TextPrimary,
              lineHeight = 24.sp
            )
          } else {
            Text(
              text = currentPuzzle.question,
              style = MaterialTheme.typography.titleLarge,
              textAlign = TextAlign.Center,
              color = TextPrimary,
              lineHeight = 26.sp,
              fontWeight = FontWeight.SemiBold
            )
          }

          // Active Hint Banner
          if (activeHint != null) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(GoldAmber.copy(alpha = 0.15f))
                .border(1.dp, GoldAmber, RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = activeHint ?: "",
                  style = MaterialTheme.typography.bodySmall,
                  color = GoldYellow,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4 ANSWER OPTIONS (Large touch targets with spring tap scaling)
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        currentPuzzle.options.forEachIndexed { index, optionText ->
          val isDisabled = disabledOptions.contains(index)
          val isSelected = selectedIndex == index
          val isCorrectAnswer = index == currentPuzzle.correctIndex

          val (btnBg, btnBorder, textColor) = when {
            isLocked && isCorrectAnswer -> Triple(CorrectEmerald, CorrectLight, Color.Black)
            isLocked && isSelected && !isCorrectAnswer -> Triple(WrongRose, WrongLight, Color.White)
            isDisabled -> Triple(NavyCard.copy(alpha = 0.35f), NavyCardBorder.copy(alpha = 0.25f), TextMuted)
            else -> Triple(NavyCard, NavyCardBorder, TextPrimary)
          }

          val interactionSource = remember { MutableInteractionSource() }
          val isPressed by interactionSource.collectIsPressedAsState()
          val optionScale by animateFloatAsState(
            targetValue = if (isPressed && !isLocked && !isDisabled) 0.97f else 1f,
            animationSpec = spring(),
            label = "optionScale"
          )

          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .height(58.dp)
              .scale(optionScale)
              .clip(RoundedCornerShape(18.dp))
              .border(BorderStroke(1.5.dp, btnBorder), RoundedCornerShape(18.dp))
              .clickable(
                enabled = !isLocked && !isDisabled,
                interactionSource = interactionSource,
                indication = null
              ) {
                viewModel.submitAnswer(index)
              }
              .testTag("answer_option_$index"),
            color = btnBg,
            shadowElevation = if (isSelected) 6.dp else 2.dp
          ) {
            Row(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Letter badge A, B, C, D
              val letter = ('A' + index).toString()
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(
                    if (isLocked && isCorrectAnswer) Color.Black.copy(alpha = 0.2f)
                    else NavyCardElevated
                  ),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = letter,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (isLocked && isCorrectAnswer) Color.Black else ElectricCyan
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Text(
                text = optionText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.weight(1f)
              )

              // Status reveal icon
              if (isLocked) {
                if (isCorrectAnswer) {
                  Text("✓", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.Black)
                } else if (isSelected) {
                  Text("✕", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
              }
            }
          }
        }
      }

      // POST-ANSWER FEEDBACK CELEBRATION BANNER
      AnimatedVisibility(
        visible = isLocked,
        enter = scaleIn(spring()) + fadeIn(),
        exit = scaleOut() + fadeOut()
      ) {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (lastCorrect == true) CorrectEmerald.copy(alpha = 0.18f) else WrongRose.copy(alpha = 0.18f)
          ),
          border = BorderStroke(1.5.dp, if (lastCorrect == true) CorrectEmerald else WrongRose),
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = if (lastCorrect == true) "✓ CORRECT!" else "✕ NOT QUITE",
                  fontWeight = FontWeight.Black,
                  fontSize = 15.sp,
                  color = if (lastCorrect == true) CorrectLight else WrongLight,
                  letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (lastCorrect == true) {
                  Text(
                    text = "+1,000 PTS",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = GoldYellow
                  )
                }
              }

              if (lastCorrect == true && streak > 1) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(GoldAmber)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text("STREAK x$streak", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = currentPuzzle.explanation,
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary,
              lineHeight = 18.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // IN-GAME POWER-UPS TOOLBAR
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isShieldActive) ElectricCyan else NavyCardBorder,
        backgroundColor = NavyCard
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          PowerUpButton(
            type = PowerUpType.HINT,
            enabled = !isLocked && userProfile.coins >= PowerUpType.HINT.cost && activeHint == null,
            onClick = { viewModel.usePowerUp(PowerUpType.HINT) }
          )
          PowerUpButton(
            type = PowerUpType.FIFTY_FIFTY,
            enabled = !isLocked && userProfile.coins >= PowerUpType.FIFTY_FIFTY.cost && disabledOptions.isEmpty(),
            onClick = { viewModel.usePowerUp(PowerUpType.FIFTY_FIFTY) }
          )
          PowerUpButton(
            type = PowerUpType.EXTRA_TIME,
            enabled = !isLocked && userProfile.coins >= PowerUpType.EXTRA_TIME.cost,
            onClick = { viewModel.usePowerUp(PowerUpType.EXTRA_TIME) }
          )
          PowerUpButton(
            type = PowerUpType.SECOND_CHANCE,
            enabled = !isLocked && userProfile.coins >= PowerUpType.SECOND_CHANCE.cost && !isShieldActive,
            isActive = isShieldActive,
            onClick = { viewModel.usePowerUp(PowerUpType.SECOND_CHANCE) }
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  // Accidental quit confirmation dialog
  if (showExitConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showExitConfirmDialog = false },
      containerColor = NavyCardElevated,
      title = {
        Text("Quit Brain Rush?", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextPrimary)
      },
      text = {
        Text(
          "Are you sure you want to exit? Your progress and combo streak for this round will be lost.",
          color = TextSecondary,
          fontSize = 14.sp,
          lineHeight = 20.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showExitConfirmDialog = false
            viewModel.navigateTo(AppScreen.HOME)
          },
          colors = ButtonDefaults.buttonColors(containerColor = WrongRose),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.defaultMinSize(minHeight = 44.dp)
        ) {
          Text("Quit Round", fontWeight = FontWeight.Bold, color = Color.White)
        }
      },
      dismissButton = {
        TextButton(
          onClick = { showExitConfirmDialog = false },
          modifier = Modifier.defaultMinSize(minHeight = 44.dp)
        ) {
          Text("Stay & Play", fontWeight = FontWeight.Bold, color = ElectricCyan)
        }
      }
    )
  }
}

@Composable
private fun PowerUpButton(
  type: PowerUpType,
  enabled: Boolean,
  isActive: Boolean = false,
  onClick: () -> Unit
) {
  val border = if (isActive) ElectricCyan else NavyCardBorder
  val bg = if (isActive) ElectricCyan.copy(alpha = 0.25f) else NavySurface

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(
    targetValue = if (isPressed && enabled) 0.94f else 1f,
    animationSpec = spring(),
    label = "powerUpScale"
  )

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .scale(scale)
      .clip(RoundedCornerShape(14.dp))
      .background(bg)
      .border(1.dp, border, RoundedCornerShape(14.dp))
      .defaultMinSize(minWidth = 54.dp, minHeight = 52.dp)
      .clickable(
        enabled = enabled,
        interactionSource = interactionSource,
        indication = null
      ) { onClick() }
      .padding(horizontal = 8.dp, vertical = 6.dp)
      .testTag("powerup_${type.name.lowercase()}")
  ) {
    Text(text = type.icon, fontSize = 18.sp)
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = type.title,
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      color = if (enabled) TextPrimary else TextMuted
    )
    Text(
      text = "${type.cost}🪙",
      fontSize = 9.sp,
      fontWeight = FontWeight.Black,
      color = GoldYellow
    )
  }
}

private fun getCategoryColor(category: PuzzleCategory): Color {
  return when (category) {
    PuzzleCategory.LOGIC -> NeonPurple
    PuzzleCategory.MEMORY -> ElectricCyan
    PuzzleCategory.OBSERVATION -> CorrectEmerald
    PuzzleCategory.MATH -> GoldAmber
    PuzzleCategory.PATTERN -> Color(0xFFEC4899)
  }
}
