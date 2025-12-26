package com.example.test.service

import com.example.test.domain.OrganizationEntity
import com.example.test.exception.ForbiddenException
import com.example.test.exception.NotFoundException
import com.example.test.mapper.OrganizationMapper
import com.example.test.model.request.OrganizationRequest
import com.example.test.model.response.OrganizationResponse
import com.example.test.repository.OrganizationRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageRequest
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.UUID

@ExtendWith(MockKExtension::class)
class OrganizationServiceTest {

    @MockK
    private lateinit var organizationRepository: OrganizationRepository

    @MockK
    private lateinit var organizationMapper: OrganizationMapper

    @InjectMockKs
    private lateinit var organizationService: OrganizationService

    @Test
    fun `should create organization successfully`() {
        val ownerId = UUID.randomUUID()
        val request = OrganizationRequest(
            name = "Test Org",
            address = "123 Street",
            latitude = 10.0,
            longitude = 20.0,
            photoUrl = "http://photo.com"
        )
        val entity = OrganizationEntity(
            name = "Test Org",
            address = "123 Street",
            latitude = 10.0,
            longitude = 20.0,
            photoUrl = "http://photo.com",
            ownerId = ownerId,
            isActive = true
        )
        val savedEntity = entity.copy().apply { id = UUID.randomUUID() }
        val response = OrganizationResponse(
            id = savedEntity.id!!,
            name = savedEntity.name,
            address = savedEntity.address,
            latitude = savedEntity.latitude,
            longitude = savedEntity.longitude,
            photoUrl = savedEntity.photoUrl,
            ownerId = savedEntity.ownerId!!,
            isActive = savedEntity.isActive
        )

        every { organizationMapper.toEntity(request, ownerId, request.photoUrl) } returns entity
        every { organizationRepository.save(entity) } returns Mono.just(savedEntity)
        every { organizationMapper.toResponse(savedEntity) } returns response

        StepVerifier.create(organizationService.create(ownerId, request))
            .expectNext(response)
            .verifyComplete()

        verify(exactly = 1) { organizationRepository.save(entity) }
    }

    @Test
    fun `should update organization successfully`() {
        val id = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val request = OrganizationRequest(
            name = "Updated Org",
            address = "456 Avenue",
            latitude = 15.0,
            longitude = 25.0,
            photoUrl = "http://newphoto.com"
        )
        val existingEntity = OrganizationEntity(
            name = "Old Org",
            address = "123 Street",
            latitude = 10.0,
            longitude = 20.0,
            photoUrl = "http://photo.com",
            ownerId = ownerId,
            isActive = true
        ).apply { this.id = id }

        val updatedEntity = existingEntity.copy(
            name = "Updated Org",
            address = "456 Avenue",
            latitude = 15.0,
            longitude = 25.0,
            photoUrl = "http://newphoto.com"
        )

        val response = OrganizationResponse(
            id = id,
            name = "Updated Org",
            address = "456 Avenue",
            latitude = 15.0,
            longitude = 25.0,
            photoUrl = "http://newphoto.com",
            ownerId = ownerId,
            isActive = true
        )

        every { organizationRepository.findByIdAndDeletedFalse(id) } returns Mono.just(existingEntity)
        every { organizationMapper.updateEntity(existingEntity, request, request.photoUrl) } returns updatedEntity
        every { organizationRepository.save(updatedEntity) } returns Mono.just(updatedEntity)
        every { organizationMapper.toResponse(updatedEntity) } returns response

        StepVerifier.create(organizationService.update(id, ownerId, request))
            .expectNext(response)
            .verifyComplete()

        verify(exactly = 1) { organizationRepository.save(updatedEntity) }
    }

    @Test
    fun `should throw NotFoundException when updating non-existent organization`() {
        val id = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val request = OrganizationRequest("Name", "Addr", 0.0, 0.0, null)

        every { organizationRepository.findByIdAndDeletedFalse(id) } returns Mono.empty()

        StepVerifier.create(organizationService.update(id, ownerId, request))
            .expectError(NotFoundException::class.java)
            .verify()

        verify(exactly = 0) { organizationRepository.save(any()) }
    }

    @Test
    fun `should throw ForbiddenException when updating organization owned by another user`() {
        val id = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val otherUserId = UUID.randomUUID()
        val request = OrganizationRequest("Name", "Addr", 0.0, 0.0, null)
        val existingEntity = OrganizationEntity(ownerId = ownerId).apply { this.id = id }

        every { organizationRepository.findByIdAndDeletedFalse(id) } returns Mono.just(existingEntity)

        StepVerifier.create(organizationService.update(id, otherUserId, request))
            .expectError(ForbiddenException::class.java)
            .verify()

        verify(exactly = 0) { organizationRepository.save(any()) }
    }

    @Test
    fun `should get organization successfully`() {
        val id = UUID.randomUUID()
        val entity = OrganizationEntity(ownerId = UUID.randomUUID()).apply { this.id = id }
        val response = OrganizationResponse(id, "Name", "Addr", 0.0, 0.0, null, entity.ownerId!!, true)

        every { organizationRepository.findByIdAndDeletedFalse(id) } returns Mono.just(entity)
        every { organizationMapper.toResponse(entity) } returns response

        StepVerifier.create(organizationService.get(id))
            .expectNext(response)
            .verifyComplete()
    }

    @Test
    fun `should throw NotFoundException when getting non-existent organization`() {
        val id = UUID.randomUUID()
        every { organizationRepository.findByIdAndDeletedFalse(id) } returns Mono.empty()

        StepVerifier.create(organizationService.get(id))
            .expectError(NotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should get all organizations with search`() {
        val search = "Test"
        val page = 0
        val size = 10
        val entity = OrganizationEntity(name = "Test Org", ownerId = UUID.randomUUID()).apply { id = UUID.randomUUID() }
        val response = OrganizationResponse(entity.id!!, "Test Org", "", 0.0, 0.0, null, entity.ownerId!!, true)

        every { organizationRepository.findAllByNameContainingIgnoreCaseAndDeletedFalse(search, PageRequest.of(page, size)) } returns Flux.just(entity)
        every { organizationMapper.toResponse(entity) } returns response

        StepVerifier.create(organizationService.getAll(page, size, search))
            .expectNext(response)
            .verifyComplete()
    }

    @Test
    fun `should get all organizations without search`() {
        val page = 0
        val size = 10
        val entity = OrganizationEntity(name = "Org", ownerId = UUID.randomUUID()).apply { id = UUID.randomUUID() }
        val response = OrganizationResponse(entity.id!!, "Org", "", 0.0, 0.0, null, entity.ownerId!!, true)

        every { organizationRepository.findAllByDeletedFalse(PageRequest.of(page, size)) } returns Flux.just(entity)
        every { organizationMapper.toResponse(entity) } returns response

        StepVerifier.create(organizationService.getAll(page, size, null))
            .expectNext(response)
            .verifyComplete()
    }

    @Test
    fun `should delete organization successfully`() {
        val id = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val entity = OrganizationEntity(ownerId = ownerId, isActive = true).apply { this.id = id }

        every { organizationRepository.findByIdAndDeletedFalse(id) } returns Mono.just(entity)
        every { organizationRepository.save(any()) } returns Mono.just(entity)

        StepVerifier.create(organizationService.delete(id, ownerId))
            .verifyComplete()

        verify {
            organizationRepository.save(withArg {
                assert(it.deleted)
                assert(!it.isActive)
            })
        }
    }

    @Test
    fun `should throw NotFoundException when deleting non-existent organization`() {
        val id = UUID.randomUUID()
        val ownerId = UUID.randomUUID()

        every { organizationRepository.findByIdAndDeletedFalse(id) } returns Mono.empty()

        StepVerifier.create(organizationService.delete(id, ownerId))
            .expectError(NotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should throw ForbiddenException when deleting organization owned by another user`() {
        val id = UUID.randomUUID()
        val ownerId = UUID.randomUUID()
        val otherUserId = UUID.randomUUID()
        val entity = OrganizationEntity(ownerId = ownerId).apply { this.id = id }

        every { organizationRepository.findByIdAndDeletedFalse(id) } returns Mono.just(entity)

        StepVerifier.create(organizationService.delete(id, otherUserId))
            .expectError(ForbiddenException::class.java)
            .verify()
    }
}
