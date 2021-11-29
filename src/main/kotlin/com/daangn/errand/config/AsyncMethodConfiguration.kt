package com.daangn.errand.config

import io.sentry.spring.SentryTaskDecorator
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurerSupport
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
class AsyncMethodConfiguration: AsyncConfigurerSupport() {
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.setTaskDecorator(SentryTaskDecorator())
        return executor
    }
}