import Foundation
import CoreDomain

/// Feature-specific repository for AI Connect with local LLM fallback support.
public protocol AIConnectRepository: ChatRepository {
    func isLocalLLMAvailable() async -> Bool
    func queryLocalLLM(prompt: String) async -> DomainResult<String>
}
