package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.IntentHandlerActivity
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {
    private val channelId = "server"
    private val action = "action"
    private val content = "content"
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data[action]?.let {
            when (Action.valueOf(it)) {
                Action.LIKE -> handleLike(Gson().fromJson(message.data[content], Like::class.java))
                Action.SAVE_POST -> handleSavePost(Gson().fromJson(message.data[content], Post::class.java))
            }
        }
    }

    private fun handleLike(like: Like) {

        //интент на переход в активити по клику на уведомление
        val intent = Intent(this, IntentHandlerActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            -1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_netology_foreground)
            .setContentText(
                getString(
                    R.string.notification_user_liked,
                    like.userName,
                    like.postAuthor
                )
            )
            .setContentIntent(pendingIntent) //при клике на уведомление будет переход в активити
            .setAutoCancel(true)//закрытие уведомления после клика
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)//вызвать показ уведомления
        }
    }

    private fun handleSavePost(post: Post) {

        //интент на переход в активити по клику на уведомление
        val intent = Intent(this, IntentHandlerActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            -1,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_netology_foreground)
            .setContentTitle(
                getString(R.string.notification_user_save_post, post.author)
            )
            .setContentText(
                getString(
                    R.string.notification_user_content_post,
                    post.content
                )
            )
            .setContentIntent(pendingIntent) //при клике на уведомление будет переход в активити
            .setAutoCancel(true)//закрытие уведомления после клика
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getString(R.string.content)))
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification)//вызвать показ уведомления
        }
    }


    override fun onNewToken(token: String) {
        println(token)
    }
}

//класс из сервера
enum class Action {
    LIKE, SAVE_POST
}

//класс из сервера
data class Like(
    val userId: Int,
    val userName: String,
    val authorId: Int,
    val postAuthor: String
)