package com.vvautotest.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvautotest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.submitBtn)
    CardView submitBtn;
    @BindView(R.id.logInBtn)
    TextView logInBtn;
    @BindView(R.id.back)
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        init();
    }

    public void init()
    {

    }

    @OnClick(R.id.submitBtn)
    public void onSubmit()
    {
       /* Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();*/
    }

    @OnClick(R.id.logInBtn)
    public void onLoginBtnClick()
    {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.back)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}