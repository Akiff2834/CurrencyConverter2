package com.example.currencyconverter.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.currencyconverter.data.local.entity.CachedExchangeRate;

import java.util.List;

/**
 * DAO for cached exchange rates
 * Supports offline-first functionality
 */
@Dao
public interface CachedRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRate(CachedExchangeRate rate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRates(List<CachedExchangeRate> rates);

    @Query("SELECT * FROM cached_rates WHERE baseCurrency = :base AND targetCurrency = :target LIMIT 1")
    CachedExchangeRate getRate(String base, String target);

    @Query("SELECT * FROM cached_rates WHERE baseCurrency = :base")
    List<CachedExchangeRate> getAllRatesForBase(String base);

    @Query("SELECT * FROM cached_rates WHERE baseCurrency = :base")
    List<CachedExchangeRate> getRatesForBase(String base);

    @Query("DELETE FROM cached_rates WHERE timestamp < :expiryTime")
    void deleteOldRates(long expiryTime);

    @Query("DELETE FROM cached_rates")
    void deleteAll();
}
