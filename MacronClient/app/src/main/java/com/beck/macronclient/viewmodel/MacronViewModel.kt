package com.beck.macronclient.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beck.macronclient.data.SessionManager
import com.beck.macronclient.data.SessionState
import com.beck.macronclient.model.ReceiverFunction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class MacronViewModel(): ViewModel() {
    abstract val url: StateFlow<String>
    abstract val password: StateFlow<String>

    abstract val loadingStatus: StateFlow<ScreenState>
    abstract val clientState: StateFlow<ClientState>

    abstract val uiEventFlow: SharedFlow<UiEvent>

    abstract val currentReceiver: StateFlow<ReceiverState?>

    abstract val clientStatus: StateFlow<SessionState>
    //abstract val clientStatus: StateFlow<WebsocketStatus>

    abstract fun setUrl(url: String)
    abstract fun setPassword(password: String)
    abstract fun loginWithPassword()
    abstract fun getReceivers()
    abstract fun selectReceiver(name: String)
    abstract fun execFunction(id: Int)
}

data class ClientState(
    val receivers: List<String>? = null
)
data class ReceiverState(
    val name: String,
    val functions: List<ReceiverFunction>? = null
)

enum class UiEvent(msg: String? = null){
    LOADING,
    SUCCESS,
    ERROR
}
enum class ScreenState{
    LOADING,
    ERROR,
    SUCCESS
}

class MacronViewModelImpl(): MacronViewModel() {
    private val _url = MutableStateFlow("")
    override val url = _url.asStateFlow()
    private val _password = MutableStateFlow("")
    override val password = _password.asStateFlow()

    private val _loadingStatus = MutableStateFlow(ScreenState.SUCCESS)
    override val loadingStatus = _loadingStatus.asStateFlow()

    private val _uiEventFlow = MutableSharedFlow<UiEvent>()
    override val uiEventFlow = _uiEventFlow.asSharedFlow()

    private val _clientState = MutableStateFlow(ClientState())
    override val clientState = _clientState.asStateFlow()

    private val _currentReceiver: MutableStateFlow<ReceiverState?> = MutableStateFlow(null)
    override val currentReceiver = _currentReceiver.asStateFlow()

    private val sessionManager = SessionManager()
    override val clientStatus = sessionManager.status

    override fun setUrl(url: String) {
        _url.value = url
    }

    override fun setPassword(password: String) {
        _password.value = password
    }


    override fun loginWithPassword() {
        _loadingStatus.value = ScreenState.LOADING
        viewModelScope.launch {
            val state = sessionManager
                .startSessionWithPassword(url.value, password.value)
                .onEach {
                    Log.d("MacronViewModel", "Message: $it")
                    if(it.type == "auth_success") {
                        _loadingStatus.value = ScreenState.SUCCESS
                        _uiEventFlow.emit(UiEvent.SUCCESS)
                        sessionManager.requestReceivers()
                    }
                    if(it.type == "receivers") {
                        _loadingStatus.value = ScreenState.SUCCESS
                        _uiEventFlow.emit(UiEvent.SUCCESS)
                        _clientState.value = _clientState.value.copy(
                            receivers = it.receivers
                        )
                    }
                    if(it.type == "functions") {
                        Log.d("MacronViewModelImpl", "Received functions: ${it.functions.toString()}")
                        _loadingStatus.value = ScreenState.SUCCESS
                        _uiEventFlow.emit(UiEvent.SUCCESS)
                        _currentReceiver.value = _currentReceiver.value?.copy(
                            functions = it.functions
                        )
                    }
                }
                .catch {
                    Log.e("MacronViewModel", "Error: $it")
                    _loadingStatus.value = ScreenState.ERROR
                    _uiEventFlow.emit(UiEvent.ERROR)
                }
                .stateIn(viewModelScope)
        }

        viewModelScope.launch {
            Log.d("MacronViewModelImpl", "Listening to sessionManager status")
            sessionManager.status.map {
                when(it) {
                    is SessionState.Open -> {
                        _loadingStatus.value = ScreenState.SUCCESS
                        _uiEventFlow.emit(UiEvent.SUCCESS)
                    }
                    is SessionState.Closed -> {

                    }
                    is SessionState.Error -> {
                        _loadingStatus.value = ScreenState.ERROR
                        _uiEventFlow.emit(UiEvent.SUCCESS)
                    }
                }
            }
        }

    }


    override fun getReceivers() {
        _loadingStatus.value = ScreenState.LOADING
         viewModelScope.launch {
            //client.requestReceivers()
            sessionManager.requestReceivers()
        }
    }

    override fun selectReceiver(name: String) {
        _currentReceiver.value = ReceiverState(name, null)
        viewModelScope.launch {
            sessionManager.requestFunctions(name)
        }
    }

    override fun execFunction(id: Int) {
        viewModelScope.launch {
            sessionManager.execFunction(currentReceiver.value!!.name, id)
        }
    }
}

class MockViewModel: MacronViewModel() {
    override val url: StateFlow<String> = MutableStateFlow("foobar.com")
    override val password: StateFlow<String> = MutableStateFlow("123pass")
    override var loadingStatus: StateFlow<ScreenState> = MutableStateFlow(ScreenState.SUCCESS)
    override val clientState: StateFlow<ClientState> = MutableStateFlow(ClientState(receivers = mockReceivers))
    override val uiEventFlow = MutableSharedFlow<UiEvent>()

    //override val clientStatus = MutableStateFlow(WebsocketStatus.OPEN)
    override val currentReceiver = MutableStateFlow(ReceiverState(
        "foo",
        mockFunctions
    ))
    override val clientStatus = MutableStateFlow(SessionState.Open)
    override fun setUrl(url: String) {
    }

    override fun setPassword(password: String) {
    }

    override fun loginWithPassword() {
    }

    override fun getReceivers() {
    }

    override fun selectReceiver(name: String) {

    }

    override fun execFunction(id: Int) {

    }

}

val mockFunction: ReceiverFunction = ReceiverFunction(id=0, name="Terminal", description="Alacritty")
val mockFunctions = listOf(
    mockFunction, mockFunction, mockFunction
)
val mockReceivers: List<String> = listOf(
    "Desktop", "TV", "Rust"
)
/*val mockReceivers: List<Receiver> = listOf(
    Receiver(
        name="Desktop",
        functions = listOf(
            mockFunction, mockFunction, mockFunction
        )
    ),
    Receiver(
        name="TV",
        functions = listOf(
            mockFunction, mockFunction, mockFunction
        )

    ),
    Receiver(
        name="Rust",
        functions = listOf(
            mockFunction, mockFunction, mockFunction
        )

    ),
)*/