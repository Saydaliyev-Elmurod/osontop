package com.example.test.service

import com.example.test.AbstractIntegrationTest
import com.example.test.domain.OrganizationEntity
import com.example.test.model.request.OrganizationRequest
import com.example.test.repository.OrganizationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.util.UUID

class OrganizationServiceIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var organizationService: OrganizationService

    @Autowired
    private lateinit var organizationRepository: OrganizationRepository

    @BeforeEach
    fun setUp() {
        organizationRepository.deleteAll().block()
    }

    @Test
    fun `should create organization and persist to database`() {
        val ownerId = UUID.randomUUID()
        val request = OrganizationRequest(
            name = "Integration Test Org",
            address = "123 Test St",
            latitude = 10.0,
            longitude = 20.0,
            photoUrl = "http://test.com/photo.jpg"
        )

        StepVerifier.create(organizationService.create(ownerId, request))
            .expectNextMatches { response ->
                response.name == request.name &&
                response.address == request.address &&
                response.ownerId == ownerId &&
                response.isActive
            }
            .verifyComplete()

        StepVerifier.create(organizationRepository.findAll())
            .expectNextMatches { entity ->
                entity.name == request.name &&
                entity.ownerId == ownerId
            }
            .verifyComplete()
    }

    @Test
    fun `should update organization in database`() {
        val ownerId = UUID.randomUUID()
        val initialEntity = OrganizationEntity(
            name = "Old Name",
            address = "Old Address",
            latitude = 0.0,
            longitude = 0.0,
            photoUrl = null,
            ownerId = ownerId,
            isActive = true
        )
        val savedEntity = organizationRepository.save(initialEntity).block()!!

        val updateRequest = OrganizationRequest(
            name = "New Name",
            address = "New Address",
            latitude = 1.0,
            longitude = 1.0,
            photoUrl = "http://new.com"
        )

        StepVerifier.create(organizationService.update(savedEntity.id!!, ownerId, updateRequest))
            .expectNextMatches { response ->
                response.name == "New Name" &&
                response.address == "New Address"
            }
            .verifyComplete()

        StepVerifier.create(organizationRepository.findById(savedEntity.id!!))
            .expectNextMatches { entity ->
                entity.name == "New Name" &&
                entity.address == "New Address"
            }
            .verifyComplete()
    }

    @Test
    fun `should soft delete organization`() {
        val ownerId = UUID.randomUUID()
        val entity = OrganizationEntity(
            name = "To Delete",
            ownerId = ownerId,
            isActive = true
        )
        val savedEntity = organizationRepository.save(entity).block()!!

        StepVerifier.create(organizationService.delete(savedEntity.id!!, ownerId))
            .verifyComplete()

        StepVerifier.create(organizationRepository.findById(savedEntity.id!!))
            .expectNextMatches { it.deleted && !it.isActive }
            .verifyComplete()
    }
}
