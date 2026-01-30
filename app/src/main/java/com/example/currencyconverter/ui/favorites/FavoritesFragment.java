package com.example.currencyconverter.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.currencyconverter.R;
import com.example.currencyconverter.data.local.entity.FavoriteConversion;
import com.example.currencyconverter.databinding.FragmentFavoritesBinding;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for displaying favorite conversions
 * 2nd Semester - Navigation Component + RecyclerView + MVVM + Hilt DI
 * 
 * @AndroidEntryPoint enables Hilt dependency injection
 */
@AndroidEntryPoint
public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnFavoriteClickListener {

    private FragmentFavoritesBinding binding;
    private FavoritesViewModel viewModel;
    private FavoritesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel with Hilt
        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        setupRecyclerView();
        setupObservers();
        setupListeners();
    }

    private void setupRecyclerView() {
        adapter = new FavoritesAdapter(this);
        binding.recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewFavorites.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getAllFavorites().observe(getViewLifecycleOwner(), favorites -> {
            adapter.submitList(favorites);

            // Stop refreshing animation
            binding.swipeRefreshLayout.setRefreshing(false);

            if (favorites == null || favorites.isEmpty()) {
                binding.textViewEmpty.setVisibility(View.VISIBLE);
                binding.swipeRefreshLayout.setVisibility(View.GONE);
            } else {
                binding.textViewEmpty.setVisibility(View.GONE);
                binding.swipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupListeners() {
        binding.buttonBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        // Swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh favorites list (data comes from LiveData automatically)
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onFavoriteClick(FavoriteConversion favorite) {
        // Navigate back to conversion with pre-filled data using SafeArgs
        FavoritesFragmentDirections.ActionFavoritesFragmentToConversionFragment action = FavoritesFragmentDirections
                .actionFavoritesFragmentToConversionFragment(
                        favorite.getFromCurrency(),
                        favorite.getToCurrency());

        Navigation.findNavController(requireView()).navigate(action);

        Toast.makeText(requireContext(),
                "Loading: " + favorite.getFromCurrency() + " → " + favorite.getToCurrency(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(FavoriteConversion favorite) {
        viewModel.deleteFavorite(favorite);
        Toast.makeText(requireContext(), "Deleted from favorites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
