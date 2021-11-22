package com.daangn.errand.domain.event

import com.daangn.errand.domain.BaseEntity
import com.daangn.errand.domain.errand.Errand
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class CompleteNotiEvent(
    @OneToOne(fetch = FetchType.LAZY)
    val errand: Errand,
    @Column(nullable = false)
    val scheduledAt: LocalDateTime
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column
    @ColumnDefault("null")
    var handledAt: LocalDateTime? = null
}