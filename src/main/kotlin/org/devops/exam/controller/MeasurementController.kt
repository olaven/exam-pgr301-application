package org.devops.exam.controller

import io.micrometer.core.instrument.DistributionSummary
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import org.devops.exam.dto.MeasurementDTO
import org.devops.exam.entity.MeasurementEntity
import org.devops.exam.repository.DeviceRepository
import org.devops.exam.repository.MeasurementRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.websocket.server.PathParam


@RestController
class MeasurementController {

    @Autowired
    private lateinit var deviceRepository: DeviceRepository
    @Autowired
    private lateinit var measurementRepository: MeasurementRepository
    @Autowired
    private lateinit var registry: MeterRegistry

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/devices/{deviceId}/measurements")
    fun postMeasurement(
            @PathVariable("deviceId") deviceId: Long,
            @RequestBody measurement: MeasurementDTO
    ): ResponseEntity<MeasurementDTO> = registry.timer("posting.measurement").recordCallable {

        if (measurement.id != null) {
            return@recordCallable ResponseEntity.status(409).build()
        }

        val deviceEntity = deviceRepository.findById(deviceId)
        if (!deviceEntity.isPresent) return@recordCallable notFound().build()

        handleConstraintViolation(registry, logger) {

            val measurementEntity = deviceRepository.findById(deviceId)
            if (!measurementEntity.isPresent) notFound().build<MeasurementDTO>()

            val entity = MeasurementEntity(measurement.sievert, measurement.lat, measurement.long, measurementEntity.get())
            val persisted = measurementRepository.save(entity)
            measurement.apply {
                this.id = persisted.id
            }

            registry.counter("api.response", "created", "measurement").increment()
            ResponseEntity.created(URI.create("/devices/$deviceId/measurements")).body(measurement)
        }
    }


    @GetMapping("/devices/{id}/measurements")
    fun getMeasurements(
            @PathVariable("id") id: Long,
            @PathParam("sorted") sorted: Boolean //NOTE: added to show LongTaskTimer
    ): ResponseEntity<List<MeasurementDTO>> {
        
        if (!deviceRepository.existsById(id)) return notFound().build()
        val measurements = measurementRepository.findByDeviceId(id)
                .toList()
                .map { MeasurementDTO(it.sievert, it.lat, it.long, it.id) }
                .onEach { measurement ->

                    logger.debug("Sending measurement: sievert: ${measurement.sievert}, lat: ${measurement.lat}, long: ${measurement.long}")
                    //registry.summary("retrieved.measurements.values").percentile()//.record(it.sievert.toDouble())
                    DistributionSummary.builder("retrieved.measurements.values")
                            .publishPercentiles(25.0, 50.0, 75.0)
                            .register(registry)
                }
                .also { measurement ->

                    logger.info("${measurement.count()} (all) from device $id were retrieved")
                    registry.gaugeCollectionSize("retrieved.measurements.count", listOf(Tag.of("type", "collection")), measurement)
                    registry.gauge("retrieved.measurements.average", measurement.map { it.sievert }.average())
                }

        return if (sorted) {

            logger.debug("User wants to sort")
            val sortedMeasurements = registry.more().longTaskTimer("sorting.measurements").recordCallable {

                measurements.sortedBy { it.sievert }
            }
            ok(sortedMeasurements)
        }
        else ok(measurements)
    }
}