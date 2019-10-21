package org.devops.exam.entity

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.validation.constraints.Max
import javax.validation.constraints.Min

@Entity
class MeasurementEntity (

        @field:NotNull
        @field:Min(0)
         val sievert: Long,

        @field:NotNull
        @field:Min(-90)
        @field:Max(90)
        var lat: Float,

        @field:NotNull
        @field:Min(-180)
        @field:Max(180)
        val long: Float,

        @field:NotNull
        @field:ManyToOne
        val device: DeviceEntity,

        @field:Id
        @field:GeneratedValue
        @field:NotNull
        var id: Long? = null // set automatically when persisting
)