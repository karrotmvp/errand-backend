package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.event.CompleteNotiEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface CompleteNotiEventRepository : JpaRepository<CompleteNotiEvent, Long> {
    fun findAllByScheduledAtBeforeAndHandledAtIsNullOrderByScheduledAt(scheduledAt: LocalDateTime): MutableList<CompleteNotiEvent>
    fun findByErrand(errand: Errand): CompleteNotiEvent?
}