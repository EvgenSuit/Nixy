package com.example.nixy.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nixy.NotesApplication
import com.example.nixy.data.Note
import com.example.nixy.data.NoteDatabase
import com.example.nixy.data.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel(
    private val noteRepository: NoteRepository): ViewModel() {
    private val _notesUiState = MutableStateFlow(NotesUi())
    val notesUiState = _notesUiState.asStateFlow()

    init {
        viewModelScope.launch {
            noteRepository.getAllNotes().collect {notes ->
                _notesUiState.update { state ->
                    state.copy(notesList = notes)
                }
            }
        }
    }

    suspend fun deleteNote(note: Note) {
        noteRepository.deleteNote(note)
    }

    suspend fun getMaxTableIndex(): Int {
        return noteRepository.getMaxIndex()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[APPLICATION_KEY] as NotesApplication).context
                val db = NoteDatabase.getNoteDatabase(context)
                NotesViewModel(NoteRepository(db.noteDao()))
            }
        }
    }
}

data class NotesUi(val notesList: List<Note> = listOf())

data class NoteUi(
    val id: Int = 1,
    val title: String = "",
    val description: String = ""
)
fun NoteUi.toNote(): Note = Note(
    id = id,
    title = title,
    description = description
)