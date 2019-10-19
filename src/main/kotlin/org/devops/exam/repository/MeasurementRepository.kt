package org.devops.exam.repository

import org.devops.exam.entity.MeasurementEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MeasurementRepository: CrudRepository<MeasurementEntity, Long> {

    fun findByDeviceId(id: Long): List<MeasurementEntity>
}