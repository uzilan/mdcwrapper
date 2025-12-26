package com.example.kotlin.mdcwrapper

import com.example.mdcwrapper.MdcwrapperApplication
import com.example.mdcwrapper.kotlin.Note
import com.example.mdcwrapper.kotlin.NoteNotFoundException
import com.example.mdcwrapper.kotlin.NoteRepository
import com.example.mdcwrapper.kotlin.NoteService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [MdcwrapperApplication::class])
class NoteServiceIntegrationTest {
    @Autowired
    private lateinit var noteService: NoteService

    @Autowired
    private lateinit var noteRepository: NoteRepository

    @Test
    fun `should create note in database`() {
        val note = Note(title = "Create Test", content = "This is saved to H2")

        val savedNote = noteService.createNote(note)

        assertThat(savedNote.id).isNotNull()
        assertThat(savedNote.title).isEqualTo("Create Test")
        assertThat(savedNote.content).isEqualTo("This is saved to H2")
    }

    @Test
    fun `should get all notes from database`() {
        noteRepository.deleteAll()
        noteService.createNote(Note(title = "Note 1", content = "Content 1"))
        noteService.createNote(Note(title = "Note 2", content = "Content 2"))

        val notes = noteService.getAllNotes()

        assertThat(notes).hasSize(2)
    }

    @Test
    fun `should get note by id from database`() {
        val saved = noteService.createNote(Note(title = "Find Me", content = "Find Content"))

        val found = noteService.getNoteById(saved.id!!)

        assertThat(found).isNotNull()
        assertThat(found.title).isEqualTo("Find Me")
        assertThat(found.content).isEqualTo("Find Content")
    }

    @Test
    fun `should throw exception when note not found`() {
        assertThatThrownBy { noteService.getNoteById(99999L) }
            .isInstanceOf(NoteNotFoundException::class.java)
    }

    @Test
    fun `should update note in database`() {
        val saved = noteService.createNote(Note(title = "Original", content = "Original Content"))

        val updated = noteService.updateNote(saved.id!!, Note(title = "Updated", content = "Updated Content"))

        assertThat(updated).isNotNull()
        assertThat(updated.title).isEqualTo("Updated")
        assertThat(updated.content).isEqualTo("Updated Content")

        val found = noteService.getNoteById(saved.id!!)
        assertThat(found.title).isEqualTo("Updated")
    }

    @Test
    fun `should throw exception when updating non-existent note`() {
        assertThatThrownBy {
            noteService.updateNote(99999L, Note(title = "Updated", content = "Updated Content"))
        }.isInstanceOf(NoteNotFoundException::class.java)
    }

    @Test
    fun `should delete note from database`() {
        val saved = noteService.createNote(Note(title = "Delete Me", content = "Delete Content"))

        noteService.deleteNote(saved.id!!)

        assertThatThrownBy { noteService.getNoteById(saved.id!!) }
            .isInstanceOf(NoteNotFoundException::class.java)
    }

    @Test
    fun `should throw exception when deleting non-existent note`() {
        assertThatThrownBy { noteService.deleteNote(99999L) }
            .isInstanceOf(NoteNotFoundException::class.java)
    }
}
