package com.beck.macronclient.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.beck.macronclient.viewmodel.MacronViewModel

// Top level composable to provide scaffold and navigation between main screens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MacronViewModel) {
    val homeNavController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Macron") })
        },
    ){
        NavHost(navController = homeNavController , startDestination ="receiver" ) {
            val padding = it
            composable(route = "receiver") {
                ReceiverScreen(modifier = Modifier.padding(padding), viewModel = viewModel, onReceiverSelect = { homeNavController.navigate("macros") })
            }

            composable(route = "macros") {
                MacroScreen(modifier = Modifier.padding(padding), viewModel = viewModel)
            }
        }
    }
}