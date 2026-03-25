import Foundation
import CoreDomain

/// Chat feature state for the AI Connect tab.
public struct ChatState: Equatable, Sendable {
    public var messages: [UCPMessage]
    public var isConnected: Bool
    public var isStreaming: Bool
    public var currentStreamText: String
    public var error: DomainError?

    public init(
        messages: [UCPMessage] = [],
        isConnected: Bool = false,
        isStreaming: Bool = false,
        currentStreamText: String = "",
        error: DomainError? = nil
    ) {
        self.messages = messages
        self.isConnected = isConnected
        self.isStreaming = isStreaming
        self.currentStreamText = currentStreamText
        self.error = error
    }
}
