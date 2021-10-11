package com.daangn.errand.domain

import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.user.User
import javax.persistence.*

@Entity
class HelperHasCategories(
    @ManyToOne val errand: Errand,
    @ManyToOne val helper: User
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}