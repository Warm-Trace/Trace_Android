package com.virtuous.domain.model.notification

import com.virtuous.domain.model.post.Emotion
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Notification(
    val id: Int,
    val createdAt: LocalDateTime,
    val title: String? = null,
    val body: String? = null,
    val notificationData: NotificationData
) {
    val formattedCreatedAt: String
        get() = createdAt.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
}

data class NotificationData(
    val title: String,
    val body: String,
    val type: NotificationType,
    val postId: Int? = null,
    val emotion: Emotion? = null,
)

enum class NotificationType {
    COMMENT,
    EMOTION,
    MISSION
}