package com.example.datn.activity.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.datn.R;
import com.example.datn.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class ShowDocumentActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_document);

        // Lấy subjectName từ Intent
        String subjectName = getIntent().getStringExtra("subjectName");

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);

        // Thêm các tab cho Ôn Tập và Đề thi
        tabLayout.addTab(tabLayout.newTab().setText("Ôn Tập"));
        tabLayout.addTab(tabLayout.newTab().setText("Đề thi"));

        // Thiết lập ViewPagerAdapter với subjectName
        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager, getLifecycle(), subjectName);
        viewPager2.setAdapter(adapter);

        // Đồng bộ TabLayout và ViewPager2
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

}