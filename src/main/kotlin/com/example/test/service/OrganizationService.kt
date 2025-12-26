package com.example.test.service

import com.example.test.exception.ForbiddenException
import com.example.test.exception.NotFoundException
import com.example.test.mapper.OrganizationMapper
import com.example.test.model.request.OrganizationRequest
import com.example.test.model.response.OrganizationResponse
import com.example.test.repository.OrganizationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
class OrganizationService(
    private val organizationRepository: OrganizationRepository,
    private val organizationMapper: OrganizationMapper
) {

    @Transactional
    fun create(
        ownerId: UUID,
        request: OrganizationRequest
    ): Mono<OrganizationResponse> {
        val entity = organizationMapper.toEntity(request, ownerId, request.photoUrl)
        return organizationRepository.save(entity)
            .map { organizationMapper.toResponse(it) }
    }

    @Transactional
    fun update(
        id: UUID,
        userId: UUID,
        request: OrganizationRequest
    ): Mono<OrganizationResponse> {
        return organizationRepository.findByIdAndDeletedFalse(id)
            .switchIfEmpty(Mono.error(NotFoundException("Organization not found")))
            .flatMap { organization ->
                if (organization.ownerId != userId) {
                    return@flatMap Mono.error(ForbiddenException("You are not the owner of this organization"))
                }
                val updatedEntity = organizationMapper.updateEntity(organization, request, request.photoUrl)
                organizationRepository.save(updatedEntity)
            }
            .map { organizationMapper.toResponse(it) }
    }

    fun get(id: UUID): Mono<OrganizationResponse> {
        return organizationRepository.findByIdAndDeletedFalse(id)
            .switchIfEmpty(Mono.error(NotFoundException("Organization not found")))
            .map { organizationMapper.toResponse(it) }
    }

    fun getAll(page: Int, size: Int, search: String?): Flux<OrganizationResponse> {
        val pageable = PageRequest.of(page, size)
        val organizations = if (search != null) {
            organizationRepository.findAllByNameContainingIgnoreCaseAndDeletedFalse(search, pageable)
        } else {
            organizationRepository.findAllByDeletedFalse(pageable)
        }
        return organizations.map { organizationMapper.toResponse(it) }
    }

    @Transactional
    fun delete(id: UUID, userId: UUID): Mono<Void> {
        return organizationRepository.findByIdAndDeletedFalse(id)
            .switchIfEmpty(Mono.error(NotFoundException("Organization not found")))
            .flatMap { organization ->
                if (organization.ownerId != userId) {
                    return@flatMap Mono.error(ForbiddenException("You are not the owner of this organization"))
                }
                organization.deleted = true
                organization.isActive = false
                organizationRepository.save(organization)
            }
            .then()
    }
}
