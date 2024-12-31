package com.potatorider.config

import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RabbitMQConfig {
    @Bean
    open fun jackson2JsonMessageConverter(): Jackson2JsonMessageConverter =
        Jackson2JsonMessageConverter()

    @Bean
    open fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate =
        RabbitTemplate(connectionFactory).apply {
            messageConverter = jackson2JsonMessageConverter()
        }
}
