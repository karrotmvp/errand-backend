package com.daangn.errand.domain

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.hibernate.annotations.ColumnDefault
import javax.persistence.*

@Entity
class Errand (
    @ManyToOne
    @JoinColumn(name = "customer_id")
    val customer: Customer,
    @ManyToOne
    @JoinColumn(name = "category_id")
    val category: Category,
    @Column(nullable = false)
    val regionId: String,
    @Column(nullable = false)
    val detail: String
        ){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false)
    @ColumnDefault("false")
    val isCompleted: Boolean = false

    @ManyToOne
    @JoinColumn(name = "chosen_helper_id")
    val chosenHelper: Helper? = null

    companion object{
        private val equalsAndHashCodeProperties = arrayOf(Errand::id)
        private val toStringProperties = arrayOf(
            Errand::id,
            Errand::customer,
            Errand::regionId,
            Errand::detail,
            Errand::chosenHelper,
            Errand::isCompleted
        )
    }

    override fun toString(): String = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)

}