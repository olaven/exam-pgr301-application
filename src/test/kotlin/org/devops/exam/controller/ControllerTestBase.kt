package org.devops.exam.controller

import com.github.javafaker.Faker
import io.restassured.RestAssured
import org.devops.exam.App
import org.devops.exam.dto.DeviceDTO
import org.devops.exam.dto.MeasurementDTO
import org.devops.exam.entity.DeviceEntity
import org.devops.exam.entity.MeasurementEntity
import org.devops.exam.repository.DeviceRepository
import org.devops.exam.repository.MeasurementRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [ App::class ], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ControllerTestBase {

    @Autowired
    protected lateinit var deviceRepository: DeviceRepository
    @Autowired
    protected lateinit var measurementRepository: MeasurementRepository

    private val faker = Faker()

    @LocalServerPort
    protected val port = 0

    @BeforeEach
    fun init() {

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        // clean database before each test
        measurementRepository.deleteAll()
        deviceRepository.deleteAll()
    }

    protected fun dummyDevice() = DeviceDTO(
            name = faker.funnyName().name()
            //measurements = emptyList() // added through `Measurement.device`
    )

    protected fun dummyMeasurement() = MeasurementDTO(
            sievert = faker.random().nextInt(1000).toLong(),
            lat = faker.random().nextInt(-90, 90).toFloat(),
            long = faker.random().nextInt(-180, 180).toFloat()
    )

    protected fun persistDevice(dto: DeviceDTO = dummyDevice()): DeviceEntity {

        val entity = DeviceEntity(dto.name)
        return deviceRepository.save(entity)
    }

    protected fun persistMeasurement(dto: MeasurementDTO = dummyMeasurement(), device: DeviceEntity): MeasurementEntity {

        val entity = MeasurementEntity(dto.sievert, dto.lat, dto.lat, device)
        return measurementRepository.save(entity)
    }
}