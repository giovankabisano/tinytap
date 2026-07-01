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

    enum class State { LOADING, SHOW, SKIP }

    val onboardingState = settingsRepository.settings
        .map { if (it.onboardingCompleted) State.SKIP else State.SHOW }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), State.LOADING)

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingCompleted()
        }
    }
}
