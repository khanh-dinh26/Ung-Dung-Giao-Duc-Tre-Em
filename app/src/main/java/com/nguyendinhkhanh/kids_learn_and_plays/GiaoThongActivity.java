package com.nguyendinhkhanh.kids_learn_and_plays;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GiaoThongActivity extends AppCompatActivity {
    private ImageView imgVehicle, btnPrev, btnPlaySound, btnNext, btnBack;
    private TextView tvVehicleName;
    private MediaPlayer mediaPlayer;

    // DANH SÁCH 8 PHƯƠNG TIỆN GIAO THÔNG
    private String[] names = {
            "XE CẢNH SÁT", "XE CỨU HỎA", "XE CỨU THƯƠNG", "Ô TÔ",
            "TÀU HỎA", "MÁY BAY", "XE MÁY", "XE ĐẠP"
    };

    private int[] images = {
            R.drawable.img_xe_canh_sat,
            R.drawable.img_xe_cuu_hoa,
            R.drawable.img_xe_cuu_thuong,
            R.drawable.img_o_to,
            R.drawable.img_tau_hoa,
            R.drawable.img_may_bay,
            R.drawable.img_xe_may,
            R.drawable.img_xe_dap
    };

    private int[] sounds = {
            R.raw.sound_xe_canh_sat,
            R.raw.sound_xe_cuu_hoa,
            R.raw.sound_xe_cuu_thuong,
            R.raw.sound_o_to,
            R.raw.sound_tau_hoa,
            R.raw.sound_may_bay,
            R.raw.sound_xe_may,
            R.raw.sound_xe_dap
    };

    private int currentIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giao_thong);

        // Ánh xạ View
        imgVehicle = findViewById(R.id.img_vehicle);
        tvVehicleName = findViewById(R.id.tv_vehicle_name);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnPlaySound = findViewById(R.id.btn_play_sound);
        btnBack = findViewById(R.id.btn_back);

        // Hiển thị xe đầu tiên
        updateUI();

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Next
        // Xử lý nút Next - Vòng lặp
        btnNext.setOnClickListener(v -> {
            SoundManager.playClick(this);
            currentIndex = (currentIndex + 1) % names.length;
            updateUI();
        });

        // Xử lý nút Prev - Vòng lặp
        btnPrev.setOnClickListener(v -> {
            SoundManager.playClick(this);
            currentIndex = (currentIndex - 1 + names.length) % names.length;
            updateUI();
        });

        // Xử lý nút Phát âm thanh
        btnPlaySound.setOnClickListener(v -> {
            playSound(sounds[currentIndex]);
        });
    }

    private void updateUI() {
        imgVehicle.setImageResource(images[currentIndex]);
        tvVehicleName.setText(names[currentIndex]);
    }

    private void playSound(int soundResource) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResource);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}