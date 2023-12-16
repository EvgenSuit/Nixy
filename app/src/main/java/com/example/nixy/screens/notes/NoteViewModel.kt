package com.example.nixy.screens.notes

import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class NoteViewModel(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository): ViewModel() {
    private val _noteState = MutableStateFlow(NoteUi())
    val noteState = _noteState.asStateFlow()
    private val noteId: String = checkNotNull(savedStateHandle["id"])
    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    private var saveJob: Job? = null
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

    fun insertNote(note: Note) {
        saveJob?.cancel()
        _saved.update { false }
        saveJob = viewModelScope.launch {
            delay(600L)
            noteRepository.insertNote(note)
            _saved.update { true }
        }
        _noteState.update {
            it.copy(note.id, note.title, note.description)
        }
    }
}
