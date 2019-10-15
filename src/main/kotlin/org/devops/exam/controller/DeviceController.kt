package org.devops.exam.controller

import org.devops.exam.entity.Device
import org.devops.exam.entity.Measurement
import org.devops.exam.repository.DeviceRepository
import org.devops.exam.repository.MeasurementRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*
import sun.audio.AudioDevice.device
import java.lang.Exception
import java.net.URI
import javax.validation.ConstraintViolationException
import kotlin.system.measureNanoTime

@RestController
class DeviceController(
        private val deviceRepository: DeviceRepository,
        private val measurementRepository: MeasurementRepository
) {

    @PostMapping("/devices", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun postDevice(
            @RequestBody device: Device //TODO: ask if this should take a device or not. If not, update!
    ): ResponseEntity<Device> {

        //i.e. user tries to decide ID
        if (device.deviceId != null) {

           return status(409).body(null)
        }

        return handleConstraintViolation {

            val persisted = deviceRepository.save(device)
            created(URI.create("${persisted.deviceId}")).body(persisted) //TODO: add endpoint for getting _one_ device (if so, update location)
        }
    }

    @PostMapping(" /devices/{deviceId}/measurements")
    fun postMeasurement(
            @PathVariable("deviceId") deviceId: Long,
            @RequestBody measurement: Measurement
    ): ResponseEntity<Measurement> {

        if (measurement.id != null || deviceId != measurement.id) {
            return status(409).build()
        }

        val entity = deviceRepository.findById(deviceId)
        if (!entity.isPresent) return notFound().build()

        return handleConstraintViolation {

            val persisted = measurementRepository.save(measurement)
            created(URI.create("/devices/$deviceId/measurements")).body(persisted)
        }
    }

    @GetMapping("/devices/{id}/measurements")
    fun getMeasurements(
            @PathVariable("id") id: Long
    ): ResponseEntity<Iterable<Measurement>> {

        if (!deviceRepository.existsById(id)) return notFound().build()
        val measurements = measurementRepository.findByDeviceDeviceId(id)
        return ok(measurements)
    }

    @GetMapping("/devices")
    fun getDevices() = ok(deviceRepository.findAll())
}