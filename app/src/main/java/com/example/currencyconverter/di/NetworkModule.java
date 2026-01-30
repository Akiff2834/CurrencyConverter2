package com.example.currencyconverter.di;

import com.example.currencyconverter.data.remote.api.CurrencyApiService;
import com.example.currencyconverter.data.remote.api.RetrofitClient;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;
import retrofit2.Retrofit;

/**
 * Hilt Module for Network dependencies
 * 2nd Semester - Week 6: Dependency Injection
 * 
 * Provides Retrofit and API service instances
 */
@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return RetrofitClient.getInstance();
    }

    @Provides
    @Singleton
    public CurrencyApiService provideCurrencyApiService(Retrofit retrofit) {
        return retrofit.create(CurrencyApiService.class);
    }
}
