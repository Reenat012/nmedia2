package ru.netology.nmedia.repositoryImpl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.Attachment
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.ModelPhoto
import ru.netology.nmedia.repository.PostRemoteMediator
import ru.netology.nmedia.repository.PostRepository
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val apiService: PostApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb
) : PostRepository {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }

    //подписка на локальную БД с видимыми постами
    @OptIn(ExperimentalPagingApi::class)
    override val data = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            postDao.getPagingSourse()
        },
        remoteMediator = PostRemoteMediator(
            apiService = apiService,
            postDao = postDao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb
        )
    ).flow
        .map {
            it.map(PostEntity::toDto)
        }
//        postDao.getAllVisible().map { it.map(PostEntity::toDto) }

    override fun repost(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
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
                val postsResponse = apiService.getNewer(newerId)

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
        postDao.getPosts()
    }

    override suspend fun getHiddenCount(): Flow<Int> {
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
            val response = apiService.likeById(id)

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
            val response = apiService.dislikeById(id)

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
            val response = apiService.removeById(id)

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
            val response = apiService.savePost(post)

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

    override suspend fun saveWithAttachment(post: Post, photo: ModelPhoto) {
        //загружаем фото на сервер
        val media = upload(photo)

        //сохраняем пост с Attachment
        val postWithAttachment = try {
            post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }

        //отправляем пост на сервер
        saveAsync(postWithAttachment)
    }

    private suspend fun upload(photo: ModelPhoto): Media {
        val uploadResponse = apiService.upload(
            MultipartBody.Part.createFormData("file", photo.file.name, photo.file.asRequestBody())
        )

        //проверяем на соответствие ожидаемому
        if (!uploadResponse.isSuccessful) {
            throw RuntimeException(uploadResponse.message())
        }

        //елси все хорошо, возвращаем тело запроса
        return uploadResponse.body() ?: throw ApiError(
            uploadResponse.code(),
            uploadResponse.message()
        )
    }

}



