import SwiftUI
import CoreDomain

/// Renders a single explore section — carousel (horizontal scroll) or grid layout.
public struct ExploreSectionView: View {
    let section: ExploreSection
    let onItemTapped: (String) -> Void

    public init(section: ExploreSection, onItemTapped: @escaping (String) -> Void) {
        self.section = section
        self.onItemTapped = onItemTapped
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            // Section title
            Text(section.title)
                .font(.headline)
                .fontWeight(.bold)
                .padding(.horizontal, 16)
                .accessibilityIdentifier("sectionTitle_\(section.id)")

            switch section.sectionType {
            case .carousel:
                SectionCarouselView(
                    items: section.items,
                    sectionId: section.id,
                    onItemTapped: onItemTapped
                )
            case .grid:
                SectionGridView(
                    items: section.items,
                    sectionId: section.id,
                    onItemTapped: onItemTapped
                )
            }
        }
    }
}

// MARK: - Carousel Layout

private struct SectionCarouselView: View {
    let items: [SectionItem]
    let sectionId: String
    let onItemTapped: (String) -> Void

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(items) { item in
                    SectionItemCardView(item: item) {
                        onItemTapped(item.ctaDeepLink)
                    }
                    .frame(width: 170)
                }
            }
            .padding(.horizontal, 16)
        }
        .accessibilityIdentifier("sectionCarousel_\(sectionId)")
    }
}

// MARK: - Grid Layout

private struct SectionGridView: View {
    let items: [SectionItem]
    let sectionId: String
    let onItemTapped: (String) -> Void

    private let columns = [
        GridItem(.flexible(), spacing: 12),
        GridItem(.flexible(), spacing: 12)
    ]

    var body: some View {
        LazyVGrid(columns: columns, spacing: 12) {
            ForEach(items) { item in
                SectionItemCardView(item: item) {
                    onItemTapped(item.ctaDeepLink)
                }
            }
        }
        .padding(.horizontal, 16)
        .accessibilityIdentifier("sectionGrid_\(sectionId)")
    }
}

// MARK: - Section Item Card

private struct SectionItemCardView: View {
    let item: SectionItem
    let onTapped: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            // Item image
            AsyncImage(url: URL(string: item.imageUrl)) { phase in
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
            .frame(height: 110)
            .clipped()

            VStack(alignment: .leading, spacing: 4) {
                Text(item.imageText)
                    .font(.subheadline)
                    .fontWeight(.semibold)
                    .lineLimit(1)

                Text(item.context)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(2)

                Button(action: onTapped) {
                    Text(item.ctaText)
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 4)
                        .background(Color.red)
                        .clipShape(Capsule())
                }
                .padding(.top, 4)
                .accessibilityIdentifier("sectionItemCta_\(item.id)")
            }
            .padding(10)
        }
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.08), radius: 3, y: 1)
        .accessibilityIdentifier("sectionItem_\(item.id)")
    }
}
