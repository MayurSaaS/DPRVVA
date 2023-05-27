package com.vvautotest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.vvautotest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FullPhotoViewActivity extends AppCompatActivity {
    @BindView(R.id.backBtn)
    LinearLayout backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_photo_view);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.backBtn)
    public void onBack(){
        onBackPressed();
    }
}