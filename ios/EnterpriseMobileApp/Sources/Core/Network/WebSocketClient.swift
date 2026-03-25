import Foundation

/// WebSocket client for real-time AI chat streaming.
public final class WebSocketClient: NSObject, @unchecked Sendable {
    private var webSocketTask: URLSessionWebSocketTask?
    private let url: URL
    private let session: URLSession
    private var messageContinuation: AsyncStream<String>.Continuation?

    public init(url: URL) {
        self.url = url
        self.session = URLSession(configuration: .default)
        super.init()
    }

    public func connect() -> AsyncStream<String> {
        webSocketTask = session.webSocketTask(with: url)
        webSocketTask?.resume()

        return AsyncStream { continuation in
            self.messageContinuation = continuation
            continuation.onTermination = { @Sendable _ in
                self.disconnect()
            }
            self.receiveMessages()
        }
    }

    public func send(_ message: String) async throws {
        try await webSocketTask?.send(.string(message))
    }

    public func disconnect() {
        webSocketTask?.cancel(with: .goingAway, reason: nil)
        messageContinuation?.finish()
        messageContinuation = nil
    }

    private func receiveMessages() {
        webSocketTask?.receive { [weak self] result in
            switch result {
            case .success(let message):
                switch message {
                case .string(let text):
                    self?.messageContinuation?.yield(text)
                case .data(let data):
                    if let text = String(data: data, encoding: .utf8) {
                        self?.messageContinuation?.yield(text)
                    }
                @unknown default:
                    break
                }
                self?.receiveMessages()
            case .failure:
                self?.messageContinuation?.finish()
            }
        }
    }
}
