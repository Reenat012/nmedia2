package ru.netology.nmedia.repositoryImpl

import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.api.UserApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.repository.AuthRepository
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: UserApiService,
    private var appAuth: AppAuth
) : AuthRepository {

    override suspend fun auth(login: String, password: String) {
        try {
            val response = apiService.updateUser(login, password)

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