package com.example.datn.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.datn.activity.main.ViewDocumentActivity;
import com.example.datn.databinding.ItemDocumentBinding;
import com.example.datn.models.DocumentModel;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.MyDocumentViewHolder> {

    private final Context context;
    private final List<DocumentModel> dataList;

    public DocumentAdapter(Context context, List<DocumentModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyDocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDocumentBinding binding = ItemDocumentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyDocumentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDocumentViewHolder holder, int position) {
        DocumentModel document = dataList.get(position);
        holder.binding.txtTitle.setText(document.getTitle());
        holder.binding.txtSubject.setText(document.getSubjectName());

        holder.binding.iteamDocument.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewDocumentActivity.class);
            intent.putExtra("title", document.getTitle());
            intent.putExtra("pdf", document.getLinkDocument());
            intent.putExtra("key", document.getKey());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<DocumentModel> newData) {
        dataList.clear();
        dataList.addAll(newData);
        notifyDataSetChanged();
    }

    public static class MyDocumentViewHolder extends RecyclerView.ViewHolder {

        private final ItemDocumentBinding binding;

        public MyDocumentViewHolder(ItemDocumentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
