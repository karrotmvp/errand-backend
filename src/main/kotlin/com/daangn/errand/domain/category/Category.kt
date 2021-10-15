package com.daangn.errand.domain.category

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.daangn.errand.domain.errand.Errand
import javax.persistence.*

@Entity
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var name: String = ""

    companion object{
        private val equalsAndHashCodeProperties = arrayOf(Category::id)
        private val toStringProperties = arrayOf(
            Category::id,
            Category::name
        )
    }

    override fun toString(): String = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)
}