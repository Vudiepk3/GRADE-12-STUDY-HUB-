package com.example.datn.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.datn.fragment.OnBoardingFragment1;
import com.example.datn.fragment.OnBoardingFragment2;

public class ViewOnboardingAdapter extends FragmentPagerAdapter {

    private final Fragment[] fragments = new Fragment[]{
            new OnBoardingFragment1(),
            new OnBoardingFragment2()
    };

    public ViewOnboardingAdapter(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position >= 0 && position < fragments.length) {
            return fragments[position];
        } else {
            return fragments[0]; // Tránh lỗi nếu position sai
        }
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}

