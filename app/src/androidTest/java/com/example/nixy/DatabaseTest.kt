package com.example.nixy

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nixy.data.Note
import com.example.nixy.data.NoteDao
import com.example.nixy.data.NoteDatabase
import kotlinx.coroutines.flow.first
import org.junit.After
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var noteDao: NoteDao
    private lateinit var db: NoteDatabase
    private val exampleNote = Note(3, "Title", "Description")
    @Before
    fun initDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            NoteDatabase::class.java
        ).build()
        noteDao = db.noteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeReadDb() = runTest{
        noteDao.insertNote(exampleNote)
        val insertedNote = noteDao.getNote(exampleNote.id).first()
        assertEquals(exampleNote.title, insertedNote.title)
    }

    @Test
    fun updateDb() = runTest {
        noteDao.insertNote(exampleNote)
        noteDao.insertNote(exampleNote.copy(title = "Title"))
        val updatedNote = noteDao.getNote(exampleNote.id).first()
        assertEquals(exampleNote.id, updatedNote.id)
    }

    @Test
    fun deleteFromDb() = runTest {
        noteDao.insertNote(exampleNote)
        noteDao.deleteNote(exampleNote)
        val newNote = noteDao.getNote(exampleNote.id).first()
        assertNull(newNote)
    }
}