package com.beck.macronclient.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beck.macronclient.model.Receiver
import com.beck.macronclient.ui.common.MacronButton
import com.beck.macronclient.viewmodel.MacronViewModel
import com.beck.macronclient.viewmodel.MacronViewModelImpl
import com.beck.macronclient.viewmodel.MockViewModel
import com.beck.macronclient.viewmodel.mockReceivers

@Composable
fun MacroScreen(modifier: Modifier = Modifier, viewModel: MacronViewModel) {
    val receiver = viewModel.currentReceiver.collectAsState().value

    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .padding(bottom = 16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (receiver != null) {
                Text(
                    receiver.name,
                    fontSize = 30.sp
                )
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxWidth(),
                    columns = GridCells.Adaptive(120.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (receiver.functions != null) {
                        items(receiver.functions) {
                            MacronButton(modifier = Modifier, text = it.name, onClick = {
                                viewModel.execFunction(it.id)
                            })
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { }
            ) {
                Text("Refresh")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMacroScreen() {
    MacroScreen(viewModel = MockViewModel())
}