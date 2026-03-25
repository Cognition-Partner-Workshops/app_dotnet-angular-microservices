package com.enterprise.core.uicomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.enterprise.core.domain.entities.AlertLevel
import com.enterprise.core.domain.entities.MessageRole
import com.enterprise.core.domain.entities.UCPAttachment
import com.enterprise.core.domain.entities.UCPMessage

/** Chat message bubble rendering text and polymorphic UCP attachments. */
@Composable
fun MessageBubbleComposable(
    message: UCPMessage,
    onActionTapped: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.role == MessageRole.USER
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        // Text content bubble
        message.content?.let { text ->
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bubbleColor)
                    .padding(12.dp)
                    .testTag("messageBubble_${message.id}")
            ) {
                Text(
                    text = text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Polymorphic UCP Attachments
        message.attachments.forEach { attachment ->
            Spacer(modifier = Modifier.height(4.dp))
            when (attachment) {
                is UCPAttachment.OfferCard -> OfferCardComposable(
                    payload = attachment.payload,
                    onCtaTapped = onActionTapped
                )
                is UCPAttachment.ActionGrid -> ActionGridComposable(
                    payload = attachment.payload,
                    onActionTapped = onActionTapped
                )
                is UCPAttachment.SystemAlert -> SystemAlertComposable(
                    payload = attachment.payload
                )
                is UCPAttachment.RichMedia -> RichMediaComposable(
                    payload = attachment.payload
                )
            }
        }
    }
}

/** Renders OfferCard attachment with image, title, description, CTA. */
@Composable
private fun OfferCardComposable(
    payload: com.enterprise.core.domain.entities.OfferCardPayload,
    onCtaTapped: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .testTag("offerCard_${payload.id}"),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            payload.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = payload.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = payload.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = payload.description,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onCtaTapped(payload.targetUri) },
                    modifier = Modifier.testTag("offerCta_${payload.id}")
                ) {
                    Text(payload.ctaText)
                }
            }
        }
    }
}

/** Renders ActionGrid attachment with icon buttons. */
@Composable
private fun ActionGridComposable(
    payload: com.enterprise.core.domain.entities.ActionGridPayload,
    onActionTapped: (String) -> Unit
) {
    Card(
        modifier = Modifier.widthIn(max = 280.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            payload.actions.chunked(2).forEach { rowActions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowActions.forEach { action ->
                        OutlinedButton(
                            onClick = { onActionTapped(action.targetUri) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(action.label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

/** Renders SystemAlert with level-based styling (info/warning/error). */
@Composable
private fun SystemAlertComposable(
    payload: com.enterprise.core.domain.entities.SystemAlertPayload
) {
    val (backgroundColor, icon) = when (payload.level) {
        AlertLevel.INFO -> MaterialTheme.colorScheme.primaryContainer to Icons.Outlined.Info
        AlertLevel.WARNING -> Color(0xFFFFF3E0) to Icons.Outlined.Warning
        AlertLevel.ERROR -> MaterialTheme.colorScheme.errorContainer to Icons.Outlined.Error
    }

    Card(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .testTag("systemAlert_${payload.id}"),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = payload.level.name,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = payload.message,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/** Renders RichMedia attachment with image/video and caption. */
@Composable
private fun RichMediaComposable(
    payload: com.enterprise.core.domain.entities.RichMediaPayload
) {
    Card(
        modifier = Modifier
            .widthIn(max = 280.dp)
            .testTag("richMedia_${payload.id}"),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = payload.url,
                contentDescription = payload.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            payload.caption?.let { caption ->
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
