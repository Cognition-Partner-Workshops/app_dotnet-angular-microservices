package com.enterprise.feature.explore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.enterprise.core.domain.entities.UIComponent
import com.enterprise.core.uicomponents.ActionPillRowComposable
import com.enterprise.core.uicomponents.CarouselComposable
import com.enterprise.core.uicomponents.HeroCardComposable

/** Dynamic feed screen using LazyColumn with polymorphic UI components. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicFeedScreen(
    viewModel: ExploreViewModel = hiltViewModel(),
    onDeepLink: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.processIntent(ExploreViewModel.Intent.RefreshFeed) },
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            state.isLoading && state.components.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.testTag("loadingIndicator"))
                }
            }

            state.error != null && state.components.isEmpty() -> {
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
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
