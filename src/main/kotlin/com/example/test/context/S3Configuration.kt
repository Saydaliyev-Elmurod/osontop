package com.example.test.context

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration
class S3Configuration {

    @Value($$"${application.s3.endpoint}")
    private lateinit var endpoint: String

    @Value($$"${application.s3.access-key}")
    private lateinit var accessKey: String

    @Value("\${application.s3.secret-key}")
    private lateinit var secretKey: String

    @Value("\${application.s3.region:us-east-1}")
    private lateinit var region: String

    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)

        // SeaweedFS/MinIO uchun pathStyleAccessEnabled(true) muhim
        val serviceConfiguration = S3Configuration.builder()
            .pathStyleAccessEnabled(true)
            .build()

        return S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(region))
            .serviceConfiguration(serviceConfiguration)
            .build()
    }
}
