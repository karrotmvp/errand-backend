package com.daangn.errand.domain.errand

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.daangn.errand.domain.BaseEntity
import com.daangn.errand.domain.category.Category
import com.daangn.errand.domain.image.Image
import com.daangn.errand.domain.user.User
import org.hibernate.annotations.ColumnDefault
import javax.persistence.*

@Entity
class Errand (
    @ManyToOne
    @JoinColumn(name = "customer_id")
    val customer: User,
    @ManyToOne
    @JoinColumn(name = "category_id")
    var category: Category,
    @Column(nullable = false)
    var regionId: String,
    @Column(nullable = false)
    var detailAddress: String,
    @Column(nullable = false)
    var gratuity: String,
    @Column(nullable = false)
    var detail: String
        ): BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    @ColumnDefault("false")
    var isCompleted: Boolean = false

    @ManyToOne
    @JoinColumn(name = "chosen_helper_id")
    var chosenHelper: User? = null

    @OneToMany(mappedBy = "errand")
    var images: MutableList<Image> = ArrayList()

    companion object{
        private val equalsAndHashCodeProperties = arrayOf(Errand::id)
        private val toStringProperties = arrayOf(
            Errand::id,
            Errand::customer,
            Errand::regionId,
            Errand::detail,
            Errand::chosenHelper,
            Errand::isCompleted
        )
    }

    override fun toString(): String = kotlinToString(properties = toStringProperties)
    override fun equals(other: Any?) = kotlinEquals(other = other, properties = equalsAndHashCodeProperties)
    override fun hashCode() = kotlinHashCode(properties = equalsAndHashCodeProperties)

}