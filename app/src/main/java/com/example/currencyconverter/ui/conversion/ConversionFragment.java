package com.example.currencyconverter.ui.conversion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.currencyconverter.R;
import com.example.currencyconverter.data.local.entity.FavoriteConversion;
import com.example.currencyconverter.databinding.FragmentConversionBinding;
import com.example.currencyconverter.ui.common.UiState;
import com.example.currencyconverter.ui.favorites.FavoritesViewModel;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for currency conversion
 * 2nd Semester - PURE MVVM Implementation + Hilt DI
 * 
 * This Fragment is DUMB - it contains ZERO business logic:
 * ❌ NO if/else statements
 * ❌ NO try/catch blocks
 * ❌ NO network checks
 * ❌ NO String parsing
 * ❌ NO validation
 * ❌ NO error message generation
 * 
 * It ONLY:
 * ✅ Sets up UI components
 * ✅ Passes raw input to ViewModel
 * ✅ Observes UiState and updates UI accordingly
 * 
 * @AndroidEntryPoint enables Hilt dependency injection
 */
@AndroidEntryPoint
public class ConversionFragment extends Fragment {

    private FragmentConversionBinding binding;
    private CurrencyConverterViewModel viewModel;
    private FavoritesViewModel favoritesViewModel;

    private final String[] currencyList = {
            "USD", "EUR", "GBP", "TRY", "JPY", "AUD", "CAD",
            "CHF", "CNY", "SEK", "NOK", "DKK", "NZD", "MXN",
            "INR", "RUB", "ZAR", "BRL", "SGD", "HKD", "KRW", "PLN"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentConversionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels with Hilt
        viewModel = new ViewModelProvider(this).get(CurrencyConverterViewModel.class);
        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        setupSpinners();
        setupObservers();
        setupListeners();

        // SafeArgs: Read arguments and pre-fill UI if coming from favorites
        readSafeArgsAndPreFill();
    }

    /**
     * SafeArgs: Read navigation arguments and pre-fill conversion form
     * This is UI-only logic, acceptable in Fragment
     */
    private void readSafeArgsAndPreFill() {
        if (getArguments() != null) {
            ConversionFragmentArgs args = ConversionFragmentArgs.fromBundle(getArguments());

            String fromCurrency = args.getFromCurrency();
            String toCurrency = args.getToCurrency();
            float amount = args.getAmount();

            // Pre-fill UI if arguments are provided
            if (fromCurrency != null && !fromCurrency.isEmpty()) {
                int fromPosition = getSpinnerPosition(fromCurrency);
                if (fromPosition >= 0) {
                    binding.spinnerFrom.setSelection(fromPosition);
                }
            }

            if (toCurrency != null && !toCurrency.isEmpty()) {
                int toPosition = getSpinnerPosition(toCurrency);
                if (toPosition >= 0) {
                    binding.spinnerTo.setSelection(toPosition);
                }
            }

            if (amount > 0) {
                binding.editTextAmount.setText(String.valueOf(amount));
            }
        }
    }

    /**
     * Helper method to find currency position in spinner
     * Pure UI logic, acceptable in Fragment
     */
    private int getSpinnerPosition(String currency) {
        for (int i = 0; i < currencyList.length; i++) {
            if (currencyList[i].equals(currency)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Setup spinners - Pure UI logic
     */
    private void setupSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                currencyList);
        binding.spinnerFrom.setAdapter(adapter);
        binding.spinnerTo.setAdapter(adapter);
    }

    /**
     * CRITICAL: Single observer for UiState
     * This is the ONLY place where UI updates happen
     * NO business logic here - just UI updates based on state
     */
    private void setupObservers() {
        viewModel.uiState.observe(getViewLifecycleOwner(), state -> {
            // Handle Loading state
            if (state instanceof UiState.Loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.buttonConvert.setEnabled(false);
            }
            // Handle Success state
            else if (state instanceof UiState.Success) {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonConvert.setEnabled(true);
                binding.textViewResult.setText(((UiState.Success) state).result);
            }
            // Handle Error state
            else if (state instanceof UiState.Error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonConvert.setEnabled(true);
                binding.textViewResult.setText(((UiState.Error) state).message);
                Toast.makeText(requireContext(), ((UiState.Error) state).message, Toast.LENGTH_SHORT).show();
            }
            // Handle Idle state
            else {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonConvert.setEnabled(true);
            }
        });
    }

    /**
     * Setup click listeners
     * Fragment ONLY passes raw data to ViewModel
     * NO validation, NO parsing, NO logic
     */
    private void setupListeners() {
        // Convert button - Pass RAW strings to ViewModel
        binding.buttonConvert.setOnClickListener(v -> {
            viewModel.onConvertClicked(
                    binding.spinnerFrom.getSelectedItem().toString(),
                    binding.spinnerTo.getSelectedItem().toString(),
                    binding.editTextAmount.getText().toString());
        });

        // Save favorite button
        binding.buttonSaveFavorite.setOnClickListener(v -> saveFavorite());

        // Navigation buttons
        binding.buttonViewFavorites.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_conversionFragment_to_favoritesFragment);
        });

        binding.buttonAbout.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_conversionFragment_to_aboutFragment);
        });
    }

    /**
     * Save current conversion as favorite
     * Simple data passing, no business logic
     */
    private void saveFavorite() {
        CurrencyConverterViewModel.ConversionData data = viewModel.getCurrentConversionData();

        if (data == null || data.fromCurrency == null || data.toCurrency == null) {
            Toast.makeText(requireContext(), "Please perform a conversion first", Toast.LENGTH_SHORT).show();
            return;
        }

        FavoriteConversion favorite = new FavoriteConversion(
                data.fromCurrency,
                data.toCurrency,
                data.amount,
                data.result,
                System.currentTimeMillis());

        favoritesViewModel.insertFavorite(favorite);
        Toast.makeText(requireContext(), "Saved to favorites!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
