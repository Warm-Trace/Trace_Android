package com.virtuous.network.model.notification

import com.virtuous.domain.model.notification.Notification
import com.virtuous.domain.model.notification.NotificationType
import com.virtuous.domain.model.post.Emotion
import com.virtuous.network.model.cursor.Cursor
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class GetNotificationsResponse(
    val hasNext: Boolean,
    val cursor: Cursor?,
    val content: List<NotificationContent>
) {
    fun toDomain(): List<Notification> = content.map { it.toDomain() }
}

@Serializable
data class NotificationContent(
    val id: Int,
    val createdAt: LocalDateTime,
    val title: String?,
    val body: String?,
    val data: NotificationData
) {
    fun toDomain(): Notification {
        return Notification(
            id = id,
            createdAt = createdAt.toJavaLocalDateTime(),
            title = data.title,
            body = data.body,
            postId = data.postId,
            type = NotificationType.fromString(data.type),
            emotion = Emotion.fromString(data.emotion)
        )
    }
}

@Serializable
data class NotificationData(
    val title: String,
    val body: String,
    val postId: Int?,
    val type: String,
    val emotion: String?,
    val timestamp: String
)
