package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Token
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val appAuth: AppAuth) : ViewModel() {
    //данные авторизации
    val authData: LiveData<Token?> = appAuth
        .data.asLiveData()

    //проверяем авторизован пользователь или нет
    val isAuthenticated: Boolean
        get() = authData.value?.token != null
}