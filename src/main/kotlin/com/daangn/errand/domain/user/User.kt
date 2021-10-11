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
    nickname: String,
    phoneNumber: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var nickname: String = nickname

    @Column(nullable = false, unique = true)
    var phoneNumber: String = phoneNumber

    @Column
    var profileImageUrl: String? = null

    @OneToMany(mappedBy = "customer")
    var errandReqList: MutableList<Errand> = ArrayList()

    @OneToMany(mappedBy = "chosenHelper")
    var errandList: MutableList<Errand> = ArrayList()

    @OneToMany
    var categories: MutableList<HelperHasCategories> = ArrayList()

    companion object {
        val equalsAndHashcodeProperties = arrayOf(
            User::nickname,
            User::phoneNumber,
            User::id,
            User::profileImageUrl,
            User::createdAt,
            User::updatedAt
        )
        val toStringProperties = arrayOf(
            User::id,
            User::nickname,
            User::phoneNumber,
            User::profileImageUrl
        )
    }

    override fun toString() = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashcodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashcodeProperties)

}