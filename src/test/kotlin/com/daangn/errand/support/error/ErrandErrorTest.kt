package com.daangn.errand.support.error

import com.daangn.errand.support.exception.ErrandException
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class ErrandErrorTest {

    private fun throwNullPointerException() {
        try {
            val nullValue: String = null!!
            println(nullValue)
        } catch (e: Exception) {
            throw ErrandException(ErrandError.UNEXPECTED_ERROR, e.toString())
        }
    }

    @Test
    fun setDescExceptionMsg() {
        val thrown: ErrandException = assertThrows(ErrandException::class.java) {
            throwNullPointerException()
        }

        Assertions.assertThat(thrown.message).isEqualTo("서버 에러입니다.(java.lang.NullPointerException)")
    }
}