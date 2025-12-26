package com.example.test.mapper

import com.example.test.domain.CategoryEntity
import com.example.test.model.request.CategoryRequest
import com.example.test.model.response.CategoryResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface CategoryMapper {

    @Mapping(source = "name.uz", target = "nameUz")
    @Mapping(source = "name.ru", target = "nameRu")
    @Mapping(source = "name.en", target = "nameEn")
    @Mapping(source = "description.uz", target = "descriptionUz")
    @Mapping(source = "description.ru", target = "descriptionRu")
    @Mapping(source = "description.en", target = "descriptionEn")
    fun toEntity(request: CategoryRequest): CategoryEntity

    @Mapping(source = "name.uz", target = "nameUz")
    @Mapping(source = "name.ru", target = "nameRu")
    @Mapping(source = "name.en", target = "nameEn")
    @Mapping(source = "description.uz", target = "descriptionUz")
    @Mapping(source = "description.ru", target = "descriptionRu")
    @Mapping(source = "description.en", target = "descriptionEn")
    fun updateEntity(@MappingTarget entity: CategoryEntity, request: CategoryRequest): CategoryEntity

    @Mapping(source = "nameUz", target = "name.uz")
    @Mapping(source = "nameRu", target = "name.ru")
    @Mapping(source = "nameEn", target = "name.en")
    @Mapping(source = "descriptionUz", target = "description.uz")
    @Mapping(source = "descriptionRu", target = "description.ru")
    @Mapping(source = "descriptionEn", target = "description.en")
    fun toResponse(entity: CategoryEntity): CategoryResponse

}
