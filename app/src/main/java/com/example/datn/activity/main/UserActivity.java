package com.example.datn.activity.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.datn.R;
import com.example.datn.activity.onboarding.OnboardingActivity;
import com.example.datn.adapter.EditProfileAdapter;
import com.example.datn.databinding.ActivityUserBinding;
import com.example.datn.firebase.FireBaseClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class UserActivity extends AppCompatActivity {
    private ActivityUserBinding binding;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        loadUserInfo();
        setUpClick();
    }
    private void loadUserInfo() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            boolean isGoogleUser = false;

            for (UserInfo profile : currentUser.getProviderData()) {
                if ("google.com".equals(profile.getProviderId())) {
                    isGoogleUser = true;
                    break;
                }
            }

            if (isGoogleUser) {
                // Lấy thông tin từ FirebaseAuth (đăng nhập Gmail)
                String name = currentUser.getDisplayName();
                String email = currentUser.getEmail();
                Uri photoUrl = currentUser.getPhotoUrl();

                binding.tvUserName.setText(name != null ? name : "N/A");
                binding.tvUserEmail.setText(email != null ? email : "N/A");

                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.img_user)
                        .error(R.drawable.img_user)
                        .into(binding.iconImage);
            } else {
                // Nếu không phải Gmail -> lấy từ FireBaseClass
                FireBaseClass.getUserInfo(userInfo -> {
                    if (userInfo != null) {
                        binding.tvUserName.setText(userInfo.getName());
                        binding.tvUserEmail.setText(currentUser.getEmail());

                        Glide.with(this)
                                .load(userInfo.getImage())
                                .placeholder(R.drawable.img_user)
                                .error(R.drawable.img_user)
                                .into(binding.iconImage);
                    } else {
                        binding.tvUserName.setText("N/A");
                        binding.tvUserEmail.setText("N/A");
                    }
                });
            }
        }
    }
    private void setUpClick() {
        binding.cardImage.setOnClickListener(v -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            boolean isGoogleUser = false;

            if (currentUser != null) {
                for (UserInfo profile : currentUser.getProviderData()) {
                    if ("google.com".equals(profile.getProviderId())) {
                        isGoogleUser = true;
                        break;
                    }
                }
            }

            if (isGoogleUser) {
                // Nếu là người dùng Gmail → không cho sửa hồ sơ
                Toast.makeText(this, "Tài khoản Google không thể chỉnh sửa thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu là người dùng thường → cho phép chỉnh sửa
                EditProfileAdapter bottomSheetFragment = new EditProfileAdapter(this);
                bottomSheetFragment.show(this.getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        binding.cvSignOut.setOnClickListener(v -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                auth.signOut();
                Intent intent = new Intent(this, OnboardingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }

}