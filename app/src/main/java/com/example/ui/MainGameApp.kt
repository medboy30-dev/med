package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AchievementUnlockToast
import com.example.ui.components.LevelUpCelebrationToast
import com.example.ui.components.ProUpgradeModal
import com.example.ui.components.ScoreBreakdownDialog
import com.example.ui.components.TopEconomyBar
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.DailyBattleScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ResultScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

@Composable
fun MainGameApp(viewModel: GameViewModel) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val profile by viewModel.userProfile.collectAsState()
  val unlockedToast by viewModel.unlockedToast.collectAsState()
  val levelUpToast by viewModel.levelUpToast.collectAsState()
  val showBreakdown by viewModel.showBreakdownModal.collectAsState()
  val showPro by viewModel.showProModal.collectAsState()
  val latestResult by viewModel.latestResult.collectAsState()

  // Handle system back button gracefully
  BackHandler(enabled = currentScreen != AppScreen.HOME) {
    if (currentScreen == AppScreen.GAME) {
      viewModel.navigateTo(AppScreen.HOME)
    } else {
      viewModel.navigateTo(AppScreen.HOME)
    }
  }

  val isGameplayScreen = currentScreen == AppScreen.GAME || currentScreen == AppScreen.RESULT

  Scaffold(
    topBar = {
      if (!isGameplayScreen) {
        TopEconomyBar(
          profile = profile,
          onCoinsClick = { viewModel.navigateTo(AppScreen.SHOP) },
          onProClick = { viewModel.setShowProModal(true) }
        )
      }
    },
    bottomBar = {
      if (!isGameplayScreen) {
        NavigationBar(
          containerColor = NavySurface,
          contentColor = TextPrimary,
          tonalElevation = 8.dp,
          windowInsets = WindowInsets.navigationBars,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("main_bottom_nav")
        ) {
          NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { viewModel.navigateTo(AppScreen.HOME) },
            icon = {
              Icon(
                if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                contentDescription = "Home"
              )
            },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = navItemColors()
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.DAILY_BATTLE,
            onClick = { viewModel.navigateTo(AppScreen.DAILY_BATTLE) },
            icon = {
              Icon(
                if (currentScreen == AppScreen.DAILY_BATTLE) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                contentDescription = "Daily"
              )
            },
            label = { Text("Daily", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = navItemColors()
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.LEADERBOARD,
            onClick = { viewModel.navigateTo(AppScreen.LEADERBOARD) },
            icon = {
              Icon(
                if (currentScreen == AppScreen.LEADERBOARD) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                contentDescription = "Ranks"
              )
            },
            label = { Text("Ranks", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = navItemColors()
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.ACHIEVEMENTS,
            onClick = { viewModel.navigateTo(AppScreen.ACHIEVEMENTS) },
            icon = {
              Text("🏆", fontSize = 18.sp)
            },
            label = { Text("Badges", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = navItemColors()
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.PROFILE,
            onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
            icon = {
              Icon(
                if (currentScreen == AppScreen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                contentDescription = "Profile"
              )
            },
            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = navItemColors()
          )

          NavigationBarItem(
            selected = currentScreen == AppScreen.SHOP,
            onClick = { viewModel.navigateTo(AppScreen.SHOP) },
            icon = {
              Icon(
                if (currentScreen == AppScreen.SHOP) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                contentDescription = "Shop"
              )
            },
            label = { Text("Shop", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = navItemColors()
          )
        }
      }
    },
    containerColor = NavyBackground
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        AppScreen.HOME -> HomeScreen(viewModel = viewModel, profile = profile)
        AppScreen.GAME -> GameScreen(viewModel = viewModel)
        AppScreen.RESULT -> ResultScreen(viewModel = viewModel)
        AppScreen.DAILY_BATTLE -> DailyBattleScreen(viewModel = viewModel)
        AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
        AppScreen.LEADERBOARD -> LeaderboardsScreen(viewModel = viewModel)
        AppScreen.ACHIEVEMENTS -> AchievementsScreen(viewModel = viewModel)
        AppScreen.SHOP -> ShopScreen(viewModel = viewModel)
      }

      // Achievement Unlock Floating Toast
      AchievementUnlockToast(
        achievement = unlockedToast,
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 16.dp)
      )

      // Level Up Celebration Toast
      LevelUpCelebrationToast(
        levelData = levelUpToast,
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = if (unlockedToast != null) 96.dp else 16.dp)
      )
    }
  }

  // Score Breakdown Modal
  if (showBreakdown) {
    ScoreBreakdownDialog(
      result = latestResult,
      onDismiss = { viewModel.setShowBreakdownModal(false) }
    )
  }

  // Mind Rush Pro Modal
  if (showPro) {
    ProUpgradeModal(
      onDismiss = { viewModel.setShowProModal(false) },
      onUpgrade = { viewModel.buyProPass() }
    )
  }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
  selectedIconColor = ElectricCyan,
  selectedTextColor = ElectricCyan,
  unselectedIconColor = TextSecondary,
  unselectedTextColor = TextMuted,
  indicatorColor = ElectricCyan.copy(alpha = 0.15f)
)
