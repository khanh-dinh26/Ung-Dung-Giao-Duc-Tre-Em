package com.nguyendinhkhanh.kids_learn_and_plays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TruyenAdapter extends RecyclerView.Adapter<TruyenAdapter.TruyenViewHolder> {

    private Context context;
    private List<Truyen> listTruyen;
    private static Toast mToast;
    private TextView tvCurrentStars;
    private SharedPreferences pref;

    public TruyenAdapter(Context context, List<Truyen> listTruyen, TextView tvCurrentStars) {
        this.context = context;
        this.listTruyen = listTruyen;
        this.tvCurrentStars = tvCurrentStars;
        this.pref = context.getSharedPreferences("GameData", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public TruyenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_truyen, parent, false);
        return new TruyenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TruyenViewHolder holder, int position) {
        Truyen truyen = listTruyen.get(position);
        holder.tvTitle.setText(truyen.getTenTruyen());
        holder.tvTitle.setSelected(true); // Hiệu ứng chữ chạy ngang Marquee

        int currentStars = pref.getInt("total_stars", 0);

        Glide.with(context)
                .load(truyen.getLinkAnh())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imgThumb);

        boolean isFree = truyen.getRequiredStars() == 0;
        boolean isPurchased = pref.getBoolean("unlocked_" + truyen.getId(), false);
        boolean isUnlocked = isFree || isPurchased;

        if (isUnlocked) {
            holder.layoutItemContent.setAlpha(1.0f);
            holder.imgActionIcon.setImageResource(android.R.drawable.ic_media_play);
            holder.tvLockStatus.setText("Đã mở khóa");
            holder.tvLockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.layoutItemContent.setAlpha(0.4f);
            holder.imgActionIcon.setImageResource(android.R.drawable.ic_secure);
            holder.tvLockStatus.setText("Giá: " + truyen.getRequiredStars() + " ⭐");
            holder.tvLockStatus.setTextColor(Color.parseColor("#9E9E9E"));
        }

        // SỰ KIỆN 1: BẤM BÌNH THƯỜNG (Click) ĐỂ MỞ HOẶC MUA
        holder.itemView.setOnClickListener(v -> {
            SoundManager.playClick(context);

            if (isUnlocked) {
                Intent intent = new Intent(context, TruyenPlayerActivity.class);
                intent.putExtra("ID_TRUYEN", truyen.getId());
                intent.putExtra("TEN_TRUYEN", truyen.getTenTruyen());
                intent.putExtra("LINK_ANH", truyen.getLinkAnh());
                intent.putExtra("LINK_AUDIO", truyen.getLinkAudio());
                context.startActivity(intent);
            } else {
                int latestStars = pref.getInt("total_stars", 0);
                if (latestStars >= truyen.getRequiredStars()) {
                    showPurchaseDialog(truyen, holder.getAdapterPosition());
                } else {
                    showToast("Bé cần thêm " + (truyen.getRequiredStars() - latestStars) + " ⭐ nữa để mua truyện này!");}
            }
        });

        // SỰ KIỆN 2: ẤN GIỮ (Long Click) ĐỂ HOÀN TRẢ TRUYỆN ĐÃ MUA
        holder.itemView.setOnLongClickListener(v -> {
            if (isPurchased && !isFree) {
                // Nếu là truyện phải mua bằng sao và đã mua -> Cho phép hoàn trả
                showRefundDialog(truyen, holder.getAdapterPosition());
            } else if (isFree) {
                // Nếu là truyện miễn phí (Giá 0 sao)
                Toast.makeText(context, "Truyện này miễn phí, bé không thể hoàn trả nhé!", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu chưa mua
                Toast.makeText(context, "Bé chưa mua truyện này mà!", Toast.LENGTH_SHORT).show();
            }
            return true; // Trả về true để Android biết mình đã xử lý xong, không gọi nhầm sang sự kiện Click bình thường
        });
    }
    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel(); // Tắt ngay thông báo cũ
        }
        mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
    // HÀM 1: HỘP THOẠI MUA TRUYỆN
    private void showPurchaseDialog(Truyen truyen, int position) {
        int currentStars = pref.getInt("total_stars", 0);
        android.app.Dialog dialog = new android.app.Dialog(context);
        dialog.setContentView(R.layout.dialog_mua_truyen);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_dialog_message);
        android.widget.Button btnCancel = dialog.findViewById(R.id.btn_dialog_cancel);
        android.widget.Button btnBuy = dialog.findViewById(R.id.btn_dialog_buy);

        tvTitle.setText("MỞ KHÓA TRUYỆN");
        tvMessage.setText("Bé có muốn dùng " + truyen.getRequiredStars() + " ⭐ để mở khóa truyện\n'" + truyen.getTenTruyen() + "' không?");

        btnCancel.setOnClickListener(v -> {
            SoundManager.playClick(context);
            dialog.dismiss();
        });

        btnBuy.setOnClickListener(v -> {
            SoundManager.playClick(context);
            int newStars = currentStars - truyen.getRequiredStars();
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("total_stars", newStars);
            editor.putBoolean("unlocked_" + truyen.getId(), true);
            editor.apply();

            notifyItemChanged(position);
            if (tvCurrentStars != null) {
                tvCurrentStars.setText("⭐ " + newStars);
            }

            Toast.makeText(context, "Mở khóa thành công!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.show();
    }

    // HÀM 2: HỘP THOẠI HOÀN TRẢ TRUYỆN (MỚI THÊM)
    private void showRefundDialog(Truyen truyen, int position) {
        int refundStars = truyen.getRequiredStars() / 2; // Tính 50% số sao được hoàn lại
        int currentStars = pref.getInt("total_stars", 0);

        android.app.Dialog dialog = new android.app.Dialog(context);
        dialog.setContentView(R.layout.dialog_mua_truyen); // Tái sử dụng lại giao diện Mua truyện

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_dialog_message);
        android.widget.Button btnCancel = dialog.findViewById(R.id.btn_dialog_cancel);
        com.google.android.material.button.MaterialButton btnRefund = dialog.findViewById(R.id.btn_dialog_buy);

        // Đổi nội dung thành Hoàn trả
        tvTitle.setText("HOÀN TRẢ TRUYỆN");
        tvTitle.setTextColor(Color.parseColor("#E53935")); // Chữ màu đỏ
        tvMessage.setText("Bé có muốn trả lại truyện '" + truyen.getTenTruyen() + "' để nhận lại " + refundStars + " ⭐ (50% giá trị) không?");

        btnRefund.setText("Hoàn trả");
        btnRefund.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E53935")));

        btnCancel.setOnClickListener(v -> {
            SoundManager.playClick(context);
            dialog.dismiss();
        });

        btnRefund.setOnClickListener(v -> {
            SoundManager.playClick(context);

            // 1. Cộng lại 50% số sao
            int newStars = currentStars + refundStars;
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("total_stars", newStars);

            // 2. Thu hồi quyền đã mua (Khóa lại truyện)
            editor.putBoolean("unlocked_" + truyen.getId(), false);
            editor.apply();

            // 3. Xóa truyện khỏi danh sách Yêu thích (nếu đang có)
            SharedPreferences favPref = context.getSharedPreferences("FavoritesData", Context.MODE_PRIVATE);
            favPref.edit().putBoolean(truyen.getId(), false).apply();

            // 4. Cập nhật lại giao diện ngay lập tức
            notifyItemChanged(position);
            if (tvCurrentStars != null) {
                tvCurrentStars.setText("⭐ " + newStars);
            }

            showToast("Đã hoàn trả và nhận lại " + refundStars + " ⭐");
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return listTruyen != null ? listTruyen.size() : 0;
    }

    public static class TruyenViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb, imgActionIcon;
        TextView tvTitle, tvLockStatus;
        LinearLayout layoutItemContent;

        public TruyenViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.img_thumb_truyen);
            tvTitle = itemView.findViewById(R.id.tv_title_truyen);
            tvLockStatus = itemView.findViewById(R.id.tv_lock_status);
            imgActionIcon = itemView.findViewById(R.id.img_action_icon);
            layoutItemContent = itemView.findViewById(R.id.layout_item_content);
        }
    }
}