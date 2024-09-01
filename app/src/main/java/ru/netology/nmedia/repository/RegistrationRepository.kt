package ru.netology.nmedia.repository

import ru.netology.nmedia.model.ModelPhoto

interface RegistrationRepository {
    suspend fun registerUser(login: String, pass: String, name: String, avatar: ModelPhoto)
}