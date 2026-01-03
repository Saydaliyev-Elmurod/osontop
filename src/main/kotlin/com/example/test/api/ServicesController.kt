package com.example.test.api

import com.example.test.model.request.ServicesRequest
import com.example.test.model.response.ServicesResponse
import com.example.test.service.ServicesService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/services/v1")
class ServicesController(private val servicesService: ServicesService) {

  @PostMapping
  fun create(
    @AuthenticationPrincipal
    @RequestBody request: ServicesRequest
  ): Mono<ServicesResponse> {
    return servicesService.create(request)
  }

  @PutMapping("/{id}")
  fun update(
    @PathVariable id: UUID,
    @RequestBody request: ServicesRequest
  ): Mono<ServicesResponse> {
    return servicesService.update(id, request)
  }

  @GetMapping("/{id}")
  fun get(@PathVariable id: UUID): Mono<ServicesResponse> {
    return servicesService.get(id)
  }

  @GetMapping
  fun getAll(
    @RequestParam(required = false) parentId: UUID?,
    @RequestParam(required = false) onlyRoots: Boolean?,
    @RequestParam(required = false) search: String?
  ): Flux<ServicesResponse> {
    return servicesService.getAll(parentId, onlyRoots, search)
  }

  @DeleteMapping("/{id}")
  fun delete(@PathVariable id: UUID): Mono<Void> {
    return servicesService.delete(id)
  }
}
