package com.enterprise.core.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.enterprise.core.domain.entities.NavIcon
import com.enterprise.core.domain.entities.NavIconType
import com.enterprise.core.domain.entities.TopNavBarConfig

/** CMS-driven top navigation bar with welcome greeting and action icons. */
@Composable
fun TopNavBarComposable(
    config: TopNavBarConfig,
    onIconTapped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("topNavBar"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Welcome greeting
            val greeting = config.welcomeGreeting.template.replace(
                "{first_name}", config.welcomeGreeting.firstName
            )
            Text(
                text = greeting,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .testTag("welcomeGreeting")
            )

            // Action icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                config.icons.forEach { navIcon ->
                    NavIconButton(
                        navIcon = navIcon,
                        onTapped = { navIcon.deepLink?.let { onIconTapped(it) } }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavIconButton(
    navIcon: NavIcon,
    onTapped: () -> Unit
) {
    IconButton(
        onClick = onTapped,
        modifier = Modifier
            .size(40.dp)
            .testTag("navIcon_${navIcon.id}")
    ) {
        if (navIcon.badgeCount > 0) {
            BadgedBox(
                badge = {
                    Badge {
                        Text(
                            text = navIcon.badgeCount.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = iconForNavType(navIcon.type),
                    contentDescription = navIcon.label,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            Icon(
                imageVector = iconForNavType(navIcon.type),
                contentDescription = navIcon.label,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun iconForNavType(type: NavIconType): ImageVector = when (type) {
    NavIconType.REWARDS -> Icons.Default.Star
    NavIconType.SEARCH -> Icons.Default.Search
    NavIconType.NOTIFICATION -> Icons.Default.Notifications
    NavIconType.CHAT -> Icons.Default.ChatBubble
    NavIconType.PROFILE -> Icons.Default.Person
}
