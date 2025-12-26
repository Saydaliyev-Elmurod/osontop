package com.example.test.service

import com.example.test.domain.CategoryEntity
import com.example.test.mapper.CategoryMapper
import com.example.test.model.common.TextModel
import com.example.test.model.request.CategoryRequest
import com.example.test.model.response.CategoryResponse
import com.example.test.repository.CategoryRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant
import java.util.UUID

@ExtendWith(MockKExtension::class)
class CategoryServiceTest {

    @MockK
    private lateinit var categoryRepository: CategoryRepository

    @MockK
    private lateinit var categoryMapper: CategoryMapper

    @MockK
    private lateinit var r2dbcEntityTemplate: R2dbcEntityTemplate

    @InjectMockKs
    private lateinit var categoryService: CategoryService

    @Test
    fun `should create category successfully`() {
        val request = CategoryRequest(
            id = "",
            name = TextModel("Uz", "Ru", "En"),
            description = TextModel("Desc Uz", "Desc Ru", "Desc En"),
            image = "image.png",
            icon = "icon.png",
            parentId = null,
            orderIndex = 1
        )
        val entity = CategoryEntity(
            nameUz = "Uz", nameRu = "Ru", nameEn = "En",
            descriptionUz = "Desc Uz", descriptionRu = "Desc Ru", descriptionEn = "Desc En",
            image = "image.png", icon = "icon.png", parentId = null, orderIndex = 1
        )
        val savedEntity = entity.apply { id = UUID.randomUUID() }
        val response = CategoryResponse(
            id = savedEntity.id!!,
            createdDate = Instant.now(),
            lastModifiedDate = Instant.now(),
            name = request.name,
            description = request.description,
            image = request.image,
            icon = request.icon,
            parentId = request.parentId,
            orderIndex = request.orderIndex
        )

        every { categoryMapper.toEntity(request) } returns entity
        every { categoryRepository.save(entity) } returns Mono.just(savedEntity)
        every { categoryMapper.toResponse(savedEntity) } returns response

        StepVerifier.create(categoryService.create(request))
            .expectNext(response)
            .verifyComplete()

        verify(exactly = 1) { categoryRepository.save(entity) }
    }

    @Test
    fun `should create sub-category successfully`() {
        val parentId = UUID.randomUUID()
        val request = CategoryRequest(
            id = "",
            name = TextModel("Sub"),
            description = null,
            image = null,
            icon = null,
            parentId = parentId,
            orderIndex = 1
        )
        val parentEntity = CategoryEntity().apply { id = parentId }
        val entity = CategoryEntity(parentId = parentId)
        val savedEntity = entity.apply { id = UUID.randomUUID() }
        val response = CategoryResponse(
            id = savedEntity.id!!,
            createdDate = Instant.now(),
            lastModifiedDate = Instant.now(),
            name = request.name,
            description = null,
            image = null,
            icon = null,
            parentId = parentId,
            orderIndex = 1
        )

        every { categoryRepository.findByIdAndDeletedFalse(parentId) } returns Mono.just(parentEntity)
        every { categoryMapper.toEntity(request) } returns entity
        every { categoryRepository.save(entity) } returns Mono.just(savedEntity)
        every { categoryMapper.toResponse(savedEntity) } returns response

        StepVerifier.create(categoryService.create(request))
            .expectNext(response)
            .verifyComplete()
    }

    @Test
    fun `should fail to create category if parent not found`() {
        val parentId = UUID.randomUUID()
        val request = CategoryRequest(
            id = "",
            name = TextModel("Sub"),
            description = null,
            image = null,
            icon = null,
            parentId = parentId,
            orderIndex = 1
        )

        every { categoryRepository.findByIdAndDeletedFalse(parentId) } returns Mono.empty()

        StepVerifier.create(categoryService.create(request))
            .expectErrorMatches { it is RuntimeException && it.message == "Parent category not found" }
            .verify()

        verify(exactly = 0) { categoryRepository.save(any()) }
    }

    @Test
    fun `should update category successfully`() {
        val id = UUID.randomUUID()
        val request = CategoryRequest(
            id = id.toString(),
            name = TextModel("Updated"),
            description = null,
            image = null,
            icon = null,
            parentId = null,
            orderIndex = 2
        )
        val existingEntity = CategoryEntity().apply { this.id = id }
        val updatedEntity = CategoryEntity(nameUz = "Updated").apply { this.id = id }
        val response = CategoryResponse(
            id = id,
            createdDate = Instant.now(),
            lastModifiedDate = Instant.now(),
            name = request.name,
            description = null,
            image = null,
            icon = null,
            parentId = null,
            orderIndex = 2
        )

        every { categoryRepository.findByIdAndDeletedFalse(id) } returns Mono.just(existingEntity)
        every { categoryMapper.updateEntity(existingEntity, request) } returns updatedEntity
        every { categoryRepository.save(updatedEntity) } returns Mono.just(updatedEntity)
        every { categoryMapper.toResponse(updatedEntity) } returns response

        StepVerifier.create(categoryService.update(id, request))
            .expectNext(response)
            .verifyComplete()
    }

    @Test
    fun `should fail to update if category not found`() {
        val id = UUID.randomUUID()
        val request = CategoryRequest(
            id = id.toString(),
            name = TextModel("Updated"),
            description = null,
            image = null,
            icon = null,
            parentId = null,
            orderIndex = 2
        )

        every { categoryRepository.findByIdAndDeletedFalse(id) } returns Mono.empty()

        StepVerifier.create(categoryService.update(id, request))
            .expectErrorMatches { it is RuntimeException && it.message == "Category not found" }
            .verify()
    }

    @Test
    fun `should fail to update if parent category not found`() {
        val id = UUID.randomUUID()
        val parentId = UUID.randomUUID()
        val request = CategoryRequest(
            id = id.toString(),
            name = TextModel("Updated"),
            description = null,
            image = null,
            icon = null,
            parentId = parentId,
            orderIndex = 2
        )

        every { categoryRepository.findByIdAndDeletedFalse(parentId) } returns Mono.empty()

        StepVerifier.create(categoryService.update(id, request))
            .expectErrorMatches { it is RuntimeException && it.message == "Parent category not found" }
            .verify()
    }

    @Test
    fun `should get category successfully`() {
        val id = UUID.randomUUID()
        val entity = CategoryEntity().apply { this.id = id }
        val response = CategoryResponse(
            id = id,
            createdDate = Instant.now(),
            lastModifiedDate = Instant.now(),
            name = null,
            description = null,
            image = null,
            icon = null,
            parentId = null,
            orderIndex = 0
        )

        every { categoryRepository.findByIdAndDeletedFalse(id) } returns Mono.just(entity)
        every { categoryMapper.toResponse(entity) } returns response

        StepVerifier.create(categoryService.get(id))
            .expectNext(response)
            .verifyComplete()
    }

    @Test
    fun `should fail to get non-existent category`() {
        val id = UUID.randomUUID()
        every { categoryRepository.findByIdAndDeletedFalse(id) } returns Mono.empty()

        StepVerifier.create(categoryService.get(id))
            .expectErrorMatches { it is RuntimeException && it.message == "Category not found" }
            .verify()
    }

    @Test
    fun `should get all categories with filters`() {
        val parentId = UUID.randomUUID()
        val search = "Test"
        val entity = CategoryEntity().apply { id = UUID.randomUUID() }
        val response = CategoryResponse(
            id = entity.id!!,
            createdDate = Instant.now(),
            lastModifiedDate = Instant.now(),
            name = null,
            description = null,
            image = null,
            icon = null,
            parentId = null,
            orderIndex = 0
        )

        every {
            r2dbcEntityTemplate.select(CategoryEntity::class.java)
                .matching(any<Query>())
                .all()
        } returns Flux.just(entity)
        every { categoryMapper.toResponse(entity) } returns response

        StepVerifier.create(categoryService.getAll(parentId, false, search))
            .expectNext(response)
            .verifyComplete()
    }

    @Test
    fun `should get root categories`() {
        val entity = CategoryEntity().apply { id = UUID.randomUUID() }
        val response = CategoryResponse(
            id = entity.id!!,
            createdDate = Instant.now(),
            lastModifiedDate = Instant.now(),
            name = null,
            description = null,
            image = null,
            icon = null,
            parentId = null,
            orderIndex = 0
        )

        every {
            r2dbcEntityTemplate.select(CategoryEntity::class.java)
                .matching(any<Query>())
                .all()
        } returns Flux.just(entity)
        every { categoryMapper.toResponse(entity) } returns response

        StepVerifier.create(categoryService.getAll(null, true, null))
            .expectNext(response)
            .verifyComplete()
    }

    @Test
    fun `should delete category successfully`() {
        val id = UUID.randomUUID()
        val entity = CategoryEntity().apply { this.id = id }

        every { categoryRepository.findByIdAndDeletedFalse(id) } returns Mono.just(entity)
        every { categoryRepository.save(any()) } returns Mono.just(entity)

        StepVerifier.create(categoryService.delete(id))
            .verifyComplete()

        verify {
            categoryRepository.save(withArg {
                assert(it.deleted)
            })
        }
    }

    @Test
    fun `should fail to delete non-existent category`() {
        val id = UUID.randomUUID()
        every { categoryRepository.findByIdAndDeletedFalse(id) } returns Mono.empty()

        StepVerifier.create(categoryService.delete(id))
            .expectErrorMatches { it is RuntimeException && it.message == "Category not found" }
            .verify()
    }
}
