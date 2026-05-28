package com.nguyendinhkhanh.kids_learn_and_plays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    private int currentStars;

    public TruyenAdapter(Context context, List<Truyen> listTruyen) {
        this.context = context;
        this.listTruyen = listTruyen;

        // Đọc tổng điểm sao hiện tại của bé trong máy ra để đối chiếu
        SharedPreferences pref = context.getSharedPreferences("GameData", Context.MODE_PRIVATE);
        this.currentStars = pref.getInt("total_stars", 0);
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

        // Sử dụng Glide để tải ảnh mượt mà từ Link Internet vào ImageView
        Glide.with(context)
                .load(truyen.getLinkAnh())
                .placeholder(R.drawable.ic_launcher_foreground) // Ảnh hiện trong lúc chờ tải
                .into(holder.imgThumb);

        // Logic kiểm tra xem bé có đủ điểm sao để mở khóa truyện hay không
        boolean isUnlocked = currentStars >= truyen.getRequiredStars();

        if (isUnlocked) {
            holder.tvLockStatus.setText("Đã mở khóa");
            holder.tvLockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.layoutItemContent.setAlpha(0.4f); // Làm mờ toàn bộ khối (ảnh, chữ, icon)
            holder.imgActionIcon.setImageResource(android.R.drawable.ic_secure); // Icon Ổ Khóa

            holder.tvLockStatus.setText("Cần đạt " + truyen.getRequiredStars() + " ⭐");
            holder.tvLockStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        }

        // Sự kiện khi bấm vào dòng truyện
        holder.itemView.setOnClickListener(v -> {
            SoundManager.playClick(context); // Phát tiếng click đồng bộ

            if (isUnlocked) {
                // Đủ điểm -> Chuyển sang Màn hình trình phát nhạc trực tuyến
                Intent intent = new Intent(context, TruyenPlayerActivity.class);
                intent.putExtra("TEN_TRUYEN", truyen.getTenTruyen());
                intent.putExtra("LINK_ANH", truyen.getLinkAnh());
                intent.putExtra("LINK_AUDIO", truyen.getLinkAudio());
                context.startActivity(intent);
            } else {
                // Không đủ điểm -> Cảnh báo nhắc bé quay lại Tab 2 chơi Đố Vui tích điểm
                Toast.makeText(context, "Bé cần tích lũy đủ " + truyen.getRequiredStars() + " ⭐ ở mục Đố Vui để mở khóa truyện này nhé!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTruyen != null ? listTruyen.size() : 0;
    }

    public static class TruyenViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        ImageView imgActionIcon;
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