package com.example.currencyconverter.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.currencyconverter.databinding.FragmentAboutBinding;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Fragment for About screen
 * 2nd Semester - Navigation Component + Hilt DI
 * 
 * @AndroidEntryPoint enables Hilt dependency injection
 */
@AndroidEntryPoint
public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupListeners();
    }

    private void setupListeners() {
        binding.buttonBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        binding.buttonGithub.setOnClickListener(v -> {
            String githubUrl = "https://github.com/Akiff2834";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl));
            startActivity(intent);
        });

        binding.buttonEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("kabaeros@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Currency Converter Feedback");
            startActivity(Intent.createChooser(intent, "Send Email"));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
