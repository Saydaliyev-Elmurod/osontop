package com.example.test.service

import com.example.test.AbstractIntegrationTest
import com.example.test.domain.CategoryEntity
import com.example.test.model.common.TextModel
import com.example.test.model.request.CategoryRequest
import com.example.test.repository.CategoryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.test.StepVerifier
import java.util.UUID

class CategoryServiceIntegrationTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var categoryService: CategoryService

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp() {
        categoryRepository.deleteAll().block()
    }

    @Test
    fun `should create category and persist to database`() {
        val request = CategoryRequest(
            id = "",
            name = TextModel("Uz", "Ru", "En"),
            description = TextModel("Desc Uz", "Desc Ru", "Desc En"),
            image = "image.png",
            icon = "icon.png",
            parentId = null,
            orderIndex = 1
        )

        StepVerifier.create(categoryService.create(request))
            .expectNextMatches { response ->
                response.name?.uz == "Uz" &&
                response.parentId == null
            }
            .verifyComplete()

        StepVerifier.create(categoryRepository.findAll())
            .expectNextMatches { entity ->
                entity.nameUz == "Uz" &&
                entity.orderIndex == 1
            }
            .verifyComplete()
    }

    @Test
    fun `should create sub-category with valid parent`() {
        val parent = CategoryEntity(nameUz = "Parent")
        val savedParent = categoryRepository.save(parent).block()!!

        val request = CategoryRequest(
            id = "",
            name = TextModel("Child"),
            description = null,
            image = null,
            icon = null,
            parentId = savedParent.id,
            orderIndex = 1
        )

        StepVerifier.create(categoryService.create(request))
            .expectNextMatches { it.parentId == savedParent.id }
            .verifyComplete()
    }

    @Test
    fun `should fail to create sub-category with invalid parent`() {
        val request = CategoryRequest(
            id = "",
            name = TextModel("Child"),
            description = null,
            image = null,
            icon = null,
            parentId = UUID.randomUUID(),
            orderIndex = 1
        )

        StepVerifier.create(categoryService.create(request))
            .expectError()
            .verify()
    }

    @Test
    fun `should update category in database`() {
        val entity = CategoryEntity(nameUz = "Old Name")
        val savedEntity = categoryRepository.save(entity).block()!!

        val request = CategoryRequest(
            id = savedEntity.id.toString(),
            name = TextModel("New Name"),
            description = null,
            image = null,
            icon = null,
            parentId = null,
            orderIndex = 1
        )

        StepVerifier.create(categoryService.update(savedEntity.id!!, request))
            .expectNextMatches { it.name?.uz == "New Name" }
            .verifyComplete()

        StepVerifier.create(categoryRepository.findById(savedEntity.id!!))
            .expectNextMatches { it.nameUz == "New Name" }
            .verifyComplete()
    }

    @Test
    fun `should soft delete category`() {
        val entity = CategoryEntity(nameUz = "To Delete")
        val savedEntity = categoryRepository.save(entity).block()!!

        StepVerifier.create(categoryService.delete(savedEntity.id!!))
            .verifyComplete()

        StepVerifier.create(categoryRepository.findById(savedEntity.id!!))
            .expectNextMatches { it.deleted }
            .verifyComplete()
    }
}
