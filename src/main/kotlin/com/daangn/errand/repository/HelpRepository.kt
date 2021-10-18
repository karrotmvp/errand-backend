package com.daangn.errand.repository

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.help.Help
import com.daangn.errand.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface HelpRepository: JpaRepository<Help, Long> {
    fun findByErrandAndHelper(errand: Errand, helper: User): Help?
}