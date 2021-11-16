package com.daangn.errand.admin

import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.domain.help.HelpAdmin
import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/admin")
class  AdminController(
    private val adminService: AdminService,
) {
    @GetMapping("")
    fun getErrandList(model: Model): String {
        val errands = adminService.getErrandAdminList()
        model.addAttribute("errandList", errands)
        return "errand-list"
    }

    @GetMapping("/errand/{id}")
    fun getErrandDetail(model: Model, @PathVariable(value = "id") id: Long): String {
        val errandAdmin = adminService.getErrandAdminDetail(id)
        val userId = errandAdmin.customer.id ?: throw ErrandException(ErrandError.BAD_REQUEST, "customer id 없음")
        val userInfo = adminService.getUserDaangnInfo(userId)
        model.addAttribute("userInfo", userInfo)
        model.addAttribute("errand", errandAdmin)
        return "errand-detail"
    }

    @GetMapping("/errand/{id}/unexposed")
    @ResponseBody
    fun makeErrandUnexposed(
        @PathVariable(value = "id") id: Long,
    ): ResponseEntity<String> {
        adminService.makeUnexposed(id)
        return ResponseEntity<String>(true.toString(), HttpStatus.OK)
    }

    @GetMapping("/errand/{id}/exposed")
    @ResponseBody
    fun makeErrandExposed(
        @PathVariable(value = "id") id: Long,
    ): ResponseEntity<Boolean> {
        adminService.makeExposed(id)
        return ResponseEntity<Boolean>(false, HttpStatus.OK)
    }

    @GetMapping("/errand/{id}/help-list")
    fun getErrandHelpList(
        @PathVariable(value = "id") id: Long,
        model: Model,
    ): String {
        val helps: List<HelpAdmin> = adminService.getHelpList(id)
        model.addAttribute("helpList", helps)
        return "help-list"
    }

    @GetMapping("/user/{id}")
    fun getUserInfo(
        @PathVariable(value = "id") userId: Long,
        model: Model,
    ): String {
        val userInfo = adminService.getUserDaangnInfo(userId)
        model.addAttribute("userInfo", userInfo)
        return "user-detail"
    }
}