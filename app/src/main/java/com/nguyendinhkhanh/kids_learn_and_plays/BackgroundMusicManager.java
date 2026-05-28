package com.nguyendinhkhanh.kids_learn_and_plays;

import android.content.Context;
import android.media.MediaPlayer;

public class BackgroundMusicManager {
    private static MediaPlayer mainMusicPlayer;
    private static MediaPlayer gameMusicPlayer;

    // ==========================================
    // 1. QUẢN LÝ NHẠC NỀN TOÀN APP (bg_main)
    // ==========================================
    public static void startMainMusic(Context context) {
        if (mainMusicPlayer == null) {
            mainMusicPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.bg_main);
            if (mainMusicPlayer != null) {
                mainMusicPlayer.setLooping(true);
                // Dùng OnPreparedListener để fix triệt để lỗi 1, -2147483648 (Chỉ phát khi đã load xong)
                mainMusicPlayer.setOnPreparedListener(MediaPlayer::start);
            }
        } else if (!mainMusicPlayer.isPlaying()) {
            mainMusicPlayer.start();
        }
    }

    public static void pauseMainMusic() {
        if (mainMusicPlayer != null && mainMusicPlayer.isPlaying()) {
            mainMusicPlayer.pause();
        }
    }

    public static void resumeMainMusic() {
        if (mainMusicPlayer != null && !mainMusicPlayer.isPlaying()) {
            mainMusicPlayer.start();
        }
    }

    // ==========================================
    // 2. QUẢN LÝ NHẠC NỀN GAME ĐỐ VUI (bg_game)
    // ==========================================
    public static void startGameMusic(Context context) {
        pauseMainMusic(); // Tự động tắt nhạc Main đi nhường chỗ cho nhạc Game
        if (gameMusicPlayer == null) {
            gameMusicPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.bg_game);
            if (gameMusicPlayer != null) {
                gameMusicPlayer.setLooping(true);
                gameMusicPlayer.setOnPreparedListener(MediaPlayer::start);
            }
        } else if (!gameMusicPlayer.isPlaying()) {
            gameMusicPlayer.start();
        }
    }

    public static void stopGameMusic() {
        if (gameMusicPlayer != null && gameMusicPlayer.isPlaying()) {
            gameMusicPlayer.pause();
            gameMusicPlayer.seekTo(0); // Tua lại từ đầu cho lần chơi sau
        }
        resumeMainMusic(); // Tự động bật lại nhạc Main
    }

    // ==========================================
    // 3. DỌN DẸP KHI THOÁT APP
    // ==========================================
    public static void releaseAll() {
        if (mainMusicPlayer != null) {
            mainMusicPlayer.release();
            mainMusicPlayer = null;
        }
        if (gameMusicPlayer != null) {
            gameMusicPlayer.release();
            gameMusicPlayer = null;
        }
    }
}