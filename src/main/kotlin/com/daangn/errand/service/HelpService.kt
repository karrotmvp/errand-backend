package com.daangn.errand.service

import com.daangn.errand.domain.help.Help
import com.daangn.errand.domain.help.HelpConverter
import com.daangn.errand.domain.help.HelpVo
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.HelpRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.help.PostHelpReqDto
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.HelpRegisteredChatEvent
import com.daangn.errand.support.event.publisher.MixpanelEventPublisher
import com.daangn.errand.support.exception.ErrandException
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class HelpService(
    private val userRepository: UserRepository,
    private val helpRepository: HelpRepository,
    private val errandRepository: ErrandRepository,
    private val helpConverter: HelpConverter,
    private val eventPublisher: ApplicationEventPublisher,
    private val mixpanelEventPublisher: MixpanelEventPublisher,
    @Value("\${host.url}") val baseUrl: String
) {
    fun createHelp(userId: Long, postHelpReqDto: PostHelpReqDto): HelpVo {
        val user = userRepository.findById(userId)
            .orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val errand = errandRepository.findById(postHelpReqDto.errandId)
            .orElseThrow { ErrandException(ErrandError.BAD_REQUEST, "해당 id의 심부름이 없어요.") }
        if (user == errand.customer) throw ErrandException(ErrandError.BAD_REQUEST, "본인의 심부름에 지원할 수 없어요.")

        val helpCnt = helpRepository.countByErrand(errand)
        if (helpCnt >= 5) throw ErrandException(ErrandError.BAD_REQUEST, "하나의 심부름에는 5명까지만 지원할 수 있어요.")
        if (helpRepository.findByErrandAndHelper(errand, user) != null) throw ErrandException(
            ErrandError.BAD_REQUEST,
            "하나의 심부름에는 한번만 지원할 수 있어요."
        )

        val help = helpRepository.save(
            Help(
                errand,
                user,
                postHelpReqDto.appeal,
                postHelpReqDto.phoneNumber,
                postHelpReqDto.regionId
            )
        )
        eventPublisher.publishEvent(
            HelpRegisteredChatEvent(
                listOf(errand.customer.daangnId),
                "$baseUrl/appliers/${errand.id}"
            )
        )
        mixpanelEventPublisher.publishHelpRegisteredEvent(help)
        return helpConverter.toHelpVo(help)
    }

    fun countHelp(errandId: Long): Long {
        val errand = errandRepository.findById(errandId).orElseThrow { ErrandException(ErrandError.BAD_REQUEST) }
        return helpRepository.countByErrand(errand)
    }

    fun destroyHelp(userId: Long, helpId: Long) {
        val help = helpRepository.findById(helpId).orElseThrow { ErrandException(ErrandError.BAD_REQUEST) }
        val user = userRepository.findById(userId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (help.helper != user) throw ErrandException(ErrandError.NOT_PERMITTED)
        helpRepository.delete(help)
    }
}