package com.daangn.errand.domain.help

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.daangn.errand.domain.BaseEntity
import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.user.User
import javax.persistence.*

@Entity
class Help(
    @ManyToOne
    val errand: Errand,
    @ManyToOne
    val helper: User,
    @Column(columnDefinition = "TEXT")
    val appeal: String,
    @Column(nullable = false)
    var phoneNumber: String,
    @Column(nullable = false)
    var regionId: String
): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    companion object {
        private val equalsAndHashCodeProperties = arrayOf(Help::id)
        private val toStringProperties = arrayOf(
            Help::id,
            Help::errand,
            Help::helper,
            Help::appeal,
            Help::phoneNumber,
            Help::regionId
        )
    }

    override fun toString(): String = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)
}