package com.leventgorgu.inviousgchallenge.di


import com.leventgorgu.inviousgchallenge.api.CinemaAPI
import com.leventgorgu.inviousgchallenge.utils.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun injectCinemaAPI():CinemaAPI{
        return Retrofit.Builder()
            .baseUrl(Util.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CinemaAPI::class.java)
    }
}