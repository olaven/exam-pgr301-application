package org.devops.exam.controller

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.devops.exam.entity.Device

internal class DeviceControllerTest: ControllerTestBase() {

    @Test
    fun `can POST devices`() {

        val n = 20
        val before = deviceRepository.findAll().count()

        (0 until n).forEach { _ ->

            val device = getDummyDevice()
            post(device)
                    .statusCode(201)
        }

        val after = deviceRepository.findAll().count()

        assertThat(before).isEqualTo(0)
        assertThat(after).isEqualTo(n)
    }

    @Test
    fun `409 if client tries to decide ID`() {

        val device = getDummyDevice()
        device.deviceId = 42 //NOTE: Not null
        post(device)
                .statusCode(409)
    }

    @Test
    fun `returned device has correct ID on deviceId`() {

        val device = getDummyDevice()
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

        val device = getDummyDevice()
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
    fun `getting 400 on device breaking database constraints`() {

        val device = getDummyDevice()
        device.name = "a"; //NOTE: annotated with minimum size = 2
        post(device)
                .statusCode(400)
    }

    private fun post(device: Device) = given()
            .contentType(ContentType.JSON)
            .body(device)
            .post("/device")
            .then()
}