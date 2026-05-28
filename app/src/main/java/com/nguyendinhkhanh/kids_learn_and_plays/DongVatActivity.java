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

        private int[] images = {
                R.drawable.img_khi,
                R.drawable.img_su_tu,
                R.drawable.img_voi,
                R.drawable.img_cho,
                R.drawable.img_meo,
                R.drawable.img_ga,
                R.drawable.img_heo,
                R.drawable.img_bo,
                R.drawable.img_vit,
                R.drawable.img_ho
        };
        private String[] names = {
                "CON KHỈ", "SƯ TỬ", "CON VOI", "CON CHÓ", "CON MÈO",
                "CON GÀ", "CON HEO", "CON BÒ", "CON VỊT", "CON HỔ"
        };
        private int[] sounds = {
                R.raw.sound_khi,
                R.raw.sound_su_tu,
                R.raw.sound_voi,
                R.raw.sound_cho,
                R.raw.sound_meo,
                R.raw.sound_ga,
                R.raw.sound_heo,
                R.raw.sound_bo,
                R.raw.sound_vit,
                R.raw.sound_ho
        };

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

            // Xử lý nút Phải (Next) - Vòng lặp
            btnNext.setOnClickListener(v -> {
                SoundManager.playClick(this);
                currentIndex = (currentIndex + 1) % names.length;
                updateUI();
            });

            // Xử lý nút Trái (Prev) - Vòng lặp
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


        // Hàm cập nhật Ảnh và Tên lên màn hình
        private void updateUI() {
            imgAnimal.setImageResource(images[currentIndex]);
            tvAnimalName.setText(names[currentIndex]);

            stopSound();
        }

        // Hàm chuyên dùng để dập tắt âm thanh đang phát
        private void stopSound() {
            if (mediaPlayer != null) {
                mediaPlayer.release(); // Giải phóng bộ nhớ của bài nhạc cũ
                mediaPlayer = null;
            }
        }

        // Hàm phát âm thanh
        private void playSound(int soundResource) {
            stopSound(); // Gọi hàm tắt âm thanh trước cho an toàn, tránh đè nhạc
            mediaPlayer = MediaPlayer.create(this, soundResource);
            mediaPlayer.start();
        }

        // Tắt nhạc khi thoát hẳn khỏi Activity (bấm nút Back của điện thoại)
        @Override
        protected void onDestroy() {
            super.onDestroy();
            stopSound();
        }
    }