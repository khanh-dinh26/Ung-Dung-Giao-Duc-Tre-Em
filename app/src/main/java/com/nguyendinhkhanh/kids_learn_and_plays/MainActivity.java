package com.nguyendinhkhanh.kids_learn_and_plays;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // ĐÃ XÓA biến MediaPlayer cũ vì từ nay BackgroundMusicManager sẽ quản lý nhạc

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Chỉ khai báo bottomNav đúng 1 lần ở đây
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // BẬT NHẠC NỀN CHUNG BẰNG MANAGER
        BackgroundMusicManager.startMainMusic(this);

        // Hiển thị Fragment Khám Phá mặc định khi vừa mở app lên
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new KhamPhaFragment()).commit();
        }

        // Bắt sự kiện khi bấm vào các nút trên thanh Menu
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            SoundManager.playClick(MainActivity.this); // Kêu tiếng click chạm
            int itemId = item.getItemId();

            if (itemId == R.id.nav_kham_pha) {
                selectedFragment = new KhamPhaFragment();
            } else if (itemId == R.id.nav_do_vui) {
                selectedFragment = new DoVuiFragment();
            } else if (itemId == R.id.nav_truyen) {
                selectedFragment = new TruyenFragment();
            } else if (itemId == R.id.nav_yeu_thich) {
                selectedFragment = new YeuThichFragment();
            }

            // Thay thế Fragment hiện tại bằng Fragment mới
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, selectedFragment).commit();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // GIẢI PHÓNG TOÀN BỘ NHẠC KHI BÉ THOÁT HẲN APP
        BackgroundMusicManager.releaseAll();
    }
}