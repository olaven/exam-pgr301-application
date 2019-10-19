package org.devops.exam.controller

import io.micrometer.core.instrument.MeterRegistry
import org.devops.exam.dto.DeviceDTO
import org.devops.exam.dto.MeasurementDTO
import org.devops.exam.entity.MeasurementEntity
import org.devops.exam.repository.DeviceRepository
import org.devops.exam.repository.MeasurementRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.notFound
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
    ): ResponseEntity<MeasurementDTO> {


        if (measurement.id != null) {
            return ResponseEntity.status(409).build()
        }

        val entity = deviceRepository.findById(deviceId)
        if (!entity.isPresent) return notFound().build()

        return handleConstraintViolation(registry) {

            val device = deviceRepository.findById(deviceId)
            if (!device.isPresent) ResponseEntity.notFound()

            val entity = MeasurementEntity(measurement.sievert, measurement.lat, measurement.long, device.get())
            val persisted = measurementRepository.save(entity)
            measurement.apply {
                this.id = persisted.id
            }

            registry.summary("summary.sievert").record(measurement.sievert.toDouble())
            registry.counter("api.response", "created", "measurement").increment()
            ResponseEntity.created(URI.create("/devices/$deviceId/measurements")).body(measurement)
        }
    }

    @GetMapping("/devices/{id}/measurements")
    fun getMeasurements(
            @PathVariable("id") id: Long,
            @PathParam("sorted") sorted: Boolean
    ): ResponseEntity<List<MeasurementDTO>> {

        if (!deviceRepository.existsById(id)) return notFound().build()
        val measurements = measurementRepository.findByDeviceId(id)
                .toList()
                .map { MeasurementDTO(it.sievert, it.lat, it.long, it.id) }
                .also {
                    registry.gauge("retrieved.measurements.count", it.count())
                }

        return if (sorted) {

            val sortedMeasurements = registry.timer("sorting.measurements").recordCallable {

                measurements.sortedBy { it.sievert }
            }
            ResponseEntity.ok(sortedMeasurements)
        }
        else ResponseEntity.ok(measurements)
    }
}