package com.example.datn.activity.main;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.datn.adapter.QuizListAdapter;
import com.example.datn.databinding.ActivityFullQuizTestBinding;

import com.example.datn.models.QuizModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FullQuizTestActivity extends AppCompatActivity {
    private ActivityFullQuizTestBinding binding;
    private List<QuizModel> quizModelList;
    private QuizListAdapter quizListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullQuizTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
    private void getDataFromFirebase(){

            binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

            quizModelList = new ArrayList<>();
            quizListAdapter = new QuizListAdapter(quizModelList);
            binding.recyclerView.setAdapter(quizListAdapter);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Test");
            Query query = databaseReference.orderByChild("timestamp");

            ValueEventListener eventListener = query.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   quizModelList.clear();
                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        QuizModel quizModel = snapshot.getValue(QuizModel.class);
                            quizModelList.add(quizModel);
                    }
                    Collections.reverse(quizModelList);
                    quizListAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

    }
}