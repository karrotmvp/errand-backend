package com.daangn.errand.admin

import com.daangn.errand.repository.ErrandRepository
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminService(
    val errandRepository: ErrandRepository
) {
    fun makeUnexposed(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        errand.unexposed = true
    }

    fun makeExposed(errandId: Long) {
        val errand = errandRepository.findById(errandId).orElseThrow { throw ErrandException(ErrandError.BAD_REQUEST) }
        errand.unexposed = false
    }
}