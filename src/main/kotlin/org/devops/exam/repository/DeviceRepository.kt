package org.devops.exam.repository

import org.devops.exam.entity.DeviceEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository: CrudRepository<DeviceEntity, Long>