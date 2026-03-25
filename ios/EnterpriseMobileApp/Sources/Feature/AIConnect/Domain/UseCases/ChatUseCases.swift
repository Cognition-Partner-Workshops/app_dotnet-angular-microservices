import Foundation
import CoreDomain

/// Use case for sending messages and receiving AI responses.
public struct SendMessageUseCase: Sendable {
    private let chatRepository: ChatRepository

    public init(chatRepository: ChatRepository) {
        self.chatRepository = chatRepository
    }

    public func execute(_ text: String) async -> DomainResult<UCPMessage> {
        await chatRepository.sendMessage(text)
    }
}

/// Use case for observing incoming chat messages via WebSocket.
public struct ObserveChatStreamUseCase: Sendable {
    private let chatRepository: ChatRepository

    public init(chatRepository: ChatRepository) {
        self.chatRepository = chatRepository
    }

    public func execute() -> AsyncStream<UCPMessage> {
        chatRepository.messageStream()
    }
}

/// Use case for loading chat history.
public struct GetChatHistoryUseCase: Sendable {
    private let chatRepository: ChatRepository

    public init(chatRepository: ChatRepository) {
        self.chatRepository = chatRepository
    }

    public func execute(limit: Int = 50) async -> DomainResult<[UCPMessage]> {
        await chatRepository.getHistory(limit: limit)
    }
}
