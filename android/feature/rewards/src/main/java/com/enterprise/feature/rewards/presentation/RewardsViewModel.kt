package com.enterprise.feature.rewards.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.Reward
import com.enterprise.core.domain.entities.RewardsAccount
import com.enterprise.feature.rewards.domain.usecases.GetRewardsUseCase
import com.enterprise.feature.rewards.domain.usecases.RedeemRewardUseCase
import com.enterprise.core.domain.entities.DomainError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** MVI ViewModel for the Rewards feature. */
@HiltViewModel
class RewardsViewModel @Inject constructor(
    private val getRewardsUseCase: GetRewardsUseCase,
    private val redeemRewardUseCase: RedeemRewardUseCase
) : ViewModel() {

    data class State(
        val rewards: List<Reward> = emptyList(),
        val account: RewardsAccount? = null,
        val isLoading: Boolean = false,
        val error: DomainError? = null,
        val selectedReward: Reward? = null
    )

    sealed class Intent {
        data object LoadRewards : Intent()
        data class RewardSelected(val reward: Reward) : Intent()
        data class RedeemTapped(val rewardId: String) : Intent()
        data object DismissDetail : Intent()
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        processIntent(Intent.LoadRewards)
    }

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadRewards -> loadRewards()
            is Intent.RewardSelected -> _state.update { it.copy(selectedReward = intent.reward) }
            is Intent.RedeemTapped -> redeemReward(intent.rewardId)
            is Intent.DismissDetail -> _state.update { it.copy(selectedReward = null) }
        }
    }

    private fun loadRewards() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getRewardsUseCase().collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is DomainResult.Success -> currentState.copy(
                            rewards = result.data,
                            isLoading = false,
                            error = null
                        )
                        is DomainResult.Failure -> currentState.copy(
                            isLoading = false,
                            error = result.error
                        )
                    }
                }
            }
        }
    }

    private fun redeemReward(rewardId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = redeemRewardUseCase(rewardId)) {
                is DomainResult.Success -> {
                    _state.update {
                        it.copy(
                            account = result.data,
                            isLoading = false,
                            selectedReward = null
                        )
                    }
                }
                is DomainResult.Failure -> {
                    _state.update { it.copy(isLoading = false, error = result.error) }
                }
            }
        }
    }
}
