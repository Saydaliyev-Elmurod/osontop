package com.example.test.mapper

import com.example.test.domain.DeviceEntity
import com.example.test.model.VerificationRequest
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers
import java.util.*

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface DeviceMapper {
  companion object {
    val INSTANCE: DeviceMapper =
      Mappers.getMapper(DeviceMapper::class.java)
  }

  fun toDeviceEntity(request: VerificationRequest): DeviceEntity
  fun toDeviceEntity(id: UUID?, request: VerificationRequest): DeviceEntity
}
