package com.example.datn.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.datn.fragment.ExamFragment;
import com.example.datn.fragment.HomeWorkFragment;


public class ViewPagerAdapter extends FragmentStateAdapter {
    private final String subjectName;
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String subjectName) {
        super(fragmentManager, lifecycle);
        this.subjectName = subjectName; // Lưu subjectName
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        bundle.putString("subjectName", subjectName); // Đưa subjectName vào Bundle

        // Sử dụng if-else để thay thế switch-case
        if (position == 0) {
            fragment = new HomeWorkFragment();
            fragment.setArguments(bundle); // Truyền Bundle vào Fragment
        } else if (position == 1) {
            fragment = new ExamFragment();
            fragment.setArguments(bundle); // Truyền Bundle vào Fragment
        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2; // Số lượng tab
    }
}
