package com.enterprise.mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.enterprise.mobile.navigation.AppNavigation
import com.enterprise.mobile.theme.EnterpriseMobileTheme
import dagger.hilt.android.AndroidEntryPoint

/** Main activity - dumb container hosting Compose navigation. No business logic here. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EnterpriseMobileTheme {
                AppNavigation(
                    onDeepLink = { uri -> handleDeepLink(uri) }
                )
            }
        }

        // Handle deep link from launch intent
        intent?.data?.toString()?.let { uri ->
            handleDeepLink(uri)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.toString()?.let { uri ->
            handleDeepLink(uri)
        }
    }

    private fun handleDeepLink(uri: String) {
        // Routed through GlobalNavigator via ViewModel
    }
}
