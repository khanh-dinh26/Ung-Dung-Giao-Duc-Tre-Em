package com.nguyendinhkhanh.kids_learn_and_plays;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Hiển thị Fragment Khám Phá mặc định khi vừa mở app lên
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new KhamPhaFragment()).commit();
        }

        // Bắt sự kiện khi bấm vào các nút trên thanh Menu
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
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

}