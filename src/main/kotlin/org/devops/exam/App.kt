package org.devops.exam

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

fun main(args: Array<String>) {
    //SpringApplication.run(App::class.java, "--spring.profiles.active=local")
    SpringApplication.run(App::class.java)
}

@SpringBootApplication
class App