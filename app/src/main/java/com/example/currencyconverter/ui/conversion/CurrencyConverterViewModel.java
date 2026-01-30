package com.example.currencyconverter.ui.conversion;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.currencyconverter.data.Resource;
import com.example.currencyconverter.data.repository.CurrencyRepository;
import com.example.currencyconverter.ui.common.UiState;

import java.util.Map;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * ViewModel for Currency Conversion
 * 2nd Semester - PURE MVVM Architecture Implementation + Hilt DI
 * 
 * ALL BUSINESS LOGIC IS HERE:
 * - Input validation
 * - String parsing
 * - Same currency check
 * - Network availability check
 * - API orchestration
 * - Result formatting
 * - Error handling
 * 
 * Fragment is DUMB - it only:
 * - Passes raw input to ViewModel
 * - Observes UiState and updates UI
 */
@HiltViewModel
public class CurrencyConverterViewModel extends ViewModel {

    private final CurrencyRepository repository;
    private final Context application;

    // API Key - moved from Fragment
    private static final String API_KEY = "457716a57a835e3d0027e6fc";

    // Single LiveData for unified UI state
    private final MutableLiveData<UiState> _uiState = new MutableLiveData<>(new UiState.Idle());
    public final LiveData<UiState> uiState = _uiState;

    // Current conversion data for favorites
    private String currentFromCurrency;
    private String currentToCurrency;
    private double currentAmount;
    private double currentResult;

    // Constructor with Hilt dependency injection
    @Inject
    public CurrencyConverterViewModel(@ApplicationContext Context application, CurrencyRepository repository) {
        this.application = application;
        this.repository = repository;
    }

    /**
     * MAIN ENTRY POINT - Called from Fragment
     * Contains ALL business logic that was previously in Fragment
     * 
     * @param fromCurrency Raw string from spinner
     * @param toCurrency   Raw string from spinner
     * @param amountStr    Raw string from EditText
     */
    public void onConvertClicked(String fromCurrency, String toCurrency, String amountStr) {
        // 1. VALIDATION - Check for empty input
        if (amountStr == null || amountStr.trim().isEmpty()) {
            _uiState.setValue(new UiState.Error("Please enter an amount"));
            return;
        }

        // 2. PARSING - Convert String to Double
        double amount;
        try {
            amount = Double.parseDouble(amountStr.trim());
        } catch (NumberFormatException e) {
            _uiState.setValue(new UiState.Error("Invalid amount"));
            return;
        }

        // 3. VALIDATION - Check if amount is positive
        if (amount <= 0) {
            _uiState.setValue(new UiState.Error("Amount must be greater than 0"));
            return;
        }

        // 4. SAME CURRENCY CHECK
        if (fromCurrency.equals(toCurrency)) {
            String result = String.format("Same currency selected.\n1 %s = 1 %s", fromCurrency, toCurrency);
            _uiState.setValue(new UiState.Success(result));
            return;
        }

        // 5. NETWORK CHECK
        if (!isNetworkAvailable()) {
            _uiState.setValue(new UiState.Error("No internet connection"));
            return;
        }

        // 6. ALL VALIDATIONS PASSED - Perform conversion
        performConversion(fromCurrency, toCurrency, amount);
    }

    /**
     * Perform the actual currency conversion
     * Observes Repository LiveData and updates UI state
     */
    private void performConversion(String fromCurrency, String toCurrency, double amount) {
        // Store current conversion data
        currentFromCurrency = fromCurrency;
        currentToCurrency = toCurrency;
        currentAmount = amount;

        // Set loading state
        _uiState.setValue(new UiState.Loading());

        android.util.Log.d("CurrencyViewModel",
                "Starting conversion: " + amount + " " + fromCurrency + " -> " + toCurrency);

        // Get LiveData from Repository
        LiveData<Resource<Map<String, Double>>> ratesLiveData = repository.getExchangeRates(API_KEY, fromCurrency);

        // Observe the LiveData directly and update UI state
        ratesLiveData.observeForever(new androidx.lifecycle.Observer<Resource<Map<String, Double>>>() {
            @Override
            public void onChanged(Resource<Map<String, Double>> resource) {
                if (resource != null) {
                    android.util.Log.d("CurrencyViewModel", "Resource received: " + resource.getStatus());

                    if (resource.isSuccess() && resource.getData() != null) {
                        // Success - perform conversion
                        Map<String, Double> rates = resource.getData();
                        android.util.Log.d("CurrencyViewModel", "Success! Rates size: " + rates.size());

                        if (rates.containsKey(toCurrency)) {
                            currentResult = rates.get(toCurrency) * amount;
                            String result = String.format("%.2f %s = %.2f %s",
                                    amount, fromCurrency, currentResult, toCurrency);
                            android.util.Log.d("CurrencyViewModel", "Conversion result: " + result);
                            _uiState.setValue(new UiState.Success(result));
                        } else {
                            android.util.Log.e("CurrencyViewModel", "Target currency not found: " + toCurrency);
                            _uiState.setValue(new UiState.Error("Target currency not available"));
                        }

                        // Remove observer after success
                        ratesLiveData.removeObserver(this);

                    } else if (resource.isError()) {
                        // Error - show error message
                        android.util.Log.e("CurrencyViewModel", "Error: " + resource.getMessage());
                        _uiState.setValue(new UiState.Error(resource.getMessage()));

                        // Remove observer after error
                        ratesLiveData.removeObserver(this);
                    }
                    // If loading, keep observing
                }
            }
        });
    }

    /**
     * Check network availability
     * Moved from Fragment - this is business logic
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
        return false;
    }

    /**
     * Get current conversion data for saving as favorite
     */
    public ConversionData getCurrentConversionData() {
        return new ConversionData(currentFromCurrency, currentToCurrency, currentAmount, currentResult);
    }

    /**
     * Data class to hold conversion information
     */
    public static class ConversionData {
        public final String fromCurrency;
        public final String toCurrency;
        public final double amount;
        public final double result;

        public ConversionData(String fromCurrency, String toCurrency, double amount, double result) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.amount = amount;
            this.result = result;
        }
    }
}
