import Foundation
import CoreDomain
import CoreNetwork

/// Concrete chat repository with WebSocket streaming and UCP parsing.
public final class ChatRepositoryImpl: AIConnectRepository, @unchecked Sendable {
    private let apiClient: APIClient
    private let webSocketClient: WebSocketClient
    private var messageContinuation: AsyncStream<UCPMessage>.Continuation?

    public init(apiClient: APIClient, webSocketClient: WebSocketClient) {
        self.apiClient = apiClient
        self.webSocketClient = webSocketClient
    }

    public func connect() async throws {
        let stream = webSocketClient.connect()
        Task {
            for await rawMessage in stream {
                if let message = parseUCPMessage(rawMessage) {
                    messageContinuation?.yield(message)
                }
            }
        }
    }

    public func disconnect() async {
        webSocketClient.disconnect()
        messageContinuation?.finish()
    }

    public func sendMessage(_ text: String) async -> DomainResult<UCPMessage> {
        let userMessage = UCPMessage(
            id: UUID().uuidString,
            role: .user,
            content: text,
            attachments: [],
            timestamp: Date()
        )
        do {
            let payload = "{\"type\":\"message\",\"content\":\"\(text)\"}"
            try await webSocketClient.send(payload)
            return .success(userMessage)
        } catch {
            return .failure(.networkUnavailable)
        }
    }

    public func messageStream() -> AsyncStream<UCPMessage> {
        AsyncStream { continuation in
            self.messageContinuation = continuation
        }
    }

    public func getHistory(limit: Int) async -> DomainResult<[UCPMessage]> {
        do {
            let response: ChatHistoryResponse = try await apiClient.request(
                endpoint: "/chat/history?limit=\(limit)"
            )
            let messages = response.messages.map { $0.toDomain() }
            return .success(messages)
        } catch {
            return .failure(.serverError(error.localizedDescription))
        }
    }

    public func isLocalLLMAvailable() async -> Bool {
        // In production: check CoreML model availability
        return false
    }

    public func queryLocalLLM(prompt: String) async -> DomainResult<String> {
        // In production: use CoreML inference
        return .failure(.unknown("Local LLM not available"))
    }

    // MARK: - UCP Parsing (Data layer polymorphic decoding)

    private func parseUCPMessage(_ raw: String) -> UCPMessage? {
        guard let data = raw.data(using: .utf8) else { return nil }
        let decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .iso8601

        do {
            let dto = try decoder.decode(UCPMessageDTO.self, from: data)
            return dto.toDomain()
        } catch {
            return nil
        }
    }
}

// MARK: - DTOs with Custom Decodable for Polymorphic Attachments

struct ChatHistoryResponse: Decodable {
    let messages: [UCPMessageDTO]
}

struct UCPMessageDTO: Decodable {
    let id: String
    let role: String
    let content: String?
    let uiAttachments: [UCPAttachmentDTO]?
    let timestamp: String?

    enum CodingKeys: String, CodingKey {
        case id, role, content, timestamp
        case uiAttachments = "ui_attachments"
    }

    func toDomain() -> UCPMessage {
        let attachments = (uiAttachments ?? []).compactMap { $0.toDomain() }
        let messageRole: MessageRole = MessageRole(rawValue: role) ?? .system
        let date = timestamp.flatMap { ISO8601DateFormatter().date(from: $0) } ?? Date()

        return UCPMessage(
            id: id,
            role: messageRole,
            content: content,
            attachments: attachments,
            timestamp: date
        )
    }
}

/// Polymorphic attachment parsing using KeyedDecodingContainer to inspect type field.
struct UCPAttachmentDTO: Decodable {
    let type: String
    let payload: AttachmentPayload

    enum CodingKeys: String, CodingKey {
        case type
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        type = try container.decode(String.self, forKey: .type)

        switch type {
        case "OfferCard":
            let offerPayload = try OfferCardDTO(from: decoder)
            payload = .offerCard(offerPayload)
        case "ActionGrid":
            let gridPayload = try ActionGridDTO(from: decoder)
            payload = .actionGrid(gridPayload)
        case "SystemAlert":
            let alertPayload = try SystemAlertDTO(from: decoder)
            payload = .systemAlert(alertPayload)
        case "RichMedia":
            let mediaPayload = try RichMediaDTO(from: decoder)
            payload = .richMedia(mediaPayload)
        default:
            payload = .unknown
        }
    }

    func toDomain() -> UCPAttachment? {
        switch payload {
        case .offerCard(let dto):
            return .offerCard(OfferCardPayload(
                id: dto.id, title: dto.title, description: dto.description,
                imageURL: dto.imageUrl.flatMap(URL.init(string:)),
                ctaText: dto.ctaText, targetUri: dto.targetUri
            ))
        case .actionGrid(let dto):
            let actions = dto.actions.map { GridAction(label: $0.label, iconName: $0.iconName, targetUri: $0.targetUri) }
            return .actionGrid(ActionGridPayload(id: dto.id, actions: actions))
        case .systemAlert(let dto):
            let level = AlertLevel(rawValue: dto.level) ?? .info
            return .systemAlert(SystemAlertPayload(id: dto.id, level: level, message: dto.message, dismissable: dto.dismissable))
        case .richMedia(let dto):
            guard let url = URL(string: dto.url) else { return nil }
            let mediaType = MediaType(rawValue: dto.mediaType) ?? .image
            return .richMedia(RichMediaPayload(id: dto.id, mediaType: mediaType, url: url, caption: dto.caption))
        case .unknown:
            return nil
        }
    }
}

enum AttachmentPayload {
    case offerCard(OfferCardDTO)
    case actionGrid(ActionGridDTO)
    case systemAlert(SystemAlertDTO)
    case richMedia(RichMediaDTO)
    case unknown
}

struct OfferCardDTO: Decodable {
    let id: String
    let title: String
    let description: String
    let imageUrl: String?
    let ctaText: String
    let targetUri: String

    enum CodingKeys: String, CodingKey {
        case id, title, description, ctaText, targetUri
        case imageUrl = "image_url"
    }
}

struct ActionGridDTO: Decodable {
    let id: String
    let actions: [GridActionDTO]
}

struct GridActionDTO: Decodable {
    let label: String
    let iconName: String
    let targetUri: String

    enum CodingKeys: String, CodingKey {
        case label, targetUri
        case iconName = "icon_name"
    }
}

struct SystemAlertDTO: Decodable {
    let id: String
    let level: String
    let message: String
    let dismissable: Bool
}

struct RichMediaDTO: Decodable {
    let id: String
    let mediaType: String
    let url: String
    let caption: String?

    enum CodingKeys: String, CodingKey {
        case id, url, caption
        case mediaType = "media_type"
    }
}
