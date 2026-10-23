package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.CountryInfo
import com.example.model.SupportedCountries
import com.example.model.UserProfile
import com.example.ui.components.AmbientGameBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.CorrectEmerald
import com.example.ui.theme.DeepViolet
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
import com.example.ui.theme.WrongRose
import com.example.viewmodel.GameViewModel

@Composable
fun ProfileScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.userProfile.collectAsState()
  val scrollState = rememberScrollState()

  var showCountryPicker by remember { mutableStateOf(false) }
  var showEditName by remember { mutableStateOf(false) }
  var editedName by remember { mutableStateOf(profile.nickname) }
  var showResetConfirm by remember { mutableStateOf(false) }

  AmbientGameBackground(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .widthIn(max = 600.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header Avatar & Identity
      Box(
        modifier = Modifier
          .size(86.dp)
          .clip(CircleShape)
          .background(Brush.radialGradient(listOf(ElectricCyan, NeonPurple)))
          .border(2.5.dp, ElectricCyan, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text("🧠", fontSize = 42.sp)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Nickname with edit icon
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable {
            editedName = profile.nickname
            showEditName = true
          }
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = profile.nickname,
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Black,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.Default.Edit, contentDescription = "Edit Nickname", tint = ElectricCyan, modifier = Modifier.size(18.dp))
      }

      // Country Flag and selector
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(NavyCard)
          .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
          .clickable { showCountryPicker = true }
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Text(text = "${profile.countryFlag}  ${profile.country}", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.Default.Public, contentDescription = "Select Country", tint = ElectricCyan, modifier = Modifier.size(14.dp))
      }

      Spacer(modifier = Modifier.height(20.dp))

      // BRAIN PERFORMANCE COGNITIVE METRICS (0-100)
      GlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("brain_performance_card"),
        borderColor = NavyCardBorder,
        backgroundColor = NavySurface
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "BRAIN PERFORMANCE",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = 1.sp
              )
              Text(
                text = "Cognitive skill profile (not an official IQ test)",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
              )
            }

            Text(
              text = "%,d".format(profile.overallBrainScore),
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Black,
              color = GoldYellow
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // 6 Cognitive Skill Progress Bars
          val skills = listOf(
            "Logic" to (profile.categoryScores["Logic"] ?: 60),
            "Memory" to (profile.categoryScores["Memory"] ?: 60),
            "Observation" to (profile.categoryScores["Observation"] ?: 60),
            "Math" to (profile.categoryScores["Math"] ?: 60),
            "Pattern" to (profile.categoryScores["Pattern"] ?: 60),
            "Speed" to (profile.categoryScores["Speed"] ?: 60)
          )

          skills.forEach { (skillName, score) ->
            val color = when (skillName) {
              "Logic" -> NeonPurple
              "Memory" -> ElectricCyan
              "Observation" -> CorrectEmerald
              "Math" -> GoldAmber
              "Pattern" -> Color(0xFFEC4899)
              else -> ElectricBlue
            }

            SkillStatRow(name = skillName, score = score, barColor = color)
            Spacer(modifier = Modifier.height(12.dp))
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Highlights: Strongest, Weakest, Most Improved
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            HighlightPill(
              label = "STRONGEST",
              value = "${profile.strongestSkill.first} (${profile.strongestSkill.second})",
              color = CorrectEmerald,
              modifier = Modifier.weight(1f)
            )
            HighlightPill(
              label = "WEAKEST",
              value = "${profile.weakestSkill.first} (${profile.weakestSkill.second})",
              color = GoldAmber,
              modifier = Modifier.weight(1f)
            )
            HighlightPill(
              label = "IMPROVED",
              value = "${profile.mostImprovedSkill.first} (+${profile.mostImprovedSkill.second})",
              color = ElectricCyan,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // SETTINGS & SYSTEM CONTROLS
      GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = NavyCardBorder,
        backgroundColor = NavyCard
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "SETTINGS & PREFERENCES",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Black,
            color = TextSecondary,
            letterSpacing = 1.sp
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(12.dp))
              Text("Sound Effects", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Switch(
              checked = profile.soundEnabled,
              onCheckedChange = { viewModel.toggleSound() },
              colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = DeepViolet)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Vibration, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(12.dp))
              Text("Tactile Haptics", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }
            Switch(
              checked = profile.hapticEnabled,
              onCheckedChange = { viewModel.toggleHaptic() },
              colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = DeepViolet)
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable { showResetConfirm = true }
              .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = WrongRose, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text("Reset Local Game Data", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = WrongRose)
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }

  // Edit Nickname Dialog
  if (showEditName) {
    AlertDialog(
      onDismissRequest = { showEditName = false },
      title = { Text("Edit Nickname", color = TextPrimary, fontWeight = FontWeight.Bold) },
      text = {
        OutlinedTextField(
          value = editedName,
          onValueChange = { if (it.length <= 16) editedName = it },
          singleLine = true,
          label = { Text("Player Name") }
        )
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateNickname(editedName)
            showEditName = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditName = false }) {
          Text("Cancel", color = TextSecondary)
        }
      },
      containerColor = NavySurface
    )
  }

  // Country Picker Modal
  if (showCountryPicker) {
    Dialog(onDismissRequest = { showCountryPicker = false }) {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, NavyCardBorder),
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Column(
          modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
        ) {
          Text("Select Your Country", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Black)
          Text("Used for national leaderboards (no GPS required)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          Spacer(modifier = Modifier.height(14.dp))

          SupportedCountries.forEach { country ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable {
                  viewModel.updateCountry(country)
                  showCountryPicker = false
                }
                .padding(vertical = 10.dp, horizontal = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = country.flag, fontSize = 22.sp)
              Spacer(modifier = Modifier.width(12.dp))
              Text(text = country.name, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
              if (profile.countryCode == country.code) {
                Icon(Icons.Default.Check, contentDescription = null, tint = ElectricCyan)
              }
            }
          }
        }
      }
    }
  }

  // Reset Confirm Dialog
  if (showResetConfirm) {
    AlertDialog(
      onDismissRequest = { showResetConfirm = false },
      title = { Text("Reset All Progress?", color = WrongRose, fontWeight = FontWeight.Black) },
      text = { Text("This will clear all high scores, achievements, and statistics. This action cannot be undone.", color = TextSecondary) },
      confirmButton = {
        Button(
          onClick = {
            viewModel.resetAllData()
            showResetConfirm = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = WrongRose),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Confirm Reset", color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirm = false }) {
          Text("Cancel", color = TextSecondary)
        }
      },
      containerColor = NavySurface
    )
  }
}

@Composable
private fun SkillStatRow(name: String, score: Int, barColor: Color) {
  val animatedProgress by animateFloatAsState(
    targetValue = (score / 100f).coerceIn(0f, 1f),
    animationSpec = tween(600),
    label = "skillBarProgress"
  )

  val tierLabel = when {
    score >= 90 -> "Grandmaster"
    score >= 80 -> "Master"
    score >= 70 -> "Adept"
    else -> "Novice"
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "• $tierLabel", fontSize = 10.sp, color = TextMuted)
      }
      Text(text = "$score", style = MaterialTheme.typography.bodyMedium, color = barColor, fontWeight = FontWeight.Black)
    }
    Spacer(modifier = Modifier.height(6.dp))
    LinearProgressIndicator(
      progress = { animatedProgress },
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = barColor,
      trackColor = NavyCard
    )
  }
}

@Composable
private fun HighlightPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(NavyCard)
      .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
      .padding(vertical = 10.dp, horizontal = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Black, color = color, letterSpacing = 0.5.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
  }
}
