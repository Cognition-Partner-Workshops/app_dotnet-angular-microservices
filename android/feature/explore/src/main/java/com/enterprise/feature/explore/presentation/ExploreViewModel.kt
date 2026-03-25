package com.enterprise.feature.explore.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.UIComponent
import com.enterprise.feature.explore.domain.usecases.GetExploreFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** MVI ViewModel for the Explore (Dynamic Feed) feature. */
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getExploreFeedUseCase: GetExploreFeedUseCase
) : ViewModel() {

    data class State(
        val components: List<UIComponent> = emptyList(),
        val isLoading: Boolean = false,
        val error: DomainError? = null,
        val searchQuery: String = ""
    )

    sealed class Intent {
        data object LoadFeed : Intent()
        data object RefreshFeed : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data class CtaTapped(val deepLink: String) : Intent()
        data class PillTapped(val deepLink: String) : Intent()
        data class CarouselItemTapped(val deepLink: String) : Intent()
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        processIntent(Intent.LoadFeed)
    }

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadFeed -> loadFeed()
            is Intent.RefreshFeed -> loadFeed()
            is Intent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
                if (intent.query.length >= 3) {
                    searchFeed(intent.query)
                }
            }
            is Intent.CtaTapped -> handleDeepLink(intent.deepLink)
            is Intent.PillTapped -> handleDeepLink(intent.deepLink)
            is Intent.CarouselItemTapped -> handleDeepLink(intent.deepLink)
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getExploreFeedUseCase().collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is DomainResult.Success -> currentState.copy(
                            components = result.data,
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

    private fun searchFeed(query: String) {
        viewModelScope.launch {
            val result = getExploreFeedUseCase.search(query)
            _state.update { currentState ->
                when (result) {
                    is DomainResult.Success -> currentState.copy(
                        components = result.data,
                        error = null
                    )
                    is DomainResult.Failure -> currentState.copy(error = result.error)
                }
            }
        }
    }

    private fun handleDeepLink(deepLink: String) {
        // Deep link routing handled by GlobalNavigator
    }
}
