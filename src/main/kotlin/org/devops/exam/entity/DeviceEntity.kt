package org.devops.exam.entity

import org.hibernate.validator.constraints.Length
import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class DeviceEntity (

        @field:Length(min = 2, max = 100)
        @field:NotNull
        var name: String,

        @field:Id
        @field:GeneratedValue
        var id: Long? = null
)