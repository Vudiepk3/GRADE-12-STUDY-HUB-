package com.example.datn.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datn.R;
import com.example.datn.activity.main.ViewMoreActivity;
import com.example.datn.adapter.SubjectAdapter;
import com.example.datn.databinding.FragmentHomeBinding;
import com.example.datn.helpers.CountdownTimer;
import com.example.datn.helpers.FirebaseManager;
import com.example.datn.helpers.ImageSlideManager;
import com.example.datn.helpers.ProfileImageManager;
import com.example.datn.helpers.QuoteManager;
import com.example.datn.models.SubjectModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ImageSlideManager imageSlideManager;
    private ProfileImageManager profileImageManager;
    private QuoteManager quoteManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseManager firebaseManager = new FirebaseManager();
        imageSlideManager = new ImageSlideManager(firebaseManager);
        profileImageManager = new ProfileImageManager(this);
        quoteManager = new QuoteManager(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup recyclerView
        binding.subjectRecycle.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        // Data
        List<SubjectModel> subjectList = new ArrayList<>();
        subjectList.add(new SubjectModel(R.drawable.image_maths, getString(R.string.maths)));
        subjectList.add(new SubjectModel(R.drawable.image_literature, getString(R.string.literature)));
        subjectList.add(new SubjectModel(R.drawable.image_english, getString(R.string.english)));
        subjectList.add(new SubjectModel(R.drawable.image_physics, getString(R.string.physics)));
        subjectList.add(new SubjectModel(R.drawable.image_chemistry, getString(R.string.chemistry)));
        subjectList.add(new SubjectModel(R.drawable.image_biology, getString(R.string.biology)));
        subjectList.add(new SubjectModel(R.drawable.image_history, getString(R.string.history)));
        subjectList.add(new SubjectModel(R.drawable.image_geography, getString(R.string.geography)));
        subjectList.add(new SubjectModel(R.drawable.image_civiceducation, getString(R.string.civic)));
        subjectList.add(new SubjectModel(R.drawable.image_tsa, getString(R.string.tsa)));
        subjectList.add(new SubjectModel(R.drawable.image_hsa, getString(R.string.hsa)));

        // Adapter
        SubjectAdapter adapter = new SubjectAdapter(requireContext(), subjectList);
        binding.subjectRecycle.setAdapter(adapter);

        // Slogan
        updateQuote();

        // Countdown
        new CountdownTimer(binding.countdownTextView);

        // Profile image
        profileImageManager.loadProfileImage(binding.iconImage);
        binding.iconImage.setOnClickListener(v -> profileImageManager.requestReadExternalStoragePermission());

        // Load Image Slide
        imageSlideManager.loadImageSlide(binding.ImageSlide, requireActivity(), "BANNER1");

        binding.viewMore.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), ViewMoreActivity.class));
        });

    }

    private void updateQuote() {
        String slogan = quoteManager.getQuoteForToday();
        binding.sologanTextView.setText(slogan);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
