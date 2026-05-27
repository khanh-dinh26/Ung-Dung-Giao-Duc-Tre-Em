package com.nguyendinhkhanh.kids_learn_and_plays;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public TruyenFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_truyen, container, false);

        rvTruyen = view.findViewById(R.id.rv_danh_sach_truyen);
        rvTruyen.setLayoutManager(new LinearLayoutManager(getContext()));

        listTruyen = new ArrayList<>();

        // KẾT NỐI ĐẾN NHÁNH TRUYỆN TRÊN FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference("truyen_audio");

        loadTruyenFromFirebase();

        return view;
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

                // Gắn dữ liệu vào Adapter
                if (getContext() != null) {
                    adapter = new TruyenAdapter(getContext(), listTruyen);
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