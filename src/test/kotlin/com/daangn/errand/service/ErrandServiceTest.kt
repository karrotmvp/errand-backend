package com.daangn.errand.service

import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.repository.*
import com.daangn.errand.rest.dto.daangn.RegionConverter
import com.daangn.errand.support.event.publisher.DaangnChatEventPublisher
import com.daangn.errand.support.event.publisher.MixpanelEventPublisher
import com.daangn.errand.util.DaangnUtil
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import kotlin.properties.Delegates
import kotlin.random.Random

@ExtendWith(MockKExtension::class)
class ErrandServiceTest{
    @SpyK
    @InjectMockKs
    private lateinit var errandService: ErrandService

    @MockK
    private lateinit var mockErrandRepository: ErrandRepository

    @MockK // 목 객체 만들어서 너(MockKExtension)가 넣어달라는 의미
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var categoryRepository: CategoryRepository

    @MockK
    private lateinit var helpRepository: HelpRepository

    @MockK
    private lateinit var imageRepository: ImageRepository

    @MockK
    private lateinit var errandConverter: ErrandConverter

    @MockK
    private lateinit var regionConverter: RegionConverter

    @MockK
    private lateinit var userConverter: UserConverter

    @MockK
    private lateinit var daangnUtil: DaangnUtil

    @MockK
    private lateinit var daangnChatEventPublisher: DaangnChatEventPublisher

    @MockK
    private lateinit var mixpanelEventPublisher: MixpanelEventPublisher

    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    private var randomTotalNumber by Delegates.notNull<Long>()
    private var randomMatchedNumber by Delegates.notNull<Long>()

    @BeforeEach
    fun setUp() {
        randomTotalNumber = Random.nextLong(10000, 1000000)
        randomMatchedNumber = Random.nextLong(500, 1000000)
    }

    @Test
    fun `return matched errand rate`() {
        every { mockErrandRepository.count() } returns randomTotalNumber
        every { mockErrandRepository.countByChosenHelperIsNotNull() } returns randomMatchedNumber

        val calculatedRate = errandService.getMatchedErrandRate()

        val actualRate = randomMatchedNumber.toFloat()/randomTotalNumber * 100

        assertEquals(actualRate, calculatedRate)
    }
}