package com.daangn.errand.domain.volunteer

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.daangn.errand.domain.BaseEntity
import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.user.User
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