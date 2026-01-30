package com.example.currencyconverter.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.currencyconverter.data.Resource;
import com.example.currencyconverter.data.local.dao.CachedRateDao;
import com.example.currencyconverter.data.local.dao.FavoriteConversionDao;
import com.example.currencyconverter.data.local.database.AppDatabase;
import com.example.currencyconverter.data.local.entity.CachedExchangeRate;
import com.example.currencyconverter.data.local.entity.FavoriteConversion;
import com.example.currencyconverter.data.remote.api.CurrencyApiService;
import com.example.currencyconverter.data.remote.api.RetrofitClient;
import com.example.currencyconverter.data.remote.model.ExchangeRateResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository Pattern - Single source of truth for currency data
 * 2nd Semester - Repository Pattern Implementation + Offline Caching
 * 
 * Coordinates between:
 * - Remote data source (API)
 * - Local data source (Room Database)
 * - Cached exchange rates for offline support
 */
public class CurrencyRepository {

    private final FavoriteConversionDao favoriteDao;
    private final CachedRateDao cachedRateDao;
    private final CurrencyApiService apiService;
    private final LiveData<List<FavoriteConversion>> allFavorites;
    private final ExecutorService executorService;

    // Cache expiry: 1 hour
    private static final long CACHE_EXPIRY_MS = 60 * 60 * 1000;

    public CurrencyRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        favoriteDao = database.favoriteConversionDao();
        cachedRateDao = database.cachedRateDao();
        apiService = RetrofitClient.getInstance().create(CurrencyApiService.class);
        allFavorites = favoriteDao.getAllFavorites();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Remote data source - API calls with offline-first strategy
    /**
     * Fetch exchange rates with offline-first strategy
     * 1. Check cache first
     * 2. If cache valid, return cached data
     * 3. If cache invalid/missing, fetch from API
     * 4. Cache successful API responses
     * 
     * @param apiKey       API key for the service
     * @param baseCurrency Base currency code
     * @return LiveData containing Resource with exchange rates map
     */
    public LiveData<Resource<Map<String, Double>>> getExchangeRates(String apiKey, String baseCurrency) {
        MutableLiveData<Resource<Map<String, Double>>> result = new MutableLiveData<>();

        // Start with loading state
        result.setValue(Resource.loading(null));

        android.util.Log.d("CurrencyRepository", "Starting exchange rate fetch for: " + baseCurrency);

        // Check cache on background thread
        executorService.execute(() -> {
            // Try to get all cached rates for this base currency
            List<CachedExchangeRate> cachedRates = cachedRateDao.getRatesForBase(baseCurrency);

            // Check if we have valid cache
            if (cachedRates != null && !cachedRates.isEmpty()) {
                long age = System.currentTimeMillis() - cachedRates.get(0).getTimestamp();
                android.util.Log.d("CurrencyRepository", "Cache found, age: " + age + "ms");

                if (age < CACHE_EXPIRY_MS) {
                    // Cache is valid, convert to Map
                    Map<String, Double> ratesMap = new java.util.HashMap<>();
                    for (CachedExchangeRate rate : cachedRates) {
                        ratesMap.put(rate.getTargetCurrency(), rate.getRate());
                    }
                    android.util.Log.d("CurrencyRepository", "Using cached rates: " + ratesMap.size() + " currencies");
                    result.postValue(Resource.success(ratesMap));
                    return;
                }
            }

            android.util.Log.d("CurrencyRepository", "Cache invalid/missing, fetching from API");

            // Cache invalid or missing, fetch from API
            // Retrofit's enqueue is already async, no need for ExecutorService here
            Call<ExchangeRateResponse> call = apiService.getExchangeRates(apiKey, baseCurrency);

            call.enqueue(new Callback<ExchangeRateResponse>() {
                @Override
                public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                    android.util.Log.d("CurrencyRepository", "API Response received. Success: "
                            + response.isSuccessful() + ", Code: " + response.code());

                    if (response.isSuccessful() && response.body() != null && response.body().getRates() != null) {
                        Map<String, Double> rates = response.body().getRates();
                        android.util.Log.d("CurrencyRepository", "Rates received: " + rates.size() + " currencies");

                        // Cache the successful response
                        cacheExchangeRates(baseCurrency, rates);

                        // Return success
                        result.postValue(Resource.success(rates));
                    } else {
                        String errorMsg = "Failed to retrieve rates. Code: " + response.code();
                        android.util.Log.e("CurrencyRepository", errorMsg);
                        result.postValue(Resource.error(errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                    String errorMsg = "API Error: " + t.getMessage();
                    android.util.Log.e("CurrencyRepository", errorMsg, t);
                    result.postValue(Resource.error(errorMsg, null));
                }
            });
        });

        return result;
    }

    // Local data source - Database operations
    public LiveData<List<FavoriteConversion>> getAllFavorites() {
        return allFavorites;
    }

    public void insertFavorite(FavoriteConversion favorite) {
        executorService.execute(() -> favoriteDao.insert(favorite));
    }

    public void deleteFavorite(FavoriteConversion favorite) {
        executorService.execute(() -> favoriteDao.delete(favorite));
    }

    public void deleteFavoriteById(int id) {
        executorService.execute(() -> favoriteDao.deleteById(id));
    }

    // Offline caching methods
    public void cacheExchangeRates(String baseCurrency, Map<String, Double> rates) {
        executorService.execute(() -> {
            List<CachedExchangeRate> cachedRates = new ArrayList<>();
            long timestamp = System.currentTimeMillis();

            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                CachedExchangeRate rate = new CachedExchangeRate(
                        baseCurrency,
                        entry.getKey(),
                        entry.getValue(),
                        timestamp);
                cachedRates.add(rate);
            }

            cachedRateDao.insertRates(cachedRates);
        });
    }

    public Double getCachedRate(String baseCurrency, String targetCurrency) {
        try {
            CachedExchangeRate cached = cachedRateDao.getRate(baseCurrency, targetCurrency);
            if (cached != null) {
                // Check if cache is still valid
                long age = System.currentTimeMillis() - cached.getTimestamp();
                if (age < CACHE_EXPIRY_MS) {
                    return cached.getRate();
                }
            }
        } catch (Exception e) {
            // Return null if any error
        }
        return null;
    }

    public void cleanOldCache() {
        executorService.execute(() -> {
            long expiryTime = System.currentTimeMillis() - CACHE_EXPIRY_MS;
            cachedRateDao.deleteOldRates(expiryTime);
        });
    }
}
