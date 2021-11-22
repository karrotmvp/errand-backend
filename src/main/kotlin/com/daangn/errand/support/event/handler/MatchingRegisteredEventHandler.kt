package com.daangn.errand.support.event.handler

import com.daangn.errand.domain.event.CompleteNotiEvent
import com.daangn.errand.repository.CompleteNotiEventRepository
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.MakeCompleteNotiEntityEvent
import com.daangn.errand.support.exception.ErrandException
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class MatchingRegisteredEventHandler(
    private val completeNotiEventRepository: CompleteNotiEventRepository,
    private val errandRepository: ErrandRepository,
) {
    @EventListener
    @Transactional
    fun insertCompleteNotiEvent(event: MakeCompleteNotiEntityEvent) {
        val errand =
            errandRepository.findById(event.errandId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        completeNotiEventRepository.save(CompleteNotiEvent(
            errand,
            LocalDateTime.now().plusHours(24)
        ))
    }
}