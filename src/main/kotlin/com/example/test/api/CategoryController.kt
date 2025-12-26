package com.example.test.api

import com.example.test.model.request.CategoryRequest
import com.example.test.model.response.CategoryResponse
import com.example.test.service.CategoryService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID

@RestController
@RequestMapping("/api/category/v1")
class CategoryController(private val categoryService: CategoryService) {

  @PostMapping
  fun create(
    @AuthenticationPrincipal
    @RequestBody request: CategoryRequest
  ): Mono<CategoryResponse> {
    return categoryService.create(request)
  }

  @PutMapping("/{id}")
  fun update(
    @PathVariable id: UUID,
    @RequestBody request: CategoryRequest
  ): Mono<CategoryResponse> {
    return categoryService.update(id, request)
  }

  @GetMapping("/{id}")
  fun get(@PathVariable id: UUID): Mono<CategoryResponse> {
    return categoryService.get(id)
  }

  @GetMapping
  fun getAll(
    @RequestParam(required = false) parentId: UUID?,
    @RequestParam(required = false) onlyRoots: Boolean?,
    @RequestParam(required = false) search: String?
  ): Flux<CategoryResponse> {
    return categoryService.getAll(parentId, onlyRoots, search)
  }

  @DeleteMapping("/{id}")
  fun delete(@PathVariable id: UUID): Mono<Void> {
    return categoryService.delete(id)
  }
}
