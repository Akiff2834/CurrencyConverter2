package com.example.currencyconverter.data.remote.model;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Response model for exchange rate API
 * 2nd Semester - Reorganized into data layer
 */
public class ExchangeRateResponse {

    @SerializedName("conversion_rates")
    private Map<String, Double> rates;

    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
