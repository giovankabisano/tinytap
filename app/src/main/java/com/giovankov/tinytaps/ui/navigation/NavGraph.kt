package com.giovankov.tinytaps.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.giovankov.tinytaps.ui.screen.education.EducationScreen
import com.giovankov.tinytaps.ui.screen.history.HistoryScreen
import com.giovankov.tinytaps.ui.screen.history.HistoryViewModel
import com.giovankov.tinytaps.ui.screen.home.HomeScreen
import com.giovankov.tinytaps.ui.screen.home.HomeViewModel
import com.giovankov.tinytaps.ui.screen.kickcount.KickCountScreen
import com.giovankov.tinytaps.ui.screen.kickcount.KickCountViewModel
import com.giovankov.tinytaps.ui.screen.onboarding.OnboardingScreen
import com.giovankov.tinytaps.ui.screen.onboarding.OnboardingViewModel
import com.giovankov.tinytaps.ui.screen.pattern.PatternScreen
import com.giovankov.tinytaps.ui.screen.pattern.PatternViewModel
import com.giovankov.tinytaps.ui.screen.recording.RecordingScreen
import com.giovankov.tinytaps.ui.screen.recording.RecordingViewModel
import com.giovankov.tinytaps.ui.screen.settings.SettingsScreen
import com.giovankov.tinytaps.ui.screen.settings.SettingsViewModel

@Composable
fun TinyTapsNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Screens that show bottom nav
    val showBottomBar = currentDestination?.let { dest ->
        bottomNavItems.any { item ->
            dest.hierarchy.any { it.hasRoute(item.route::class) }
        }
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                TinyTapsBottomNavBar(navController, currentDestination)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = OnboardingRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<OnboardingRoute> {
                val viewModel: OnboardingViewModel = hiltViewModel()
                val shouldSkip by viewModel.shouldSkipOnboarding.collectAsStateWithLifecycle()
                if (shouldSkip) {
                    navController.navigate(HomeRoute) {
                        popUpTo(OnboardingRoute) { inclusive = true }
                    }
                } else {
                    OnboardingScreen(
                        onComplete = {
                            viewModel.completeOnboarding()
                            navController.navigate(HomeRoute) {
                                popUpTo(OnboardingRoute) { inclusive = true }
                            }
                        }
                    )
                }
            }

            composable<HomeRoute> {
                val viewModel: HomeViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HomeScreen(
                    uiState = uiState,
                    onStartRecording = { navController.navigate(RecordingRoute) },
                    onStartKickCount = { navController.navigate(KickCountRoute) },
                    onOpenEducation = { navController.navigate(EducationRoute) }
                )
            }

            composable<RecordingRoute> {
                val viewModel: RecordingViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                RecordingScreen(
                    uiState = uiState,
                    onStart = viewModel::startRecording,
                    onStop = viewModel::stopRecording,
                    onDiscard = viewModel::discardRecording,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<KickCountRoute> {
                val viewModel: KickCountViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                KickCountScreen(
                    uiState = uiState,
                    onKick = viewModel::recordKick,
                    onFinish = viewModel::finishSession,
                    onBack = { navController.popBackStack() },
                    onOpenEducation = { navController.navigate(EducationRoute) }
                )
            }

            composable<HistoryRoute> {
                val viewModel: HistoryViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                HistoryScreen(uiState = uiState)
            }

            composable<PatternRoute> {
                val viewModel: PatternViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                PatternScreen(
                    uiState = uiState,
                    onRangeChange = viewModel::setDaysRange
                )
            }

            composable<SettingsRoute> {
                val viewModel: SettingsViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                SettingsScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent
                )
            }

            composable<EducationRoute> {
                EducationScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private fun androidx.navigation.NavDestination.hasRoute(route: kotlin.reflect.KClass<*>): Boolean {
    return this.hierarchy.any { dest ->
        dest.route?.contains(route.qualifiedName ?: "") == true
    }
}
