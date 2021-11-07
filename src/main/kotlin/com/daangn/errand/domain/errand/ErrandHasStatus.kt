package com.daangn.errand.domain.errand

interface ErrandHasStatus {
    var status: String?
    fun setStatus(errand: Errand, didUserApplyButWasNotChosen: Boolean) {
        val statusEnum = if (errand.complete) {
            Status.COMPLETE
        } else if (didUserApplyButWasNotChosen) {
            Status.FAIL
        } else if (errand.chosenHelper != null){
            Status.PROCEED
        } else {
            Status.WAIT
        }
        this.status = statusEnum.name
    }
}