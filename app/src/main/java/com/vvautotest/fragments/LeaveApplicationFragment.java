package com.vvautotest.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvautotest.R;
import com.vvautotest.activities.AddClosingStockActivity;
import com.vvautotest.activities.AddLeaveApplicationActivity;

public class LeaveApplicationFragment extends Fragment {

    CardView addNewEntryBtn, dmcView;

    public LeaveApplicationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_leave_application, container, false);
        addNewEntryBtn = rootView.findViewById(R.id.addNewEntryBtn);
        addNewEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddLeaveApplicationActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}