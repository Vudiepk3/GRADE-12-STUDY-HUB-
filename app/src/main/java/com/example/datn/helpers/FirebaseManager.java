package com.example.datn.helpers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Lớp quản lý Firebase, cung cấp các phương thức để tương tác với Firebase Database
public class FirebaseManager {
    private final DatabaseReference databaseReference;

    // Constructor: khởi tạo đối tượng FirebaseManager và lấy tham chiếu đến gốc của Firebase Database
    public FirebaseManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    // Phương thức trả về một tham chiếu đến nút con cụ thể trong Firebase Database dựa trên đường dẫn cung cấp
    public DatabaseReference getReference(String path) {
        return databaseReference.child(path);
    }
}
