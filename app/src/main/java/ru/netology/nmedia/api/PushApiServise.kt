package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token
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

interface PushApiService {
    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken)
}

object ApiServicePush {
    val service by lazy {
        retrofit.create<PushApiService>()
    }
}

