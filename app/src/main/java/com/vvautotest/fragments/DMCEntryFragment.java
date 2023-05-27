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
import android.widget.Button;

import com.vvautotest.R;
import com.vvautotest.activities.AddDMCEntryActivity;


public class DMCEntryFragment extends Fragment {

    CardView addNewEntryBtn, dmcView;

    public DMCEntryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_d_m_c_entry, container, false);
        dmcView = rootView.findViewById(R.id.dmcView);
        dmcView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails();
            }
        });
        addNewEntryBtn = rootView.findViewById(R.id.addNewEntryBtn);
        addNewEntryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddDMCEntryActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void showDetails()
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dmc_details_box);
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