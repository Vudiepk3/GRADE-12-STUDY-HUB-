package com.example.datn.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.datn.R;
import com.example.datn.adapter.DocumentAdapter;
import com.example.datn.helpers.DocumentRepository;
import com.example.datn.helpers.DocumentViewModel;
import com.example.datn.helpers.NetworkUtils;
import com.example.datn.models.DocumentModel;

import java.util.ArrayList;
import java.util.List;

public class ExamFragment extends Fragment {
    private DocumentViewModel documentViewModel;
    private DocumentAdapter adapter;
    private boolean isConnected = true; // Biến dicom tra trạng thái kết nối mạng
    private BroadcastReceiver networkChangeReceiver;

    public ExamFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_work, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel để lưu trữ và quan sát dữ liệu
        documentViewModel = new ViewModelProvider(this).get(DocumentViewModel.class);

        // Khởi tạo UI và các thành phần trong layout
        initializeUI(view);

        // Cài đặt chức năng tìm kiếm tài liệu
        setupSearchView(view);

        // Đăng ký BroadcastReceiver để lắng nghe thay đổi mạng
        registerNetworkChangeReceiver();

        // Quan sát dữ liệu trong ViewModel và cập nhật giao diện khi dữ liệu thay đổi
        documentViewModel.getDocuments().observe(getViewLifecycleOwner(), documents -> adapter.updateData(documents));

        // Lấy dữ liệu từ Firebase thông qua Repository
        fetchDocuments();
    }

    /**
     * Khởi tạo RecyclerView và Adapter.
     */
    private void initializeUI(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // Tăng hiệu suất khi kích thước item cố định
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        adapter = new DocumentAdapter(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    /**
     * Lấy danh sách tài liệu từ Firebase thông qua DocumentRepository.
     */
    private void fetchDocuments() {
        String subjectName = getArguments() != null ? getArguments().getString("subjectName") : null;
        if (subjectName == null || subjectName.isEmpty()) {
            Toast.makeText(getActivity(), "Tên chủ đề là bắt buộc", Toast.LENGTH_SHORT).show();
            return;
        }

        // Khởi tạo Repository để lấy dữ liệu từ Firebase
        DocumentRepository repository = new DocumentRepository();
        String typeDocument = "DETHI"; // Loại tài liệu cần lấy

        repository.fetchDocuments(subjectName, typeDocument, new DocumentRepository.DataCallback() {
            @Override
            public void onSuccess(List<DocumentModel> documents) {
                documentViewModel.setDocuments(documents); // Cập nhật dữ liệu vào ViewModel
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getActivity(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cài đặt SearchView để hỗ trợ tìm kiếm danh sách tài liệu.
     */
    private void setupSearchView(View view) {
        SearchView searchView = view.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Không thực hiện tìm kiếm khi nhấn submit
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    /**
     * Lọc danh sách tài liệu dựa trên văn bản nhập vào từ SearchView.
     */
    private void searchList(String text) {
        List<DocumentModel> filteredList = new ArrayList<>();
        List<DocumentModel> allDocuments = documentViewModel.getDocuments().getValue();
        if (allDocuments != null) {
            for (DocumentModel document : allDocuments) {
                if (document.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(document);
                }
            }
        }

        if (filteredList.isEmpty() && !text.isEmpty()) {
            Toast.makeText(getActivity(), "Không tìm thấy tài liệu", Toast.LENGTH_SHORT).show();
        }
        adapter.updateData(filteredList);
    }

    /**
     * Đăng ký BroadcastReceiver để lắng nghe thay đổi kết nối mạng.
     */
    private void registerNetworkChangeReceiver() {
        networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isAvailable = NetworkUtils.isNetworkAvailable(context);
                if (isAvailable && !isConnected) {
                    Toast.makeText(context, "Kết nối internet đã được khôi phục", Toast.LENGTH_SHORT).show();
                    isConnected = true;
                } else if (!isAvailable && isConnected) {
                    Toast.makeText(context, "Không có kết nối internet", Toast.LENGTH_SHORT).show();
                    isConnected = false;
                }
            }
        };
        requireActivity().registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Đăng ký lại BroadcastReceiver khi Fragment hiển thị.
     */
    @Override
    public void onResume() {
        super.onResume();
        requireActivity().registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * Hủy đăng ký BroadcastReceiver khi Fragment bị ẩn.
     */
    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(networkChangeReceiver);
    }
}