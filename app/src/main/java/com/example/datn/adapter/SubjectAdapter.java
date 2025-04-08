package com.example.datn.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.datn.activity.main.ShowDocumentActivity;
import com.example.datn.databinding.ItemSubjectBinding;
import com.example.datn.models.SubjectModel;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private final List<SubjectModel> subjectList;
    private final Context context;

    public SubjectAdapter(Context context, List<SubjectModel> subjectList) {
        this.context = context;
        this.subjectList = subjectList;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSubjectBinding binding = ItemSubjectBinding.inflate(inflater, parent, false);
        return new SubjectViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        SubjectModel subject = subjectList.get(position);
        holder.binding.imgSubject.setImageResource(subject.getImageResId());
        holder.binding.nameSubject.setText(subject.getName());

        holder.binding.getRoot().setOnClickListener(v -> navigateToSubject(subject.getName()));
    }

    private void navigateToSubject(String subjectName) {
        Intent subjectActivity = new Intent(context, ShowDocumentActivity.class);
        subjectActivity.putExtra("subjectName", subjectName);
        context.startActivity(subjectActivity);
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    static class SubjectViewHolder extends RecyclerView.ViewHolder {
        ItemSubjectBinding binding;

        public SubjectViewHolder(@NonNull ItemSubjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
