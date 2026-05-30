package com.nguyendinhkhanh.kids_learn_and_plays;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private TruyenGridAdapter gridAdapter;
    private List<Truyen> listTruyen;
    private DatabaseReference databaseReference;

    public YeuThichFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yeu_thich, container, false);

        rvTruyenYeuThich = view.findViewById(R.id.rv_danh_sach_yeu_thich);
        tvNoFavorite = view.findViewById(R.id.tv_no_favorite);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rvTruyenYeuThich.setLayoutManager(gridLayoutManager);

        listTruyen = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("truyen_audio");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // KIỂM TRA MẠNG
        if (isNetworkAvailable()) {
            loadFavoriteStories();
        } else {
            // NẾU MẤT MẠNG -> Đổi chữ thông báo ra giữa màn hình
            tvNoFavorite.setText("Vui lòng kết nối mạng để dùng chức năng này! 📶");
            tvNoFavorite.setVisibility(View.VISIBLE);
            rvTruyenYeuThich.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Không có kết nối Internet!", Toast.LENGTH_SHORT).show();
        }
    }

    // HÀM KIỂM TRA KẾT NỐI MẠNG
    private boolean isNetworkAvailable() {
        if (getContext() == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    private void loadFavoriteStories() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTruyen.clear();
                if (getContext() == null) return;

                SharedPreferences favPref = getContext().getSharedPreferences("FavoritesData", Context.MODE_PRIVATE);

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    if (truyen != null && favPref.getBoolean(truyen.getId(), false)) {
                        listTruyen.add(truyen);
                    }
                }

                if (listTruyen.isEmpty()) {
                    // Mạng bình thường nhưng chưa có truyện -> Trả lại chữ gốc
                    tvNoFavorite.setText("Bé chưa có truyện yêu thích nào.\nBé hãy vào mục Kể Truyện\nvà bấm vào ngôi sao nhé! ⭐");
                    tvNoFavorite.setVisibility(View.VISIBLE);
                    rvTruyenYeuThich.setVisibility(View.GONE);
                } else {
                    tvNoFavorite.setVisibility(View.GONE);
                    rvTruyenYeuThich.setVisibility(View.VISIBLE);

                    gridAdapter = new TruyenGridAdapter(getContext(), listTruyen);
                    rvTruyenYeuThich.setAdapter(gridAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}