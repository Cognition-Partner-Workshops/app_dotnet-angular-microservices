import Foundation

/// Repository protocol for AI chat operations (UCP).
public protocol ChatRepository: Sendable {
    func connect() async throws
    func disconnect() async
    func sendMessage(_ text: String) async -> DomainResult<UCPMessage>
    func messageStream() -> AsyncStream<UCPMessage>
    func getHistory(limit: Int) async -> DomainResult<[UCPMessage]>
}
