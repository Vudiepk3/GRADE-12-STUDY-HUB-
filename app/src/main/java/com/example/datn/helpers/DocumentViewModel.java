package com.example.datn.helpers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.datn.models.DocumentModel;

import java.util.ArrayList;
import java.util.List;

public class DocumentViewModel extends ViewModel {
    // Khai báo MutableLiveData để lưu trữ danh sách tài liệu
    private final MutableLiveData<List<DocumentModel>> documents = new MutableLiveData<>(new ArrayList<>());

    // Phương thức trả về LiveData chứa danh sách tài liệu
    public LiveData<List<DocumentModel>> getDocuments() {
        return documents;
    }

    // Phương thức cập nhật danh sách tài liệu trong ViewModel
    public void setDocuments(List<DocumentModel> documentList) {
        documents.setValue(documentList); // Cập nhật danh sách tài liệu mới
    }

}
