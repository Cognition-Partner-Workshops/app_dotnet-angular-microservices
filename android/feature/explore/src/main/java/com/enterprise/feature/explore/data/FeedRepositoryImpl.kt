package com.enterprise.feature.explore.data

import com.enterprise.core.domain.entities.DomainError
import com.enterprise.core.domain.entities.DomainResult
import com.enterprise.core.domain.entities.UIComponent
import com.enterprise.feature.explore.data.api.FeedApiService
import com.enterprise.feature.explore.data.dto.UIComponentDTO
import com.enterprise.feature.explore.data.local.FeedDao
import com.enterprise.feature.explore.data.local.FeedItemEntity
import com.enterprise.feature.explore.domain.repositories.ExploreFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Feed repository implementing SSOT (Single Source of Truth) pattern:
 * 1. Emit cached → 2. Check expiry → 3. Fetch network → 4. Save to Room → 5. Auto-emit
 */
@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val feedApiService: FeedApiService,
    private val feedDao: FeedDao
) : ExploreFeedRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getDynamicFeed(): Flow<DomainResult<List<UIComponent>>> = flow {
        // Step 1: Emit cached data
        val cachedItems = feedDao.getAll()
        if (cachedItems.isNotEmpty()) {
            emit(DomainResult.Success(cachedItems.mapNotNull { it.toDomain() }))
        }

        // Step 2: Check cache expiry (5-minute TTL)
        val lastUpdated = feedDao.getLastUpdatedTimestamp()
        val fiveMinutesMs = 5 * 60 * 1000L
        val isCacheExpired = lastUpdated == null ||
            (System.currentTimeMillis() - lastUpdated > fiveMinutesMs)

        if (isCacheExpired) {
            // Step 3: Fetch from network
            try {
                val response = feedApiService.getDynamicFeed()
                val entities = response.components.mapIndexed { index, dto ->
                    FeedItemEntity(
                        id = dto.id,
                        type = dto.type,
                        configJson = json.encodeToString(dto.config),
                        sortOrder = index,
                        lastUpdated = System.currentTimeMillis()
                    )
                }

                // Step 4: Save to Room (triggers auto-emit via reactive DAO)
                feedDao.deleteAll()
                feedDao.insertAll(entities)

                // Step 5: Emit fresh data
                val domainComponents = response.components.map { it.toDomain() }
                emit(DomainResult.Success(domainComponents))
            } catch (e: Exception) {
                // On network failure, keep cached data visible
                if (cachedItems.isEmpty()) {
                    emit(DomainResult.Failure(DomainError.NetworkUnavailable))
                }
            }
        }
    }

    override suspend fun refreshFeed(): DomainResult<List<UIComponent>> {
        return try {
            val response = feedApiService.getDynamicFeed()
            val entities = response.components.mapIndexed { index, dto ->
                FeedItemEntity(
                    id = dto.id,
                    type = dto.type,
                    configJson = json.encodeToString(dto.config),
                    sortOrder = index,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            feedDao.deleteAll()
            feedDao.insertAll(entities)
            DomainResult.Success(response.components.map { it.toDomain() })
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.NetworkUnavailable)
        }
    }

    override suspend fun getCachedFeed(): List<UIComponent> {
        return feedDao.getAll().mapNotNull { it.toDomain() }
    }

    override fun getPersonalizedFeed(userId: String): Flow<DomainResult<List<UIComponent>>> = flow {
        try {
            val response = feedApiService.getPersonalizedFeed(userId)
            emit(DomainResult.Success(response.components.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(DomainResult.Failure(DomainError.NetworkUnavailable))
        }
    }

    override suspend fun searchFeed(query: String): DomainResult<List<UIComponent>> {
        return try {
            val response = feedApiService.searchFeed(query)
            DomainResult.Success(response.components.map { it.toDomain() })
        } catch (e: Exception) {
            DomainResult.Failure(DomainError.NetworkUnavailable)
        }
    }

    /** Converts a Room entity back to a domain UIComponent. */
    private fun FeedItemEntity.toDomain(): UIComponent? {
        return try {
            val config = json.decodeFromString<com.enterprise.feature.explore.data.dto.ComponentConfig>(configJson)
            UIComponentDTO(type = type, id = id, config = config).toDomain()
        } catch (_: Exception) {
            null
        }
    }
}
