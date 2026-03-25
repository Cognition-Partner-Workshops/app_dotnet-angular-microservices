package com.enterprise.core.domain.entities

import java.util.Date

// MARK: - Universal Chat Protocol (UCP) Domain Entities

/** Represents a single message in the AI chat feed. */
data class UCPMessage(
    val id: String,
    val role: MessageRole,
    val content: String?,
    val attachments: List<UCPAttachment> = emptyList(),
    val timestamp: Date = Date()
)

/** The sender role for a chat message. */
enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

/** Polymorphic attachment types the AI agent can inject into the chat feed. */
sealed class UCPAttachment {
    abstract val attachmentId: String

    data class OfferCard(val payload: OfferCardPayload) : UCPAttachment() {
        override val attachmentId: String get() = "offer-${payload.id}"
    }

    data class ActionGrid(val payload: ActionGridPayload) : UCPAttachment() {
        override val attachmentId: String get() = "grid-${payload.id}"
    }

    data class SystemAlert(val payload: SystemAlertPayload) : UCPAttachment() {
        override val attachmentId: String get() = "alert-${payload.id}"
    }

    data class RichMedia(val payload: RichMediaPayload) : UCPAttachment() {
        override val attachmentId: String get() = "media-${payload.id}"
    }
}

data class OfferCardPayload(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val ctaText: String,
    val targetUri: String
)

data class ActionGridPayload(
    val id: String,
    val actions: List<GridAction>
)

data class GridAction(
    val label: String,
    val iconName: String,
    val targetUri: String
)

data class SystemAlertPayload(
    val id: String,
    val level: AlertLevel,
    val message: String,
    val dismissable: Boolean = true
)

enum class AlertLevel {
    INFO, WARNING, ERROR
}

data class RichMediaPayload(
    val id: String,
    val mediaType: MediaType,
    val url: String,
    val caption: String?
)

enum class MediaType {
    IMAGE, VIDEO, AUDIO
}
