package org.devops.exam.controller

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import org.devops.exam.dto.MeasurementDTO
import org.devops.exam.entity.MeasurementEntity
import org.devops.exam.repository.DeviceRepository
import org.devops.exam.repository.MeasurementRepository
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

        handleConstraintViolation(registry) {

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
                .onEach {
                    registry.summary("retrieved.measurements.values").record(it.sievert.toDouble())
                }
                .also {
                    registry.gauge("retrieved.measurements.count", it.count())
                }

        return if (sorted) {
            //TODO: find place for normal timer
            val sortedMeasurements = registry.more().longTaskTimer("sorting.measurements").recordCallable {

                measurements.sortedBy { it.sievert }
            }
            ok(sortedMeasurements)
        }
        else ok(measurements)
    }
}