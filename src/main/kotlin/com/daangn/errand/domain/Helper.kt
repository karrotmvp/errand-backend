package com.daangn.errand.domain

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Helper(
    @Column(unique = true, nullable = false)
    val daangnId: String,
    @Column(nullable = false)
    val nickname: String,
    @Column(length = 200)
    val bio: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    val profileImgUrl: String? = null

    val isActive: Boolean = false

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()

    @OneToMany(mappedBy = "chosenHelper")
    var errands: MutableList<Errand> = ArrayList()

    companion object {
        private val equalsAndHashCodeProperties = arrayOf(Helper::id)
        private val toStringProperties = arrayOf(
            Helper::id,
            Helper::daangnId,
            Helper::nickname,
            Helper::bio,
            Helper::profileImgUrl,
            Helper::isActive
        )
    }

    override fun toString(): String = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)
}