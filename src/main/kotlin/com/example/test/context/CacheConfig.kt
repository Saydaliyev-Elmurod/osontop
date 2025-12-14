package com.example.test.context

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration


@Configuration
@EnableCaching
class CacheConfig {

//  @Bean
//  fun cachedSmsDigits(): Cache<UUID, String> = Caffeine.newBuilder()
//    .expireAfterWrite(Duration.ofHours(3))
//    .build()
//
//  @Bean
//  fun cachedUserOnSignUp(): Cache<String, UserCacheModel> = Caffeine.newBuilder()
//    .expireAfterWrite(Duration.ofHours(3))
//    .build()
//
//  @Bean
//  fun cachedEskiz(): Cache<String, String> = Caffeine.newBuilder()
//    .expireAfterWrite(Duration.ofDays(25))
//    .build()
}
