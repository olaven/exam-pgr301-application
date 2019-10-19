package org.devops.exam.controller

import io.micrometer.core.instrument.MeterRegistry
import org.junit.internal.Throwables
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.transaction.TransactionSystemException
import javax.persistence.RollbackException
import javax.validation.ConstraintViolationException

fun<T> handleConstraintViolation(
        registry: MeterRegistry,
        runnable: () -> ResponseEntity<T>
): ResponseEntity<T> {

    return try {

        runnable()
    } catch (exception: Exception) {

        if (exception is TransactionSystemException) {

            registry.counter("api.response", "user.error", "bad.request").increment()
            ResponseEntity.status(400).build()
        } else {

            registry.counter("api.response", "server.error", "persisting").increment()
            ResponseEntity.status(500).build()
        }
    }
}