package com.example.kotlin.mdcwrapper

import com.example.mdcwrapper.kotlin.Note
import com.example.mdcwrapper.kotlin.NoteController
import com.example.mdcwrapper.kotlin.NoteNotFoundException
import com.example.mdcwrapper.kotlin.NoteRepository
import com.example.mdcwrapper.kotlin.NoteService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class NoteControllerTest {

    private class MockNoteService : NoteService(MockNoteRepository()) {
        private var idCounter = 0L
        private val notes = mutableMapOf<Long, Note>()

        override fun createNote(note: Note): Note {
            val newNote = note.copy(id = ++idCounter)
            notes[newNote.id!!] = newNote
            return newNote
        }

        override fun getAllNotes(): List<Note> {
            return notes.values.toList()
        }

        override fun getNoteById(id: Long): Note {
            return notes[id] ?: throw NoteNotFoundException()
        }

        override fun updateNote(id: Long, note: Note): Note {
            if (!notes.containsKey(id)) {
                throw NoteNotFoundException()
            }
            val updated = note.copy(id = id)
            notes[id] = updated
            return updated
        }

        override fun deleteNote(id: Long) {
            if (!notes.containsKey(id)) {
                throw NoteNotFoundException()
            }
            notes.remove(id)
        }
    }

    private class MockNoteRepository : NoteRepository {
        override fun <S : Note> save(entity: S): S = entity
        override fun <S : Note> saveAll(entities: Iterable<S>): Iterable<S> = entities
        override fun findById(id: Long): java.util.Optional<Note> = java.util.Optional.empty()
        override fun existsById(id: Long): Boolean = false
        override fun findAll(): Iterable<Note> = emptyList()
        override fun findAllById(ids: Iterable<Long>): Iterable<Note> = emptyList()
        override fun count(): Long = 0
        override fun deleteById(id: Long) {}
        override fun delete(entity: Note) {}
        override fun deleteAllById(ids: Iterable<Long>) {}
        override fun deleteAll(entities: Iterable<Note>) {}
        override fun deleteAll() {}
    }

    @Test
    fun `should create a note with generated id`() {
        val service = MockNoteService()
        val controller = NoteController(service)
        val note = Note(title = "Test Note", content = "Test Content")

        val response = controller.createNote(note)

        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(response.body).isNotNull()
        assertThat(response.body?.id).isEqualTo(1L)
        assertThat(response.body?.title).isEqualTo("Test Note")
        assertThat(response.body?.content).isEqualTo("Test Content")
    }

    @Test
    fun `should get all notes`() {
        val service = MockNoteService()
        val controller = NoteController(service)
        controller.createNote(Note(title = "Note 1", content = "Content 1"))
        controller.createNote(Note(title = "Note 2", content = "Content 2"))

        val response = controller.getAllNotes()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).hasSize(2)
    }

    @Test
    fun `should get note by id`() {
        val service = MockNoteService()
        val controller = NoteController(service)
        val created = controller.createNote(Note(title = "Test Note", content = "Test Content"))

        val response = controller.getNoteById(created.body?.id!!)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.title).isEqualTo("Test Note")
    }

    @Test
    fun `should throw exception when note does not exist`() {
        val service = MockNoteService()
        val controller = NoteController(service)

        assertThatThrownBy { controller.getNoteById(999L) }
            .isInstanceOf(NoteNotFoundException::class.java)
    }

    @Test
    fun `should update existing note`() {
        val service = MockNoteService()
        val controller = NoteController(service)
        val created = controller.createNote(Note(title = "Original", content = "Original Content"))

        val response = controller.updateNote(created.body?.id!!, Note(title = "Updated", content = "Updated Content"))

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.title).isEqualTo("Updated")
        assertThat(response.body?.content).isEqualTo("Updated Content")
    }

    @Test
    fun `should throw exception when updating non-existent note`() {
        val service = MockNoteService()
        val controller = NoteController(service)

        assertThatThrownBy {
            controller.updateNote(999L, Note(title = "Updated", content = "Updated Content"))
        }.isInstanceOf(NoteNotFoundException::class.java)
    }

    @Test
    fun `should delete existing note`() {
        val service = MockNoteService()
        val controller = NoteController(service)
        val created = controller.createNote(Note(title = "Test Note", content = "Test Content"))

        val response = controller.deleteNote(created.body?.id!!)

        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `should throw exception when deleting non-existent note`() {
        val service = MockNoteService()
        val controller = NoteController(service)

        assertThatThrownBy { controller.deleteNote(999L) }
            .isInstanceOf(NoteNotFoundException::class.java)
    }
}

