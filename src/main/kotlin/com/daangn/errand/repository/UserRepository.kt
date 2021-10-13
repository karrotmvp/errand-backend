package com.daangn.errand.repository

import com.daangn.errand.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, String> {
}