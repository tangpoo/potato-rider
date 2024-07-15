package com.potatorider.repository;

import com.potatorider.config.RedisConfiguration;
import com.potatorider.domain.RiderLocation;
import com.potatorider.domain.RiderLocationSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

@Testcontainers
@DataRedisTest
@Import({RiderLocationRepositoryImpl.class, RedisConfiguration.class})
public class RiderLocationRepositoryImplTests {

    @Container
    private static final GenericContainer<?> redisContainer =
        new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @Autowired
    private RiderLocationRepositoryImpl riderLocationRepositoryImpl;

    @Autowired
    private ReactiveRedisTemplate<String, RiderLocation> redisTemplate;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    @Test
    void set_if_present() {
        // Arrange
        final RiderLocation riderLocation = RiderLocationSteps.createRiderLocation();

        redisTemplate.opsForValue().set(riderLocation.getId(), riderLocation).block();

        // Act
        var result = riderLocationRepositoryImpl.setIfPresent(riderLocation);

        // Assert
        StepVerifier.create(result).expectNext(true).verifyComplete();
    }
}