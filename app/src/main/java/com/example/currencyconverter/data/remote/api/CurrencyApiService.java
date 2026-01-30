package com.example.currencyconverter.data.remote.api;

import com.example.currencyconverter.data.remote.model.ExchangeRateResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit API Service for currency exchange rates
 * 2nd Semester - Reorganized into data layer
 */
public interface CurrencyApiService {

    @GET("v6/{apiKey}/latest/{base}")
    Call<ExchangeRateResponse> getExchangeRates(
            @Path("apiKey") String apiKey,
            @Path("base") String baseCurrency);
}
