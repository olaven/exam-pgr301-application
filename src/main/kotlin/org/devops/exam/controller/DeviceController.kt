package org.devops.exam.controller

import io.micrometer.core.instrument.MeterRegistry
import org.devops.exam.dto.DeviceDTO
import org.devops.exam.entity.DeviceEntity
import org.devops.exam.repository.DeviceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class DeviceController(
        private val deviceRepository: DeviceRepository
) {

    @Autowired
    private lateinit var registry: MeterRegistry

    @PostMapping("/devices", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun postDevice(
            @RequestBody dto: DeviceDTO //TODO: ask if this should take a dto or not. If not, update!
    ): ResponseEntity<DeviceDTO> {

        //i.e. user tries to decide ID
        if (dto.deviceId != null) {

            registry.counter("api.response", "user.error", "conflict").increment()
            return status(409).body(null)
        }

        return handleConstraintViolation(registry) {

            val entity = DeviceEntity(dto.name)
            val persisted = deviceRepository.save(entity)

            registry.counter("api.response", "created", "device").increment()
            created(URI.create("${persisted.id}")).body(dto.apply {
                deviceId = persisted.id
            }) //TODO: add endpoint for getting _one_ dto (if so, update location)
        }
    }

    @GetMapping("/devices")
    fun getDevices() = ok(deviceRepository.findAll())
}