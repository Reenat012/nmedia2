package ru.netology.nmedia.auth

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.netology.nmedia.api.ApiServicePush
import ru.netology.nmedia.api.PushApiService
import ru.netology.nmedia.dto.PushToken
import ru.netology.nmedia.dto.Token
import java.security.spec.ECField

class AppAuth private constructor(context: Context) {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    //хранилище данных
    private val _data = MutableStateFlow<Token?>(null)

    //публичная ссылка на данные
    val data: StateFlow<Token?> = _data.asStateFlow()

    init {
        //считываем id и token
        val id = prefs.getLong(ID_KEY, 0)
        val token = prefs.getString(TOKEN_KEY, null)

        if (id != 0L && token != null) {
            //записываем id и token в хранилище данных _data
            _data.value = Token(id, token)
        } else {
            //чистим preference
            prefs.edit { clear() }
        }

        sendPushToken()
    }

    //сохранить аутентификацию
    @Synchronized //чтобы только один поток мог заходить и обновлять
    fun setAuth(id: Long, token: String) {
        prefs.edit {
            //функция, используемая для сохранения длинного целого числа (пара ключ-значение)
            putLong(ID_KEY, id)
            putString(TOKEN_KEY, token)


            //обновляем хранилище _data
            _data.value = Token(id, token)
        }

        sendPushToken()
    }

    //очистить аутентификацию
    @Synchronized
    fun clearAuth() {
        prefs.edit { clear() }
        _data.value = null

        sendPushToken()
    }

    companion object {
        private const val ID_KEY = "ID_KEY"
        private const val TOKEN_KEY = "TOKEN_KEY"
        private var appAuth: AppAuth? = null

        fun init(context: Context) {
            appAuth = AppAuth(context)
        }

        fun getInstanse() = appAuth ?: error("Need call init(context) before")
    }

    fun sendPushToken(token: String? = null) {
        CoroutineScope(Dispatchers.Default).launch {
            val dto = PushToken(token ?: FirebaseMessaging.getInstance().token.await())

            try {
                ApiServicePush.service.sendPushToken(dto)
            } catch (e: Exception) {
                e.printStackTrace()
                //ignore
            }
        }
    }


}

