// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "EnterpriseMobileApp",
    platforms: [
        .iOS(.v17),
        .macOS(.v14)
    ],
    products: [
        .library(name: "CoreDomain", targets: ["CoreDomain"]),
        .library(name: "CoreNetwork", targets: ["CoreNetwork"]),
        .library(name: "CoreUIComponents", targets: ["CoreUIComponents"]),
        .library(name: "CoreObservability", targets: ["CoreObservability"]),
        .library(name: "FeatureAuth", targets: ["FeatureAuth"]),
        .library(name: "FeatureExplore", targets: ["FeatureExplore"]),
        .library(name: "FeatureShop", targets: ["FeatureShop"]),
        .library(name: "FeatureAIConnect", targets: ["FeatureAIConnect"]),
        .library(name: "FeatureRewards", targets: ["FeatureRewards"]),
    ],
    dependencies: [
        .package(url: "https://github.com/pointfreeco/swift-composable-architecture", from: "1.7.0"),
        .package(url: "https://github.com/pointfreeco/swift-snapshot-testing", from: "1.15.0"),
    ],
    targets: [
        // ─── Core Modules ────────────────────────────────────────────
        .target(
            name: "CoreDomain",
            dependencies: [],
            path: "EnterpriseMobileApp/Sources/Core/Domain"
        ),
        .target(
            name: "CoreNetwork",
            dependencies: ["CoreDomain"],
            path: "EnterpriseMobileApp/Sources/Core/Network"
        ),
        .target(
            name: "CoreUIComponents",
            dependencies: [
                "CoreDomain",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
            ],
            path: "EnterpriseMobileApp/Sources/Core/UIComponents"
        ),
        .target(
            name: "CoreObservability",
            dependencies: ["CoreDomain"],
            path: "EnterpriseMobileApp/Sources/Core/Observability"
        ),

        // ─── Feature: Auth ───────────────────────────────────────────
        .target(
            name: "FeatureAuth",
            dependencies: [
                "CoreDomain",
                "CoreNetwork",
                "CoreObservability",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
            ],
            path: "EnterpriseMobileApp/Sources/Feature/Auth"
        ),

        // ─── Feature: Explore ────────────────────────────────────────
        .target(
            name: "FeatureExplore",
            dependencies: [
                "CoreDomain",
                "CoreNetwork",
                "CoreUIComponents",
                "CoreObservability",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
            ],
            path: "EnterpriseMobileApp/Sources/Feature/Explore"
        ),

        // ─── Feature: Shop ──────────────────────────────────────────
        .target(
            name: "FeatureShop",
            dependencies: [
                "CoreDomain",
                "CoreNetwork",
                "CoreUIComponents",
                "CoreObservability",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
            ],
            path: "EnterpriseMobileApp/Sources/Feature/Shop"
        ),

        // ─── Feature: AIConnect ──────────────────────────────────────
        .target(
            name: "FeatureAIConnect",
            dependencies: [
                "CoreDomain",
                "CoreNetwork",
                "CoreUIComponents",
                "CoreObservability",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
            ],
            path: "EnterpriseMobileApp/Sources/Feature/AIConnect"
        ),

        // ─── Feature: Rewards ────────────────────────────────────────
        .target(
            name: "FeatureRewards",
            dependencies: [
                "CoreDomain",
                "CoreNetwork",
                "CoreUIComponents",
                "CoreObservability",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
            ],
            path: "EnterpriseMobileApp/Sources/Feature/Rewards"
        ),

        // ─── Tests ──────────────────────────────────────────────────
        .testTarget(
            name: "CoreTests",
            dependencies: [
                "CoreDomain",
                "CoreNetwork",
                "CoreObservability",
            ],
            path: "EnterpriseMobileApp/Tests/CoreTests"
        ),
        .testTarget(
            name: "AuthTests",
            dependencies: [
                "FeatureAuth",
                "CoreDomain",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
            ],
            path: "EnterpriseMobileApp/Tests/FeatureTests/AuthTests"
        ),
        .testTarget(
            name: "ExploreTests",
            dependencies: [
                "FeatureExplore",
                "CoreDomain",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
                .product(name: "SnapshotTesting", package: "swift-snapshot-testing"),
            ],
            path: "EnterpriseMobileApp/Tests/FeatureTests/ExploreTests"
        ),
        .testTarget(
            name: "ShopTests",
            dependencies: [
                "FeatureShop",
                "CoreDomain",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
            ],
            path: "EnterpriseMobileApp/Tests/FeatureTests/ShopTests"
        ),
        .testTarget(
            name: "AIConnectTests",
            dependencies: [
                "FeatureAIConnect",
                "CoreDomain",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
            ],
            path: "EnterpriseMobileApp/Tests/FeatureTests/AIConnectTests"
        ),
        .testTarget(
            name: "RewardsTests",
            dependencies: [
                "FeatureRewards",
                "CoreDomain",
                .product(name: "ComposableArchitecture", package: "swift-composable-architecture"),
            ],
            path: "EnterpriseMobileApp/Tests/FeatureTests/RewardsTests"
        ),
    ]
)
