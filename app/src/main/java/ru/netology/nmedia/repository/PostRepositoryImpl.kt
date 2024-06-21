package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import ru.netology.nmedia.Post
import ru.netology.nmedia.api.ApiService

class PostRepositoryImpl : PostRepository {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999/"
    }

    override fun repost(id: Long) {
        TODO("Not yet implemented")
    }


    override fun getAll(): List<Post> {
        return ApiService.service.getAll()
            .execute()
            .let { it.body() ?: throw RuntimeException("body is null") }
    }

    override fun getAllAsync(callback: PostRepository.NmediaAllCallback<List<Post>>) {
        //ответ сервера
        ApiService.service
            .getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(
                    call: Call<List<Post>>,
                    response: retrofit2.Response<List<Post>>
                ) {
                    if (response.isSuccessful) {
                        callback.error(RuntimeException(response.message()))
                        return
                    }
                    callback.onSuccess(
                        response.body() ?: throw RuntimeException("body is null"))
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    //преобразовываем базовый throwable к exception
                    callback.error(Exception(t))
                }
            })
    }

//    override fun likeById(id: Long): Post {
//        val post = getPost(id)
//
//        //запрос на сервер
//        val requestLike = Request.Builder()
//            .url("${BASE_URL}api/posts/$id/likes") // /slow для задержки, имитируем реальный сервер
//            .post("${BASE_URL}api/posts/$id/likes".toRequestBody())
//            .build()
//
//        val requestDislike = Request.Builder()
//            .url("${BASE_URL}api/posts/$id/likes") // /slow для задержки, имитируем реальный сервер
//            .delete("${BASE_URL}api/posts/$id/likes".toRequestBody())
//            .build()
//
//        //ответ сервера
//        val response = client.newCall(
//            if (!post.likedByMe) requestLike else requestDislike
//        )
//            .execute()
//
//        //получаем тело ответа с сервера
//        val responseText = response.body?.string() ?: error("Response body is null")
//
//        //преобразуем в список постов
//        return gson.fromJson(
//            responseText/*откуда читаем*/,
//            Post::class.java/*во что преобразовываем*/
//        )
//    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.NmediaAllCallback<Post>) {
        ApiService.service
            .likeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    callback.onSuccess(response.body() ?: throw Exception("Body is null"))
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.error(Exception(t))
                }
            })

//        //запрос на сервер
//        val requestLike = Request.Builder()
//            .url("${BASE_URL}api/posts/$id/likes") // /slow для задержки, имитируем реальный сервер
//            .post("${BASE_URL}api/posts/$id/likes".toRequestBody())
//            .build()
//
//        client.newCall(requestLike)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.error(e)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    try {
//                        callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
//                    } catch (e: Exception) {
//                        callback.error(e)
//                    }
//                }
//            })
    }

    override fun disLikeByIdAsync(id: Long, callback: PostRepository.NmediaAllCallback<Post>) {
        ApiService.service.dislikeById(id)
            .enqueue(object :Callback<Post>{
                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    callback.onSuccess(response.body() ?: throw Exception("Body is null"))
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.error(Exception(t))
                }

            })

//        //запрос на сервер
//        val requestDisLike = Request.Builder()
//            .url("${BASE_URL}api/posts/$id/likes") // /slow для задержки, имитируем реальный сервер
//            .delete("${BASE_URL}api/posts/$id/likes".toRequestBody())
//            .build()
//
//        client.newCall(requestDisLike)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.error(e)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    try {
//                        callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
//                    } catch (e: Exception) {
//                        callback.error(e)
//                    }
//                }
//            })
    }

    override fun save(post: Post): Post {
        return ApiService.service.savePost(post)
            .execute()
            .let { it.body() ?: throw Exception("Body is null") }
    }

    override fun saveAsync(post: Post, callback: PostRepository.NmediaAllCallback<Post>) {
        ApiService.service.savePost(post)
            .enqueue(object :Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    callback.onSuccess(response.body() ?: throw Exception("Body is null"))
                }

                override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                    callback.error(Exception(t))
                }

//            })
//        //запрос на сервер
//        val request = Request.Builder()
//            .url("${BASE_URL}api/posts") // /slow для задержки, имитируем реальный сервер
//            .post(
//                gson.toJson(post).toRequestBody(jsonType)
//            ) //отправляем пост на сервер в ввиде json
//            .build()
//
//        //ответ сервера
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.error(e)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    try {
//                        callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
//                    } catch (e: Exception) {
//                        callback.error(e)
//                    }
//                }
//            })
    })
    }

    override fun openPostById(id: Long): Post {
        TODO("Not yet implemented")
    }

    override fun removeById(id: Long) {
        ApiService.service.removeById(id)
            .execute()
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.NmediaAllCallback<Post>) {
        ApiService.service.removeById(id)
            .enqueue(object :Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post>,
                    response: retrofit2.Response<Post>
                ) {
                    callback.onSuccess(response.body() ?: throw Exception("Body is null"))
                }

                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.error(Exception(t))
                }
            })

//        //ответ сервера
//        client.newCall(request)
//            .enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    callback.error(e)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    try {
//                        callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
//                    } catch (e: Exception) {
//                        callback.error(e)
//                    }
//                }
//            })
    }
}