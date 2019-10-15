package org.devops.exam.repository

import org.devops.exam.entity.Device
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeviceRepository: CrudRepository<Device, Long>