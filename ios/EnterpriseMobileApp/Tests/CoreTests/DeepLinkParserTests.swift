import XCTest
@testable import CoreDomain

final class DeepLinkParserTests: XCTestCase {
    private var parser: DeepLinkParser!

    override func setUp() {
        super.setUp()
        parser = DeepLinkParser()
    }

    func testParseExploreRoute() {
        let route = parser.parse("app://explore")
        XCTAssertEqual(route, .explore)
    }

    func testParseShopRoute() {
        let route = parser.parse("app://shop")
        XCTAssertEqual(route, .shop)
    }

    func testParseCartViewRoute() {
        let route = parser.parse("app://shop/cart")
        XCTAssertEqual(route, .cartView)
    }

    func testParseCartAddRoute() {
        let route = parser.parse("app://shop/cart/add?itemId=123")
        XCTAssertEqual(route, .cartAdd(itemId: "123"))
    }

    func testParseProductDetailRoute() {
        let route = parser.parse("app://shop/product-abc")
        XCTAssertEqual(route, .productDetail(productId: "product-abc"))
    }

    func testParseRewardsRoute() {
        let route = parser.parse("app://rewards")
        XCTAssertEqual(route, .rewards)
    }

    func testParseRewardDetailRoute() {
        let route = parser.parse("app://rewards/reward-456")
        XCTAssertEqual(route, .rewardDetail(rewardId: "reward-456"))
    }

    func testParseChatRoute() {
        let route = parser.parse("app://chat/session-789")
        XCTAssertEqual(route, .chatSession(sessionId: "session-789"))
    }

    func testParseSearchRoute() {
        let route = parser.parse("app://search?q=fios")
        XCTAssertEqual(route, .search(query: "fios"))
    }

    func testParseUnknownRoute() {
        let route = parser.parse("app://unknown/path")
        XCTAssertEqual(route, .unknown(path: "app://unknown/path"))
    }

    func testParseInvalidURL() {
        let route = parser.parse("")
        XCTAssertEqual(route, .unknown(path: ""))
    }
}
