package org.devops.exam.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.devops.exam.entity.Measurement
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class MeasurementControllerTest: ControllerTestBase() {

    @Test
    fun `can POST measurements`() {

        val n = 20
        val before = measurementRepository.findAll().count()

        (0 until n).forEach { _ ->

            val device = deviceRepository.save(dummyDevice())
            post(deviceId = device.deviceId!!, measurement = dummyMeasurement(device))
                    .statusCode(201)
        }

        val after = measurementRepository.findAll().count()

        assertThat(before).isEqualTo(0)
        assertThat(after).isEqualTo(n)
    }

    @Test
    fun `409 if client tries to decide ID`() {

        val device = deviceRepository.save(dummyDevice())
        val measurement = dummyMeasurement(device)
        measurement.id = 99//NOTE: not null
        post(device.deviceId!!, measurement)
                .statusCode(409)
    }

    @Test
    fun `409 if deviceId and path does not match`() {

        val wrongDevice = deviceRepository.save(dummyDevice())
        val device = deviceRepository.save(dummyDevice())
        val measurement = dummyMeasurement(device)
        post(wrongDevice.deviceId!!, measurement)
                .statusCode(409)
    }

    @Test
    fun `returned device has correct ID`() {

        val device = deviceRepository.save(dummyDevice())
        val id = post(device.deviceId!!, dummyMeasurement(device))
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

        val device = dummyDevice() //NOTE: not perssited
        device.deviceId = 99
        val measurement = dummyMeasurement(device)
        post(device.deviceId!!, measurement)
                .statusCode(404)
    }

    @Test
    fun `returned device has correct ID in location`() {

        val device = deviceRepository.save(dummyDevice())
        val location = post(device.deviceId!!, dummyMeasurement(device))
                .statusCode(201)
                .extract()
                .header("location")

        //NOTE: returns every measurement, not specific one. TODO: change if adding endpoints is allowed.
        given().get(location).then().statusCode(200)
    }

    @Test
    fun `getting 400 on device breaking database constraints`() {

        val device = deviceRepository.save(dummyDevice())
        val measurement = dummyMeasurement(device)
        measurement.lat = 91f//NOTE: contraint at 90
        post(device.deviceId!!, measurement)
                .statusCode(400)
    }

    @Test
    fun `getting returns 404 if invalid entity`() {

        get(99)
                .statusCode(404)
    }

    @Test //TODO: fix recursve problem visible here (always eager fetching)
    fun `getting measurements returns only those on device`() {

        val outerRuns = Random.nextInt(2, 5)
        (0 until outerRuns).forEach {_ ->

            val device = deviceRepository.save(dummyDevice())
            val persistedCount = Random.nextInt(3, 10)
            (0 until persistedCount).forEach { _ ->

                post(device.deviceId!!, dummyMeasurement(device))
            }

            val retrieved = get(device.deviceId!!)
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getList<Measurement>("")

            assertThat(retrieved)
                    .hasSize(persistedCount)
        }
    }

    private fun get(deviceId: Long) = given()
            .get("/devices/${deviceId}/measurements")
            .then()

    private fun post(deviceId: Long, measurement: Measurement) = given()
            .contentType(ContentType.JSON)
            .body(measurement)
            .post("/devices/$deviceId/measurements")
            .then()
}