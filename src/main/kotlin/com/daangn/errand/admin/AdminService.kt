package com.daangn.errand.admin

import com.daangn.errand.admin.dto.AdminLoginReqDto
import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.ErrandAdmin
import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.domain.help.HelpAdmin
import com.daangn.errand.domain.help.HelpConverter
import com.daangn.errand.domain.user.UserAdmin
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.repository.HelpRepository
import com.daangn.errand.repository.UserRepository
import com.daangn.errand.rest.dto.daangn.RegionConverter
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpSession

@Service
class AdminService(
    private val errandRepository: ErrandRepository,
    private val userRepository: UserRepository,
    private val errandConverter: ErrandConverter,
    private val daangnUtil: DaangnUtil,
    private val regionConverter: RegionConverter,
    private val helpRepository: HelpRepository,
    private val helpConverter: HelpConverter,
    private val userConverter: UserConverter,
    @Value("\${admin.username}") private val username: String,
    @Value("\${admin.password}") private val password: String,
) {
    fun login(adminLoginReqDto: AdminLoginReqDto): Boolean {
        val (username, password) = adminLoginReqDto
        return username == this.username && password == this.password;
    }

    fun setSessionId(session: HttpSession) {
        session.setAttribute("isAdmin", true)
    }

    @Transactional
    fun makeUnexposed(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        errand.unexposed = true
    }

    @Transactional
    fun makeExposed(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        errand.unexposed = false
    }

    @Transactional(readOnly = true)
    fun getErrandAdminList(pageNum: Number?): List<ErrandAdmin> {
        val page = if (pageNum != null) pageNum.toInt() - 1 else 0
        val pageable = PageRequest.of(page, 20, Sort.by("id").descending())

        val errands: Page<Errand> = errandRepository.findAll(pageable)

        return errands.asSequence().map { errand ->
            val errandAdmin = errandConverter.toErrandAdmin(errand)
            val region = daangnUtil.getRegionInfoByRegionId(errand.regionId).region
            errandAdmin.region = regionConverter.toRegionVo(region)
            errandAdmin.helpCount = helpRepository.countByErrand(errand)
            errandAdmin
        }.toList()
    }

    @Transactional(readOnly = true)
    fun getErrandAdminDetail(errandId: Long): ErrandAdmin {
        val errand = errandRepository.findById(errandId)
            .orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND, "아이디로 엔티티 조회 실패") }
        val errandAdmin = errandConverter.toErrandAdmin(errand)
        val region = daangnUtil.getRegionInfoByRegionId(errand.regionId).region
        errandAdmin.region = regionConverter.toRegionVo(region)
        errandAdmin.helpCount = helpRepository.countByErrand(errand)
        return errandAdmin
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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