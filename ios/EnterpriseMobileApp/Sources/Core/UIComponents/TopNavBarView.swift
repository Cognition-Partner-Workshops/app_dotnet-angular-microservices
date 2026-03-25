import SwiftUI
import CoreDomain

/// CMS-driven top navigation bar with welcome greeting and action icons.
public struct TopNavBarView: View {
    let config: TopNavBarConfig
    let onIconTapped: (String) -> Void

    public init(config: TopNavBarConfig, onIconTapped: @escaping (String) -> Void) {
        self.config = config
        self.onIconTapped = onIconTapped
    }

    public var body: some View {
        HStack {
            // Welcome greeting
            let greeting = config.welcomeGreeting.template.replacingOccurrences(
                of: "{first_name}", with: config.welcomeGreeting.firstName
            )
            Text(greeting)
                .font(.title3)
                .fontWeight(.bold)
                .accessibilityIdentifier("welcomeGreeting")

            Spacer()

            // Action icons
            HStack(spacing: 8) {
                ForEach(config.icons) { navIcon in
                    NavIconButton(navIcon: navIcon) {
                        if let deepLink = navIcon.deepLink {
                            onIconTapped(deepLink)
                        }
                    }
                }
            }
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(Color(.systemBackground))
        .shadow(color: Color.black.opacity(0.05), radius: 2, y: 2)
        .accessibilityIdentifier("topNavBar")
    }
}

private struct NavIconButton: View {
    let navIcon: NavIcon
    let onTapped: () -> Void

    var body: some View {
        Button(action: onTapped) {
            ZStack(alignment: .topTrailing) {
                Image(systemName: systemImageName(for: navIcon.type))
                    .font(.system(size: 20))
                    .foregroundColor(.primary)

                if navIcon.badgeCount > 0 {
                    Text("\(navIcon.badgeCount)")
                        .font(.caption2)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .padding(3)
                        .background(Color.red)
                        .clipShape(Circle())
                        .offset(x: 6, y: -6)
                }
            }
        }
        .frame(width: 36, height: 36)
        .accessibilityIdentifier("navIcon_\(navIcon.id)")
    }

    private func systemImageName(for type: NavIconType) -> String {
        switch type {
        case .rewards: return "star.fill"
        case .search: return "magnifyingglass"
        case .notification: return "bell.fill"
        case .chat: return "bubble.left.fill"
        case .profile: return "person.crop.circle"
        }
    }
}
