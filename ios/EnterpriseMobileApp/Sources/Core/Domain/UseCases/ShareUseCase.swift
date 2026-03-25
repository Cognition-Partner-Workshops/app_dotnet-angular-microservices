import Foundation

/// Use case for sharing content via native OS share sheets.
public struct ShareUseCase: Sendable {
    private let shareService: ShareService

    public init(shareService: ShareService) {
        self.shareService = shareService
    }

    public func share(text: String, url: URL? = nil) async {
        await shareService.share(text: text, url: url)
    }

    public func shareToWhatsApp(text: String) async -> Bool {
        await shareService.shareToWhatsApp(text: text)
    }
}
