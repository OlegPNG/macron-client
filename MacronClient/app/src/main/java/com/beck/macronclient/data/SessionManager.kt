package com.beck.macronclient.data

import android.util.Log
import com.beck.macronclient.model.AuthMessage
import com.beck.macronclient.model.InboundMessage
import com.beck.macronclient.model.OutboundMessage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

sealed interface SessionState{
    data object Open: SessionState
    data object Closed: SessionState
    data class Error(val error: Exception): SessionState
}

@Serializable
data class Credential (
    val email: String,
    val password: String
)
class SessionManager {
    private val _status: MutableStateFlow<SessionState> = MutableStateFlow(SessionState.Closed)
    val status = _status.asStateFlow()

    private var session: WebSocketSession? = null

    private val client = HttpClient(OkHttp) {
        install(Logging)
        install(ContentNegotiation) {
            json()
        }
        install(WebSockets)
    }

    suspend fun close() {
        session?.close()
        _status.value = SessionState.Closed
    }

    suspend fun login(url: String, email: String, password: String): Result<String> {
        return try {
            val response: AuthMessage = client.post {
                contentType(ContentType.Application.Json)
                url("https://$url/v1/login")
                setBody(Credential(email, password))
            }.body()
            Result.success(response.sessionToken)
        } catch(e: Exception) {
            Log.e("SessionManager.login()", e.stackTraceToString())
            Result.failure(e)
        }
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
    suspend fun startSession(url: String, email: String, password: String): Flow<InboundMessage>? {
        val tokenResult = login(url, email, password)
        //_status.value = SessionState.Open
        val token = if(tokenResult.isSuccess) {
            _status.value = SessionState.Open
            tokenResult.getOrNull()
        } else {
            _status.value = SessionState.Closed
            return null
        }
        return flow {
            /*session = client.webSocketSession {
                url(
                    "ws://$url/v1/ws/client"
                )
            }*/
            session = client.webSocketSession("wss://$url/v1/ws/client") {
                url {
                    parameters.append("session_token", token ?: "")
                }
            }

            /*val msg = OutboundMessage(
                type = "auth",
                password = password
            )
            session?.outgoing?.send(
                Frame.Text(Json.encodeToString(msg))
            )*/
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
            session!!.incoming.receive()
        }
    }

}