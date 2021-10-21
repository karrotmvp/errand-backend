package com.daangn.errand.support.event.scheduler

import com.daangn.errand.support.event.DaangnChatReqRegisteredEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class EventScheduler(
    val eventPublisher: ApplicationEventPublisher
) {
    private val heap = PriorityQueue<EventWithPublishesAt>()

    fun addElement(event: DaangnChatReqRegisteredEvent, publishesAt: LocalDateTime) {
        heap.add(
            EventWithPublishesAt(
                event,
                publishesAt
            )
        )
    }

    @Scheduled(cron = "0 0/5 * * * *")
    fun eventHandling() {
        val now = LocalDateTime.now()
        while (heap.isNotEmpty() && heap.peek().publishesAfter.isBefore(now)) {
            eventPublisher.publishEvent(heap.poll())
        }
    }
}