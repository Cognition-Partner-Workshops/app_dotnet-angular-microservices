import SwiftUI
import CoreDomain

/// Hero banner image carousel occupying ~40% of screen height.
/// Displays marketing banners from API with image, text, and CTA.
public struct HeroBannerCarouselView: View {
    let banners: [HeroBanner]
    let onBannerTapped: (String) -> Void

    public init(banners: [HeroBanner], onBannerTapped: @escaping (String) -> Void) {
        self.banners = banners
        self.onBannerTapped = onBannerTapped
    }

    public var body: some View {
        if banners.isEmpty { EmptyView() }
        else {
            TabView {
                ForEach(banners) { banner in
                    BannerPageView(banner: banner) {
                        onBannerTapped(banner.ctaUrl)
                    }
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .always))
            .frame(height: 320)
            .accessibilityIdentifier("heroBannerCarousel")
        }
    }
}

private struct BannerPageView: View {
    let banner: HeroBanner
    let onCtaTapped: () -> Void

    var body: some View {
        ZStack(alignment: .bottomLeading) {
            // Banner image
            AsyncImage(url: URL(string: banner.imageUrl)) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .aspectRatio(contentMode: .fill)
                case .failure:
                    Color.gray
                default:
                    Color.gray.opacity(0.3)
                        .overlay(ProgressView())
                }
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .clipped()
            .accessibilityIdentifier("bannerImage_\(banner.id)")

            // Gradient scrim
            LinearGradient(
                colors: [.clear, .black.opacity(0.7)],
                startPoint: .center,
                endPoint: .bottom
            )

            // Content overlay
            VStack(alignment: .leading, spacing: 8) {
                Text(banner.imageText)
                    .font(.title2)
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                    .accessibilityIdentifier("bannerText_\(banner.id)")

                Button(action: onCtaTapped) {
                    Text(banner.ctaText)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .padding(.horizontal, 20)
                        .padding(.vertical, 10)
                        .background(Color.red)
                        .clipShape(Capsule())
                }
                .accessibilityIdentifier("bannerCta_\(banner.id)")
            }
            .padding(20)
        }
    }
}
