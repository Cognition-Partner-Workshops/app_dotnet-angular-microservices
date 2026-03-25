package com.enterprise.feature.aiconnect.data.dto

import com.enterprise.core.domain.entities.ActionGridPayload
import com.enterprise.core.domain.entities.AlertLevel
import com.enterprise.core.domain.entities.GridAction
import com.enterprise.core.domain.entities.MediaType
import com.enterprise.core.domain.entities.MessageRole
import com.enterprise.core.domain.entities.OfferCardPayload
import com.enterprise.core.domain.entities.RichMediaPayload
import com.enterprise.core.domain.entities.SystemAlertPayload
import com.enterprise.core.domain.entities.UCPAttachment
import com.enterprise.core.domain.entities.UCPMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

@Serializable
data class ChatHistoryResponse(
    val messages: List<UCPMessageDTO>
)

@Serializable
data class UCPMessageDTO(
    val id: String? = null,
    val role: String,
    val content: String? = null,
    @SerialName("ui_attachments") val uiAttachments: List<UCPAttachmentDTO> = emptyList(),
    val timestamp: Long? = null
) {
    fun toDomain(): UCPMessage = UCPMessage(
        id = id ?: UUID.randomUUID().toString(),
        role = when (role) {
            "user" -> MessageRole.USER
            "assistant" -> MessageRole.ASSISTANT
            else -> MessageRole.SYSTEM
        },
        content = content,
        attachments = uiAttachments.mapNotNull { it.toDomain() },
        timestamp = timestamp?.let { Date(it) } ?: Date()
    )
}

/** Polymorphic attachment DTO - parsed based on "type" discriminator field. */
@Serializable
data class UCPAttachmentDTO(
    val type: String,
    val id: String? = null,
    // OfferCard fields
    val title: String? = null,
    val description: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("cta_text") val ctaText: String? = null,
    @SerialName("target_uri") val targetUri: String? = null,
    // ActionGrid fields
    val actions: List<GridActionDTO>? = null,
    // SystemAlert fields
    val level: String? = null,
    val message: String? = null,
    val dismissable: Boolean? = null,
    // RichMedia fields
    @SerialName("media_type") val mediaType: String? = null,
    val url: String? = null,
    val caption: String? = null
) {
    fun toDomain(): UCPAttachment? = when (type) {
        "offer_card" -> UCPAttachment.OfferCard(
            OfferCardPayload(
                id = id ?: UUID.randomUUID().toString(),
                title = title ?: "",
                description = description ?: "",
                imageUrl = imageUrl,
                ctaText = ctaText ?: "Learn More",
                targetUri = targetUri ?: ""
            )
        )
        "action_grid" -> UCPAttachment.ActionGrid(
            ActionGridPayload(
                id = id ?: UUID.randomUUID().toString(),
                actions = actions?.map { it.toDomain() } ?: emptyList()
            )
        )
        "system_alert" -> UCPAttachment.SystemAlert(
            SystemAlertPayload(
                id = id ?: UUID.randomUUID().toString(),
                level = when (level) {
                    "warning" -> AlertLevel.WARNING
                    "error" -> AlertLevel.ERROR
                    else -> AlertLevel.INFO
                },
                message = message ?: "",
                dismissable = dismissable ?: true
            )
        )
        "rich_media" -> UCPAttachment.RichMedia(
            RichMediaPayload(
                id = id ?: UUID.randomUUID().toString(),
                mediaType = when (mediaType) {
                    "video" -> MediaType.VIDEO
                    "audio" -> MediaType.AUDIO
                    else -> MediaType.IMAGE
                },
                url = url ?: "",
                caption = caption
            )
        )
        else -> null
    }
}

@Serializable
data class GridActionDTO(
    val label: String,
    @SerialName("icon_name") val iconName: String,
    @SerialName("target_uri") val targetUri: String
) {
    fun toDomain(): GridAction = GridAction(
        label = label,
        iconName = iconName,
        targetUri = targetUri
    )
}
