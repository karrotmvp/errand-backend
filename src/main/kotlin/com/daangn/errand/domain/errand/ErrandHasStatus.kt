package com.daangn.errand.domain.errand

import com.daangn.errand.domain.user.User

interface ErrandHasStatus {
    var status: String?
    fun setStatus(errand: Errand, user: User, didUserApply: Boolean) {
        val didUserApplyButWasNotChosen = didUserApply && errand.chosenHelper != user
        val isAnyHelperMatched: Boolean = errand.chosenHelper != null

        val statusEnum = if (errand.complete) {
            Status.COMPLETE
        } else if (didUserApplyButWasNotChosen && isAnyHelperMatched) {
            Status.FAIL
        } else if (isAnyHelperMatched) {
            Status.PROCEED
        } else {
            Status.WAIT
        }
        this.status = statusEnum.name
    }
}