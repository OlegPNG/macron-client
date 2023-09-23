package com.beck.macronclient.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.beck.macronclient.model.Receiver
import com.beck.macronclient.ui.common.MacronButton
import com.beck.macronclient.viewmodel.MacronViewModel
import com.beck.macronclient.viewmodel.MacronViewModelImpl
import com.beck.macronclient.viewmodel.MockViewModel

@Composable
fun MacroScreen(modifier: Modifier = Modifier, viewModel: MacronViewModel = MacronViewModelImpl()) {
    val receiver = viewModel.currentReceiver.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
    ) {
        if (receiver != null) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Adaptive(120.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(receiver.functions) {
                    MacronButton(modifier = Modifier, text = it.name, onClick = {})
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMacroScreen() {
    MacroScreen(viewModel = MockViewModel())
}