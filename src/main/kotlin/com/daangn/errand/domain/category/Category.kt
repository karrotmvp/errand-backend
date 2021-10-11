package com.daangn.errand.domain.category

import javax.persistence.*

@Entity
class Category {
    @Id
    var id: Long? = null

    @Column
    var name: String = ""
}