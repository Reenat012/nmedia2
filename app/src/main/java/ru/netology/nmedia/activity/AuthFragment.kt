package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.TextCallback
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.LoginViewModel

@AndroidEntryPoint
class AuthFragment : Fragment(), TextCallback {

    //передаем текст в репозиторий для обработки
    companion object {
        var Bundle.textArg: String? by StringArg
        
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
        }

        authViewModel.authData.observe(viewLifecycleOwner) { // <---
            if (it!= null) {
                binding.progressBar.visibility = View.GONE
                //переходим обратно в feedFragment
                findNavController().navigate(
                    R.id.action_authFragment_to_feedFragment
                )
            }
        }

        loginViewModel.progressRegister.observe(viewLifecycleOwner) {
                binding.progressBar.isVisible = it != null

        }

        loginViewModel.errorEvent.observe(viewLifecycleOwner) {
            if (it != null) {
                Snackbar.make(binding.root, R.string.network_error, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        return binding.root
    }

    override fun onLoginReceived(login: String) {
        loginViewModel.saveLogin(login)
    }

    override fun onPasswordReceived(password: String) {
        loginViewModel.savePassword(password)
    }

    override fun onRetryPasswordReceived(text: String) {
    }

    override fun onNameReceived(text: String) {
    }


}