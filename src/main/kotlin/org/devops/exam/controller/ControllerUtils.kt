package org.devops.exam.controller

import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.springframework.http.ResponseEntity
import org.springframework.transaction.TransactionSystemException

fun<T> handleConstraintViolation(
        registry: MeterRegistry,
        logger: Logger,
        runnable: () -> ResponseEntity<T>
): ResponseEntity<T> {

    return try {

        runnable()
    } catch (exception: Exception) {

        if (exception is TransactionSystemException) {

            registry.counter("api.response", "user.error", "bad.request").increment()
            logger.warn("User provided malformed object ${exception.message}")
            ResponseEntity.status(400).build()
        } else {

            registry.counter("api.response", "server.error", "persisting").increment()
            logger.error("occured when persisting ${exception.message}")
            ResponseEntity.status(500).build()
        }
    }
}