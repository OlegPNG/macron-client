package com.beck.macronclient.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beck.macronclient.ui.common.MacronButton
import com.beck.macronclient.viewmodel.ScreenState
import com.beck.macronclient.viewmodel.MacronViewModel
import com.beck.macronclient.viewmodel.MacronViewModelImpl
import com.beck.macronclient.viewmodel.MockViewModel
import com.beck.macronclient.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ReceiverScreen(
    modifier: Modifier = Modifier,
    viewModel: MacronViewModel = MacronViewModelImpl(),
    onReceiverSelect: () -> Unit = {},
    onRetryClick: () -> Unit = {}
) {
    //val receivers = viewModel.receivers.collectAsState().value
    val receivers = viewModel.clientState.collectAsState().value.receivers
    val status = viewModel.loadingStatus
    var screenState  by remember { mutableStateOf(ScreenState.LOADING) }

    LaunchedEffect(key1=true) {
        viewModel.uiEventFlow.collectLatest {
            when(it) {
                UiEvent.LOADING -> screenState = ScreenState.LOADING
                UiEvent.SUCCESS -> screenState = ScreenState.SUCCESS
                UiEvent.ERROR -> {}
                UiEvent.DISCONNECT -> {}
            }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .padding(bottom=16.dp)
    ) {
        //when(status.collectAsState().value) {
        when(status.collectAsState().value) {
            ScreenState.SUCCESS -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Devices",
                        fontSize = 24.sp
                    )
                    if (receivers != null) {
                        LazyVerticalGrid(
                            modifier = Modifier,
                            columns = GridCells.Adaptive(100.dp),
                            contentPadding = PaddingValues(8.dp)
                        ) {
                            items(receivers) {
                                MacronButton(
                                    text = it,
                                    onClick = {
                                        viewModel.selectReceiver(it)
                                        onReceiverSelect()
                                    }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = viewModel::getReceivers
                        ) {
                            Text("Get Receivers")
                        }
                    }
                }
            }
            ScreenState.LOADING -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Loading...")
                }
            }
            ScreenState.ERROR -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column() {
                        Text("There was an error...")
                        Button(
                            onClick = onRetryClick
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun ReceiverScreenPreview() {
    ReceiverScreen(viewModel = MockViewModel())
}

