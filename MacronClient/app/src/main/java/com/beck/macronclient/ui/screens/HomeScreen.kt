package com.beck.macronclient.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.beck.macronclient.viewmodel.ScreenState
import com.beck.macronclient.viewmodel.MacronViewModel

// Top level composable to provide scaffold and navigation between main screens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MacronViewModel,
    onRetryClick: () -> Unit
) {
    val homeNavController = rememberNavController()
    val status by viewModel.loadingStatus.collectAsState()
    var screenState by remember { mutableStateOf(ScreenState.LOADING) }
    val context = LocalContext.current
    //LaunchedEffect(status) {
    //    if (status == ScreenState.SUCCESS) {
    //        Toast.makeText(context, "Success...", Toast.LENGTH_SHORT).show()
    //    }
    //}
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Macron") })
        },
    ){
        NavHost(navController = homeNavController , startDestination ="receiver" ) {
            val padding = it
            composable(route = "receiver") {
                ReceiverScreen(
                    modifier = Modifier.padding(padding),
                    viewModel = viewModel,
                    onReceiverSelect = { homeNavController.navigate("macros") },
                    onRetryClick = onRetryClick
                )
            }

            composable(route = "macros") {
                MacroScreen(modifier = Modifier.padding(padding), viewModel = viewModel)
            }
        }
    }
}