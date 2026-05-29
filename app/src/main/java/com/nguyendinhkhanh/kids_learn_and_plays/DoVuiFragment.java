package com.nguyendinhkhanh.kids_learn_and_plays;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.Collections;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

public class DoVuiFragment extends Fragment {

    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private MediaPlayer mediaPlayer;

    // Khuôn mẫu dữ liệu
    class QuizItem {
        String name;
        int imageResId;
        int audioResId;

        public QuizItem(String name, int imageResId, int audioResId) {
            this.name = name;
            this.imageResId = imageResId;
            this.audioResId = audioResId;
        }
    }

    // Các biến trạng thái Game
    private int currentQuestionIndex = 0;
    private int userScore = 0;
    private int wrongAttemptCount = 0;
    private List<Question> questionList;

    // Khai báo View
    private LinearLayout layoutStartMenu, layoutGamePlay;
    private Button btnStartGame;
    private TextView tvQuestion, tvScore;
    private ImageView btnPlayQuestion;
    private ImageView imgOption1, imgOption2, imgOption3, imgOption4;
    private MaterialCardView cardOption1, cardOption2, cardOption3, cardOption4;

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

        // 3. Khởi tạo dữ liệu và Âm thanh
        setupQuestions();

        correctSound = MediaPlayer.create(getContext(), R.raw.sound_correct);
        wrongSound = MediaPlayer.create(getContext(), R.raw.sound_wrong);

        // 4. XỬ LÝ NÚT BẮT ĐẦU CHƠI
        btnStartGame.setOnClickListener(v -> {
            SoundManager.playClick(getContext());

            layoutStartMenu.setVisibility(View.GONE);
            layoutGamePlay.setVisibility(View.VISIBLE);

            // Nạp điểm cũ
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("GameData", Context.MODE_PRIVATE);
            userScore = sharedPreferences.getInt("total_stars", 0);

            // Tự động tắt nhạc nền App, bật nhạc Đố vui
            BackgroundMusicManager.startGameMusic(getContext());

            // Bắt đầu game
            wrongAttemptCount = 0;
            currentQuestionIndex = 0;
            loadQuestion(currentQuestionIndex);
        });

        // 5. Nút loa đọc lại câu hỏi
        btnPlayQuestion.setOnClickListener(v -> {
            SoundManager.playClick(getContext());
            playSound(questionList.get(currentQuestionIndex).getAudioResId());
        });

        // 6. Xử lý click đáp án (Gắn tiếng Click vào từng ô)
        cardOption1.setOnClickListener(v -> { SoundManager.playClick(getContext()); checkAnswer(0); });
        cardOption2.setOnClickListener(v -> { SoundManager.playClick(getContext()); checkAnswer(1); });
        cardOption3.setOnClickListener(v -> { SoundManager.playClick(getContext()); checkAnswer(2); });
        cardOption4.setOnClickListener(v -> { SoundManager.playClick(getContext()); checkAnswer(3); });

        return view;
    }

    private void setupQuestions() {
        questionList = new ArrayList<>();
        List<QuizItem> allItems = new ArrayList<>();

        // -- ĐỔ ĐỘNG VẬT --
        allItems.add(new QuizItem("CON KHỈ", R.drawable.img_khi, R.raw.sound_khi));
        allItems.add(new QuizItem("SƯ TỬ", R.drawable.img_su_tu, R.raw.sound_su_tu));
        allItems.add(new QuizItem("CON VOI", R.drawable.img_voi, R.raw.sound_voi));
        allItems.add(new QuizItem("CON CHÓ", R.drawable.img_cho, R.raw.sound_cho));
        allItems.add(new QuizItem("CON MÈO", R.drawable.img_meo, R.raw.sound_meo));
        allItems.add(new QuizItem("CON GÀ", R.drawable.img_ga, R.raw.sound_ga));
        allItems.add(new QuizItem("CON HEO", R.drawable.img_heo, R.raw.sound_heo));
        allItems.add(new QuizItem("CON BÒ", R.drawable.img_bo, R.raw.sound_bo));
        allItems.add(new QuizItem("CON VỊT", R.drawable.img_vit, R.raw.sound_vit));
        allItems.add(new QuizItem("CON HỔ", R.drawable.img_ho, R.raw.sound_ho));

        // -- ĐỔ PHƯƠNG TIỆN --
        allItems.add(new QuizItem("XE CẢNH SÁT", R.drawable.img_xe_canh_sat, R.raw.sound_xe_canh_sat));
        allItems.add(new QuizItem("XE CỨU HỎA", R.drawable.img_xe_cuu_hoa, R.raw.sound_xe_cuu_hoa));
        allItems.add(new QuizItem("XE CỨU THƯƠNG", R.drawable.img_xe_cuu_thuong, R.raw.sound_xe_cuu_thuong));
        allItems.add(new QuizItem("Ô TÔ", R.drawable.img_o_to, R.raw.sound_o_to));
        allItems.add(new QuizItem("TÀU HỎA", R.drawable.img_tau_hoa, R.raw.sound_tau_hoa));
        allItems.add(new QuizItem("MÁY BAY", R.drawable.img_may_bay, R.raw.sound_may_bay));
        allItems.add(new QuizItem("XE MÁY", R.drawable.img_xe_may, R.raw.sound_xe_may));
        allItems.add(new QuizItem("XE ĐẠP", R.drawable.img_xe_dap, R.raw.sound_xe_dap));

        int totalQuestionsToGenerate = 10;
        Random random = new Random();

        if (allItems.size() < 4) return;

        for (int i = 0; i < totalQuestionsToGenerate; i++) {
            Collections.shuffle(allItems);

            QuizItem optionA = allItems.get(0);
            QuizItem optionB = allItems.get(1);
            QuizItem optionC = allItems.get(2);
            QuizItem optionD = allItems.get(3);

            int[] options = {optionA.imageResId, optionB.imageResId, optionC.imageResId, optionD.imageResId};
            int correctIndex = random.nextInt(4);
            QuizItem correctItem = allItems.get(correctIndex);

            String dynamicQuestionText = "Bé hãy tìm " + correctItem.name + " nhé!";
            questionList.add(new Question(dynamicQuestionText, correctItem.audioResId, options, correctIndex));
        }
    }

    private void loadQuestion(int index) {
        Question currentQuestion = questionList.get(index);
        tvQuestion.setText(currentQuestion.getText());

        int[] options = currentQuestion.getImageOptions();
        imgOption1.setImageResource(options[0]);
        imgOption2.setImageResource(options[1]);
        imgOption3.setImageResource(options[2]);
        imgOption4.setImageResource(options[3]);

        tvScore.setText("⭐ Điểm: " + userScore);
        playSound(currentQuestion.getAudioResId());
    }

    private void checkAnswer(int selectedOptionIndex) {
        Question currentQuestion = questionList.get(currentQuestionIndex);

        if (selectedOptionIndex == currentQuestion.getCorrectIndex()) {
            // NẾU ĐÚNG
            if (correctSound != null) {
                correctSound.seekTo(0);
                correctSound.start();
            }

            userScore += 10;
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("GameData", Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt("total_stars", userScore).apply();

            showCongratsPopup();
        } else {
            // NẾU SAI
            if (wrongSound != null) {
                wrongSound.seekTo(0);
                wrongSound.start();
            }

            wrongAttemptCount++; // Trừ 1 mạng

            if (wrongAttemptCount >= 2) {
                // HẾT MẠNG -> GAME OVER -> Hiện bảng thông báo thua
                userScore -= 20;
                if (userScore < 0) userScore = 0;

                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("GameData", Context.MODE_PRIVATE);
                sharedPreferences.edit().putInt("total_stars", userScore).apply();

                showGameOverPopup(); // Gọi hàm Game Over
            } else {
                // CHƯA HẾT MẠNG -> Hiện Popup cảnh báo sai
                int mangConLai = 2 - wrongAttemptCount;
                Toast.makeText(getContext(), "Bé còn " + mangConLai + " lần thử thôi nhé!", Toast.LENGTH_SHORT).show();
                showWrongAnswerPopup();
            }
        }
    }

    // --- HÀM 1: BẢNG CHÚC MỪNG CÂU HỎI BÌNH THƯỜNG ---
    private void showCongratsPopup() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_congratulations);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

        Button btnNextQuestion = dialog.findViewById(R.id.btn_next_question);
        TextView tvDialogScore = dialog.findViewById(R.id.tv_dialog_score);

        boolean isFinalQuestion = (currentQuestionIndex >= questionList.size() - 1);

        tvDialogScore.setText("Bé nhận được ⭐ +10 điểm!\n(Tổng: " + userScore + ")");

        if (isFinalQuestion) {
            btnNextQuestion.setText("KẾT QUẢ ▶"); // Câu cuối thì đổi thành KẾT QUẢ
        } else {
            btnNextQuestion.setText("CHƠI TIẾP ▶");
        }

        btnNextQuestion.setOnClickListener(v -> {
            SoundManager.playClick(getContext());
            dialog.dismiss();

            if (!isFinalQuestion) {
                // Chưa phá đảo -> Sang câu tiếp
                currentQuestionIndex++;
                wrongAttemptCount = 0;
                loadQuestion(currentQuestionIndex);
            } else {
                // ĐÃ PHÁ ĐẢO -> Hiện bảng vinh danh chiến thắng
                showPhaDaoPopup();
            }
        });
        dialog.show();
    }

    // --- HÀM 2: BẢNG VINH DANH PHÁ ĐẢO (CHIẾN THẮNG) ---
    private void showPhaDaoPopup() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_pha_dao);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

        TextView tvScore = dialog.findViewById(R.id.tv_pha_dao_score);
        Button btnOk = dialog.findViewById(R.id.btn_pha_dao_ok);

        // Cộng 15 sao Bonus
        userScore += 15;
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("GameData", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("total_stars", userScore).apply();

        tvScore.setText("Bé đã hoàn thành xuất sắc!\nNhận được Bonus +15 ⭐\nTổng điểm: " + userScore);

        btnOk.setOnClickListener(v -> {
            SoundManager.playClick(getContext());
            dialog.dismiss();

            // Xử lý về màn hình chính
            BackgroundMusicManager.stopGameMusic();
            layoutGamePlay.setVisibility(View.GONE);
            layoutStartMenu.setVisibility(View.VISIBLE);
        });
        dialog.show();
    }

    // --- HÀM 3: BẢNG GAME OVER (THUA) ---
    private void showGameOverPopup() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_game_over);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);

        Button btnOk = dialog.findViewById(R.id.btn_game_over_ok);

        btnOk.setOnClickListener(v -> {
            SoundManager.playClick(getContext());
            dialog.dismiss();

            // Xử lý về màn hình Menu
            BackgroundMusicManager.stopGameMusic();
            layoutGamePlay.setVisibility(View.GONE);
            layoutStartMenu.setVisibility(View.VISIBLE);
        });
        dialog.show();
    }

    // --- HÀM 4: BẢNG CẢNH BÁO SAI ---
    private void showWrongAnswerPopup() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_wrong_answer);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);
        dialog.show();

        // Tự tắt sau 1.5 giây
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 500);
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

        if (correctSound != null) correctSound.release();
        if (wrongSound != null) wrongSound.release();
        if (mediaPlayer != null) mediaPlayer.release();

        // KHI RỜI KHỎI TAB ĐỐ VUI -> TẮT NHẠC GAME, MỞ LẠI NHẠC APP
        BackgroundMusicManager.stopGameMusic();
    }
}