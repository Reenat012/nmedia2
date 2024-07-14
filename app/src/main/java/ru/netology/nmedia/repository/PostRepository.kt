package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.Post

interface PostRepository {
    val data: LiveData<List<Post>>
    fun repost(id: Long)
//    fun getAll() : List<Post>
    suspend fun getAll()
    suspend fun likeByIdAsync(id: Long) : Post
    suspend fun disLikeByIdAsync(id: Long) : Post
//  fun removeById(id: Long)
    suspend fun removeByIdAsync(id: Long)
//    fun save(post: Post): Post
    suspend fun saveAsync(post: Post) : Post
//    fun openPostById(id: Long): Post
}

