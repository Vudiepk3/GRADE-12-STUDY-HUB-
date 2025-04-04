package com.example.datn.helpers;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.devvu.grade12.models.ImageModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

// Lớp quản lý trình chiếu ảnh từ Firebase và hiển thị trên ImageSlider
public class ImageSlideManager {
    private final FirebaseManager firebaseManager;

    // Constructor: khởi tạo đối tượng ImageSlideManager và nhận tham chiếu đến FirebaseManager
    public ImageSlideManager(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }

    // Phương thức tải và hiển thị hình ảnh trên ImageSlider
    public void loadImageSlide(ImageSlider imageSlider, Activity activity, String banner) {
        List<SlideModel> slideModels = new ArrayList<>(); // Danh sách chứa các SlideModel để hiển thị hình ảnh
        List<String> linkWebsites = new ArrayList<>(); // Danh sách chứa các liên kết website tương ứng

        // Lấy tham chiếu đến nút "SlideImage" trong Firebase Database
        firebaseManager.getReference("SlideImage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                slideModels.clear();
                linkWebsites.clear();

                // Duyệt qua từng phần tử con của nút "SlideImage"
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ImageModel imageModel = itemSnapshot.getValue(ImageModel.class);
                    if (imageModel != null && banner.equals(imageModel.getNoteImage())) {
                        // Thêm SlideModel vào danh sách nếu thỏa mãn điều kiện
                        slideModels.add(new SlideModel(imageModel.getUrlImage(), ScaleTypes.FIT));
                        // Thêm liên kết website vào danh sách
                        linkWebsites.add(imageModel.getLinkWeb());
                    }
                }

                // Cập nhật giao diện người dùng từ thread chính
                activity.runOnUiThread(() -> {
                    imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                    imageSlider.setItemClickListener(i -> openWebsite(linkWebsites, i, activity));
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Hiển thị thông báo lỗi nếu có vấn đề khi tải ảnh
                Toast.makeText(activity, "Lỗi khi tải ảnh!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức mở website dựa trên liên kết trong danh sách
    private void openWebsite(List<String> websites, int index, Context context) {
        if (index >= 0 && index < websites.size()) {
            String link = websites.get(index);
            if (link != null && !link.isEmpty()) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    // Hiển thị thông báo lỗi nếu không thể mở website
                    /*Toast.makeText(context, "Không thể mở website!", Toast.LENGTH_SHORT).show();*/
                }
            }
        }
    }
}
