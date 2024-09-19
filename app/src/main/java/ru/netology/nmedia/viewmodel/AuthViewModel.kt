package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token

class AuthViewModel : ViewModel() {
    //данные авторизации
    val authData: LiveData<Token?> = AppAuth.getInstanse()
        .data.asLiveData()

    //проверяем авторизован пользователь или нет
    val isAuthenticated: Boolean
        get() = authData.value?.token != null
}