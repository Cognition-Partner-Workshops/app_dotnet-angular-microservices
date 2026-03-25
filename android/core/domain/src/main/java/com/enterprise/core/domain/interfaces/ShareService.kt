package com.enterprise.core.domain.interfaces

/** Native share sheet interface for WhatsApp, iMessage, general sharing. */
interface ShareService {
    suspend fun shareText(text: String, subject: String? = null)
    suspend fun shareUrl(url: String, title: String? = null)
}
