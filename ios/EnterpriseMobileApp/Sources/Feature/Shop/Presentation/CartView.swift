import SwiftUI
import ComposableArchitecture
import CoreDomain

/// Shopping cart view driven by TCA Reducer.
public struct CartView: View {
    let store: StoreOf<CartReducer>

    public init(store: StoreOf<CartReducer>) {
        self.store = store
    }

    public var body: some View {
        NavigationStack {
            Group {
                if store.items.isEmpty {
                    emptyCartView
                } else {
                    cartListView
                }
            }
            .navigationTitle("Cart")
            .toolbar {
                if !store.items.isEmpty {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button("Clear") { store.send(.clearCart) }
                    }
                }
            }
        }
        .onAppear { store.send(.onAppear) }
    }

    private var emptyCartView: some View {
        VStack(spacing: 16) {
            Image(systemName: "cart")
                .font(.system(size: 48))
                .foregroundColor(.gray)
            Text("Your cart is empty")
                .font(.headline)
                .foregroundColor(.secondary)
        }
    }

    private var cartListView: some View {
        List {
            ForEach(store.items) { item in
                HStack {
                    VStack(alignment: .leading) {
                        Text(item.name)
                            .font(.headline)
                        Text("$\(item.price)")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }

                    Spacer()

                    HStack(spacing: 12) {
                        Button(action: {
                            store.send(.updateQuantity(item.id, item.quantity - 1))
                        }) {
                            Image(systemName: "minus.circle")
                        }

                        Text("\(item.quantity)")
                            .font(.headline)

                        Button(action: {
                            store.send(.updateQuantity(item.id, item.quantity + 1))
                        }) {
                            Image(systemName: "plus.circle")
                        }
                    }
                }
            }
            .onDelete { indexSet in
                for index in indexSet {
                    store.send(.removeFromCart(store.items[index].id))
                }
            }

            Section {
                HStack {
                    Text("Total")
                        .font(.headline)
                    Spacer()
                    Text("$\(store.totalPrice)")
                        .font(.title2)
                        .fontWeight(.bold)
                }
            }
        }
    }
}
