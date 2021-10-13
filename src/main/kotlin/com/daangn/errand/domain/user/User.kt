package com.daangn.errand.domain.user

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.daangn.errand.domain.BaseEntity
import com.daangn.errand.domain.HelperHasCategories
import com.daangn.errand.domain.errand.Errand
import javax.persistence.*

@Entity
class User(
    daangnId: String
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var daangnId: String = daangnId

    @OneToMany(mappedBy = "customer")
    var errandReqList: MutableList<Errand> = ArrayList()

    @OneToMany(mappedBy = "chosenHelper")
    var errandList: MutableList<Errand> = ArrayList()

    @OneToMany
    var categories: MutableList<HelperHasCategories> = ArrayList()

    companion object {
        val equalsAndHashcodeProperties = arrayOf(
            User::id,
            User::daangnId,
            User::createdAt,
            User::updatedAt
        )
        val toStringProperties = arrayOf(
            User::id,
            User::daangnId
        )
    }

    override fun toString() = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashcodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashcodeProperties)

}