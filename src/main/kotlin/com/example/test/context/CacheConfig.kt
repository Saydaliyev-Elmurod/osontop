package com.example.test.context

import com.example.test.model.cache.SmsCache
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

  @Bean
  fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
    val config = RedisCacheConfiguration.defaultCacheConfig()
      .entryTtl(Duration.ofMinutes(10))
      .disableCachingNullValues()

    return RedisCacheManager.builder(connectionFactory)
      .cacheDefaults(config)
      .build()
  }

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
      val mapper = ObjectMapper()
      mapper.registerModule(KotlinModule.Builder().build())
      mapper.registerModule(JavaTimeModule())
      mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

      val valueSerializer = Jackson2JsonRedisSerializer(mapper, SmsCache::class.java)

      val context = RedisSerializationContext
        .newSerializationContext<String, SmsCache>(StringRedisSerializer())
        .value(valueSerializer)
        .build()

      return ReactiveRedisTemplate(factory, context)
    }
  }
}
