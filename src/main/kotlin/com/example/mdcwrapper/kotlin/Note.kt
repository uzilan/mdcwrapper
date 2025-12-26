package com.example.mdcwrapper.kotlin

import org.springframework.data.annotation.Id

data class Note(
    @Id val id: Long? = null,
    val title: String,
    val content: String,
)
