package com.potatorider

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class DeliveryInfoServiceApplication

fun main(args: Array<String>) {
    runApplication<DeliveryInfoServiceApplication>(*args)
}
