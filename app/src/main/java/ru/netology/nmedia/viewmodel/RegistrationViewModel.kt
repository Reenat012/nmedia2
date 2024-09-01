package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.ModelPhoto
import ru.netology.nmedia.repositoryImpl.AuthRepositoryImpl
import ru.netology.nmedia.repositoryImpl.RegistrationRepositoryImpl
import java.io.File

class RegistrationViewModel : ViewModel() {
    private val registerRepository: RegistrationRepositoryImpl = RegistrationRepositoryImpl()

    lateinit var name: String
    lateinit var login: String
    lateinit var password: String
    lateinit var retryPassword: String

    //сохраняем фото в переменную
    private val _photo = MutableLiveData<ModelPhoto?>(null)
    val photo: LiveData<ModelPhoto?>
        get() = _photo

    fun setPhoto(uri: Uri, file: File) {
        _photo.value = ModelPhoto(uri, file)
    }

    fun clearPhoto() {
        _photo.value = null
    }

    fun saveName(textName: String) {
        name = textName
    }

    fun saveLogin(textLogin: String) {
        login = textLogin
    }

    fun savePassword(textPassword: String) {
        password = textPassword
    }

    fun saveRetryPassword(textRetryPassword: String) {
        retryPassword = textRetryPassword
    }

    //проверка на заполненность всех полей регистрации
    fun checkFieldRegister() : Boolean {
        return ((name != null && name != "")
                && (login != null && login != "")
                && (password != null && password != "")
                &&(retryPassword != null && retryPassword != ""))
    }

    //проверка на совпадение password и retryPassword
    fun checkPassword() : Boolean {
        return password == retryPassword
    }

    fun onSignUpTap() {
        viewModelScope.launch {

            FeedModelState(loading = true)

            try {
                photo.value?.let {
                    registerRepository.registerUser(login, password, name, it)
                }


                FeedModelState()
            } catch (e: Exception) {
                FeedModelState(error = true)
            }
        }
    }
}