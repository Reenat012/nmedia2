package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class PostRepositoryImpl(
    private val postDao: PostDao
) : PostRepository {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }

    //подписка на локальную БД
    override val data: LiveData<List<Post>> = postDao.getAll().map { it.map(PostEntity::toDto) }

    override fun repost(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getAll() {
        try {
            val response = ApiService.service.getAll()
            //если что-то пошло не так
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }

            //если все хорошо
            val posts = response.body() ?: throw RuntimeException("Response body is null")

            val entities = posts.map {
                PostEntity.fromDto(it)
            }

            //записываем posts в базу данных
            postDao.insert(entities)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeByIdAsync(id: Long): Post {
        try {        //модифицируем запись в локальной БД
            postDao.likeById(id)

            //отправляем запрос
            val response = ApiService.service.likeById(id)

            //если что-то пошло не так
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }

            //если все хорошо
            val post = response.body() ?: throw RuntimeException("Response body is null")

            return post
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun disLikeByIdAsync(id: Long): Post {
        try {
            //модифицируем запись в локальной БД
            postDao.likeById(id)

            //отправляем запрос
            val response = ApiService.service.dislikeById(id)

            //если что-то пошло не так
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }

            //если все хорошо
            val post = response.body() ?: throw RuntimeException("Response body is null")

            return post
        } catch (e: IOException) {
            postDao.likeById(id)
            throw NetworkError
        } catch (e: Exception) {
            postDao.likeById(id)
            throw UnknownError
        }
    }

    override suspend fun removeByIdAsync(id: Long) {
        try {
            //удаляем запись из локальной БД
            postDao.removeById(id)

            //отправляем запрос на удаление на сервер
            val response = ApiService.service.removeById(id)

            //если что-то пошло не так
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }
            //сервер нам ничего не вернет, поэтому и вставлять в post нечего
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }




//        val response = ApiService.service.removeById(id)
//        //если что-то пошло не так
//        if (!response.isSuccessful) {
//            throw RuntimeException(response.message())
//        }
//
//        //если все хорошо
//        val post = response.body() ?: throw RuntimeException("Response body is null")
//
//        postDao.insert(PostEntity.fromDto(post))
        //
    }

    override suspend fun saveAsync(post: Post): Post {
        //запись добавляется без текста
        //кнопка сохранить пост не закрывает активити
        try {
            val response = ApiService.service.savePost(post)

            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))

            return body
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}



