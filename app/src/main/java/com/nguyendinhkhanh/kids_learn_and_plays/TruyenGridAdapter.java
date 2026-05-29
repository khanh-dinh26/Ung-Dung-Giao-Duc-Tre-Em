package com.nguyendinhkhanh.kids_learn_and_plays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TruyenGridAdapter extends RecyclerView.Adapter<TruyenGridAdapter.GridViewHolder> {

    private Context context;
    private List<Truyen> listTruyenYeuThich;

    public TruyenGridAdapter(Context context, List<Truyen> listTruyenYeuThich) {
        this.context = context;
        this.listTruyenYeuThich = listTruyenYeuThich;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_truyen_grid, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        Truyen truyen = listTruyenYeuThich.get(position);

        holder.tvTitle.setText(truyen.getTenTruyen());
        holder.tvTitle.setSelected(true);

        Glide.with(context)
                .load(truyen.getLinkAnh())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imgThumb);

        // 1. Nhấp bình thường -> Mở nghe truyện luôn
        holder.itemView.setOnClickListener(v -> {
            SoundManager.playClick(context);
            Intent intent = new Intent(context, TruyenPlayerActivity.class);
            intent.putExtra("ID_TRUYEN", truyen.getId());
            intent.putExtra("TEN_TRUYEN", truyen.getTenTruyen());
            intent.putExtra("LINK_ANH", truyen.getLinkAnh());
            intent.putExtra("LINK_AUDIO", truyen.getLinkAudio());
            context.startActivity(intent);
        });

        // 2. Ấn giữ lâu -> Bỏ Yêu thích
        holder.itemView.setOnLongClickListener(v -> {
            SharedPreferences favPref = context.getSharedPreferences("FavoritesData", Context.MODE_PRIVATE);
            favPref.edit().putBoolean(truyen.getId(), false).apply();

            listTruyenYeuThich.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, listTruyenYeuThich.size());

            Toast.makeText(context, "Đã xóa khỏi danh sách Yêu thích", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listTruyenYeuThich != null ? listTruyenYeuThich.size() : 0;
    }

    public static class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumb;
        TextView tvTitle;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumb = itemView.findViewById(R.id.img_thumb_grid);
            tvTitle = itemView.findViewById(R.id.tv_title_grid);
        }
    }
}