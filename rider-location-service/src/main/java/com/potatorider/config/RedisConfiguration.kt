package com.potatorider.config

import com.potatorider.domain.RiderLocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
open class RedisConfiguration @Autowired constructor(
    val factory: ReactiveRedisConnectionFactory
) {

    @Bean
    open fun reactiveRedisTemplate(
        factory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, RiderLocation> {
        val serializer =
            Jackson2JsonRedisSerializer(RiderLocation::class.java)

        val builder =
            RedisSerializationContext.newSerializationContext<String, RiderLocation>(
                StringRedisSerializer()
            )

        val context =
            builder.value(serializer).build()
        return ReactiveRedisTemplate(factory, context)
    }
}
