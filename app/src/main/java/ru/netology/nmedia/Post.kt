package ru.netology.nmedia

import android.net.Uri
import android.provider.MediaStore.Video

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val reposts: Int = 0,
    val views: Int = 0,
    val countReposts: Int = 0,
    val video: Uri? = null //необязательное поле, если пользователь добавит ссылку на видео
)