package com.example.test.mapper

import com.example.test.domain.DeviceEntity
import com.example.test.domain.UserEntity
import com.example.test.model.AdminLoginRequest
import com.example.test.model.UpdateUserRequest
import com.example.test.model.UserResponse
import com.example.test.model.VerificationRequest
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import java.util.*

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface UserMapper {
  companion object {
    val INSTANCE: UserMapper =
      Mappers.getMapper(UserMapper::class.java)
  }

  fun toResponse(request: UserEntity): UserResponse
  fun toUpdate(@MappingTarget request: UserEntity, request1: UpdateUserRequest): UserEntity
}
