package ru.netology.nmedia.repositoryImpl

import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.api.UserApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.model.ModelPhoto
import ru.netology.nmedia.repository.RegistrationRepository
import java.io.IOException
import javax.inject.Inject

class RegistrationRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private val appAuth: AppAuth) : RegistrationRepository {

    override suspend fun registerUser(login: String, pass: String, name: String) {
        try {
            val response = apiService.registerUser(login, pass, name)

            //если что-то пошло не так
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }

            val answer = response.body() ?: throw RuntimeException("Response body is null")

            appAuth.setAuth(answer.id, answer.token)

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerUserWithPhoto(
        login: String,
        pass: String,
        name: String,
        avatar: ModelPhoto
    ) {
        try {
            val response = apiService.registerWithPhoto(
                login.toRequestBody("text/plain".toMediaType()),
                pass.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                MultipartBody.Part.createFormData(
                    "file",
                    avatar.file.name,
                    avatar.file.asRequestBody()
                )
            )

            //если что-то пошло не так
            if (!response.isSuccessful) {
                throw RuntimeException(response.message())
            }

            val answer = response.body() ?: throw RuntimeException("Response body is null")

            appAuth.setAuth(answer.id, answer.token)

        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}