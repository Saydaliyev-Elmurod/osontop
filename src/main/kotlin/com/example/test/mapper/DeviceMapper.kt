package com.example.test.mapper

import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
interface DeviceMapper {
  companion object {
    val INSTANCE: DeviceMapper =
      Mappers.getMapper(DeviceMapper::class.java)
  }
}
