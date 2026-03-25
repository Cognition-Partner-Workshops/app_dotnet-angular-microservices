import Foundation

/// Interface for sharing content via native share sheets.
public protocol ShareService: Sendable {
    func share(text: String, url: URL?) async
    func shareToWhatsApp(text: String) async -> Bool
}
