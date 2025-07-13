package com.virtuous.domain.model.post


import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class PostFeed(
    val postId : Int,
    val providerId : String,
    val postType: PostType,
    val title: String,
    val content: String,
    val nickname: String,
    val profileImageUrl: String? = null,
    val viewCount: Int = 0,
    val commentCount: Int = 0,
    val totalEmotionCount : Int = 0,
    val isVerified: Boolean = false,
    val imageUrl: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    fun getFormattedTime(): String {
        val now = LocalDateTime.now()
        val duration = Duration.between(createdAt, now)

        // 1일 이상일 경우
        if (duration.toDays() >= 1) {
            return if (duration.toDays() <= 7) {
                "${duration.toDays()}일 전"
            } else {
                val formatter = DateTimeFormatter.ofPattern("M/d")
                createdAt.format(formatter)
            }
        }

        // 1시간 이상일 경우
        if (duration.toHours() >= 1) {
            return "${duration.toHours()}시간 전"
        }

        // 1분 이상일 경우
        if (duration.toMinutes() >= 1) {
            return "${duration.toMinutes()}분 전"
        }

        // 1분 미만일 경우
        return "방금 전"
    }
}

data class PostDetail(
    val postId: Int,
    val postType: PostType,
    val viewCount: Int,
    val emotionCount: EmotionCount,
    val title: String,
    val content: String,
    val missionContent: String? = null,
    val providerId:String,
    val nickname: String,
    val images: List<String> = emptyList(),
    val profileImageUrl: String? = null,
    val yourEmotionType: Emotion? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val isOwner: Boolean = true,
    val isVerified: Boolean = false,
) {
    fun getFormattedDate(): String {
        val formatter = DateTimeFormatter.ofPattern("M/d HH:mm")
        return createdAt.format(formatter)
    }
}

enum class HomeTab(val label: String) {
    ALL("전체"),
    FREE("자유"),
    GOOD_DEED("선행"),
    MISSION("미션")
}


enum class PostType(val label: String) {
    FREE("자유"),
    GOOD_DEED("선행"),
    MISSION("미션");

    companion object {
        fun fromString(postType : String) : PostType {
            return entries.find { it.name == postType } ?: throw IllegalArgumentException("Invalid PostType: $postType")
        }
    }
}

enum class WritePostType(val label: String) {
    GOOD_DEED("선행"),
    FREE("자유"),
}