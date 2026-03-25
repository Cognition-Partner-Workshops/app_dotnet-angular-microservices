import SwiftUI
import CoreDomain

/// Full-bleed hero card with gradient scrim, pricing, and CTA.
public struct HeroCardView: View {
    private let config: HeroConfig
    private let onCTATapped: (String) -> Void

    public init(config: HeroConfig, onCTATapped: @escaping (String) -> Void) {
        self.config = config
        self.onCTATapped = onCTATapped
    }

    public var body: some View {
        ZStack(alignment: .bottomLeading) {
            // Full-bleed background image
            AsyncImage(url: config.imageURL) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                case .failure:
                    Rectangle()
                        .fill(Color.gray.opacity(0.3))
                        .overlay {
                            Image(systemName: "photo")
                                .font(.largeTitle)
                                .foregroundColor(.gray)
                        }
                case .empty:
                    Rectangle()
                        .fill(Color.gray.opacity(0.1))
                        .overlay { ProgressView() }
                @unknown default:
                    EmptyView()
                }
            }
            .frame(height: 320)
            .clipped()

            // Gradient scrim
            LinearGradient(
                gradient: Gradient(colors: [.clear, .black.opacity(0.8)]),
                startPoint: .top,
                endPoint: .bottom
            )

            // Content overlay
            VStack(alignment: .leading, spacing: 8) {
                Text(config.title)
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.white)

                Text(config.subtitle)
                    .font(.subheadline)
                    .foregroundColor(.white.opacity(0.9))

                HStack(alignment: .firstTextBaseline) {
                    if let original = config.pricing.originalAmount {
                        Text("\(config.pricing.currency)\(original)")
                            .font(.caption)
                            .strikethrough()
                            .foregroundColor(.white.opacity(0.6))
                    }
                    Text("\(config.pricing.currency)\(config.pricing.amount)")
                        .font(.title)
                        .fontWeight(.heavy)
                        .foregroundColor(.white)
                    Text("/\(config.pricing.period)")
                        .font(.caption)
                        .foregroundColor(.white.opacity(0.8))
                }

                Button(action: { onCTATapped(config.ctaDeepLink) }) {
                    Text(config.ctaText)
                        .font(.headline)
                        .foregroundColor(.black)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 12)
                        .background(Color.white)
                        .cornerRadius(24)
                }
                .accessibilityIdentifier("heroCardCTA")
            }
            .padding(20)
        }
        .frame(height: 320)
        .cornerRadius(16)
        .shadow(radius: 4)
    }
}
