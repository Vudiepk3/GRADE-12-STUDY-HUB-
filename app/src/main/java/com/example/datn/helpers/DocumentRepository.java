package com.example.datn.helpers;

import androidx.annotation.NonNull;


import com.example.datn.models.DocumentModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DocumentRepository {
    private final DatabaseReference databaseReference;

    public DocumentRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("documents");
    }

    public void fetchDocuments(String subjectName,String typeDocument, DataCallback callback) {
        Query query = databaseReference.orderByChild("timestamp");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DocumentModel> documentList = new ArrayList<>();
                boolean isReverseOrder = false; // Biến để kiểm tra có cần đảo danh sách hay không

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DocumentModel dataClass = itemSnapshot.getValue(DocumentModel.class);
                    if (dataClass != null) {
                        // Thêm điều kiện lọc
                        if (subjectName.equals(dataClass.getSubjectName()) && typeDocument.equals(dataClass.getTypeDocument())) {
                            documentList.add(dataClass);
                        }
                        // Kiểm tra xem loại tài liệu có cần đảo ngược không
                        if ("DETHI".equals(dataClass.getTypeDocument())) {
                            isReverseOrder = true;
                        }
                    }
                }
                // Đảo ngược danh sách nếu cần
                if (isReverseOrder) {
                    Collections.reverse(documentList);
                }
                callback.onSuccess(documentList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public interface DataCallback {
        void onSuccess(List<DocumentModel> documents);
        void onError(String errorMessage);
    }
}
