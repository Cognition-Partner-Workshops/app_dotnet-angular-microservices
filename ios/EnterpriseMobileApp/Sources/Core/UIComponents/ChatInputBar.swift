import SwiftUI

/// Sticky chat input bar with text, mic, and camera buttons.
public struct ChatInputBar: View {
    @Binding var text: String
    let onSend: () -> Void
    let onMicTapped: () -> Void
    let onCameraTapped: () -> Void

    public init(
        text: Binding<String>,
        onSend: @escaping () -> Void,
        onMicTapped: @escaping () -> Void,
        onCameraTapped: @escaping () -> Void
    ) {
        self._text = text
        self.onSend = onSend
        self.onMicTapped = onMicTapped
        self.onCameraTapped = onCameraTapped
    }

    public var body: some View {
        HStack(spacing: 8) {
            Button(action: onCameraTapped) {
                Image(systemName: "camera.fill")
                    .font(.title3)
                    .foregroundColor(.accentColor)
            }
            .accessibilityIdentifier("chatCameraButton")

            TextField("Type a message...", text: $text)
                .textFieldStyle(.roundedBorder)
                .accessibilityIdentifier("chatTextField")

            Button(action: onMicTapped) {
                Image(systemName: "mic.fill")
                    .font(.title3)
                    .foregroundColor(.accentColor)
            }
            .accessibilityIdentifier("chatMicButton")

            Button(action: onSend) {
                Image(systemName: "arrow.up.circle.fill")
                    .font(.title2)
                    .foregroundColor(text.isEmpty ? .gray : .accentColor)
            }
            .disabled(text.isEmpty)
            .accessibilityIdentifier("chatSendButton")
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(Color(.systemBackground))
        .shadow(color: .black.opacity(0.1), radius: 4, y: -2)
    }
}
