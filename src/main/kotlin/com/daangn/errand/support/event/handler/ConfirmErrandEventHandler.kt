package com.daangn.errand.support.event.handler

import com.daangn.errand.repository.CompleteNotiEventRepository
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.support.event.HelperConfirmedErrandEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class ConfirmErrandEventHandler(
    private val errandRepository: ErrandRepository,
    private val completeNotiEventRepository: CompleteNotiEventRepository,
) {
    @Async
    @EventListener
    @Transactional
    fun findUnhandledNotificationEvent(event: HelperConfirmedErrandEvent) {
        val errand = errandRepository.findById(event.errandId)
        if (errand.isEmpty) return
        val completeNotiEvent = completeNotiEventRepository.findByErrand(errand.get()) ?: return
        if (completeNotiEvent.handledAt == null) {
            completeNotiEvent.handledAt = LocalDateTime.now()
        }
    }
}