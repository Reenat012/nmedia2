package ru.netology.nmedia.api



import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.create
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.Post
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://10.0.2.2:9999/pi/slow/"

private val client = OkHttpClient.Builder()
    .callTimeout(10, TimeUnit.SECONDS) //30 сек будем ждать вызова клиента
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .baseUrl(BASE_URL)
    .build()

interface PostApiService {
    @GET("posts")
    fun getAll(): Call<List<Post>>

    @POST("posts")
    fun savePost(post: Post): Call<Post>

    @DELETE("posts/{id}")
    fun removeById(@Path("id") id: Long): Call<Post>

    @DELETE("posts/{id}/likes")
    fun dislikeById(@Path("id") id: Long): Call<Post>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id: Long): Call<Post>
}

object ApiService {
    val service by lazy {
        retrofit.create<PostApiService>()
    }
}