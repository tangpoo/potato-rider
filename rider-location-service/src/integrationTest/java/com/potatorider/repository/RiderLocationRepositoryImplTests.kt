package com.potatorider.repository

import com.potatorider.config.RedisConfiguration
import com.potatorider.domain.RiderLocation
import com.potatorider.domain.createRiderLocation
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.context.annotation.Import
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.test.StepVerifier

@Testcontainers
@DataRedisTest
@Import(RiderLocationRepositoryImpl::class, RedisConfiguration::class)
class RiderLocationRepositoryImplTests @Autowired constructor(
    private val riderLocationRepositoryImpl: RiderLocationRepositoryImpl,
    private val redisTemplate: ReactiveRedisTemplate<String, RiderLocation>
) {

    @Test
    fun set_if_present() {
        // Arrange
        val riderLocation = createRiderLocation()

        redisTemplate.opsForValue().set(riderLocation.id, riderLocation).block()

        // Act
        val result = riderLocationRepositoryImpl.setIfPresent(riderLocation)

        // Assert
        StepVerifier.create(result).expectNext(true).verifyComplete()
    }

    companion object {
        @Container
        private val redisContainer: GenericContainer<*> =
            GenericContainer<Nothing>(DockerImageName.parse("redis:latest")).withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host") { redisContainer.host }
            registry.add("spring.data.redis.port") { redisContainer.firstMappedPort }
        }
    }
}
