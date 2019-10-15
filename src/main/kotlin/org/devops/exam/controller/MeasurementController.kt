package org.devops.exam.controller

import org.devops.exam.entity.Device
import org.devops.exam.entity.Measurement
import org.devops.exam.repository.DeviceRepository
import org.devops.exam.repository.MeasurementRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.web.bind.annotation.*
import java.net.URI


@RestController
class MeasurementController(
        private val deviceRepository: DeviceRepository,
        private val measurementRepository: MeasurementRepository
) {

    @PostMapping("/devices/{deviceId}/measurements")
    fun postMeasurement(
            @PathVariable("deviceId") deviceId: Long,
            @RequestBody measurement: Measurement
    ): ResponseEntity<Measurement> {

        if (measurement.id != null || deviceId != measurement.device.deviceId) {
            return ResponseEntity.status(409).build()
        }

        val entity = deviceRepository.findById(deviceId)
        if (!entity.isPresent) return notFound().build()

        return handleConstraintViolation {

            val persisted = measurementRepository.save(measurement)
            ResponseEntity.created(URI.create("/devices/$deviceId/measurements")).body(persisted)
        }
    }

    @GetMapping("/devices/{id}/measurements")
    fun getMeasurements(
            @PathVariable("id") id: Long
    ): ResponseEntity<Iterable<Measurement>> {

        if (!deviceRepository.existsById(id)) return notFound().build()
        val measurements = measurementRepository.findByDeviceDeviceId(id)
        return ResponseEntity.ok(measurements)
    }
}