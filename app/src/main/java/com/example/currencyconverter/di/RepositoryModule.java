package com.example.currencyconverter.di;

import android.app.Application;
import com.example.currencyconverter.data.repository.CurrencyRepository;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

/**
 * Hilt Module for Repository
 * 2nd Semester - Week 6: Dependency Injection
 * 
 * Provides singleton instance of CurrencyRepository
 */
@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {

    @Provides
    @Singleton
    public CurrencyRepository provideCurrencyRepository(Application application) {
        return new CurrencyRepository(application);
    }
}
