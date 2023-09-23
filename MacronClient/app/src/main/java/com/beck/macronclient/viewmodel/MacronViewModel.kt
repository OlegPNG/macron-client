package com.beck.macronclient.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beck.macronclient.data.MacronClient
import com.beck.macronclient.data.MacronClientMock
import com.beck.macronclient.data.mockReceivers
import com.beck.macronclient.model.Receiver
import com.beck.macronclient.model.ReceiverFunction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


abstract class MacronViewModel: ViewModel() {
    abstract val loadingStatus: StateFlow<LoadingStatus>

    abstract val receivers: StateFlow<List<Receiver>?>

    abstract val email: StateFlow<String>

    abstract val currentReceiver: StateFlow<Receiver?>

    abstract fun setEmail(email: String)

    abstract fun getReceivers()

    abstract fun selectReceiver(name: String)

}

enum class LoadingStatus {
    SUCCESS, FAILURE, LOADING
}
class MacronViewModelImpl: MacronViewModel() {

    private val client = MacronClientMock()

    private val _loadingStatus = MutableStateFlow(LoadingStatus.SUCCESS)
    override val loadingStatus = _loadingStatus.asStateFlow()

    private var _receivers: MutableStateFlow<List<Receiver>?> = MutableStateFlow(null)
    override val receivers = _receivers.asStateFlow()

    private val _email = MutableStateFlow("")
    override val email = _email.asStateFlow()

    private val _currentReceiver = MutableStateFlow<Receiver?>(null)
    override val currentReceiver = _currentReceiver.asStateFlow()

    override fun setEmail(email: String) {
        _email.value = email
    }

    override fun getReceivers() {
        _loadingStatus.value = LoadingStatus.LOADING
        viewModelScope.launch {
            val result = client.getReceivers("foo")

            result.onSuccess {
                _loadingStatus.value = LoadingStatus.SUCCESS
                _receivers.value = it
            }

            result.onFailure {
                _loadingStatus.value = LoadingStatus.FAILURE
            }
        }
    }

    override fun selectReceiver(name: String) {
        _currentReceiver.value = receivers.value?.find { it.name == name }
    }
}

class MockViewModel: MacronViewModel() {
    override val loadingStatus: StateFlow<LoadingStatus> = MutableStateFlow(LoadingStatus.SUCCESS).asStateFlow()
    override val receivers: StateFlow<List<Receiver>?> = MutableStateFlow(mockReceivers).asStateFlow()
    override val email: StateFlow<String> = MutableStateFlow("1234@email.com").asStateFlow()
    override val currentReceiver: StateFlow<Receiver?> = MutableStateFlow(mockReceivers[0]).asStateFlow()
    override fun setEmail(email: String) {
        TODO("Not yet implemented")
    }

    override fun getReceivers() {
        TODO("Not yet implemented")
    }

    override fun selectReceiver(name: String) {
        TODO("Not yet implemented")
    }
}