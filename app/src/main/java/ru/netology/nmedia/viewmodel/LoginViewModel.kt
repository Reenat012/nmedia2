package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.UserApiService
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repositoryImpl.AuthRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepositoryImpl) : ViewModel() {
    //переменные для хранения логина и пароля
    lateinit var login: String
    lateinit var password: String

    private val _progressRegister = SingleLiveEvent<Unit>()
    val progressRegister: SingleLiveEvent<Unit> = _progressRegister

    private val _errorEvent = SingleLiveEvent<String>()
    val errorEvent: SingleLiveEvent<String> = _errorEvent

    //сохраняем логин и пароль из фрагмента авторизации
    fun saveLogin(text: String) {
        login = text
    }

    fun savePassword(text: String) {
        password = text
    }

    fun onLoginTap() {
        viewModelScope.launch {
            _progressRegister.postValue(Unit)
            try {
                authRepository.auth(login, password)
            } catch (e: Exception) {
                _errorEvent.postValue("Error")
            }
        }
    }
}