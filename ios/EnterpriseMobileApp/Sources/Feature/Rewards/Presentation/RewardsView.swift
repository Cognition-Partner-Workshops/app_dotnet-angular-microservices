import SwiftUI
import ComposableArchitecture
import CoreDomain

/// Rewards listing and detail view.
public struct RewardsView: View {
    let store: StoreOf<RewardsReducer>

    public init(store: StoreOf<RewardsReducer>) {
        self.store = store
    }

    public var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Account summary
                if let account = store.account {
                    accountBanner(account)
                }

                // Rewards list
                List(store.rewards) { reward in
                    Button(action: { store.send(.rewardSelected(reward)) }) {
                        rewardRow(reward)
                    }
                }
                .listStyle(.plain)
            }
            .navigationTitle("Rewards")
            .overlay {
                if store.isLoading && store.rewards.isEmpty {
                    ProgressView()
                }
            }
            .sheet(item: Binding(
                get: { store.selectedReward },
                set: { store.send(.rewardSelected($0)) }
            )) { reward in
                rewardDetailSheet(reward)
            }
        }
        .onAppear { store.send(.onAppear) }
    }

    private func accountBanner(_ account: RewardsAccount) -> some View {
        HStack {
            VStack(alignment: .leading) {
                Text("\(account.totalPoints) pts")
                    .font(.title2)
                    .fontWeight(.bold)
                Text(account.tierLevel.rawValue.capitalized)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            Spacer()
            Image(systemName: "crown.fill")
                .font(.title)
                .foregroundColor(.yellow)
        }
        .padding()
        .background(Color(.systemGray6))
    }

    private func rewardRow(_ reward: Reward) -> some View {
        HStack(spacing: 12) {
            AsyncImage(url: reward.imageURL) { phase in
                if case .success(let image) = phase {
                    image.resizable().aspectRatio(contentMode: .fill)
                } else {
                    Color.gray.opacity(0.2)
                }
            }
            .frame(width: 60, height: 60)
            .cornerRadius(8)

            VStack(alignment: .leading, spacing: 4) {
                Text(reward.title)
                    .font(.headline)
                Text(reward.description)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(2)
            }

            Spacer()

            Text("\(reward.pointsCost) pts")
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundColor(.accentColor)
        }
    }

    private func rewardDetailSheet(_ reward: Reward) -> some View {
        VStack(spacing: 20) {
            AsyncImage(url: reward.imageURL) { phase in
                if case .success(let image) = phase {
                    image.resizable().aspectRatio(contentMode: .fit)
                } else {
                    Color.gray.opacity(0.2).frame(height: 200)
                }
            }
            .frame(height: 200)
            .cornerRadius(12)

            Text(reward.title)
                .font(.title2)
                .fontWeight(.bold)

            Text(reward.description)
                .font(.body)
                .foregroundColor(.secondary)

            Text("\(reward.pointsCost) points")
                .font(.title3)
                .fontWeight(.semibold)
                .foregroundColor(.accentColor)

            Button(action: { store.send(.redeemTapped(reward.id)) }) {
                Text("Redeem")
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.accentColor)
                    .cornerRadius(12)
            }
            .padding(.horizontal)

            Spacer()
        }
        .padding()
    }
}
