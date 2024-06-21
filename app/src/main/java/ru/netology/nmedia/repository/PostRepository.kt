package ru.netology.nmedia.repository

import ru.netology.nmedia.Post

interface PostRepository {
    fun repost(id: Long)
    fun getAll() : List<Post>
    fun getAllAsync(callback: NmediaAllCallback<List<Post>>)
    fun likeByIdAsync(id: Long, callback: NmediaAllCallback<Post>)
    fun disLikeByIdAsync(id: Long, callback: NmediaAllCallback<Post>)
    fun removeById(id: Long)
    fun removeByIdAsync(id: Long, callback: NmediaAllCallback<Post>)
    fun save(post: Post): Post
    fun saveAsync(post: Post, callback: NmediaAllCallback<Post>)
    fun openPostById(id: Long): Post

    interface NmediaAllCallback<T>{
        fun onSuccess(data: T)
        fun error(e: Exception)
    }
}

