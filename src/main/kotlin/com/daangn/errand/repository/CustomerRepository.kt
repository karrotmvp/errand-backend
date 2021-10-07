package com.daangn.errand.repository

import com.daangn.errand.domain.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository: JpaRepository<Customer, Long> {
}