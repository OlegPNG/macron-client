package com.beck.macronclient.data

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.beck.macronclient.model.InboundMessage
import com.beck.macronclient.model.OutboundMessage
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

sealed interface SessionState{
    data object Open: SessionState
    data object Closed: SessionState
    data class Error(val error: Exception): SessionState
}
class SessionManager {
    private val _status: MutableStateFlow<SessionState> = MutableStateFlow(SessionState.Closed)
    val status = _status.asStateFlow()

    private var session: WebSocketSession? = null

    private val client = HttpClient(OkHttp) {
        install(Logging)
        install(WebSockets)
    }

    suspend fun close() {
        session?.close()
        _status.value = SessionState.Closed
    }


    suspend fun requestReceivers() {
        if(status.value is SessionState.Open) {
            val msg = OutboundMessage(
                type = "receivers"
            )
            try {
                session?.outgoing?.send(Frame.Text(Json.encodeToString(msg)))
            } catch(e: Exception) {
                Log.e("SessionManager.requestReceivers()", e.stackTraceToString())
                _status.value = SessionState.Error(e)
            }
        }
    }
    suspend fun requestFunctions(receiverName: String) {
        if(status.value is SessionState.Open) {
            Log.d("SessionManager", "receiverName: $receiverName")
            val msg = OutboundMessage(
                type = "functions",
                receiverName = receiverName
            )
            try {
                val string = Json.encodeToString(msg)
                Log.d("SessionManager", "Function Request: $string")
                session?.outgoing?.send(Frame.Text(string))
            } catch(e: Exception) {
                Log.e("SessionManager.requestFunctions()", e.stackTraceToString())
                _status.value = SessionState.Error(e)
            }
        }
    }

    suspend fun execFunction(name: String, id: Int) {
        if(status.value is SessionState.Open) {
            val msg = OutboundMessage(
                type = "exec",
                receiverName = name,
                functionId = id,
            )
            try {
                session?.outgoing?.send(Frame.Text(Json.encodeToString(msg)))
            } catch(e: Exception) {
                Log.e("SessionManager.execFunction()", e.stackTraceToString())
                _status.value = SessionState.Error(e)
            }
        }
    }
    suspend fun startSessionWithPassword(url: String, password: String): Flow<InboundMessage> {
        _status.value = SessionState.Open
        return flow {
            session = client.webSocketSession {
                url(
                    "ws://$url/v1/ws/client"
                )
            }

            val msg = OutboundMessage(
                type = "auth",
                password = password
            )
            session?.outgoing?.send(
                Frame.Text(Json.encodeToString(msg))
            )
            val inbound = session!!
                .incoming
                .consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .mapNotNull { Json.decodeFromString<InboundMessage>(it.readText()) }
                .catch {
                    Log.e("SessionManager", it.stackTraceToString())
                    if(it is Exception) {
                        _status.value = SessionState.Error(it)
                    }
                }
            emitAll(inbound)
        }
    }

}