package com.daangn.errand.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Configuration
class JacksonConfig {
    @Bean
    fun jackson2ObjectMapperBuilder(): Jackson2ObjectMapperBuilder {
        val serializer = CustomJsonSerializer(LocalDateTime::class.java)
        return Jackson2ObjectMapperBuilder().serializers(serializer)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
}

class CustomJsonSerializer(t: Class<LocalDateTime>) : StdSerializer<LocalDateTime>(t) {
    override fun serialize(value: LocalDateTime?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(DateTimeFormatter.ofPattern("yyyy-MM-ddTk:mm:ss").format(value))
    }
}

