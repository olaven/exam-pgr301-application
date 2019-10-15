package org.devops.exam.entity

import org.hibernate.validator.constraints.Length
import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.Size
import kotlin.math.min

@Entity
data class Device (

        @field:Length(min = 2, max = 100)
        @field:NotNull
        var name: String,

        /*@field:NotNull
        @field:OneToMany(mappedBy = "device")
        var measurements: List<Measurement>,*/

        @field:Id
        @field:GeneratedValue
        var deviceId: Long? = null
)