package com.example.test.context

import com.example.test.model.SmsCache
import com.example.test.model.VerificationResponse
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableCaching
class CacheConfig {

  @Bean
  fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Any> {
    val mapper = ObjectMapper()

    mapper.registerModule(KotlinModule.Builder().build())
    mapper.registerModule(JavaTimeModule())
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    mapper.activateDefaultTyping(
      BasicPolymorphicTypeValidator.builder()
        .allowIfBaseType(Any::class.java)
        .build(),
      ObjectMapper.DefaultTyping.NON_FINAL,
      JsonTypeInfo.As.PROPERTY
    )

    val serializer = Jackson2JsonRedisSerializer(mapper, Any::class.java)

    val context = RedisSerializationContext
      .newSerializationContext<String, Any>(StringRedisSerializer())
      .value(serializer)
      .hashValue(serializer)
      .build()

    return ReactiveRedisTemplate(factory, context)
  }
  @Configuration
  class RedisConfig {

    @Bean
    fun verificationResponseTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, SmsCache> {
      val valueSerializer = Jackson2JsonRedisSerializer(SmsCache::class.java)

      val context = RedisSerializationContext
        .newSerializationContext<String, SmsCache>(StringRedisSerializer())
        .value(valueSerializer)
        .build()

      return ReactiveRedisTemplate(factory, context)
    }
  }
}
