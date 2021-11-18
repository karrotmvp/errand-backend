package com.daangn.errand.admin.web

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

@Component
class LoginInterceptor: HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val session: HttpSession = request.session
        val ret = session.getAttribute("isAdmin")
        if (ret != null) return ret as Boolean
        response.sendRedirect("/admin")
        return false // return 값이 false 이면 호출하지 않는다.
    }
}