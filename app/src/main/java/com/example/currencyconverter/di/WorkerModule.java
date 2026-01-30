package com.example.currencyconverter.di;

import androidx.work.Configuration;
import androidx.hilt.work.HiltWorkerFactory;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

/**
 * Hilt Module for WorkManager
 * 2nd Semester - Week 7: Background Tasks
 * 
 * Provides WorkManager configuration with Hilt support
 */
@Module
@InstallIn(SingletonComponent.class)
public class WorkerModule {

    @Provides
    @Singleton
    public Configuration provideWorkManagerConfiguration(
            HiltWorkerFactory workerFactory) {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }
}
