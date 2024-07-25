package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError

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
        val response = ApiService.service.likeById(id)
        //если что-то пошло не так
        if (!response.isSuccessful) {
            throw RuntimeException(response.message())
        }

        //если все хорошо
        val post = response.body() ?: throw RuntimeException("Response body is null")

        postDao.insert(PostEntity.fromDto(post))

        return post



//        val entities = posts.map {
//            PostEntity.fromDto(it)}
//
//        //записываем posts в базу данных
//        postDao.insert(entities)
    }

    override suspend fun disLikeByIdAsync(id: Long): Post {
        val response = ApiService.service.dislikeById(id)
        //если что-то пошло не так
        if (!response.isSuccessful) {
            throw RuntimeException(response.message())
        }

        //если все хорошо
        val post = response.body() ?: throw RuntimeException("Response body is null")

        postDao.insert(PostEntity.fromDto(post))

        return post
    }

    override suspend fun removeByIdAsync(id: Long) {
        val response = ApiService.service.removeById(id)
        //если что-то пошло не так
        if (!response.isSuccessful) {
            throw RuntimeException(response.message())
        }

        //если все хорошо
        val post = response.body() ?: throw RuntimeException("Response body is null")

        postDao.insert(PostEntity.fromDto(post))
    }

    override suspend fun saveAsync(post: Post): Post {
        val response = ApiService.service.savePost(post)

        if (!response.isSuccessful) {
            throw RuntimeException(response.message())
        }

        val body = response.body() ?: throw ApiError(response.code(), response.message())
        postDao.insert(PostEntity.fromDto(body))

        return body
    }

}
//    override fun getAll(): List<Post> {
//        return ApiService.service.getAll()
//            .execute()
//            .let { it.body() ?: throw RuntimeException("body is null") }
//    }


