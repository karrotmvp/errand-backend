package com.daangn.errand.rest.resolver

import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.JwtPayload
import com.daangn.errand.util.JwtUtil
import com.fasterxml.jackson.core.JsonProcessingException
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.util.WebUtils
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

@Component
class TokenPayloadArgumentResolver(
    val jwtUtil: JwtUtil
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(TokenPayload::class.java)
    }

    @Throws(JsonProcessingException::class)
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): JwtPayload? {
        val servletRequest: HttpServletRequest = webRequest.getNativeRequest(HttpServletRequest::class.java)!!
        val cookieValue: Cookie =
            WebUtils.getCookie(servletRequest, "token") ?: throw ErrandException(ErrandError.BAD_REQUEST)
        val jwt = cookieValue.value
        return jwtUtil.decodeToken(jwt)
    }
}