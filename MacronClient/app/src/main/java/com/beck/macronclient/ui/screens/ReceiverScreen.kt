package com.beck.macronclient.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beck.macronclient.data.mockReceivers
import com.beck.macronclient.model.Receiver
import com.beck.macronclient.ui.common.MacronButton
import com.beck.macronclient.viewmodel.LoadingStatus
import com.beck.macronclient.viewmodel.MacronViewModel
import com.beck.macronclient.viewmodel.MacronViewModelImpl
import com.beck.macronclient.viewmodel.MockViewModel

@Composable
fun ReceiverScreen(
    modifier: Modifier = Modifier,
    viewModel: MacronViewModel = MacronViewModelImpl(),
    onReceiverSelect: () -> Unit = {}
) {
    val receivers = viewModel.receivers.collectAsState().value
    val status = viewModel.loadingStatus.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        when(status) {
            LoadingStatus.SUCCESS -> {
                if (receivers != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Macros",
                            fontSize = 24.sp
                        )
                        LazyVerticalGrid(
                            modifier = Modifier,
                            columns = GridCells.Adaptive(100.dp),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(receivers) {
                                MacronButton(
                                    text = it.name,
                                    onClick = {
                                        viewModel.selectReceiver(it.name)
                                        onReceiverSelect()
                                    }
                                )
                            }
                        }
                    }
                }
            }
            LoadingStatus.LOADING -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...")
                }
            }
            LoadingStatus.FAILURE -> {
                TODO("Execute onFailure callback")
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun ReceiverScreenPreview() {
    ReceiverScreen(viewModel = MockViewModel())
}

