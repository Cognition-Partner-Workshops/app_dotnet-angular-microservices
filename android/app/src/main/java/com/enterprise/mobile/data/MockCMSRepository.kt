package com.enterprise.mobile.data

import android.content.Context
import com.enterprise.core.domain.entities.BottomNavBarConfig
import com.enterprise.core.domain.entities.ExploreSectionsPayload
import com.enterprise.core.domain.entities.HeroBannerPayload
import com.enterprise.core.domain.entities.ServiceIconsPayload
import com.enterprise.core.domain.entities.TopNavBarConfig
import com.enterprise.core.domain.repositories.CMSRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mock CMS repository that loads JSON from assets/mock/ folder.
 * Used when MockForDemo flag is enabled.
 */
@Singleton
class MockCMSRepository @Inject constructor(
    @ApplicationContext private val context: Context
) : CMSRepository {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private fun loadAsset(filename: String): String {
        return context.assets.open("mock/$filename").bufferedReader().use { it.readText() }
    }

    override suspend fun getTopNavBar(): TopNavBarConfig {
        return json.decodeFromString(loadAsset("top_nav_bar.json"))
    }

    override suspend fun getBottomNavBar(): BottomNavBarConfig {
        return json.decodeFromString(loadAsset("bottom_nav_bar.json"))
    }

    override suspend fun getHeroBanners(): HeroBannerPayload {
        return json.decodeFromString(loadAsset("hero_banners.json"))
    }

    override suspend fun getServiceIcons(): ServiceIconsPayload {
        return json.decodeFromString(loadAsset("service_icons.json"))
    }

    override suspend fun getExploreSections(): ExploreSectionsPayload {
        return json.decodeFromString(loadAsset("explore_sections.json"))
    }
}
