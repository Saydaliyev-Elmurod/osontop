//package com.example.test.context
//
//import org.springframework.cache.annotation.EnableCaching
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.data.redis.cache.RedisCacheConfiguration
//import org.springframework.data.redis.connection.RedisConnectionFactory
//import org.springframework.data.redis.core.RedisTemplate
//import org.springframework.data.redis.serializer.RedisSerializationContext
//import org.springframework.data.redis.serializer.StringRedisSerializer
//import tools.jackson.databind.ObjectMapper
//import java.time.Duration
//
//@Configuration
//@EnableCaching
//class CacheConfig() {
//
//  @Bean
//  fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
//    return RedisTemplate<String, Any>().apply {
//      setConnectionFactory(connectionFactory)
//      keySerializer = StringRedisSerializer()
//      valueSerializer = GenericJackson2JsonRedisSerializer(ObjectMapper())
//    }
//  }
//
//  @Bean
//  fun cacheConfiguration(): RedisCacheConfiguration {
//    return RedisCacheConfiguration.defaultCacheConfig()
//      .entryTtl(Duration.ofMinutes(60))
//      .disableCachingNullValues()
//      .serializeKeysWith(
//        RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
//      )
//      .serializeValuesWith(
//        RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(mapper))
//      )
//  }
//
//
//
//  private fun <T> createConfig(ttl: Duration, clazz: Class<T>): RedisCacheConfiguration {
//    return RedisCacheConfiguration.defaultCacheConfig()
//      .disableCachingNullValues()
//      .entryTtl(ttl)
//      .serializeKeysWith(
//        RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
//      )
//      .serializeValuesWith(
//        RedisSerializationContext.SerializationPair.fromSerializer(Jackson2JsonRedisSerializer(mapper, clazz))
//      )
//  }
//
//  private fun createGenericConfig(ttl: Duration): RedisCacheConfiguration {
//    return RedisCacheConfiguration.defaultCacheConfig()
//      .disableCachingNullValues()
//      .entryTtl(ttl)
//      .serializeKeysWith(
//        RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
//      )
//      .serializeValuesWith(
//        RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(mapper))
//      )
//  }
//}