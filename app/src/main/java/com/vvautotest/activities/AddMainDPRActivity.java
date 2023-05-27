package com.vvautotest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.vvautotest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddMainDPRActivity extends AppCompatActivity {


    @BindView(R.id.navigationIcon)
    ImageView navigationIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_main_dpractivity);
        ButterKnife.bind(this);
        setupToolbar();
    }

    private void setupToolbar(){
        navigationIcon.setOnClickListener(v -> onBackPressed());
    }
}