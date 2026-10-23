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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Tv
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
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameViewModel

@Composable
fun ShopScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.userProfile.collectAsState()
  val isAdLoading by viewModel.isAdLoading.collectAsState()
  val scrollState = rememberScrollState()

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
        text = "MIND STORE",
        style = MaterialTheme.typography.displaySmall.copy(
          fontWeight = FontWeight.Black,
          brush = Brush.horizontalGradient(listOf(GoldYellow, ElectricCyan))
        )
      )
      Text(
        text = "Current Balance: %,d Mind Coins".format(profile.coins),
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        color = TextSecondary
      )

      Spacer(modifier = Modifier.height(18.dp))

      // PRO PASS HERO CARD
      GlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("pro_pass_hero_card"),
        borderColor = NeonPurple,
        backgroundColor = NavySurface
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
            text = if (profile.isPro) "MIND RUSH PRO ACTIVE" else "MIND RUSH PRO",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = if (profile.isPro) CorrectEmerald else TextPrimary
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "Zero Ads • 2x Coins Multiplier • Pro Battles • Deep Analytics",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = TextSecondary,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(18.dp))

          if (profile.isPro) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(CorrectEmerald.copy(alpha = 0.2f))
                .border(1.5.dp, CorrectEmerald, RoundedCornerShape(14.dp))
                .padding(horizontal = 18.dp, vertical = 10.dp)
            ) {
              Text("✓ PRO MEMBERSHIP UNLOCKED", fontWeight = FontWeight.Black, color = CorrectEmerald, fontSize = 13.sp)
            }
          } else {
            Button(
              onClick = { viewModel.buyProPass() },
              colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("unlock_pro_pass_btn")
            ) {
              Text("Upgrade to Mind Rush Pro", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // REWARDED AD FREE COINS CARD
      GlassCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("free_coins_ad_card"),
        borderColor = GoldAmber.copy(alpha = 0.6f),
        backgroundColor = NavyCard
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .background(GoldAmber.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Tv, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(26.dp))
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text("Free Mind Coins", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = TextPrimary)
            Text("Watch a short sponsored video", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
          }

          Button(
            onClick = { viewModel.watchRewardedAd(AdRewardType.COINS_50) },
            enabled = !isAdLoading,
            colors = ButtonDefaults.buttonColors(containerColor = GoldAmber),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.testTag("watch_ad_coins_btn")
          ) {
            Text(
              text = if (isAdLoading) "Loading..." else "+50 🪙",
              color = Color.Black,
              fontWeight = FontWeight.Black
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(22.dp))

      // COIN BUNDLES
      Text(
        text = "COIN PACKAGES",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Black,
        color = TextSecondary,
        letterSpacing = 1.sp,
        modifier = Modifier.align(Alignment.Start)
      )

      Spacer(modifier = Modifier.height(10.dp))

      CoinBundleRow(amount = "500 Coins", bonus = "", price = "$0.99", onClick = { viewModel.purchaseCoinPackage(500, "coins_pack_500") })
      Spacer(modifier = Modifier.height(10.dp))
      CoinBundleRow(amount = "1,500 Coins", bonus = "+20% Extra", price = "$2.49", isPopular = true, onClick = { viewModel.purchaseCoinPackage(1500, "coins_pack_1500") })
      Spacer(modifier = Modifier.height(10.dp))
      CoinBundleRow(amount = "5,000 Coins", bonus = "+50% Extra", price = "$6.99", onClick = { viewModel.purchaseCoinPackage(5000, "coins_pack_5000") })

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}

@Composable
private fun CoinBundleRow(
  amount: String,
  bonus: String,
  price: String,
  isPopular: Boolean = false,
  onClick: () -> Unit
) {
  GlassCard(
    modifier = Modifier.fillMaxWidth(),
    borderColor = if (isPopular) ElectricCyan else NavyCardBorder,
    backgroundColor = NavyCard
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("🪙", fontSize = 26.sp)
      Spacer(modifier = Modifier.width(14.dp))
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = amount, fontWeight = FontWeight.Black, fontSize = 15.sp, color = TextPrimary)
          if (bonus.isNotEmpty()) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(ElectricCyan.copy(alpha = 0.2f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(text = bonus, fontSize = 9.sp, fontWeight = FontWeight.Black, color = ElectricCyan)
            }
          }
        }
        Text(text = "Use for hints, 50/50 & extra time", fontSize = 11.sp, color = TextMuted)
      }

      Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = if (isPopular) ElectricCyan else NavySurface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.2.dp, if (isPopular) ElectricCyan else NavyCardBorder)
      ) {
        Text(text = price, fontWeight = FontWeight.Black, color = if (isPopular) Color.Black else TextPrimary)
      }
    }
  }
}
