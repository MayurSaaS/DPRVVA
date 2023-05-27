package com.vvautotest.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvautotest.R;
import com.vvautotest.activities.AddDMCEntryActivity;
import com.vvautotest.activities.AddOpeningStockActivity;

public class OpeningStockFragment extends Fragment {

    CardView addNewEntryBtn, dmcView;

    public OpeningStockFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_opening_stock, container, false);
        addNewEntryBtn = rootView.findViewById(R.id.addNewEntryBtn);
        addNewEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddOpeningStockActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }



}