package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.AuthRepository
import ru.netology.nmedia.repository.AuthRepositoryImpl

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()

    //переменные для хранения логина и пароля
    lateinit var login: String
    lateinit var password: String

    //сохраняем логин и пароль из фрагмента авторизации
    fun saveLogin(text: String) {
        login = text
    }

    fun savePassword(text: String) {
        password = text
    }

    fun onLoginTap() {
        viewModelScope.launch {

            FeedModelState(loading = true)

            try {
                authRepository.auth(login, password)

                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }
}