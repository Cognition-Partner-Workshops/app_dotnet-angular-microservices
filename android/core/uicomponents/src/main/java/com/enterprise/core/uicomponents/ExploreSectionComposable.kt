package com.enterprise.core.uicomponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.enterprise.core.domain.entities.ExploreSection
import com.enterprise.core.domain.entities.SectionItem
import com.enterprise.core.domain.entities.SectionType

/** Renders a single explore section — carousel (horizontal scroll) or grid layout. */
@Composable
fun ExploreSectionComposable(
    section: ExploreSection,
    onItemTapped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section title
        Text(
            text = section.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .testTag("sectionTitle_${section.id}")
        )

        Spacer(modifier = Modifier.height(8.dp))

        when (section.sectionType) {
            SectionType.CAROUSEL -> {
                SectionCarousel(
                    items = section.items,
                    sectionId = section.id,
                    onItemTapped = onItemTapped
                )
            }
            SectionType.GRID -> {
                SectionGrid(
                    items = section.items,
                    sectionId = section.id,
                    onItemTapped = onItemTapped
                )
            }
        }
    }
}

@Composable
private fun SectionCarousel(
    items: List<SectionItem>,
    sectionId: String,
    onItemTapped: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.testTag("sectionCarousel_$sectionId")
    ) {
        items(items, key = { it.id }) { item ->
            SectionItemCard(
                item = item,
                onTapped = { onItemTapped(item.ctaDeepLink) }
            )
        }
    }
}

@Composable
private fun SectionGrid(
    items: List<SectionItem>,
    sectionId: String,
    onItemTapped: (String) -> Unit
) {
    // Non-scrollable grid — fixed height calculated from item count
    val rows = (items.size + 1) / 2
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height((rows * 240).dp)
            .testTag("sectionGrid_$sectionId"),
        userScrollEnabled = false
    ) {
        items(items, key = { it.id }) { item ->
            SectionItemCard(
                item = item,
                onTapped = { onItemTapped(item.ctaDeepLink) },
                isGridItem = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SectionItemCard(
    item: SectionItem,
    onTapped: () -> Unit,
    isGridItem: Boolean = false
) {
    Card(
        onClick = onTapped,
        modifier = Modifier
            .then(if (isGridItem) Modifier.fillMaxWidth() else Modifier.width(170.dp))
            .testTag("sectionItem_${item.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.imageText,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.imageText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = item.context,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onTapped,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("sectionItemCta_${item.id}")
                ) {
                    Text(
                        text = item.ctaText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
