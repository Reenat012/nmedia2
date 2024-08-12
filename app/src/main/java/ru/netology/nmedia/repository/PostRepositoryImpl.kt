package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import kotlin.time.Duration.Companion.seconds

class PostRepositoryImpl(
    private val postDao: PostDao
) : PostRepository {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }

    //подписка на локальную БД с видимыми постами
    override val data = postDao.getAllVisible().map { it.map(PostEntity::toDto) }

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

    override fun getNewer(newerId: Long): Flow<Int> = flow {
        while (true) {
            //замедляем процесс на 10 секунд
            delay(10.seconds)

            try {//получаем вновь сгенерированные посты
                val postsResponse = ApiService.service.getNewer(newerId)

                //пробуем взять тело постов
                val posts = postsResponse.body().orEmpty()

                //выбрасываем количество новых сообщений
                emit(posts.size)

                //вписываем сгенирированный список постов в локальную  БД
                //полученные посты невидимы, когда записаны в базу
                postDao.insert(posts.toEntity(hidden = true))

            } //при попытке отменить flow
            catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                //ignore
            }
        }
    }

    override fun getAllVisible() {
        //если ответ от сервера приходит не 0, значит появились новые посты
        //получаем посты только с локальной БД
        postDao.getAllVisible()
        }

    override suspend fun getHiddenCount() : Flow<Int> {
        //получаем количество скрытых постов
        return postDao.getHiddenCount()
    }

    override suspend fun changeHiddenPosts() {
        postDao.changeHiddenPosts()
    }

    override suspend fun likeByIdAsync(id: Long): Post {
        try {
            //модифицируем запись в локальной БД
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



