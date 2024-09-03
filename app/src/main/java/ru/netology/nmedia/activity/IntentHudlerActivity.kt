package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.FeedFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityIntentHandlerBinding
import ru.netology.nmedia.viewmodel.AuthViewModel

class IntentHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityIntentHandlerBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //проверяем входящий intent через savecall на случай если там записано null
        intent?.let {
            if (intent.action !== Intent.ACTION_SEND) {
                return@let
            }

            //получаем текст
            val text = it.getStringExtra(Intent.EXTRA_TEXT)

            //проверяем не пуста ли строка
            if (text.isNullOrBlank())
                Snackbar.make(
                    binding.root, //ссылка на корневой view
                    R.string.empty, //сообщение об ошибке
                    Snackbar.LENGTH_INDEFINITE /*константа сколько времени показывать увкдомление*/
                ).
                    //нажимаем ОК и выходим из activity
                setAction(android.R.string.ok) {
                    finish()
                }
                    .show() //иначе snackbar показан не будет

            intent.removeExtra(Intent.EXTRA_TEXT) //убираем текст

            //получаем доступ к навигации активити
            findNavController(R.id.nav_host_fragment).navigate(
                R.id.action_feedFragment_to_newPostFragment, //маршрут, по которому хотим перейти
                Bundle().apply {//аргументы, которые необходимо передать объекту
                    textArg = text
                }
            )
        }

        val viewModel by viewModels<AuthViewModel>()


        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.auth_menu, menu)

                    //подписываем на обновления
                    viewModel.authData.observe(this@IntentHandlerActivity) {
                        //проверяем авторизован пользователь или нет
                        val isAuthenticated = viewModel.isAuthenticated

                        //настраиваем видимость групп меню авторизации в зависимости от того авторизован пользователь или нет
                        menu.setGroupVisible(R.id.authenticated, !isAuthenticated)
                        menu.setGroupVisible(R.id.unauthenticated, isAuthenticated)
                    }
                }

                //при выборе элементов
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.sign_in -> {
                            //TODO homework
                            //переходим в фргамент авторизации
                            findNavController(R.id.nav_host_fragment).navigate(
                                R.id.action_feedFragment_to_authFragment
                            )
                            true
                        }

                        R.id.sign_up -> {
                            findNavController(R.id.nav_host_fragment).navigate(
                                R.id.action_feedFragment_to_registrationFragment
                            )
                            true
                        }

                        R.id.logout -> {
                            AppAuth.getInstanse().clearAuth()
                            true
                        }

                        else -> false
                    }
            }
        )
    }
}

