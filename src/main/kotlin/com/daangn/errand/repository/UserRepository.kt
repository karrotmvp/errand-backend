package com.daangn.errand.repository

import com.daangn.errand.domain.user.User
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.exception.ErrandException
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long>, UserQueryRepository {
    fun findByDaangnId(daangnId: String): User?

    fun findBy(id: Long): User {
        return findById(id).orElseThrow { ErrandException(ErrandError.ENTITY_NOT_FOUND) }
    }
}
