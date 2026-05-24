package com.nguyendinhkhanh.kids_learn_and_plays;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class KhamPhaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp giao diện XML vào Fragment
        View view = inflater.inflate(R.layout.fragment_kham_pha, container, false);

        // Ánh xạ 3 thẻ CardView
        CardView cardDongVat = view.findViewById(R.id.card_dong_vat);
        CardView cardGiaoThong = view.findViewById(R.id.card_giao_thong);
        CardView cardChuCai = view.findViewById(R.id.card_chu_cai);

        cardDongVat.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DongVatActivity.class);
            startActivity(intent);
        });

        cardGiaoThong.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), GiaoThongActivity.class));
        });

        cardChuCai.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChuCaiActivity.class));
        });

        return view;
    }
}