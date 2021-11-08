package com.daangn.errand.domain.errand

import com.daangn.errand.domain.help.Help
import com.daangn.errand.domain.user.User

interface ErrandHasStatus {
    var status: String?
    fun setStatus(errand: Errand, user: User, help: Help?) {
        val didUserApplyButWasNotChosen = help != null && errand.chosenHelper != user
        val isAnyHelperMatched: Boolean = errand.chosenHelper != null

        val statusEnum = if (errand.complete) {
            Status.COMPLETE
        } else if (didUserApplyButWasNotChosen && isAnyHelperMatched) {
            Status.FAIL
        } else if (isAnyHelperMatched){
            Status.PROCEED
        } else {
            Status.WAIT
        }
        this.status = statusEnum.name
    }
}