package com.daangn.errand.domain.image

import com.daangn.errand.domain.errand.Errand
import javax.persistence.*

@Entity
class Image(
    url: String,
    errand: Errand
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column
    var url: String = url

    @ManyToOne
    @JoinColumn(name = "errand_id")
    var errand: Errand = errand
}