package com.example.currencyconverter.ui.favorites;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.data.local.entity.FavoriteConversion;
import com.example.currencyconverter.databinding.ItemFavoriteBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RecyclerView Adapter for Favorites
 * 2nd Semester - RecyclerView Advanced + DiffUtil
 */
public class FavoritesAdapter extends ListAdapter<FavoriteConversion, FavoritesAdapter.FavoriteViewHolder> {

    private final OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(FavoriteConversion favorite);

        void onDeleteClick(FavoriteConversion favorite);
    }

    public FavoritesAdapter(OnFavoriteClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<FavoriteConversion> DIFF_CALLBACK = new DiffUtil.ItemCallback<FavoriteConversion>() {
        @Override
        public boolean areItemsTheSame(@NonNull FavoriteConversion oldItem, @NonNull FavoriteConversion newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull FavoriteConversion oldItem, @NonNull FavoriteConversion newItem) {
            return oldItem.getFromCurrency().equals(newItem.getFromCurrency()) &&
                    oldItem.getToCurrency().equals(newItem.getToCurrency()) &&
                    oldItem.getAmount() == newItem.getAmount() &&
                    oldItem.getResult() == newItem.getResult();
        }
    };

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteBinding binding = ItemFavoriteBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new FavoriteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteConversion favorite = getItem(position);
        holder.bind(favorite);
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavoriteBinding binding;

        public FavoriteViewHolder(ItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FavoriteConversion favorite) {
            String conversion = String.format(Locale.getDefault(),
                    "%.2f %s = %.2f %s",
                    favorite.getAmount(),
                    favorite.getFromCurrency(),
                    favorite.getResult(),
                    favorite.getToCurrency());

            binding.textViewConversion.setText(conversion);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateStr = sdf.format(new Date(favorite.getTimestamp()));
            binding.textViewDate.setText(dateStr);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(favorite);
                }
            });

            binding.buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(favorite);
                }
            });
        }
    }
}
