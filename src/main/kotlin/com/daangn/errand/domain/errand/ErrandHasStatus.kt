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
    fun setStatus(errand: MainErrandQueryResult, userId: Long) {
        val statusEnum = if (errand.complete) {
            Status.COMPLETE
        } else if (errand.viewerHelpId != null && errand.chosenHelperId != null && errand.chosenHelperId != userId) {
            Status.FAIL
        } else if (errand.chosenHelperId != null) {
            Status.PROCEED
        } else {
            Status.WAIT
        }
        this.status = statusEnum.name
    }
}