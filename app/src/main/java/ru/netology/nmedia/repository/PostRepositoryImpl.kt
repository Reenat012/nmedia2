package ru.netology.nmedia.repository

import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.Post
import ru.netology.nmedia.entity.PostEntity
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS) //30 сек будем ждать вызова клиента
        .build()

    private val gson = Gson()
    private val type = object : TypeToken<List<Post>>() {}.type

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }
    override fun repost(id: Long) {
        TODO("Not yet implemented")
    }

    override fun getAll():List<Post> {
        //запрос на сервер
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts") // /slow для задержки, имитируем реальный сервер
            .build()

        //ответ сервера
        val response = client.newCall(request)
            .execute()

        //получаем тело ответа с сервера
        val responseText = response.body?.string() ?: error("Response body is null")

        //преобразуем в список постов
        return gson.fromJson<List<Post>>(responseText/*откуда читаем*/, type/*во что преобразовываем*/)
    }

    override fun likeById(id: Long) {
        TODO()
    }

    override fun save(post: Post) {
        TODO()
    }

    override fun openPostById(id: Long): Post {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        TODO()
    }
}