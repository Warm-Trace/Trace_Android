package com.virtuous.domain.model.post

enum class Emotion(val label: String) {
    HEARTWARMING("따뜻해요"),
    LIKEABLE("좋아요"),
    TOUCHING("감동이에요"),
    IMPRESSIVE("멋져요"),
    GRATEFUL("고마워요");

    companion object {
        fun fromString(emotionType: String?): Emotion? =
            Emotion.entries.find { it.name == emotionType }
    }
}

data class EmotionCount(
    val heartWarmingCount: Int = 0,
    val likeableCount: Int = 0,
    val touchingCount: Int = 0,
    val impressiveCount: Int = 0,
    val gratefulCount: Int = 0
) {
    companion object {
        fun fromMap(map: Map<String, Int>): EmotionCount {
            return EmotionCount(
                heartWarmingCount = map["heartwarmingCount"] ?: 0,
                likeableCount = map["likeableCount"] ?: 0,
                touchingCount = map["touchingCount"] ?: 0,
                impressiveCount = map["impressiveCount"] ?: 0,
                gratefulCount = map["gratefulCount"] ?: 0
            )
        }
    }
}