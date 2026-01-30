package com.example.currencyconverter.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.currencyconverter.data.local.dao.CachedRateDao;
import com.example.currencyconverter.data.local.dao.FavoriteConversionDao;
import com.example.currencyconverter.data.local.entity.CachedExchangeRate;
import com.example.currencyconverter.data.local.entity.FavoriteConversion;

/**
 * Room Database singleton
 * 2nd Semester - Room Database Implementation + Offline Caching
 */
@Database(entities = { FavoriteConversion.class, CachedExchangeRate.class }, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract FavoriteConversionDao favoriteConversionDao();

    public abstract CachedRateDao cachedRateDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "currency_converter_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
