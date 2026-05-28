package com.nguyendinhkhanh.kids_learn_and_plays;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChuCaiActivity extends AppCompatActivity {

    private ImageView btnPrev, btnPlaySound, btnNext, btnBack;
    private TextView tvBigLetter;
    private MediaPlayer mediaPlayer;

    // 1. MẢNG 29 CHỮ CÁI TIẾNG VIỆT
    private String[] letters = {
            "A", "Ă", "Â", "B", "C", "D", "Đ", "E", "Ê", "G",
            "H", "I", "K", "L", "M", "N", "O", "Ô", "Ơ", "P",
            "Q", "R", "S", "T", "U", "Ư", "V", "X", "Y"
    };

    // 2. MẢNG MÀU SẮC (6 màu rực rỡ dành cho trẻ em)
    private String[] textColors = {
            "#FF7043", // Cam
            "#42A5F5", // Xanh dương
            "#66BB6A", // Xanh lá
            "#FFCA28", // Vàng
            "#AB47BC", // Tím
            "#EC407A"  // Hồng
    };

    // 3. Mảng file âm thanh
    private int[] sounds = {
            R.raw.sound_a, R.raw.sound_aw, R.raw.sound_aa, R.raw.sound_b, R.raw.sound_c,
            R.raw.sound_d, R.raw.sound_dd, R.raw.sound_e, R.raw.sound_ee, R.raw.sound_g,
            R.raw.sound_h, R.raw.sound_i, R.raw.sound_k, R.raw.sound_l, R.raw.sound_m,
            R.raw.sound_n, R.raw.sound_o, R.raw.sound_oo, R.raw.sound_ow, R.raw.sound_p,
            R.raw.sound_q, R.raw.sound_r, R.raw.sound_s, R.raw.sound_t, R.raw.sound_u,
            R.raw.sound_uw, R.raw.sound_v, R.raw.sound_x, R.raw.sound_y
    };

    private int currentIndex = 0; // Bắt đầu từ chữ A

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chu_cai);

        // Ánh xạ
        tvBigLetter = findViewById(R.id.tv_big_letter);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnPlaySound = findViewById(R.id.btn_play_sound);
        btnBack = findViewById(R.id.btn_back);

        updateUI();

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            SoundManager.playClick(this); // Tiếng chạm
            // Chuyển tới: Cộng 1 rồi chia lấy dư cho tổng số chữ cái
            currentIndex = (currentIndex + 1) % letters.length;
            updateUI();
        });

        btnPrev.setOnClickListener(v -> {
            SoundManager.playClick(this);
            // Lùi lại: Trừ 1, cộng thêm tổng số chữ, rồi chia lấy dư (để tránh số âm)
            currentIndex = (currentIndex - 1 + letters.length) % letters.length;
            updateUI();
        });

        // (Bạn mở comment dòng này ra khi đã có đủ file âm thanh nhé)
        btnPlaySound.setOnClickListener(v -> playSound(sounds[currentIndex]));
    }

    private void updateUI() {
        // Cập nhật chữ cái
        tvBigLetter.setText(letters[currentIndex]);

        // Đổi màu luân phiên cho đẹp mắt
        String currentColor = textColors[currentIndex % textColors.length];
        tvBigLetter.setTextColor(Color.parseColor(currentColor));

        // NGẮT ÂM THANH NGAY KHI CHUYỂN CHỮ KHÁC
        stopSound();
    }

    // Hàm chuyên dùng để dập tắt âm thanh đang phát
    private void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Giải phóng bộ nhớ
            mediaPlayer = null;
        }
    }

    // Hàm phát âm thanh
    private void playSound(int soundResource) {
        stopSound(); // Tắt tiếng cũ trước khi phát tiếng mới cho an toàn
        mediaPlayer = MediaPlayer.create(this, soundResource);
        mediaPlayer.start();
    }

    // Tắt nhạc khi thoát khỏi màn hình Chữ Cái
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSound();
    }
}