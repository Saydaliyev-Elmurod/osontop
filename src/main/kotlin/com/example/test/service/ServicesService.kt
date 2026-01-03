package com.example.test.service

import com.example.test.domain.ServicesEntity
import com.example.test.mapper.ServicesMapper
import com.example.test.model.request.ServicesRequest
import com.example.test.model.response.ServicesResponse
import com.example.test.repository.ServicesRepository
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
class ServicesService(
    private val servicesRepository: ServicesRepository,
    private val servicesMapper: ServicesMapper,
    private val r2dbcEntityTemplate: R2dbcEntityTemplate
) {
    companion object {
        private val LOGGER = LogManager.getLogger()
    }

    fun create(request: ServicesRequest): Mono<ServicesResponse> {
        return validateParentId(request.parentId)
            .then(Mono.just(request))
            .map { servicesMapper.toEntity(it) }
            .flatMap { servicesRepository.save(it) }
            .map { servicesMapper.toResponse(it) }
            .doOnSuccess { LOGGER.info("Service created with id: ${it.id}") }
    }

    fun update(id: UUID, request: ServicesRequest): Mono<ServicesResponse> {
        return validateParentId(request.parentId)
            .then(servicesRepository.findByIdAndDeletedFalse(id))
            .switchIfEmpty(Mono.error(RuntimeException("Service not found")))
            .map { servicesMapper.updateEntity(it, request) }
            .flatMap { servicesRepository.save(it) }
            .map { servicesMapper.toResponse(it) }
            .doOnSuccess { LOGGER.info("Service updated with id: ${it.id}") }
    }

    fun get(id: UUID): Mono<ServicesResponse> {
        return servicesRepository.findByIdAndDeletedFalse(id)
            .switchIfEmpty(Mono.error(RuntimeException("Service not found")))
            .map { servicesMapper.toResponse(it) }
    }

    fun getAll(parentId: UUID?, onlyRoots: Boolean?, search: String?): Flux<ServicesResponse> {
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

        return r2dbcEntityTemplate.select(ServicesEntity::class.java)
            .matching(Query.query(criteria))
            .all()
            .map { servicesMapper.toResponse(it) }
    }

    fun delete(id: UUID): Mono<Void> {
        return servicesRepository.findByIdAndDeletedFalse(id)
            .switchIfEmpty(Mono.error(RuntimeException("Service not found")))
            .flatMap {
                it.deleted = true
                servicesRepository.save(it)
            }
            .doOnSuccess { LOGGER.info("Service deleted with id: $id") }
            .then()
    }

    private fun validateParentId(parentId: UUID?): Mono<Void> {
        return if (parentId != null) {
            servicesRepository.findByIdAndDeletedFalse(parentId)
                .switchIfEmpty(Mono.error(RuntimeException("Parent service not found")))
                .then()
        } else {
            Mono.empty()
        }
    }
}
