package org.devops.exam.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.devops.exam.dto.DeviceDTO
import org.devops.exam.entity.DeviceEntity
import org.junit.jupiter.api.Test
import kotlin.random.Random

internal class DeviceControllerTest: ControllerTestBase() {

    @Test
    fun `can POST devices`() {

        val n = 20
        val before = deviceRepository.findAll().count()

        (0 until n).forEach { _ ->

            val device = dummyDevice()
            post(device)
                    .statusCode(201)
        }

        val after = deviceRepository.findAll().count()

        assertThat(before).isEqualTo(0)
        assertThat(after).isEqualTo(n)
    }

    @Test
    fun `returned device has correct ID on deviceId`() {

        val device = dummyDevice()
        val id = post(device)
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("deviceId")

        val retrieved = deviceRepository.findById(id)
        assertThat(retrieved)
                .isPresent
    }

    @Test
    fun `returned device has correct ID in location`() {

        val device = dummyDevice()
        val id = post(device)
                .statusCode(201)
                .extract()
                .header("location")
                .toLong()

        val retrieved = deviceRepository.findById(id)
        assertThat(retrieved)
                .isPresent
    }
    
    @Test
    fun `getting devices returns 200`() {

        get()
                .statusCode(200)
    }

    @Test
    fun `getting returns all devices`() {

        val n = Random.nextInt(1, 20)
        val devices = deviceRepository.saveAll(
                (0 until n)
                        .map { dummyDevice() }
                        .map { DeviceEntity(it.name) }
        ).toList()

        assertThat(devices.count())
                .isEqualTo(n)
    }

    private fun get() = given()
            .get("/devices")
            .then()

    private fun post(dto: DeviceDTO) = given()
            .contentType(ContentType.JSON)
            .body(dto)
            .post("/devices")
            .then()
}