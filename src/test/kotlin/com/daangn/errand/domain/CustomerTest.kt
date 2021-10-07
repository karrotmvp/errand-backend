package com.daangn.errand.domain

import com.daangn.errand.repository.CustomerRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CustomerTest @Autowired constructor(
    private val customerRepository: CustomerRepository
        ) {
    @Test
    fun saveCustomer() {
        val customer = Customer(
            nickname = "당근이",
            daangnId = "d5cb4d894fa942c48e60fea0a9228581",
            region_id = "6530459d189b",
            profileImageUrl = null
        )
        customerRepository.save(customer)

        val readCustomer: Customer = customerRepository.findById(customer.id!!).get()
        Assertions.assertThat(readCustomer).isEqualTo(customer)
    }
}

