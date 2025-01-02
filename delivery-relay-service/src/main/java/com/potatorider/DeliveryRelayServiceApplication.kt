package com.potatorider

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
open class DeliveryRelayServiceApplication

fun main(args: Array<String>) {
    runApplication<DeliveryRelayServiceApplication>(*args)
}

