package com.daangn.errand.support.event.scheduler

import com.daangn.errand.support.event.DaangnChatReqRegisteredEvent
import mu.KotlinLogging
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
    private val kotlinLogger = KotlinLogging.logger {}

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
            kotlinLogger.info("errand completed event published")
            eventPublisher.publishEvent(heap.poll().event)
        }
    }
}