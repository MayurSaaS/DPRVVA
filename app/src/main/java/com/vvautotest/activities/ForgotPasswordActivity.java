package com.vvautotest.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vvautotest.R;
import com.vvautotest.model.Result;
import com.vvautotest.model.User;
import com.vvautotest.utils.AppUtils;
import com.vvautotest.utils.L;
import com.vvautotest.utils.ServerConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    @BindView(R.id.emailET)
    EditText emailET;

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
        String email = emailET.getText().toString().trim();

        if("".equals(email))
        {
            AppUtils.showToast(ForgotPasswordActivity.this, "Please enter email");
            return;
        }

        forgetPasswordApi(email);
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


    private void forgetPasswordApi(String email){


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting, Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AndroidNetworking.post(ServerConfig.Forgot_Password_URL)
                .addJSONObjectBody(jsonObject) // posting json
                .setTag("login")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                                     @Override
                                     public void onResponse(JSONObject response) {
                                         progressDialog.dismiss();
                                         L.printInfo(response.toString());
                                         ObjectMapper om = new ObjectMapper();
                                         try {
                                             Result result = om.readValue(response.toString(), Result.class);
                                             if(result.result)
                                             {
                                                 AppUtils.showToast(ForgotPasswordActivity.this, result.message);
                                             }else
                                             {
                                                 AppUtils.showToast(ForgotPasswordActivity.this, result.message);
                                             }
                                         } catch (IOException e) {
                                             e.printStackTrace();
                                         }

                                     }
                                     @Override
                                     public void onError(ANError anError) {
                                         progressDialog.dismiss();
                                         L.printError(anError.toString());
                                         AppUtils.showToast(ForgotPasswordActivity.this, "Something went wrong, please try after sometime");
                                     }
                                 }
                );

    }

}