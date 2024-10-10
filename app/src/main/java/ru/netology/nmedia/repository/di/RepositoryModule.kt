package ru.netology.nmedia.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.repository.AuthRepository
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.RegistrationRepository
import ru.netology.nmedia.repositoryImpl.AuthRepositoryImpl
import ru.netology.nmedia.repositoryImpl.PostRepositoryImpl
import ru.netology.nmedia.repositoryImpl.RegistrationRepositoryImpl

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Binds
    fun bindsPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    fun bindsAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindsRegistrRepository(impl: RegistrationRepositoryImpl): RegistrationRepository
}