package com.daangn.errand.support.event.scheduler

import com.daangn.errand.repository.CompleteNotiEventRepository
import com.daangn.errand.support.event.publisher.DaangnChatEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class EventScheduler(
    private val completeNotiEventRepository: CompleteNotiEventRepository,
    private val daangnChatEventPublisher: DaangnChatEventPublisher
) {
    @Scheduled(cron = "0 0/5 * * * *")
    @Transactional
    fun eventHandling() {
        val now = LocalDateTime.now()
        val notifications =
            completeNotiEventRepository.findAllByScheduledAtBeforeAndHandledAtIsNullOrderByScheduledAt(now)
        notifications.forEach { notiEvent ->
            daangnChatEventPublisher.publishMatchingAfterChatEvent(
                notiEvent.errand.chosenHelper!!.daangnId,
                notiEvent.errand.id!!
            )
            notiEvent.handledAt = LocalDateTime.now()
        }
    }
}