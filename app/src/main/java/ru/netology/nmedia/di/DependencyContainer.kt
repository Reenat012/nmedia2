//package ru.netology.nmedia.di
//
//import android.content.Context
//import androidx.room.Room
//import okhttp3.OkHttpClient
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.create
//import ru.netology.nmedia.api.PostApiService
//import ru.netology.nmedia.api.PushApiService
//import ru.netology.nmedia.api.UserApiService
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.db.AppDb
//import ru.netology.nmedia.repository.AuthRepository
//import ru.netology.nmedia.repository.PostRepository
//import ru.netology.nmedia.repositoryImpl.AuthRepositoryImpl
//import ru.netology.nmedia.repositoryImpl.PostRepositoryImpl
//import ru.netology.nmedia.repositoryImpl.RegistrationRepositoryImpl
//import java.util.concurrent.TimeUnit
//
//
//class DependencyContainer(context: Context) {
//    val appDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
//    .fallbackToDestructiveMigration()
//    .build()
//
//    val appAuth = AppAuth(context)
//
//
//    companion object {
//        @Volatile
//        private var instance: DependencyContainer? = null
//
//        fun initApp(context: Context) {
//            instance = DependencyContainer(context)
//        }
//
//        fun getInstance(): DependencyContainer {
//            return instance!!
//        }
//
//        private const val BASE_URL = "http://10.0.2.2:9999/api/slow/"
//    }
//
//
//    private val client = OkHttpClient.Builder()
//        .callTimeout(10, TimeUnit.SECONDS) //30 сек будем ждать вызова клиента
//        .addInterceptor { chain ->
//            chain.proceed(
//                //либо у нас есть токен и создается билдер с новым заголовком
//                appAuth.data.value?.token?.let {
//                    chain.request().newBuilder()
//                        .addHeader("Authorization", it)
//                        .build()
//                }
//                //либо заголовок остается неизменным
//                    ?: chain.request()
//            )
//        }
//        .build()
//
//    private val retrofit = Retrofit.Builder()
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(client)
//        .baseUrl(BASE_URL)
//        .build()
//
//
//    private val posDao = appDb.postDao()
//    private val postApiService = retrofit.create<PostApiService>()
//    private val userApiService = retrofit.create<UserApiService>()
//    val pushApiService = retrofit.create<PushApiService>()
//
//    val postRepository: PostRepository = PostRepositoryImpl(
//        posDao,
//        postApiService)
//
//    val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(
//        userApiService)
//
//    val registrRepository: RegistrationRepositoryImpl = RegistrationRepositoryImpl(
//        userApiService)
//
//
//}