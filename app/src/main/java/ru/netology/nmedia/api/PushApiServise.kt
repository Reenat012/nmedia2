package ru.netology.nmedia.api

import retrofit2.http.Body
import retrofit2.http.POST
import ru.netology.nmedia.dto.PushToken

interface PushApiService {
    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body token: PushToken)
}


