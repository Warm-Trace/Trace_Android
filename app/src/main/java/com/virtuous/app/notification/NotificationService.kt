package com.virtuous.app.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.virtuous.domain.model.post.Emotion
import com.virtuous.domain.repository.NotificationRepository
import com.virtuous.main.MainActivity
import com.virtuous.trace.R
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : FirebaseMessagingService() {

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var notificationRepository: NotificationRepository

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        scope.launch {
            notificationRepository.updateDeviceToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data

        val title = data["title"] ?: "흔적"
        val body = data["body"] ?: ""
        val type = data["type"]
        val postId = data["postId"]

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        if (data.isNotEmpty()) {
            data.forEach { (key, value) -> intent.putExtra(key, value) }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(context, BACKGROUND_CHANNEL_ID)
            .setSmallIcon(com.virtuous.designsystem.R.drawable.app_icon_pencil)
            .setLargeIcon(getEmotionBitmap(context, type, data["EMOTION"]))
            .setColor(ContextCompat.getColor(context, R.color.white))
            .setContentTitle(title)
            .setContentText(body)
            .setShowWhen(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val systemNotificationId = if ((type == "COMMENT" || type == "EMOTION") && postId != null) {
            "${type}_${postId}".hashCode()
        } else {
            System.currentTimeMillis().toInt()
        }

        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(systemNotificationId, builder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val BACKGROUND_CHANNEL_ID = "background_channel_id"
        const val BACKGROUND_CHANNEL = "백그라운드 알림"
        const val BACKGROUND_CHANNEL_DESCRIPTION = "백그라운드 알림을 관리하는 채널입니다."
    }
}


private fun getEmotionBitmap(context: Context, type: String?, emotionType: String?): Bitmap? {
    if (type == "EMOTION" && emotionType != null) {
        val emotion = Emotion.fromString(emotionType)
        val emotionResId = when (emotion) {
            Emotion.HEARTWARMING -> com.virtuous.designsystem.R.drawable.heartwarming_ic
            Emotion.LIKEABLE -> com.virtuous.designsystem.R.drawable.likeable_ic
            Emotion.TOUCHING -> com.virtuous.designsystem.R.drawable.touching_ic
            Emotion.IMPRESSIVE -> com.virtuous.designsystem.R.drawable.impressive_ic
            Emotion.GRATEFUL -> com.virtuous.designsystem.R.drawable.grateful_ic
            else -> null
        }

        return emotionResId?.let {
            BitmapFactory.decodeResource(context.resources, it)
        }
    }

    return null
}

