package com.example.mdcwrapper

import com.example.mdcwrapper.java.GoatNotFoundException
import com.example.mdcwrapper.kotlin.NoteNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoteNotFoundException::class)
    fun handleNoteNotFoundException(ex: NoteNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(ex.message ?: "Note not found")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(GoatNotFoundException::class)
    fun handleGoatNotFoundException(ex: GoatNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(ex.message ?: "Goat not found")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }
}

data class ErrorResponse(val message: String)

