package ru.netology.nmedia

data class Post(
    val id: Long,
    val author: String,
    val authorAvatar: String = "http://10.0.2.2:9999/avatars/netology.jpg",
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val reposts: Int = 0,
    val views: Int = 0,
    val countReposts: Int = 0,
    val video: String? = null //необязательное поле, если пользователь добавит ссылку на видео
)