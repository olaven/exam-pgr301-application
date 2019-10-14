package org.devops.exam.controller

import org.devops.exam.entity.Device
import org.devops.exam.repository.DeviceRepository
import org.devops.exam.repository.MeasurementRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception
import java.net.URI
import javax.validation.ConstraintViolationException

@RestController
class DeviceController(
        private val deviceRepository: DeviceRepository,
        private val measurementRepository: MeasurementRepository
) {

    @PostMapping("/device", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun postDevice(
            @RequestBody device: Device
    ): ResponseEntity<Device> {

        //i.e. user tries to decide ID
        if (device.deviceId != null) {

           return status(409).body(null)
        }

        return try {

            val persisted = deviceRepository.save(device)
            created(URI.create("${persisted.deviceId}")).body(persisted) //TODO: add endpoint for getting _one_ device (if so, update location)
        } catch (exception: Exception) {

            if (exception is ConstraintViolationException) {

                status(400).build()
            } else {

                badRequest().build()
            }
        }
    }

    @PostMapping(" /device/{deviceId}/measurement")
    fun postMeasurement() {

    }

    @GetMapping("/device/{id}/measurements")
    fun getMeasurements() {

    }

    @GetMapping("/devices")
    fun getDevices() {


    }
}