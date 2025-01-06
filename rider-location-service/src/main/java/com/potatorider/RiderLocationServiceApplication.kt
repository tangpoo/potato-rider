package com.potatorider

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class RiderLocationServiceApplication

fun main(args: Array<String>) {
    runApplication<RiderLocationServiceApplication>(*args)
}