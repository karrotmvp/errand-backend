package com.daangn.errand.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class S3Config(
    @Value("\${cloud.aws.credentials.access-key}")
    val accessKey: String,

    @Value("\${cloud.aws.credentials.secret-key}")
    val secretKey: String,

    @Value("\${cloud.aws.region.static}")
    val region: String
) {
    @Primary
    @Bean
    fun awsS3Client(): AmazonS3Client {
        val awsCreds = BasicAWSCredentials(accessKey, secretKey)
        return AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(AWSStaticCredentialsProvider(awsCreds))
            .build() as AmazonS3Client
    }
}