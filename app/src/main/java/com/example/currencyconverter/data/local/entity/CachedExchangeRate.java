package com.example.currencyconverter.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room Entity for caching exchange rates
 * Enables offline-first functionality
 */
@Entity(tableName = "cached_rates")
public class CachedExchangeRate {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String baseCurrency;
    private String targetCurrency;
    private double rate;
    private long timestamp;

    public CachedExchangeRate(String baseCurrency, String targetCurrency, double rate, long timestamp) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
