package com.enterprise.core.domain.usecases

import com.enterprise.core.domain.interfaces.ShareService
import javax.inject.Inject

/** Shares content via native OS share sheets (WhatsApp, iMessage, etc.). */
class ShareUseCase @Inject constructor(
    private val shareService: ShareService
) {
    suspend fun shareText(text: String, subject: String? = null) {
        shareService.shareText(text, subject)
    }

    suspend fun shareUrl(url: String, title: String? = null) {
        shareService.shareUrl(url, title)
    }
}
