import SwiftUI

/// Floating search bar for AI Verizon search at top of Explore page.
public struct FloatingSearchBarView: View {
    @Binding var query: String
    let placeholder: String

    public init(query: Binding<String>, placeholder: String = "Search Verizon") {
        self._query = query
        self.placeholder = placeholder
    }

    public var body: some View {
        HStack(spacing: 10) {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.accentColor)

            TextField(placeholder, text: $query)
                .textFieldStyle(.plain)
                .font(.body)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(Color(.systemBackground))
        .clipShape(Capsule())
        .shadow(color: Color.black.opacity(0.12), radius: 6, y: 3)
        .padding(.horizontal, 16)
        .padding(.vertical, 8)
        .accessibilityIdentifier("floatingSearchBar")
    }
}
