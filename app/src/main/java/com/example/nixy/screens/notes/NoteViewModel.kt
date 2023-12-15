package com.example.nixy.screens.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nixy.NotesApplication
import com.example.nixy.data.Note
import com.example.nixy.data.NoteDatabase
import com.example.nixy.data.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository): ViewModel() {
    private val _noteState = MutableStateFlow(NoteUi())
    val noteState = _noteState.asStateFlow()
    private val noteId: String = checkNotNull(savedStateHandle["id"])

    init {
                viewModelScope.launch {
                    noteRepository.getNote(noteId.toInt()).collect { note ->
                        if (note != null) {
                            _noteState.update {
                                it.copy(
                                    id = note.id,
                                    title = note.title,
                                    description = note.description
                                )
                            }
                        }
                    }
                }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val context = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NotesApplication).context
                val db = NoteDatabase.getNoteDatabase(context)
                NoteViewModel(
                    this.createSavedStateHandle(),
                    NoteRepository(db.noteDao()))
            }
        }
    }

    suspend fun insertNote(note: Note) {
        noteRepository.insertNote(note)
        _noteState.update {
            it.copy(note.id, note.title, note.description)
        }
    }
    suspend fun updateNote(note: Note) {
        noteRepository.updateNote(note)
        _noteState.update {
            it.copy(note.id, note.title, note.description)
        }
    }
}
