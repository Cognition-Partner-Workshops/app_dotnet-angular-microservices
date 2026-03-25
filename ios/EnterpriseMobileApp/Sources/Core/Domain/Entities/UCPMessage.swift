import Foundation

// MARK: - Universal Chat Protocol (UCP) Domain Entities

/// Represents a single message in the AI chat feed.
public struct UCPMessage: Equatable, Sendable, Identifiable {
    public let id: String
    public let role: MessageRole
    public let content: String?
    public let attachments: [UCPAttachment]
    public let timestamp: Date

    public init(
        id: String,
        role: MessageRole,
        content: String?,
        attachments: [UCPAttachment] = [],
        timestamp: Date = Date()
    ) {
        self.id = id
        self.role = role
        self.content = content
        self.attachments = attachments
        self.timestamp = timestamp
    }
}

/// The sender role for a chat message.
public enum MessageRole: String, Equatable, Sendable {
    case user
    case assistant
    case system
}

/// Polymorphic attachment types the AI agent can inject into the chat feed.
public enum UCPAttachment: Equatable, Sendable, Identifiable {
    case offerCard(OfferCardPayload)
    case actionGrid(ActionGridPayload)
    case systemAlert(SystemAlertPayload)
    case richMedia(RichMediaPayload)

    public var id: String {
        switch self {
        case .offerCard(let p): return "offer-\(p.id)"
        case .actionGrid(let p): return "grid-\(p.id)"
        case .systemAlert(let p): return "alert-\(p.id)"
        case .richMedia(let p): return "media-\(p.id)"
        }
    }
}

public struct OfferCardPayload: Equatable, Sendable {
    public let id: String
    public let title: String
    public let description: String
    public let imageURL: URL?
    public let ctaText: String
    public let targetUri: String

    public init(id: String, title: String, description: String, imageURL: URL?, ctaText: String, targetUri: String) {
        self.id = id
        self.title = title
        self.description = description
        self.imageURL = imageURL
        self.ctaText = ctaText
        self.targetUri = targetUri
    }
}

public struct ActionGridPayload: Equatable, Sendable {
    public let id: String
    public let actions: [GridAction]

    public init(id: String, actions: [GridAction]) {
        self.id = id
        self.actions = actions
    }
}

public struct GridAction: Equatable, Sendable {
    public let label: String
    public let iconName: String
    public let targetUri: String

    public init(label: String, iconName: String, targetUri: String) {
        self.label = label
        self.iconName = iconName
        self.targetUri = targetUri
    }
}

public struct SystemAlertPayload: Equatable, Sendable {
    public let id: String
    public let level: AlertLevel
    public let message: String
    public let dismissable: Bool

    public init(id: String, level: AlertLevel, message: String, dismissable: Bool = true) {
        self.id = id
        self.level = level
        self.message = message
        self.dismissable = dismissable
    }
}

public enum AlertLevel: String, Equatable, Sendable {
    case info
    case warning
    case error
}

public struct RichMediaPayload: Equatable, Sendable {
    public let id: String
    public let mediaType: MediaType
    public let url: URL
    public let caption: String?

    public init(id: String, mediaType: MediaType, url: URL, caption: String?) {
        self.id = id
        self.mediaType = mediaType
        self.url = url
        self.caption = caption
    }
}

public enum MediaType: String, Equatable, Sendable {
    case image
    case video
    case audio
}
