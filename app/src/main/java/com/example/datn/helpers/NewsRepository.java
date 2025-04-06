package com.example.datn.helpers;

import androidx.annotation.NonNull;

import com.example.datn.models.NewsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Repository dùng để quản lý việc lấy dữ liệu tin tức từ Firebase Realtime Database.
public class NewsRepository {
    private final DatabaseReference databaseReference;

    // Khởi tạo tham chiếu tới nút "News" trong Firebase Realtime Database.
    public NewsRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("News");
    }

    /**
     * Lấy danh sách tin tức từ Firebase và trả về thông qua callback.
     *
     * @param callback Interface cung cấp hai phương thức: onSuccess (khi thành công) và onError (khi có lỗi).
     */
    public void fetchNews(Callback<List<NewsModel>> callback) {
        // Tạo truy vấn Firebase để lấy dữ liệu tin tức, sắp xếp theo "timestamp".
        Query query = databaseReference.orderByChild("timestamp");

        // Lắng nghe dữ liệu từ Firebase thông qua ValueEventListener.
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Tạo danh sách để lưu trữ các đối tượng tin tức.
                List<NewsModel> newsList = new ArrayList<>();

                // Lặp qua tất cả các phần tử con của nút "News".
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    // Chuyển đổi dữ liệu từ Firebase thành đối tượng NewsModel.
                    NewsModel news = itemSnapshot.getValue(NewsModel.class);

                    // Kiểm tra nếu đối tượng không null, thêm vào danh sách và đặt key của Firebase.
                    if (news != null) {
                        news.setKey(itemSnapshot.getKey());
                        newsList.add(news);
                    }
                }

                // Đảo ngược danh sách để hiển thị các tin tức mới nhất ở đầu.
                Collections.reverse(newsList);

                // Trả về dữ liệu thông qua phương thức onSuccess của callback.
                callback.onSuccess(newsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Trả về lỗi thông qua phương thức onError của callback.
                callback.onError(error.getMessage());
            }
        });
    }

    // Interface Callback định nghĩa hai phương thức xử lý kết quả trả về từ Firebase.
    public interface Callback<T> {
        void onSuccess(T data); // Được gọi khi lấy dữ liệu thành công.
        void onError(String error); // Được gọi khi có lỗi xảy ra.
    }
}
