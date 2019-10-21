package org.devops.exam.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.devops.exam.dto.MeasurementDTO
import org.devops.exam.entity.DeviceEntity
import org.devops.exam.entity.MeasurementEntity
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class MeasurementControllerTest: ControllerTestBase() {

    @Test
    fun `can POST measurements`() {

        val n = 20
        val before = measurementRepository.findAll().count()

        (0 until n)
                .map { dummyDevice() }
                .map { DeviceEntity(it.name) }
                .forEach {

                    val device = deviceRepository.save(it)
                    val measurement = dummyMeasurement()
                    post(deviceId = device.id!!, measurement = measurement)
                            .statusCode(201)
                }

        val after = measurementRepository.findAll().count()

        assertThat(before).isEqualTo(0)
        assertThat(after).isEqualTo(n)
    }

    @Test
    fun `409 if client tries to decide ID`() {

        val device = persistDevice()
        val measurement = dummyMeasurement()
        measurement.id = 99//NOTE: not null
        post(device.id!!, measurement)
                .statusCode(409)
    }

    @Test
    fun `returned device has correct ID`() {

        val device = persistDevice()
        val id = post(device.id!!, dummyMeasurement())
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id")

        val retrieved = measurementRepository.findById(id)
        assertThat(retrieved)
                .isPresent
    }

    @Test
    fun `returns 404 if device is not found`() {

        val device = DeviceEntity("test name", 99) //NOTE: not persisted
        val measurement = dummyMeasurement()
        post(device.id!!, measurement)
                .statusCode(404)
    }

    @Test
    fun `returned device has correct ID in location`() {

        val device = persistDevice()
        val location = post(device.id!!, dummyMeasurement())
                .statusCode(201)
                .extract()
                .header("location")

        //NOTE: returns every measurement, not specific one. TODO: change if adding endpoints is allowed.
        given().get(location).then().statusCode(200)
    }

    @Test
    fun `getting 400 on device breaking database constraints`() {

        val device = persistDevice()
        val measurement = dummyMeasurement()
        measurement.lat = 91f//NOTE: contraint at 90
        post(device.id!!, measurement)
                .statusCode(400)
    }

    @Test
    fun `getting returns 404 if invalid entity`() {

        get(99)
                .statusCode(404)
    }

    @Test
    fun `getting measurements returns only those on device`() {

        val outerRuns = Random.nextInt(2, 5)
        (0 until outerRuns).forEach {_ ->

            val device = persistDevice()
            val persistedCount = Random.nextInt(3, 10)
            (0 until persistedCount).forEach { _ ->

                post(device.id!!, dummyMeasurement())
            }

            val retrieved = get(device.id!!)
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getList<MeasurementEntity>("")

            assertThat(retrieved)
                    .hasSize(persistedCount)
        }
    }

    @Test
    fun `measurements are not sorted by default`() {

        val device = persistDevice()
        (0 until 20).forEach {

            persistMeasurement(device = device)
        }

        val retrieved = get(deviceId = device.id!!)
                .extract()
                .`as`(Array<MeasurementDTO>::class.java)

        assertThat(retrieved.sortedBy { it.sievert })
                .isNotSameAs(retrieved)
    }

    @Test
    fun `measurements are  sorted if explicitly asked to`() {

        val device = persistDevice()
        (0 until 20).forEach {

            persistMeasurement(device = device)
        }

        val retrieved = given()
                .get("/devices/${device.id}/measurements?sorted=true")
                .then()
                .extract()
                .`as`(Array<MeasurementDTO>::class.java)

        assertThat(retrieved.map { it.sievert })
                .isSorted
    }

    private fun get(deviceId: Long) = given()
            .get("/devices/${deviceId}/measurements")
            .then()

    private fun post(deviceId: Long, measurement: MeasurementDTO) = given()
            .contentType(ContentType.JSON)
            .body(measurement)
            .post("/devices/$deviceId/measurements")
            .then()

    private fun getAverage(deviceId: Long) = given()
            .get("/devices/$deviceId/average")
            .then()
            .extract()
            .asString()
            .toLong()
}