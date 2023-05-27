package com.vvautotest.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvautotest.R;
import com.vvautotest.activities.AddManpowerEntryActivity;
import com.vvautotest.activities.ProgressEntryActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProgressEntryFragment extends Fragment {

    public ProgressEntryFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_progress_entry, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.addNewEntryBtn)
    public void addNewEntry()
    {
        startActivity(new Intent(getActivity(), ProgressEntryActivity.class));
    }

}