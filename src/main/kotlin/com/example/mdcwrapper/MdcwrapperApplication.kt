package com.example.mdcwrapper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@SpringBootApplication
@EnableJdbcRepositories(basePackages = ["com.example.mdcwrapper.kotlin", "com.example.mdcwrapper.java"])
class MdcwrapperApplication

fun main(args: Array<String>) {
    runApplication<MdcwrapperApplication>(*args)
}
