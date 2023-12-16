package com.example.nixy.screens.notes

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nixy.Navigation
import com.example.nixy.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@Composable
fun NotesScreen(
    viewModel: NotesViewModel = viewModel(factory = NotesViewModel.Factory),
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
                        Item(title = it.title,
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
    title: String,
    onItemClicked: () -> Unit,
    onItemRemoved: () -> Unit
) {
    var selected by rememberSaveable {
        mutableStateOf(false)
    }
    val animatedPadding by animateDpAsState(
        if (selected) {
            15.dp
        } else {
            0.dp
        },
        label = ""
    )
    val interactionSource = remember { MutableInteractionSource() }
    val viewConfiguration = LocalViewConfiguration.current
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collectLatest { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    selected = false
                    delay(viewConfiguration.longPressTimeoutMillis)
                    selected = true
                }
            }
        }
    }

    ElevatedButton(
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
            .padding(animatedPadding),
        onClick = { if (animatedPadding.value == 0.0f) onItemClicked()},
        ) {
        Text(title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (selected) MaterialTheme.colorScheme.secondary
            else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            if (animatedPadding.value > 0.0f) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.1f),
                    onClick = onItemRemoved) {
                    Icon(imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error)
                }
            }
    }
}