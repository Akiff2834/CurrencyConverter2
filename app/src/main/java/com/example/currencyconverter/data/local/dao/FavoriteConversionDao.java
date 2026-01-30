package com.example.currencyconverter.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.currencyconverter.data.local.entity.FavoriteConversion;

import java.util.List;

/**
 * Data Access Object for FavoriteConversion
 * 2nd Semester - Room Database Implementation
 */
@Dao
public interface FavoriteConversionDao {

    @Insert
    void insert(FavoriteConversion favoriteConversion);

    @Delete
    void delete(FavoriteConversion favoriteConversion);

    @Query("SELECT * FROM favorite_conversions ORDER BY timestamp DESC")
    LiveData<List<FavoriteConversion>> getAllFavorites();

    @Query("DELETE FROM favorite_conversions WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT COUNT(*) FROM favorite_conversions")
    int getCount();
}
