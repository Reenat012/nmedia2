package ru.netology.nmedia.repository

interface AuthRepository {
    suspend fun auth(login: String, password: String)
}