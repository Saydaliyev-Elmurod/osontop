package com.example.test.context

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.*

@Configuration
class AppConfig {
  @Bean
  fun messageSource(): MessageSource {
    val messageSource = ResourceBundleMessageSource()
    messageSource.setBasenames("i18n/messages")
    messageSource.setDefaultEncoding("UTF-8")
    messageSource.setDefaultLocale(Locale.forLanguageTag("uz"))
    return messageSource
  }
}
