package org.devops.exam.controller

import org.devops.exam.repository.DeviceRepository
import org.devops.exam.repository.MeasurementRepository
import org.springframework.stereotype.Service

@Service
class ResetDatabase(
        private val deviceRepository: DeviceRepository,
        private val measurementRepository: MeasurementRepository
) {

    fun reset() {

        measurementRepository.deleteAll()
        deviceRepository.deleteAll()
    }
}