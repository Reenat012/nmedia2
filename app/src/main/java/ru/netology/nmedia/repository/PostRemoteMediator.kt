package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import okhttp3.internal.http.hasBody
import retrofit2.HttpException
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: PostApiService,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb
) : RemoteMediator<Int, PostEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val result = when (loadType) {
                LoadType.REFRESH -> {
                    val count = postDao.count()
                    if (count == 0) {
                        // Обновляем ключ BEFORE до текущего минимального значения
                        val minId = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                        apiService.getBefore(minId, state.config.pageSize)
                    } else {
                        // Если база данных не пуста, не обновляем ключ BEFORE
                        return MediatorResult.Success(true)
                    }

//                    apiService.getLatest(state.config.pageSize)
                }

                //скролл вниз
                //postRemoteKeyDao.min() загружает самый старый пост из БД
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getBefore(id, state.config.pageSize)
                }
                //когда пользовател скроллит вверх, у него не будет загружаться новая страница
                ////postRemoteKeyDao.min() загружает самый новый пост из БД
                LoadType.PREPEND -> {
                    // Отключаем автоматический PREPEND
                    return MediatorResult.Success(true)

//                    val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(false)
//                    apiService.getAfter(id, state.config.pageSize)
                }
            }

            if (!result.isSuccessful) {
                throw ApiError(result.code(), result.message())
            }

            val data = result.body().orEmpty()

            appDb.withTransaction {
                //заполняем таблицу ключей данными, которые приходят по сети
                //для этого узнаем какой был тип входных данных
                when (loadType) {
                    LoadType.REFRESH -> {
                        //очищаем таблицу с постами
                        //по условию дз очищать таблицу не нужно
//                        postDao.clear()

                        // и записывем оба ключа
                        postRemoteKeyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    //берем самый первый пост из пришедшего списка
                                    PostRemoteKeyEntity.KeyType.AFTER,
                                    data.first().id
                                ),

                                PostRemoteKeyEntity(
                                    //берем самый первый пост из пришедшего списка
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    data.last().id
                                )
                            )
                        )
                    }

                    LoadType.PREPEND -> {
                        //скролл вверх
                        //записываем только ключ after
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                //берем самый первый пост из пришедшего списка
                                PostRemoteKeyEntity.KeyType.AFTER,
                                data.first().id
                            )
                        )
                    }

                    LoadType.APPEND -> {
                        //скролл вниз
                        //записываем только ключ before
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                //берем самый первый пост из пришедшего списка
                                PostRemoteKeyEntity.KeyType.BEFORE,
                                data.last().id
                            )
                        )
                    }
                }

                postDao.insert(data.map { PostEntity.fromDto(it) })

            }

            return MediatorResult.Success(data.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}