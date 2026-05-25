package com.nguyendinhkhanh.kids_learn_and_plays; // Nhớ kiểm tra lại tên package

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class DoVuiFragment extends Fragment {

    // Khai báo layout
    private LinearLayout layoutStartMenu, layoutGamePlay;
    private Button btnStartGame;

    // Khai báo view trong game
    private TextView tvQuestion, tvScore;
    private ImageView btnPlayQuestion;
    private ImageView imgOption1, imgOption2, imgOption3, imgOption4;
    private MaterialCardView cardOption1, cardOption2, cardOption3, cardOption4;

    private MediaPlayer mediaPlayer;
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int userScore = 0;

    public DoVuiFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_do_vui, container, false);

        // 1. Ánh xạ layout Menu và Game
        layoutStartMenu = view.findViewById(R.id.layout_start_menu);
        layoutGamePlay = view.findViewById(R.id.layout_game_play);
        btnStartGame = view.findViewById(R.id.btn_start_game);

        // 2. Ánh xạ thành phần Game
        tvQuestion = view.findViewById(R.id.tv_question);
        tvScore = view.findViewById(R.id.tv_score);
        btnPlayQuestion = view.findViewById(R.id.btn_play_question);

        imgOption1 = view.findViewById(R.id.img_option_1);
        imgOption2 = view.findViewById(R.id.img_option_2);
        imgOption3 = view.findViewById(R.id.img_option_3);
        imgOption4 = view.findViewById(R.id.img_option_4);

        cardOption1 = view.findViewById(R.id.card_option_1);
        cardOption2 = view.findViewById(R.id.card_option_2);
        cardOption3 = view.findViewById(R.id.card_option_3);
        cardOption4 = view.findViewById(R.id.card_option_4);

        // Chuẩn bị dữ liệu
        setupQuestions();

        // 3. XỬ LÝ NÚT BẮT ĐẦU CHƠI
        btnStartGame.setOnClickListener(v -> {
            // Ẩn Menu đi, Hiện màn hình chơi game lên
            layoutStartMenu.setVisibility(View.GONE);
            layoutGamePlay.setVisibility(View.VISIBLE);

            // Reset điểm số và Load câu hỏi đầu tiên
            userScore = 0;
            currentQuestionIndex = 0;
            loadQuestion(currentQuestionIndex);
        });

        // Xử lý nút phát lại âm thanh câu hỏi
        btnPlayQuestion.setOnClickListener(v -> playSound(questionList.get(currentQuestionIndex).getAudioResId()));

        // Xử lý click đáp án
        cardOption1.setOnClickListener(v -> checkAnswer(0));
        cardOption2.setOnClickListener(v -> checkAnswer(1));
        cardOption3.setOnClickListener(v -> checkAnswer(2));
        cardOption4.setOnClickListener(v -> checkAnswer(3));

        return view;
    }

    private void setupQuestions() {
        questionList = new ArrayList<>();

        // Thêm 5 câu đố (đổi tên ảnh/âm thanh khớp với hệ thống drawables của bạn)
        int[] options1 = {R.drawable.img_khi, R.drawable.img_su_tu, R.drawable.img_voi, R.drawable.img_cho};
        questionList.add(new Question("Bé hãy chọn hình của CON KHỈ nhé!", R.raw.sound_khi, options1, 0));

        int[] options2 = {R.drawable.img_meo, R.drawable.img_heo, R.drawable.img_voi, R.drawable.img_ga};
        questionList.add(new Question("Đâu là CON VOI vậy bé nhỉ?", R.raw.sound_voi, options2, 2));

        int[] options3 = {R.drawable.img_bo, R.drawable.img_vit, R.drawable.img_ho, R.drawable.img_meo};
        questionList.add(new Question("Bé chỉ giúp cô đâu là CON MÈO với nào?", R.raw.sound_meo, options3, 3));

        int[] options4 = {R.drawable.img_cho, R.drawable.img_xe_canh_sat, R.drawable.img_su_tu, R.drawable.img_heo};
        questionList.add(new Question("Hình nào là XE CẢNH SÁT hả bé?", R.raw.sound_xe_canh_sat, options4, 1));

        int[] options5 = {R.drawable.img_cho, R.drawable.img_khi, R.drawable.img_ga, R.drawable.img_vit};
        questionList.add(new Question("Tiếng Gâu Gâu là của bạn nào đây?", R.raw.sound_cho, options5, 0));
    }

    private void loadQuestion(int index) {
        Question currentQuestion = questionList.get(index);
        tvQuestion.setText(currentQuestion.getText());

        int[] options = currentQuestion.getImageOptions();
        imgOption1.setImageResource(options[0]);
        imgOption2.setImageResource(options[1]);
        imgOption3.setImageResource(options[2]);
        imgOption4.setImageResource(options[3]);

        // Cập nhật điểm lên màn hình chính
        tvScore.setText("⭐ Điểm: " + userScore);

        // Đọc to câu hỏi lên ngay khi vừa hiện ra
        playSound(currentQuestion.getAudioResId());
    }

    private void checkAnswer(int selectedOptionIndex) {
        Question currentQuestion = questionList.get(currentQuestionIndex);

        if (selectedOptionIndex == currentQuestion.getCorrectIndex()) {
            // CỘNG ĐIỂM KHI ĐÚNG
            userScore += 10;

            // SHOW BẢNG POPUP CHÚC MỪNG
            showCongratsPopup();

        } else {
            Toast.makeText(getContext(), "Chưa đúng rồi, bé thử lại nhé! ❌", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCongratsPopup() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_congratulations);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(false);

        Button btnNextQuestion = dialog.findViewById(R.id.btn_next_question);
        TextView tvDialogScore = dialog.findViewById(R.id.tv_dialog_score);

        tvDialogScore.setText("Bé nhận được ⭐ +10 điểm! (Tổng: " + userScore + ")");

        btnNextQuestion.setOnClickListener(v -> {
            dialog.dismiss();

            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                loadQuestion(currentQuestionIndex);
            } else {
                Toast.makeText(getContext(), "🎉 Xuất sắc! Bé đã phá đảo tất cả câu hỏi! Tổng điểm: " + userScore, Toast.LENGTH_LONG).show();
                // Khi hết câu hỏi, đưa bé quay lại màn hình Menu
                layoutGamePlay.setVisibility(View.GONE);
                layoutStartMenu.setVisibility(View.VISIBLE);
            }
        });

        dialog.show();
    }

    private void playSound(int soundResource) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (soundResource != 0) {
            mediaPlayer = MediaPlayer.create(getContext(), soundResource);
            mediaPlayer.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}