package com.daangn.errand.util

import com.google.common.net.HttpHeaders
import org.springframework.context.annotation.Profile
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

class CookieUtil: ICookieUtil {
    override fun setCookie(token: String, res: HttpServletResponse) {
        val tokenCookie = Cookie("token", token)
        tokenCookie.maxAge = 60 * 60 * 24
        tokenCookie.isHttpOnly = true
        tokenCookie.domain = ".daangn-errand.com" // set domain to verify request origin is same-site
        res.addCookie(tokenCookie)
    }
}


class DevCookieUtil: ICookieUtil {
    override fun setCookie(token: String, res: HttpServletResponse) {
        val tokenCookie = ResponseCookie.from("token", token)
            .maxAge(60 * 60 * 24)
            .sameSite("None")
            .httpOnly(false)
            .path("/")
            .build()
        res.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString())
    }
}

interface ICookieUtil {
    fun setCookie(token: String, res: HttpServletResponse)
}
