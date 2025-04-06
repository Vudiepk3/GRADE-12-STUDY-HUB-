package com.example.datn;

import android.app.Application;

import com.example.datn.repository.BannerRepository;
import com.example.datn.repository.NewsRepository;


public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BannerRepository.getInstance().loadData();
        NewsRepository.getInstance();
    }

    public static MyApplication getInstance() {
        return instance;
    }
}

