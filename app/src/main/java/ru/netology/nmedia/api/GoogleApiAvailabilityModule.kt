package ru.netology.nmedia.api

import com.google.android.gms.common.GoogleApiAvailability
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GoogleApiAvailabilityModule {
    @Provides
    @Singleton
    fun provideGoogleApiAvailability(): GoogleApiAvailability {
        return GoogleApiAvailability.getInstance()
    }
}