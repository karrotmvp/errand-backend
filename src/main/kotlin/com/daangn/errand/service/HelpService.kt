package com.daangn.errand.service

import com.daangn.errand.domain.help.Help
import com.daangn.errand.domain.help.HelpConverter
import com.daangn.errand.domain.help.HelpVo
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.HelpRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.help.PostHelpReqDto
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class HelpService(
    val userRepository: UserRepository,
    val helpRepository: HelpRepository,
    val errandRepository: ErrandRepository,
    val helpConverter: HelpConverter
) {
    fun createHelp(postHelpReqDto: PostHelpReqDto): HelpVo {
        val user = userRepository.findById(postHelpReqDto.helperId)
            .orElseThrow { throw ErrandException(ErrandError.ENTITY_NOT_FOUND) }
        val errand = errandRepository.findById(postHelpReqDto.errandId)
            .orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        val help = helpRepository.save(
            Help(
                errand,
                user,
                postHelpReqDto.appeal,
                postHelpReqDto.phoneNumber,
                postHelpReqDto.regionId
            )
        )
        return helpConverter.toHelpVo(help)
    }
}