package com.nguyendinhkhanh.kids_learn_and_plays;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.IOException;

public class TruyenPlayerActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ImageView imgCover, btnPlayPause, btnBack, btnRewind, btnForward;
    private SeekBar seekBar;
    private ProgressBar progressBar;

    private MediaPlayer mediaPlayer;
    private Handler seekHandler = new Handler();
    private Runnable seekRunnable;

    private ObjectAnimator rotateAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truyen_player);
        BackgroundMusicManager.pauseMainMusic();

        // Ánh xạ View
        tvTitle = findViewById(R.id.tv_player_title);
        imgCover = findViewById(R.id.img_player_cover);
        btnPlayPause = findViewById(R.id.btn_player_play_pause);
        btnBack = findViewById(R.id.btn_player_back);
        btnRewind = findViewById(R.id.btn_player_rewind);     // Nút lùi
        btnForward = findViewById(R.id.btn_player_forward);   // Nút tới
        seekBar = findViewById(R.id.player_seekbar);
        progressBar = findViewById(R.id.progress_loading_audio);

        // Nhận liên kết dữ liệu gửi sang từ Adapter
        String tenTruyen = getIntent().getStringExtra("TEN_TRUYEN");
        String linkAnh = getIntent().getStringExtra("LINK_ANH");
        String linkAudio = getIntent().getStringExtra("LINK_AUDIO");

        tvTitle.setText(tenTruyen);
        Glide.with(this).load(linkAnh).into(imgCover);

        // Cài đặt đĩa xoay
        rotateAnimator = ObjectAnimator.ofFloat(imgCover, "rotation", 0f, 360f);
        rotateAnimator.setDuration(15000);
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimator.setInterpolator(new LinearInterpolator());

        // Nút Back
        btnBack.setOnClickListener(v -> {
            SoundManager.playClick(this);
            finish();
        });

        // KHỞI TẠO VÀ STREAM AUDIO TRỰC TUYẾN
        mediaPlayer = new MediaPlayer();
        try {
            progressBar.setVisibility(View.VISIBLE);
            btnPlayPause.setVisibility(View.INVISIBLE);

            mediaPlayer.setDataSource(linkAudio);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                progressBar.setVisibility(View.GONE);
                btnPlayPause.setVisibility(View.VISIBLE);

                mp.start();
                rotateAnimator.start();

                seekBar.setMax(mediaPlayer.getDuration());
                updateSeekBar();
            });

            mediaPlayer.setOnInfoListener((mp, what, extra) -> {
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    progressBar.setVisibility(View.VISIBLE);
                    rotateAnimator.pause();
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    progressBar.setVisibility(View.GONE);
                    if(mediaPlayer.isPlaying()) rotateAnimator.resume();
                }
                return false;
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                rotateAnimator.pause();
            });

        } catch (IOException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Lỗi link âm thanh không hợp lệ!", Toast.LENGTH_SHORT).show();
        }

        // Logic Play / Pause
        btnPlayPause.setOnClickListener(v -> {
            SoundManager.playClick(this);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                rotateAnimator.pause();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            } else {
                mediaPlayer.start();
                rotateAnimator.resume();
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                updateSeekBar();
            }
        });

        // TUA LÙI 10 GIÂY
        btnRewind.setOnClickListener(v -> {
            SoundManager.playClick(this);
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                // Lùi 10.000 ms, nếu nhỏ hơn 0 thì trả về 0 (đầu bài hát)
                int seekPosition = Math.max(currentPosition - 10000, 0);
                mediaPlayer.seekTo(seekPosition);
            }
        });

        // TUA TỚI 10 GIÂY
        btnForward.setOnClickListener(v -> {
            SoundManager.playClick(this);
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();
                // Tiến 10.000 ms, nếu vượt quá độ dài bài hát thì trỏ về đoạn cuối
                int seekPosition = Math.min(currentPosition + 10000, duration);
                mediaPlayer.seekTo(seekPosition);
            }
        });

        // Kéo thanh SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            seekRunnable = this::updateSeekBar;
            seekHandler.postDelayed(seekRunnable, 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
        }
        if (rotateAnimator != null && rotateAnimator.isRunning()) {
            rotateAnimator.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (rotateAnimator != null) {
            rotateAnimator.cancel();
        }
        seekHandler.removeCallbacks(seekRunnable);

        BackgroundMusicManager.resumeMainMusic();
    }
}