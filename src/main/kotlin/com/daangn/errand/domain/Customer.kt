package com.daangn.errand.domain

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Customer(
    @Column(unique = true, nullable = false)
    val daangnId: String,
    @Column(length = 50, nullable = false)
    val nickname: String,
    @Column
    val profileImageUrl: String? = null,
    @Column(nullable = false) // 앱에 들어올 때 query parameter로 들어온다.
    val region_id: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    val updatedAt: LocalDateTime = LocalDateTime.now()

    @OneToMany(mappedBy = "customer")
    var errands: MutableList<Errand> = ArrayList()

    companion object {
        private val equalsAndHashCodeProperties = arrayOf(Customer::id)
        private val toStringProperties = arrayOf(
            Customer::id,
            Customer::daangnId,
            Customer::nickname,
            Customer::profileImageUrl,
            Customer::region_id,
            Customer::createdAt,
            Customer::updatedAt
        )
    }

    override fun toString() = kotlinToString(properties = toStringProperties)

    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)

    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)

}