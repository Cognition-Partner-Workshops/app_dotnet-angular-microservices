import Foundation

/// Repository protocol for offline action queue.
public protocol OfflineActionRepository: Sendable {
    func enqueue(_ action: OfflineAction) async -> DomainResult<Void>
    func getPendingActions() async -> [OfflineAction]
    func markSynced(actionId: String) async -> DomainResult<Void>
    func syncAll() async -> DomainResult<Int>
}
