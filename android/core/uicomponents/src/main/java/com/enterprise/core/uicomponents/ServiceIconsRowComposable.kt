package com.enterprise.core.uicomponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.enterprise.core.domain.entities.ServiceIcon

/** API-driven row of service icons (Bill, Usage, Manage Lines, Change Plan). */
@Composable
fun ServiceIconsRowComposable(
    icons: List<ServiceIcon>,
    onIconTapped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .testTag("serviceIconsRow"),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(icons, key = { it.id }) { icon ->
            ServiceIconItem(
                icon = icon,
                onTapped = { onIconTapped(icon.deepLink) }
            )
        }
    }
}

@Composable
private fun ServiceIconItem(
    icon: ServiceIcon,
    onTapped: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onTapped)
            .padding(horizontal = 16.dp)
            .testTag("serviceIcon_${icon.id}")
    ) {
        Surface(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 2.dp
        ) {
            AsyncImage(
                model = icon.iconUrl,
                contentDescription = icon.label,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(32.dp)
                    .padding(12.dp)
            )
        }

        Text(
            text = icon.label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}
