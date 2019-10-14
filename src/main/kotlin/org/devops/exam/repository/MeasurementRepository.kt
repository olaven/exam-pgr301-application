package org.devops.exam.repository

import org.devops.exam.entity.Measurement
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MeasurementRepository: CrudRepository<Measurement, Long>