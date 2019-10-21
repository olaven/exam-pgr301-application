package org.devops.exam

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!local")
class MicrometerConfig {

    @Primary
    @Bean
    fun meterRegistry(): MeterRegistry = SimpleMeterRegistry()
}