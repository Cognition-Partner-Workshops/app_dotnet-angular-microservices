package com.enterprise.core.domain.entities

import java.util.Date
import java.util.UUID

/** Queued action to sync when connectivity returns. */
data class OfflineAction(
    val id: String = UUID.randomUUID().toString(),
    val actionType: OfflineActionType,
    val payload: Map<String, String>,
    val createdAt: Date = Date(),
    val synced: Boolean = false
)

enum class OfflineActionType {
    ADD_TO_CART, REMOVE_FROM_CART, REDEEM_REWARD, SEND_MESSAGE
}
