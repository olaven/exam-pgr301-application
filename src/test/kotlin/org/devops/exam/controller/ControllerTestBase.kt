package org.devops.exam.controller

import com.github.javafaker.Faker
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.devops.exam.App
import org.devops.exam.entity.Device
import org.devops.exam.entity.Measurement
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

    @Autowired
    private lateinit var resetDatabase: ResetDatabase

    private val faker = Faker()

    @LocalServerPort
    protected val port = 0

    @BeforeEach
    fun init() {

        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        resetDatabase.reset()
    }

    protected fun dummyDevice() = Device(
            name = faker.funnyName().name(),
            measurements = emptyList() // added through `Measurement.device`
    )

    protected fun dummyMeasurement(device: Device) = Measurement(
            sievert = faker.random().nextInt(1000).toLong(),
            lat = faker.random().nextInt(-90, 90).toFloat(),
            long = faker.random().nextInt(-180, 180).toFloat(),
            device = device
    )
}