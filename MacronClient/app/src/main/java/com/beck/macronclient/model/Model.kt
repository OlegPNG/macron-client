package com.beck.macronclient.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OutboundMessage(
    val type: String,
    val password: String? = null,
    @SerialName("auth_key")
    val authKey: String? = null,
    @SerialName("receiver_name")
    val receiverName: String? = null,
    @SerialName("function_id")
    val functionId: Int? = null
)

@Serializable
data class InboundMessage(
    val type: String,
    val error: String? = null,
    val receivers: List<String>? = null,
    val functions: List<ReceiverFunction>? = null,
)

@Serializable
data class Receiver(
    val name: String,
    val functions: List<ReceiverFunction>
)

@Serializable
data class ReceiverFunction(
    val id: Int,
    val name: String,
    val description: String
)