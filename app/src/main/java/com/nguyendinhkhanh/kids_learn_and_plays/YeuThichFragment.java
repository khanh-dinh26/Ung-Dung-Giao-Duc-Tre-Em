package com.nguyendinhkhanh.kids_learn_and_plays;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class YeuThichFragment extends Fragment {

    private RecyclerView rvTruyenYeuThich;
    private TextView tvNoFavorite;
    private TruyenAdapter adapter;
    private List<Truyen> listTruyen;
    private DatabaseReference databaseReference;

    public YeuThichFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yeu_thich, container, false);

        rvTruyenYeuThich = view.findViewById(R.id.rv_danh_sach_yeu_thich);
        tvNoFavorite = view.findViewById(R.id.tv_no_favorite);

        rvTruyenYeuThich.setLayoutManager(new LinearLayoutManager(getContext()));
        listTruyen = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("truyen_audio");

        return view;
    }

    // Dùng onResume để mỗi lần bé mở sang Tab Yêu thích là danh sách tự động Update ngay lập tức
    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteStories();
    }

    private void loadFavoriteStories() {
        // Chỉ tải dữ liệu 1 lần (SingleValueEvent) để tối ưu hiệu năng
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTruyen.clear();
                if (getContext() == null) return;

                // Đọc file lưu trữ Yêu thích Offline
                SharedPreferences favPref = getContext().getSharedPreferences("FavoritesData", Context.MODE_PRIVATE);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    // Nếu truyện tồn tại VÀ ID của truyện được đánh dấu là True (Yêu thích)
                    if (truyen != null && favPref.getBoolean(truyen.getId(), false)) {
                        listTruyen.add(truyen);
                    }
                }

                // Cập nhật giao diện (Ẩn/Hiện câu thông báo chưa có truyện)
                if (listTruyen.isEmpty()) {
                    tvNoFavorite.setVisibility(View.VISIBLE);
                    rvTruyenYeuThich.setVisibility(View.GONE);
                } else {
                    tvNoFavorite.setVisibility(View.GONE);
                    rvTruyenYeuThich.setVisibility(View.VISIBLE);

                    // Tái sử dụng lại TruyenAdapter cực kỳ tiện lợi
                    adapter = new TruyenAdapter(getContext(), listTruyen, null);
                    rvTruyenYeuThich.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}