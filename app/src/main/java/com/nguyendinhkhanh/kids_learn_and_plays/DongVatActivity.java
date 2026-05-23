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

public class DongVatActivity extends AppCompatActivity {
    // 1. Khai báo các thành phần giao diện
    private ImageView imgAnimal, btnPrev, btnPlaySound, btnNext, btnBack;
    private TextView tvAnimalName;
    private MediaPlayer mediaPlayer;


    private int[] images = {R.drawable.lion_img, R.drawable.car_img, R.drawable.abc_img}; // Tạm dùng ảnh cũ,
    private String[] names = {"CON KHỈ", "SƯ TỬ", "CON NGỰA"};
    private int[] sounds = {R.raw.tieng_khi, R.raw.tieng_su_tu, R.raw.tieng_ngua};

    // Biến lưu vị trí con vật đang xem (bắt đầu từ 0 là con đầu tiên)
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dong_vat);

        // Ánh xạ View
        imgAnimal = findViewById(R.id.img_animal);
        tvAnimalName = findViewById(R.id.tv_animal_name);
        btnPrev = findViewById(R.id.btn_prev);
        btnNext = findViewById(R.id.btn_next);
        btnPlaySound = findViewById(R.id.btn_play_sound);
        btnBack = findViewById(R.id.btn_back);

        // Hiển thị con vật đầu tiên khi vừa mở trang
        updateUI();

        // Xử lý nút Back (Quay lại trang chủ)
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Phải (Next)
        btnNext.setOnClickListener(v -> {
            if (currentIndex < names.length - 1) {
                currentIndex++; // Tăng vị trí lên 1
                updateUI();
            } else {
                Toast.makeText(this, "Đã đến thẻ cuối cùng!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút Trái (Prev)
        btnPrev.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--; // Giảm vị trí đi 1
                updateUI();
            } else {
                Toast.makeText(this, "Đây là thẻ đầu tiên!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút Phát âm thanh
        btnPlaySound.setOnClickListener(v -> {
            playSound(sounds[currentIndex]);
        });
    }

    // Hàm cập nhật Ảnh và Tên lên màn hình
    private void updateUI() {
        imgAnimal.setImageResource(images[currentIndex]);
        tvAnimalName.setText(names[currentIndex]);
    }

    // Hàm phát âm thanh
    private void playSound(int soundResource) {
        // Nếu đang có âm thanh phát thì dừng lại trước khi phát cái mới
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResource);
        mediaPlayer.start();
    }

    // Tắt nhạc nếu thoát khỏi màn hình này
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}