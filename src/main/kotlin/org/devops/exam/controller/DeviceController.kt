package org.devops.exam.controller

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.devops.exam.dto.DeviceDTO
import org.devops.exam.entity.DeviceEntity
import org.devops.exam.repository.DeviceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
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
            })
        }
    }

    @Autowired
    lateinit var environment: Environment

    @GetMapping("/devices")
    fun getDevices() = deviceRepository.findAll()
            .map { DeviceDTO(it.name, it.id) }
            .also {
                registry.gauge("retrieved.devices.count", it.count())
            }
            .map { ok(it) }
            .also {
                if (registry is SimpleMeterRegistry) {

                    println("ER SIMPLE, ${environment.activeProfiles.contentToString()}")
                } else {
                    println("ER _IKKE_ SIMPLE, ${environment.activeProfiles.contentToString()}")
                }
            }
}