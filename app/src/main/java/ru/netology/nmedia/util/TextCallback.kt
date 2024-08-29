package ru.netology.nmedia.util

//передача текста из фрагмента в репозиторий для обработки
interface TextCallback {
    fun onLoginReceived(text: String)
    fun onPasswordReceived(text: String)
}