package com.example.datn.helpers;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Calendar;

public class CountdownTimer {

    private final TextView countdownTextView;

    public CountdownTimer(TextView textView) {
        this.countdownTextView = textView;
        startCountdown();
    }

    private void startCountdown() {
        // Ngày thi tốt nghiệp THPT 2025 (ngày 27 tháng 6 năm 2025)
        Calendar endDate = Calendar.getInstance();
        endDate.set(2025, Calendar.JUNE, 27, 0, 0, 0); // 0 giờ ngày 27 tháng 6

        long timeInMillis = endDate.getTimeInMillis() - System.currentTimeMillis();

        new CountDownTimer(timeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                long days = millisUntilFinished / (1000 * 60 * 60 * 24);
                long hours = (millisUntilFinished / (1000 * 60 * 60)) % 24;

                @SuppressLint("DefaultLocale") String countdown = String.format(" Đếm Ngược: %02d Ngày %02d Giờ", days, hours);
                countdownTextView.setText(countdown);
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                countdownTextView.setText("Đã đến ngày thi!");
            }
        }.start();
    }
}
