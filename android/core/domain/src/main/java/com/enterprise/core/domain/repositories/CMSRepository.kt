package com.enterprise.core.domain.repositories

import com.enterprise.core.domain.entities.BottomNavBarConfig
import com.enterprise.core.domain.entities.ExploreSectionsPayload
import com.enterprise.core.domain.entities.HeroBannerPayload
import com.enterprise.core.domain.entities.ServiceIconsPayload
import com.enterprise.core.domain.entities.TopNavBarConfig

/** CMS repository contract for fetching server-driven UI configurations. */
interface CMSRepository {
    suspend fun getTopNavBar(): TopNavBarConfig
    suspend fun getBottomNavBar(): BottomNavBarConfig
    suspend fun getHeroBanners(): HeroBannerPayload
    suspend fun getServiceIcons(): ServiceIconsPayload
    suspend fun getExploreSections(): ExploreSectionsPayload
}
