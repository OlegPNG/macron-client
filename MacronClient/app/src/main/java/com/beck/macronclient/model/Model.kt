package com.beck.macronclient.model

import kotlinx.serialization.Serializable

@Serializable
data class MacronMessage(
    val type: String,
    val email: String
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