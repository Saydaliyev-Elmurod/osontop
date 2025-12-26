package com.example.test.mapper

import com.example.test.domain.UserEntity
import com.example.test.model.request.UpdateUserRequest
import com.example.test.model.response.UserResponse
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper {
  companion object {
    val INSTANCE: UserMapper =
      Mappers.getMapper(UserMapper::class.java)
  }

  fun toResponse(request: UserEntity): UserResponse
  fun toUpdate(@MappingTarget request: UserEntity, request1: UpdateUserRequest): UserEntity
}
