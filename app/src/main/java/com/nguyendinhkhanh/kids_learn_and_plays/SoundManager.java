package com.nguyendinhkhanh.kids_learn_and_plays;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundManager {
    // Biến static để dùng chung một máy phát âm thanh cho toàn app
    private static MediaPlayer clickPlayer;

    // Hàm phát tiếng Click
    public static void playClick(Context context) {
        // Nếu máy phát chưa được tạo, thì tạo mới
        if (clickPlayer == null) {
            // Dùng ApplicationContext để tránh rò rỉ bộ nhớ (Memory Leak)
            clickPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.sound_click);
        }

        // Tua lại từ đầu và phát
        if (clickPlayer != null) {
            clickPlayer.seekTo(0);
            clickPlayer.start();
        }
    }

    // Hàm dọn dẹp bộ nhớ (gọi khi thoát hẳn App)
    public static void release() {
        if (clickPlayer != null) {
            clickPlayer.release();
            clickPlayer = null;
        }
    }
}