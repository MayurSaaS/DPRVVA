package com.vvautotest.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.vvautotest.R;
import com.vvautotest.activities.FullPhotoViewActivity;
import com.vvautotest.activities.UploadPhotosActivity;
import com.vvautotest.adapter.SpinnerAdapter;
import com.vvautotest.model.SpinnerData;
import com.vvautotest.utils.DummyData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhotosFragment extends Fragment {

    @BindView(R.id.uploadPhotos)
    CardView uploadPhotos;

    @BindView(R.id.photoll)
    RelativeLayout photoll;

    public PhotosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_photos, container, false);
        ButterKnife.bind(this, rootView);
        init(rootView);
        return rootView;
    }



    private void init(View rootView)
    {
        photoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FullPhotoViewActivity.class);
                getActivity().startActivity(intent);
            }
        });
    }

    @OnClick(R.id.uploadPhotos)
    public void uploadPhotos()
    {
        startActivity(new Intent(getActivity(), UploadPhotosActivity.class));
    }
}