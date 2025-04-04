package com.example.datn.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.datn.R;
import com.example.datn.activity.main.ShowDocumentActivity;
import com.example.datn.helpers.CountdownTimer;
import com.example.datn.helpers.FirebaseManager;
import com.example.datn.helpers.ImageSlideManager;
import com.example.datn.helpers.ProfileImageManager;
import com.example.datn.helpers.QuoteManager;


public class HomeFragment extends Fragment {
    private ImageSlideManager imageSlideManager;
    private ProfileImageManager profileImageManager;
    private QuoteManager quoteManager;
    private TextView sloganTextView;
    private final String[] subjectNames = {
            "Toán Học", "Văn Học", "Tiếng Anh", "Vật Lý", "Hoá Học",
            "Sinh Học", "Lịch Sử", "Địa Lý", "Giáo Dục Công Dân",
            "Đánh Giá Tư Duy", "Đánh Giá Năng Lực"
    };
    private final int[] cardViewIds = {
            R.id.mathsCard, R.id.literatureCard, R.id.englishCard, R.id.physicsCard,
            R.id.chemistryCard, R.id.biologyCard, R.id.historyCard, R.id.geographyCard,
            R.id.civicEducationCard, R.id.tsaCard, R.id.hsaCard
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo các Class thừa helpers
        FirebaseManager firebaseManager = new FirebaseManager();
        imageSlideManager = new ImageSlideManager(firebaseManager);
        profileImageManager = new ProfileImageManager(this);
        quoteManager = new QuoteManager(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sloganTextView = view.findViewById(R.id.sologanTextView);
        // ImageView để hiển thị ảnh đại diện
        ImageView imageView = view.findViewById(R.id.iconImage);

        // Hiển thị slogan
        updateQuote();

        TextView countdownTextView = view.findViewById(R.id.countdownTextView);
        new CountdownTimer(countdownTextView);

        // Hiển thị ảnh đại diện
        profileImageManager.loadProfileImage(imageView);

        // Gắn sự kiện click để thay đổi ảnh đại diện
        imageView.setOnClickListener(v -> profileImageManager.requestReadExternalStoragePermission());

        // Load slide ảnh
        imageSlideManager.loadImageSlide(view.findViewById(R.id.ImageSlide), requireActivity(),"BANNER1");

        // Load tài liệu các môn học
        setupSubjectNavigation(view);
    }
    // cập nhật sologan
    private void updateQuote() {
        String slogan = quoteManager.getQuoteForToday();
        sloganTextView.setText(slogan);
    }
    // cài đặt tên môn học
    private void setupSubjectNavigation(View view) {
        for (int i = 0; i < cardViewIds.length; i++) {
            LinearLayout cardView = view.findViewById(cardViewIds[i]);
            final String subjectName = subjectNames[i];
            cardView.setOnClickListener(v -> navigateToSubject(subjectName));
        }
    }
    // chuyến đến activity môn học
    private void navigateToSubject(String subjectName) {
        Intent subjectActivity = new Intent(requireActivity(), ShowDocumentActivity.class);
        subjectActivity.putExtra("subjectName", subjectName);
        startActivity(subjectActivity);
    }

}
