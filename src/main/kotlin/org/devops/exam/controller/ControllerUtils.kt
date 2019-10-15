package org.devops.exam.controller

import org.springframework.http.ResponseEntity
import javax.validation.ConstraintViolationException

fun<T> handleConstraintViolation(runnable: () -> ResponseEntity<T>): ResponseEntity<T> {

    return try {

        return runnable()
    } catch (exception: Exception) {

        if (exception is ConstraintViolationException) {

            ResponseEntity.status(400).build()
        } else {

            ResponseEntity.badRequest().build()
        }
    }
}