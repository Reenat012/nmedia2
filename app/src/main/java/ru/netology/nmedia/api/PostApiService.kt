package ru.netology.nmedia.api



import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.netology.nmedia.Post
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.User
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"

private val client = OkHttpClient.Builder()
    .callTimeout(10, TimeUnit.SECONDS) //30 сек будем ждать вызова клиента
    .addInterceptor { chain ->
        chain.proceed(
            //либо у нас есть токен и создается билдер с новым заголовком
            AppAuth.getInstanse().data.value?.token?.let {
                chain.request().newBuilder()
                    .addHeader("Authorization", it)
                    .build()
            }
                //либо заголовок остается неизменным
                ?: chain.request()
        )
    }
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .baseUrl(BASE_URL)
    .build()

interface PostApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @POST("posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part file: MultipartBody.Part) : Response<Media>
}

object ApiService {
    val service by lazy {
        retrofit.create<PostApiService>()
    }
}