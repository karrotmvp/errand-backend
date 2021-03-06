package com.daangn.errand.service

import com.daangn.errand.domain.help.Help
import com.daangn.errand.domain.help.HelpConverter
import com.daangn.errand.domain.help.HelpVo
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.HelpRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.help.GetHelpDetailResDto
import com.daangn.errand.rest.dto.help.PostHelpReqDto
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.publisher.DaangnChatEventPublisher
import com.daangn.errand.support.event.publisher.MixpanelEventPublisher
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.JwtPayload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HelpService(
    private val userRepository: UserRepository,
    private val helpRepository: HelpRepository,
    private val errandRepository: ErrandRepository,
    private val helpConverter: HelpConverter,
    private val daangnChatEventPublisher: DaangnChatEventPublisher,
    private val mixpanelEventPublisher: MixpanelEventPublisher,
    private val daangnUtil: DaangnUtil,
    private val userConverter: UserConverter
) {
    @Transactional
    fun readHelpDetail(payload: JwtPayload, helpId: Long): GetHelpDetailResDto {
        val userId = payload.userId
        val user = userRepository.findById(userId).orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val help = helpRepository.findById(helpId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }

        val thisHelper = help.helper
        val errandCustomer = help.errand.customer
        val errandChosenHelper = help.errand.chosenHelper

        if (user != errandCustomer && user != thisHelper) throw ErrandException(ErrandError.NOT_PERMITTED)

        val helperProfileVo = daangnUtil.setUserDaangnProfile(userConverter.toUserProfileVo(thisHelper), help.regionId)

        val helperPhoneNumberOrNull =
            // 1. ????????? ????????? ????????? ????????? 2. ???????????? ????????? ???????????? ??? ???????????? ????????? ?????? ??????????????? ??????
            if (user == thisHelper || (user == errandCustomer && errandChosenHelper == thisHelper)) help.phoneNumber else null


        return GetHelpDetailResDto(
            help.errand.id!!,
            user == errandCustomer,
            errandChosenHelper == thisHelper,
            helperProfileVo,
            help.appeal,
            helperPhoneNumberOrNull,
            help.createdAt
        )
    }

    @Transactional
    fun createHelp(userId: Long, postHelpReqDto: PostHelpReqDto): HelpVo {
        val user = userRepository.findById(userId)
            .orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val errand = errandRepository.findById(postHelpReqDto.errandId)
            .orElseThrow { ErrandException(ErrandError.BAD_REQUEST, "?????? id??? ???????????? ?????????.") }
        if (user == errand.customer) throw ErrandException(ErrandError.BAD_REQUEST, "????????? ???????????? ????????? ??? ?????????.")

        val helpCnt = helpRepository.countByErrand(errand)
        if (helpCnt >= 5) throw ErrandException(ErrandError.BAD_REQUEST, "????????? ??????????????? 5???????????? ????????? ??? ?????????.")
        if (helpRepository.existsByErrandAndHelper(errand, user)) throw ErrandException(
            ErrandError.BAD_REQUEST,
            "????????? ??????????????? ????????? ????????? ??? ?????????."
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

        daangnChatEventPublisher.publishHelpRegisteredEvent(errand.id!!)
        mixpanelEventPublisher.publishHelpRegisteredEvent(help.id!!)

        return helpConverter.toHelpVo(help)
    }

    fun countHelp(errandId: Long): Long {
        val errand = errandRepository.findById(errandId).orElseThrow { ErrandException(ErrandError.BAD_REQUEST) }
        return helpRepository.countByErrand(errand)
    }

    @Transactional
    fun destroyHelp(userId: Long, helpId: Long) {
        val help = helpRepository.findById(helpId).orElseThrow { ErrandException(ErrandError.BAD_REQUEST) }
        val user = userRepository.findById(userId).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        if (help.helper != user) throw ErrandException(ErrandError.NOT_PERMITTED)
        if (help.errand.chosenHelper == user) throw ErrandException(
            ErrandError.BAD_REQUEST,
            "????????? ????????? ???????????? ????????? ????????? ??? ????????????."
        )
        helpRepository.delete(help)
    }
}