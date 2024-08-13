package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.PostApiService

interface PostRepository {
    //подписка на посты
    val data: Flow<List<Post>>
    fun repost(id: Long)
    suspend fun getAll()
    fun getNewer(newerId: Long) : Flow<Int>
    fun getAllVisible()
    suspend fun getHiddenCount() : Flow<Int>
    suspend fun changeHiddenPosts()
    suspend fun likeByIdAsync(id: Long) : Post
    suspend fun disLikeByIdAsync(id: Long) : Post
//  fun removeById(id: Long)
    suspend fun removeByIdAsync(id: Long)
//    fun save(post: Post): Post
    suspend fun saveAsync(post: Post) : Post
//    fun openPostById(id: Long): Post
}

