package org.devops.exam.controller

import com.github.javafaker.Faker
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.devops.exam.dto.DeviceDTO
import org.devops.exam.entity.DeviceEntity
import org.devops.exam.repository.DeviceRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.created
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class DeviceController(
        private val deviceRepository: DeviceRepository
) {

    @Autowired
    private lateinit var registry: MeterRegistry

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/devices", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun postDevice(): ResponseEntity<DeviceDTO> {

        logger.info("Received POST to /devices")
        return handleConstraintViolation(registry, logger) {

            val entity = DeviceEntity(Faker().funnyName().name())
            val persisted = deviceRepository.save(entity)

            registry.counter("api.response", "created", "device").increment()
            created(URI.create("${persisted.id}")).body(DeviceDTO(persisted.name, persisted.id))
        }
    }

    @GetMapping("/devices")
    fun getDevices() = deviceRepository.findAll()
            .map { DeviceDTO(it.name, it.id) }
            .also {

                registry.gaugeCollectionSize("retrieved.measurements.count", listOf(Tag.of("type", "collection")), it)
                logger.debug("Current device count: ${it.count()}")
                logger.info("User requests devices")
            }
            .map { ok(it) }
}