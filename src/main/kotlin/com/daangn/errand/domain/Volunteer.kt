package com.daangn.errand.domain

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import javax.persistence.*

@Entity
class Volunteer(
    @ManyToOne
    val errand: Errand,
    @ManyToOne
    val helper: User,
    @Column
    val bio: String? = null
): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    companion object {
        private val equalsAndHashCodeProperties = arrayOf(Volunteer::id)
        private val toStringProperties = arrayOf(
            Volunteer::id,
            Volunteer::errand,
            Volunteer::helper
        )
    }

    override fun toString(): String = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)
}