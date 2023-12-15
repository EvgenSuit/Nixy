package com.example.nixy

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType

class Navigation {
    companion object {
        const val rootDestination: String = "notes"

        val type: NavType<String?> = NavType.StringType
    }
}

@Composable
fun NavGraph(navController: NavController) {

}