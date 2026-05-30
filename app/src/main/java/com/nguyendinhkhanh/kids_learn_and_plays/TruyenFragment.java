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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TruyenFragment extends Fragment {

    private RecyclerView rvTruyen;
    private TruyenAdapter adapter;
    private List<Truyen> listTruyen;
    private DatabaseReference databaseReference;
    private TextView tvCurrentStars;

    public TruyenFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truyen, container, false);

        rvTruyen = view.findViewById(R.id.rv_danh_sach_truyen);
        tvCurrentStars = view.findViewById(R.id.tv_current_stars);

        rvTruyen.setLayoutManager(new LinearLayoutManager(getContext()));
        listTruyen = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("truyen_audio");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStarsUI();

        // KIỂM TRA MẠNG TRƯỚC KHI TẢI
        if (isNetworkAvailable()) {
            loadTruyenFromFirebase();
        } else {
            Toast.makeText(getContext(), "Vui lòng kết nối mạng để xem truyện nhé! 📶", Toast.LENGTH_LONG).show();
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

    private void updateStarsUI() {
        if (getContext() != null) {
            SharedPreferences pref = getContext().getSharedPreferences("GameData", Context.MODE_PRIVATE);
            int stars = pref.getInt("total_stars", 0);
            tvCurrentStars.setText("⭐ " + stars);
        }
    }

    private void loadTruyenFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTruyen.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Truyen truyen = dataSnapshot.getValue(Truyen.class);
                    if (truyen != null) {
                        listTruyen.add(truyen);
                    }
                }
                if (getContext() != null) {
                    adapter = new TruyenAdapter(getContext(), listTruyen, tvCurrentStars);
                    rvTruyen.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Không thể tải dữ liệu truyện!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}