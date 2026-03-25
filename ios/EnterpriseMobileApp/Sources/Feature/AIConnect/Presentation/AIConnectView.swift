import SwiftUI
import ComposableArchitecture
import CoreDomain
import CoreUIComponents

/// AI Connect chat view with sticky input bar and UCP message rendering.
public struct AIConnectView: View {
    @Bindable var store: StoreOf<ChatReducer>

    public init(store: StoreOf<ChatReducer>) {
        self.store = store
    }

    public var body: some View {
        VStack(spacing: 0) {
            // Chat messages
            ScrollViewReader { proxy in
                ScrollView {
                    LazyVStack(spacing: 12) {
                        ForEach(store.messages) { message in
                            MessageBubbleView(message: message) { targetUri in
                                store.send(.attachmentActionTapped(targetUri))
                            }
                        }

                        if store.isStreaming {
                            HStack {
                                ProgressView()
                                    .scaleEffect(0.8)
                                Text("AI is typing...")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                Spacer()
                            }
                            .padding(.horizontal, 16)
                        }
                    }
                    .padding(.vertical, 8)
                }
                .onChange(of: store.messages.count) { _, _ in
                    if let lastId = store.messages.last?.id {
                        withAnimation { proxy.scrollTo(lastId, anchor: .bottom) }
                    }
                }
            }

            Divider()

            // Sticky input bar
            ChatInputBar(
                text: $store.inputText.sending(\.inputTextChanged),
                onSend: { store.send(.sendTapped) },
                onMicTapped: { store.send(.micTapped) },
                onCameraTapped: { store.send(.cameraTapped) }
            )
        }
        .navigationTitle("AI Connect")
        .onAppear { store.send(.onAppear) }
        .onDisappear { store.send(.onDisappear) }
    }
}
