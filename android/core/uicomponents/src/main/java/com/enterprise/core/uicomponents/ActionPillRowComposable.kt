package com.enterprise.core.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.DataUsage
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.enterprise.core.domain.entities.ActionPill
import com.enterprise.core.domain.entities.ActionPillConfig

/** Horizontal row of action pills (Bill, Usage, Payment, etc.). */
@Composable
fun ActionPillRowComposable(
    config: ActionPillConfig,
    onPillTapped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.testTag("actionPillRow_${config.id}"),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(config.pills, key = { it.label }) { pill ->
            ActionPillChip(
                pill = pill,
                onTapped = { onPillTapped(pill.deepLink) }
            )
        }
    }
}

@Composable
private fun ActionPillChip(
    pill: ActionPill,
    onTapped: () -> Unit
) {
    FilterChip(
        selected = false,
        onClick = onTapped,
        label = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = iconForName(pill.iconName),
                    contentDescription = pill.label,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = pill.label,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.testTag("pill_${pill.label}")
    )
}

private fun iconForName(name: String) = when (name) {
    "bill", "doc.text" -> Icons.Outlined.Description
    "usage", "chart.bar" -> Icons.Outlined.DataUsage
    "payment", "creditcard" -> Icons.Outlined.Payment
    else -> Icons.Outlined.Settings
}
