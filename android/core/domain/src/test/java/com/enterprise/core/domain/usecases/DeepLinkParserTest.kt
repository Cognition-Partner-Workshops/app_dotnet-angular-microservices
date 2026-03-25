package com.enterprise.core.domain.usecases

import com.enterprise.core.domain.entities.AppRoute
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeepLinkParserTest {

    private lateinit var parser: DeepLinkParser

    @BeforeEach
    fun setUp() {
        parser = DeepLinkParser()
    }

    @Test
    fun `parse explore route`() {
        val route = parser.parse("app://explore")
        assertEquals(AppRoute.Explore, route)
    }

    @Test
    fun `parse shop route`() {
        val route = parser.parse("app://shop")
        assertEquals(AppRoute.Shop, route)
    }

    @Test
    fun `parse cart view route`() {
        val route = parser.parse("app://shop/cart")
        assertEquals(AppRoute.CartView, route)
    }

    @Test
    fun `parse cart add route with itemId`() {
        val route = parser.parse("app://shop/cart/add?itemId=123")
        assertEquals(AppRoute.CartAdd("123"), route)
    }

    @Test
    fun `parse product detail route`() {
        val route = parser.parse("app://shop/product-abc")
        assertEquals(AppRoute.ProductDetail("product-abc"), route)
    }

    @Test
    fun `parse rewards route`() {
        val route = parser.parse("app://rewards")
        assertEquals(AppRoute.Rewards, route)
    }

    @Test
    fun `parse reward detail route`() {
        val route = parser.parse("app://rewards/reward-456")
        assertEquals(AppRoute.RewardDetail("reward-456"), route)
    }

    @Test
    fun `parse chat session route`() {
        val route = parser.parse("app://chat/session-789")
        assertEquals(AppRoute.ChatSession("session-789"), route)
    }

    @Test
    fun `parse search route with query`() {
        val route = parser.parse("app://search?q=fios")
        assertEquals(AppRoute.Search("fios"), route)
    }

    @Test
    fun `parse unknown route`() {
        val route = parser.parse("app://unknown/path")
        assertTrue(route is AppRoute.Unknown)
    }

    @Test
    fun `parse empty string returns unknown`() {
        val route = parser.parse("")
        assertTrue(route is AppRoute.Unknown)
    }
}
