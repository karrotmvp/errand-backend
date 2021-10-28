package com.daangn.errand.domain.errand

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.daangn.errand.domain.BaseEntity
import com.daangn.errand.domain.category.Category
import com.daangn.errand.domain.help.Help
import com.daangn.errand.domain.image.Image
import com.daangn.errand.domain.user.User
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@SQLDelete(sql = "UPDATE errand SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
class Errand(
    @ManyToOne
    @JoinColumn(name = "customer_id")
    val customer: User,
    @ManyToOne
    @JoinColumn(name = "category_id")
    var category: Category,
    @Column(nullable = false)
    var title: String,
    @Column(nullable = false)
    var regionId: String,
    @Column(nullable = false)
    var detailAddress: String,
    @Column(nullable = false)
    var reward: String,
    @Column(nullable = false, columnDefinition = "TEXT")
    var detail: String,
    @Column(nullable = false)
    var customerPhoneNumber: String
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    @ColumnDefault("false")
    var complete: Boolean = false

    @ManyToOne
    @JoinColumn(name = "chosen_helper_id")
    var chosenHelper: User? = null

    @OneToMany(mappedBy = "errand")
    var images: MutableList<Image> = ArrayList()

    // help를 전부 가지고 있는게 편하다
    @OneToMany(mappedBy = "errand")
    var helps: MutableList<Help> = ArrayList()

    @Column(nullable = false)
    var deleted: Boolean = false

    companion object {
        private val equalsAndHashCodeProperties = arrayOf(Errand::id)
        private val toStringProperties = arrayOf(
            Errand::id,
            Errand::customer,
            Errand::category,
            Errand::regionId,
            Errand::reward,
            Errand::detail,
            Errand::chosenHelper,
            Errand::complete,
            Errand::createdAt,
            Errand::updatedAt
        )
    }

    override fun toString(): String = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)

}