package com.giovankov.tinytaps.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.giovankov.tinytaps.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val shouldSkipOnboarding = settingsRepository.settings
        .map { it.onboardingCompleted }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted()
        }
    }
}
