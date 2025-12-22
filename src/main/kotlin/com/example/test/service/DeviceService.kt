package com.example.test.service

import com.example.test.mapper.DeviceMapper
import com.example.test.repository.DeviceRepository
import lombok.extern.log4j.Log4j2
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service

@Service
@Log4j2
class DeviceService(private val deviceRepository: DeviceRepository) {
  companion object {
    private val LOGGER = LogManager.getLogger()
    private val deviceMapper = DeviceMapper::INSTANCE
  }
}