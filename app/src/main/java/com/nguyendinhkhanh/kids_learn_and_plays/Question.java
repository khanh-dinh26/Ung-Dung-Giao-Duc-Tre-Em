package com.nguyendinhkhanh.kids_learn_and_plays;

public class Question {
    private String text;         // Câu hỏi hiển thị trên màn hình (VD: "Bé hãy tìm CON KHỈ nhé!")
    private int audioResId;      // File âm thanh (VD: R.raw.sound_khi - Đọc "Con khỉ... khẹc khẹc")
    private int[] imageOptions;  // Mảng chứa 4 bức ảnh để làm đáp án (A, B, C, D)
    private int correctIndex;    // Vị trí của đáp án đúng (0, 1, 2 hoặc 3)

    // Constructor (Hàm tạo)
    public Question(String text, int audioResId, int[] imageOptions, int correctIndex) {
        this.text = text;
        this.audioResId = audioResId;
        this.imageOptions = imageOptions;
        this.correctIndex = correctIndex;
    }

    // Các hàm Getter để lấy dữ liệu ra
    public String getText() { return text; }
    public int getAudioResId() { return audioResId; }
    public int[] getImageOptions() { return imageOptions; }
    public int getCorrectIndex() { return correctIndex; }
}