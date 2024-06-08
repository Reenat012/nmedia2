package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.Post

interface PostRepository {
    fun repost(id: Long)
    fun getPost(id: Long): Post
    fun getAll(): List<Post>
    fun likeById(id: Long): Post
    fun removeById(id: Long)
    fun save(post: Post): Post
    fun openPostById(id: Long): Post
}

