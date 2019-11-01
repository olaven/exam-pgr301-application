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

    /*
    * This is triggered, but it does not
    * do anything on its own.
    *
    * In the application-properties file,
    * metrics will be disabled for all
    * environments except `local`*/

    @Primary
    @Bean
    fun meterRegistry(): MeterRegistry = SimpleMeterRegistry()
}