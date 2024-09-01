package ru.netology.nmedia.repositoryImpl

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.ApiServiceUser
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.ModelPhoto
import ru.netology.nmedia.repository.RegistrationRepository
import java.io.IOException

class RegistrationRepositoryImpl : RegistrationRepository {
    override suspend fun registerUser(login: String, pass: String, name: String, avatar: ModelPhoto) {
        try {
            val response = ApiServiceUser.service.registerWithPhoto(
                login.toRequestBody("text/plain".toMediaType()),
                pass.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                MultipartBody.Part.createFormData("file", avatar.file.name, avatar.file.asRequestBody())
            )

            //если что-то пошло не так
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }

            val answer = response.body() ?: throw RuntimeException("Response body is null")

            AppAuth.getInstanse().setAuth(answer.id, answer.token)

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}