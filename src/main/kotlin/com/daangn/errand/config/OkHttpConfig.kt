package com.daangn.errand.config

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class OkHttpConfig (
    @Value("\${test}")
    val isTest: Boolean
        ){
    @Bean("okHttpClient")
    fun okHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (isTest) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return OkHttpClient()
            .newBuilder().apply {
                // 서버 연결을 최대 10초 수행
                connectTimeout(10, TimeUnit.SECONDS)
                // 서버 요청을 최대 10초 수행
                writeTimeout(10, TimeUnit.SECONDS)
                // 서버 응답을 최대 10초 기다림
                readTimeout(10, TimeUnit.SECONDS)
            }.addNetworkInterceptor(interceptor).build()
    }
}