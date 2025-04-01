package com.example.datn;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {
    protected VB binding;

    public BaseActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Common.setCustomLocale(this);
        binding = (VB) getLayoutInflater().inflate(getLayoutId(), null, false);
        setContentView(binding.getRoot());
        initViewBase();
    }

    protected abstract int getLayoutId();

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    public abstract void initViewBase();
}