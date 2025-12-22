package com.example.test.service

import com.example.test.domain.DeviceEntity
import com.example.test.mapper.DeviceMapper
import com.example.test.model.VerificationRequest
import com.example.test.repository.DeviceRepository
import lombok.extern.log4j.Log4j2
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
@Log4j2
class DeviceService(private val deviceRepository: DeviceRepository) {
  companion object {
    private val LOGGER = LogManager.getLogger()
    private val deviceMapper = DeviceMapper::INSTANCE
  }

  fun toggleDevice(request: VerificationRequest): Mono<DeviceEntity?> {

  }
}