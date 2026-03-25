package com.enterprise.feature.explore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.enterprise.core.domain.entities.SectionType
import com.enterprise.core.domain.entities.UIComponent
import com.enterprise.core.uicomponents.ActionPillRowComposable
import com.enterprise.core.uicomponents.CarouselComposable
import com.enterprise.core.uicomponents.ExploreSectionComposable
import com.enterprise.core.uicomponents.FloatingSearchBarComposable
import com.enterprise.core.uicomponents.HeroBannerCarouselComposable
import com.enterprise.core.uicomponents.HeroCardComposable
import com.enterprise.core.uicomponents.ServiceIconsRowComposable
import com.enterprise.core.uicomponents.TopNavBarComposable

/**
 * CMS-driven Explore screen with:
 * - Top Nav Bar (welcome greeting + icons)
 * - Floating AI search bar
 * - Hero banner carousel (~40% of screen)
 * - Service icons row (Bill, Usage, Manage Lines, Change Plan)
 * - Content sections: 5 carousels + 1 grid ("Everything You Need")
 * - Legacy polymorphic feed components
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicFeedScreen(
    viewModel: ExploreViewModel = hiltViewModel(),
    onDeepLink: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // ── CMS-driven Top Nav Bar ──────────────────────────────────────
        state.topNavBar?.let { topNav ->
            TopNavBarComposable(
                config = topNav,
                onIconTapped = { deepLink -> onDeepLink(deepLink) }
            )
        }

        // ── Main scrollable content ─────────────────────────────────────
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.processIntent(ExploreViewModel.Intent.RefreshFeed) },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                state.isLoading && state.components.isEmpty() && state.heroBanners.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.testTag("loadingIndicator"))
                    }
                }

                state.error != null && state.components.isEmpty() && state.heroBanners.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Unable to load feed. Pull to refresh.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(16.dp)
                                .testTag("errorText")
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("dynamicFeed"),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // ── Floating AI Search Bar ──────────────────────
                        item(key = "search_bar") {
                            FloatingSearchBarComposable(
                                query = state.searchQuery,
                                onQueryChanged = { query ->
                                    viewModel.processIntent(
                                        ExploreViewModel.Intent.SearchQueryChanged(query)
                                    )
                                },
                                placeholder = "Search Verizon"
                            )
                        }

                        // ── Hero Banner Carousel (~40% screen) ──────────
                        if (state.heroBanners.isNotEmpty()) {
                            item(key = "hero_banners") {
                                HeroBannerCarouselComposable(
                                    banners = state.heroBanners,
                                    onBannerTapped = { deepLink ->
                                        viewModel.processIntent(
                                            ExploreViewModel.Intent.BannerTapped(deepLink)
                                        )
                                        onDeepLink(deepLink)
                                    }
                                )
                            }
                        }

                        // ── Service Icons Row ───────────────────────────
                        if (state.serviceIcons.isNotEmpty()) {
                            item(key = "service_icons") {
                                ServiceIconsRowComposable(
                                    icons = state.serviceIcons,
                                    onIconTapped = { deepLink ->
                                        viewModel.processIntent(
                                            ExploreViewModel.Intent.ServiceIconTapped(deepLink)
                                        )
                                        onDeepLink(deepLink)
                                    }
                                )
                            }
                        }

                        // ── CMS Content Sections (Carousels + Grid) ─────
                        items(
                            items = state.exploreSections,
                            key = { it.id }
                        ) { section ->
                            ExploreSectionComposable(
                                section = section,
                                onItemTapped = { deepLink ->
                                    viewModel.processIntent(
                                        ExploreViewModel.Intent.SectionItemTapped(deepLink)
                                    )
                                    onDeepLink(deepLink)
                                },
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }

                        // ── Legacy Polymorphic Feed Components ──────────
                        items(
                            items = state.components,
                            key = { it.id }
                        ) { component ->
                            when (component) {
                                is UIComponent.HeroCard -> HeroCardComposable(
                                    config = component.config,
                                    onCtaTapped = { deepLink ->
                                        viewModel.processIntent(
                                            ExploreViewModel.Intent.CtaTapped(deepLink)
                                        )
                                        onDeepLink(deepLink)
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                is UIComponent.ActionPillRow -> ActionPillRowComposable(
                                    config = component.config,
                                    onPillTapped = { deepLink ->
                                        viewModel.processIntent(
                                            ExploreViewModel.Intent.PillTapped(deepLink)
                                        )
                                        onDeepLink(deepLink)
                                    }
                                )

                                is UIComponent.Carousel -> CarouselComposable(
                                    config = component.config,
                                    onItemTapped = { deepLink ->
                                        viewModel.processIntent(
                                            ExploreViewModel.Intent.CarouselItemTapped(deepLink)
                                        )
                                        onDeepLink(deepLink)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
