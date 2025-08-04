package com.virtuous.domain.model.notification

import com.virtuous.domain.model.post.Emotion
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Notification(
    val id: String,
    val isRead : Boolean,
    val createdAt: LocalDateTime,
    val title: String,
    val body: String,
    val type: NotificationType,
    val postId: Int? = null,
    val emotion: Emotion? = null
) {
    val formattedCreatedAt: String by lazy {
        createdAt.format(formatter)
    }

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm")
    }
}

enum class NotificationType {
    COMMENT,
    EMOTION,
    MISSION;

    companion object {
        fun fromString(type: String): NotificationType {
            return entries.firstOrNull { it.name == type } ?: COMMENT
        }
    }
}
