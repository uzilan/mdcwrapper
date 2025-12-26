package com.example.mdcwrapper.kotlin

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val noteService: NoteService,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @PostMapping
    fun createNote(
        @RequestBody note: Note,
    ): ResponseEntity<Note> {
        MdcWrapper.info(logger, "serving POST /api/notes").use {
            // val createdNote = noteService.createNoteTheOldWay(note)
            val createdNote = noteService.createNote(note)
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNote)
        }
    }

    @GetMapping
    fun getAllNotes(): ResponseEntity<List<Note>> {
        MdcWrapper.info(logger, "serving GET /api/notes").use {
            val notes = noteService.getAllNotes()
            return ResponseEntity.ok(notes)
        }
    }

    @GetMapping("/{id}")
    fun getNoteById(
        @PathVariable id: Long,
    ): ResponseEntity<Note> {
        MdcWrapper.info(logger, "serving GET /api/notes/{id}", "id" to id.toString()).use {
            val note = noteService.getNoteById(id)
            return ResponseEntity.ok(note)
        }
    }

    @PutMapping("/{id}")
    fun updateNote(
        @PathVariable id: Long,
        @RequestBody note: Note,
    ): ResponseEntity<Note> {
        MdcWrapper.info(logger, "serving PUT /api/notes/{id}", "id" to id.toString()).use {
            val updatedNote = noteService.updateNote(id, note)
            return ResponseEntity.ok(updatedNote)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteNote(
        @PathVariable id: Long,
    ): ResponseEntity<Void> {
        MdcWrapper.info(logger, "serving DELETE /api/notes/{id}", "id" to id.toString()).use {
            noteService.deleteNote(id)
            return ResponseEntity.noContent().build()
        }
    }
}
