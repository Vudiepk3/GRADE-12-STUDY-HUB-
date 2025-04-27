package com.example.datn.activity.main;

import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datn.adapter.SubjectAdapter;
import com.example.datn.databinding.ActivityViewMoreBinding;

import com.example.datn.R;
import com.example.datn.models.SubjectModel;

import java.util.ArrayList;
import java.util.List;

public class ViewMoreActivity extends AppCompatActivity {
    private ActivityViewMoreBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewMoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setUpClick();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpSubject();
    }

    private void setUpSubject(){

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int itemWidth = screenWidth / 3 - 30;

        // Setup recyclerView
        binding.subjectRecycle.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false));
        // Data
        List<SubjectModel> subjectList = new ArrayList<>();
        subjectList.add(new SubjectModel(R.drawable.image_maths, getString(R.string.maths)));
        subjectList.add(new SubjectModel(R.drawable.image_literature, getString(R.string.literature)));
        subjectList.add(new SubjectModel(R.drawable.image_english, getString(R.string.english)));
        subjectList.add(new SubjectModel(R.drawable.image_physics, getString(R.string.physics)));
        subjectList.add(new SubjectModel(R.drawable.image_chemistry, getString(R.string.chemistry)));
        subjectList.add(new SubjectModel(R.drawable.image_biology, getString(R.string.biology)));
        subjectList.add(new SubjectModel(R.drawable.image_history, getString(R.string.history)));
        subjectList.add(new SubjectModel(R.drawable.image_geography, getString(R.string.geography)));
        subjectList.add(new SubjectModel(R.drawable.image_civiceducation, getString(R.string.civic)));
        subjectList.add(new SubjectModel(R.drawable.image_tsa, getString(R.string.tsa)));
        subjectList.add(new SubjectModel(R.drawable.image_hsa, getString(R.string.hsa)));
        // Adapter
        SubjectAdapter adapter = new SubjectAdapter(itemWidth,this, subjectList);
        binding.subjectRecycle.setAdapter(adapter);
    }
    private void setUpClick(){
        binding.imgBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}