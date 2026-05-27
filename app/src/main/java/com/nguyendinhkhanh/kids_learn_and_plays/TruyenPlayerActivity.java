package com.nguyendinhkhanh.kids_learn_and_plays;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

public class TruyenPlayerActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ImageView imgCover, btnPlayPause, btnBack;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private Handler seekHandler = new Handler();
    private Runnable seekRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truyen_player);

        // Ánh xạ View
        tvTitle = findViewById(R.id.tv_player_title);
        imgCover = findViewById(R.id.img_player_cover);
        btnPlayPause = findViewById(R.id.btn_player_play_pause);
        btnBack = findViewById(R.id.btn_player_back);
        seekBar = findViewById(R.id.player_seekbar);

        // Nhận liên kết dữ liệu gửi sang từ Adapter
        String tenTruyen = getIntent().getStringExtra("TEN_TRUYEN");
        String linkAnh = getIntent().getStringExtra("LINK_ANH");
        String linkAudio = getIntent().getStringExtra("LINK_AUDIO");

        tvTitle.setText(tenTruyen);
        Glide.with(this).load(linkAnh).into(imgCover);

        // Xử lý nút back quay lại danh sách truyện
        btnBack.setOnClickListener(v -> {
            SoundManager.playClick(this);
            finish();
        });

        // KHỞI TẠO VÀ STREAM AUDIO TRỰC TUYẾN TỪ URL
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(linkAudio); // Cài link Audio online (Direct link Google Drive/Firebase)
            mediaPlayer.prepareAsync(); // Luồng chạy ngầm tải nhạc trực tuyến tránh đơ màn hình
            Toast.makeText(this, "Đang tải câu chuyện qua Internet...", Toast.LENGTH_SHORT).show();

            mediaPlayer.setOnPreparedListener(mp -> {
                // Khi nhạc tải trực tuyến xong -> Bắt đầu phát
                mp.start();
                seekBar.setMax(mediaPlayer.getDuration()); // Cài mốc tối đa cho SeekBar bằng độ dài bài nhạc
                updateSeekBar(); // Chạy bộ cập nhật thời gian liên tục
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                // Khi nghe hết câu truyện -> Tự động đổi icon nút về dạng Play
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            });

        } catch (IOException e) {
            Toast.makeText(this, "Lỗi link âm thanh không hợp lệ!", Toast.LENGTH_SHORT).show();
        }

        // Logic xử lý nút Tạm Dừng / Chơi Tiếp (Play/Pause)
        btnPlayPause.setOnClickListener(v -> {
            SoundManager.playClick(this);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                updateSeekBar();
            }
        });

        // Xử lý khi bé lấy tay kéo thanh SeekBar để tua nhanh câu chuyện
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress); // Tua nhạc đến giây tương ứng
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // Tiến trình đồng bộ thanh tua nhạc khớp thời gian thực (real-time)
    private void updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            seekRunnable = this::updateSeekBar;
            seekHandler.postDelayed(seekRunnable, 1000); // Lặp lại tiến trình sau mỗi 1 giây (1000ms)
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause(); // Tạm tắt nhạc nếu bé ẩn app xuống nền hoặc có cuộc gọi đến
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Giải phóng bộ nhớ giải mã âm thanh khi bé tắt màn hình phát truyện
            mediaPlayer = null;
        }
        seekHandler.removeCallbacks(seekRunnable); // Dừng bộ đếm thời gian gỡ đơ máy
    }
}