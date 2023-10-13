package com.beck.macronclient.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.beck.macronclient.viewmodel.MacronViewModel

@Composable
fun WsTestScreen(viewModel: MacronViewModel) {
    val receivers = viewModel.clientState.collectAsState().value.receivers
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(onClick = viewModel::getReceivers) {
            Text("Get Receivers")
        }
        if (receivers != null) {
            LazyColumn() {
                items(receivers) {
                    Text(it)
                }
            }
        }
    }
}