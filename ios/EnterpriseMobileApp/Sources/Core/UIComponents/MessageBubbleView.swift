import SwiftUI
import CoreDomain

/// Chat message bubble that renders text and polymorphic UCP attachments.
public struct MessageBubbleView: View {
    private let message: UCPMessage
    private let onActionTapped: (String) -> Void

    public init(message: UCPMessage, onActionTapped: @escaping (String) -> Void) {
        self.message = message
        self.onActionTapped = onActionTapped
    }

    public var body: some View {
        HStack {
            if message.role == .user { Spacer() }

            VStack(alignment: message.role == .user ? .trailing : .leading, spacing: 8) {
                if let content = message.content {
                    Text(content)
                        .padding(12)
                        .background(message.role == .user ? Color.accentColor : Color(.systemGray5))
                        .foregroundColor(message.role == .user ? .white : .primary)
                        .cornerRadius(16)
                }

                ForEach(message.attachments) { attachment in
                    attachmentView(for: attachment)
                }
            }

            if message.role != .user { Spacer() }
        }
        .padding(.horizontal, 16)
    }

    @ViewBuilder
    private func attachmentView(for attachment: UCPAttachment) -> some View {
        switch attachment {
        case .offerCard(let payload):
            OfferCardView(payload: payload, onActionTapped: onActionTapped)
        case .actionGrid(let payload):
            ActionGridView(payload: payload, onActionTapped: onActionTapped)
        case .systemAlert(let payload):
            SystemAlertView(payload: payload)
        case .richMedia(let payload):
            RichMediaView(payload: payload)
        }
    }
}

// MARK: - UCP Attachment Views

struct OfferCardView: View {
    let payload: OfferCardPayload
    let onActionTapped: (String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let imageURL = payload.imageURL {
                AsyncImage(url: imageURL) { phase in
                    if case .success(let image) = phase {
                        image.resizable().aspectRatio(contentMode: .fill)
                    } else {
                        Color.gray.opacity(0.2)
                    }
                }
                .frame(height: 120)
                .clipped()
                .cornerRadius(8)
            }

            Text(payload.title)
                .font(.headline)
            Text(payload.description)
                .font(.caption)
                .foregroundColor(.secondary)

            Button(action: { onActionTapped(payload.targetUri) }) {
                Text(payload.ctaText)
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .foregroundColor(.white)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(Color.accentColor)
                    .cornerRadius(20)
            }
            .accessibilityIdentifier("offerCardCTA_\(payload.id)")
        }
        .padding(12)
        .background(Color(.systemGray6))
        .cornerRadius(12)
        .frame(maxWidth: 280)
    }
}

struct ActionGridView: View {
    let payload: ActionGridPayload
    let onActionTapped: (String) -> Void

    var body: some View {
        LazyVGrid(columns: [GridItem(.adaptive(minimum: 80))], spacing: 8) {
            ForEach(payload.actions, id: \.label) { action in
                Button(action: { onActionTapped(action.targetUri) }) {
                    VStack(spacing: 4) {
                        Image(systemName: action.iconName)
                            .font(.title3)
                        Text(action.label)
                            .font(.caption2)
                    }
                    .frame(width: 72, height: 64)
                    .background(Color(.systemGray6))
                    .cornerRadius(8)
                }
            }
        }
        .frame(maxWidth: 280)
    }
}

struct SystemAlertView: View {
    let payload: SystemAlertPayload

    var body: some View {
        HStack(spacing: 8) {
            Image(systemName: iconName)
                .foregroundColor(alertColor)
            Text(payload.message)
                .font(.caption)
        }
        .padding(10)
        .background(alertColor.opacity(0.1))
        .cornerRadius(8)
        .frame(maxWidth: 280)
    }

    private var iconName: String {
        switch payload.level {
        case .info: return "info.circle.fill"
        case .warning: return "exclamationmark.triangle.fill"
        case .error: return "xmark.octagon.fill"
        }
    }

    private var alertColor: Color {
        switch payload.level {
        case .info: return .blue
        case .warning: return .orange
        case .error: return .red
        }
    }
}

struct RichMediaView: View {
    let payload: RichMediaPayload

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            AsyncImage(url: payload.url) { phase in
                if case .success(let image) = phase {
                    image.resizable().aspectRatio(contentMode: .fit)
                } else {
                    Color.gray.opacity(0.2).frame(height: 120)
                }
            }
            .frame(maxWidth: 280)
            .cornerRadius(8)

            if let caption = payload.caption {
                Text(caption)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
    }
}
