package ru.netology.nmedia.activity

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.repository.AuthRepositoryImpl
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.TextCallback
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.LoginViewModel
import kotlin.math.log

class AuthFragment : Fragment(), TextCallback {

    //передаем текст в репозиторий для обработки
    companion object {
        var Bundle.textArg: String? by StringArg

        val authRepositoryImpl: AuthRepositoryImpl = AuthRepositoryImpl()
    }

    private val loginViewModel: LoginViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthBinding.inflate(
            inflater,
            container,
            false
        )

        binding.signInButton.setOnClickListener {
            //пробрасываем логин и пароль во authViewModel
            onLoginReceived(binding.login.text.toString())
            onPasswordReceived(binding.password.text.toString())

            loginViewModel.onLoginTap()

            //переходим обратно в feedFragment
            findNavController().navigate(
                R.id.action_authFragment_to_feedFragment
            )
        }

        return binding.root
    }

    override fun onLoginReceived(login: String) {
        loginViewModel.saveLogin(login)
    }

    override fun onPasswordReceived(password: String) {
        loginViewModel.savePassword(password)
    }


}