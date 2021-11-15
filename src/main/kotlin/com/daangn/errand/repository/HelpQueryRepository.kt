package com.daangn.errand.repository

import com.daangn.errand.domain.help.Help
import com.daangn.errand.domain.user.User

interface HelpQueryRepository {
    fun findByHelperTopSize(helper: User, size: Long): MutableList<Help>
    fun findByHelper(helper: User, lastHelp: Help, size: Long): MutableList<Help>
    fun countByErrandId(errandId: Long): Long
}