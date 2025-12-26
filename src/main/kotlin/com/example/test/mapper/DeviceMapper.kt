package com.example.test.mapper

import com.example.test.domain.DeviceEntity
import com.example.test.model.request.AdminLoginRequest
import com.example.test.model.request.VerificationRequest
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import java.util.*

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface DeviceMapper {
  companion object {
    val INSTANCE: DeviceMapper =
      Mappers.getMapper(DeviceMapper::class.java)
  }

  @Mapping(target = "id", ignore = true)
  fun toDeviceEntity(request: VerificationRequest): DeviceEntity

  @Mapping(target = "id", ignore = true)
  fun toDeviceEntity(request: AdminLoginRequest): DeviceEntity

  @Mapping(target = "id", source = "id")
  fun toDeviceEntity(id: UUID?, request: VerificationRequest): DeviceEntity
  @Mapping(target = "id", source = "id")

  fun toDeviceEntity(id: UUID?, request: AdminLoginRequest): DeviceEntity
}
