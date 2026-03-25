import SwiftUI
import CoreDomain

/// Horizontal carousel for "Just For You" bundles.
public struct CarouselView: View {
    private let config: CarouselConfig
    private let onItemTapped: (String) -> Void

    public init(config: CarouselConfig, onItemTapped: @escaping (String) -> Void) {
        self.config = config
        self.onItemTapped = onItemTapped
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(config.title)
                .font(.headline)
                .padding(.horizontal, 16)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 14) {
                    ForEach(config.items) { item in
                        Button(action: { onItemTapped(item.deepLink) }) {
                            VStack(alignment: .leading, spacing: 8) {
                                AsyncImage(url: item.imageURL) { phase in
                                    switch phase {
                                    case .success(let image):
                                        image
                                            .resizable()
                                            .aspectRatio(contentMode: .fill)
                                    case .failure:
                                        Color.gray.opacity(0.3)
                                    case .empty:
                                        Color.gray.opacity(0.1)
                                            .overlay { ProgressView() }
                                    @unknown default:
                                        EmptyView()
                                    }
                                }
                                .frame(width: 160, height: 120)
                                .clipped()
                                .cornerRadius(8)

                                Text(item.title)
                                    .font(.subheadline)
                                    .fontWeight(.medium)
                                    .lineLimit(2)
                                    .foregroundColor(.primary)

                                if let price = item.price {
                                    Text("\(price.currency)\(price.amount)/\(price.period)")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                            }
                            .frame(width: 160)
                        }
                        .accessibilityIdentifier("carouselItem_\(item.id)")
                    }
                }
                .padding(.horizontal, 16)
            }
        }
    }
}
