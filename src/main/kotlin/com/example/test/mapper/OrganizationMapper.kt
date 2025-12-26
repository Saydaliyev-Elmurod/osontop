package com.example.test.mapper

import com.example.test.domain.OrganizationEntity
import com.example.test.model.request.OrganizationRequest
import com.example.test.model.response.OrganizationResponse
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class OrganizationMapper {
    fun toEntity(request: OrganizationRequest, ownerId: UUID, photoUrl: String?): OrganizationEntity {
        return OrganizationEntity(
            name = request.name,
            address = request.address,
            latitude = request.latitude,
            longitude = request.longitude,
            photoUrl = photoUrl,
            ownerId = ownerId,
            isActive = true
        )
    }

    fun toResponse(entity: OrganizationEntity): OrganizationResponse {
        return OrganizationResponse(
            id = entity.id!!,
            name = entity.name,
            address = entity.address,
            latitude = entity.latitude,
            longitude = entity.longitude,
            photoUrl = entity.photoUrl,
            ownerId = entity.ownerId!!,
            isActive = entity.isActive
        )
    }

    fun updateEntity(entity: OrganizationEntity, request: OrganizationRequest, photoUrl: String?): OrganizationEntity {
        entity.name = request.name
        entity.address = request.address
        entity.latitude = request.latitude
        entity.longitude = request.longitude
        if (photoUrl != null) {
            entity.photoUrl = photoUrl
        }
        return entity
    }
}
