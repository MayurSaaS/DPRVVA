package com.vvautotest.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvautotest.R;
import com.vvautotest.activities.AddLeaveApplicationActivity;
import com.vvautotest.activities.AddPreSaleSiteSurwayActivity;


public class PreSaleSiteSurwayFragment extends Fragment {

    CardView addNewEntryBtn, dmcView;

    public PreSaleSiteSurwayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_pre_sale_site_surway, container, false);

        addNewEntryBtn = rootView.findViewById(R.id.addNewEntryBtn);
        addNewEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPreSaleSiteSurwayActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}