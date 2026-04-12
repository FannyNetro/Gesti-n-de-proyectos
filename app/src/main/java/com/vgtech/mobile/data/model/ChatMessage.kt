package com.vgtech.mobile.data.model

import java.util.UUID

/**
 * ChatMessage data class for communication between users.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderUid: String = "",
    val receiverUid: String = "",
    val projectId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
