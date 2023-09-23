package com.beck.macronclient.data

import com.beck.macronclient.model.MacronMessage
import com.beck.macronclient.model.Receiver
import com.beck.macronclient.model.ReceiverFunction
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.coroutines.delay

interface MacronClient {
    // Request
    suspend fun registerUser(email: String): Result<String>

    suspend fun getReceivers(key: String): Result<List<Receiver>>

    suspend fun execReceiverFunction(key: String, receiver: String, id: Int)
}

//class MacronClientImpl: MacronClient {
//    val client = HttpClient {
//        install(WebSockets)
//    }
//
//    override suspend fun registerUser(email: String) {
//        val authMsg = MacronMessage("auth", email)
//
//
//    }
//}

val mockFunctions = listOf(
    ReceiverFunction(0, "Firefox", "Open Firefox"),
    ReceiverFunction(1, "Alacritty", "Open Terminal"),
    ReceiverFunction(2, "Netflix", "Open Firefox and navigate to Netflix"),
)
val mockReceivers = listOf(
    Receiver("Desktop", mockFunctions),
    Receiver("TV", mockFunctions),
    Receiver("Tablet", mockFunctions),
)

class MacronClientMock: MacronClient {
    override suspend fun registerUser(email: String): Result<String> {
        delay(3000)
        return Result.success("123456")
    }

    override suspend fun getReceivers(key: String): Result<List<Receiver>> {
        delay(3000)
        return Result.success(mockReceivers)
    }

    override suspend fun execReceiverFunction(key: String, receiver: String, id: Int) {
        TODO("Not yet implemented")
    }
}