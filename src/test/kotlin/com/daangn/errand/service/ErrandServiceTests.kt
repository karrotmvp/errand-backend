package com.daangn.errand.service

import com.daangn.errand.domain.category.Category
import com.daangn.errand.domain.errand.Errand
import com.daangn.errand.domain.errand.ErrandConverter
import com.daangn.errand.domain.image.Image
import com.daangn.errand.domain.user.User
import com.daangn.errand.domain.user.UserConverter
import com.daangn.errand.repository.*
import com.daangn.errand.rest.dto.daangn.RegionConverter
import com.daangn.errand.rest.dto.errand.PostErrandReqDto
import com.daangn.errand.support.error.ErrandError
import com.daangn.errand.support.event.publisher.DaangnChatEventPublisher
import com.daangn.errand.support.event.publisher.MixpanelEventPublisher
import com.daangn.errand.support.exception.ErrandException
import com.daangn.errand.util.DaangnUtil
import com.daangn.errand.util.S3AsyncUploader
import com.github.javafaker.Faker
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates
import kotlin.random.Random

@ExtendWith(MockKExtension::class)
class ErrandServiceTests {
    @SpyK
    @InjectMockKs // 가짜 빈 객체 주입
    private lateinit var errandService: ErrandService

    @MockK // 목 객체 만들어서 너(MockKExtension)가 넣어달라는 의미
    private lateinit var mockUserRepository: UserRepository

    @MockK
    private lateinit var mockErrandRepository: ErrandRepository

    @MockK
    private lateinit var mockCategoryRepository: CategoryRepository

    @MockK
    private lateinit var helpRepository: HelpRepository

    @MockK
    private lateinit var mockImageRepository: ImageRepository

    @MockK
    private lateinit var errandConverter: ErrandConverter

    @MockK
    private lateinit var regionConverter: RegionConverter

    @MockK
    private lateinit var userConverter: UserConverter

    @MockK
    private lateinit var mockS3AsyncUploader: S3AsyncUploader

    @MockK
    private lateinit var daangnUtil: DaangnUtil

    @MockK
    private lateinit var daangnChatEventPublisher: DaangnChatEventPublisher

    @MockK
    private lateinit var mixpanelEventPublisher: MixpanelEventPublisher

    @MockK
    private lateinit var eventPublisher: ApplicationEventPublisher

    private var randomUserId by Delegates.notNull<Long>()
    private var randomCategoryId by Delegates.notNull<Long>()

    private val faker = Faker()

    @BeforeEach
    fun setUp() {
        randomUserId = Random.nextLong(10000, 1000000) // 유저아이디 최대한 테스트마다 랜덤하게 해주고 싶다면
        randomCategoryId = Random.nextLong(10000, 1000000) // 카테고리도 아이디를 랜덤하게 주쟈.

        mockkObject(HelloSpeaker)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(HelloSpeaker)
    }

    @Nested
    inner class MethodCreateErrand {
        @Test
        fun `should throw ErrandException if userRepository returns empty`() { // 유저 리포지토리가 empty를 반환하면 예외를 던진다
            // given
            every {
                mockUserRepository.findById(randomUserId) // findById(randomUserId)를 호출하면 empty를 반환한다.
            } returns Optional.empty()

            val mockErrReqDto = mockk<PostErrandReqDto>() // mock DTO 만들기

            // when
            val thrown = assertThrows<ErrandException> { // 예외 받기
                errandService.createErrand(randomUserId, mockErrReqDto)
            }

            // then
            assertEquals(ErrandError.ENTITY_NOT_FOUND, thrown.error)
        }

        @Test
        fun `should throw ErrandException if categoryRepository returns empty`() {
            // given
            val mockUser = mockk<User>()
            every {
                mockUserRepository.findById(randomUserId)
            } returns Optional.of(mockUser)

            every {
                mockCategoryRepository.findById(randomCategoryId)
            } returns Optional.empty()

            val mockErrReqDto = mockk<PostErrandReqDto> {
                every { categoryId } returns randomCategoryId
            }

            // when
            val thrown = assertThrows<ErrandException> {
                errandService.createErrand(randomUserId, mockErrReqDto)
            }

            // then
            assertEquals(ErrandError.BAD_REQUEST, thrown.error)
        }

        @Test
        fun `should upload images successfully and return errand under perfect circumstance`() {
            // given
            val mockUser = mockk<User>(relaxed = true)
            every {
                mockUserRepository.findById(randomUserId)
            } returns Optional.of(mockUser)

            val mockCategory = mockk<Category>()
            every {
                mockCategoryRepository.findById(randomCategoryId)
            } returns Optional.of(mockCategory)

            val mockImageFiles = (0 until 5).map {
                mockk<MultipartFile>()
            }

            val mockErrReqDto = mockk<PostErrandReqDto> {
                every { categoryId } returns randomCategoryId
                every { images } returns mockImageFiles
                every { detail } returns faker.lorem().sentence()
                every { phoneNumber } returns faker.phoneNumber().cellPhone()
                every { detailAddress } returns faker.address().fullAddress()
                every { regionId } returns Random.nextInt(100, 1000).toString()
                every { reward } returns Random.nextInt(1000, 10000).toString()
            }

            val mockSavedErrand = mockk<Errand>(relaxed = true)
            every {
                mockErrandRepository.save(any())
            } returns mockSavedErrand

            every {
                mockS3AsyncUploader.generateKey(any())
            } returns "key"

            every {
                mockS3AsyncUploader.generateObjectUrl(any())
            } returns "url"

            val mockFutures = (0 until 5).map {
                CompletableFuture.supplyAsync({
                    PutObjectResponse.builder().build()
                }, CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS))
            }

            every {
                mockS3AsyncUploader.putObject(any(), any())
            } returnsMany mockFutures

            // 내가 실수 한 것
//            every {
//                mockS3AsyncUploader.putObject(any(), any())
//            } returns CompletableFuture.supplyAsync({
//                PutObjectResponse.builder().build()
//            }) // 이렇게 하면 mockS3AsyncUploader.putObject()가 반환하는 CompletableFuture 는 모두 동일한 인스턴스가 된다.

                every {
                    mockImageRepository.save(any())
                } returns mockk()

                every {
                    errandService.sayHello()
                } answers {
                    println("hello rosie")
                }

                every {
                    HelloSpeaker.sayHello()
                } answers {
                    println("hello rosie")
                }

                // then
                assertDoesNotThrow {
                    errandService.createErrand(randomUserId, mockErrReqDto)
                }

                verify(exactly = 5) {
                    mockS3AsyncUploader.putObject(any(), any())
                }

                verify(exactly = 5) {
                    mockImageRepository.save(any())
                }

                assertTrue(mockFutures.all { it.isDone })
            }
        }
    }
}