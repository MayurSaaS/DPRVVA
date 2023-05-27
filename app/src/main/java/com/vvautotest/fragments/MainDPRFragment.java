package com.vvautotest.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vvautotest.R;
import com.vvautotest.activities.AddMainDPRActivity;
import com.vvautotest.activities.ProgressEntryActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainDPRFragment extends Fragment {

    public MainDPRFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_d_p_r, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.addNewEntryBtn)
    public void addNewEntry()
    {
        startActivity(new Intent(getActivity(), AddMainDPRActivity.class));
    }

    @OnClick(R.id.mainCard)
    public void showDetails()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.main_dpr_detail_dailog_box);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /*Button cancelBTN = dialog.findViewById(R.id.cancelBTN);
        Button acceptBTN = dialog.findViewById(R.id.acceptBtn);
        TextView tvTitle = dialog.findViewById(R.id.title);
        TextView tvDescription = dialog.findViewById(R.id.description);
        ImageView ivIcon = dialog.findViewById(R.id.icon);

        tvTitle.setText(title);
        tvDescription.setText(des);
        ivIcon.setImageResource(icon);

        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        acceptBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        dialog.show();
    }
}