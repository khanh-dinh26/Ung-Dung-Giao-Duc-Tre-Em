package com.nguyendinhkhanh.kids_learn_and_plays;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView tvProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progress_bar);
        tvProgress = findViewById(R.id.tv_progress);

        // Tạo hiệu ứng số chạy từ 0 đến 100 trong đúng 2.5 giây
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(2500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                progressBar.setProgress(progress);
                tvProgress.setText(progress + "%");
            }
        });
        animator.start();
        // Chờ 2.5 giây (khi thanh tải đầy 100%) rồi tự động nhảy sang màn hình chính
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng Splash lại để bé ấn nút Back không bị quay ngược về màn hình tải
        }, 2500);
    }
}