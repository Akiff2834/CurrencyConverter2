package com.example.currencyconverter.ui.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.currencyconverter.data.local.entity.FavoriteConversion;
import com.example.currencyconverter.data.repository.CurrencyRepository;

import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for Favorites
 * 2nd Semester - MVVM Architecture Implementation with Hilt DI
 * 
 * Manages favorites list with LiveData from Room Database
 * Repository injected via Hilt
 */
@HiltViewModel
public class FavoritesViewModel extends ViewModel {

    private final CurrencyRepository repository;
    private final LiveData<List<FavoriteConversion>> allFavorites;

    // Constructor with Hilt dependency injection
    @Inject
    public FavoritesViewModel(CurrencyRepository repository) {
        this.repository = repository;
        allFavorites = repository.getAllFavorites();
    }

    public LiveData<List<FavoriteConversion>> getAllFavorites() {
        return allFavorites;
    }

    public void insertFavorite(FavoriteConversion favorite) {
        repository.insertFavorite(favorite);
    }

    public void deleteFavorite(FavoriteConversion favorite) {
        repository.deleteFavorite(favorite);
    }

    public void deleteFavoriteById(int id) {
        repository.deleteFavoriteById(id);
    }
}
