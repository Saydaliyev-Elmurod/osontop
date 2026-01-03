package com.example.test.mapper

import com.example.test.domain.ServicesEntity
import com.example.test.model.request.ServicesRequest
import com.example.test.model.response.ServicesResponse
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface ServicesMapper {

    @Mapping(source = "name.uz", target = "nameUz")
    @Mapping(source = "name.ru", target = "nameRu")
    @Mapping(source = "name.en", target = "nameEn")
    fun toEntity(request: ServicesRequest): ServicesEntity

    @Mapping(source = "name.uz", target = "nameUz")
    @Mapping(source = "name.ru", target = "nameRu")
    @Mapping(source = "name.en", target = "nameEn")
    fun updateEntity(@MappingTarget entity: ServicesEntity, request: ServicesRequest): ServicesEntity

    @Mapping(source = "nameUz", target = "name.uz")
    @Mapping(source = "nameRu", target = "name.ru")
    @Mapping(source = "nameEn", target = "name.en")
    fun toResponse(entity: ServicesEntity): ServicesResponse
}
