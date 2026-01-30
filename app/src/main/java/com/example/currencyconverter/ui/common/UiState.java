package com.example.currencyconverter.ui.common;

/**
 * Sealed class pattern for UI State Management
 * Represents all possible states of the conversion screen
 * 
 * This replaces multiple LiveData objects with a single unified state
 */
public abstract class UiState {

    /**
     * Initial state - no action taken yet
     */
    public static class Idle extends UiState {
    }

    /**
     * Loading state - API call in progress
     */
    public static class Loading extends UiState {
    }

    /**
     * Success state - conversion completed successfully
     */
    public static class Success extends UiState {
        public final String result;

        public Success(String result) {
            this.result = result;
        }
    }

    /**
     * Error state - something went wrong
     */
    public static class Error extends UiState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
