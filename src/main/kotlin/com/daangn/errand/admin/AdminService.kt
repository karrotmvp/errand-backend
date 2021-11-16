package com.daangn.errand.admin

import com.daangn.errand.domain.errand.ErrandAdmin
import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.domain.help.HelpAdmin
import com.daangn.errand.domain.help.HelpConverter
import com.daangn.errand.domain.user.UserAdmin
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.HelpRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.daangn.DaangnUserInfo
import com.daangn.errand.rest.dto.daangn.GetUserInfoByUserIdRes
import com.daangn.errand.rest.dto.daangn.RegionConverter
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminService(
    val errandRepository: ErrandRepository,
    val userRepository: UserRepository,
    val errandConverter: ErrandConverter,
    val daangnUtil: DaangnUtil,
    val regionConverter: RegionConverter,
    val helpRepository: HelpRepository,
    val helpConverter: HelpConverter,
    val userConverter: UserConverter,
) {
    fun makeUnexposed(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        errand.unexposed = true
    }

    fun makeExposed(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        errand.unexposed = false
    }

    fun getErrandAdminList(): List<ErrandAdmin> {
        return errandRepository.findAll().asSequence().map { errand ->
            val errandAdmin = errandConverter.toErrandAdmin(errand)
            val region = daangnUtil.getRegionInfoByRegionId(errand.regionId).region
            errandAdmin.region = regionConverter.toRegionVo(region)
            errandAdmin.helpCount = helpRepository.countByErrand(errand)
            errandAdmin
        }.toList()
    }

    fun getErrandAdminDetail(errandId: Long): ErrandAdmin {
        val errand = errandRepository.findById(errandId)
            .orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND, "아이디로 엔티티 조회 실패") }
        val errandAdmin = errandConverter.toErrandAdmin(errand)
        val region = daangnUtil.getRegionInfoByRegionId(errand.regionId).region
        errandAdmin.region = regionConverter.toRegionVo(region)
        errandAdmin.helpCount = helpRepository.countByErrand(errand)
        return errandAdmin
    }

    fun getHelpList(errandId: Long): List<HelpAdmin> {
        val errand = errandRepository.findById(errandId).orElseThrow { ErrandException(ErrandError.BAD_REQUEST) }
        val helps = helpRepository.findByErrand(errand)
        return helps.asSequence().map { help ->
            val helpAdmin = helpConverter.toHelpAdmin(help)
            val region = daangnUtil.getRegionInfoByRegionId(help.regionId).region
            helpAdmin.region = regionConverter.toRegionVo(region)
            val userProfile = userConverter.toUserProfileVo(help.helper)
            helpAdmin.helper = daangnUtil.setUserDaangnProfile(userProfile)
            helpAdmin
        }.toList()
    }

    fun getUserDaangnInfo(userId: Long): UserAdmin {
        val user = userRepository.findById(userId).get()
        val daangnProfile = daangnUtil.getUserInfo(user.daangnId).data.user
        val errandCount = errandRepository.countByCustomer(user)
        val helpCount = helpRepository.countByHelper(user)
        return UserAdmin(
            user.id!!,
            user.daangnId,
            daangnProfile.nickname,
            daangnProfile.profileImageUrl,
            daangnProfile.mannerTemperature,
            errandCount,
            helpCount.toInt()
        )
    }
}