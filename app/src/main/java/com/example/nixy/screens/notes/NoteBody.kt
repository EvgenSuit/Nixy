package com.example.nixy.screens.notes

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.nixy.R
import com.example.nixy.data.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun NoteBody(
    id: Int,
    navController: NavHostController,
    viewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory),
) {
    val note by viewModel.noteState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var convertedNote = note.copy(id = id).toNote()
    Column {
        ElevatedButton(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
            onClick = { navController.navigateUp() }) {
            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
        }
        OutlinedTextField(
            label = {
                    Text(stringResource(id = R.string.title))
            },
            value = note.title,
            onValueChange = {
                 convertedNote = convertedNote.copy(title = it)
                handleNote(viewModel, coroutineScope, convertedNote)
            },
            modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            maxLines = 100,
            value = note.description, onValueChange = {
             convertedNote = convertedNote.copy(description = it)
            handleNote(viewModel, coroutineScope, convertedNote)
        },
            modifier = Modifier.fillMaxSize())
    }
}

fun handleNote(viewModel: NoteViewModel, coroutineScope: CoroutineScope, note: Note) {
    coroutineScope.launch {
        try {
            viewModel.insertNote(note)
        }
    catch (e: SQLiteConstraintException) {
            viewModel.updateNote(note)
        }
}
    }
