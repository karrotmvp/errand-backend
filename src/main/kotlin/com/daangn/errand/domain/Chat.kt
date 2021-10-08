package com.daangn.errand.domain

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Chat(
    @OneToOne
    val errand: Errand,
    
    @Column(nullable = false)
    val senderId: String, // daangnId
    
    @Column(nullable = false)
    val text: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @CreatedDate
    val createdAt: LocalDateTime = LocalDateTime.now()

    companion object {
        private val equalsAndHashCodeProperties = arrayOf(Chat::id)
        private val toStringProperties = arrayOf(
            Chat::id,
            Chat::errand,
            Chat::senderId,
            Chat::text,
            Chat::createdAt
        )
    }
    override fun toString(): String = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)

}