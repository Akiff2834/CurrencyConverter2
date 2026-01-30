package com.example.currencyconverter.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.currencyconverter.R;
import com.example.currencyconverter.data.Resource;
import com.example.currencyconverter.data.remote.api.CurrencyApiService;
import com.example.currencyconverter.data.remote.api.RetrofitClient;
import com.example.currencyconverter.data.remote.model.ExchangeRateResponse;
import com.example.currencyconverter.data.repository.CurrencyRepository;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Response;

/**
 * WorkManager Worker for periodic exchange rate updates
 * 2nd Semester - Week 7: Background Tasks
 * 
 * Runs daily to:
 * - Fetch latest exchange rates
 * - Update Room cache
 * - Show notification with update status
 * 
 * Uses Hilt for dependency injection
 */
@HiltWorker
public class ExchangeRateUpdateWorker extends Worker {

    private static final String CHANNEL_ID = "exchange_rate_updates";
    private static final int NOTIFICATION_ID = 1001;
    private static final String API_KEY = "457716a57a835e3d0027e6fc";

    private final CurrencyRepository repository;

    @AssistedInject
    public ExchangeRateUpdateWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters params,
            CurrencyRepository repository) {
        super(context, params);
        this.repository = repository;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Create notification channel (required for Android 8.0+)
            createNotificationChannel();

            // Fetch latest exchange rates for major currencies
            String[] baseCurrencies = { "USD", "EUR", "GBP", "TRY" };
            int successCount = 0;

            for (String baseCurrency : baseCurrencies) {
                // Fetch rates synchronously (we're already on background thread)
                Resource<Map<String, Double>> result = fetchRatesSync(baseCurrency);

                if (result != null && result.isSuccess() && result.getData() != null) {
                    // Cache the rates
                    repository.cacheExchangeRates(baseCurrency, result.getData());
                    successCount++;
                }
            }

            // Show success notification
            if (successCount > 0) {
                showNotification(
                        "Exchange Rates Updated",
                        "Successfully updated rates for " + successCount + " currencies");
                return Result.success();
            } else {
                showNotification(
                        "Update Failed",
                        "Could not update exchange rates. Will retry later.");
                return Result.retry();
            }

        } catch (Exception e) {
            showNotification(
                    "Update Error",
                    "Error updating rates: " + e.getMessage());
            return Result.failure();
        }
    }

    /**
     * Fetch exchange rates synchronously using Retrofit's execute() method
     * This properly blocks until the API call completes
     */
    private Resource<Map<String, Double>> fetchRatesSync(String baseCurrency) {
        try {
            // Create API service
            CurrencyApiService apiService = RetrofitClient.getInstance()
                    .create(CurrencyApiService.class);

            // Make synchronous API call using execute()
            Call<ExchangeRateResponse> call = apiService.getExchangeRates(API_KEY, baseCurrency);
            Response<ExchangeRateResponse> response = call.execute();

            // Check if successful
            if (response.isSuccessful() && response.body() != null && response.body().getRates() != null) {
                Map<String, Double> rates = response.body().getRates();
                return Resource.success(rates);
            } else {
                return Resource.error("Failed to fetch rates for " + baseCurrency, null);
            }
        } catch (Exception e) {
            return Resource.error("Error: " + e.getMessage(), null);
        }
    }

    /**
     * Create notification channel for Android 8.0+
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Exchange Rate Updates";
            String description = "Notifications for daily exchange rate updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getApplicationContext()
                    .getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Show notification to user
     */
    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
