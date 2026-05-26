package com.nguyendinhkhanh.kids_learn_and_plays; // Nhớ kiểm tra lại tên package

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class DoVuiFragment extends Fragment {


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
    private int currentQuestionIndex = 0;
    private int userScore = 0;
    private int wrongAttemptCount = 0;
    private LinearLayout layoutStartMenu, layoutGamePlay;
    private Button btnStartGame;

    // Khai báo view trong game
    private TextView tvQuestion, tvScore;
    private ImageView btnPlayQuestion;
    private ImageView imgOption1, imgOption2, imgOption3, imgOption4;
    private MaterialCardView cardOption1, cardOption2, cardOption3, cardOption4;

    private MediaPlayer mediaPlayer;
    private List<Question> questionList;

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
            layoutStartMenu.setVisibility(View.GONE);
            layoutGamePlay.setVisibility(View.VISIBLE);

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("GameData", Context.MODE_PRIVATE);
            userScore = sharedPreferences.getInt("total_stars", 0);

            wrongAttemptCount = 0; // RESET LẠI SỐ LẦN SAI VỀ 0 KHI BẮT ĐẦU GAME MỚI
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

        // 2. TẠO "CÁI TÚI" VÀ ĐỔ TẤT CẢ DỮ LIỆU VÀO TRONG
        List<QuizItem> allItems = new ArrayList<>();

        // ==========================================
        // -- ĐỔ 10 ĐỘNG VẬT VÀO TÚI --
        // ==========================================
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

        // ==========================================
        // -- ĐỔ 8 PHƯƠNG TIỆN VÀO TÚI --
        // ==========================================
        allItems.add(new QuizItem("XE CẢNH SÁT", R.drawable.img_xe_canh_sat, R.raw.sound_xe_canh_sat));
        allItems.add(new QuizItem("XE CỨU HỎA", R.drawable.img_xe_cuu_hoa, R.raw.sound_xe_cuu_hoa));
        allItems.add(new QuizItem("XE CỨU THƯƠNG", R.drawable.img_xe_cuu_thuong, R.raw.sound_xe_cuu_thuong));
        allItems.add(new QuizItem("Ô TÔ", R.drawable.img_o_to, R.raw.sound_o_to));
        allItems.add(new QuizItem("TÀU HỎA", R.drawable.img_tau_hoa, R.raw.sound_tau_hoa));
        allItems.add(new QuizItem("MÁY BAY", R.drawable.img_may_bay, R.raw.sound_may_bay));
        allItems.add(new QuizItem("XE MÁY", R.drawable.img_xe_may, R.raw.sound_xe_may));
        allItems.add(new QuizItem("XE ĐẠP", R.drawable.img_xe_dap, R.raw.sound_xe_dap));



        // 3. BẮT ĐẦU TỰ ĐỘNG SINH CÂU HỎI
        int totalQuestionsToGenerate = 10;
        Random random = new Random();

        // Kiểm tra an toàn: Phải có ít nhất 4 item trong túi thì mới tạo được câu hỏi 4 đáp án
        if (allItems.size() < 4) return;

        for (int i = 0; i < totalQuestionsToGenerate; i++) {
            // Bước A: Lắc đều túi dữ liệu lên (Xáo trộn ngẫu nhiên)
            Collections.shuffle(allItems);

            // Bước B: Bốc 4 cái đầu tiên ra làm 4 đáp án
            QuizItem optionA = allItems.get(0);
            QuizItem optionB = allItems.get(1);
            QuizItem optionC = allItems.get(2);
            QuizItem optionD = allItems.get(3);

            int[] options = {optionA.imageResId, optionB.imageResId, optionC.imageResId, optionD.imageResId};

            // Bước C: Chọn ngẫu nhiên 1 vị trí (0, 1, 2, hoặc 3) làm đáp án ĐÚNG
            int correctIndex = random.nextInt(4);
            QuizItem correctItem = allItems.get(correctIndex);

            // Bước D: Tự động ghép thành câu hỏi (Ví dụ: "Bé hãy tìm CON KHỈ nhé!")
            String dynamicQuestionText = "Bé hãy tìm " + correctItem.name + " nhé!";

            // Bước E: Nạp vào danh sách Game
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

        // Cập nhật điểm lên màn hình chính
        tvScore.setText("⭐ Điểm: " + userScore);

        // Đọc to câu hỏi lên ngay khi vừa hiện ra
        playSound(currentQuestion.getAudioResId());
    }

    private void checkAnswer(int selectedOptionIndex) {
        Question currentQuestion = questionList.get(currentQuestionIndex);

        if (selectedOptionIndex == currentQuestion.getCorrectIndex()) {
            // ĐÚNG: Cộng điểm, lưu vào máy và hiện Popup Chúc mừng
            userScore += 10;

            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("GameData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("total_stars", userScore);
            editor.apply();

            showCongratsPopup();
        } else {
            // SAI: Tăng số lần sai lên 1
            wrongAttemptCount++;

            if (wrongAttemptCount >= 3) {
                // GAME OVER: ĐÃ SAI 3 LẦN
                userScore -= 20;

                // Tránh để điểm bị âm (dưới 0) làm bé khó hiểu
                if (userScore < 0) {
                    userScore = 0;
                }

                // Lưu lại điểm mới (sau khi bị trừ) vào bộ nhớ máy
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("GameData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("total_stars", userScore);
                editor.apply();

                // Báo cho bé biết tại sao trò chơi dừng lại
                Toast.makeText(getContext(), "Trời ơi! Bé chọn sai 3 lần rồi. Trừ 20 điểm nhé! 😢", Toast.LENGTH_LONG).show();

                // Đẩy bé ra lại màn hình Menu
                layoutGamePlay.setVisibility(View.GONE);
                layoutStartMenu.setVisibility(View.VISIBLE);

            } else {
                // NẾU CHƯA QUÁ 3 LẦN SAI: Chỉ báo Popup sai và nhắc bé số mạng còn lại
                int mangConLai = 3 - wrongAttemptCount;
                Toast.makeText(getContext(), "Cố lên! Bé chỉ còn " + mangConLai + " lần thử thôi nhé!", Toast.LENGTH_SHORT).show();
                showWrongAnswerPopup();
            }
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
    private void showWrongAnswerPopup() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_wrong_answer);

        // Làm mờ nền xung quanh
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(false); // Không cho bé bấm bậy bạ ra ngoài
        dialog.show();


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss(); // Lệnh đóng Popup
                }
            }
        }, 1500);
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