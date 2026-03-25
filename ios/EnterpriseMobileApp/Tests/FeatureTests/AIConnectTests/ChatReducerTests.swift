import XCTest
import ComposableArchitecture
@testable import CoreDomain
@testable import FeatureAIConnect

final class ChatReducerTests: XCTestCase {
    func testSendMessageAppendsToState() async {
        let sentMessage = UCPMessage(
            id: "msg-1", role: .user, content: "Hello AI",
            attachments: [], timestamp: Date()
        )

        let store = TestStore(initialState: ChatReducer.State()) {
            ChatReducer()
        } withDependencies: {
            $0.chatRepository = FakeChatRepository(sendResult: .success(sentMessage))
            $0.deepLinkRouter = FakeDeepLinkRouter()
        }

        store.exhaustivity = .off

        await store.send(.inputTextChanged("Hello AI")) {
            $0.inputText = "Hello AI"
        }
        await store.send(.sendTapped) {
            $0.inputText = ""
            $0.isStreaming = true
        }
        await store.receive(.messageSent(.success(sentMessage))) {
            $0.messages = [sentMessage]
        }
    }
}

// MARK: - Fakes

private struct FakeChatRepository: ChatRepository {
    let sendResult: DomainResult<UCPMessage>

    func connect() async throws {}
    func disconnect() async {}
    func sendMessage(_ text: String) async -> DomainResult<UCPMessage> { sendResult }
    func messageStream() -> AsyncStream<UCPMessage> { AsyncStream { $0.finish() } }
    func getHistory(limit: Int) async -> DomainResult<[UCPMessage]> { .success([]) }
}

private struct FakeDeepLinkRouter: DeepLinkRouterProtocol {
    func navigate(to deepLink: String) async {}
}
