package com.daangn.errand.domain

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.daangn.errand.domain.category.Category
import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.help.Help
import com.daangn.errand.domain.user.User
import javax.persistence.*

@Entity
class HelperHasCategories(
    @ManyToOne val errand: Errand,
    @ManyToOne val helper: User
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    companion object{
        private val equalsAndHashCodeProperties = arrayOf(HelperHasCategories::id)
        private val toStringProperties = arrayOf(
            HelperHasCategories::id,
            HelperHasCategories::helper
        )
    }

    override fun toString(): String = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)
}