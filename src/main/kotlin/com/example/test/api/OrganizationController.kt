package com.example.test.api

import com.example.test.context.UserPrincipal
import com.example.test.model.request.OrganizationRequest
import com.example.test.model.response.OrganizationResponse
import com.example.test.service.OrganizationService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/organizations")
class OrganizationController(
    private val organizationService: OrganizationService
) {

    @PostMapping
    fun create(
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody request: OrganizationRequest
    ): Mono<OrganizationResponse> {
        return organizationService.create(principal.user.id, request)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @AuthenticationPrincipal principal: UserPrincipal,
        @RequestBody request: OrganizationRequest
    ): Mono<OrganizationResponse> {
        return organizationService.update(id, principal.user.id, request)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): Mono<OrganizationResponse> {
        return organizationService.get(id)
    }

    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) search: String?
    ): Flux<OrganizationResponse> {
        return organizationService.getAll(page, size, search)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: UUID,
        @AuthenticationPrincipal principal: UserPrincipal
    ): Mono<Void> {
        return organizationService.delete(id, principal.user.id)
    }
}
