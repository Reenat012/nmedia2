package ru.netology.nmedia.repositoryImpl

import ru.netology.nmedia.api.ApiServiceUser
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.repository.AuthRepository
import java.io.IOException

class AuthRepositoryImpl : AuthRepository {

    override suspend fun auth(login: String, password: String) {
        try {
            val response = ApiServiceUser.service.updateUser(login, password)

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