package ru.netology.nmedia

import android.net.Uri
import ru.netology.nmedia.enumeration.AttachmentType

data class Post(
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorAvatar: String = "http://10.0.2.2:9999/avatars/netology.jpg",
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val reposts: Int = 0,
    val views: Int = 0,
    val countReposts: Int = 0,
    val video: String? = null, //необязательное поле, если пользователь добавит ссылку на видео
    var attachment: Attachment? = null,
    val photoPost: String? = null,
    val ownedByMe: Boolean = false
)

data class Attachment(
    val url: String,
    val type: AttachmentType,
)