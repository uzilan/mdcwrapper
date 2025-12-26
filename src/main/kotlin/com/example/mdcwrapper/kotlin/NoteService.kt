package com.example.mdcwrapper.kotlin

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val noteRepository: NoteRepository,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun createNoteTheOldWay(note: Note): Note {
        try {
            MDC.put("title", note.title)
            logger.debug("Started creating a new note")

            return noteRepository
                .save(note)
                .also {
                    MDC.put("id", it.id.toString())
                    logger.debug("Finished creating a new note")
                }
        } finally {
            MDC.clear()
        }
    }

    fun createNote(note: Note): Note {
        MdcWrapper.debug(logger, "creating a new note", "title" to note.title).use { mdc ->
            return noteRepository
                .save(note)
                .also { mdc.put("id", it.id.toString()) }
        }
    }

    fun getAllNotes(): List<Note> {
        MdcWrapper.debug(logger, "fetching all notes").use { mdc ->
            return noteRepository
                .findAll()
                .toList()
                .also { mdc.put("notes", it.size.toString()) }
        }
    }

    fun getNoteById(id: Long): Note {
        MdcWrapper.debug(logger, "getting a note").use {
            return noteRepository
                .findById(id)
                .orElseThrow { NoteNotFoundException() }
        }
    }

    fun updateNote(
        id: Long,
        note: Note,
    ): Note {
        MdcWrapper.debug(logger, "updating a note").use {
            return noteRepository
                .findById(id)
                .map { noteRepository.save(note.copy(id = id)) }
                .orElseThrow { NoteNotFoundException() }
        }
    }

    fun deleteNote(id: Long) {
        MdcWrapper.debug(logger, "deleting a note").use {
            noteRepository
                .findById(id)
                .map { noteRepository.deleteById(id) }
                .orElseThrow { NoteNotFoundException() }
        }
    }
}

class NoteNotFoundException : RuntimeException("Note not found") {
    init {
        LoggerFactory.getLogger(this.javaClass).error(message)
    }
}
