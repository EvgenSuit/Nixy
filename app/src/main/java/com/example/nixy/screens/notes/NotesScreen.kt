package com.example.nixy.screens.notes

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nixy.Navigation
import com.example.nixy.R
import com.example.nixy.data.Note
import kotlinx.coroutines.launch

@Composable
fun NotesScreen(
    viewModel: NotesViewModel = viewModel(factory = NotesViewModel.Factory)
) {
    NotesScreenBody(viewModel = viewModel)
}

@Composable
fun NotesScreenBody(
    viewModel: NotesViewModel,
) {
    val notesUiState by viewModel.notesUiState.collectAsState()
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    NavHost(navController, startDestination = Navigation.rootDestination) {
        composable(Navigation.rootDestination)
        {
            Box{
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small)),
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                ) {
                    items(notesUiState.notesList) {
                        Item(text = it.title,
                            onItemClicked = {navController.navigate("${Navigation.rootDestination}/${it.id}")},
                            onItemRemoved = {
                                coroutineScope.launch {
                                    viewModel.deleteNote(it)
                                }
                            })
                    }
                }
                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(dimensionResource(id = R.dimen.padding_large)),
                    onClick = {
                        coroutineScope.launch {
                            val maxTableIndex = viewModel.getMaxTableIndex()
                            navController.navigate("${Navigation.rootDestination}/${maxTableIndex+1}")
                        }
                    }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                }
            }
        }
        composable("${Navigation.rootDestination}/{id}",
            arguments = listOf(navArgument("id"){Navigation.type})
        ) {
            val id = it.arguments?.getString("id") ?: "0"
            NoteBody(id = id.toInt(),
                navController = navController)
        }
    }
}

@Composable
fun Item(
    text: String,
    onItemClicked: () -> Unit,
    onItemRemoved: () -> Unit
) {
    ElevatedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onItemClicked) {
        Row {
            Text(text,
                modifier = Modifier.align(Alignment.CenterVertically))
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = onItemRemoved) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
            }
        }
    }
}