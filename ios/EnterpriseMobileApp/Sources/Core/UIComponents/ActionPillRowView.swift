import SwiftUI
import CoreDomain

/// Horizontal row of action pills (Bill, Usage, etc.).
public struct ActionPillRowView: View {
    private let config: ActionPillConfig
    private let onPillTapped: (String) -> Void

    public init(config: ActionPillConfig, onPillTapped: @escaping (String) -> Void) {
        self.config = config
        self.onPillTapped = onPillTapped
    }

    public var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(config.pills, id: \.label) { pill in
                    Button(action: { onPillTapped(pill.deepLink) }) {
                        VStack(spacing: 6) {
                            Image(systemName: pill.iconName)
                                .font(.title3)
                                .foregroundColor(.accentColor)
                            Text(pill.label)
                                .font(.caption)
                                .foregroundColor(.primary)
                        }
                        .frame(width: 80, height: 72)
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                    }
                    .accessibilityIdentifier("actionPill_\(pill.label)")
                }
            }
            .padding(.horizontal, 16)
        }
    }
}
