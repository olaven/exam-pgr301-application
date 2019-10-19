package org.devops.exam

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties


fun main(args: Array<String>) {
    SpringApplication.run(App::class.java, "--spring.profiles.active=local")
}

@SpringBootApplication
class App