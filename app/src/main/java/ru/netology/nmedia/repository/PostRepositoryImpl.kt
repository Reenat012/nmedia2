package ru.netology.nmedia.repository

import androidx.lifecycle.map
import androidx.room.util.copy
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.Post
import ru.netology.nmedia.entity.PostEntity
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS) //30 сек будем ждать вызова клиента
        .build()

    private val gson = Gson()
    private val type = object : TypeToken<List<Post>>() {}.type
    private val jsonType = "application/json".toMediaType()

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }

    override fun repost(id: Long) {
        TODO("Not yet implemented")
    }

    override fun getPost(id: Long): Post {
        val request = Request.Builder()
            .url("${BASE_URL}api/slow/posts/$id")
            .build()

        val response = client.newCall(request)
            .execute()

        val responseText = response.body?.string() ?: error("Response body is null")

        //преобразуем в список постов
        return gson.fromJson(responseText/*откуда читаем*/, Post::class.java/*во что преобразовываем*/)
    }

    override fun getAll(): List<Post> {
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
        return gson.fromJson(responseText/*откуда читаем*/, type/*во что преобразовываем*/)
    }

    override fun likeById(id: Long): Post {
        val post = getPost(id)

        //запрос на сервер
        val requestLike = Request.Builder()
            .url("${BASE_URL}api/posts/$id/likes") // /slow для задержки, имитируем реальный сервер
            .post("${BASE_URL}api/posts/$id/likes".toRequestBody())
            .build()

        val requestDislike = Request.Builder()
            .url("${BASE_URL}api/posts/$id/likes") // /slow для задержки, имитируем реальный сервер
            .delete("${BASE_URL}api/posts/$id/likes".toRequestBody())
            .build()

        //ответ сервера
        val response = client.newCall(
            if (!post.likedByMe) requestLike else requestDislike
        )
            .execute()

        //получаем тело ответа с сервера
        val responseText = response.body?.string() ?: error("Response body is null")

        //преобразуем в список постов
        return gson.fromJson(
            responseText/*откуда читаем*/,
            Post::class.java/*во что преобразовываем*/
        )
    }

    override fun save(post: Post): Post {
        //запрос на сервер
        val request = Request.Builder()
            .url("${BASE_URL}api/posts") // /slow для задержки, имитируем реальный сервер
            .post(gson.toJson(post).toRequestBody(jsonType)) //отправляем пост на сервер в ввиде json
            .build()

        //ответ сервера
        val response = client.newCall(request)
            .execute()

        //получаем тело ответа с сервера
        val responseText = response.body?.string() ?: error("Response body is null")

        //преобразуем в список постов
        return gson.fromJson(
            responseText/*откуда читаем*/,
            Post::class.java/*во что преобразовываем*/
        )
    }

    override fun openPostById(id: Long): Post {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()


        //ответ сервера
        val response = client.newCall(request)
            .execute()

        //получаем тело ответа с сервера
        val responseText = response.body?.string() ?: error("Response body is null")
    }
}