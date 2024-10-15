//package ru.netology.nmedia.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewmodel.CreationExtras
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.repository.PostRepository
//import ru.netology.nmedia.repository.RegistrationRepository
//import ru.netology.nmedia.repositoryImpl.AuthRepositoryImpl
//import ru.netology.nmedia.repositoryImpl.RegistrationRepositoryImpl
//
//class ViewModelFactory(
//    private val postRepository: PostRepository,
//    private val appAuth: AppAuth,
//    private val registrationRepository: RegistrationRepositoryImpl,
//    private val authRepositoryImpl: AuthRepositoryImpl
//) : ViewModelProvider.Factory {
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T =
//        when {
//            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
//                PostViewModel(postRepository, appAuth) as T
//            }
//
//            modelClass.isAssignableFrom(RegistrationViewModel::class.java) -> {
//                RegistrationViewModel(registrationRepository) as T
//            }
//
//            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
//                AuthViewModel(appAuth) as T
//            }
//
//            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
//                LoginViewModel(authRepositoryImpl) as T
//            }
//
//            else -> error("Unknow class: $modelClass")
//        }
//}
