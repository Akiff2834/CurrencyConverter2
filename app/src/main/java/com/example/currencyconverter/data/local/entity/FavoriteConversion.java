package com.example.currencyconverter.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room Entity for storing favorite currency conversions
 * 2nd Semester - Room Database Implementation
 */
@Entity(tableName = "favorite_conversions")
public class FavoriteConversion {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String fromCurrency;
    private String toCurrency;
    private double amount;
    private double result;
    private long timestamp;

    public FavoriteConversion(String fromCurrency, String toCurrency, double amount, double result, long timestamp) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
        this.result = result;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
