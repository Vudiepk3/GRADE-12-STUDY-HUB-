package com.example.datn.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.datn.R;
import com.example.datn.activity.main.UserActivity;
import com.example.datn.activity.main.ViewMoreActivity;
import com.example.datn.adapter.ScholarshipAdapter;
import com.example.datn.adapter.SubjectAdapter;
import com.example.datn.databinding.FragmentHomeBinding;
import com.example.datn.helpers.CountdownTimer;
import com.example.datn.helpers.FirebaseManager;
import com.example.datn.helpers.ImageSlideManager;
import com.example.datn.helpers.ProfileImageManager;
import com.example.datn.helpers.QuoteManager;
import com.example.datn.models.NewsModel;
import com.example.datn.models.SubjectModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private ImageSlideManager imageSlideManager;
    private ProfileImageManager profileImageManager;
    private QuoteManager quoteManager;
    private List<NewsModel> dataList;
    private ScholarshipAdapter adapter;

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
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int itemWidth = screenWidth / 5;
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
        SubjectAdapter adapter = new SubjectAdapter(itemWidth,requireContext(), subjectList);
        binding.subjectRecycle.setAdapter(adapter);

        // Slogan
        updateQuote();
        loadData();

        // Countdown
        new CountdownTimer(binding.countdownTextView);

        // Profile image
        profileImageManager.loadProfileImage(binding.iconImage);
//        binding.iconImage.setOnClickListener(v -> profileImageManager.requestReadExternalStoragePermission());

        // Load Image Slide
        imageSlideManager.loadImageSlide(binding.ImageSlide, requireActivity(), "BANNER1");

        binding.viewMore.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), ViewMoreActivity.class));
        });
        binding.iconImage.setOnClickListener(v->{
            startActivity(new Intent(requireActivity(), UserActivity.class));
        });
    }
    private void loadData() {
        binding.recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        dataList = new ArrayList<>();
        adapter = new ScholarshipAdapter(dataList, requireContext());
        binding.recyclerView.setAdapter(adapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("News");
        Query query = databaseReference.orderByChild("timestamp");

        ValueEventListener eventListener = query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    NewsModel news = itemSnapshot.getValue(NewsModel.class);
                    if (news != null && news.getTypeNews().equals("SCHOLARSHIP")) {
                        news.setKey(itemSnapshot.getKey());
                        dataList.add(news);
                    }
                }
                Collections.reverse(dataList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
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
