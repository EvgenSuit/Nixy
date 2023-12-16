package com.example.nixy.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao): NoteDao {
    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()
    override fun getNote(id: Int) = noteDao.getNote(id)
    override suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    override suspend fun getMaxIndex(): Int = noteDao.getMaxIndex()
}