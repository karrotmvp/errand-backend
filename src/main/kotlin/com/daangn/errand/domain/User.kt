package com.daangn.errand.domain

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
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
    var mannerTemp: Float? = null // TODO: 매너온도 저장할지 말지 그리즈한테 물어보기

    @Column
    var profileImageUrl: String? = null

    @OneToMany(mappedBy = "customer")
    var errandReqList: MutableList<Errand> = ArrayList()

    @OneToMany(mappedBy = "chosenHelper")
    var errandList: MutableList<Errand> = ArrayList()

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