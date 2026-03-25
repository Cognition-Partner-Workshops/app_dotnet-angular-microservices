package com.enterprise.feature.explore.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enterprise.core.domain.entities.BottomNavBarConfig
import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.ExploreSection
import com.enterprise.core.domain.entities.HeroBanner
import com.enterprise.core.domain.entities.MockForDemo
import com.enterprise.core.domain.entities.ServiceIcon
import com.enterprise.core.domain.entities.TopNavBarConfig
import com.enterprise.core.domain.entities.UIComponent
import com.enterprise.core.domain.repositories.CMSRepository
import com.enterprise.feature.explore.domain.usecases.GetExploreFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** MVI ViewModel for the CMS-driven Explore page. */
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getExploreFeedUseCase: GetExploreFeedUseCase,
    private val cmsRepository: CMSRepository
) : ViewModel() {

    data class State(
        // Legacy polymorphic feed components
        val components: List<UIComponent> = emptyList(),
        // CMS-driven content
        val topNavBar: TopNavBarConfig? = null,
        val bottomNavBar: BottomNavBarConfig? = null,
        val heroBanners: List<HeroBanner> = emptyList(),
        val serviceIcons: List<ServiceIcon> = emptyList(),
        val exploreSections: List<ExploreSection> = emptyList(),
        // UI state
        val isLoading: Boolean = false,
        val error: DomainError? = null,
        val searchQuery: String = "",
        val isMockMode: Boolean = MockForDemo.isEnabled
    )

    sealed class Intent {
        data object LoadFeed : Intent()
        data object RefreshFeed : Intent()
        data object LoadCMSContent : Intent()
        data class SearchQueryChanged(val query: String) : Intent()
        data class CtaTapped(val deepLink: String) : Intent()
        data class PillTapped(val deepLink: String) : Intent()
        data class CarouselItemTapped(val deepLink: String) : Intent()
        data class BannerTapped(val deepLink: String) : Intent()
        data class ServiceIconTapped(val deepLink: String) : Intent()
        data class SectionItemTapped(val deepLink: String) : Intent()
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    init {
        processIntent(Intent.LoadCMSContent)
        processIntent(Intent.LoadFeed)
    }

    fun processIntent(intent: Intent) {
        when (intent) {
            is Intent.LoadFeed -> loadFeed()
            is Intent.RefreshFeed -> {
                loadCMSContent()
                loadFeed()
            }
            is Intent.LoadCMSContent -> loadCMSContent()
            is Intent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
                if (intent.query.length >= 3) {
                    searchFeed(intent.query)
                }
            }
            is Intent.CtaTapped -> handleDeepLink(intent.deepLink)
            is Intent.PillTapped -> handleDeepLink(intent.deepLink)
            is Intent.CarouselItemTapped -> handleDeepLink(intent.deepLink)
            is Intent.BannerTapped -> handleDeepLink(intent.deepLink)
            is Intent.ServiceIconTapped -> handleDeepLink(intent.deepLink)
            is Intent.SectionItemTapped -> handleDeepLink(intent.deepLink)
        }
    }

    private fun loadCMSContent() {
        viewModelScope.launch {
            try {
                val topNav = cmsRepository.getTopNavBar()
                val bottomNav = cmsRepository.getBottomNavBar()
                val banners = cmsRepository.getHeroBanners()
                val icons = cmsRepository.getServiceIcons()
                val sections = cmsRepository.getExploreSections()

                _state.update { currentState ->
                    currentState.copy(
                        topNavBar = topNav,
                        bottomNavBar = bottomNav,
                        heroBanners = banners.banners,
                        serviceIcons = icons.icons,
                        exploreSections = sections.sections
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = DomainError.Unknown(e.message ?: "CMS load failed")) }
            }
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
