import Foundation
import ComposableArchitecture
import CoreDomain

/// TCA Reducer for AI Connect chat - appends WebSocket stream tokens to UI state.
@Reducer
public struct ChatReducer {
    @ObservableState
    public struct State: Equatable {
        public var messages: [UCPMessage] = []
        public var inputText: String = ""
        public var isConnected: Bool = false
        public var isStreaming: Bool = false
        public var error: DomainError?

        public init() {}
    }

    public enum Action: Equatable, Sendable {
        case onAppear
        case onDisappear
        case connected
        case disconnected
        case inputTextChanged(String)
        case sendTapped
        case messageSent(DomainResult<UCPMessage>)
        case messageReceived(UCPMessage)
        case historyLoaded(DomainResult<[UCPMessage]>)
        case micTapped
        case cameraTapped
        case attachmentActionTapped(String)
    }

    @Dependency(\.chatRepository) var chatRepository
    @Dependency(\.deepLinkRouter) var deepLinkRouter

    public init() {}

    public var body: some ReducerOf<Self> {
        Reduce { state, action in
            switch action {
            case .onAppear:
                return .run { send in
                    // Load history
                    let history = await chatRepository.getHistory(limit: 50)
                    await send(.historyLoaded(history))

                    // Connect WebSocket
                    try await chatRepository.connect()
                    await send(.connected)

                    // Listen for incoming messages
                    let stream = chatRepository.messageStream()
                    for await message in stream {
                        await send(.messageReceived(message))
                    }
                    await send(.disconnected)
                }

            case .onDisappear:
                return .run { _ in
                    await chatRepository.disconnect()
                }

            case .connected:
                state.isConnected = true
                return .none

            case .disconnected:
                state.isConnected = false
                return .none

            case .inputTextChanged(let text):
                state.inputText = text
                return .none

            case .sendTapped:
                let text = state.inputText
                guard !text.isEmpty else { return .none }
                state.inputText = ""
                state.isStreaming = true
                return .run { send in
                    let result = await chatRepository.sendMessage(text)
                    await send(.messageSent(result))
                }

            case .messageSent(let result):
                switch result {
                case .success(let message):
                    state.messages.append(message)
                case .failure(let error):
                    state.error = error
                }
                return .none

            case .messageReceived(let message):
                state.isStreaming = false
                state.messages.append(message)
                return .none

            case .historyLoaded(let result):
                if case .success(let messages) = result {
                    state.messages = messages
                }
                return .none

            case .micTapped:
                // Speech-to-text integration point
                return .none

            case .cameraTapped:
                // Camera access integration point
                return .none

            case .attachmentActionTapped(let targetUri):
                return .run { _ in
                    await deepLinkRouter.navigate(to: targetUri)
                }
            }
        }
    }
}

// MARK: - Dependencies

private enum ChatRepositoryKey: DependencyKey {
    static let liveValue: any ChatRepository = UnimplementedChatRepository()
}

extension DependencyValues {
    var chatRepository: any ChatRepository {
        get { self[ChatRepositoryKey.self] }
        set { self[ChatRepositoryKey.self] = newValue }
    }
}

private struct UnimplementedChatRepository: ChatRepository {
    func connect() async throws {}
    func disconnect() async {}
    func sendMessage(_ text: String) async -> DomainResult<UCPMessage> { .failure(.unknown("unimplemented")) }
    func messageStream() -> AsyncStream<UCPMessage> { AsyncStream { $0.finish() } }
    func getHistory(limit: Int) async -> DomainResult<[UCPMessage]> { .failure(.unknown("unimplemented")) }
}
