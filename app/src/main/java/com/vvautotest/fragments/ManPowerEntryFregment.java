package com.vvautotest.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvautotest.R;
import com.vvautotest.activities.AddManpowerEntryActivity;
import com.vvautotest.activities.UploadPhotosActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManPowerEntryFregment extends Fragment {

    @BindView(R.id.addNewEntryBtn)
    CardView addNewEntryBtn;


    public ManPowerEntryFregment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_man_power_entry_fregment, container, false);
        ButterKnife.bind(this, rootView);
        init(rootView);
        return rootView;
    }

    private void init(View rootView)
    {

    }

    @OnClick(R.id.addNewEntryBtn)
    public void addNewEntry()
    {
        startActivity(new Intent(getActivity(), AddManpowerEntryActivity.class));
    }


}