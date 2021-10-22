package com.daangn.errand.support.event.scheduler

import com.daangn.errand.support.event.DaangnChatReqRegisteredEvent
import java.time.LocalDateTime

data class EventWithPublishesAt(
    val event: DaangnChatReqRegisteredEvent,
    val publishesAfter: LocalDateTime
): Comparable<EventWithPublishesAt> {
    override fun compareTo(other: EventWithPublishesAt): Int {
        return if (this.publishesAfter.isBefore(other.publishesAfter)) -1 else 1
    }
}
