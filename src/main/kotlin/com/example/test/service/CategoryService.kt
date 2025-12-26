package com.example.test.service

import com.example.test.domain.CategoryEntity
import com.example.test.mapper.CategoryMapper
import com.example.test.model.request.CategoryRequest
import com.example.test.model.response.CategoryResponse
import com.example.test.repository.CategoryRepository
import lombok.extern.log4j.Log4j2
import org.apache.logging.log4j.LogManager
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@Service
@Log4j2
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val categoryMapper: CategoryMapper,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    fun create(request: CategoryRequest): Mono<CategoryResponse> {
        return validateParentId(request.parentId)
            .then(Mono.just(request))
            .map { categoryMapper.toEntity(it) }
            .flatMap { categoryRepository.save(it) }
            .map { categoryMapper.toResponse(it) }
            .doOnSuccess { LOGGER.info("Category created with id: ${it.id}") }
    }

    fun update(id: UUID, request: CategoryRequest): Mono<CategoryResponse> {
        return validateParentId(request.parentId)
            .then(categoryRepository.findByIdAndDeletedFalse(id))
            .switchIfEmpty(Mono.error(RuntimeException("Category not found")))
            .map { categoryMapper.updateEntity(it, request) }
            .flatMap { categoryRepository.save(it) }
            .map { categoryMapper.toResponse(it) }
            .doOnSuccess { LOGGER.info("Category updated with id: ${it.id}") }
    }

    fun get(id: UUID): Mono<CategoryResponse> {
        return categoryRepository.findByIdAndDeletedFalse(id)
            .switchIfEmpty(Mono.error(RuntimeException("Category not found")))
            .map { categoryMapper.toResponse(it) }
    }

    fun getAll(parentId: UUID?, onlyRoots: Boolean?, search: String?): Flux<CategoryResponse> {
        var criteria = Criteria.where("deleted").`is`(false)

        if (onlyRoots == true) {
            criteria = criteria.and("parentId").isNull
        } else if (parentId != null) {
            criteria = criteria.and("parentId").`is`(parentId)
        }

        if (!search.isNullOrBlank()) {
            val searchCriteria = Criteria.where("nameUz").like("%$search%")
                .or("nameRu").like("%$search%")
                .or("nameEn").like("%$search%")
            criteria = criteria.and(searchCriteria)
        }

        return r2dbcEntityTemplate.select(CategoryEntity::class.java)
            .matching(Query.query(criteria))
            .all()
            .map { categoryMapper.toResponse(it) }
    }

    fun delete(id: UUID): Mono<Void> {
        return categoryRepository.findByIdAndDeletedFalse(id)
            .switchIfEmpty(Mono.error(RuntimeException("Category not found")))
            .flatMap {
                it.deleted = true
                categoryRepository.save(it)
            }
            .doOnSuccess { LOGGER.info("Category deleted with id: $id") }
            .then()
    }

    private fun validateParentId(parentId: UUID?): Mono<Void> {
        return if (parentId != null) {
            categoryRepository.findByIdAndDeletedFalse(parentId)
                .switchIfEmpty(Mono.error(RuntimeException("Parent category not found")))
                .then()
        } else {
            Mono.empty()
        }
    }
}
