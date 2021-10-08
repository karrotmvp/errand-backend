package com.daangn.errand.domain

import javax.persistence.*

@Entity
class Category {
    @Id
    var id: Long? = null

    @Column
    val name: String = ""
}