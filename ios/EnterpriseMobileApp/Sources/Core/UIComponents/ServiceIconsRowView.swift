import SwiftUI
import CoreDomain

/// API-driven row of service icons (Bill, Usage, Manage Lines, Change Plan).
public struct ServiceIconsRowView: View {
    let icons: [ServiceIcon]
    let onIconTapped: (String) -> Void

    public init(icons: [ServiceIcon], onIconTapped: @escaping (String) -> Void) {
        self.icons = icons
        self.onIconTapped = onIconTapped
    }

    public var body: some View {
        HStack(spacing: 0) {
            ForEach(icons) { icon in
                ServiceIconItemView(icon: icon) {
                    onIconTapped(icon.deepLink)
                }
                .frame(maxWidth: .infinity)
            }
        }
        .padding(.vertical, 16)
        .accessibilityIdentifier("serviceIconsRow")
    }
}

private struct ServiceIconItemView: View {
    let icon: ServiceIcon
    let onTapped: () -> Void

    var body: some View {
        Button(action: onTapped) {
            VStack(spacing: 6) {
                ZStack {
                    Circle()
                        .fill(Color.accentColor.opacity(0.12))
                        .frame(width: 56, height: 56)

                    AsyncImage(url: URL(string: icon.iconUrl)) { phase in
                        switch phase {
                        case .success(let image):
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .frame(width: 28, height: 28)
                        default:
                            Image(systemName: systemImageFallback(for: icon.label))
                                .font(.system(size: 22))
                                .foregroundColor(.accentColor)
                        }
                    }
                }

                Text(icon.label)
                    .font(.caption2)
                    .foregroundColor(.primary)
                    .lineLimit(1)
            }
        }
        .accessibilityIdentifier("serviceIcon_\(icon.id)")
    }

    private func systemImageFallback(for label: String) -> String {
        switch label.lowercased() {
        case "bill": return "doc.text"
        case "usage": return "chart.bar"
        case "manage lines": return "iphone.gen3"
        case "change plan": return "arrow.triangle.2.circlepath"
        default: return "square.grid.2x2"
        }
    }
}
