package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryImpl(
    private val postDao: PostDao
) : PostRepository {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }

    override val data: LiveData<List<Post>> = postDao.getAll().map { it.map(PostEntity::toDto) }

    override fun repost(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getAll() {
        val response = ApiService.service.getAll()
        //если что-то пошло не так
        if (!response.isSuccessful) {
            throw RuntimeException(response.message())
        }

        //если все хорошо
        val posts = response.body() ?: throw RuntimeException("Response body is null")

        val entities = posts.map {
            PostEntity.fromDto(it)}

        //записываем posts в базу данных
        postDao.insert(entities)
    }

    override suspend fun likeByIdAsync(id: Long): Post {
        TODO("Not yet implemented")
    }

    override suspend fun disLikeByIdAsync(id: Long): Post {
        TODO("Not yet implemented")
    }

    override suspend fun removeByIdAsync(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun saveAsync(post: Post): Post {
        TODO("Not yet implemented")
    }

}
//    override fun getAll(): List<Post> {
//        return ApiService.service.getAll()
//            .execute()
//            .let { it.body() ?: throw RuntimeException("body is null") }
//    }


