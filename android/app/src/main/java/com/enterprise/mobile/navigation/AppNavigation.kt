package com.enterprise.mobile.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.enterprise.feature.aiconnect.presentation.AIConnectScreen
import com.enterprise.feature.explore.presentation.DynamicFeedScreen
import com.enterprise.feature.rewards.presentation.RewardsScreen
import com.enterprise.feature.shop.presentation.CartScreen

/** Root navigation: Bottom Tab Bar with Explore, Shop, Account, Rewards, AI Connect. */
@Composable
fun AppNavigation(
    onDeepLink: (String) -> Unit = {}
) {
    val navController = rememberNavController()

    val tabs = listOf(
        TabItem("explore", "Explore", Icons.Default.Explore),
        TabItem("shop", "Shop", Icons.Default.ShoppingBag),
        TabItem("account", "My Account", Icons.Default.AccountCircle),
        TabItem("rewards", "Rewards", Icons.Default.Star),
        TabItem("aiconnect", "AI Connect", Icons.Outlined.ChatBubbleOutline)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(modifier = Modifier.testTag("bottomNavBar")) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        modifier = Modifier.testTag("tab_${tab.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "explore",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("explore") {
                DynamicFeedScreen(onDeepLink = onDeepLink)
            }
            composable("shop") {
                CartScreen()
            }
            composable("account") {
                // Account screen placeholder
                Text("My Account")
            }
            composable("rewards") {
                RewardsScreen()
            }
            composable("aiconnect") {
                AIConnectScreen(onDeepLink = onDeepLink)
            }
        }
    }
}

private data class TabItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)
