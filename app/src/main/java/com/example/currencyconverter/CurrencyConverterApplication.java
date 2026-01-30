package com.example.currencyconverter;

import android.app.Application;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import dagger.hilt.android.HiltAndroidApp;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

/**
 * Application class for Currency Converter
 * 2nd Semester - Hilt Dependency Injection (Week 6)
 * 
 * @HiltAndroidApp triggers Hilt's code generation
 *                 This is the entry point for Hilt DI
 */
@HiltAndroidApp
public class CurrencyConverterApplication extends Application implements Configuration.Provider {

    @Inject
    androidx.work.Configuration workConfiguration;

    @Override
    public void onCreate() {
        super.onCreate();

        // Schedule periodic exchange rate updates (WorkManager - Week 7)
        scheduleExchangeRateUpdates();
    }

    /**
     * Schedule daily exchange rate updates using WorkManager
     * 2nd Semester - Week 7: Background Tasks
     */
    private void scheduleExchangeRateUpdates() {
        // Define constraints: only run when connected to network
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Create periodic work request (runs once per day)
        PeriodicWorkRequest updateWorkRequest = new PeriodicWorkRequest.Builder(
                com.example.currencyconverter.worker.ExchangeRateUpdateWorker.class,
                24, TimeUnit.HOURS) // Repeat every 24 hours
                .setConstraints(constraints)
                .build();

        // Enqueue the work (keep existing work if already scheduled)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "ExchangeRateUpdate",
                ExistingPeriodicWorkPolicy.KEEP,
                updateWorkRequest);
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return workConfiguration;
    }
}
