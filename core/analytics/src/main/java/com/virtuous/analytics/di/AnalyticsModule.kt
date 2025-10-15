package com.virtuous.analytics.di

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.virtuous.analytics.AmplitudeAnalyticsHelper
import com.virtuous.analytics.AnalyticsHelper
import com.virtuous.analytics.NoOpAnalyticsHelper
import com.virtuous.analytics.BuildConfig
import com.virtuous.analytics.error.ErrorHelper
import com.virtuous.analytics.error.FirebaseErrorHelper
import com.virtuous.analytics.error.NoOpErrorHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun providesAmplitude(@ApplicationContext context: Context): Amplitude = Amplitude(
        Configuration(
            apiKey = BuildConfig.AMPLITUDE_API_KEY,
            context = context,
        ),
    )

    @Provides
    @Singleton
    fun providesFirebaseCrashlytics(@ApplicationContext context: Context): FirebaseCrashlytics =
        FirebaseCrashlytics.getInstance()



    @Provides
    @Singleton
    fun provideAnalyticsHelper(
        @ApplicationContext context: Context,
        amplitudeProvider: Provider<Amplitude>,
    ): AnalyticsHelper {
        return if (BuildConfig.DEBUG) {
            NoOpAnalyticsHelper()
        } else {
            AmplitudeAnalyticsHelper(amplitudeProvider.get())
        }
    }

    @Provides
    @Singleton
    fun provideErrorHelper(
        firebaseCrashlyticsProvider: Provider<FirebaseCrashlytics>
    ): ErrorHelper {
        return if (BuildConfig.DEBUG) {
            NoOpErrorHelper()
        } else {
            FirebaseErrorHelper(firebaseCrashlyticsProvider.get())
        }
    }
}
