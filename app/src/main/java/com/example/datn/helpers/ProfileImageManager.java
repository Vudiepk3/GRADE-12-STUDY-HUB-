package com.example.datn.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

// Lớp quản lý ảnh hồ sơ, cung cấp các phương thức để tải và lưu ảnh hồ sơ
public class ProfileImageManager {
    private final Fragment fragment;
    private final ActivityResultLauncher<Intent> pickImageLauncher;
    private final ActivityResultLauncher<String> requestPermissionLauncher;

    // Constructor: khởi tạo đối tượng ProfileImageManager và các launcher để chọn ảnh và yêu cầu quyền
    public ProfileImageManager(Fragment fragment) {
        this.fragment = fragment;

        // Đăng ký launcher để chọn ảnh từ thư viện
        pickImageLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        assert selectedImageUri != null;
                        saveProfileImage(selectedImageUri); // Lưu ảnh hồ sơ
                    }
                }
        );

        // Đăng ký launcher để yêu cầu quyền truy cập bộ nhớ ngoài
        requestPermissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) openImagePicker(); // Mở trình chọn ảnh nếu quyền được cấp
                    else Toast.makeText(fragment.getContext(), "Quyền bị từ chối.", Toast.LENGTH_SHORT).show();
                }
        );
    }

    // Phương thức tải ảnh hồ sơ và hiển thị lên ImageView
    public void loadProfileImage(ImageView imageView) {
        SharedPreferences prefs = fragment.requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        String uri = prefs.getString("profile_image_uri", null);
        if (uri != null) {
            Glide.with(fragment).load(Uri.parse(uri)).apply(RequestOptions.circleCropTransform()).into(imageView);
        }
    }

    // Phương thức yêu cầu quyền truy cập bộ nhớ ngoài
    public void requestReadExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            openImagePicker(); // Mở trình chọn ảnh nếu quyền đã được cấp
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    // Phương thức mở trình chọn ảnh từ thư viện
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    // Phương thức lưu ảnh hồ sơ vào SharedPreferences
    private void saveProfileImage(Uri uri) {
        SharedPreferences prefs = fragment.requireContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        prefs.edit().putString("profile_image_uri", uri.toString()).apply();
        Toast.makeText(fragment.getContext(), "Ảnh đã lưu!", Toast.LENGTH_SHORT).show();
    }
}
